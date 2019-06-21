package com.held.retrofit.response;

import java.util.List;

/**
 * Created by MAHESH on 10/12/2015.
 */
public class ChatHeadResponse {
    private List<InboxData> objects;
    String text;
    private boolean lastPage;
    private long nextPageStart;

    public List<InboxData> getObjects() {
        return objects;
    }

    public String getText() {
        return text;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public long getNextPageStart() {
        return nextPageStart;
    }
}
