package com.promact.akansh.samplefirebaserestapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.promact.akansh.samplefirebaserestapp.pojo.ContactsBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<String> contacts;
    private Context context;
    private String name;
    private APIInterface apiInterface;
    private static final String TAG = "ChatMainActivity";
    public static int count = 0;
    private String str;
    private ArrayList<String> time;
    private ArrayList<String> chats;

    public ContactsAdapter(List<String> contacts, Context context, String name,
                           String str, ArrayList<String> time,
                           ArrayList<String> chats) {
        this.contacts = contacts;
        this.context = context;
        this.name = name;
        this.str = str;
        this.time = time;
        this.chats = chats;
    }

    @Override
    public int getItemCount() { return contacts.size(); }

    @Override
    public ContactsAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_contact, null);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactsAdapter.ContactViewHolder holder, int position) {
        final String contactName = contacts.get(position);
        String timing = "";
        Log.d(TAG, "time adapter: " + time);

        Log.d("ChatsContactNames", "contacts: " + str);
        holder.contactName.setText(contactName);
        holder.contactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("contactName", holder.contactName.getText());
                intent.putExtra("name", name);

                context.startActivity(intent);
            }
        });

        for (int i=0; i<time.size(); i++) {
            String t1 = time.get(i).split("~")[0].split("-")[0];
            String t2 = time.get(i).split("~")[0].split("-")[1];

            Log.d(TAG, "contractName: " + contactName + " t1: " + t1);
            Log.d(TAG, "contractName: " + contactName.equals(t1));

            if (contactName.equals(t1) || contactName.equals(t2)) {
                Log.d(TAG, "same name" + time.get(i).split("~")[1]
                        .replaceAll(("/"+ Calendar.getInstance()
                                .get(Calendar.YEAR)), ""));
                timing = time.get(i).split("~")[1]
                        .replaceAll(("/"+ Calendar.getInstance()
                                .get(Calendar.YEAR)), "");
            }
        }
        Log.d(TAG, "timing: " + timing);
        holder.time.setText(timing);
        for (int i=0; i<chats.size(); i++) {
            if (chats.get(i).split("~")[1].split(":")[0].equals(contactName)) {
                holder.chatBubble.setText(chats.get(i).split("~")[3]);
                holder.chatBubble.setBackgroundResource(R.drawable.ic_chat);
                holder.chatBubble.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImage;
        TextView contactName;
        TextView time;
        TextView chatBubble;

        ContactViewHolder(View itemView) {
            super(itemView);

            contactImage = (CircleImageView) itemView.findViewById(R.id.contact_image);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            time = (TextView) itemView.findViewById(R.id.time_of_msg);
            chatBubble = (TextView) itemView.findViewById(R.id.chatBubble);
        }
    }
}