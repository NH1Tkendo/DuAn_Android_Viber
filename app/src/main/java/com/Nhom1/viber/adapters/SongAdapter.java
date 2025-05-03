package com.Nhom1.viber.adapters;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.R;
import com.Nhom1.viber.Singleton.PlayerManage;
import com.Nhom1.viber.models.Song;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

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
    }

    public void setHighlightCurrent(boolean highlight) {
        this.highlightCurrent = highlight;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songList.size();
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
}

