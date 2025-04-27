package com.Nhom1.viber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.R;
import com.Nhom1.viber.models.PlayList;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListHolder>{
    private final List<PlayList> playLists;
    private final OnPlayListClickListener listener;

    public interface OnPlayListClickListener {
        void onPlayListClick(PlayList playList);
    }

    public PlayListAdapter(List<PlayList> playLists, OnPlayListClickListener listener) {
        this.playLists = playLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlayListAdapter.PlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {
        PlayList playList = playLists.get(position);
        holder.title.setText(playList.getName());

        if (playList.getCover() != null && !playList.getCover().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(playList.getCover())
                    .placeholder(R.drawable.viber) // Ảnh tạm thời khi đang tải
                    .error(R.drawable.viber) // Ảnh thay thế khi lỗi
                    .into(holder.cover);
        }

        holder.itemView.setOnClickListener(v -> listener.onPlayListClick(playList));
    }


    @Override
    public int getItemCount() {
        return playLists.size();
    }

    static class PlayListHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView cover;


        public PlayListHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtPlayListName);
            cover = itemView.findViewById(R.id.imgPic);
        }
    }
}
