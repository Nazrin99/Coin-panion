package com.example.coin_panion.classes.utility;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class BaseViewModel extends ViewModel implements Serializable {
    private HashMap<String, Object> data = new HashMap<>();
    private HashMap<String, List<Object>> arrayData = new HashMap<>();

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
