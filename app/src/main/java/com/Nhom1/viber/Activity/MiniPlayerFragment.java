package com.Nhom1.viber.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.Nhom1.viber.R;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.databinding.PlayerBarBinding;
import com.Nhom1.viber.models.Song;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MiniPlayerFragment extends Fragment {
    private ExoPlayer player;
    private PlayerBarBinding binding;
    private Song currentSong;
    private boolean isFavorite = false;

    private Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (binding != null) {
                binding.btnPlayPause.setImageResource(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play
                );
            }
        }
    };

    public void updatePlayer(Song song, Context context) {
        this.currentSong = song;
        if (binding == null || context == null) return;

        PlayerManage manager = PlayerManage.getInstance(requireContext());
        player = manager.getPlayer();


        binding.tvSongTitle.setText(song.getTitle());
        binding.tvArtist.setText(song.getArtist());
        Glide.with(context)
                .load(song.getCover())
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(binding.imgSong);

        manager.play(song, context);
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

        binding.playerBar.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);

            FullPlayerFragment fullPlayerFragment = new FullPlayerFragment();

            // Truyền dữ liệu bài hát bằng Bundle
            Bundle bundle = new Bundle();
            bundle.putSerializable("song", currentSong);
            fullPlayerFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_up,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_down
                    )
                    .add(R.id.mainScreen, fullPlayerFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
