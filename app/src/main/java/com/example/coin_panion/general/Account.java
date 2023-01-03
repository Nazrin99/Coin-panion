package com.example.coin_panion.general;

import com.example.coin_panion.Friends.Friend;
import com.example.coin_panion.SettleUp.SettleUp;
import com.example.coin_panion.SettleUp.SettleUpDebt;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private User profile;
    private DebtLimit userDebtLimit;
    private ArrayList<PaymentRequest> paymentRequestsList;
    private List<Friend> friendsList;

    public Account() {
        paymentRequestsList = new ArrayList<>();
        friendsList = new ArrayList<>();
    }

    public void updateUserProfile(Blob profilePic, String userName, String phoneNumber, String email){
        if(profilePic!=null) {
            profile.UpdateProfilePic(profilePic);
        }
        if(userName!=null){
            profile.UpdateUserName(userName);
        }
        if(phoneNumber!=null){
            profile.updatePhoneNumber(phoneNumber);
        }
        if(email!=null){
            profile.updateEmail(email);
        }
    }

    // TODO get the userID and DebtLimitID to perform update of debt limit
    public void setUserDebtLimit(String currencyType, String debtLimitTime, Integer debtLimitAmount){
        if(currencyType!=null){
            userDebtLimit.setCurrencyType(currencyType);
        }
        if(debtLimitTime!=null){
            userDebtLimit.setDebtLimitTime(debtLimitTime);
        }
        if(debtLimitAmount!=null){
            userDebtLimit.setDebtLimitAmount(debtLimitAmount);
        }
    }

    public List<Friend> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<Friend> friendsList) {
        this.friendsList = friendsList;
    }

}
