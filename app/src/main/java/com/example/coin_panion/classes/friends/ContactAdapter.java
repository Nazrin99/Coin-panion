package com.example.coin_panion.classes.friends;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {

    Activity readContactLog;
    List<Contact> contacts;
    List<Contact> selectedContacts;
    List<Contact> filteredContacts;

    public ContactAdapter(Activity readContactLog, List<Contact> contacts) {
        this.readContactLog = readContactLog;
        this.contacts = contacts;
        selectedContacts = new ArrayList<>();
        filteredContacts = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*Initialize view item*/
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_from_contact_item,parent,false);

        /*Return the view inflated*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        /*Get the contact object*/
        Contact contact = contacts.get(position);

        /*Initialze the variable based on contact*/
        holder.TVContactName.setText(contact.getContactName());
        holder.TVContactNumber.setText(contact.getContactNumber());

        /*Check if the contact is selected*/
        holder.CBSelectContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                contact.setSelected(isChecked);

                if (contact.getSelected()) {

                    if (contact.getHasAccount()) {
                        /*Add every selected contact to an arraylist if and only if they have an account*/
                        selectedContacts.add(contact);
                    } else {
                        /*Check if the contact selected is have conpainion account if not toast*/
                        Toast.makeText(buttonView.getContext(), "This contact does not have a Companion account", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    /*If the contact selected can be added but diselected*/
                    selectedContacts.remove(contact);
                }
            }
        });



        /*TODO onclick removed all the friend that is not selected*/
//        selectedContacts.removeIf(friend -> !friend.isSelected());

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public Filter getFilter() {

        return contactsFilter;
    }

    private final Filter contactsFilter = new Filter() {
        /*Filter the contact by number and name of contact that is to be added*/
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Contact> filteredContacts = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){

                filteredContacts.addAll(contacts);

            } else {

                String filterPattern = contactsFilter.toString().toLowerCase().trim();

                for(Contact contact : contacts){

                    if(contact.getContactName().contains(filterPattern)){

                        filteredContacts.add(contact);

                    }else if(contact.getContactNumber().contains(filterPattern)){

                        filteredContacts.add(contact);

                    }

                }

            }

            /*Create filter results*/
            FilterResults results = new FilterResults();
            results.values = filteredContacts;
            results.count = filteredContacts.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts.clear();
            contacts.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*Variable that will hold contact info from cardview*/
        TextView TVContactName, TVContactNumber;
        CheckBox CBSelectContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*Assign the variable*/
            TVContactName = itemView.findViewById(R.id.TVContact_name);
            TVContactNumber = itemView.findViewById(R.id.TVContact_number);
            CBSelectContact = itemView.findViewById(R.id.CBSelectContact);
        }

    }

    public void getAllSelectedContact(){
        contacts.removeIf(contact -> !contact.getSelected());
    }
}
