package com.example.coin_panion.classes.settleUp;

import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.transaction.TransactionStatus;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class PaymentRequest {

    private String transactionID;
    private Long epochPaymentIssued;
    private Long epochPaymentApproved;
    private Picture paymentProof;
    private PaymentRequestStatus paymentRequestStatus;

    public PaymentRequest(String transactionID, Long epochPaymentIssued, Long epochPaymentApproved, Picture paymentProof, PaymentRequestStatus paymentRequestStatus) {
        this.transactionID = transactionID;
        this.epochPaymentIssued = epochPaymentIssued;
        this.epochPaymentApproved = epochPaymentApproved;
        this.paymentProof = paymentProof;
        this.paymentRequestStatus = paymentRequestStatus;
    }

    public static void getDebts(Account account){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        AtomicReference<List<String>> listAtomicReference = new AtomicReference<>(null);
        AtomicReference<List<String>> requiredTransactionIDs = new AtomicReference<>(null);
        DocumentReference userReference = firebaseFirestore.collection("user").document(account.getUser().getUserID());


        Query query = firebaseFirestore.collection("transaction")
                .whereEqualTo("debtor", userReference);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<String> transactionIDs = new ArrayList<>();
                        for(int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++){
                            transactionIDs.add(queryDocumentSnapshots.getDocuments().get(i).getString("transID"));
                        }
                        System.out.println();
                        listAtomicReference.set(transactionIDs);
                    }
                    else{
                        System.out.println("Snapshot is empty");
                        listAtomicReference.set(new ArrayList<>());
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                    listAtomicReference.set(new ArrayList<>());
                }
            }
        });

        while(listAtomicReference.get() == null){
        }
        System.out.println("My transaction size: " + listAtomicReference.get().size());
        System.out.println(listAtomicReference.get().get(0));

        Query query1 = firebaseFirestore.collection("payment_request")
                .whereIn("transID", listAtomicReference.get())
                .whereEqualTo("payReqStatus", "PAYMENT_NOT_MADE");
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<String> transIDs = new ArrayList<>();
                        for(int i = 0 ; i < queryDocumentSnapshots.getDocuments().size(); i++){
                            transIDs.add(queryDocumentSnapshots.getDocuments().get(i).getString("transID"));
                        }
                        requiredTransactionIDs.set(transIDs);
                    }
                    else{
                        System.out.println("Snapshot is empty");
                        requiredTransactionIDs.set(new ArrayList<>());
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                    requiredTransactionIDs.set(new ArrayList<>());
                }
            }
        });

        while(requiredTransactionIDs.get() == null){

        }

        System.out.println("My required transaction size: " + requiredTransactionIDs.get().size());

        Query query2 = firebaseFirestore.collection("transaction")
                .whereIn("transID", requiredTransactionIDs.get());
        query2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<Transaction> debts = new ArrayList<>();
                        for(int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            Transaction transaction = new Transaction(
                                    documentSnapshot.getString("transID"),
                                    TransactionStatus.getTransType(documentSnapshot.getString("transType")),
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
                            debts.add(transaction);
                        }
                        System.out.println("Number of debts: " + debts.size());
                        account.setDebts(debts);
                    }
                    else{
                        System.out.println("Snapshot is empty");
                        account.setDebts(new ArrayList<>());
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                    account.setDebts(new ArrayList<>());
                }
            }
        });
    }

    // Getters and setters


    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
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
