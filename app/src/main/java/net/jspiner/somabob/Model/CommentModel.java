package net.jspiner.somabob.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 17.
 */
public class CommentModel extends HttpModel {

    @SerializedName("result")
    public List<CommentObject> result;

    public class CommentObject {

        @SerializedName("commentSeqNo")
        public int commentSeqNo;

        @SerializedName("reviewSeqNo")
        public int reviewSeqNo;

        @SerializedName("commentText")
        public String commentText;

        @SerializedName("userName")
        public String userName;

        @SerializedName("userImage")
        public String userImage;

        @SerializedName("writeTime")
        public String writeTime;

    }

}
