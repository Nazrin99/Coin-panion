package com.example.coin_panion.classes.general;

public class Notification {


    private Integer senderID;
    private Integer receiverID;
    private String notificationDesc;
    private Integer groupID;

    public Notification(Integer groupID, Integer senderID, Integer receiverID, String notificationDesc) {
        this.groupID = groupID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.notificationDesc = notificationDesc;
    }

    public String getNotificationDesc() {
        return notificationDesc;
    }

    public void setNotificationDesc(String notificationDesc) {
        this.notificationDesc = notificationDesc;
    }



}
