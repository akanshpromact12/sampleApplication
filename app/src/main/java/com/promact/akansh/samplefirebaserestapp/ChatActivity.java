package com.promact.akansh.samplefirebaserestapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.promact.akansh.samplefirebaserestapp.pojo.Chats;
import com.promact.akansh.samplefirebaserestapp.pojo.ChatsRealm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    public static String TAG = "ChatActivity";
    public EditText textToSend;
    public FloatingActionButton sendButton;
    public APIInterface apiInterface;
    public RecyclerView chatView;
    public int number = 0;
    private RecyclerView.LayoutManager mLayoutManager;
    public static int nm = 0;
    public static int n = 0;
    Random random = new Random();
    public ChatsAdapter chatsAdapter;
    private ArrayList<String> chatStr;
    public String name;
    public ChatsRealm chatsRealm;
    public Middleware middleware;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        textToSend = (EditText) findViewById(R.id.textToSend);
        sendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        chatView = (RecyclerView) findViewById(R.id.chatsRecycler);
        chatsRealm = new ChatsRealm();
        middleware = new Middleware();
        final String str = getIntent().getStringExtra("contactName");
        Log.d(TAG, "str: " + str);
        mLayoutManager = new LinearLayoutManager(this);
        if (SaveSharedPrefs.getName(ChatActivity.this).length()==0) {
            name = getIntent().getStringExtra("name");
        } else {
            name = SaveSharedPrefs.getName(ChatActivity.this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(" " + str);
        getSupportActionBar().setLogo(getDrawable(R.drawable.ic_user1));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chatView.setLayoutManager(mLayoutManager);
        chatStr = new ArrayList<>();
        fetchAllPrevMsg(str);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("ddMyyyy", Locale.getDefault())
                        .format(new Date());
                if (!textToSend.getText().toString().equalsIgnoreCase("")) {
                    int num = (random.nextInt(1081) + 2000);

                    Chats chats = new Chats(name, str,
                            name + ": " + textToSend.getText().toString(), date);
                    chatsRealm.setUserFrom(name);
                    chatsRealm.setUserTo(str);
                    chatsRealm.setMsg(textToSend.getText().toString());
                    chatsRealm.setTime(date);
                    chatsRealm.setNetAvailable(true);
                    middleware.addChats(chatsRealm);

                    chatStr.add(textToSend.getText().toString());

                    if (chatView.getAdapter() == null) {
                        chatsAdapter = new ChatsAdapter(ChatActivity.this,
                                chatStr);

                        chatView.setAdapter(chatsAdapter);
                    } else {
                        chatsAdapter.notifyDataSetChanged();
                    }

                    textToSend.setText("");

                    Call<Chats> call = null;
                    if (!chatsAdapter.getFirstItemName().equals("")) {
                        if (chatsAdapter.getFirstItemName().split(":")[0].equals(str)) {
                            call = apiInterface.registerChat(str, name, num+"",
                                    chats);
                        } else {
                            call = apiInterface.registerChat(name, str, num+"",
                                    chats);
                        }
                    }

                    if (call != null) {
                        call.enqueue(new Callback<Chats>() {
                            @Override
                            public void onResponse(Call<Chats> call, Response<Chats> response) {
                                Log.d(TAG, "response code: " + response.code());
                                String displayRespUser = "";

                                Chats chats = response.body();

                                String userFrom = chats.userFrom;
                                String userTo = chats.userTo;
                                String Msg = name + ": " + chats.Msg;
                                String Time = chats.Time;

                                displayRespUser = "UserFromName: " + userFrom +
                                        " UserTo: " + userTo + " Msg: " + Msg +
                                        " Time: " + Time;

                                Log.d(TAG, "UserFromName: " + userFrom + " UserTo: " +
                                        userTo + " Msg: " + Msg + " Time: " + Time);

                                Log.d(TAG, "Data successfully uploaded");
                            }

                            @Override
                            public void onFailure(Call<Chats> call, Throwable t) {
                                call.cancel();
                            }
                        });
                    }
                }
            }
        });
    }

    private void fetchAllPrevMsg(final String str) {
        //str-name combination
        Call<ResponseBody> call = apiInterface.ReceiveChats(str, name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "receive response code: " + response.code());

                try {
                    if (response.body().contentLength() > 4) {
                        JSONObject jsonObject = new JSONObject(response.body().string());/*  (new JsonParser()).parse(response.body().string()).getAsJsonObject();*/
                        JSONArray jsonArray = jsonObject.names();

                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject(jsonArray.get(i).toString());
                            String msg = jsonObject1.getString("Msg");
                            String userTo = jsonObject1.getString("userTo");

                            if (msg.split(":")[0].equals(name)) {
                                chatStr.add("You: " + msg.split(":")[1]);
                            } else {
                                chatStr.add(msg);
                            }
                        }

                        chatsAdapter = new ChatsAdapter(ChatActivity.this,
                                chatStr);
                        //chatsAdapter.notifyDataSetChanged();
                        chatView.setAdapter(chatsAdapter);
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error occured", t);
            }
        });

        //name-str combination
        Call<ResponseBody> call1 = apiInterface.ReceiveChats(name, str);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response1) {
                Log.d(TAG, "receive response code: " + response1.code());

                try {
                    if (response1.body().contentLength() > 4) {
                        JSONObject jsonObject = new JSONObject(response1.body().string());/*  (new JsonParser()).parse(response.body().string()).getAsJsonObject();*/
                        JSONArray jsonArray = jsonObject.names();

                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject(jsonArray.get(i).toString());
                            String msg = jsonObject1.getString("Msg");
                            String userTo = jsonObject1.getString("userTo");

                            if (msg.split(":")[0].equals(name)) {
                                chatStr.add("You: " + msg.split(":")[1]);
                            } else {
                                chatStr.add(msg);
                            }
                        }

                        chatsAdapter = new ChatsAdapter(ChatActivity.this,
                                chatStr);
                        //chatsAdapter.notifyDataSetChanged();
                        chatView.setAdapter(chatsAdapter);
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error occured", t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}