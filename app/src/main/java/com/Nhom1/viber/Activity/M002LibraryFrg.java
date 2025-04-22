package com.Nhom1.viber.Activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

import com.Nhom1.viber.R;
import com.Nhom1.viber.adapters.MusicAdapter;
import com.Nhom1.viber.models.SongEntity;
public class M002LibraryFrg extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView albumArt, ivPlay;
    private ObjectAnimator discAnimator;
    private static final int STATE_IDE = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSED = 3;
    private final ArrayList<SongEntity> listSong = new ArrayList<>();
    private TextView tvName, tvAlbum, tvTime;
    private SeekBar seekBar;
    private int index;
    private SongEntity songEntity;
    private Thread thread;
    private int state = STATE_IDE;
    private String totalTime;
    private RecyclerView recyclerView;
    private View itemController;
    private static final MediaPlayer player = new MediaPlayer();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.m002_frg_library, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        albumArt = view.findViewById(R.id.albumArt);
        ivPlay = view.findViewById(R.id.playButton);
        tvName = view.findViewById(R.id.songTitle);
        tvAlbum = view.findViewById(R.id.artistName);
        tvTime = view.findViewById(R.id.tv_time);
        seekBar = view.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        itemController = view.findViewById(R.id.itemController);
        discAnimator = ObjectAnimator.ofFloat(albumArt, "rotation", 0f, 360f);
        discAnimator.setDuration(5000);
        discAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        discAnimator.setInterpolator(new LinearInterpolator());
        ivPlay.setOnClickListener(this);
        view.findViewById(R.id.prevButton).setOnClickListener(this);
        view.findViewById(R.id.nextButton).setOnClickListener(this);
        // Khởi tạo RecyclerView trước khi tải nhạc
        recyclerView = view.findViewById(R.id.rv_song);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 101);
            } else {
                loadingListSongOffline();
            }
        } else { // Android 12 trở xuống
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                loadingListSongOffline();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadingListSongOffline();
        } else {
            Toast.makeText(requireContext(), "Ứng dụng cần quyền để đọc nhạc!", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("Range")
    private void loadingListSongOffline() {
        listSong.clear(); // Xóa danh sách cũ trước khi tải lại

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM
        };

        Cursor c = requireContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null
        );

        if (c == null) {
            Log.e("MediaStore", "❌ Không thể truy vấn danh sách nhạc!");
            return;
        }

        if (c.getCount() == 0) {
            Log.w("MediaStore", "⚠️ Không có bài hát nào trên thiết bị!");
        } else {
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                listSong.add(new SongEntity(name, path, album != null ? album : "Unknown Album"));
                Log.d("MediaStore", "🎵 Đã tìm thấy: " + name + path + album);
            }
        }
        c.close();

        Log.d("MediaStore", "✅ Tổng số bài hát tìm thấy: " + listSong.size());
        MusicAdapter adapter = new MusicAdapter(listSong, this::playSong);
        recyclerView.setAdapter(adapter);
        // Luôn cập nhật Adapter sau khi load dữ liệu
    }
    public void playSong(SongEntity songEntity) {
        if (isAdded()) { // 🔹 Kiểm tra Fragment đã sẵn sàng chưa
            index = listSong.indexOf(songEntity);
            this.songEntity = songEntity;
            play();
        } else {
            Log.e("DEBUG", "Fragment chưa sẵn sàng, không thể play");
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playButton) playPause();
        else if (v.getId() == R.id.nextButton) next();
        else if (v.getId() == R.id.prevButton) back();
    }
    private void back() {
        index = (index == 0) ? listSong.size() - 1 : index - 1;
        play();
    }
    private void next() {
        index = (index >= listSong.size() - 1) ? 0 : index + 1;
        play();
    }
    private void playPause() {
        if (state == STATE_PLAYING && player.isPlaying()) {
            player.pause();
            ivPlay.setImageResource(R.drawable.ic_play);
            state = STATE_PAUSED;
            discAnimator.pause();
        } else {
            player.start();
            ivPlay.setImageResource(R.drawable.ic_pause);
            state = STATE_PLAYING;
            discAnimator.resume();
        }
    }
    private void play() {
        if (!listSong.isEmpty() && tvName != null && tvAlbum != null) { // 🔹 Kiểm tra null
            songEntity = listSong.get(index);
            tvName.setText(songEntity.getName());
            tvAlbum.setText(songEntity.getAlbum());
            player.reset();
            try {
                player.setDataSource(songEntity.getPath());
                player.prepare();
                player.start();
                ivPlay.setImageResource(R.drawable.ic_pause);
                state = STATE_PLAYING;
                totalTime = getTime(player.getDuration());
                seekBar.setMax(player.getDuration());
                itemController.setVisibility(View.VISIBLE);
                if (thread == null) {
                    startLooping();
                }
                int randomColor = getRandomColor();
                itemController.setBackgroundColor(randomColor);
                Log.e("DEBUG", "Màu nền mới: " + Integer.toHexString(randomColor));
                if (!discAnimator.isRunning()) {
                    discAnimator.start();
                }
                player.setOnCompletionListener(mp -> {
                    next(); // Chuyển bài tiếp theo
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("DEBUG", "play() bị gọi trước khi Fragment hoàn tất hoặc tvName bị null");
        }
    }
    private void startLooping() {
        thread = new Thread(() -> {
            while (state == STATE_PLAYING) {
                try {
                    Thread.sleep(200);
                    requireActivity().runOnUiThread(this::updateTime);
                } catch (Exception e) {
                    return;
                }
            }
        });
        thread.start();
    }
    private void updateTime() {
        tvTime.setText(String.format("%s/%s", getTime(player.getCurrentPosition()), totalTime));
        seekBar.setProgress(player.getCurrentPosition());
    }
    @SuppressLint("SimpleDateFormat")
    private String getTime(int time) {
        return new SimpleDateFormat("mm:ss").format(new Date(time));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null) thread.interrupt();
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (state == STATE_PLAYING || state == STATE_PAUSED) {
            player.seekTo(seekBar.getProgress());
        }
    }
    private int getRandomColor() {
        Random random = new Random();
        float hue = random.nextInt(360);  // Hue: Chọn ngẫu nhiên từ 0 đến 360 (để có đủ các màu sắc)
        float saturation = 0.7f + random.nextFloat() * 0.3f;  // Saturation: Giữ màu sắc tươi (0.7 - 1.0)
        float brightness = 0.6f + random.nextFloat() * 0.4f;  // Brightness: Giữ độ sáng cao (0.6 - 1.0)
        return Color.HSVToColor(new float[]{hue, saturation, brightness});
    }
}
