package com.example.coin_panion.classes.notification;

import android.speech.RecognitionService;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class Notification {
    private Integer notiID;
    private NotificationType notiType;
    private Integer groupID;
    private Integer transactionID;
    private Integer senderID;
    private Integer receiverID;
    private Long notiEpoch;

    // Constructor to create complete notification object
    public Notification(Integer notiID, String notiType, Integer groupID, Integer transactionID, Integer senderID, Integer receiverID, Long notiEpoch) {
        this.notiID = notiID;
        this.notiType = getNotiType(notiType);
        this.groupID = groupID;
        this.transactionID = transactionID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.notiEpoch = notiEpoch;
    }

    /**
     * Returns a list of notifications based on the given accountID
     * @param accountID
     * @param dataThread
     * @return
     */
    public static List<Notification> getNotifications(Integer accountID, Thread dataThread){
        AtomicReference<List<Notification>> listAtomicReference = new AtomicReference<>(new ArrayList<>());
        dataThread = new Thread(() -> {
            List<Notification> notifications = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM notification WHERE sender = ? OR receiver = ?")
            ){
                preparedStatement.setInt(1, accountID);
                preparedStatement.setInt(2, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    notifications.add(new Notification(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5), resultSet.getInt(6), resultSet.getLong(7)));
                }
                resultSet.close();
                listAtomicReference.set(notifications);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

    private static NotificationType getNotiType(String argument){
        if(NotificationType.ADDED_INTO_GROUP.getType().equalsIgnoreCase(argument)){
            return NotificationType.ADDED_INTO_GROUP;
        }
        else if(NotificationType.PAYMENT_APPROVAL.getType().equalsIgnoreCase(argument)){
            return NotificationType.PAYMENT_APPROVAL;
        }
        else{
            return NotificationType.GENERAL;
        }
    }
}
