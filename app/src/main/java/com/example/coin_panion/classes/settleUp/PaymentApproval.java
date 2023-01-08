package com.example.coin_panion.classes.settleUp;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PaymentApproval{

    private Integer transactionID;
    private Long epochPaymentIssued;
    private Long epochPaymentApproved;
    private Picture paymentProof;
    private PaymentApprovalStatus paymentApprovalStatus;

    public PaymentApproval(Integer transactionID, Long epochPaymentIssued, Long epochPaymentApproved, Picture paymentProof, PaymentApprovalStatus paymentApprovalStatus) {
        this.transactionID = transactionID;
        this.epochPaymentIssued = epochPaymentIssued;
        this.epochPaymentApproved = epochPaymentApproved;
        this.paymentProof = paymentProof;
        this.paymentApprovalStatus = paymentApprovalStatus;
    }

    // Method to retrieve payment approval from database
    public static List<PaymentApproval> getPaymentApprovals(Integer accountID, PaymentApprovalStatus paymentApprovalStatus, Thread dataThread){
        AtomicReference<List<PaymentApproval>> listAtomicReference = new AtomicReference<>();
        AtomicReference<List<Integer>> ids = new AtomicReference<>();
        if(paymentApprovalStatus == PaymentApprovalStatus.PAYMENT_PENDING_APPROVAL || paymentApprovalStatus == PaymentApprovalStatus.PAYMENT_APPROVED) {
            ids.set(getAccountDebtsID(accountID, dataThread));
        }
        else if(paymentApprovalStatus == PaymentApprovalStatus.APPROVAL_GIVEN || paymentApprovalStatus == PaymentApprovalStatus.APPROVAL_REQUESTED){
            ids.set(getAccountCredits(accountID, dataThread));
        }
        else{
            return null;
        }
        List<PaymentApproval> paymentApprovalList = new ArrayList<>();
        List<Integer> pictureIDs = new ArrayList<>();
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM payment_approval WHERE payment_approv_status = ? AND transaction_id = ? " + createWhereQueryString("transaction_id", ids.get().size()));
            ){
                preparedStatement.setString(1, paymentApprovalStatus.toString());
                for(int i  = 0 ; i < ids.get().size(); i++){
                    preparedStatement.setInt(i+2, ids.get().get(i));
                }
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    paymentApprovalList.add(new PaymentApproval(resultSet.getInt(1), resultSet.getLong(2), resultSet.getLong(3), null , getPaymentApprovalStatus(resultSet.getString(5))));
                    pictureIDs.add(resultSet.getInt(4));
                }
                resultSet.close();

                // Query and append Picture object
                for(int i = 0 ; i < pictureIDs.size(); i++){
                    paymentApprovalList.get(i).setPaymentProof(Picture.getPictureFromDB(pictureIDs.get(i)));
                }
                listAtomicReference.set(paymentApprovalList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return listAtomicReference.get();
    }

    // Method to get the ids of debts that users have. Debts are eligible for PaymentApprovalStatus of type PAYMENT_PENDING_APPROVAL and PAYMENT_APPROVED
    public static List<Integer> getAccountDebtsID(Integer accountID, Thread dataThread){
        AtomicReference<List<Integer>> listAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            List<Integer> transactionIDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT transaction_id FROM transaction WHERE debtor = ?");
                    ){
                preparedStatement.setInt(1, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    transactionIDs.add(resultSet.getInt(1));
                }
                listAtomicReference.set(transactionIDs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

    // Method to retrieve ids of credits that users have. Credits are eligible for PaymentApprovalStatus of type APPROVAL_GIVEN and APPROVAL_REQUESTED
    public static List<Integer> getAccountCredits(Integer accountID, Thread dataThread){
        AtomicReference<List<Integer>> listAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            List<Integer> transactionIDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT transaction_id FROM transaction WHERE creditor = ?");
            ){
                preparedStatement.setInt(1, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    transactionIDs.add(resultSet.getInt(1));
                }
                listAtomicReference.set(transactionIDs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

    private static PaymentApprovalStatus getPaymentApprovalStatus(String status){
        if(PaymentApprovalStatus.PAYMENT_APPROVED.toString().equals(status)){
            return PaymentApprovalStatus.PAYMENT_APPROVED;
        }
        else if(PaymentApprovalStatus.APPROVAL_GIVEN.toString().equals(status)){
            return PaymentApprovalStatus.APPROVAL_GIVEN;
        }
        else if(PaymentApprovalStatus.PAYMENT_PENDING_APPROVAL.toString().equals(status)){
            return PaymentApprovalStatus.PAYMENT_PENDING_APPROVAL;
        }
        else if(PaymentApprovalStatus.APPROVAL_REQUESTED.toString().equals(status)){
            return PaymentApprovalStatus.APPROVAL_REQUESTED;
        }
        else{
            return null;
        }
    }

    // Create where query statement
    private static String createWhereQueryString(String columnName, Integer length){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length-1; i++){
            stringBuilder.append(" OR " + columnName + " = ?");
        }
        return stringBuilder.toString();
    }


    // Getters and setters
    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public Long getEpochPaymentIssued() {
        return epochPaymentIssued;
    }

    public void setEpochPaymentIssued(Long epochPaymentIssued) {
        this.epochPaymentIssued = epochPaymentIssued;
    }

    public Long getEpochPaymentApproved() {
        return epochPaymentApproved;
    }

    public void setEpochPaymentApproved(Long epochPaymentApproved) {
        this.epochPaymentApproved = epochPaymentApproved;
    }

    public Picture getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(Picture paymentProof) {
        this.paymentProof = paymentProof;
    }
}
