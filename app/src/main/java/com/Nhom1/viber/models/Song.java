package com.Nhom1.viber.models;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.io.Serializable;

import java.util.Date;

public class Song implements Serializable {
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


    public String getArtist() {
        return Artist;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
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
