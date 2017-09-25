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

import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<String> contacts;
    private Context context;
    private String name;
    private APIInterface apiInterface;
    private static final String TAG = "ChatMainActivity";
    public static int count = 0;
    private String str;
    private String time;

    public ContactsAdapter(List<String> contacts, Context context, String name,
                           String str, String time) {
        this.contacts = contacts;
        this.context = context;
        this.name = name;
        this.str = str;
        this.time = time;
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
        final String dt = time.replaceAll(("/"+ Calendar.getInstance()
                .get(Calendar.YEAR)), "");

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
        holder.time.setText(dt/*.split("-")[position]*/);
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImage;
        TextView contactName;
        TextView time;

        ContactViewHolder(View itemView) {
            super(itemView);

            contactImage = (CircleImageView) itemView.findViewById(R.id.contact_image);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            time = (TextView) itemView.findViewById(R.id.time_of_msg);
        }
    }
}