package com.promact.akansh.samplefirebaserestapp;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;
import com.promact.akansh.samplefirebaserestapp.pojo.Users;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
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

    @PUT("Users/{name}.json")
    Call<Users> registerUsers(@Path("name") String name, @Body String body);

    @GET("chats.json")
    Call<ResponseBody> fetchAllContactNames();

    @GET("chats/Akansh-{name2}.json")
    Call<ResponseBody> fetchChatWithUserNames(@Path("name2") String n2);

    @GET("chats/{name1}-{name2}.json")
    Call<ResponseBody> ReceiveChats(@Path("name1") String n1, @Path("name2") String n2);

    @GET("Users.json")
    Call<ResponseBody> fetchAllUsers();
}