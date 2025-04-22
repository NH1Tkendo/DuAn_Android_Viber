package com.Nhom1.viber.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;

public class MainActivity extends AppCompatActivity {
    public static final String SAVE_PREF = "save_pref";
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
