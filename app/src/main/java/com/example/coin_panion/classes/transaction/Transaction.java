package com.example.coin_panion.classes.transaction;

import com.example.coin_panion.ForgotPassword;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction {
    private String transID;
    private String transName;
    private TransactionStatus transStatus;
    private String groupID;
    private String groupName;
    private User creditorID;
    private User debtorID;
    private Double amount;
    private Date epochIssued;
    private Picture payProof;

    // Default constructor
    public Transaction(){

    }

    public Transaction(String transID, String transName, TransactionStatus transStatus, String groupID, String groupName, User creditorID, User debtorID, Double amount, Date epochIssued, Picture payProof) {
        this.transID = transID;
        this.transName = transName;
        this.transStatus = transStatus;
        this.groupID = groupID;
        this.groupName = groupName;
        this.creditorID = creditorID;
        this.debtorID = debtorID;
        this.amount = amount;
        this.epochIssued = epochIssued;
        this.payProof = payProof;
    }

    public Transaction(String transID, TransactionStatus transStatus, String groupID, String groupName, User creditorID, String debtorID, String transName, Double amount, Date epochIssued) {
        this.transID = transID;
        this.transStatus = transStatus;
        this.groupID = groupID;
        this.groupName = groupName;
        this.creditorID = creditorID;
        this.transName = transName;
        this.amount = amount;
        this.epochIssued = epochIssued;
        this.debtorID = new User(debtorID);
    }

    public Transaction(String transID, TransactionStatus transStatus, String groupID, String groupName, String creditorID, User debtorID, String transName, Double amount, Date epochIssued) {
        this.transID = transID;
        this.transStatus = transStatus;
        this.groupID = groupID;
        this.groupName = groupName;
        this.creditorID = new User(creditorID);
        this.transName = transName;
        this.amount = amount;
        this.epochIssued = epochIssued;
        this.debtorID = debtorID;
    }

    public static void getGroupTransaction(String groupID, List<Transaction> transactionList, Account account){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("transaction")
                .whereIn("transStatus", Arrays.asList("PAYMENT_SETTLED", "PAYMENT_NOT_MADE"))
                .whereEqualTo("creditor", firebaseFirestore.collection("user").document(account.getUser().getUserID()))
                .whereEqualTo("groupID", groupID);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<Transaction> transactionList1 = new ArrayList<>();
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            Transaction transaction = new Transaction(
                                    documentSnapshot.getString("transID"),
                                    TransactionStatus.getTransType(documentSnapshot.getString("transStatus")),
                                    documentSnapshot.getString("groupID"),
                                    documentSnapshot.getString("groupName"),
                                    account.getUser(),
                                    documentSnapshot.getDocumentReference("debtor").getId(),
                                    documentSnapshot.getString("transName"),
                                    documentSnapshot.getDouble("amount"),
                                    documentSnapshot.getDate("epochIssued")
                            );
                            Executors.newSingleThreadExecutor().execute(() -> {
                                transaction.getDebtorID().getUser();
                            });
                            transactionList.add(transaction);
                            transactionList.sort(new Comparator<Transaction>() {
                                @Override
                                public int compare(Transaction o1, Transaction o2) {
                                    return o2.getEpochIssued().compareTo(o1.getEpochIssued());
                                }
                            });
                        }
                        System.out.println("Transaction size: " + transactionList.size());
                    }
                    else{
                        System.out.println("Transaction snapshot is empty");
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                }
            }
        });

        Query query1 = firebaseFirestore.collection("transaction")
                .whereIn("transStatus", Arrays.asList("PAYMENT_NOT_MADE", "PAYMENT_SETTLED"))
                .whereEqualTo("groupID", groupID)
                .whereEqualTo("debtor", firebaseFirestore.collection("user").document(account.getUser().getUserID()));
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            Transaction transaction = new Transaction(
                                    documentSnapshot.getString("transID"),
                                    TransactionStatus.getTransType(documentSnapshot.getString("transStatus")),
                                    documentSnapshot.getString("groupID"),
                                    documentSnapshot.getString("groupName"),
                                    documentSnapshot.getDocumentReference("creditor").getId(),
                                    account.getUser(),
                                    documentSnapshot.getString("transName"),
                                    documentSnapshot.getDouble("amount"),
                                    documentSnapshot.getDate("epochIssued")
                            );
                            Executors.newSingleThreadExecutor().execute(() -> {
                                transaction.getCreditorID().getUser();
                            });
                            transactionList.add(transaction);
                            transactionList.sort(new Comparator<Transaction>() {
                                @Override
                                public int compare(Transaction o1, Transaction o2) {
                                    return o2.getEpochIssued().compareTo(o1.getEpochIssued());
                                }
                            });
                        }
                        System.out.println("Transaction size: " + transactionList.size());
                    }
                    else{
                        System.out.println("Transaction snapshot is empty");
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                }
            }
        });
    }

    public static void getAccountDebts(Account account, List<Transaction> transactionList){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Definition of debt:
        // 1. debtor field is associated with current user
        // 2. transStatus value is PAYMENT_REQUEST & PAYMENT_NOT_MADE

        Query query = firebaseFirestore.collection("transaction")
                .whereEqualTo("debtor", firebaseFirestore.collection("user").document(account.getUser().getUserID()))
                .whereIn("transStatus", Arrays.asList("PAYMENT_NOT_MADE", "PAYMENT_REQUEST"));
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            Transaction transaction = new Transaction(
                                    documentSnapshot.getString("transID"),
                                    TransactionStatus.getTransType(documentSnapshot.getString("transStatus")),
                                    documentSnapshot.getString("groupID"),
                                    documentSnapshot.getString("groupName"),
                                    documentSnapshot.getDocumentReference("creditor").getId(),
                                    account.getUser(),
                                    documentSnapshot.getString("transName"),
                                    documentSnapshot.getDouble("amount"),
                                    documentSnapshot.getDate("epochIssued")
                            );
                            Executors.newSingleThreadExecutor().execute(() -> {
                                transaction.getCreditorID().getUser();
                                if(transaction.getTransStatus().getType().equalsIgnoreCase(TransactionStatus.PAYMENT_REQUEST.getType())){
                                    transaction.setPayProof(new Picture(documentSnapshot.getString("payProof")));
                                    transaction.getPayProof().getPictureFromDatabase();
                                }
                            });
                            transactionList.add(transaction);
                            System.out.println("List of debts: " + transactionList.size());
                        }
                    }
                    else{
                        System.out.println("Account has no debts");
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                }
            }
        });
    }

    public static void getAccountCredits(Account account, List<Transaction> transactionList){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Definition of credit:
        // 1. creditor field is associated with current user
        // 2. transStatus value is PAYMENT_REQUEST & PAYMENT_NOT_MADE

        Query query = firebaseFirestore.collection("transaction")
                .whereEqualTo("creditor", firebaseFirestore.collection("user").document(account.getUser().getUserID()))
                .whereIn("transStatus", Arrays.asList("PAYMENT_NOT_MADE", "PAYMENT_REQUEST"));
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(int i = 0; i < queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            Transaction transaction = new Transaction(
                                    documentSnapshot.getString("transID"),
                                    TransactionStatus.getTransType(documentSnapshot.getString("transStatus")),
                                    documentSnapshot.getString("groupID"),
                                    documentSnapshot.getString("groupName"),
                                    account.getUser(),
                                    documentSnapshot.getDocumentReference("debtor").getId(),
                                    documentSnapshot.getString("transName"),
                                    documentSnapshot.getDouble("amount"),
                                    documentSnapshot.getDate("epochIssued")
                            );
                            Executors.newSingleThreadExecutor().execute(() -> {
                                transaction.getDebtorID().getUser();
                                if(transaction.getTransStatus().getType().equalsIgnoreCase(TransactionStatus.PAYMENT_REQUEST.getType())){
                                    transaction.setPayProof(new Picture(documentSnapshot.getString("payProof")));
                                    transaction.getPayProof().getPictureFromDatabase();
                                }
                            });
                            transactionList.add(transaction);
                        }
                        System.out.println("List of credits: " + transactionList.size());
                    }
                    else{
                        System.out.println("Account has no credits");
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                }
            }
        });
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public TransactionStatus getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(TransactionStatus transStatus) {
        this.transStatus = transStatus;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public User getDebtorID() {
        return debtorID;
    }

    public void setDebtorID(User debtorID) {
        this.debtorID = debtorID;
    }

    public User getCreditorID() {
        return creditorID;
    }

    public void setCreditorID(User creditorID) {
        this.creditorID = creditorID;
    }

    public void setEpochIssued(Date epochIssued) {
        this.epochIssued = epochIssued;
    }

    public Date getEpochIssued() {
        return epochIssued;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Picture getPayProof() {
        return payProof;
    }

    public void setPayProof(Picture payProof) {
        this.payProof = payProof;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
