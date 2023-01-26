package com.example.coin_panion.classes.utility;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class Validifier {
    public static boolean isEmail(String email){
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isPhoneNumber(String phoneNumber){
        // Definitions of a phone number
        //      +601xxxxxxxx[x?]
        // Prefix +601 followed by 8 or 9 numbers

        String pattern = "[+]601[0-9]{8}[0-9]?";
        return (!TextUtils.isEmpty(phoneNumber) && Pattern.compile(pattern).matcher(phoneNumber).matches());
    }

    public static boolean validPassword(String password){
        return (password.length() >= 8 && password.matches(".*[^a-zA-Z0-9 ].*") && password.matches(".*\\d+.*"));
    }

    public static Date getProperDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        return calendar.getTime();
    }
}
