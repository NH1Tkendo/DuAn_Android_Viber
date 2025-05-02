package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.databinding.CreatePlaylistBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePlaylist extends Fragment {
    private CreatePlaylistBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CreatePlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSavePlaylist.setOnClickListener(v -> {
            String name = binding.editTextText.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên playlist", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            createPlaylistForUser(email, name);
        });
    }

    private void createPlaylistForUser(String userEmail, String playlistName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference playlistsRef = db
                .collection("users")
                .document(userEmail)
                .collection("playlists");

        Map<String, Object> playlist = new HashMap<>();
        playlist.put("name", playlistName);
        playlist.put("createdAt", System.currentTimeMillis());

        playlistsRef.add(playlist)
                .addOnSuccessListener(doc -> {
                    Log.d("Firestore", "Tạo playlist thành công: " + doc.getId());
                    Toast.makeText(getContext(), "Đã tạo playlist", Toast.LENGTH_SHORT).show();

                    // Gửi kết quả về Fragment trước
                    Bundle result = new Bundle();
                    result.putBoolean("playlist_created", true);

                    requireActivity()
                            .getSupportFragmentManager()
                            .setFragmentResult("create_playlist_request", result);

                    requireActivity().onBackPressed(); // Thoát
                });

   } }