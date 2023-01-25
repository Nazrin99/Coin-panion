package com.example.coin_panion.classes.transaction;

import androidx.core.content.res.ResourcesCompat;

import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class Transaction {
    private String transactionID;
    private String transactionType;
    private String groupID;
    private String creditorID;
    private String debtorID;
    private String transactionName;
    private Double amount;
    private Date epochIssued;
    private Date epochSettled;
    private static List<Transaction> transactions;

    // Default constructor
    public Transaction(){

    }

    // Constructor for creation of new transaction database object. Serves no purpose other than facilitating database insertion
    public Transaction(String groupID, String creditorID, String debtorID, String transactionName, Double amount, TransactionType transactionType) {
        this.groupID = groupID;
        this.creditorID = creditorID;
        this.debtorID = debtorID;
        this.transactionName = transactionName;
        this.amount = amount;
        this.epochIssued = new Date();
        this.transactionType = transactionType.toString();
    }

    public Transaction(String transactionID, String transactionType, String groupID, String creditorID, String debtorID, String transactionName, Double amount, Date epochIssued, Date epochSettled) {
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getCreditorID() {
        return creditorID;
    }

    public void setCreditorID(String creditorID) {
        this.creditorID = creditorID;
    }

    public String getDebtorID() {
        return debtorID;
    }

    public void setDebtorID(String debtorID) {
        this.debtorID = debtorID;
    }

    public void setEpochIssued(Date epochIssued) {
        this.epochIssued = epochIssued;
    }

    public void setEpochSettled(Date epochSettled) {
        this.epochSettled = epochSettled;
    }

    public Date getEpochIssued() {
        return epochIssued;
    }

    public Date getEpochSettled() {
        return epochSettled;
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

    public static List<Transaction> getListOfTransaction(String groupID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference groupReference = firebaseFirestore.collection("group").document("groupID");

        Query query = firebaseFirestore.collection("transaction").whereEqualTo("groupID", groupReference);
        AtomicReference<List<Transaction>> listAtomicReference = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<Transaction> transactions = new ArrayList<>();
                    for(int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                        Transaction transaction = new Transaction(
                                documentSnapshot.getString("transactionID"),
                                documentSnapshot.getString("transType"),
                                documentSnapshot.getString("groupID"),
                                documentSnapshot.getString("creditor"),
                                documentSnapshot.getString("debtor"),
                                documentSnapshot.getString("transName"),
                                documentSnapshot.getDouble("amount"),
                                documentSnapshot.getDate("epochIssued"),
                                documentSnapshot.getDate("epochSettle"));
                        transactions.add(transaction);
                    }
                    listAtomicReference.set(transactions);
                }
                else{
                    listAtomicReference.set(new ArrayList<>());
                }
            }

        });
        while(null == listAtomicReference.get()){
        }
        System.out.println(listAtomicReference.get().size());
        return listAtomicReference.get();
    }

    public static List<Transaction> getTransactions() {
        return transactions;
    }

    public static void setTransactions(List<Transaction> transactions) {
        Transaction.transactions = transactions;
    }
}
