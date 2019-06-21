package com.held.retrofit.response;

/**
 * Created by MAHESH on 11/21/2015.
 */
public class InviteResponse {
    String date,phone,rid;
    User user;
    Invite invite;

    public User getUser() {
        return user;
    }
    public String getRid() {
        return rid;
    }
    public String getDate() {
        return date;
    }
    public String getPhone() {
        return phone;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setRid(String rid) {
        this.rid = rid;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Invite getInvite() {
        return invite;
    }
    public void setInvite(Invite invite) {
        this.invite = invite;
    }
}
