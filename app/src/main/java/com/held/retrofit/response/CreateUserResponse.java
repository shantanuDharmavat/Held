package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class CreateUserResponse {

    private int pin;
    private String phone;
    private String join_date;
    private boolean verified;
    private boolean online;
    private boolean banned;
    private String rid;
    private String display_name;
    private String error;
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
    public String getError() {
        return error;
    }
    public int getPin() {
        return pin;
    }

    public String getPhone() {
        return phone;
    }

    public String getJoin_date() {
        return join_date;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isBanned() {
        return banned;
    }

    public String getRid() {
        return rid;
    }

    public String getDisplay_name() {
        return display_name;
    }
}
