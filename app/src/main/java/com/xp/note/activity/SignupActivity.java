package com.xp.note.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xp.note.R;
import com.xp.note.model.User;
import com.xp.note.utils.SharedPreferencesUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/*
 *  用户注册页面
 *  该页面注释的两个参数是用于获取验证码并输入的 无需删除
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";

    EditText emailText;
//    Button codeButton;
//    EditText codeText;
    EditText passwordText;
    EditText reEnterPasswordText;
    Button signupButton;
    TextView loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferencesUtil.init(this);

        setContentView(R.layout.activity_signup);

        emailText = findViewById(R.id.input_email);
//       codeButton = findViewById(R.id.send_code);
//        codeText = findViewById(R.id.input_code);
        passwordText = findViewById(R.id.input_password);
        reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);

//        codeButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        loginLink.setOnClickListener(this);


    }



    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.send_code:
//                final String email = emailText.getText().toString();
//                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    emailText.setError("您输入的邮箱地址有误");
//                } else {
//                    emailText.setError(null);
//                    BmobUser.requestEmailVerify(email, new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e == null) {
//                                Toast.makeText(SignupActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
//                                Snackbar.make(signupButton, "请求验证邮件成功，请到" + email + "邮箱中进行激活账户。", Snackbar.LENGTH_LONG).show();
//                            } else {
//                                Log.e("BMOB", e.toString());
//                                Snackbar.make(signupButton, e.getMessage(), Snackbar.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//
//                    CountDownTimer timer = new CountDownTimer(30000,1000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
////                            codeButton.setEnabled(false);
////                            codeButton.setText("已发送(" + millisUntilFinished / 1000 + ")");
//                        }
//
//                        @Override
//                        public void onFinish() {
////                            codeButton.setEnabled(true);
////                            codeButton.setText("重新获取验证码");
//                        }
//                    }.start();
//                }
//
//                break;
            case R.id.btn_signup:
                signup();
                break;
            case R.id.link_login:
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在注册账号...");
        progressDialog.show();

        final String email = emailText.getText().toString();
//        String code = codeText.getText().toString();
        final String password = passwordText.getText().toString();

        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    //发送邮箱验证
//                    BmobUser.requestEmailVerify(email, new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            onSignupSuccess();
//                            SharedPreferencesUtil.setUsername(email);
//                            SharedPreferencesUtil.setPassword(password);
//                            progressDialog.dismiss();
//                            Toast.makeText(SignupActivity.this, "注册成功，请到" + email + "邮箱中进行激活账户完成注册最后一步", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(loginLink, "注册成功", Snackbar.LENGTH_LONG).show();
//                        }
//                    });

                    onSignupSuccess();
//                    SharedPreferencesUtil.setUsername(email);
//                    SharedPreferencesUtil.setPassword(password);
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "注册成功，请到" + email + "邮箱中进行激活账户完成注册最后一步" +
                            "(如未收到邮件请把1002648595@qq.com加入白名单再验证邮箱)", Toast.LENGTH_LONG).show();

                } else {
                    progressDialog.dismiss();
                    onSignupFailed();
                    Toast.makeText(getBaseContext(), "注册失败," + "请稍后重试", Toast.LENGTH_LONG).show();
                }
            }
        });




    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);
//        SharedPreferencesUtil.setIsLogin(true);
        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "注册失败,请稍后重试", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
//        String code = codeText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("您输入的邮箱地址有误");
            valid = false;
        } else {
            emailText.setError(null);
        }

//        if (code.isEmpty()) {
//            codeText.setError("验证码不正确");
//            valid = false;
//        } else {
//            codeText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 25) {
            passwordText.setError("密码在6到25位之间");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("两次输入的密码不一致");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }




}