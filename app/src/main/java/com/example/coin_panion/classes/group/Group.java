package com.example.coin_panion.classes.group;


import android.content.ContentResolver;
import android.net.Uri;

import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ALL")
public class Group {
    private int groupID;
    private String groupName;
    private String groupDesc;
    private String groupType;
    private List<Integer> groupMembers = new ArrayList<>();
    private List<Integer> groupTransactions = new ArrayList<>();
    private Picture groupPic = Picture.getPictureFromDB(2501);
    private Picture groupCover = Picture.getPictureFromDB(2500);

    // Default constructor
    public Group(){

    }

    // Constructor to create new group (group pic not selected, use default)
    public Group(String groupName, String groupDesc, String groupType, List<Integer> groupMembers) {
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
    }

    // Constructor to create new group (group pic selected)
    public Group(String groupName, String groupDesc, String groupType, List<Integer> groupMembers, List<Integer> groupTransactions, Uri groupPicUri, ContentResolver contentResolver, Thread dataThread) {
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
        this.groupTransactions = groupTransactions;
        this.groupPic = Picture.insertPicIntoDB(groupPicUri, contentResolver, dataThread);
    }

    // Constructor to load existing group (complete parameters)
    public Group(Integer groupID, String groupName, String groupDesc, String groupType, List<Integer> groupMembers, List<Integer> groupTransactions, Integer groupPicID, Integer groupCoverID) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
        this.groupTransactions = groupTransactions;
        this.groupPic = Picture.getPictureFromDB(groupPicID);
        this.groupCover = Picture.getPictureFromDB(groupCoverID);
    }

    /**
     * Insert new group into database, returns the original Group object with extra parameter groupID value
     * @param newGroup
     * @param dataThread
     * @return Group object with updated groupID info
     */
    public static Group insertNewGroupToDB(Group newGroup, Thread dataThread){
        AtomicReference<Group> groupAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO group_table(group_name, group_type, group_desc, group_pic, group_cover) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, newGroup.getGroupName());
                preparedStatement.setString(2, newGroup.getGroupType());
                preparedStatement.setString(3, newGroup.getGroupDesc());
                preparedStatement.setInt(4, 2500);
                preparedStatement.setInt(5, 2501);
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();

                newGroup.setGroupID(resultSet.getInt(1));
                groupAtomicReference.set(newGroup);

                // Insert members userID and groupID into bridge entity account_group
                PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO account_group VALUES(?, ?)");
                preparedStatement1.setInt(2, newGroup.getGroupID());
                for(int i = 0; i < newGroup.getGroupMembers().size(); i++){
                    preparedStatement1.setInt(1, newGroup.getGroupMembers().get(i));
                    preparedStatement1.addBatch();
                }
                preparedStatement1.executeBatch();
                preparedStatement1.close();
                preparedStatement1.close();
                resultSet.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        ThreadStatic.run(dataThread);
        return groupAtomicReference.get();
    }

    /**
     * Retrieve the group data from database based on the groupID, returns a Group object
     * @param groupID
     * @param dataThread
     * @return Group object from database based on groupID
     */
    public static Group retrieveGroupFromDB(Integer groupID, Thread dataThread){
        AtomicReference<Group> retrievedGroup = new AtomicReference<>();
        dataThread = new Thread(() ->{
            // Getting the list of members in the group
            Connection connection = Line.getConnection();
            List<Integer> userIDs = new ArrayList<>();
            List<Integer> transactionIDs = new ArrayList<>();
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT account_id FROM account_group WHERE group_id = ?");
                preparedStatement.setInt(1, groupID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    userIDs.add(resultSet.getInt(1));
                }
                // Already obtained members, next get the list of activities
                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT transaction_id FROM transaction WHERE group_id = ?");
                preparedStatement1.setInt(1, groupID);
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                while (resultSet1.next()){
                    transactionIDs.add(resultSet.getInt(1));
                }
                // Already get transactions, next get the group info
                PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM group_table WHERE group_id = ?");
                preparedStatement2.setInt(1, groupID);
                ResultSet resultSet2 = preparedStatement2.executeQuery();

                // Construct group object, use complete constructor parameters
                while(resultSet2.next()){
                    retrievedGroup.set(new Group(groupID, resultSet2.getString(2), resultSet2.getString(4), resultSet2.getString(3), userIDs, transactionIDs, resultSet2.getInt(5), resultSet2.getInt(6)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return retrievedGroup.get();
    }

    public static List<Group> retrieveGroupFromDB(List<Integer> groupID, Thread dataThread){
        List<Group> group = new ArrayList<>();
        for(int i = 0; i < groupID.size(); i++){
            group.add(retrieveGroupFromDB(groupID.get(i), new Thread()));
        }
        return group;
    }

    /**
     * Retrieve list of groups joined by a account
     * @param userID
     * @param dataThread
     * @return
     */
    public static List<Integer> retrieveGroupIDsFromDB(Integer accountID, Thread dataThread){
        AtomicReference<List<Integer>> listAtomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            List<Integer> groupIDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM account_group WHERE account_id = ?")){
                preparedStatement.setInt(1, accountID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    groupIDs.add(resultSet.getInt(2));
                }
                listAtomicReference.set(groupIDs);
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

    /**
     * Deletes the group from the database
     * @param groupID
     * @param dataThread
     */
    public static void deleteGroupFromDB(Integer groupID, Thread dataThread){
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ? WHERE group_id = ?");
            ){
                preparedStatement.setInt(2, groupID);
                preparedStatement.setString(1, "group_table");
                preparedStatement.execute();
                preparedStatement.setString(1, "account_group");
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    public static List<User> retrieveGroupParticipants(Integer groupID, Thread dataThread){
        AtomicReference<List<User>> listAtomicReference = new AtomicReference<>(new ArrayList<>());
        dataThread = new Thread(() -> {
            List<Integer> userIDs = new ArrayList<>();
            List<Integer> accountIDs = new ArrayList<>();
            List<User> users = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT account_id FROM account_group WHERE group_id = ?");
                    ){
                preparedStatement.setInt(1, groupID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    accountIDs.add(resultSet.getInt(1));
                }
                resultSet.close();

                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT user_id FROM account WHERE " + User.getQueryString("account_id", accountIDs.size()));
                for(int i = 0; i < accountIDs.size(); i++){
                    preparedStatement1.setInt((i+1), accountIDs.get(i));
                }
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while(resultSet1.next()){
                    userIDs.add(resultSet1.getInt(1));
                }
                resultSet1.close();

                PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM user WHERE " + User.getQueryString("user_id", userIDs.size()));
                for(int i = 0; i < userIDs.size(); i++){
                    preparedStatement2.setInt((i+1), userIDs.get(i));
                }
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                while(resultSet2.next()){
                    users.add(new User(resultSet2.getInt(1), resultSet2.getLong(2), resultSet2.getString(4), resultSet2.getString(5), resultSet2.getString(6), resultSet2.getString(3)));
                }
                resultSet2.close();
                listAtomicReference.set(users);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return listAtomicReference.get();
    }

    public static void removeUsersFromGroup(List<User> users, Integer groupID, Thread dataThread){
        dataThread = new Thread(() -> {
            List<Integer> accountIDs = new ArrayList<>();
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT account_id FROM account WHERE " + User.getQueryString("user_id", users.size()))
                    ){
                for(int i = 0; i < users.size(); i++){
                    preparedStatement.setInt((i+1), users.get(i).getUserID());
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    accountIDs.add(resultSet.getInt(1));
                }
                resultSet.close();

                PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM account_group WHERE account_id = ? AND group_id = ?");
                preparedStatement1.setInt(2, groupID);
                for(int i = 0; i < accountIDs.size(); i++) {
                    preparedStatement1.setInt(1, accountIDs.get(i));
                    preparedStatement1.addBatch();
                }
                preparedStatement1.executeBatch();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
    }

    // Getters & Setters
    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

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

    public List<Integer> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Integer> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<Integer> getGroupTransactions() {
        return groupTransactions;
    }

    public void setGroupTransactions(List<Integer> groupTransactions) {
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
