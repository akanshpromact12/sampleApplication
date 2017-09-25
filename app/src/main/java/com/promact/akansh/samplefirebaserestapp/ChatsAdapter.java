package com.promact.akansh.samplefirebaserestapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.promact.akansh.samplefirebaserestapp.pojo.Chats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Akansh on 12-09-2017.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private Context context;
    private ArrayList<String> textToSend = new ArrayList<>();
    private String str = "";
    private String date = "";

    public ChatsAdapter(Context context, ArrayList<String> textToSend, String date) {
        this.context = context;
        this.textToSend = textToSend;
        this.date = date;
    }

    @Override
    public int getItemCount() {
        return textToSend.size();
    }

    public String getFirstItemName() {
        if (!textToSend.isEmpty()) {
            str = textToSend.get(0);
        }

        return str;
    }

    @Override
    public ChatsAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.chat_display, null);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatsAdapter.ChatViewHolder holder, int position) {
        Log.d("Inside ChatsAdapter", "/ChatsAdapter");
        String chatText;
        String dt = date.replaceAll(("/"+Calendar.getInstance()
                .get(Calendar.YEAR)), "");

        if (textToSend.size()==0) {
            chatText = textToSend.get(position);
        } else {
            chatText = textToSend.get(holder.getAdapterPosition());
        }
        holder.chat.setText(chatText.trim());
        holder.chatTime.setText(dt.split("-")[position]);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chat;
        TextView chatTime;

        public ChatViewHolder(View itemView) {
            super(itemView);

            chat = (TextView) itemView.findViewById(R.id.textChat);
            chatTime = (TextView) itemView.findViewById(R.id.time_chat);
        }
    }
}
