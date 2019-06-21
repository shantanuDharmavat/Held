package com.held.retrofit.response;

/**
 * Created by MAHESH on 9/29/2015.
 */
public class FriendRequestObject {


    User fromUser;
    User toUser;
    User creator;
    String rid,imageUri;
    public User getFromUser() {
        return fromUser;
    }

    public String getRid() {
        return rid;
    }

    public User getToUser() {
        return toUser;
    }

    public String getImageUri() {
        return imageUri;
    }

    public User getCreator() {
        return creator;
    }
}
