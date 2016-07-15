package net.jspiner.somabob.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import net.jspiner.somabob.Model.HttpModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 12.
 */
public class SplashActivity extends Activity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    @Bind(R.id.btn_splash_login)
    LoginButton loginButton;

    ProfileTracker mProfileTracker;

    CallbackManager callbackManager;


    String [] permissions = {
            Manifest.permission.CAMERA,
            "android.permission.INTERNET",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "com.google.android.c2dm.permission.RECEIVE",

    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            if (AccessToken.getCurrentAccessToken() != null) {
                login();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_splash);

        init();
    }

    void init() {

        ButterKnife.bind(this);
        initFacebookSdk();

        if(AccessToken.getCurrentAccessToken()!=null){

            loginButton.setVisibility(View.GONE);

        }

        if (checkPermission()) {
            handler.sendEmptyMessageDelayed(0, 2000);
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    101); // define this constant yourself
        }
    }

    boolean checkPermission(){
        for(String permission : permissions){

            Log.d(TAG,"permission : "+permission);
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    permission);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED)  return false;
        }
        return true;
    }

    public void login(){
        Profile profile = Profile.getCurrentProfile();

        Log.d(TAG,"request login : "+profile.getId() +" name : "+profile.getName());

        Util.getHttpSerivce().login(profile.getId(), profile.getName(), profile.getProfilePictureUri(400,400).toString(),
            new Callback<HttpModel>() {
            @Override
            public void success(HttpModel response, Response response2) {
                if (response.code == 0) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "로그인 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "error : " + error.getMessage());
                Toast.makeText(getBaseContext(), "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "net.jspiner.somabob", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==101){

            handler.sendEmptyMessageDelayed(0, 2000);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void initFacebookSdk() {

        callbackManager = CallbackManager.Factory.create();

        loginButton.setTextLocale(Locale.KOREA);

        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_about_me");
        loginButton.setReadPermissions("user_friends");

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Profile profile = Profile.getCurrentProfile();
                        if (profile == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile3, Profile profile2) {
                                    // profile2 is the new profile
                                    Log.v("facebook - profile2", profile2.getFirstName());
                                    mProfileTracker.stopTracking();
                                }
                            };
                            mProfileTracker.startTracking();
                        } else {
                            profile = Profile.getCurrentProfile();
                            Log.v("facebook - profile", profile.getFirstName());
                        }
                        profile = Profile.getCurrentProfile();

                        Log.d(TAG, "login success");
                        Log.d(TAG, "profile : " + new Gson().toJson(profile));
                        Log.d(TAG, "profile : " + profile.getProfilePictureUri(100, 100).toString());

                        login();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e(TAG, "login cancel");
                        Toast.makeText(getBaseContext(), "로그인을 취소하셨습니다.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        // App code
                        Log.e(TAG, "login error" + error.getMessage());
                        Toast.makeText(getBaseContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        super.onPause();
    }

}
