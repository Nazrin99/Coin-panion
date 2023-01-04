package com.example.coin_panion.Group;

import com.example.coin_panion.Friends.Friend;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {

    private Integer groupID;
    private Integer creatorID;
    private String groupDescription;
    private Map<Integer,Friend> groupMembers;
    private Blob groupProfilePic;
    private Blob groupBackgroundPic;
    private ArrayList<String> groupActivity;

    public Group(Integer groupID, Integer creatorID, String groupDescription, Blob groupProfilePic, Blob groupBackgroundPic) {
        this.groupID = groupID;
        this.creatorID = creatorID;
        this.groupDescription = groupDescription;
        this.groupMembers = new HashMap<>();
        this.groupProfilePic = groupProfilePic;
        this.groupBackgroundPic = groupBackgroundPic;
        this.groupActivity = new ArrayList<>();
    }

    // Group description will have | to seperate each section
    public void addGroupExpanses(String groupDescription, String amountToPay, String creditor, String splitMethod){
        this.groupDescription = groupDescription + "|" + amountToPay + "|" + creditor + "|" + splitMethod;
    }

    // Get the latest group description
    public String getGroupDescription(){
        String[] groupDesc = this.groupDescription.split("|");
        return groupDesc[0] + "\n" + groupDesc[1] + "\n" + groupDesc[2] + "\n" + groupDesc[3] + "\n";
    }

    //add friend into group
    public Boolean addGroupMembers(Integer friendID, Friend friendToAdd){
        if(!groupMembers.containsKey(friendID)){
            groupMembers.put(friendID,friendToAdd);
            return true;
        }else{
            System.out.println("Friend already added into group");
        }
        return false;
    }

    //remove friend from group
    public Boolean removeGroupMembers(Integer friendID){
        if(groupMembers.containsKey(friendID)){
            groupMembers.remove(friendID);
            return true;
        }
        return false;
    }

    public Map<Integer, Friend> getGroupMembers() {
        return groupMembers;
    }

    public Blob getGroupBackgroundPic() {
        return groupBackgroundPic;
    }

    public void setGroupBackgroundPic(Blob groupBackgroundPic) {
        this.groupBackgroundPic = groupBackgroundPic;
    }

    public Blob getGroupProfilePic() {
        return groupProfilePic;
    }

    public void setGroupProfilePic(Blob groupProfilePic) {
        this.groupProfilePic = groupProfilePic;
    }

    public ArrayList<String> getGroupActivity() {
        return groupActivity;
    }

    public void setGroupActivity(ArrayList<String> groupActivity) {
        this.groupActivity = groupActivity;
    }
}
