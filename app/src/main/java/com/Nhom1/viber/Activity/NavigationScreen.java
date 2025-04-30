package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.Nhom1.viber.R;
import com.Nhom1.viber.databinding.NavigationScreenBinding;
import com.Nhom1.viber.utils.ControlUI;
import com.Nhom1.viber.utils.NavigateToFullPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationScreen extends Fragment{
    private final Fragment m0001MainFrg = new M001MainFrg();
    private final Fragment m0002Library1Frg = new M002Libraly1Frg();
    private final Fragment m0003ChartFrg = new M003ChartFrg();
    private final Fragment m0004UserFrg = new M004UserFrg();
    private Fragment activeFragment = m0001MainFrg;
    private NavigationScreenBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NavigationScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction()
                .add(R.id.frame_container, m0004UserFrg, "4").hide(m0004UserFrg)
                .add(R.id.frame_container, m0003ChartFrg, "3").hide(m0003ChartFrg)
                .add(R.id.frame_container, m0002Library1Frg, "2").hide(m0002Library1Frg)
                .add(R.id.frame_container, m0001MainFrg, "1")
                .commit();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_explore) {
                fm.beginTransaction().hide(activeFragment).show(m0001MainFrg).commit();
                activeFragment = m0001MainFrg;
                return true;
            } else if (id == R.id.nav_library) {
                fm.beginTransaction().hide(activeFragment).show(m0002Library1Frg).commit();
                activeFragment = m0002Library1Frg;
                return true;
            } else if (id == R.id.nav_chart) {
                fm.beginTransaction().hide(activeFragment).show(m0003ChartFrg).commit();
                activeFragment = m0003ChartFrg;
                return true;
            } else if (id == R.id.nav_canhan) {
                fm.beginTransaction().hide(activeFragment).show(m0004UserFrg).commit();
                activeFragment = m0004UserFrg;
                return true;
            }
            return false;
        });
    }
}

