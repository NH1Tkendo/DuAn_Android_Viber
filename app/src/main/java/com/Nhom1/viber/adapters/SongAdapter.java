package com.Nhom1.viber.adapters;



import android.app.AlertDialog;
import android.content.Context;

import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.R;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.models.Song;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songList;
    private final OnSongClickListener listener;
    private boolean showIndex;
    private boolean highlightCurrent = false;
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(List<Song> songList, boolean showIndex, OnSongClickListener listener) {
        this.songList = songList;
        this.showIndex = showIndex;
        this.listener = listener;
    }

    public void setData(List<Song> data) {
        this.songList = new ArrayList<>(data); // hoặc this.songList.clear(); this.songList.addAll(data);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (showIndex) {
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText(String.valueOf(position + 1));
        } else {
            holder.tvIndex.setVisibility(View.GONE);
        }

        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        //Xử lý ảnh bìa dành cho nhạc online
        if (song.getCover() != null && !song.getCover().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(song.getCover())
                    .placeholder(R.drawable.viber) // Ảnh tạm thời khi đang tải
                    .error(R.drawable.viber) // Ảnh thay thế khi lỗi
                    .into(holder.cover);
        }

        if (highlightCurrent) {
            int currentIndex = PlayerManage.getInstance(holder.itemView.getContext()).getCurrentIndex();
            if (position == currentIndex) {
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));
        holder.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.song_options_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_add_to_playlist) {
                    showSelectPlaylistDialog(v.getContext(), song);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

    }
    public void setHighlightCurrent(boolean highlight) {
        this.highlightCurrent = highlight;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return (songList != null) ? songList.size() : 0;
    }
    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, tvIndex;
        ImageView cover, menu;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            menu = itemView.findViewById(R.id.imgMenu);
            title = itemView.findViewById(R.id.songTitle);
            artist = itemView.findViewById(R.id.songArtist);
            cover = itemView.findViewById(R.id.songCover);
        }
    }
    private void showSelectPlaylistDialog(Context context, Song song) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(context, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail(); // ví dụ: "vy11@gmail.com"
        if (userEmail == null) {
            Toast.makeText(context, "Không lấy được email người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_playlist, null);
        ListView lvPlaylists = dialogView.findViewById(R.id.lvPlaylists);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Chọn playlist để thêm")
                .setView(dialogView)
                .setNegativeButton("Hủy", null)
                .create();

        db.collection("users")
                .document(userEmail)
                .collection("playlists")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> playlistNames = new ArrayList<>();
                    List<String> playlistIds = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String name = doc.getString("name");
                        if (name != null) {
                            playlistNames.add(name);
                            playlistIds.add(doc.getId()); // lưu ID document
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, playlistNames);
                    lvPlaylists.setAdapter(adapter);

                    lvPlaylists.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedPlaylistId = playlistIds.get(position);

                        // Chỉ lưu ID bài hát
                        Map<String, Object> songRefData = new HashMap<>();
                        songRefData.put("songId", song.getId()); // ID từ Firestore collection "songs"

                        db.collection("users")
                                .document(userEmail)
                                .collection("playlists")
                                .document(selectedPlaylistId)
                                .collection("songs")
                                .add(songRefData)
                                .addOnSuccessListener(docRef -> Toast.makeText(context, "Đã thêm vào playlist", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi thêm bài hát", Toast.LENGTH_SHORT).show());

                        dialog.dismiss();
                    });

                    dialog.show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Không thể tải danh sách playlist", Toast.LENGTH_SHORT).show();
                });
    }
    private void addSongToPlaylist(Context context, String playlistId, Song song) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("playlists")
                .document(playlistId)
                .collection("songs")
                .document(song.getId()) // Dùng ID bài hát làm document ID
                .set(song)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Đã thêm vào playlist", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi khi thêm bài hát", Toast.LENGTH_SHORT).show());
    }
}

