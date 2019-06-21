package com.held.retrofit.response;

/**
 * Created by MAHESH on 11/2/2015.
 */
public class SubFeedData {
    User user;
    String rid,text,date,held;

    public String getHeld() {
        return held;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String getRid() {
        return rid;
    }
}
