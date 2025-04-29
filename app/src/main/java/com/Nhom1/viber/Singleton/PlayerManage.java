package com.Nhom1.viber.Singleton;

import android.content.Context;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.utils.BusinessLogic;

import java.util.ArrayList;
import java.util.List;

public class PlayerManage {
    private static PlayerManage instance;
    private ExoPlayer player;
    private Song currentSong;
    private boolean isPlaying = true;
    private ArrayList<Song> queue;
    private BusinessLogic bs = new BusinessLogic();
    private int currentIndex = -1;
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

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song song) {
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

    public void setCurrentIndex(int index) {
        this.currentIndex = index;
    }

    public void removeListener(Player.Listener listener) {
        if (player != null) {
            player.removeListener(listener);
        }
    }

    public void setQueue(ArrayList<Song> queue) {
        this.queue = queue;
    }

    public void play(Song song, Context context) {
        if (player != null) {
            setCurrentSong(song); // gọi method đã có
            player.setMediaItem(MediaItem.fromUri(song.getUrl()));
            player.prepare();
            player.play();
        }
    }

    public void playCurrent(Context context) {
        if (queue == null || queue.isEmpty()) return;

        if (currentIndex >= 0 && currentIndex < queue.size()) {
            currentSong = queue.get(currentIndex);
        }

        // Nếu đã có URL thì phát luôn
        if (currentSong.getUrl() != null && !currentSong.getUrl().isEmpty()) {
            play(currentSong, context);
        } else {
            // Lazy loading: gọi Firebase để lấy dữ liệu chi tiết
            bs.GetSongDetail(currentSong.getId(), detailedSong -> {
                if (detailedSong != null) {
                    currentSong = detailedSong; // cập nhật currentSong
                    play(currentSong, context);
                } else {
                    Log.e("PlayerManage", "Không lấy được chi tiết bài hát");
                }
            });
        }
    }

    public void playNext(Context context){
        currentIndex++;

        if (currentIndex >= queue.size()) {
            currentIndex = 0; // vòng lại từ đầu (hoặc return nếu bạn không muốn vòng)
        }

        playCurrent(context);
    }

    public void playPrevious(Context context){
        currentIndex--;

        if (currentIndex <= queue.size()) {
            currentIndex = queue.size() - 1; // vòng lại từ đầu (hoặc return nếu bạn không muốn vòng)
        }

        playCurrent(context);
    }
}
