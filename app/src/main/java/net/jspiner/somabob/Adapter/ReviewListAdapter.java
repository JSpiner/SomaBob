package net.jspiner.somabob.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.jspiner.somabob.Model.HttpModel;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 14.
 */
public class ReviewListAdapter extends BaseAdapter{


    //로그에 쓰일 tag
    public static final String TAG = ReviewListAdapter.class.getSimpleName();

    LayoutInflater inflater;

    private SparseArray<WeakReference<View>> viewArray;

    public ArrayList<ReviewModel.ReviewObject> reviewList;
    Context context;

    ProgressDialog mProgressDialog;

    public ReviewListAdapter(Context context, ArrayList<ReviewModel.ReviewObject> reviewList){
        this.context = context;
        this.reviewList = reviewList;
        this.viewArray = new SparseArray<WeakReference<View>>(reviewList.size());
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(viewArray != null && viewArray.get(position) != null) {
            convertView = viewArray.get(position).get();
            if(convertView != null)
                return convertView;
        }

        try {

            ReviewModel.ReviewObject reviewModel = reviewList.get(position);

            convertView = inflater.inflate(R.layout.item_review_row, null);

            ViewBinder binder = new ViewBinder(convertView, position);

            String url = context.getString(R.string.API_SERVER) + reviewModel.reviewImage;
            Log.d(TAG, "url : " + url);
            Picasso.with(context)
                    .load(url)
                    .resize(400,180)
                    .centerCrop()
                    .into(binder.imvFile);


            binder.tvName.setText( reviewModel.userName);

            //profile image

            Picasso.with(context)
                    .load(reviewModel.userImage)
                    .resize(200, 200)
                    .centerCrop()
                    .into(binder.imvProfile);


            binder.tvContent.setText(Html.fromHtml(reviewModel.reviewDetail));
            binder.tvTime.setText(reviewModel.writeTime);



            binder.tvComment.setText("댓글 "+reviewModel.commentCount+"명");
            binder.tvLike.setText("좋아요 "+reviewModel.likeCount+"명");
/*
            for(BoardModel.LikeObject like : likeList){
                if(like.boarditemidx == boardObject.idx){
                    binder.tvLike.setTextColor(Color.BLUE);
                }
            }*/


        } finally {
            viewArray.put(position, new WeakReference<View>(convertView));
        }
        return convertView;
    }

    class ViewBinder {

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

        int position;

        public ViewBinder(View view, int position){
            this.position = position;
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void update() {
        viewArray.clear();
        notifyDataSetChanged();
    }
}
