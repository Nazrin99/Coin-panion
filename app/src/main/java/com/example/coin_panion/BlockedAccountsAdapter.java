package com.example.coin_panion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.classes.general.Account;

import java.util.ArrayList;
import java.util.List;

public class BlockedAccountsAdapter extends RecyclerView.Adapter<BlockedAccountsAdapter.ContactViewHolder>{

    private List<Account> blockedContactList;
    private Context mContext;
    private List<Account> unblockedContacts = new ArrayList<>();
    public BlockedAccountsAdapter(List<Account> blockedContactList, Context mContext){
        this.blockedContactList = blockedContactList;
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blocked_contact_list_item, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Account blockedContact = blockedContactList.get(position);
        holder.tvContactName.setText(blockedContact.getUser().getUsername());
        holder.tvPhoneNumber.setText(blockedContact.getUser().getPhoneNumber());
        holder.blockedCheckBox.setChecked(true);
        holder.blockedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    unblockedContacts.add(blockedContact);
                }
                else{
                    unblockedContacts.remove(blockedContact);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockedContactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;
        CheckBox blockedCheckBox;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            blockedCheckBox = (CheckBox) itemView.findViewById(R.id.blockedCheckBox);
        }
    }

    public List<Account> getBlockedContactList() {
        return blockedContactList;
    }

    public void setBlockedContactList(List<Account> blockedContactList) {
        this.blockedContactList = blockedContactList;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<Account> getUnblockedContacts() {
        return unblockedContacts;
    }

    public void setUnblockedContacts(List<Account> unblockedContacts) {
        this.unblockedContacts = unblockedContacts;
    }
}