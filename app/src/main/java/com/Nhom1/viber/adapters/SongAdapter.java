package com.Nhom1.viber.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.R;
import com.Nhom1.viber.models.Song;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songList;
    private final OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(List<Song> songList, OnSongClickListener listener) {
        this.songList = songList;
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

        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView cover, menu;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.imgMenu);
            title = itemView.findViewById(R.id.songTitle);
            artist = itemView.findViewById(R.id.songArtist);
            cover = itemView.findViewById(R.id.songCover);
        }
    }
}

