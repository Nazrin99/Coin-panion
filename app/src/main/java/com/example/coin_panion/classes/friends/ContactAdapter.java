package com.example.coin_panion.classes.friends;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable{

    List<Contact> contacts;
    List<Contact> otherContacts;
    ContactAdapter otherAdapter;
    List<Contact> backupContacts = new ArrayList<>();
    Context context;
    Boolean toAdd;

    public ContactAdapter(List<Contact> contacts, List<Contact> otherContacts, ContactAdapter otherAdapter, Context context, Boolean toAdd) {
        this.contacts = contacts;
        this.otherContacts = otherContacts;
        this.otherAdapter = otherAdapter;
        this.context = context;
        this.toAdd = toAdd;
        this.backupContacts = new ArrayList<>(contacts);
    }

    public ContactAdapter(List<Contact> contacts, List<Contact> otherContacts, Context context, Boolean toAdd) {
        this.contacts = contacts;
        this.otherContacts = otherContacts;
        this.context = context;
        this.toAdd = toAdd;
        this.backupContacts = contacts;
    }

    public List<Contact> getOtherContacts() {
        return otherContacts;
    }

    public void setOtherContacts(List<Contact> otherContacts) {
        this.otherContacts = otherContacts;
    }

    public ContactAdapter getOtherAdapter() {
        return otherAdapter;
    }

    public void setOtherAdapter(ContactAdapter otherAdapter) {
        this.otherAdapter = otherAdapter;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*Initialize view item*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_from_contact_item,parent,false);

        /*Return the view inflated*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        /*Get the contact object*/
        Contact contact = contacts.get(holder.getAdapterPosition());

        /*Initialze the variable based on contact*/
        holder.TVContactName.setText(contact.getContactName());
        holder.TVContactNumber.setText(contact.getContactNumber());
        holder.removeContactButton.setText(toAdd == true ? "Add" : "Remove");

        // Check for each contact if they have an account, if they don't disable their respective checkboxes
        /*Check if the contact is selected*/
        holder.removeContactButton.setOnClickListener(v -> {
            otherContacts.add(contact);
            otherContacts.sort(Comparator.comparing(Contact::getContactName));
            contacts.remove(contact);
            contacts.sort(Comparator.comparing(Contact::getContactName));
            notifyDataSetChanged();
            otherAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    contacts = backupContacts;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact contact : backupContacts) {
                        if (contact.getContactName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(contact);
                        }
                    }
                    contacts = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contacts = (List<Contact>) results.values;

                notifyDataSetChanged();
            }
        };
    }

    //    @Override
//    public Filter getFilter() {
//
//        return contactsFilter;
//    }
//
//    private final Filter contactsFilter = new Filter() {
//        /*Filter the contact by number and name of contact that is to be added*/
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//
//            ArrayList<Contact> filteredContacts = new ArrayList<>();
//
//            if(constraint == null || constraint.length() == 0){
//
//                filteredContacts.addAll(contacts);
//
//            } else {
//
//                String filterPattern = contactsFilter.toString().toLowerCase().trim();
//
//                for(Contact contact : contacts){
//
//                    if(contact.getContactName().contains(filterPattern)){
//
//                        filteredContacts.add(contact);
//
//                    }else if(contact.getContactNumber().contains(filterPattern)){
//
//                        filteredContacts.add(contact);
//
//                    }
//
//                }
//
//            }
//
//            /*Create filter results*/
//            FilterResults results = new FilterResults();
//            results.values = filteredContacts;
//            results.count = filteredContacts.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            contacts.clear();
//            contacts.addAll((ArrayList)results.values);
//            notifyDataSetChanged();
//        }
//    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*Variable that will hold contact info from cardview*/
        TextView TVContactName, TVContactNumber;
        Button removeContactButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*Assign the variable*/
            TVContactName = itemView.findViewById(R.id.contactNameTextView);
            TVContactNumber = itemView.findViewById(R.id.TVContact_number);
            removeContactButton = itemView.findViewById(R.id.removeContactButton);
        }

    }

    public List<Contact> getBackupContacts() {
        return backupContacts;
    }

    public void setBackupContacts(List<Contact> backupContacts) {
        this.backupContacts = backupContacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
