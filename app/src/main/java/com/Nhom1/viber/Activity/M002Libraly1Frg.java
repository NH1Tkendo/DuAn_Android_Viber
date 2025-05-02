package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.Nhom1.viber.R;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.services.FirebaseService;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.utils.RecentManager;
import com.Nhom1.viber.Activity.MiniPlayerFragment; // <-- Thêm dòng này
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class M002Libraly1Frg extends Fragment {

    private ImageButton ibtnGanDay, IbtnRandom;
    private FirebaseService firebaseService;
    private List<Song> songList = new ArrayList<>();
    private CardView CVDowload;
    private TextView tvTitle, tvArtist;
    private ImageView ivCover;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.m002_frg_library1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        IbtnRandom = view.findViewById(R.id.IbtnRandom);
        ibtnGanDay = view.findViewById(R.id.ibtnGanDay);
        CVDowload = view.findViewById(R.id.CVDowload);
        tvTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtist);

        firebaseService = new FirebaseService();

        CVDowload.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new M002LibraryFrg());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        IbtnRandom.setOnClickListener(v -> {
            firebaseService.getSongs(songs -> {
                if (songs != null && !songs.isEmpty()) {
                    songList.clear();
                    songList.addAll(songs);

                    Song randomSong = songs.get(new Random().nextInt(songs.size()));
                    if (randomSong.getId() == null || randomSong.getId().isEmpty()) {
                        Toast.makeText(getContext(), "Bài hát không hợp lệ (thiếu ID)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseService.getSongDetail(randomSong.getId(), fullSong -> {
                        if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                            PlayerManage.getInstance(requireContext()).play(fullSong, requireContext());
                            updatePlayerBar(fullSong);
                            showMiniPlayer(); // <-- Gọi player bar
                        } else {
                            Toast.makeText(getContext(), "Không thể phát bài hát (thiếu URL)", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Không có bài hát nào từ Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        });

        ibtnGanDay.setOnClickListener(v -> {
            List<Song> recentList = RecentManager.getRecentList(requireContext());
            if (recentList == null || recentList.isEmpty()) {
                Toast.makeText(getContext(), "Bạn chưa nghe bài nào gần đây", Toast.LENGTH_SHORT).show();
                return;
            }

            Random rand = new Random();
            Song randomRecent = recentList.get(rand.nextInt(recentList.size()));
            if (randomRecent.getId() == null || randomRecent.getId().isEmpty()) {
                Toast.makeText(getContext(), "Bài hát gần đây bị thiếu ID", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseService.getSongDetail(randomRecent.getId(), fullSong -> {
                if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                    PlayerManage.getInstance(requireContext()).play(fullSong, requireContext());
                    updatePlayerBar(fullSong);
                    showMiniPlayer(); // <-- Gọi player bar
                } else {
                    Toast.makeText(getContext(), "Không thể phát bài hát (thiếu URL)", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updatePlayerBar(Song song) {
        if (tvTitle != null) tvTitle.setText(song.getTitle());
        if (tvArtist != null) tvArtist.setText(song.getArtist());
        if (ivCover != null && song.getCover() != null && !song.getCover().isEmpty()) {
            Glide.with(this).load(song.getCover()).into(ivCover);
        }
    }

    // Hàm gọi MiniPlayer giống bên M001
    private void showMiniPlayer() {
        Fragment miniPlayer = new MiniPlayerFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.playerBarContainer, miniPlayer)
                .commitNow(); // commitNow để update kịp thời
        ((MiniPlayerFragment) miniPlayer).updatePlayer(requireContext());
    }
}
