package com.Nhom1.viber.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.Nhom1.viber.R;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.databinding.FullPlayerBinding;
import com.Nhom1.viber.models.Song;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FullPlayerFragment extends Fragment {
    private FullPlayerBinding binding;
    private ExoPlayer player;
    private Song currentSong;
    private final Handler handler = new Handler();
    private final Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                int currentPos = (int) player.getCurrentPosition() / 1000;
                binding.sbMusicTimeBar.setProgress(currentPos);
            }
            handler.postDelayed(this, 1000);
        }
    };

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (binding != null) {
                binding.btnPause.setImageResource(
                        isPlaying ? R.drawable.ic_pause: R.drawable.ic_play
                );
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FullPlayerBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            currentSong = (Song) getArguments().getSerializable("song");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentSong != null && getContext() != null) {
            updateFullPlayer(currentSong, requireContext());
        }

        binding.imgReturn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
            nav.setVisibility(View.VISIBLE);
        });
        //Đồng bộ seekbar với player
        binding.sbMusicTimeBar.setMax((int) player.getDuration() / 1000); // set tối đa = tổng thời lượng (giây)
        handler.post(updateSeekbar);
        binding.sbMusicTimeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player != null) {
                    player.seekTo(progress * 1000L); // Seek theo mili giây
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tạm ngưng cập nhật nếu cần
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume cập nhật nếu cần
            }
        });
        //===========================
        PlayerManage manager = PlayerManage.getInstance(requireContext());
        if (player != null) {
            boolean isPlaying = player.isPlaying();
            binding.btnPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
        binding.btnPause.setOnClickListener(v -> {
            if (player == null) return;
            if (player.isPlaying()) {
                manager.pause();
                binding.btnPause.setImageResource(R.drawable.ic_play);
            } else {
                manager.resume();
                binding.btnPause.setImageResource(R.drawable.ic_pause);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int totalSecs) {
        int minutes = totalSecs / 60;
        int seconds = totalSecs % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void updateFullPlayer(Song song, Context context){
        currentSong = song;
        if (binding == null || context == null) return;

        PlayerManage manager = PlayerManage.getInstance(requireContext());
        player = manager.getPlayer();

        String endTime = formatTime(currentSong.getDuration());
        binding.txtTenBaiHat.setText(currentSong.getTitle());
        binding.txtTenCaSi.setText(currentSong.getArtist());
        binding.txtThoiGianKetThuc.setText(endTime);
        Glide.with(context)
                .load(currentSong.getCover())
                .placeholder(R.drawable.viber)
                .error(R.drawable.viber)
                .into(binding.imgCover);
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
        handler.removeCallbacks(updateSeekbar);
        binding = null;
    }
}
