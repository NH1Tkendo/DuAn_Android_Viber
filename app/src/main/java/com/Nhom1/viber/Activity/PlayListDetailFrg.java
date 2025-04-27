package com.Nhom1.viber.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Nhom1.viber.R;
import com.Nhom1.viber.adapters.PlayListAdapter;
import com.Nhom1.viber.adapters.SongAdapter;
import com.Nhom1.viber.databinding.M001FrgMainBinding;
import com.Nhom1.viber.databinding.PlaylistDetailsBinding;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.utils.BusinessLogic;

import java.util.ArrayList;
import java.util.List;

public class PlayListDetailFrg extends Fragment {
    private static final String ARG_SONGS = "arg_songs";
    private PlaylistDetailsBinding binding;
    private SongAdapter adapter;
    private List<Song> songList;
    private final BusinessLogic bs = new BusinessLogic();

    public static PlayListDetailFrg newInstance(ArrayList<Song> songs) {
        PlayListDetailFrg fragment = new PlayListDetailFrg();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONGS, songs); // Song phải Serializable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songList = (ArrayList<Song>) getArguments().getSerializable(ARG_SONGS);
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
        binding.rvSongHolder.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter(songList, this::onSongClick);
        binding.rvSongHolder.setAdapter(adapter);

    }

    private void onSongClick(Song song) {
        if (song == null) return;

        // Gọi lấy chi tiết bài hát từ Firestore
        bs.GetSongDetail(song.getId(), detailedSong -> {  // giả sử bạn có getSongDetail(String songId, Callback)
            if (detailedSong == null) {
                Log.e("onSongClick", "Không lấy được chi tiết bài hát");
                return;
            }

            // Xóa mini player cũ trước
            MiniPlayerFragment currentFragment = (MiniPlayerFragment) getChildFragmentManager().findFragmentById(R.id.playerBarContainer);
            if (currentFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .remove(currentFragment)
                        .commitNow();
            }

            // Gọi mini player mới
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.playerBarContainer, miniPlayerFragment)
                    .commitNow();

            // Truyền bài hát đã có đầy đủ dữ liệu vào
            miniPlayerFragment.updatePlayer(detailedSong, requireContext());
        });
    }
}
