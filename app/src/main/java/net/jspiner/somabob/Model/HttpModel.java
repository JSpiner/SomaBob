package net.jspiner.somabob.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 14.
 */
public class HttpModel {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

}
