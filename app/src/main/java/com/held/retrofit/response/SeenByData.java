package com.held.retrofit.response;

/**
 * Created by swapnil on 4/10/15.
 */
public class SeenByData {

    public User user;
    public boolean isFriend;
    public boolean friendRequestSent;

    public SeenByData(User u, boolean isFriend, boolean requestSent){
        this.user = u;
        this.isFriend = isFriend;
        this.friendRequestSent = requestSent;
    }

}
