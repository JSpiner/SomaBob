package net.jspiner.somabob.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 7. 13.
 */
public class ReviewModel extends HttpModel {

    @SerializedName("result")
    public List<ReviewObject> result;

    public class ReviewObject{

        @SerializedName("reviewSeqNo")
        public int reviewSeqNo;

        @SerializedName("storeName")
        public String storeName;

        @SerializedName("userToken")
        public String userToken;

        @SerializedName("userName")
        public String userName;

        @SerializedName("userImage")
        public String userImage;

        @SerializedName("reviewPoint")
        public int reviewPoint;

        @SerializedName("reviewPrice")
        public int reviewPrice;

        @SerializedName("reviewType")
        public int reviewType;

        @SerializedName("reviewDetail")
        public String reviewDetail;

        @SerializedName("reviewImage")
        public String reviewImage;

        @SerializedName("likeCount")
        public int likeCount;

        @SerializedName("commentCount")
        public int commentCount;

        @SerializedName("writeTime")
        public String writeTime;


    }
}
