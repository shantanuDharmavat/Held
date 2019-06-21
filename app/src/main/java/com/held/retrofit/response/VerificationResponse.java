package com.held.retrofit.response;

/**
 * Created by jay on 1/8/15.
 */
public class VerificationResponse {


    private boolean verified;
    private String rid;
    private String sessionToken;
    private String phone,displayName;

    public boolean isVerified() {
        return verified;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getRid() {
        return rid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhone() {
        return phone;
    }
}
