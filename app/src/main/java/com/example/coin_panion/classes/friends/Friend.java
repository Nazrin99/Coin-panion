package com.example.coin_panion.classes.friends;

import java.sql.Blob;
import java.util.List;

public class Friend{

    /*Friend Info*/
    private boolean blockedStatus;
    private Integer friendID;
    private Integer accountID;
    private String friendName;
    private String phoneNumber;
    private Blob profilePic;

    /*Additional Friend info*/
    //TODO how to know if that friend is in debt with us


    /*Create new friend object based on the connection created between user and the other user as friend*/
    public Friend(Integer friendID, Integer userID) {
        this.friendID = friendID;
        this.accountID = userID;
        this.blockedStatus = false;

        //TODO fetch the friend info from account based on their id then define the variable
        this.friendName = null;
        this.phoneNumber = null;
        this.profilePic = null;
    }

    /*TODO insert connection between user to database as friend based on this.variable*/
    public void addFriend(){

    }

    /*TODO remove connection between user to database as friend based on this.variable*/
    public void removeFriend(){
        String query = "DELETE FROM friends WHERE friend_id = [friend_id] AND user_id = [user_id]";
    }

    /*Fetch the friend information from database based on the accountID*/
    public static List<Friend> fetchFriends(Integer accountID){
        return null;
    }

    /*Friend blocked status*/
    public void setBlockedStatus(boolean blockedStatus) {
        this.blockedStatus = blockedStatus;
    }

    public boolean isBlockedStatus() {
        return blockedStatus;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Blob getProfilePic() {
        return profilePic;
    }

}
