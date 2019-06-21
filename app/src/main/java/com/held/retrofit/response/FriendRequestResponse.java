package com.held.retrofit.response;


import java.util.List;

public class FriendRequestResponse {


    private List<FriendRequestObject> objects;
    private boolean lastPage;
    private long nextPageStart;
    private String declined,date,rid;

    public long getNextPageStart() {
        return nextPageStart;
    }

    public List<FriendRequestObject> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public String getRid() {
        return rid;
    }

    public String getDate() {
        return date;
    }

    public String getDeclined() {
        return declined;
    }

    public void setDeclined(String declined) {
        this.declined = declined;
    }

}
