package com.Nhom1.viber.models;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.io.Serializable;

import java.util.Date;

public class Song implements Serializable {
    private String Id;
    private String Artist;
    private String Cover;
    private String Title;
    private String Url;
    private int Duration;
    private int Likes;
    private int Plays;
    private Timestamp ReleaseDate;
    public Song() {

    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getArtist() {
        return Artist;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public void setLikes(int likes) {
        Likes = likes;
    }

    public void setPlays(int plays) {
        Plays = plays;
    }

    public String getTitle() {
        return Title;
    }

    public String getUrl() {
        return Url;
    }

    public int getDuration() {
        return Duration;
    }

    public int getLikes() {
        return Likes;
    }


    public int getPlays() {
        return Plays;
    }

    public Timestamp getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        ReleaseDate = releaseDate;
    }
}
