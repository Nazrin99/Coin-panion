package com.example.coin_panion.general;

public class Notification {


    private Integer notificationID;
    private Integer senderID;
    private Integer receiverID;
    private String notificationDesc;

    public Notification(Integer notificationID, Integer senderID, Integer receiverID, String notificationDesc) {
        this.notificationID = notificationID;
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
