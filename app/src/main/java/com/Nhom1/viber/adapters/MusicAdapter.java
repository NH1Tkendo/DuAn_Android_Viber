package com.Nhom1.viber.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.Activity.M002LibraryFrg;
import com.Nhom1.viber.R;
import com.Nhom1.viber.models.SongEntity;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private final ArrayList<SongEntity> listSong;
    private final OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(SongEntity song);
    }

    public MusicAdapter(ArrayList<SongEntity> listSong, OnSongClickListener listener) {
        if (listSong == null) {
            Log.e("Adapter", "❌ Danh sách bài hát bị null khi khởi tạo Adapter!");
        } else {
            Log.d("Adapter", "✅ Adapter khởi tạo với số bài hát: " + listSong.size());
        }
        this.listSong = listSong != null ? listSong : new ArrayList<>(); // Tránh NullPointerException
        this.listener = listener;
    }


    @Override
    public MusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_offline, parent, false);
        return new MusicHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicAdapter.MusicHolder holder, int position) {
        SongEntity item = listSong.get(position);

        if (item == null) {
            Log.e("Adapter", "❌ item tại vị trí " + position + " bị null!");
            return;
        }

        if (item.getName() == null) {
            Log.w("Adapter", "⚠️ item.getName() bị null! Gán giá trị mặc định.");
            holder.tvName.setText("Unknown Song");
        } else {
            String a = item.getName();
            Log.e("Loi", a);
            holder.tvName.setText(item.getName());
        }

        holder.itemView.setOnClickListener(v -> listener.onSongClick(item));
    }


    @Override
    public int getItemCount() {
        return listSong.size();
    }

    public static class MusicHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public MusicHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_song);
        }
    }
}

