package com.promact.akansh.samplefirebaserestapp;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("chats.json")
    Call<Chats> registerChat(@Body Chats chatBean);

    @GET("chats.json")
    Call<Chats> fetchChatWithUserNames();
}