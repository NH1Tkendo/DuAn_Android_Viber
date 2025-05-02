package com.Nhom1.viber.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;
import com.Nhom1.viber.utils.ControlUI;
import com.Nhom1.viber.utils.NavigateToFullPlayer;


public class MainActivity extends AppCompatActivity implements ControlUI,NavigateToFullPlayer {
    public static final String SAVE_PREF = "save_pref";

    @Override
    public void showPlayerUI() {
        findViewById(R.id.playerBarContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePlayerUI() {
        View playerBar = findViewById(R.id.playerBarContainer);
        View navBar = findViewById(R.id.bottom_navigation);

        Log.d("DEBUG_UI", "Hiding UI...");
        if (playerBar != null) {
            playerBar.setVisibility(View.GONE);
            Log.d("DEBUG_UI", "playerBar hidden");
        } else {
            Log.d("DEBUG_UI", "playerBar is null");
        }

        if (navBar != null) {
            navBar.setVisibility(View.GONE);
            Log.d("DEBUG_UI", "bottom_navigation hidden");
        } else {
            Log.d("DEBUG_UI", "bottom_navigation is null");
        }
    }


    @Override
    public void openFullPlayer() {
        hidePlayerUI();
        FullPlayerFragment fullPlayerFragment = new FullPlayerFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down)
                .replace(R.id.frame_container, fullPlayerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openMusicQueue() {
        showPlayerUI();
        hidePlayerUI();
        MusicQueueFrg musicQueueFrg = new MusicQueueFrg();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down)
                .replace(R.id.frame_container, musicQueueFrg)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFrg(new M000SplashFrg());
    }
    public void gotoRegisterScreen() {
        getSupportFragmentManager().beginTransaction().replace(R.id.ln_main, new
                RegisterFrg()).commit();
    }
    public void gotoLoginScreen() {
        getSupportFragmentManager().beginTransaction().replace(R.id.ln_main, new
                LoginFrg()).commit();
    }

    private void showFrg(Fragment frg) {
        getSupportFragmentManager().beginTransaction().replace(R.id.ln_main, frg,
                null).commit();
    }
    public void gotoMainScreen() {
        getSupportFragmentManager().beginTransaction().replace(R.id.ln_main, new
                NavigationScreen(), null).commit();
    }
}
