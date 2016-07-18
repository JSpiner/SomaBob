package net.jspiner.somabob.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.jspiner.somabob.Activity.ReviewActivity;
import net.jspiner.somabob.Activity.ReviewListActivity;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 7. 13.
 */
public class MainReviewAdapter extends PagerAdapter {

    //로그에 쓰일 tag
    public static final String TAG = MainReviewAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    Context context;

    ReviewModel.ReviewObject[] reviewObjects;

    SparseArray<View> views = new SparseArray<View>();

    public MainReviewAdapter(final Context c){
        super();
        this.context = c;
        mInflater = LayoutInflater.from(c);

        Util.getHttpSerivce().load_top_review(Profile.getCurrentProfile().getId(),
                new Callback<ReviewModel>() {
                    @Override
                    public void success(ReviewModel reviewModel, Response response) {
                        if(reviewModel.code == 0){
                            reviewObjects = new ReviewModel.ReviewObject[reviewModel.result.size()];

                            int i=0;
                            for(ReviewModel.ReviewObject reviewObject : reviewModel.result){
                                reviewObjects[i] = reviewObject;
                                i++;
                            }

                            update();

                        }
                        else{
                            Toast.makeText(c, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            ((Activity)c).finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.e(TAG, "error : " + error.getMessage());
                        Toast.makeText(c, "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                        ((Activity)c).finish();
                    }
                }
        );
    }

    @Override
    public int getCount() {

        if(reviewObjects == null) {
            return 4;
        }
        else{
            return reviewObjects.length;
        }
    }

    @Override
    public Object instantiateItem(View pager, int position) {
        View v = null;

        v = mInflater.inflate(R.layout.item_seller_image_row, null);

        ViewBinder binder = new ViewBinder(v, position);

        ((ViewPager)pager).addView(v, 0);
        views.put(position, v);

        return v;
    }

    @Override
    public void destroyItem(View pager, int position, Object o) {

        View view = (View)o;
        ((ViewPager) pager).removeView(view);
        views.remove(position);
        view = null;
    }

    @Override
    public boolean isViewFromObject(View pager, Object obj) {
        return pager == obj;
    }

    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    @Override public Parcelable saveState() { return null; }
    @Override public void startUpdate(View arg0) {}
    @Override public void finishUpdate(View arg0) {}

    public class ViewBinder{

        @Bind(R.id.imv_seller_image_row)
        ImageView imvRow;

        @Bind(R.id.tv_review_date)
        TextView tvDate;

        @Bind(R.id.tv_review_title)
        TextView tvTitle;

        @Bind(R.id.tv_review_content)
        TextView tvContent;



        int position;


        public ViewBinder(View view, final int position){
            ButterKnife.bind(this, view);

            this.position = position;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReviewListActivity.class);
                    context.startActivity(intent);

                    Intent intent2 = new Intent(context, ReviewActivity.class);
                    intent2.putExtra("data", new Gson().toJson(reviewObjects[position]));
                    context.startActivity(intent2);


                }
            });

            init();
        }

        void init(){

            Log.d(TAG,"init image");

            Picasso.with(context)
                    .load(R.drawable.sample)
                    .fit()
                    .into(imvRow);
            if(reviewObjects == null) return;
            if(reviewObjects.length <= position) return;

            Log.d(TAG,"position : "+position);
            Log.d(TAG,"reviewObjects : "+new Gson().toJson(reviewObjects));
            ReviewModel.ReviewObject reviewObject = reviewObjects[position];


            String url = context.getString(R.string.API_SERVER) + reviewObject.reviewImage;
            Log.d(TAG, "url : " + url);
            Picasso.with(context)
                    .load(url)
                    .fit()
                    .into(imvRow);


            tvContent.setText(reviewObject.reviewDetail);
            tvDate.setText(reviewObject.writeTime.substring(0,10));
            tvTitle.setText(reviewObject.storeName);

        }
    }


    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        int key = 0;
        for(int i = 0; i < views.size(); i++) {
            key = views.keyAt(i);
            View view = views.get(key);
            new ViewBinder(view, i);
        }
        super.notifyDataSetChanged();
    }
}
