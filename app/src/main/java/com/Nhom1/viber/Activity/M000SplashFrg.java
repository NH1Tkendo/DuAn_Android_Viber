package com.Nhom1.viber.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;

public class M000SplashFrg extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViews();
        return inflater.inflate(R.layout.m000_frg_splash, container, false);
    }
    private void initViews() {
        new Handler().postDelayed(() -> {
            // Bắt đầu hiệu ứng fade out
            Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            getView().startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    gotoM001Screen();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }, 2000);
    }
    private void gotoM001Screen() {
        ((MainActivity) getActivity()).gotoLoginScreen();
    }
}
