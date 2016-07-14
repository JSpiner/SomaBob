package net.jspiner.somabob.Service;

import net.jspiner.somabob.Model.CountModel;
import net.jspiner.somabob.Model.HttpModel;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 12.
 */
public interface HttpService {

    @FormUrlEncoded
    @POST("/login.php")
    void login(@Field("userToken") String userToken,
               @Field("userName") String userName,
             Callback<HttpModel> ret);

    @GET("/user_count.php")
    void user_count(Callback<CountModel> ret);

    @Multipart
    @POST("/write_review.php")
    void write_review(@Part("userToken") String userToken,
                      @Part("storeName") String storeName,
                      @Part("reviewPoint") int reviewPoint,
                      @Part("reviewPrice") int reviewPrice,
                      @Part("foodType") int foodType,
                      @Part("reviewDetail") String reviewDetail,
                      @Part("reviewImage") TypedFile reviewImage,
                      Callback<HttpModel> ret);

}
