package com.held.retrofit.response;

/**
 * Created by swapnil on 1/10/15.
 */
public class FriendData {

    User fromUser;
    User toUser;
    User user;
    String date;
    String rid;

    public String getProfilePic(){
        return  toUser.getProfilePic();
    }

    public String getDisplayName(){
        return toUser.getDisplayName();
    }

    public User getUser() { return user; }

    public String getJoinDate(){
        return toUser.getJoinDate();
    }

    public User getToUser() {
        return toUser;
    }

    public User getFromUser() {
        return fromUser;
    }


}
