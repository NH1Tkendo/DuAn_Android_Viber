package com.Nhom1.viber.Activity;

import android.content.Context;
import android.os.Bundle;
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

public class RegisterFrg  extends Fragment implements View.OnClickListener{
    private EditText edtEmail, edtUser,edtPass, edtRepass;
    private Context mContext;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_screen, container, false);
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
        edtEmail = v.findViewById(R.id.edt_email);
        edtPass = v.findViewById(R.id.edt_pass);
        edtRepass = v.findViewById(R.id.edt_re_pass);
        edtUser = v.findViewById(R.id.edt_user);
        v.findViewById(R.id.tv_register).setOnClickListener(this);
        v.findViewById(R.id.iv_back).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(mContext,
                androidx.appcompat.R.anim.abc_fade_in));
        if (v.getId() == R.id.iv_back) {
            gotoLoginScreen();
        } else if (v.getId() == R.id.tv_register) {
            register(edtEmail.getText().toString(), edtUser.getText().toString(), edtPass.getText().toString(), edtRepass.getText().toString());
        }
    }
    private void gotoLoginScreen() {
        ((MainActivity) mContext).gotoLoginScreen();
    }

    private void register(String mail, String user, String pass, String repass) {
        if (mail.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
            Toast.makeText(mContext, "Empty value", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(repass)) {
            Toast.makeText(mContext, "Password is not match", Toast.LENGTH_SHORT).show();
        }
        try {
            //Tao tai khoan trong firebase database
            FirebaseService service = new FirebaseService();
            service.registerUser(user, mail);

            //Tao tai khoan trong firebase auth
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            Toast.makeText(mContext, "Register account successfully!", Toast.LENGTH_SHORT).show();
            gotoLoginScreen();
        }catch (Exception ex){
            Log.d("Loi:", "Loi dang ki: ", ex);
        }

    }
}
