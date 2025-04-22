package com.Nhom1.viber.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nhom1.viber.databinding.SliderItemBinding;
import com.Nhom1.viber.models.Song;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private final List<Song> songList;
    private Context context;

    public SliderAdapter(List<Song> songList) {
        this.songList = songList;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private final SliderItemBinding binding;

        public SliderViewHolder(SliderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Song song) {
            if (context != null) {
                Glide.with(context)
                        .load(song.getCover())
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(60)))
                        .into(binding.imageSlide);
            }
        }
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        SliderItemBinding binding = SliderItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SliderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int realPosition = position % songList.size(); // Đảm bảo lặp vô hạn mà không bỏ ảnh
        holder.bind(songList.get(realPosition));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Cho phép lướt vô hạn
    }
}



