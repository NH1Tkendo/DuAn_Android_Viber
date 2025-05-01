package com.Nhom1.viber.Activity;

import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayListDetailFrg extends Fragment {
    private static final String ARG_SONGS = "arg_songs";
    private static final String ARG_COVER = "arg_cover";

    private PlaylistDetailsBinding binding;
    private SongAdapter adapter;
    private List<Song> songList;
    private String playListCover;
    private final BusinessLogic bs = new BusinessLogic();

    public static PlayListDetailFrg newInstance(ArrayList<Song> songs, String cover) {
        PlayListDetailFrg fragment = new PlayListDetailFrg();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONGS, songs); // Song phải Serializable
        args.putString(ARG_COVER, cover);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songList = (ArrayList<Song>) getArguments().getSerializable(ARG_SONGS);
            playListCover = getArguments().getString(ARG_COVER);
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
        Glide.with(requireContext())
                .load(playListCover)
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(binding.imgCover);

        binding.rvSongHolder.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter(songList, this::onSongClick);
        binding.rvSongHolder.setAdapter(adapter);

        binding.imgReturn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        binding.btnPlay.setOnClickListener(v -> {
            ArrayList<Song> subListCopy = new ArrayList<>(songList);
            PlayerManage manager = PlayerManage.getInstance(requireContext());
            manager.setQueue(subListCopy);
            manager.setCurrentSong(songList.get(0));
            manager.setCurrentIndex(0);

            FrameLayout playerBar = requireActivity().findViewById(R.id.playerBarContainer);
            if (playerBar != null) {
                playerBar.setVisibility(View.VISIBLE);

                // Xóa mini player cũ nếu có
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                MiniPlayerFragment currentFragment = (MiniPlayerFragment) fragmentManager.findFragmentById(R.id.playerBarContainer);
                if (currentFragment != null) {
                    fragmentManager.beginTransaction()
                            .remove(currentFragment)
                            .commitNow();
                }

                // Gọi mini player mới
                MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.playerBarContainer, miniPlayerFragment)
                        .commitNow();

                // Truyền bài hát đã có đầy đủ dữ liệu vào miniPlayerFragment
                miniPlayerFragment.updatePlayer(requireContext());
                manager.playCurrent(requireContext());
            }
        });
    }

    private void onSongClick(Song song) {
        if (song == null) return;
        Collections.shuffle(songList);
        ArrayList<Song> subListCopy = new ArrayList<>(songList);
        PlayerManage manager = PlayerManage.getInstance(requireContext());
        manager.setQueue(subListCopy);
        manager.setCurrentSong(song);
        manager.setCurrentIndex(-1);

        FrameLayout playerBar = requireActivity().findViewById(R.id.playerBarContainer);
        if (playerBar != null) {
            playerBar.setVisibility(View.VISIBLE);

            // Xóa mini player cũ nếu có
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            MiniPlayerFragment currentFragment = (MiniPlayerFragment) fragmentManager.findFragmentById(R.id.playerBarContainer);
            if (currentFragment != null) {
                fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commitNow();
            }

            // Gọi mini player mới
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.playerBarContainer, miniPlayerFragment)
                    .commitNow();

            // Truyền bài hát đã có đầy đủ dữ liệu vào miniPlayerFragment
            miniPlayerFragment.updatePlayer(requireContext());
            manager.playCurrent(requireContext());
        }
    }
}
