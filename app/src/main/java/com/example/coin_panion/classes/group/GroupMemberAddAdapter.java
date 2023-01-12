package com.example.coin_panion.classes.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.friends.ContactAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberAddAdapter extends RecyclerView.Adapter<GroupMemberAddAdapter.ViewHolder>{
    List<Contact> contacts = new ArrayList<>();
    Context context;
    List<Contact> selectedContacts = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*Variable that will hold contact info from cardview*/
        TextView TVContactName, TVContactNumber;
        CheckBox CBSelectContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*Assign the variable*/
            TVContactName = itemView.findViewById(R.id.contactNameTextView);
            TVContactNumber = itemView.findViewById(R.id.TVContact_number);
            CBSelectContact = itemView.findViewById(R.id.CBSelectContact);
        }

    }

    public GroupMemberAddAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*Initialize view item*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_from_contact_item,parent,false);

        /*Return the view inflated*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*Get the contact object*/
        Contact contact = contacts.get(position);

        /*Initialze the variable based on contact*/
        holder.TVContactName.setText(contact.getContactName());
        holder.TVContactNumber.setText(contact.getContactNumber());

        // Check for each contact if they have an account, if they don't disable their respective checkboxes
        /*Check if the contact is selected*/
        holder.CBSelectContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedContacts.add(contact);
                    // Toast.makeText(buttonView.getContext(), "This contact does not have a Companion account", Toast.LENGTH_SHORT).show();
                } else {
                    /*If the contact selected can be added but diselected*/
                    selectedContacts.remove(contact);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(List<Contact> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }
}
