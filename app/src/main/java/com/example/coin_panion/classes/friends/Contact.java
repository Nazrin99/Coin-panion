package com.example.coin_panion.classes.friends;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.health.PackageHealthStats;
import android.provider.ContactsContract;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Contact {

    private String contactName;
    private String contactNumber;
    private Boolean isSelected;
    private Boolean hasAccount; //????

    public Contact() {

    }

    public Contact(String contactName, String contactNumber, Boolean isSelected) {
        this.contactName = contactName;

        if(contactNumber.matches("^\\d+$")){
            this.contactNumber = contactNumber.replaceAll("[^\\d]", "");
        }else {
            this.contactNumber = contactNumber;
        }

        this.isSelected = isSelected;
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

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    /*TODO based on contact number check if the contact number exist in database*/
    public Boolean getHasAccount() {
        return hasAccount;
    }

    @SuppressLint("Range")
    public static ArrayList<Contact> getAllContacts(ContentResolver contentResolver) {
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> phoneNumber = new ArrayList<>();
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver cr = contentResolver;
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                @SuppressLint("Range") String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber.add(phoneNo.substring(1));
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        for(int i = 0; i  < phoneNumber.size(); i++){
            contacts.add(new Contact(nameList.get(i), phoneNumber.get(i)));
        }
        return contacts;
    }

    public static boolean hasAnAccount(Long phoneNumber, Thread dataThread){
        AtomicReference<Boolean> booleanAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE phone_number = ?")
                    ){
                preparedStatement.setLong(1, phoneNumber);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    booleanAtomicReference.set(true);
                }
                else{
                    booleanAtomicReference.set(false);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return booleanAtomicReference.get();
    }
}
