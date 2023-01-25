package com.example.coin_panion.classes.friends;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.health.PackageHealthStats;
import android.provider.ContactsContract;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Contact {

    private String contactName;
    private String contactNumber;

    public Contact() {

    }

    // Constructor to necessary contacts objects
    public Contact(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public static List<Contact> getAllContacts(ContentResolver contentResolver, List<Account> friends, Account currentAccount){
        List<String> friendPhoneNumbers = new ArrayList<>();
        for(Account account: friends){
            friendPhoneNumbers.add(account.getUser().getPhoneNumber());
        }
        List<Contact> contactList = new ArrayList();
        List<String> bufferSet = new ArrayList<>();
        Contact contact;

        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contact = new Contact();
                    contact.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!bufferSet.contains(phoneNumber)){
                            contact.setContactNumber(phoneNumber);
                        }
                        else{
                            continue;
                        }
                    }

                    phoneCursor.close();

                    AtomicReference<Contact> contactAtomicReference = new AtomicReference<>(contact);
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    Query query = firebaseFirestore.collection("user").whereEqualTo("phoneNumber", contact.getContactNumber() == null ? null : contact.getContactNumber());
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                if(!contactAtomicReference.get().getContactNumber().equalsIgnoreCase(currentAccount.getUser().getPhoneNumber())){
                                    contactList.add(contactAtomicReference.get());
                                }
                                for(String number: friendPhoneNumbers){
                                    if(number.equalsIgnoreCase(contactAtomicReference.get().getContactNumber())){
                                        contactList.remove(contactAtomicReference.get());
                                        return;
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
        Set<Contact> set = new LinkedHashSet<>(contactList);
        contactList.clear();
        contactList.addAll(set);

        return contactList;
    }
}
