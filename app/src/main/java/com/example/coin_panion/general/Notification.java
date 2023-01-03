package com.example.coin_panion.general;

public class Notification {

    private Integer NotificationID;
    private Integer SenderID;
    private Integer ReceiverID;
    private String NotificationDesc;

    public Notification(Integer notificationID, Integer senderID, Integer receiverID, String notificationDesc) {
        NotificationID = notificationID;
        SenderID = senderID;
        ReceiverID = receiverID;
        NotificationDesc = notificationDesc;
    }

    public String getNotificationDesc() {
        return NotificationDesc;
    }

    public void setNotificationDesc(String notificationDesc) {
        NotificationDesc = notificationDesc;
    }

}
