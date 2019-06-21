package com.held.retrofit.response;

import java.util.List;

public class PostMessageResponse {

    private String tag;
    private String text;
    private User toUser,fromUser;
    private String date,rid;


    public String getDate() {
        return date;
    }


    public String getTag() {
        return tag;
    }

    public String getRid() {
        return rid;
    }

    public String getText() {
        return text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }
}
