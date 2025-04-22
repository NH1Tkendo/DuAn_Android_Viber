package com.Nhom1.viber.utils;

import com.Nhom1.viber.services.FirebaseService;

public class BusinessLogic {
    private final FirebaseService firebaseService;

    public BusinessLogic(){
        firebaseService = new FirebaseService();
    }

    public void GetListSong(FirebaseService.OnSongsLoadedListener listener) {
        firebaseService.getSongs(listener);
    }

    // 🔥 Lấy danh sách banner bài hát (5 bài mới nhất)
    public void GetBannerSongs(FirebaseService.FirestoreCallback callback) {
        firebaseService.getBanners(callback);
    }
}
