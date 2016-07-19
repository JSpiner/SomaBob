package net.jspiner.somabob.Service;

import net.jspiner.somabob.Model.CommentModel;
import net.jspiner.somabob.Model.CountModel;
import net.jspiner.somabob.Model.HttpModel;
import net.jspiner.somabob.Model.LikeModel;
import net.jspiner.somabob.Model.ReviewModel;

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
               @Field("userImage") String userImage,
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

    @FormUrlEncoded
    @POST("/reviews.php")
    void reviews(@Field("userToken") String userToken,
                 @Field("page") int page,
                 @Field("optionPrice") int optionPrice,
                 @Field("optionPoint") int optionPoint,
                 @Field("optionType") int optionType,
                 Callback<ReviewModel> ret);

    @FormUrlEncoded
    @POST("/write_comment.php")
    void write_comment(@Field("userToken") String userToken,
                       @Field("reviewSeqNo") int reviewSeqNo,
                       @Field("commentText") String commentText,
                       Callback<HttpModel> ret);

    @FormUrlEncoded
    @POST("/comments.php")
    void comments(@Field("reviewSeqNo") int reviewSeqNo,
                  Callback<CommentModel> ret);

    @FormUrlEncoded
    @POST("/load_top_review.php")
    void load_top_review(@Field("userToken") String userToken,
            Callback<ReviewModel> ret);

    @FormUrlEncoded
    @POST("/like.php")
    void like(@Field("userToken") String userToken,
              @Field("reviewSeqNo") int reviewSeqNo,
              Callback<LikeModel> ret);


    @FormUrlEncoded
    @POST("/pushtoken.php")
    void pushtoken(@Field("userToken") String userToken,
                   @Field("pushToken") String pushToken,
                   Callback<HttpModel> ret);

}
