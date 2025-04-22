package com.Nhom1.viber.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String email;
    private String user;
    private List<String> favorite_songs;
    private Map<String, Object> playlists = new HashMap<>();


    public User() {
        this.favorite_songs = new ArrayList<>();
        this.playlists = new HashMap<>();
    }

    // Constructor đầy đủ (khi tạo tài khoản)
    public User(String user, String email) {
        this.user = user;
        this.email = email;
        this.favorite_songs = new ArrayList<>();
        this.playlists = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getFavorite_songs() {
        return favorite_songs;
    }

    public void setFavorite_songs(List<String> favorite_songs) {
        this.favorite_songs = favorite_songs;
    }

    public Map<String, Object> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Map<String, Object> playlists) {
        this.playlists = playlists;
    }
}
