package com.example.coin_panion.classes.group;


import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.transaction.Transaction;
import com.example.coin_panion.classes.utility.Picture;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ALL")
public class Group implements Serializable {
    private String groupID;
    private String groupName;
    private String groupDesc;
    private String groupType;
    private List<Account> groupMembers = new ArrayList<>();
    private List<Transaction> groupTransactions;
    private Picture groupPic = null; // TODO
    private Picture groupCover = null; // TODO

    // Default constructor
    public Group(){

    }

    public Group(String groupID, String groupName, String groupDesc, String groupType, String groupPic, String groupCover) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.groupType = groupType;
        this.groupPic = new Picture(groupPic);
        this.groupCover = new Picture(groupCover);
    }

    public static void insertGroupIntoDatabase(Group group){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void getGroupMembersFromDatabase(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String groupID = this.getGroupID();
        DocumentReference groupReference = firebaseFirestore.collection("group").document(groupID);

        Query query = firebaseFirestore.collection("account_group").whereEqualTo("groupID", groupReference);
        AtomicReference<List<String>> listAtomicReference = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> groupMemberAccountIDs = new ArrayList<>();
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++){
                            groupMemberAccountIDs.add(queryDocumentSnapshots.getDocuments().get(i).getDocumentReference("accountID").getId());
                        }
                        listAtomicReference.set(groupMemberAccountIDs);
                    }
                    else{
                        System.out.println("Snapshot is empty");
                    }
                }
                else{
                    System.out.println("Snapshot is empty");
                }
            }
        });
        while(listAtomicReference.get() == null){

        }
        List<String> groupMemberAccountIDs = listAtomicReference.get();

        // Get the Accounts now
        List<Account> groupMembers = new ArrayList<>();
        for(int i = 0; i < groupMemberAccountIDs.size(); i++){
            groupMembers.add(Account.getAccount(groupMemberAccountIDs.get(i)));
        }
        while(groupMembers.size() < groupMemberAccountIDs.size()){

        }
        this.setGroupMembers(groupMembers);
    }

    public static List<Group> getGroups(String accountID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        List<DocumentReference> groupReferences = getGroupReferences(accountID);
        System.out.println("References gotten "+ groupReferences.size());

        List<Group> groups = new ArrayList<>();
        for(int i = 0; i < groupReferences.size(); i++){
            groupReferences.get(i).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String groupID = documentSnapshot.getString("groupID");
                    String groupName = documentSnapshot.getString("groupName");
                    String groupDesc = documentSnapshot.getString("groupDesc");
                    String groupType = documentSnapshot.getString("groupType");
                    String groupPic = documentSnapshot.getString("groupPic");
                    String groupCover = documentSnapshot.getString("groupCover");

                    Group group = new Group(groupID, groupName, groupDesc, groupType, groupPic, groupCover);
                    groups.add(group);
                }
            });
        }
        while(groups.size() < groupReferences.size()){

        }
        Executors.newSingleThreadExecutor().execute(() -> {
            for(int i = 0; i < groups.size(); i++) {
                groups.get(i).getGroupPic().getPictureFromDatabase();
                groups.get(i).getGroupCover().getPictureFromDatabase();
            }
        });
        return groups;
    }

    public static List<DocumentReference> getGroupReferences(String accountID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference accountReference = firebaseFirestore.collection("account").document(accountID);
        Query query = firebaseFirestore.collection("account_group").whereEqualTo("accountID", accountReference);

        AtomicReference<List<DocumentReference>> listAtomicReference = new AtomicReference<>(null);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentReference> documentReferences = new ArrayList<>();
                if(queryDocumentSnapshots != null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            documentReferences.add(documentSnapshot.getDocumentReference("groupID"));
                        }
                        listAtomicReference.set(documentReferences);
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
        return listAtomicReference.get();
    }

    /**
     * Retrieve the group data from database based on the groupID, returns a Group object
     * @param groupID
     * @param dataThread
     * @return Group object from database based on groupID
     */

    /**
     * Retrieve list of groups joined by a account
     * @param userID
     * @param dataThread
     * @return
     */

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    /**
     * Deletes the group from the database
     * @param groupID
     * @param dataThread
     */

    // Getters & Setters


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<Account> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Account> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<Transaction> getGroupTransactions() {
        return groupTransactions;
    }

    public void setGroupTransactions(List<Transaction> groupTransactions) {
        this.groupTransactions = groupTransactions;
    }

    public Picture getGroupPic() {
        return groupPic;
    }

    public void setGroupPic(Picture groupPic) {
        this.groupPic = groupPic;
    }

    public Picture getGroupCover() {
        return groupCover;
    }

    public void setGroupCover(Picture groupCover) {
        this.groupCover = groupCover;
    }


}
