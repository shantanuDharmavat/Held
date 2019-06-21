package com.held.retrofit.response;


import java.util.List;
import com.held.retrofit.response.FriendData;

public class FriendsResponse {

    private List<FriendData> objects;

    private boolean lastPage;
    private long nextPageStart;
    private String declined,date,rid;

    public long getNextPageStart() {
        return nextPageStart;
    }

    public List<FriendData> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }


}