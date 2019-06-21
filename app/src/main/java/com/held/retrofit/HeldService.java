package com.held.retrofit;


import com.held.utils.AppConstants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by YMediaLabs on 04/02/15.
 * Helper class which provides Retrofit\'s RestAdapter .
 */
public class HeldService {

    public static HeldAPI getService() {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        return new RestAdapter
                .Builder()
                .setLogLevel(RestAdapter
                        .LogLevel.FULL)
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(AppConstants.BASE_URL)
                .build()
                .create(HeldAPI.class);
    }
}
