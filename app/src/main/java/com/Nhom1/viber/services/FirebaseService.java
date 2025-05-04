package com.Nhom1.viber.services;

import android.util.Log;
import android.widget.Toast;

import com.Nhom1.viber.models.PlayList;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseService {
    private final FirebaseFirestore db;
    private DocumentSnapshot lastVisible = null;

    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public void getSongs(OnSongsLoadedListener listener) {
        List<Song> songList = new ArrayList<>();
        // Tên collection trong Firestore
        String COLLECTION_NAME = "songs";
        double randomFloat = Math.random();
        db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("random", 0)
                .orderBy("random")
                .limit(30)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String artist = document.getString("Artist");
                            String title = document.getString("Title");
                            String cover = document.getString("Cover");
                            String songId = document.getId();

                            Song song = new Song();
                            song.setArtist(artist);
                            song.setTitle(title);
                            song.setId(songId);
                            song.setCover(cover);

                            songList.add(song);
                            Log.e("đá", "da" + songList.size());
                        }
                        listener.onSongsLoaded(songList);
                    } else {
                        Log.e("Firestore", "Lỗi khi lấy danh sách bài hát", task.getException());
                        listener.onSongsLoaded(null);
                    }
                });
    }

    public void getSongDetail(String songId, OnSongDetailLoadedListener listener) {
        db.collection("songs")
                .document(songId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Song song = documentSnapshot.toObject(Song.class);
                        listener.onSongDetailLoaded(song);
                    } else {
                        listener.onSongDetailLoaded(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi lấy chi tiết bài hát", e);
                    listener.onSongDetailLoaded(null);
                });
    }

    public void registerUser(String username, String email) {
        User user = new User(username, email);

        db.collection("users")
                .document(email)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", " ✅Đăng ký thành công!"))
                .addOnFailureListener(e -> Log.e("Firestore", "❎Lỗi khi đăng ký!", e));
    }

    public void getBanners(FirestoreCallback callback) {
        db.collection("songs")
                .orderBy("ReleaseDate", Query.Direction.DESCENDING)
                .limit(5) // Lấy 5 bài hát mới nhất
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Song> songList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String cover = document.getString("Cover");
                        Song song = new Song();
                        song.setCover(cover);
                        songList.add(song);
                    }
                    callback.onCallback(songList); // Trả dữ liệu qua callback
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Lỗi khi lấy danh sách bài hát", e);
                    callback.onCallback(null); // Trả về null nếu lỗi
                });
    }

    public void getPlayListArtist(OnPlaylistsLoadedListener listener) {
        List<PlayList> pls = new ArrayList<>();
        db.collection("playlists")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), "Artist")
                .whereLessThan(FieldPath.documentId(), "Artist~")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        List<String> ids = (List<String>) doc.get("Songs");
                        String name = doc.getString("Name");
                        String cover = doc.getString("Cover");

                        PlayList pl = new PlayList(name, cover, ids);
                        pls.add(pl);
                    }
                    listener.onPlaylistsLoaded(pls); // Chỉ gọi callback sau khi đã load xong!
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi truy vấn playlists", e);
                    listener.onPlaylistsLoaded(null); // Báo lỗi bằng cách trả null
                });
    }

    public void getPlayListGenres(OnPlaylistsLoadedListener listener) {
        List<PlayList> pls = new ArrayList<>();
        db.collection("playlists")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), "Genres")
                .whereLessThan(FieldPath.documentId(), "Genres~")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        List<String> ids = (List<String>) doc.get("Songs");
                        String name = doc.getString("Name");
                        String cover = doc.getString("Cover");

                        PlayList pl = new PlayList(name, cover, ids);
                        pls.add(pl);
                    }
                    listener.onPlaylistsLoaded(pls); // Chỉ gọi callback sau khi đã load xong!
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi truy vấn playlists", e);
                    listener.onPlaylistsLoaded(null); // Báo lỗi bằng cách trả null
                });
    }

    public void getPlayListEvent(OnPlaylistsLoadedListener listener) {
        List<PlayList> pls = new ArrayList<>();
        db.collection("playlists")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), "Event")
                .whereLessThan(FieldPath.documentId(), "Event~")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        List<String> ids = (List<String>) doc.get("Songs");
                        String name = doc.getString("Name");
                        String cover = doc.getString("Cover");

                        PlayList pl = new PlayList(name, cover, ids);
                        pls.add(pl);
                    }
                    listener.onPlaylistsLoaded(pls); // Chỉ gọi callback sau khi đã load xong!
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi truy vấn playlists", e);
                    listener.onPlaylistsLoaded(null); // Báo lỗi bằng cách trả null
                });
    }

    public void loadPlayListDetails(List<String> songIds, OnSongsLoadedListener listener){
        List<List<String>> partitions = partitionList(songIds, 10);
        List<Song> allSongs = new ArrayList<>();
        AtomicInteger completedCount = new AtomicInteger(0); // Dùng để đếm số partition xong

        for (List<String> partition : partitions) {
            db.collection("songs")
                    .whereIn(FieldPath.documentId(), partition)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String artist = doc.getString("Artist");
                            String title = doc.getString("Title");
                            String cover = doc.getString("Cover");
                            String songId = doc.getId();

                            Song song = new Song();
                            song.setArtist(artist);
                            song.setTitle(title);
                            song.setId(songId);
                            song.setCover(cover);

                            allSongs.add(song);
                        }

                        // Kiểm tra xem đã xong hết chưa
                        if (completedCount.incrementAndGet() == partitions.size()) {
                            listener.onSongsLoaded(allSongs);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi tải bài hát playlist", e);
                        listener.onSongsLoaded(null);
                    });
        }
    }

    private List<List<String>> partitionList(List<String> list, int size) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 10) {
            partitions.add(list.subList(i, Math.min(i + 10, list.size())));
        }
        return partitions;
    }
    public void searchSongs(String keyword, boolean isNewSearch, OnSongsLoadedListener listener) {
        if (isNewSearch) lastVisible = null; // reset khi bắt đầu tìm kiếm mới

        String lowercaseKeyword = keyword.toLowerCase();
        Query query = db.collection("songs")
                .orderBy("title_lower")
                .startAt(lowercaseKeyword)
                .endAt(lowercaseKeyword + "\uf8ff")
                .limit(20);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Song> songList = new ArrayList<>();
                List<DocumentSnapshot> docs = task.getResult().getDocuments();

                for (DocumentSnapshot doc : docs) {
                    Song song = doc.toObject(Song.class);
                    song.setId(doc.getId());
                    songList.add(song);
                }

                if (!docs.isEmpty()) {
                    lastVisible = docs.get(docs.size() - 1); // cập nhật cho phân trang
                }

                listener.onSongsLoaded(songList);
            } else {
                listener.onSongsLoaded(null);
            }
        });
    }

    public void incrementSongPlays(String songId) {
        if (songId == null || songId.isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference songRef = db.collection("songs").document(songId);

        songRef.update("Plays", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d("UpdatePlay", "Tăng lượt plays thành công cho bài: " + songId))
                .addOnFailureListener(e -> Log.e("UpdatePlay", "Lỗi khi tăng lượt plays", e));
    }


    public void getTopSongs(int limit, OnSongsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("songs")
                .orderBy("Plays", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Song> topSongs = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String artist = document.getString("Artist");
                        String title = document.getString("Title");
                        String cover = document.getString("Cover");
                        int plays = document.getLong("Plays").intValue();
                        String songId = document.getId();

                        Song song = new Song();
                        song.setArtist(artist);
                        song.setTitle(title);
                        song.setId(songId);
                        song.setCover(cover);
                        song.setPlays(plays);

                        topSongs.add(song);
                    }
                    listener.onSongsLoaded(topSongs);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi lấy top bài hát", e);
                    listener.onSongsLoaded(null);
                });
    }

    public interface OnSongsLoadedListener {
        void onSongsLoaded(List<Song> songs);
    }

    public interface FirestoreCallback {
        void onCallback(List<Song> songs);
    }

    public interface OnSongDetailLoadedListener {
        void onSongDetailLoaded(Song song);
    }

    public interface OnPlaylistsLoadedListener {
        void onPlaylistsLoaded(List<PlayList> playlists);
    }
    private void addPlaylistToExistingUser(String userEmail, String playlistName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo update cho field playlists.<playlistName>
        Map<String, Object> update = new HashMap<>();
        update.put("playlists." + playlistName, true); // lưu như object con

        db.collection("users")
                .document(userEmail)
                .update(update)  // Chỉ cập nhật nếu user đã có
                .addOnSuccessListener(unused -> {
                    Log.d("Firestore", "Đã thêm playlist vào field playlists");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi thêm playlist", e);
                });
    }
    public interface ISongFetchListener {
        void onSongsFetched(List<Song> songs);
    }
    public static void getSongsByIds(List<String> ids, ISongFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Song> songs = new ArrayList<>();

        if (ids.isEmpty()) {
            listener.onSongsFetched(songs);
            return;
        }

        AtomicInteger completedCount = new AtomicInteger(0);

        for (String id : ids) {
            db.collection("songs")
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Song song = documentSnapshot.toObject(Song.class);
                            if (song != null) {
                                song.setId(documentSnapshot.getId());
                                synchronized (songs) {
                                    songs.add(song);
                                }
                            }
                        }
                        if (completedCount.incrementAndGet() == ids.size()) {
                            listener.onSongsFetched(songs);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi lấy bài hát", e);
                        if (completedCount.incrementAndGet() == ids.size()) {
                            listener.onSongsFetched(songs);
                        }
                    });
        }
    }
    public interface OnSongLoadedListener {
        void onSongLoaded(Song song);
    }
    public void getPlaylistSongs(String userEmail, String playlistId, OnSongsLoadedListener listener) {
        Log.d("FIREBASE_DEBUG", "Bắt đầu truy vấn Firestore...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userEmail)
                .collection("playlists")
                .document(playlistId)
                .collection("songs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("FIREBASE_DEBUG", "Tổng số documents: " + queryDocumentSnapshots.size());
                    List<String> songIds = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        // Đây là document con: users/.../songs/{autoId}
                        // Và nó chứa field songId = "id thực sự của bài hát"
                        String songId = doc.getString("songId");
                        Log.d("FIREBASE_DEBUG", "Song document ID: " + doc.getId() + ", songId = " + songId);
                        if (songId != null && !songId.isEmpty()) {
                            songIds.add(songId);
                        }
                    }

                    // Gọi hàm có sẵn để lấy chi tiết bài hát
                   getSongsByIds(songIds, new ISongFetchListener() {
                        @Override
                        public void onSongsFetched(List<Song> songs) {
                            listener.onSongsLoaded(songs); // map lại sang listener cũ
                        }
                    });
                })
                .addOnFailureListener(queryDocumentSnapshots-> {
                    Log.d("FIREBASE_DEBUG", "Tổng số documents: " + queryDocumentSnapshots.getMessage());
                    listener.onSongsLoaded(new ArrayList<>()); // Trả về danh sách rỗng nếu lỗi
                });
    }
    public class PlaylistBusiness {
        public  void getSongsOfPlaylist(List<String> songIds, ISongFetchListener listener) {
            FirebaseService.getSongsByIds(songIds, listener);
        }
    }
}
