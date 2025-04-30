package com.Nhom1.viber.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.Nhom1.viber.models.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentManager {
    private static final String PREF_NAME = "recent_songs";
    private static final String KEY_LIST = "recent_list";
    private static final int MAX_SIZE = 20;

    public static void addSong(Context context, Song song) {
        // Tạo 1 bản rút gọn của bài hát (chỉ chứa thông tin đơn giản)
        Song safeSong = new Song();
        safeSong.setId(song.getId());
        safeSong.setTitle(song.getTitle());
        safeSong.setArtist(song.getArtist());
        safeSong.setCover(song.getCover());
        safeSong.setUrl(song.getUrl());

        List<Song> list = getRecentList(context);

        // Xoá nếu bài đó đã có
        list.removeIf(s -> s.getId().equals(safeSong.getId()));

        // Thêm lên đầu
        list.add(0, safeSong);

        // Giới hạn số lượng
        if (list.size() > MAX_SIZE) {
            list = list.subList(0, MAX_SIZE);
        }

        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LIST, new Gson().toJson(list)).apply();
    }

    public static List<Song> getRecentList(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LIST, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Song>>() {}.getType();
        return new Gson().fromJson(json, type);

    }
}