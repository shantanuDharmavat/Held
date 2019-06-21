package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class LoginUserResponse {

    private String sessionToken;
    private boolean login;
    public User user;


    public String getSessionToken() {
        return sessionToken;
    }
    public User getUser(){return user;}
    public boolean isLogin() {
        return login;
    }
}
