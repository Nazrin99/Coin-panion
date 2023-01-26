package com.example.coin_panion.classes.transaction;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction {
    private String transID;
    private TransactionType transType;
    private String groupID;
    private User creditorID;
    private User debtorID;
    private String transName;
    private Double amount;
    private Date epochIssued;

    // Default constructor
    public Transaction(){

    }

    public Transaction(String transID, TransactionType transType, String groupID, User creditorID, User debtorID, String transName, Double amount, Date epochIssued) {
        this.transID = transID;
        this.transType = transType;
        this.groupID = groupID;
        this.creditorID = creditorID;
        this.debtorID = debtorID;
        this.transName = transName;
        this.amount = amount;
        this.epochIssued = epochIssued;
    }

    public Transaction(String transID, TransactionType transType, String groupID, User creditorID, String debtorID, String transName, Double amount, Date epochIssued) {
        this.transID = transID;
        this.transType = transType;
        this.groupID = groupID;
        this.creditorID = creditorID;
        this.transName = transName;
        this.amount = amount;
        this.epochIssued = epochIssued;
        this.debtorID = new User(debtorID);
    }

    public Transaction(String transID, TransactionType transType, String groupID, String creditorID, User debtorID, String transName, Double amount, Date epochIssued) {
        this.transID = transID;
        this.transType = transType;
        this.groupID = groupID;
        this.creditorID = new User(creditorID);
        this.transName = transName;
        this.amount = amount;
        this.epochIssued = epochIssued;
        this.debtorID = debtorID;
    }

    public static int getListOfTransaction(String groupID, List<Transaction> transactionList, Account account){
        AtomicReference<Integer> integerAtomicReference = new AtomicReference<>(-1);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("transaction").whereEqualTo("groupID", groupID);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                integerAtomicReference.set(documentSnapshots.size());
                for(int i = 0; i < documentSnapshots.size(); i++){
                    DocumentSnapshot documentSnapshot = documentSnapshots.get(i);
                    Transaction transaction = new Transaction(
                            documentSnapshot.getString("transID"),
                            TransactionType.getTransType(Objects.requireNonNull(documentSnapshot.getString("transType"))),
                            documentSnapshot.getString("groupID"),
                            account.getUser(),
                            documentSnapshot.getDocumentReference("debtor").getId(),
                            documentSnapshot.getString("transName"),
                            documentSnapshot.getDouble("amount"),
                            documentSnapshot.getDate("epochIssued")
                    );
                    transactionList.add(transaction);
                    transactionList.sort(new Comparator<Transaction>() {
                        @Override
                        public int compare(Transaction o1, Transaction o2) {
                            return o2.getEpochIssued().compareTo(o1.getEpochIssued());
                        }
                    });
                    Executors.newSingleThreadExecutor().execute(() -> {
                        for(int j = 0; j < account.getFriends().size(); j++){
                            if(transaction.getDebtorID().getUserID().equalsIgnoreCase(account.getFriends().get(j).getUser().getUserID())){
                                transaction.setDebtorID(account.getFriends().get(j).getUser());
                                break;
                            }
                        }
                        if(transaction.getDebtorID().getFirstName() == null || transaction.getDebtorID().getFirstName().isEmpty()){
                            transaction.getDebtorID().getUser();
                        }
                    });
                }
            }
        });
        while(integerAtomicReference.get()  < 0){

        }
        return integerAtomicReference.get();
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public TransactionType getTransType() {
        return transType;
    }

    public void setTransType(TransactionType transType) {
        this.transType = transType;
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

}
