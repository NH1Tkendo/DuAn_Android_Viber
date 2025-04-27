package com.Nhom1.viber.models;

import java.util.List;

public class PlayList {
    private String name;
    private String cover;
    private List<String> songs;

    public PlayList(){

    }

    public PlayList(String name, String cover, List<String> songs) {
        this.name = name;
        this.cover = cover;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
}
