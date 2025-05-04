package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;

public class AccountSettingFragment extends Fragment {

    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_setting, container, false);
        initView(view);

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void initView(View view) {
        btnBack = view.findViewById(R.id.btn_back);
    }
}
