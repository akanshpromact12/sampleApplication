package com.promact.akansh.samplefirebaserestapp;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIInterface {
    @PUT("chats/{name1}-{name2}/{number}.json")
    Call<Chats> registerChat(@Path("name1") String n1, @Path("name2") String n2,
                             @Path("number") String number, @Body Chats chatBean);

    @GET("chats/akansh-{name2}/{number}.json")
    Call<Chats> fetchChatWithUserNames(@Path("name2") String n2,
                                       @Path("number") String number);
}