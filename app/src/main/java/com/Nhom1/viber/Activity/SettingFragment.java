package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.ImageButton;

import com.Nhom1.viber.R;

public class SettingFragment extends Fragment {

    private TextView tvAccountSetting, tvNotificationSetting;
    private ImageButton btnBack; // THÊM biến này

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Ánh xạ các thành phần giao diện
        tvAccountSetting = view.findViewById(R.id.tvAccountSetting);
        tvNotificationSetting = view.findViewById(R.id.tvNotificationSetting);
        btnBack = view.findViewById(R.id.btn_back); // Gán ID nút back

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại Fragment trước
        });

        // Sự kiện mở Cài đặt tài khoản
        tvAccountSetting.setOnClickListener(v -> {
            AccountSettingFragment fragment = new AccountSettingFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Sự kiện mở Cài đặt thông báo
        tvNotificationSetting.setOnClickListener(v -> {
            NotificationSettingFragment fragment = new NotificationSettingFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();;
        });

        return view;
    }
}
