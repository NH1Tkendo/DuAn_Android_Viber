package com.Nhom1.viber.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.Nhom1.viber.R;
import com.Nhom1.viber.adapters.PlayListAdapter;
import com.Nhom1.viber.adapters.SliderAdapter;
import com.Nhom1.viber.adapters.SongAdapter;
import com.Nhom1.viber.databinding.HotHitSongBinding;
import com.Nhom1.viber.models.PlayList;
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
    private PlayListAdapter adapterPL;
    private PlayListAdapter adapterGN;
    private final List<Song> songList = new ArrayList<>();
    private final List<PlayList> playLists = new ArrayList<>();
    private final List<PlayList> playListsGenre = new ArrayList<>();
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
        GridLayoutManager songManager = new GridLayoutManager(
                getContext(),
                3, // số dòng muốn hiển thị
                GridLayoutManager.HORIZONTAL, // cuộn ngang
                false
        );

        GridLayoutManager playListManager = new GridLayoutManager(
                getContext(),
                1, // số dòng muốn hiển thị
                GridLayoutManager.HORIZONTAL, // cuộn ngang
                false
        );

        GridLayoutManager playListManager2 = new GridLayoutManager(
                getContext(),
                1, // số dòng muốn hiển thị
                GridLayoutManager.HORIZONTAL, // cuộn ngang
                false
        );

        binding.recyclerView.setLayoutManager(songManager);
        adapter = new SongAdapter(songList, this::onSongClick);
        binding.recyclerView.setAdapter(adapter);

        binding.rvArtistPlaylist.setLayoutManager(playListManager);
        adapterPL = new PlayListAdapter(playLists, this::onPlayListClick);
        binding.rvArtistPlaylist.setAdapter(adapterPL);

        binding.rvGenresPlaylist.setLayoutManager(playListManager2);
        adapterGN = new PlayListAdapter(playListsGenre, this::onPlayListClick);
        binding.rvGenresPlaylist.setAdapter(adapterGN);
        //------------------------------------------------------------------------
        // Khởi tạo FirebaseService
        loadSongs();
        loadBanner();
        loadPlayListArtist();
        loadPlayListGenres();
        //------------------------------------------------------------------------
    }
    //==============================================
    //=============Xử lý chức năng =================
    private void onPlayListClick(PlayList playList){
        if(playList != null){
            List<String> songIds = playList.getSongs();
            String playListCover = playList.getCover();
            bs.GetPlayListDetails(songIds, detailedSong -> {
                if (detailedSong == null) {
                    Log.e("onSongClick", "Không lấy được chi tiết bài hát");
                    return;
                }
                PlayListDetailFrg fragment = PlayListDetailFrg.newInstance(new ArrayList<>(detailedSong), playListCover);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
    private void onSongClick(Song song) {
        if (song == null) return;

        bs.GetSongDetail(song.getId(), detailedSong -> {
            if (detailedSong == null) {
                Log.e("onSongClick", "Không lấy được chi tiết bài hát");
                return;
            }

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
                miniPlayerFragment.updatePlayer(detailedSong, requireContext());
            }
        });
    }

    //=============================================
    //=============Xử lý giao diện =================
    private void loadPlayListArtist(){
        bs.GetPlayListArtist(listResult ->{
            if(listResult != null && !listResult.isEmpty()){
                playLists.clear();
                playLists.addAll(listResult);
                adapterPL.notifyDataSetChanged();
            }else
                Log.e("LoadSongs", "Danh sách bài hát rỗng hoặc null");
        });
    }

    private void loadPlayListGenres(){
        bs.GetPlayListGenres(listResult ->{
            if(listResult != null && !listResult.isEmpty()){
                playListsGenre.clear();
                playListsGenre.addAll(listResult);
                adapterGN.notifyDataSetChanged();
            }else
                Log.e("LoadSongs", "Danh sách bài hát rỗng hoặc null");
        });
    }

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
