package com.Nhom1.viber.utils;

import com.Nhom1.viber.services.FirebaseService;

import java.util.List;

public class BusinessLogic {
    private final FirebaseService firebaseService;

    public BusinessLogic(){
        firebaseService = new FirebaseService();
    }

    public void GetListSong(FirebaseService.OnSongsLoadedListener listener) {
        firebaseService.getSongs(listener);
    }

    public void GetSongDetail(String id, FirebaseService.OnSongDetailLoadedListener detail) {
        firebaseService.getSongDetail(id, detail);
    }

    // 🔥 Lấy danh sách banner bài hát (5 bài mới nhất)
    public void GetBannerSongs(FirebaseService.FirestoreCallback callback) {
        firebaseService.getBanners(callback);
    }

    public void GetPlayListArtist(FirebaseService.OnPlaylistsLoadedListener pl){
        firebaseService.getPlayListArtist(pl);
    }

    public void GetPlayListGenres(FirebaseService.OnPlaylistsLoadedListener pl){
        firebaseService.getPlayListGenres(pl);
    }

    public void GetPlayListDetails(List<String> songIds, FirebaseService.OnSongsLoadedListener loaded){
        firebaseService.loadPlayListDetails(songIds, loaded);
    }

    public void GetPlayListEvent(FirebaseService.OnPlaylistsLoadedListener pl){
        firebaseService.getPlayListEvent(pl);
    }

    public void SearchKeyWord(String s, boolean isnewsearch, FirebaseService.OnSongsLoadedListener listener){
        firebaseService.searchSongs(s, isnewsearch, listener);
    }
}
