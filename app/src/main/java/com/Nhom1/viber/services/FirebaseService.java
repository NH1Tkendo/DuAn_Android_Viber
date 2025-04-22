package com.Nhom1.viber.services;

import android.util.Log;

import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {
    private final FirebaseFirestore db;

    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public void getSongs(OnSongsLoadedListener listener) {
        List<Song> songList = new ArrayList<>();
        // Tên collection trong Firestore
        String COLLECTION_NAME = "songs";
        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Song song = document.toObject(Song.class);
                            songList.add(song);
                        }
                        listener.onSongsLoaded(songList);
                    } else {
                        Log.e("Firestore", "Lỗi khi lấy danh sách bài hát", task.getException());
                        listener.onSongsLoaded(null);
                    }
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
                        Song song = document.toObject(Song.class);
                        songList.add(song);
                    }
                    callback.onCallback(songList); // Trả dữ liệu qua callback
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Lỗi khi lấy danh sách bài hát", e);
                    callback.onCallback(null); // Trả về null nếu lỗi
                });
    }


    public interface OnSongsLoadedListener {
        void onSongsLoaded(List<Song> songs);
    }

    public interface FirestoreCallback {
        void onCallback(List<Song> songs);
    }
}
