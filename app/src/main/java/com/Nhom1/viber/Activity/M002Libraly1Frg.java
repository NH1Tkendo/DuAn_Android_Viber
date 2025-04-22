package com.Nhom1.viber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.services.FirebaseService;
import com.bumptech.glide.Glide;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class M002Libraly1Frg extends Fragment {

    private ImageView IbtnRandom;
    private FirebaseService firebaseService;
    private List<Song> songList = new ArrayList<>();
    private ExoPlayer player;
    private Song currentSong;

    private LinearLayout playerBar;
    private ImageView imgSong, btnPlayPause, btnNext;
    private TextView tvSongTitle, tvArtist;
    private boolean isPlaying = true;
    private CardView CVDowload;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.m002_frg_library1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        IbtnRandom = view.findViewById(R.id.IbtnRandom);
        firebaseService = new FirebaseService();
        CVDowload=view.findViewById(R.id.CVDowload);
        playerBar = view.findViewById(R.id.playerBar);
        imgSong = view.findViewById(R.id.imgSong);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnNext = view.findViewById(R.id.btnNext);

        playerBar.setVisibility(View.GONE);

        CVDowload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, new M002LibraryFrg()); // Thay thế Fragment
                transaction.addToBackStack(null); // Cho phép quay lại Fragment trước đó
                transaction.commit();
            }
        });
        IbtnRandom.setOnClickListener(v -> {
            playerBar.setVisibility(View.VISIBLE); // Hiện player bar
            loadSongsAndPlayRandom();              // Gọi hàm phát ngẫu nhiên
        });
        btnPlayPause.setOnClickListener(v -> {
            if (player != null) {
                if (isPlaying) {
                    player.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                } else {
                    player.play();
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                }
                isPlaying = !isPlaying;
            }
        });

        btnNext.setOnClickListener(v -> playNextRandomSong());
        playerBar = view.findViewById(R.id.playerBar);
        if (playerBar == null) {
            Toast.makeText(getContext(), "Thiếu layout playerBar!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private void loadSongsAndPlayRandom() {
        firebaseService.getSongs(songs -> {
            if (songs != null && !songs.isEmpty()) {
                songList.clear();
                songList.addAll(songs);
                playRandomSong();
            } else {
                Toast.makeText(getContext(), "Không có bài hát nào từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void playRandomSong() {
        if (songList.isEmpty()) return;

        Random rand = new Random();
        int index = rand.nextInt(songList.size());
        Song song = songList.get(index);
        currentSong = song;

        tvSongTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        Glide.with(requireContext())
                .load(song.getCover())
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(imgSong);

        if (player != null) player.release();
        player = new ExoPlayer.Builder(requireContext()).build();
        player.setMediaItem(MediaItem.fromUri(song.getUrl()));
        player.prepare();
        player.play();

        playerBar.setVisibility(View.VISIBLE);
        btnPlayPause.setImageResource(R.drawable.ic_pause);
        isPlaying = true;
    }
    private void playNextRandomSong() {
        if (songList.isEmpty() || currentSong == null) return;

        Random rand = new Random();
        int randomIndex;
        do {
            randomIndex = rand.nextInt(songList.size());
        } while (songList.get(randomIndex).equals(currentSong));

        Song nextSong = songList.get(randomIndex);
        currentSong = nextSong;

        tvSongTitle.setText(nextSong.getTitle());
        tvArtist.setText(nextSong.getArtist());
        Glide.with(requireContext())
                .load(nextSong.getCover())
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(imgSong);

        if (player != null) player.release();
        player = new ExoPlayer.Builder(requireContext()).build();
        player.setMediaItem(MediaItem.fromUri(nextSong.getUrl()));
        player.prepare();
        player.play();

        btnPlayPause.setImageResource(R.drawable.ic_pause);
        isPlaying = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }
}