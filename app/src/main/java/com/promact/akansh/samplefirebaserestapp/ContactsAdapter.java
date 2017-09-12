package com.promact.akansh.samplefirebaserestapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.promact.akansh.samplefirebaserestapp.pojo.ContactsBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<ContactsBean> contacts;
    private Context context;

    public ContactsAdapter(List<ContactsBean> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
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
        ContactsBean contactsBean = contacts.get(position);

        holder.contactName.setText(contactsBean.getContactName());
        holder.contactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("contactName", holder.contactName.getText());

                context.startActivity(intent);
            }
        });
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImage;
        TextView contactName;

        ContactViewHolder(View itemView) {
            super(itemView);

            contactImage = (CircleImageView) itemView.findViewById(R.id.contact_image);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
        }
    }
}