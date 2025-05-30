package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.Nhom1.viber.databinding.SearchScreenBinding;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.utils.BusinessLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchScreenFrg extends Fragment {
    private SearchScreenBinding binding;
    private SongAdapter adapter;
    private final List<Song> song = new ArrayList<>();
    private List<Song> songList = new ArrayList<>();
    private boolean isLoading = false;
    private final BusinessLogic bs = new BusinessLogic();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            songList = (ArrayList<Song>) args.getSerializable("song_list");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        binding.recyclerSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongAdapter(song, false, this::onSongClick);
        binding.recyclerSongs.setAdapter(adapter);

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Mỗi khi người dùng nhập, gọi hàm tìm kiếm
                performSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    binding.layoutSearchHint.setVisibility(View.VISIBLE);
                    binding.recyclerSongs.setVisibility(View.GONE);
                } else {
                    binding.layoutSearchHint.setVisibility(View.GONE);
                    binding.recyclerSongs.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            // override các method còn lại nếu cần
        });

    }
    private void performSearch(String keyword) {
        boolean isLastPage = false;
        String currentKeyword = "";
        if (keyword.isEmpty()) {
            adapter.setData(new ArrayList<>());
            isLastPage = false;
            currentKeyword = "";
            return;
        }

        isLoading = true;
        isLastPage = false;
        currentKeyword = keyword;

        // Bắt đầu tìm kiếm mới
        bs.SearchKeyWord(keyword, true, songs -> {
            isLoading = false;
            if (songs != null) {
                adapter.setData(songs); // setData để thay toàn bộ danh sách
            }
        });
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
