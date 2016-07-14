package net.jspiner.somabob.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import net.jspiner.somabob.Model.HttpModel;
import net.jspiner.somabob.R;
import net.jspiner.somabob.Util;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project TraTalk
 * @since 2016. 7. 14.
 */
public class WriteActivity extends AppCompatActivity {

    //toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_toolbar_title)
    TextView tvTitle;
    @Bind(R.id.btn_toolbar_write)
    Button btnWrite;

    @Bind(R.id.imv_write_img)
    ImageView imvImg;
    @Bind(R.id.edt_write_text)
    EditText edtText;
    @Bind(R.id.edt_write_title)
    EditText edtTitle;

    @Bind(R.id.spinner_food_type)
    Spinner spinnerFoodType;
    @Bind(R.id.spinner_review_point)
    Spinner spinnerReviewPoint;
    @Bind(R.id.spinner_review_price)
    Spinner spinnerReviewPrice;


    File selectedImage;

    ProgressDialog mProgressDialog;

    //로그에 쓰일 tag
    public static final String TAG = WriteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        init();
    }

    void init(){

        Intent intent = getIntent();

        ButterKnife.bind(this);

        initToolbar();
    }

    void initToolbar(){

        tvTitle.setText("리뷰쓰기");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnWrite.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.food_type, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodType.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.review_point, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReviewPoint.setAdapter( adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.review_price, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReviewPrice.setAdapter( adapter3);

    }


    @OnClick(R.id.ib_write_image)
    void onImageClick(){
        Intent intent = new Intent(getBaseContext(), ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.green)
                .setSelectionLimit(1)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, 11);
    }

    @OnClick(R.id.btn_toolbar_write)
    void onWriteClick(){

        mProgressDialog = ProgressDialog.show(WriteActivity.this,"",
                "글을 작성하는 중입니다.",true);
        mProgressDialog.show();


        Util.getHttpSerivce().write_review(
                Profile.getCurrentProfile().getId(),
                edtTitle.getText().toString(),
                spinnerReviewPoint.getSelectedItemPosition(),
                spinnerReviewPrice.getSelectedItemPosition(),
                spinnerFoodType.getSelectedItemPosition()
                edtText.getText().toString(),
                Util.resizeImage(selectedImage) == null ? null : new TypedFile("image/jpeg", selectedImage),
                new Callback<HttpModel>() {
                    @Override
                    public void success(HttpModel httpModel, Response response) {
                        mProgressDialog.dismiss();
                        if (httpModel.code == 1) {
                            Toast.makeText(getBaseContext(), "성공적으로 작성하셨습니다 ", Toast.LENGTH_LONG).show();

                            finish();

                        } else {
                            Toast.makeText(getBaseContext(),
                                    "알 수 없는 에러가 발생하였습니다."
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "network calc error : " + httpModel.message);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getBaseContext(),
                                "네트워크 에러가 발생하였습니다.",
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "network error : " + error.getMessage());


                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 11) { // Icon
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.i(TAG, " uri: " + uri);
                        selectedImage = new File(uri.getPath());
                        Picasso.with(getBaseContext()).load(selectedImage).into(imvImg);
                        imvImg.setVisibility(View.VISIBLE);

                    }
                }
            }
        }
    }

    /*
    @Override
    protected void onResume() {
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        super.onPause();
    }
    *
    */
}
