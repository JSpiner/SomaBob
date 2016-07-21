package net.jspiner.somabob.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.nirhart.parallaxscroll.views.ParallaxListView;
import com.squareup.picasso.Picasso;

import net.jspiner.somabob.Adapter.MainAdapter;
import net.jspiner.somabob.Model.HttpModel;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Service.GCMRegister;
import net.jspiner.somabob.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 13.
 */
public class MainActivity extends AppCompatActivity {


    //로그에 쓰일 tag
    public static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.bg_loading)
    LinearLayout bgLoading;

    private ActionBarDrawerToggle toggle;
    boolean loadingMore;

    @Bind(R.id.lv_main_list)
    ParallaxListView lvList;

    @Bind(R.id.profile_image)
    ImageView profileImage;
    @Bind(R.id.tv_drawer_name)
    TextView tvName;

    MainAdapter adapter;

    @Bind(R.id.fab_main_add)
    FloatingActionButton btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Util.getHttpSerivce().pushtoken(Profile.getCurrentProfile().getId(),
                GCMRegister.push_token,
                new Callback<HttpModel>() {
                    @Override
                    public void success(HttpModel httpModel, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    @OnClick(R.id.fab_main_add)
    void onAddClick(){
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivity(intent);
    }

    void init(){
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        ButterKnife.bind(this);
        bgLoading.setVisibility(View.VISIBLE);
        delayHandler.sendEmptyMessageDelayed(0, 2000);
    }

    Handler delayHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            bgLoading.setVisibility(View.GONE);
            initToolbar();
            initLayout();
        }
    };

    /*    @OnClick(R.id.btn_next)
        void onNextClick(){
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(intent);
        }
    */
    void initLayout(){

        initUpdateFrame();
        initParallaxScroll();

        Log.d(TAG, "user id : " + Profile.getCurrentProfile().getId());
        if(Profile.getCurrentProfile() == null){
            ProfileTracker profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    this.stopTracking();
                    Log.d(TAG,"currentProfile");
                    Profile.setCurrentProfile(currentProfile);

                    Picasso.with(getBaseContext())
                            .load(currentProfile.getProfilePictureUri(400, 400).toString())
                            .fit()
                            .into(profileImage);
                    tvName.setText(Profile.getCurrentProfile().getName());
                }
            };
            profileTracker.startTracking();
        }
        else {
            Picasso.with(getBaseContext())
                    .load(Profile.getCurrentProfile().getProfilePictureUri(400, 400).toString())
                    .fit()
                    .into(profileImage);
            tvName.setText(Profile.getCurrentProfile().getName());
        }
    }

    void initUpdateFrame(){
        PtrClassicFrameLayout mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                updateData();
//                reloadData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                Log.d(TAG, "refresh checkCanDoRefresh");
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);

        final MaterialHeader header = new MaterialHeader(MainActivity.this);
        int[] colors = {Color.BLUE,Color.RED,Color.GRAY,Color.CYAN};
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 25, 0, 25);
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
    }

    void initParallaxScroll(){

        View header = LayoutInflater.from(this).inflate(R.layout.item_main_header,null);
        ArrayList<ReviewModel> arrayList = new ArrayList<>();
        for(int i=0;i<4;i++){
            arrayList.add(new ReviewModel());
        }
        adapter = new MainAdapter(MainActivity.this,arrayList );

        ViewBinder binder = new ViewBinder(header);

        lvList.addParallaxedHeaderView(header);
        lvList.setAdapter(adapter);

        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (totalItemCount <= 1) return;
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                }
            }
        });
    }

    void initToolbar(){


        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();

    }

    @OnClick(R.id.lv_drawer_lv1)
    void onDrawerMenu1Click(){
        LoginManager.getInstance().logOut();
        finish();
    }

    public class ViewBinder{

        @Bind(R.id.imv_header_row)
        ImageView imvRow;

        public ViewBinder(View view){
            ButterKnife.bind(this,view);

            init();
        }

        void init(){

            Picasso.with(getBaseContext())
                    .load(R.drawable.background)
                    .fit()
                    .into(imvRow);
        }
    }

    @Override
    protected void onPause() {
        this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        super.onPause();
    }
}
