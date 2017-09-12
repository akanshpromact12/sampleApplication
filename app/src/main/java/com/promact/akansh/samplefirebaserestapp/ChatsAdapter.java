package com.promact.akansh.samplefirebaserestapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Akansh on 12-09-2017.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private Context context;
    private ArrayList<String> textToSend;

    public ChatsAdapter(Context context, ArrayList<String> textToSend) {
        this.context = context;
        this.textToSend = textToSend;
    }

    @Override
    public int getItemCount() {
        return textToSend.size();
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
        String time = new SimpleDateFormat("HH:SS", Locale.getDefault())
                .format(new Date());

        String chatText = textToSend.get(position);
        holder.chat.setText(chatText + "    ");
        holder.chatTime.setText(time);
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
