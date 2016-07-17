package net.jspiner.somabob.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.jspiner.somabob.Adapter.ReviewListAdapter;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnItemSelected;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 14.
 */
public class ReviewListActivity extends AppCompatActivity {

    public static final String TAG = ReviewListActivity.class.getSimpleName();

    //toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_toolbar_title)
    TextView tvTitle;

    @Bind(R.id.lv_reviewlist)
    ListView lvReview;

    ReviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewlist);

        init();
    }

    @Override
    protected void onStart() {
        adapter.reviewList.clear();
        loadMore(0);
        super.onStart();
    }

    @OnItemClick(R.id.lv_reviewlist)
    void onListItemClick(int position){
        Log.d(TAG, " onItemClick : " + position);
        Intent intent = new Intent(ReviewListActivity.this, ReviewActivity.class);
        intent.putExtra("data", new Gson().toJson(adapter.getItem(position)));
        startActivity(intent);
    }


    void init(){

        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        ButterKnife.bind(this);

        initToolbar();

        adapter = new ReviewListAdapter(getBaseContext(), new ArrayList<ReviewModel.ReviewObject>());
        lvReview.setAdapter(adapter);

    }

    void loadMore(int page){
        Util.getHttpSerivce().reviews(page, 0, 0, 0,
                new Callback<ReviewModel>() {
                    @Override
                    public void success(ReviewModel response, Response response2) {
                        if (response.code == 0) {


                            for (ReviewModel.ReviewObject reviewObject : response.result) {
                                adapter.reviewList.add(reviewObject);
                            }
                            adapter.update();

                        } else {
                            Toast.makeText(getBaseContext(), "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
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


    void initToolbar(){

        tvTitle.setText("리뷰보기");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onPause() {
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        super.onPause();
    }
}
