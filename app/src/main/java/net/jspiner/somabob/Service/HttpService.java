package net.jspiner.somabob.Service;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 12.
 */
public interface HttpService {

    @FormUrlEncoded
    @POST("/test")
    void test(@Field("test") String test,
             Callback<String> ret);

}
