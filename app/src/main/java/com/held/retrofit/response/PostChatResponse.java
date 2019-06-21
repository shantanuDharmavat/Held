package com.held.retrofit.response;


import java.util.List;

public class PostChatResponse {

    private List<PostChatData> objects;
    private boolean lastPage;
    private long next;


    public boolean isLastPage() {
        return lastPage;
    }

    public List<PostChatData> getObjects() {
        return objects;
    }

    public void setObjects(List<PostChatData> objects) {
        this.objects = objects;
    }

    public long getNext(){
        return next;
    }
}
