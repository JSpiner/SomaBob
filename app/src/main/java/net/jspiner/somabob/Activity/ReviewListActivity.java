package net.jspiner.somabob.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.jspiner.somabob.Adapter.ReviewListAdapter;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.util.ArrayList;

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
 * @since 2016. 7. 14.
 */
public class ReviewListActivity extends AppCompatActivity {

    //toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_toolbar_title)
    TextView tvTitle;

    @Bind(R.id.lv_reviewlist)
    ListView lvReview;
    ReviewListAdapter adapter;

    public static final String TAG = ReviewListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewlist);

        init();
    }

    void init(){

        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        ButterKnife.bind(this);

        initToolbar();


        Util.getHttpSerivce().reviews(0, 0, 0, 0,
                new Callback<ReviewModel>() {
                    @Override
                    public void success(ReviewModel response, Response response2) {
                        if(response.code==0){

                            ArrayList<ReviewModel.ReviewObject> ar = new ArrayList();

                            for (ReviewModel.ReviewObject reviewObject : response.result){
                                ar.add(reviewObject);
                            }

                            adapter = new ReviewListAdapter(getBaseContext(), ar);
                            lvReview.setAdapter(adapter);
                        }
                        else{
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

    void loadMore(int page){

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