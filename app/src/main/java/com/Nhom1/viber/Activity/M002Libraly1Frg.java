package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.R;
import com.Nhom1.viber.adapters.PlayListAdapter;
import com.Nhom1.viber.models.PlayList;
import com.Nhom1.viber.models.Song;
import com.Nhom1.viber.services.FirebaseService;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.utils.BusinessLogic;
import com.Nhom1.viber.utils.RecentManager;
import com.Nhom1.viber.Activity.MiniPlayerFragment; // <-- Thêm dòng này
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class M002Libraly1Frg extends Fragment {

    private ImageButton ibtnGanDay, IbtnRandom;
    private FirebaseService firebaseService;
    private List<Song> songList = new ArrayList<>();
    private CardView CVDowload;
    private TextView tvTitle, tvArtist;
    private ImageView ivCover,CreatPlaylist;
    private RecyclerView rvPlaylists;
    private PlayListAdapter adapter;
    private List<PlayList> playLists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.m002_frg_library1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        IbtnRandom = view.findViewById(R.id.IbtnRandom);
        ibtnGanDay = view.findViewById(R.id.ibtnGanDay);
        CVDowload = view.findViewById(R.id.CVDowload);
        tvTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        CreatPlaylist=view.findViewById(R.id.CreatPlaylist);

        firebaseService = new FirebaseService();

        CVDowload.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new M002LibraryFrg());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        CreatPlaylist.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new CreatePlaylist());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        IbtnRandom.setOnClickListener(v -> {
            firebaseService.getSongs(songs -> {
                if (songs != null && !songs.isEmpty()) {
                    songList.clear();
                    songList.addAll(songs);

                    Song randomSong = songs.get(new Random().nextInt(songs.size()));
                    if (randomSong.getId() == null || randomSong.getId().isEmpty()) {
                        Toast.makeText(getContext(), "Bài hát không hợp lệ (thiếu ID)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseService.getSongDetail(randomSong.getId(), fullSong -> {
                        if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                            PlayerManage.getInstance(requireContext()).play(fullSong, requireContext());
                            updatePlayerBar(fullSong);
                            showMiniPlayer(); // <-- Gọi player bar
                        } else {
                            Toast.makeText(getContext(), "Không thể phát bài hát (thiếu URL)", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Không có bài hát nào từ Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        });

        ibtnGanDay.setOnClickListener(v -> {
            List<Song> recentList = RecentManager.getRecentList(requireContext());
            if (recentList == null || recentList.isEmpty()) {
                Toast.makeText(getContext(), "Bạn chưa nghe bài nào gần đây", Toast.LENGTH_SHORT).show();
                return;
            }

            Random rand = new Random();
            Song randomRecent = recentList.get(rand.nextInt(recentList.size()));
            if (randomRecent.getId() == null || randomRecent.getId().isEmpty()) {
                Toast.makeText(getContext(), "Bài hát gần đây bị thiếu ID", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseService.getSongDetail(randomRecent.getId(), fullSong -> {
                if (fullSong != null && fullSong.getUrl() != null && !fullSong.getUrl().isEmpty()) {
                    PlayerManage.getInstance(requireContext()).play(fullSong, requireContext());
                    updatePlayerBar(fullSong);
                    showMiniPlayer(); // <-- Gọi player bar
                } else {
                    Toast.makeText(getContext(), "Không thể phát bài hát (thiếu URL)", Toast.LENGTH_SHORT).show();
                }
            });
        });
        rvPlaylists = view.findViewById(R.id.rvPlaylists);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new PlayListAdapter(playLists, new PlayListAdapter.OnPlayListClickListener() {
            @Override
            public void onPlayListClick(PlayList playList) {
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String playlistId = playList.getPlaylistId();

                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userEmail)
                        .collection("playlists")
                        .document(playlistId)
                        .collection("songs")
                        .get()
                        .addOnSuccessListener(query -> {
                            List<String> songIds = new ArrayList<>();
                            for (DocumentSnapshot doc : query.getDocuments()) {
                                songIds.add(doc.getString("songId"));
                            }

                            if (songIds.isEmpty()) {
                                Toast.makeText(getContext(), "Playlist này chưa có bài hát nào", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Dùng BusinessLogic để lấy chi tiết bài hát
                            Fragment fragment = PlayListDetailFrg.newInstanceFromFirestore(
                                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    playList.getPlaylistId(),
                                    playList.getCover()
                            );
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frame_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Không thể tải bài hát trong playlist", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        rvPlaylists.setAdapter(adapter);
        getParentFragmentManager().setFragmentResultListener(
                "create_playlist_request",
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean created = bundle.getBoolean("playlist_created", false);
                    if (created) {
                        loadPlaylistsFromFirestore(); // Cập nhật danh sách playlist
                    }
                }
        );
        requireActivity().getSupportFragmentManager().setFragmentResultListener(
                "create_playlist_request",
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean created = bundle.getBoolean("playlist_created", false);
                    if (created) {
                        loadPlaylistsFromFirestore(); // Load lại khi có playlist mới
                    }
                }
        );
        loadPlaylistsFromFirestore();

    }

    private void updatePlayerBar(Song song) {
        if (tvTitle != null) tvTitle.setText(song.getTitle());
        if (tvArtist != null) tvArtist.setText(song.getArtist());
        if (ivCover != null && song.getCover() != null && !song.getCover().isEmpty()) {
            Glide.with(this).load(song.getCover()).into(ivCover);
        }
    }

    // Hàm gọi MiniPlayer giống bên M001
    private void showMiniPlayer() {
        Fragment miniPlayer = new MiniPlayerFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.playerBarContainer, miniPlayer)
                .commitNow(); // commitNow để update kịp thời
        ((MiniPlayerFragment) miniPlayer).updatePlayer(requireContext());
    }
    private void loadPlaylistsFromFirestore() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(email)
                .collection("playlists")
                .get()
                .addOnSuccessListener(query -> {
                    playLists.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        PlayList p = doc.toObject(PlayList.class);
                        if (p != null) {
                            p.setPlaylistId(doc.getId()); // <-- BỔ SUNG DÒNG NÀY để gán ID từ Firestore
                            playLists.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("PlaylistLoad", "Lỗi load playlist", e));
    }}