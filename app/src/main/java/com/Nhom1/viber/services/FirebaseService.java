package com.Nhom1.viber.services;

import android.util.Log;

import com.Nhom1.viber.models.PlayList;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

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
                .whereGreaterThanOrEqualTo("random", randomFloat)
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
                        // Nếu đã lấy hết tất cả partition
                        if (allSongs.size() == songIds.size()) {
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
}
