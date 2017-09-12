package com.promact.akansh.samplefirebaserestapp;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Akansh on 09-09-2017.
 */

public class APIClient {

    static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();
        Random random = new Random();
        int num = (random.nextInt(1081) + 2000);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reduxexample-f56e2.firebaseio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
