package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.Nhom1.viber.Singleton.PlayerManage;;
import com.Nhom1.viber.adapters.SongAdapter;;
import com.Nhom1.viber.databinding.MusicQueueBinding;
import com.Nhom1.viber.models.Song;
import java.util.ArrayList;

public class MusicQueueFrg extends Fragment {
    private MusicQueueBinding binding;
    private SongAdapter adapter;
    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            if (adapter != null) {
                adapter.notifyDataSetChanged(); // cập nhật lại UI
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MusicQueueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        PlayerManage manager = PlayerManage.getInstance(requireContext());
        ArrayList<Song> list = manager.getQueue();
        binding.rvQueue.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter(list, false, this::onSongClick);
        adapter.setHighlightCurrent(true);
        binding.rvQueue.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void onSongClick(Song song) {
        if (song == null) return;

        PlayerManage manager = PlayerManage.getInstance(requireContext());
        manager.setCurrentSong(song);
        manager.playCurrent(requireContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerManage.getInstance(requireContext()).addListener(playerListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerManage.getInstance(requireContext()).removeListener(playerListener);
    }
}
