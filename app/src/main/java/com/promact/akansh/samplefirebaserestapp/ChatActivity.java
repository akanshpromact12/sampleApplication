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
import com.promact.akansh.samplefirebaserestapp.pojo.Chats;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    public static String TAG = "ChatActivity";
    public EditText textToSend;
    public FloatingActionButton sendButton;
    public APIInterface apiInterface;
    public RecyclerView chatView;
    public ArrayList<String> chatsList;
    public int number = 0;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiInterface  = APIClient.getClient().create(APIInterface.class);
        textToSend = (EditText) findViewById(R.id.textToSend);
        sendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        chatView = (RecyclerView) findViewById(R.id.chatsRecycler);
        chatsList = new ArrayList<>();
        final String str = getIntent().getStringExtra("contactName");
        Log.d(TAG, "str: " + str);
        mLayoutManager = new LinearLayoutManager(this);
       /* Call<Chats> call = apiInterface.fetchChatWithUserNames("Akansh", "Madhuri");
        call.enqueue(new Callback<Chats>() {
            @Override
            public void onResponse(Call<Chats> call, Response<Chats> response) {
                Log.d(TAG, response.code() + "");
                String displayRespUser = "";

                Chats chats = response.body();

                String userFrom = chats.userFrom;
                String userTo = chats.userTo;
                String Msg = chats.Msg;
                String Time = chats.Time;

                displayRespUser = "UserFromName: " + userFrom +
                        " UserTo: " + userTo + " Msg: " + Msg +
                        " Time: " + Time;

                Log.d(TAG, "UserFromName: " + userFrom +
                        " UserTo: " + userTo + " Msg: " + Msg +
                        " Time: " + Time);
            }

            @Override
            public void onFailure(Call<Chats> call, Throwable t) {
                call.cancel();
            }
        });*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(" " + str);
        getSupportActionBar().setLogo(getDrawable(R.drawable.ic_user1));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chatView.setLayoutManager(mLayoutManager);
        for (int num=1; num<=20; num++) {
            Call<Chats> call = apiInterface.fetchChatWithUserNames(str, num+"");
            call.enqueue(new Callback<Chats>() {
                @Override
                public void onResponse(Call<Chats> call, Response<Chats> response) {
                    if (response.body() != null) {
                        Log.d(TAG, "response code: " + response.code());
                        Chats chats = response.body();

                        ArrayList<String> chatStr = new ArrayList<>();
                        chatStr.add(response.body().Msg);

                        ChatsAdapter chatsAdapter = new ChatsAdapter(ChatActivity.this,
                                chatStr);
                        chatView.setAdapter(chatsAdapter);

                        // chatsAdapter.notifyDataSetChanged();

                        Log.d(TAG, chats.Msg + "\n");
                    }
                }

                @Override
                public void onFailure(Call<Chats> call, Throwable t) {

                }
            });
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("ddMyyyy", Locale.getDefault())
                        .format(new Date());
                if (!textToSend.getText().toString().equalsIgnoreCase("")) {
                    Chats chats = new Chats("Akansh", str,
                            textToSend.getText().toString(), date);
                    chatsList.add(textToSend.getText().toString());
                    ChatsAdapter chatsAdapter = new ChatsAdapter(getApplicationContext(),
                            chatsList);
                    chatView.setLayoutManager(new
                            LinearLayoutManager(getApplicationContext()));
                    chatView.setAdapter(chatsAdapter);
                    textToSend.setText("");

                    Call<Chats> call = apiInterface.registerChat("akansh", str, "1",  chats);

                    call.enqueue(new Callback<Chats>() {
                        @Override
                        public void onResponse(Call<Chats> call, Response<Chats> response) {
                            Log.d(TAG, "response code: " + response.code());
                            String displayRespUser = "";

                            Chats chats = response.body();

                            String userFrom = chats.userFrom;
                            String userTo = chats.userTo;
                            String Msg = chats.Msg;
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
