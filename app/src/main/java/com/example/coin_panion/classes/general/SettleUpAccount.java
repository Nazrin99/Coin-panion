package com.example.coin_panion.classes.general;

import android.content.ContentResolver;
import android.net.Uri;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

public class SettleUpAccount implements Serializable{
    private Integer accountID;
    private String settleUpAccountName;
    private Long settleUpAccountNumber;
    private Picture qrImage;

    // Constructor
    public SettleUpAccount(Integer accountID, String settleUpAccountName, Long settleUpAccountNumber, Picture qrImage) {
        this.accountID = accountID;
        this.settleUpAccountName = settleUpAccountName;
        this.settleUpAccountNumber = settleUpAccountNumber;
        this.qrImage = qrImage;
    }

    /**
     * Deletes the settle up account a particular account
     * @param accountID
     * @param settleUpAccountName
     * @param dataThread
     */
    public static void deleteSettleUpAccount(Integer accountID, String settleUpAccountName, Thread dataThread){
        dataThread = new Thread(() ->{
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM settle_up_account WHERE account_id = ? AND settleup_account_name = ?")
                    ){
                preparedStatement.setInt(1, accountID);
                preparedStatement.setString(2, settleUpAccountName);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    /**
     * Retrieve SettleUpAccount from database
     * @param accountID
     * @param dataThread
     * @return Return an object of SettleUpAccount
     */
    public static SettleUpAccount retrieveSettleUpAccount(Integer accountID, Thread dataThread){
        AtomicReference<SettleUpAccount> settleUpAccountAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM settle_up_account WHERE account_id = ?");
                    PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM picture WHERE picture_id = ?");){
                preparedStatement.setInt(1, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();

                Integer pictureID = 0;
                if(resultSet.next()){
                    SettleUpAccount settleUpAccount = new SettleUpAccount(accountID, resultSet.getString(2), resultSet.getLong(3), null);
                    pictureID = resultSet.getInt(4);
                    settleUpAccount.setQrImage(Picture.getPictureFromDB(pictureID));
                    settleUpAccountAtomicReference.set(settleUpAccount);
                }
                resultSet.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return settleUpAccountAtomicReference.get();
    }

    /**
     * Insert a new settle up account in the database
     * @param accountID
     * @param settleUpAccountName
     * @param settleUpAccountNumber
     * @param imageUri
     * @param contentResolver
     * @param dataThread
     * @return Returns an object of SettleUpAccount
     */
    public static SettleUpAccount insertSettleUpAccount(Integer accountID, String settleUpAccountName, Long settleUpAccountNumber, Uri imageUri, ContentResolver contentResolver, Thread dataThread){
        AtomicReference<SettleUpAccount> settleUpAccountAtomicReference = new AtomicReference<>();
        Picture qr = Picture.insertPicIntoDB(imageUri, contentResolver, dataThread);
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO settle_up_account VALUES(?,?,?,?)");
                    ){
                preparedStatement.setInt(1, accountID);
                preparedStatement.setString(2, settleUpAccountName);
                preparedStatement.setLong(3, settleUpAccountNumber);
                preparedStatement.setInt(4, qr.getPictureID());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            settleUpAccountAtomicReference.set(new SettleUpAccount(accountID, settleUpAccountName, settleUpAccountNumber, qr));
        });
        return settleUpAccountAtomicReference.get();
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public String getSettleUpAccountName() {
        return settleUpAccountName;
    }

    public void setSettleUpAccountName(String settleUpAccountName) {
        this.settleUpAccountName = settleUpAccountName;
    }

    public Long getSettleUpAccountNumber() {
        return settleUpAccountNumber;
    }

    public void setSettleUpAccountNumber(Long settleUpAccountNumber) {
        this.settleUpAccountNumber = settleUpAccountNumber;
    }

    public Picture getQrImage() {
        return qrImage;
    }

    public void setQrImage(Picture qrImage) {
        this.qrImage = qrImage;
    }
}
