package com.held.retrofit.response;

/**
 * Created by MAHESH on 10/7/2015.
 */
public class LatestHold {
    private User user;
    String rid;
    long held,date;

    public User getUser() {
        return user;
    }

    public String getRid() {
        return rid;
    }

    public long getHeld() {
        return held;
    }

    public long getDate(){return date;}
}
