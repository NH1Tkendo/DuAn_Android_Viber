package com.Nhom1.viber.Singleton;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.Nhom1.viber.models.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayerManage {
    private static PlayerManage instance;
    private ExoPlayer player;
    private Song currentSong;
    private boolean isPlaying = true;

    private final List<Player.Listener> listeners = new ArrayList<>();

    private PlayerManage(Context context) {
        player = new ExoPlayer.Builder(context).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                PlayerManage.this.isPlaying = isPlaying;
                for (Player.Listener listener : listeners) {
                    listener.onIsPlayingChanged(isPlaying);
                }
            }
        });
    }
    public static PlayerManage getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerManage(context);
        }
        return instance;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void play(Song song, Context context) {
        if (player != null) {
            setCurrentSong(song); // gọi method đã có
            player.setMediaItem(MediaItem.fromUri(song.getUrl()));
            player.prepare();
            player.play();
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song song){
        this.currentSong = song;
    }

    public void pause() {
        if (player != null) player.pause();
    }

    public void resume() {
        if (player != null) player.play();
    }

    public void addListener(Player.Listener listener) {
        if (player != null) {
            player.addListener(listener);
        }
    }

    public void removeListener(Player.Listener listener) {
        if (player != null) {
            player.removeListener(listener);
        }
    }
}
