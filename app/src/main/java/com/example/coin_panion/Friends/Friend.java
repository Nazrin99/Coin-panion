package com.example.coin_panion.Friends;

import com.example.coin_panion.SettleUp.SettleUpDebt;
import com.example.coin_panion.general.Notification;
import com.example.coin_panion.general.User;

import java.sql.Blob;

public class Friend{

    private boolean blockedStatus;
    private Integer friendID;
    private Integer userID;
    private String friendName;
    private String phoneNumber;
    private Blob profilePic;

    public Friend(Integer userID, Integer friendID, String friendName, String phoneNumber, Blob profilePic) {
        this.friendID = friendID;
        this.friendName = friendName;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
        this.blockedStatus = false;
        this.userID = userID;
    }

    public boolean isBlockedStatus() {
        return blockedStatus;
    }

    public void setBlockedStatus(boolean blockedStatus) {
        this.blockedStatus = blockedStatus;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Blob getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Blob profilePic) {
        this.profilePic = profilePic;
    }

    // TODO add remind function
    // should the notification id be fixed
    // get the friend debt
    // if this friend is in debtlist this function  will create noti if not toast message
    // "friendName is not in debt with you"
    public String remind(){
        String reminder = getFriendName() + "Don't forget to pay me 'RM 30'";
        Notification notification = new Notification(3,userID,friendID,reminder);
        return notification.getNotificationDesc();
    }

    // TODO add settle up function
    public void settleUpFriendDebt(Blob paymentEvidence){
        SettleUpDebt settleUpDebt = new SettleUpDebt(friendID, userID, paymentEvidence);
    }


    // TODO get debt info
    public void getDebtOwned(){

    }

}
