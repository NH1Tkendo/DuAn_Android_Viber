package com.Nhom1.viber.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.Nhom1.viber.R;
import com.Nhom1.viber.adapters.SliderAdapter;
import com.Nhom1.viber.adapters.SongAdapter;
import com.Nhom1.viber.databinding.HotHitSongBinding;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.databinding.M001FrgMainBinding;
import com.Nhom1.viber.utils.BusinessLogic;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class M001MainFrg extends Fragment {
    private M001FrgMainBinding binding;
    private HotHitSongBinding hotHitSongBinding;
    private SongAdapter adapter;
    private final List<Song> songList = new ArrayList<>();
    private final BusinessLogic bs = new BusinessLogic();
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (hotHitSongBinding.adBanner.getAdapter() == null) return;

            int currentItem = hotHitSongBinding.adBanner.getCurrentItem();
            int nextItem = currentItem + 1;

            hotHitSongBinding.adBanner.setCurrentItem(nextItem, true); // Chuyển ảnh
            sliderHandler.postDelayed(this, 3000); // Duy trì auto-scroll
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = M001FrgMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        hotHitSongBinding = binding.viewPagerContainer;
        // Cấu hình RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongAdapter(songList, this::onSongClick);
        binding.recyclerView.setAdapter(adapter);
        //------------------------------------------------------------------------
        // Khởi tạo FirebaseService
        new Thread(this::loadSongs).start();
        loadBanner();
        //------------------------------------------------------------------------
    }
    //==============================================
    //=============Xử lý chức năng =================
    private void onSongClick(Song song) {
        if (song == null) return;
        //Xoa mini player cu truoc khi goi miniplayer moi
        MiniPlayerFragment currentFragment = (MiniPlayerFragment) getChildFragmentManager().findFragmentById(R.id.playerBarContainer);
        if (currentFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commitNow();
        }
        //================================================
        MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.playerBarContainer, miniPlayerFragment)
                .commitNow();

        miniPlayerFragment.updatePlayer(song, requireContext());
        binding.playerBarContainer.setVisibility(View.VISIBLE);
    }
    //=============================================
    //=============Xử lý giao diện =================
    @SuppressLint("NotifyDataSetChanged")
    private void loadSongs() {
        binding.progressBarMusic.setVisibility(View.VISIBLE);

        bs.GetListSong(songListResult -> {
            if (songListResult != null && !songListResult.isEmpty()) {
                songList.clear();
                songList.addAll(songListResult);
                adapter.notifyDataSetChanged();
            } else {
                Log.e("LoadSongs", "Danh sách bài hát rỗng hoặc null");
            }
        });

        binding.progressBarMusic.setVisibility(View.GONE);
    }

    private void loadBanner() {
        hotHitSongBinding.progressBarSlider.setVisibility(View.VISIBLE);
        long start = System.currentTimeMillis();
        bs.GetBannerSongs(songs -> {
            if (songs != null && !songs.isEmpty()) {
                banners(songs); // Cập nhật dữ liệu
            } else {
                Log.e("LoadSongs", "Danh sách bài hát rỗng hoặc null");
            }
            hotHitSongBinding.progressBarSlider.setVisibility(View.GONE);
            long end = System.currentTimeMillis();
            Log.d("🔥FirebaseTime", "Tải banner mất: " + (end - start) + "ms");
        });
    }

    private void banners(List<Song> topFive) {
        SliderAdapter adapter = new SliderAdapter(topFive);
        hotHitSongBinding.adBanner.setAdapter(adapter);
        hotHitSongBinding.adBanner.setClipToPadding(false);
        hotHitSongBinding.adBanner.setClipChildren(false);
        hotHitSongBinding.adBanner.setOffscreenPageLimit(3);
        if (hotHitSongBinding.adBanner.getChildCount() > 0) { // Kiểm tra để tránh lỗi
            hotHitSongBinding.adBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        }

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        hotHitSongBinding.adBanner.setPageTransformer(compositePageTransformer);
        hotHitSongBinding.adBanner.setCurrentItem(1);

        hotHitSongBinding.adBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    int curr = hotHitSongBinding.adBanner.getCurrentItem();
                    int lastReal = songList.size() - 1;

                    if (curr == 0) {
                        hotHitSongBinding.adBanner.setCurrentItem(lastReal, false);
                    } else if (curr == lastReal + 2) {
                        hotHitSongBinding.adBanner.setCurrentItem(1, false);
                    }

                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }

                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    sliderHandler.removeCallbacks(sliderRunnable); // Dừng khi user kéo
                }
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.post(sliderRunnable); // Bắt đầu tự động chạy ViewPager khi Fragment hiển thị
    }
    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable); // Dừng Runnable khi Fragment không hiển thị
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy binding để tránh memory leak
        binding = null;
    }
}
