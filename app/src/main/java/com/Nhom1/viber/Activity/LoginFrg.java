package com.Nhom1.viber.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.Nhom1.viber.R;
import com.Nhom1.viber.services.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFrg extends Fragment implements View.OnClickListener {
    private EditText edtUser, edtPass;
    private Context mContext;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.login_screen, container, false);
        initView(rootView);
        return rootView;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
    private void initView(View v) {
        mAuth = FirebaseAuth.getInstance();
        edtUser = v.findViewById(R.id.edt_user);
        edtPass = v.findViewById(R.id.edt_pass);
        v.findViewById(R.id.btn_login).setOnClickListener(this);
        v.findViewById(R.id.btn_register).setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            gotoMainScreen(); // Chuyển sang màn hình chính
            new Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }, 30000);
        }
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(mContext,
                androidx.appcompat.R.anim.abc_fade_in));
        if (v.getId() == R.id.btn_login) {
            login(edtUser.getText().toString(), edtPass.getText().toString());
        } else if (v.getId() == R.id.btn_register) {
            gotoRegisterScreen();
        }
    }

    private void login(String user, String pass) {
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(mContext, "Empty value", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển sang màn hình chính trước khi đóng Activity
                        gotoMainScreen();

                        // Chờ một chút trước khi gọi finish()
                        new Handler().postDelayed(() -> {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }, 30000);

                    } else {
                        Toast.makeText(mContext, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void gotoRegisterScreen() {
        ((MainActivity) mContext).gotoRegisterScreen();
    }
    private void gotoMainScreen() {
        ((MainActivity) mContext).gotoMainScreen();
    }
}
