package com.Nhom1.viber.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.Nhom1.viber.R;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.databinding.PlayerBarBinding;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.utils.ControlUI;
import com.Nhom1.viber.utils.NavigateToFullPlayer;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;




public class MiniPlayerFragment extends Fragment implements PlayerManage.PlayerUpdateListener {
    private ExoPlayer player;
    private PlayerBarBinding binding;
    private boolean isFavorite = false;
    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (binding != null) {
                binding.btnPlayPause.setImageResource(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play
                );
            }
        }
    };

    public void updatePlayer(Context context) {
        if (binding == null || context == null) return;

        PlayerManage manager = PlayerManage.getInstance(requireContext());
        Song current = manager.getCurrentSong();
        player = manager.getPlayer();
        if (current == null) return;

        binding.tvSongTitle.setText(current.getTitle());
        binding.tvArtist.setText(current.getArtist());
        Glide.with(context)
                .load(current.getCover())
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(binding.imgSong);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PlayerBarBinding.inflate(inflater, container, false);
        PlayerManage manager = PlayerManage.getInstance(requireContext());

        binding.btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            binding.btnFavorite.setImageResource(isFavorite ? R.drawable.ic_fullfavorite : R.drawable.ic_favorite);
        });

        binding.btnPlayPause.setOnClickListener(v -> {
            if (player == null) return;
            if (player.isPlaying()) {
                manager.pause();
                binding.btnPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                manager.resume();
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            PlayerManage.getInstance(requireContext()).playNext(requireContext());
            updatePlayer(requireContext());
        });
        binding.playerBar.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof NavigateToFullPlayer) {
                ((NavigateToFullPlayer) activity).openFullPlayer();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerManage.getInstance(requireContext()).addListener(playerListener);
        PlayerManage.getInstance(requireContext()).addUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerManage.getInstance(requireContext()).removeListener(playerListener);
        PlayerManage.getInstance(requireContext()).removeUpdateListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void updatePlayer() {
        updatePlayer(requireContext());
    }
}
