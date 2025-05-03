package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.Nhom1.viber.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class M004UserFrg extends Fragment {
    FirebaseAuth mAuth;
    private Button btnLogout, btnSetting;
    private TextView tvAccountSetting, tvNotificationSetting;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m004_frg_user, container, false);
        initView(view);

        // Sự kiện quay lại
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) nav.setVisibility(View.GONE);

            FirebaseAuth.getInstance().signOut();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new LoginFrg());
            transaction.commit();
        });

        // Sự kiện mở Cài đặt
        btnSetting.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new SettingFragment());
            transaction.addToBackStack(null); // PHẢI CÓ DÒNG NÀY để nút quay lại hoạt động
            transaction.commit();
        });
        return view;
    }

    private void initView(View v) {
        btnLogout = v.findViewById(R.id.btn_logout);
        btnSetting = v.findViewById(R.id.btn_setting);
        btnBack = v.findViewById(R.id.btn_back); // Gán nút quay lại
    }
}
