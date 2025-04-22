package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationScreen extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        // Hiển thị mặc định HomeFragment khi mở NavigationScreen
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new M001MainFrg())
                .commit();

        // Xử lý sự kiện click trên Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_library) {
                selectedFragment = new M002Libraly1Frg();
            } else if (item.getItemId() == R.id.nav_explore) {
                selectedFragment = new M001MainFrg();
            } else if (item.getItemId() == R.id.nav_chart) {
                selectedFragment = new M003ChartFrg();
            }else if (item.getItemId() == R.id.nav_canhan) {
                selectedFragment = new M004UserFrg();
            }

            if (selectedFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}

