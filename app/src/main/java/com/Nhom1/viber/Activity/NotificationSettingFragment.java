package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;

public class NotificationSettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_setting, container, false);

        Switch notificationSwitch = view.findViewById(R.id.switch_notification);
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Bật thông báo
                Toast.makeText(getContext(), "Thông báo đã được bật", Toast.LENGTH_SHORT).show();
            } else {
                // Tắt thông báo
                Toast.makeText(getContext(), "Thông báo đã được tắt", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}