package com.example.coin_panion.classes.utility;

import androidx.lifecycle.ViewModel;

import com.example.coin_panion.classes.general.Account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class BaseViewModel extends ViewModel implements Serializable {
    private HashMap<String, Object> data = new HashMap<>();
    private HashMap<String, List<Object>> arrayData = new HashMap<>();
    private HashMap<String, Account> nonUserAccountAccountKey = new HashMap<>();
    private HashMap<String, Account> nonUserAccountUserKey = new HashMap<>();

    public void putAccount(String accountID, String userID, Account account){
        this.nonUserAccountAccountKey.put(accountID, account);
        this.nonUserAccountUserKey.put(userID, account);
    }

    public Account getAccountWithUserID(String userID){
        return nonUserAccountUserKey.get(userID);
    }

    public Account getAccountWithAccountID(String accountID){
        return nonUserAccountAccountKey.get(accountID);
    }

    public void put(String key, Object value){
        this.data.put(key, value);
    }

    public Object get(String key){
        return this.data.get(key);
    }

    public void putArray(String key, List<Object> values){
        this.arrayData.put(key, values);
    }

    public List<Object> getArray(String key){
        return this.arrayData.get(key);
    }
}
