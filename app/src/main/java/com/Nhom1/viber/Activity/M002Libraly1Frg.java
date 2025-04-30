package com.Nhom1.viber.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.Nhom1.viber.R;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.services.FirebaseService;
import com.Nhom1.viber.utils.RecentManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.util.Log;

public class M002Libraly1Frg extends Fragment {


    private ImageButton ibtnGanDay;
    private ImageButton IbtnRandom;
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
        ibtnGanDay = view.findViewById(R.id.ibtnGanDay);
        firebaseService = new FirebaseService();
        CVDowload = view.findViewById(R.id.CVDowload);
        playerBar = view.findViewById(R.id.playerBar);
        imgSong = view.findViewById(R.id.imgSong);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnNext = view.findViewById(R.id.btnNext);

        playerBar.setVisibility(View.GONE);

        CVDowload.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new M002LibraryFrg());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        IbtnRandom.setOnClickListener(v -> {
            playerBar.setVisibility(View.VISIBLE);
            loadSongsAndPlayRandom();
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

        btnNext.setOnClickListener(v -> fetchFullDetailsAndPlayRandom());
        //Them chưc nang nhạc nghe gần đây
        ibtnGanDay.setOnClickListener(v -> {
            List<Song> recentList = RecentManager.getRecentList(requireContext());

            if (recentList == null || recentList.isEmpty()) {
                Toast.makeText(getContext(), "Bạn chưa nghe bài nào gần đây", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔀 Chọn ngẫu nhiên 1 bài từ danh sách gần đây
            Random rand = new Random();
            int index = rand.nextInt(recentList.size());
            Song recentSong = recentList.get(index);

            // 🛡 Bảo vệ nếu thiếu ID
            if (recentSong.getId() == null || recentSong.getId().isEmpty()) {
                Toast.makeText(getContext(), "Bài hát gần đây bị thiếu ID", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Gọi getSongDetail giống random để lấy đầy đủ thông tin từ Firebase
            firebaseService.getSongDetail(recentSong.getId(), fullSong -> {
                if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                    playSong(fullSong); // ✅ Dùng chung playerBar để phát
                } else {
                    Toast.makeText(getContext(), "Không thể phát bài hát (thiếu URL)", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadSongsAndPlayRandom() {
        firebaseService.getSongs(songs -> {
            if (songs != null && !songs.isEmpty()) {
                songList.clear();
                songList.addAll(songs);

                // 🔀 Chọn ngẫu nhiên 1 bài từ danh sách
                Random rand = new Random();
                int index = rand.nextInt(songList.size());
                Song randomSong = songList.get(index);

                // ✅ Lấy thông tin chi tiết từ Firebase (bao gồm URL)
                if (randomSong.getId() == null || randomSong.getId().isEmpty()) {
                    Toast.makeText(getContext(), "Bài hát không hợp lệ (thiếu ID)", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseService.getSongDetail(randomSong.getId(), fullSong -> {
                    if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                        currentSong = fullSong;
                        playSong(fullSong); // ✅ Phát bài đã đủ thông tin
                    } else {
                        Toast.makeText(getContext(), "Không thể phát bài hát (thiếu URL)", Toast.LENGTH_SHORT).show();
                        Log.e("Library1", "❌ Song thiếu URL: " + randomSong.getTitle());
                    }
                });

            } else {
                Toast.makeText(getContext(), "Không có bài hát nào từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFullDetailsAndPlayRandom() {
        if (songList.isEmpty()) return;

        Random rand = new Random();
        int index = rand.nextInt(songList.size());
        Song randomSong = songList.get(index);

        firebaseService.getSongDetail(randomSong.getId(), fullSong -> {
            if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                currentSong = fullSong;
                playSong(fullSong);
            } else {
                Toast.makeText(getContext(), "Bài hát không có URL hoặc lỗi ánh xạ", Toast.LENGTH_SHORT).show();
                Log.e("Library1", "❌ Song thiếu URL hoặc lỗi ánh xạ: " + randomSong.getTitle());
            }
        });
    }

    private void playSong(Song song) {
        try {
            if (song.getUrl() == null || song.getUrl().isEmpty()) {
                Log.e("PLAY_ERROR", "⛔ Bài hát không có URL, không thể phát");
                return;
            }

            tvSongTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());

            Glide.with(requireContext())
                    .load(song.getCover())
                    .placeholder(R.drawable.viber)
                    .error(R.drawable.viber)
                    .into(imgSong);

            if (player != null) {
                player.release();
                player = null;
            }

            Context context = getContext();
            if (context == null) return;

            player = new ExoPlayer.Builder(context).build();
            player.setMediaItem(MediaItem.fromUri(song.getUrl()));
            player.prepare();
            player.play();

            playerBar.setVisibility(View.VISIBLE);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            isPlaying = true;

            // ✅ Lưu vào danh sách nghe gần đây sau khi đã có đầy đủ thông tin
            RecentManager.addSong(requireActivity().getApplicationContext(), song);
            Log.d("RECENT_SAVE", "✅ Đã thêm vào gần đây: " + song.getTitle());

        } catch (Exception e) {
            Log.e("Library1", "Lỗi khi phát bài hát", e);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }
}