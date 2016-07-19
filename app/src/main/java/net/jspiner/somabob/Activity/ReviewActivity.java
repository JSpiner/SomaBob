package net.jspiner.somabob.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.jspiner.somabob.Adapter.CommentAdapter;
import net.jspiner.somabob.Model.CommentModel;
import net.jspiner.somabob.Model.HttpModel;
import net.jspiner.somabob.Model.LikeModel;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 17.
 */
public class ReviewActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    public static final String TAG = ReviewActivity.class.getSimpleName();

    //toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_toolbar_title)
    TextView tvTitle;


    @Bind(R.id.item_review)
    public LinearLayout itemArticle;

    @Bind(R.id.imv_review_profile)
    public ImageView imvProfile;

    @Bind(R.id.imv_review_file)
    public ImageView imvFile;

    @Bind(R.id.tv_review_name)
    public TextView tvName;

    @Bind(R.id.tv_review_time)
    public TextView tvTime;

    @Bind(R.id.tv_review_content)
    public TextView tvContent;

    @Bind(R.id.tv_review_like)
    public TextView tvLike;

    @Bind(R.id.tv_review_comment)
    public TextView tvComment;

    @Bind(R.id.lv_review_like)
    LinearLayout lvLike;

    @Bind(R.id.edt_review_comment)
    EditText edtComment;

    @Bind(R.id.lv_comment)
    LinearLayout lvComment;

    @Bind(R.id.tv_review_option)
    TextView tvOption;

    @Bind(R.id.tv_review_title)
    TextView tvStoreTitle;

    ReviewModel.ReviewObject reviewObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        init();
    }


    @Override
    protected void onStart() {
        super.onStart();

        loadComment();
    }

    void loadComment(){

        lvComment.removeAllViews();

        Util.getHttpSerivce().comments(reviewObject.reviewSeqNo,
                new Callback<CommentModel>() {
                    @Override
                    public void success(CommentModel commentModel, Response response) {
                        if (commentModel.code == 0) {

                            LayoutInflater inflater = LayoutInflater.from(getBaseContext());

                            int i = 0;
                            for (CommentModel.CommentObject commentObject : commentModel.result) {
                                View view = inflater.inflate(R.layout.item_comment_row, null);

                                new ViewBinder(view, i).bindData(commentObject);
                                i++;

                                lvComment.addView(view);
                            }

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

    void init() {

        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        ButterKnife.bind(this);

        initToolbar();

        Intent intent = getIntent();
        reviewObject =
                new Gson().fromJson(
                        intent.getStringExtra("data"),
                        ReviewModel.ReviewObject.class);

        bindData(reviewObject);
    }

    void bindData(ReviewModel.ReviewObject reviewModel){


        String url = getString(R.string.API_SERVER) + reviewModel.reviewImage;
        Log.d(TAG, "url : " + url);
        Picasso.with(getBaseContext())
                .load(url)
                .resize(400,180)
                .centerCrop()
                .into(imvFile);


        tvName.setText(reviewModel.userName);

        //profile image

        Picasso.with(getBaseContext())
                .load(reviewModel.userImage)
                .resize(200, 200)
                .centerCrop()
                .into(imvProfile);


        tvContent.setText(reviewModel.reviewDetail);
        tvTime.setText(reviewModel.writeTime);


        tvComment.setText("댓글 "+reviewModel.commentCount+"명");
        tvLike.setText("좋아요 " + reviewModel.likeCount + "명");

        tvStoreTitle.setText(reviewModel.storeName);
        tvOption.setText(
                "가격 : " + getResources().getStringArray(R.array.review_price)[reviewModel.reviewPrice] +"\n"+
                        "평점 : " + getResources().getStringArray(R.array.review_point)[reviewModel.reviewPoint] +"\n"+
                        "종류 : " + getResources().getStringArray(R.array.food_type)[reviewModel.reviewType] +"\n"
        );


        if(reviewModel.isLike){
            lvLike.setBackgroundColor(Color.argb(50, 0, 0, 200));
        }
        else{
            lvLike.setBackgroundColor(Color.TRANSPARENT);
        }
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

    @OnClick(R.id.lv_review_like)
    void onLikeClick(){
        Util.getHttpSerivce().like(Profile.getCurrentProfile().getId(), reviewObject.reviewSeqNo,
                new Callback<LikeModel>() {
                    @Override
                    public void success(LikeModel httpModel, Response response) {
                        if (httpModel.code == 0) {
                            reviewObject.likeCount += httpModel.type==1?1:-1;
                            reviewObject.isLike = httpModel.type==1;


                            tvLike.setText("좋아요 " + reviewObject.likeCount + "명");


                            if(reviewObject.isLike){
                                lvLike.setBackgroundColor(Color.argb(50, 0, 0, 200));
                            }
                            else{
                                lvLike.setBackgroundColor(Color.TRANSPARENT);
                            }
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

    @OnClick(R.id.btn_review_send)
    void onSendClick(){
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
        Util.getHttpSerivce().write_comment(
                Profile.getCurrentProfile().getId(),
                reviewObject.reviewSeqNo,
                edtComment.getText().toString(),
                new Callback<HttpModel>() {
                    @Override
                    public void success(HttpModel httpModel, Response response) {
                        if(httpModel.code == 0){
                            Toast.makeText(getBaseContext(), "댓글을 작성했습니다.", Toast.LENGTH_LONG).show();
                            edtComment.setText("");
                            loadComment();
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

    @Override
    protected void onPause() {
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        super.onPause();
    }


    class ViewBinder {

        @Bind(R.id.tv_comment_name)
        TextView tvName;
        @Bind(R.id.tv_comment_text)
        TextView tvText;
        @Bind(R.id.tv_comment_time)
        TextView tvTime;
        @Bind(R.id.imv_comment_profile)
        ImageView imvProfile;


        int position;

        public ViewBinder(View view, int position){
            this.position = position;
            ButterKnife.bind(this, view);

        }

        void bindData(CommentModel.CommentObject commentObject){

            tvName.setText(commentObject.userName);
            tvText.setText( Html.fromHtml(commentObject.commentText));
            tvTime.setText(commentObject.writeTime);

            Picasso.with(getBaseContext())
                    .load(commentObject.userImage)
                    .resize(200, 200)
                    .centerCrop()
                    .into(imvProfile);
        }

    }

}
