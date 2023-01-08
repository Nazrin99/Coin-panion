package com.example.coin_panion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.coin_panion.classes.friends.Contact;
import com.example.coin_panion.classes.friends.ContactAdapter;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    //Initialize variable
    RecyclerView RVContactList;
    ArrayList<Contact> contacts = new ArrayList<>();
    ContactAdapter contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        callView();

        checkPermission();
    }

    public void callView() {
        RVContactList = findViewById(R.id.RVContactList);
    }

    public void checkPermission() {
        // Check Condition
        if (ContextCompat.checkSelfPermission(FriendsActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // When permission is not granted

            // Request permission from user
            ActivityCompat.requestPermissions(FriendsActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {

            /*Read User's contact log*/
            getContactLog();
        }
    }

    private void getContactLog(){
        /*Initialize URI*/
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        /*Sort contact by ascending*/
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC";
        /*Initialize cursor for selected contact*/
        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
        /*Check Condition*/
        if(cursor.getCount() > 0){
            //When count is greater than 0
            //User while loop
            while(cursor.moveToNext()){
                // Cursor move to the next
                // Get contact id
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                // Get contact name
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // Initialize phone URI
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                // Initialize selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                // Initialize phone cursor
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection,new String[]{id},null);
                // Check condition
                if(phoneCursor.moveToNext()){
                    // When phone cursor move to next
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Initialize the contact object
                    Contact contact = new Contact(name,Integer.valueOf(number));
                    // add to arraylist to
                    contacts.add(contact);
                    // Close phone cursor
                    phoneCursor.close();
                }
            }
            // Close cursor
            cursor.close();
        }
        /*Set layout manager*/
        RVContactList.setLayoutManager(new LinearLayoutManager(this));
        /*Initialize adapter*/
        contactAdapter = new ContactAdapter(this,contacts);
        /*Set the adapter*/
        RVContactList.setAdapter(contactAdapter);
    }

    /*TODO check if permission access is required*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Check condition
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // When permission is granted
            // Call method
            getContactLog();
        }else{
            // When permission is denied
            Toast.makeText(FriendsActivity.this, "Need permission",Toast.LENGTH_SHORT).show();
            // Call back check permission method
            checkPermission();
        }
    }
}

