package net.jspiner.somabob.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.ProfileTracker;

import net.jspiner.somabob.Activity.SplashActivity;
import net.jspiner.somabob.Model.CountModel;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.lang.ref.WeakReference;
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
 * @project EnerBnB
 * @since 2016. 7. 13.
 */
public class MainAdapter extends BaseAdapter {

    //로그에 쓰일 tag
    public static final String TAG = MainAdapter.class.getSimpleName();

    LayoutInflater inflater;

    private SparseArray<WeakReference<View>> viewArray;

    public ArrayList<ReviewModel> sellerList;
    Context context;

    public MainAdapter(Context context, ArrayList<ReviewModel> sellerList){
        this.context = context;
        this.sellerList = sellerList;
        this.viewArray = new SparseArray<WeakReference<View>>(sellerList.size());
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

            ReviewModel boardObject = sellerList.get(position);

            switch (position) {
                case 0:
                    convertView = inflater.inflate(R.layout.item_seller_header, null);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.item_seller_user, null);
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.item_seller_review, null);
                    break;
                case 3:
                    convertView = inflater.inflate(R.layout.item_seller_proc, null);
                    break;

            }


            final ViewBinder binder = new ViewBinder(convertView, position);

            switch (position){
                case 0:

                    if(Profile.getCurrentProfile() != null) {
                        binder.tvHeaderText.setText(
                                Profile.getCurrentProfile().getName() + "님, 안녕하세요!"
                        );
                    }
                    else{
                        ProfileTracker profileTracker = new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                this.stopTracking();
                                Profile.setCurrentProfile(currentProfile);
                                binder.tvHeaderText.setText(
                                        Profile.getCurrentProfile().getName() + "님, 안녕하세요!"
                                );

                            }
                        };
                        profileTracker.startTracking();
                    }
                    break;

                case 1:
                    Util.getHttpSerivce().user_count(new Callback<CountModel>() {
                        @Override
                        public void success(CountModel response, Response response2) {
                            if(response.result>=5){
                                binder.tvUserText.setText("5명 이상의 연수생들이\n SomaBob을 사용중입니다.");
                            }
                            else if(response.result>=10){
                                binder.tvUserText.setText("10명 이상의 연수생들이\n SomaBob을 사용중입니다.");
                            }
                            else if(response.result>=50){
                                binder.tvUserText.setText("50명 이상의 연수생들이\n SomaBob을 사용중입니다.");
                            }
                            else if(response.result>=100){
                                binder.tvUserText.setText("100명 이상의 연수생들이\n SomaBob을 사용중입니다.");
                            }
                            else if(response.result>=150){
                                binder.tvUserText.setText("150명 이상의 연수생들이\n SomaBob을 사용중입니다.");
                            }
                            else if(response.result>=200){
                                binder.tvUserText.setText("200명 이상의 연수생들이\n SomaBob을 사용중입니다.");
                            }
                            binder.tvUserCount.setText(""+response.result);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "error : " + error.getMessage());
                            Toast.makeText(context, "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            ((Activity)context).finish();
                        }
                    });
                    break;

                case 2:
                    binder.initPager();
                    break;

                case 3:
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SplashActivity.class);
                            context.startActivity(intent);
                        }
                    });
                    break;
            }


        } finally {
            viewArray.put(position, new WeakReference<View>(convertView));
        }
        return convertView;
    }

    class ViewBinder {

        @Nullable
        @Bind(R.id.pager_seller_near)
        ViewPager pager;

        @Nullable
        @Bind(R.id.tv_header_text)
        TextView tvHeaderText;

        @Nullable
        @Bind(R.id.tv_user_usercount)
        TextView tvUserCount;

        @Nullable
        @Bind(R.id.tv_user_usertext)
        TextView tvUserText;


        int position;

        public ViewBinder(View view, final int position){
            this.position = position;
            ButterKnife.bind(this, view);

        }

        void initPager(){
            MainReviewAdapter adapter = new MainReviewAdapter(context);

            pager.setAdapter(adapter);

            pager.setClipToPadding(false);
            pager.setPadding(80, 0, 80, 0);
        }

    }

    @Override
    public int getCount() {
        return sellerList.size();
    }

    @Override
    public Object getItem(int position) {
        return sellerList.get(position);
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
