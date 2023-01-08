package com.example.coin_panion.classes.transaction;

import androidx.core.content.res.ResourcesCompat;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class Transaction {
    private Integer transactionID;
    private String transactionType;
    private Integer groupID;
    private Integer creditorID;
    private Integer debtorID;
    private String transactionName;
    private Double amount;
    private Long epochIssued;
    private Long epochSettled;

    // Default constructor
    public Transaction(){

    }

    // Constructor for creation of new transaction database object. Serves no purpose other than facilitating database insertion
    public Transaction(Integer groupID, Integer creditorID, Integer debtorID, String transactionName, Double amount, TransactionType transactionType) {
        this.groupID = groupID;
        this.creditorID = creditorID;
        this.debtorID = debtorID;
        this.transactionName = transactionName;
        this.amount = amount;
        this.epochIssued = Instant.now().toEpochMilli();
        this.transactionType = transactionType.toString();
    }

    // Constructor for creation of existing transaction object
    public Transaction(Integer transactionID, String transactionType, Integer groupID, Integer creditorID, Integer debtorID, String transactionName, Double amount, Long epochIssued, Long epochSettled) {
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.groupID = groupID;
        this.creditorID = creditorID;
        this.debtorID = debtorID;
        this.transactionName = transactionName;
        this.amount = amount;
        this.epochIssued = epochIssued;
        this.epochSettled = epochSettled;
    }


    // Method to store the new transaction in database and returns a complete Transaction object
    public Transaction storeNewTransaction(Transaction transaction, Thread dataThread){
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                // Insert new transaction object into database
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO transaction(group_id, creditor, debtor, trans_name, amount, epoch_issued, trans_type) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, transaction.getGroupID());
                preparedStatement.setInt(2, transaction.getCreditorID());
                preparedStatement.setInt(3, transaction.getDebtorID());
                preparedStatement.setString(4, transaction.getTransactionName());
                preparedStatement.setDouble(5, transaction.getAmount());
                preparedStatement.setLong(6, transaction.getEpochIssued());
                preparedStatement.setString(7, transaction.getTransactionType());
                preparedStatement.execute();

                // Get the generated id from database
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                Integer transactionID = resultSet.getInt(1);
                resultSet.close();

                // Add the id to the existing transaction object
                transaction.setTransactionID(transactionID);
                transactionAtomicReference.set(transaction);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return transactionAtomicReference.get();
    }

    // Method to load existing transaction for a particular group and returns a list of transactions
    public List<Transaction> loadGroupTransaction(Integer groupID, Thread dataThread){
        AtomicReference<List<Transaction>> listAtomicReference = new AtomicReference<>();
        dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Transaction> transactions = new ArrayList<>();
                try{
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM transaction WHERE group_id = ?");
                    preparedStatement.setInt(1, groupID);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while(resultSet.next()){
                        Transaction toAdd = new Transaction(resultSet.getInt(1), resultSet.getString(9), resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getDouble(6), resultSet.getLong(7), resultSet.getLong(8));
                        transactions.add(toAdd);
                    }
                    resultSet.close();
                    listAtomicReference.set(transactions);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        ThreadStatic.run(dataThread);
        return  listAtomicReference.get();
    };


    // Getters and Setters
    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getCreditorID() {
        return creditorID;
    }

    public void setCreditorID(Integer creditorID) {
        this.creditorID = creditorID;
    }

    public Integer getDebtorID() {
        return debtorID;
    }

    public void setDebtorID(Integer debtorID) {
        this.debtorID = debtorID;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getEpochIssued() {
        return epochIssued;
    }

    public void setEpochIssued(Long epochIssued) {
        this.epochIssued = epochIssued;
    }

    public Long getEpochSettled() {
        return epochSettled;
    }

    public void setEpochSettled(Long epochSettled) {
        this.epochSettled = epochSettled;
    }
}
