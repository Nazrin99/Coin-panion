package com.example.coin_panion.classes.notification;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Notification {

    /*Info for each notification created*/
   private Integer transaction_id;
   private Integer noti_id;
   private Integer group_id;
   private Timestamp epoch_noti;
   private Integer sender_id;
   private Integer receiver_id;
   private String noti_title;
   private String noti_desc;

   /*TODO how notification is fetched when no desc is included*/
    public Notification(Integer transaction_id, Integer noti_id, Integer group_id, Timestamp epoch_noti, Integer sender_id, Integer receiver_id) {
        this.transaction_id = transaction_id;
        this.noti_id = noti_id;
        this.group_id = group_id;
        this.epoch_noti = epoch_noti;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
    }

    public Notification(Integer transaction_id, Integer group_id, Timestamp epoch_noti, Integer sender_id, Integer receiver_id) {
        this.transaction_id = transaction_id;
        this.group_id = group_id;
        this.epoch_noti = epoch_noti;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
    }

    /*Receiver Notification*/
    public Notification(Integer receiver_id) {
        this.receiver_id = receiver_id;
    }

    /*Sender Notification*/
    public Notification(Integer group_id, Integer sender_id) {
        this.group_id = group_id;
        this.sender_id = sender_id;
    }

    /*Dummy*/

    public Notification(String noti_title, String noti_desc) {
        this.noti_title = noti_title;
        this.noti_desc = noti_desc;
    }

    /*TODO Insert notification created into database based on this.variable*/
    public void addNotification(){

    }

    /*Fetch relevant notification based on the user account id*/
    public static void fetchUserNotification(){

    }

    /*Fetch relevant notification based on the groupID */
    public static void fetchGroupNotification(){

    }

    /* Fetch general notification */
    public List<Notification> fetchGeneralNotification(){
        // Fetch from notification table for all user's notification
        return null;
    }

    public String getNoti_title() {
        return noti_title;
    }

    public String getNoti_desc() {
        return noti_desc;
    }
}
