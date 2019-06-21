package com.held.retrofit.response;

/**
 * Created by admin on 20-Feb-16.
 */
public class LatestMessage {
    private User user;
    private long date;
    private String rid,text;

    public long getDate(){return date;}

    public User getUser(){return user;}

    public String getRid(){return rid;}

    public String getText(){return text;}
}
