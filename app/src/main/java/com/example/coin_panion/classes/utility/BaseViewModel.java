package com.example.coin_panion.classes.utility;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class BaseViewModel extends ViewModel {
    private HashMap<String, Object> data = new HashMap<>();
    private HashMap<String, Object[]> arrayData = new HashMap<>();

    public void put(String key, Object value){
        this.data.put(key, value);
    }

    public Object get(String key){
        return this.data.get(key);
    }

    public void putArray(String key, Object[] values){
        this.arrayData.put(key, values);
    }

    public Object[] getArray(String key){
        return this.arrayData.get(key);
    }
}
