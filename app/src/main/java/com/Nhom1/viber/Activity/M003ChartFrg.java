package com.Nhom1.viber.Activity;

import android.annotation.SuppressLint;
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
import com.Nhom1.viber.databinding.M003FrgChartBinding;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.utils.BusinessLogic;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class M003ChartFrg extends Fragment {
    private M003FrgChartBinding binding;
    private BarChart barChart;
    private final BusinessLogic bs = new BusinessLogic();
    private final List<Song> songList = new ArrayList<>();
    private SongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = M003FrgChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        barChart = binding.barChart;
        loadSongs();

        binding.rvTopSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter(songList, true, this::onSongClick);
        binding.rvTopSongs.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadSongs() {
        bs.GetDataSet(10,song -> {
            if (song != null && !song.isEmpty()) {
                songList.clear();
                songList.addAll(song);
                List<Song> top3 = songList.subList(0,3);
                showBarChart(top3);
                adapter.notifyDataSetChanged();
            } else {
                Log.e("LoadSongs", "Danh sách bài hát rỗng hoặc null");
            }
        });

    }

    private void showBarChart(List<Song> songs) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            entries.add(new BarEntry(i, song.getPlays()));

            // Cắt ngắn tên nếu dài > 10 ký tự
            String title = song.getTitle();
            if (title.length() > 10) {
                title = title.substring(0, 10) + "...";
            }
            labels.add(title);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Lượt phát");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f); // chữ trên cột

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f); // Thu hẹp chiều rộng cột

        barChart.setData(data);

        // Trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // bỏ lưới trục X
        xAxis.setLabelRotationAngle(-30); // nghiêng chữ cho đỡ chồng

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });

        // Trục Y
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawGridLines(false); // tắt lưới dọc

        barChart.getAxisRight().setEnabled(false); // tắt trục phải

        // Description & Legend
        barChart.getDescription().setEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false); // Tắt nếu không cần thiết

        // Hiệu ứng & vẽ lại
        barChart.setExtraBottomOffset(10f); // đẩy biểu đồ lên tí để tránh chữ bị cắt
        barChart.animateY(1000);
        barChart.invalidate();
    }
    private void onSongClick(Song song) {
        if (song == null) return;
        Collections.shuffle(songList);
        ArrayList<Song> subListCopy = new ArrayList<>(songList.subList(0, 10));

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
