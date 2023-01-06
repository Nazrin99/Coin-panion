package com.example.coin_panion.classes.group;


import android.graphics.drawable.Drawable;

import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Line;
import com.example.coin_panion.classes.utility.Picture;
import com.example.coin_panion.classes.utility.ThreadStatic;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Group {
    private int groupID;
    private String groupName;
    private String group_desc;
    private String groupType;
    private List<String> groupMembers = new ArrayList<>();
    private List<String> groupTransactions
            = new ArrayList<>();
    private Drawable picture;
    private int pictureID;

    // Default constructor
    public Group(){

    }

    // Constructor to create new group (no picture selected)
    public Group(String groupName, String groupDesc, String groupType, List<String> groupMembers, List<String> groupTransactions) {
        this.groupName = groupName;
        this.group_desc = groupDesc;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
        this.groupTransactions = groupTransactions;
    }

    // Constructor to create new group (picture selected)
    public Group(String groupName, String group_desc, String groupType, List<String> groupMembers, List<String> groupTransactions, Drawable picture) {
        this.groupName = groupName;
        this.group_desc = group_desc;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
        this.groupTransactions = groupTransactions;
        this.picture = picture;
    }

    // Constructor to load existing group (complete parameters)
    public Group(int groupID, String groupName, String groupDesc, String groupType, List<String> groupMembers, List<String> groupTransactions, Integer pictureID) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.group_desc = groupDesc;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
        this.groupTransactions = groupTransactions;
        this.pictureID = pictureID;
        this.picture = Picture.constructDrawableFromBlob(Picture.getBlobFromDB(pictureID));
    }

    // Constructor to load existing group (no picture)
    public Group(int groupID, String groupName, String group_desc, String groupType, List<String> groupMembers, List<String> groupTransactions) {
        this(groupID, groupName, group_desc, groupType, groupMembers, groupTransactions, null);
    }

    // Constructor to load existing group (no picture, no activities)
    public Group(int groupID, String groupName, String groupDesc, String groupType, List<String> groupMembers) {
        this(groupID, groupName, groupDesc, groupType, groupMembers, new ArrayList<>(), null);
    }

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

    public String getGroup_desc() {
        return group_desc;
    }

    public void setGroup_desc(String group_desc) {
        this.group_desc = group_desc;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<String> getGroupTransactions() {
        return groupTransactions;
    }

    public void setGroupTransactions(List<String> groupTransactions) {
        this.groupTransactions = groupTransactions;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public void setPictureID(int pictureID) {
        this.pictureID = pictureID;
    }

    // Method to send newly create group info to database, return the ID of the group from the database
    public AtomicInteger addNewGroupToDB(BaseViewModel viewModel, Thread dataThread){
        AtomicInteger groupID = new AtomicInteger(0);
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO group(group_name, group_type, group_desc, group_pic) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, viewModel.get("groupName").toString());
                preparedStatement.setString(2, viewModel.get("groupType").toString());
                preparedStatement.setString(3, viewModel.get("groupDesc").toString());
                preparedStatement.setInt(4, Integer.parseInt(viewModel.get("pictureID").toString()));
                boolean status = preparedStatement.execute();
                if (!status) {
                    // Keys generated, group stored in database, return the ID of the new group
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    resultSet.next();
                    groupID.set(Integer.parseInt(resultSet.getString(1)));

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        dataThread.start();
        while(dataThread.isAlive()){

        }
        return groupID;
    }

    public Group retrieveGroupFromDB(Integer groupID, Thread dataThread){
        AtomicReference<Group> retrievedGroup = new AtomicReference<>();
        dataThread = new Thread(() ->{
            // Getting the list of members in the group
            Connection connection = Line.getConnection();
            List<String> userIDs = new ArrayList<>();
            List<String> transactionIDs = new ArrayList<>();
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT account_id FROM account_group WHERE group_id = ?");
                preparedStatement.setInt(1, groupID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    userIDs.add(resultSet.getString(1));
                }
                // Already obtained userID, next get the list of activities
                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT transaction_id FROM transaction WHERE group_id = ?");
                preparedStatement1.setInt(1, groupID);
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                while (resultSet1.next()){
                    transactionIDs.add(resultSet.getString(1));
                }
                // Already get transactions, next get the group info
                PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM group WHERE group_id = ?");
                preparedStatement2.setInt(1, groupID);
                ResultSet resultSet2 = preparedStatement2.executeQuery();

                while(resultSet2.next()){
                    retrievedGroup.set(new Group(groupID, resultSet2.getString(2), resultSet2.getString(4), resultSet2.getString(3), userIDs, transactionIDs, resultSet2.getInt(5)));
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return retrievedGroup.get();
    }
}
