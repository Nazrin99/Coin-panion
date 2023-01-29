package com.example.coin_panion.classes.general;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.coin_panion.classes.group.Group;
import com.example.coin_panion.classes.notification.Notification;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.Hashing;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import kotlinx.coroutines.internal.AtomicOp;

public class Account implements Serializable {
    private String accountID;
    private User user;
    private String bio;
    private List<Account> friends;
    private List<Account> blockedContacts;
    private List<Group> groups;
    private Double debtLimitAmount;
    private Date debtLimitEndDate;
    private String settleUpAccountName;
    private String settleUpAccountNumber;
    private List<Transaction> debts;
    private List<Transaction> credits;
    private List<Notification> notifications = new ArrayList<>();
    private Picture accountPic;
    private Picture accountCover;
    private Picture qrImage;

    public Account() {
    }

    /**
     * Create complete Account objects without friends and notifications
     * @param accountID
     * @param user
     * @param bio
     * @param debtLimitAmount
     * @param debtLimitEndDate
     * @param settleUpAccountName
     * @param settleUpAccountNumber
     * @param accountPic
     * @param accountCover
     * @param qrImage
     */
    public Account(String accountID, User user, String bio, Double debtLimitAmount, Date debtLimitEndDate, String settleUpAccountName, String settleUpAccountNumber, Picture accountPic, Picture accountCover, Picture qrImage) {
        this.accountID = accountID;
        this.user = user;
        this.bio = bio;
        this.debtLimitAmount = debtLimitAmount;
        this.debtLimitEndDate = debtLimitEndDate;
        this.settleUpAccountName = settleUpAccountName;
        this.settleUpAccountNumber = settleUpAccountNumber;
        this.accountPic = accountPic;
        this.accountCover = accountCover;
        this.qrImage = qrImage;
    }



    public static List<Account> getListOfFriends(String accountID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("friend").whereEqualTo("accountID", accountID).whereEqualTo("blocked", false);

        AtomicReference<List<String>> listAtomicReference1 = new AtomicReference<>(null);
        Executors.newSingleThreadExecutor().execute(() -> {
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots != null){
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<String> accountIDs = new ArrayList<>();
                            for(int i = 0 ; i < queryDocumentSnapshots.size(); i++){
                                accountIDs.add(queryDocumentSnapshots.getDocuments().get(i).getDocumentReference("friendAccountID").getId());
                            }
                            listAtomicReference1.set(accountIDs);
                        }
                        else{
                            System.out.println("Snapshot empty");
                            listAtomicReference1.set(new ArrayList<>());
                        }
                    }
                    else{
                        System.out.println("Snapshot is null");
                        listAtomicReference1.set(new ArrayList<>());
                    }
                }
            });
        });
        while(listAtomicReference1.get() == null){
        }

        List<Account> accounts = new ArrayList<>();
        List<String> accountIDs = listAtomicReference1.get();
        for(int i = 0; i < accountIDs.size(); i++) {
            accounts.add(Account.getAccount(accountIDs.get(i)));
        }

        return accounts;
    }

    public static List<Account> getBlockedFriends(String accountID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("friend").whereEqualTo("accountID", accountID).whereEqualTo("blocked", true);

        AtomicReference<List<String>> listAtomicReference1 = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<String> accountIDs = new ArrayList<>();
                        for(int i = 0 ; i < queryDocumentSnapshots.size(); i++){
                            accountIDs.add(queryDocumentSnapshots.getDocuments().get(i).getDocumentReference("friendAccountID").getId());
                        }
                        listAtomicReference1.set(accountIDs);
                    }
                    else{
                        System.out.println("Snapshot empty");
                        listAtomicReference1.set(new ArrayList<>());
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                    listAtomicReference1.set(new ArrayList<>());
                }
            }
        });
        while(listAtomicReference1.get() == null){
        }
        List<Account> accounts = new ArrayList<>();
        List<String> accountIDs = listAtomicReference1.get();
        for(int i = 0; i < accountIDs.size(); i++) {
            accounts.add(Account.getAccount(accountIDs.get(i)));
        }

        return accounts;
    }


    public static Account getAccount(DocumentReference userReference){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("account").whereEqualTo("user", userReference);

        AtomicReference<Account> accountAtomicReference = new AtomicReference<>(null);
        AtomicReference<DocumentReference> documentReferenceAtomicReference = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Sending references for creating later
                        DocumentReference documentReference = documentSnapshot.getDocumentReference("user");
                        documentReferenceAtomicReference.set(documentReference);

                        String accountID1 = documentSnapshot.getString("accountID");
                        Picture accountPic = new Picture(documentSnapshot.getString("accountPic"));
                        Picture accountCover = new Picture(documentSnapshot.getString("accountCover"));
                        Picture qrImage = new Picture(documentSnapshot.getString("qrImage"));
                        String bio = documentSnapshot.getString("bio");
                        Double debtLimitAmount = documentSnapshot.getDouble("debtLimitAmount");
                        Date debtLimitEndDate = documentSnapshot.getDate("debtLimitEndDate");
                        String settleUpAccountName = documentSnapshot.getString("settleUpAccountName");
                        String settleUpAccountNumber = documentSnapshot.getString("settleUpAccountNumber");

                        Account account = new Account(accountID1, null, bio, debtLimitAmount, debtLimitEndDate, settleUpAccountName, settleUpAccountNumber, accountPic, accountCover, qrImage);
                        accountAtomicReference.set(account);
                        System.out.println("Leaving");
                    }
                    else{
                        System.out.println("Snapshot is empty");
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                }
            }
        });
        while(accountAtomicReference.get() == null || documentReferenceAtomicReference.get() == null){

        }
        Account account = accountAtomicReference.get();

        // Getting user object
        User user = User.getUser(documentReferenceAtomicReference.get());

        // Getting the pictures
        Executors.newSingleThreadExecutor().execute(() -> {
            Picture accountPic = account.getAccountPic();
            Picture accountCover = account.getAccountCover();
            Picture qrImage = account.getQrImage();
            accountPic.getPictureFromDatabase();
            accountCover.getPictureFromDatabase();
            qrImage.getPictureFromDatabase();
        });
        account.setUser(user);

        return account;
    }

    public static Account getAccount(String accountID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("account").whereEqualTo("accountID", accountID);

        AtomicReference<Account> accountAtomicReference = new AtomicReference<>(null);
        AtomicReference<DocumentReference> documentReferenceAtomicReference = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Sending references for creating later
                        DocumentReference documentReference = documentSnapshot.getDocumentReference("user");
                        documentReferenceAtomicReference.set(documentReference);

                        String accountID1 = documentSnapshot.getString("accountID");
                        Picture accountPic = new Picture(documentSnapshot.getString("accountPic"));
                        Picture accountCover = new Picture(documentSnapshot.getString("accountCover"));
                        Picture qrImage = new Picture(documentSnapshot.getString("qrImage"));
                        String bio = documentSnapshot.getString("bio");
                        Double debtLimitAmount = documentSnapshot.getDouble("debtLimitAmount");
                        Date debtLimitEndDate = documentSnapshot.getDate("debtLimitEndDate");
                        String settleUpAccountName = documentSnapshot.getString("settleUpAccountName");
                        String settleUpAccountNumber = documentSnapshot.getString("settleUpAccountNumber");

                        Account account = new Account(accountID1, null, bio, debtLimitAmount, debtLimitEndDate, settleUpAccountName, settleUpAccountNumber, accountPic, accountCover, qrImage);
                        accountAtomicReference.set(account);
                        System.out.println("Leaving");
                    }
                    else{
                        System.out.println("Snapshot is empty");
                    }
                }
                else{
                    System.out.println("Snapshot is null");
                }
            }
        });
        while(accountAtomicReference.get() == null || documentReferenceAtomicReference.get() == null){

        }
        Account account = accountAtomicReference.get();

        // Getting user object
        User user = User.getUser(documentReferenceAtomicReference.get());

        // Getting the pictures
        Executors.newSingleThreadExecutor().execute(() -> {
            Picture accountPic = account.getAccountPic();
            Picture accountCover = account.getAccountCover();
            Picture qrImage = account.getQrImage();
            accountPic.getPictureFromDatabase();
            accountCover.getPictureFromDatabase();
            qrImage.getPictureFromDatabase();
        });
        account.setUser(user);

        return account;
    }

    public static Account getAccountFromPhoneNumber(String phoneNumber){
        long start = System.currentTimeMillis();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        AtomicReference<DocumentReference> documentReferenceAtomicReference = new AtomicReference<>(null);
        AtomicReference<User> userAtomicReference = new AtomicReference<>(null);
        Query query = firebaseFirestore.collection("user").whereEqualTo("phoneNumber", phoneNumber);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    documentReferenceAtomicReference.set(documentSnapshot.getReference());
                    String userID = documentSnapshot.getString("userID");
                    String phoneNumber = documentSnapshot.getString("phoneNumber");
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String username = documentSnapshot.getString("username");
                    String email = documentSnapshot.getString("email");
                    String password = documentSnapshot.getString("password");
                    User user = new User(userID, phoneNumber, firstName, lastName, username, email, password);
                    userAtomicReference.set(user);
                }
            }
        });
        while(System.currentTimeMillis() >= start + 3000L){

        }
        if(userAtomicReference.get() == null){
            return null;
        }

        AtomicReference<Account> accountAtomicReference = new AtomicReference<>(null);
        Query query1 = firebaseFirestore.collection("account").whereEqualTo("user", documentReferenceAtomicReference.get());
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    String accountID = documentSnapshot.getString("accountID");
                    String bio = documentSnapshot.getString("bio");
                    Double debtLimitAmount = documentSnapshot.getDouble("debtLimitAmount");
                    Date debtLimitEndDate = documentSnapshot.getDate("debtLimitEndDate");
                    String settleUpAccountName = documentSnapshot.getString("settleUpAccountName");
                    String settleUpAccountNumber = documentSnapshot.getString("settleUpAccountNumber");
                    Picture accountPic = new Picture(documentSnapshot.getString("accountPic"));
                    Picture accountCover = new Picture(documentSnapshot.getString("accountCover"));
                    Picture qrImage = new Picture(documentSnapshot.getString("qrImage"));

                    Account account = new Account(accountID, userAtomicReference.get(), bio, debtLimitAmount, debtLimitEndDate, settleUpAccountName, settleUpAccountNumber, accountPic, accountCover, qrImage);
                    accountAtomicReference.set(account);
                    System.out.println("Pass by here");
                }
                else{
                    System.out.println("Snapshot is empty");
                }
            }
        });
        while(accountAtomicReference.get() == null){

        }
        System.out.println("Account created");
        Account account = accountAtomicReference.get();
        Picture accountPic = account.getAccountPic();
        Picture accountCover = account.getAccountCover();
        Picture qrImage = account.getQrImage();
        accountPic.getPictureFromDatabase();
        accountCover.getPictureFromDatabase();
        qrImage.getPictureFromDatabase();


        while(accountPic.getPicture() == null || accountCover.getPicture() == null || qrImage.getPicture() == null){

        }
        return account;

    }

    public static void changePassword( Integer accountID, String password, Thread dataThread){
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE account SET password = ? WHERE account_id = ?")
                    ){
                preparedStatement.setString(1, password);
                preparedStatement.setInt(2, accountID);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    public static void insertNewAccount(Account account){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference accountDocument = firebaseFirestore.collection("account").document(account.getAccountID());
        DocumentReference userDocument = firebaseFirestore.collection("users").document(account.getUser().getUserID());

        Map<String, Object> accountDetails = new HashMap<>();
        accountDetails.put("accountPic", account.getAccountPic().getFirebaseReference());
        accountDetails.put("accountID", account.getAccountID());
        accountDetails.put("accountCover", account.getAccountCover().getFirebaseReference());
        accountDetails.put("bio", account.getBio());
        accountDetails.put("user", userDocument);
        accountDetails.put("debtLimitAmount", null);
        accountDetails.put("debtLimitEndDate", null);

        accountDocument.set(accountDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Account successfully added");
            }
        });
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Account> getFriends() {
        return friends;
    }

    public void setFriends(List<Account> friends) {
        this.friends = friends;
    }

    public Double getDebtLimitAmount() {
        return debtLimitAmount;
    }

    public void setDebtLimitAmount(Double debtLimitAmount) {
        this.debtLimitAmount = debtLimitAmount;
    }

    public Date getDebtLimitEndDate() {
        return debtLimitEndDate;
    }

    public void setDebtLimitEndDate(Date debtLimitEndDate) {
        this.debtLimitEndDate = debtLimitEndDate;
    }

    public String getSettleUpAccountName() {
        return settleUpAccountName;
    }

    public void setSettleUpAccountName(String settleUpAccountName) {
        this.settleUpAccountName = settleUpAccountName;
    }

    public String getSettleUpAccountNumber() {
        return settleUpAccountNumber;
    }

    public void setSettleUpAccountNumber(String settleUpAccountNumber) {
        this.settleUpAccountNumber = settleUpAccountNumber;
    }

    public Picture getQrImage() {
        return qrImage;
    }

    public void setQrImage(Picture qrImage) {
        this.qrImage = qrImage;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Picture getAccountPic() {
        return accountPic;
    }

    public void setAccountPic(Picture accountPic) {
        this.accountPic = accountPic;
    }

    public Picture getAccountCover() {
        return accountCover;
    }

    public void setAccountCover(Picture accountCover) {
        this.accountCover = accountCover;
    }

    public List<Account> getBlockedContacts() {
        return blockedContacts;
    }

    public void setBlockedContacts(List<Account> blockedContacts) {
        this.blockedContacts = blockedContacts;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Transaction> getDebts() {
        return debts;
    }

    public List<Transaction> getCredits() {
        return credits;
    }

    public void setCredits(List<Transaction> credits) {
        this.credits = credits;
    }

    public void setDebts(List<Transaction> debts) {
        this.debts = debts;
    }
}