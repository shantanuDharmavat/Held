package com.held.retrofit.response;

/**
 * Created by MAHESH on 10/12/2015.
 */
public class InboxData {
    User fromUser;
    User toUser;
    String date;
    String rid,text;

    public String getProfilePic(){
        return  fromUser.getProfilePic();
    }

    public String getDisplayName(){
        return fromUser.getDisplayName();
    }

    public String getJoinDate(){
        return fromUser.getJoinDate();
    }

    public User getToUser() {
        return toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
