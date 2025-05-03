package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Nhom1.viber.R;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.adapters.SongAdapter;
import com.Nhom1.viber.databinding.PlaylistDetailsBinding;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.utils.BusinessLogic;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayListDetailFrg extends Fragment {

    private static final String ARG_SONGS = "arg_songs";
    private static final String ARG_COVER = "arg_cover";
    private static final String ARG_EMAIL = "arg_email";
    private static final String ARG_PLAYLIST_ID = "arg_playlist_id";

    private PlaylistDetailsBinding binding;
    private SongAdapter adapter;
    private List<Song> songList = new ArrayList<>();

    private final BusinessLogic bs = new BusinessLogic();

    private String playListCover;
    private String userEmail;
    private String playlistId;


    public static PlayListDetailFrg newInstanceFromFirestore(String userEmail, String playlistId, String cover) {

        PlayListDetailFrg fragment = new PlayListDetailFrg();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, userEmail);
        args.putString(ARG_PLAYLIST_ID, playlistId);
        args.putString(ARG_COVER, cover);
        fragment.setArguments(args);
        return fragment;
    }
    // Playlist mặc định (có sẵn danh sách bài hát)
    public static PlayListDetailFrg newInstance(ArrayList<Song> songs, String cover) {
        PlayListDetailFrg fragment = new PlayListDetailFrg();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONGS, songs);
        args.putString(ARG_COVER, cover);
        fragment.setArguments(args);
        return fragment;
    }

    // Playlist tự tạo (cần truy vấn danh sách bài hát)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playListCover = getArguments().getString(ARG_COVER);
            userEmail = getArguments().getString(ARG_EMAIL);
            playlistId = getArguments().getString(ARG_PLAYLIST_ID);

            // Nếu là playlist mặc định thì lấy danh sách từ arguments
            if (userEmail == null || playlistId == null) {
                songList = (ArrayList<Song>) getArguments().getSerializable(ARG_SONGS);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PlaylistDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setupUI();
        loadSongsIfNeeded();
    }

    private void setupUI() {
        // Load ảnh bìa playlist
        Glide.with(requireContext())
                .load(playListCover)
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(binding.imgCover);

        // Thiết lập RecyclerView
        binding.rvSongHolder.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter(songList, this::onSongClick);
        binding.rvSongHolder.setAdapter(adapter);

        // Nút quay lại
        binding.imgReturn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Nút phát tất cả
        binding.btnPlay.setOnClickListener(v -> {
            if (songList.isEmpty()) return;
            PlayerManage manager = PlayerManage.getInstance(requireContext());
            manager.setQueue(new ArrayList<>(songList));
            manager.setCurrentSong(songList.get(0));
            manager.setCurrentIndex(0);
            showMiniPlayer();
        });
    }

    private void loadSongsIfNeeded() {
        // Nếu là playlist tự tạo thì gọi BusinessLogic để lấy bài hát
        if (userEmail != null && playlistId != null) {
            bs.GetPlaylistSongs(userEmail, playlistId, songs -> {
                songList.clear();
                songList.addAll(songs);
                adapter.notifyDataSetChanged();

                // ✅ Nếu đã có bài đang phát thì hiển thị playbar
                if (PlayerManage.getInstance(requireContext()).getCurrentSong() != null) {
                    showMiniPlayer();
                }
            });
        }
    }
    private void onSongClick(Song song) {
        if (song == null) return;
        Collections.shuffle(songList); // nếu bạn không muốn shuffle thì xóa dòng này
        PlayerManage manager = PlayerManage.getInstance(requireContext());
        manager.setQueue(new ArrayList<>(songList));
        manager.setCurrentSong(song);
        manager.setCurrentIndex(-1);
        showMiniPlayer();
    }

    private void showMiniPlayer() {
        FrameLayout playerBar = requireActivity().findViewById(R.id.playerBarContainer);
        if (playerBar != null) {
            playerBar.setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            Fragment current = fragmentManager.findFragmentById(R.id.playerBarContainer);
            if (current != null) {
                fragmentManager.beginTransaction().remove(current).commitNow();
            }

            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.playerBarContainer, miniPlayerFragment)
                    .commitNow();

            miniPlayerFragment.updatePlayer(requireContext());
            PlayerManage.getInstance(requireContext()).playCurrent(requireContext());
        }
    }
}


