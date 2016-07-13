package net.jspiner.somabob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import net.jspiner.somabob.R;

import butterknife.ButterKnife;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 12.
 */
public class SplashActivity extends AppCompatActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        init();
    }

    void init(){

        ButterKnife.bind(this);
        initFacebookSdk();
    }

    void initFacebookSdk() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG, "login success");
                        Toast.makeText(getBaseContext(), "로그인을 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG, "login cancel");
                        Toast.makeText(getBaseContext(), "로그인을 취소하셨습니다.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG, "login error");
                        Toast.makeText(getBaseContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
