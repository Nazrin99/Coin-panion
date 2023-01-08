package com.example.coin_panion.classes.general;

import android.graphics.drawable.Drawable;

import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.transaction.SettleUpAccount;

import java.util.List;

public class Account{
    private String accountID;
    private User user;
    private String password;
    private String bio;
    private List<User> friends;
    private DebtLimit debtLimit;
    private SettleUpAccount settleUpAccount;
    private List<Notification> notifications;

}