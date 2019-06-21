package com.held.retrofit.response;

/**
 * Created by MAHESH on 9/30/2015.
 */
public class PostData {

    private String thumbnailUri,text,rid,imageUri;
    private Creator creator;
    private long held;
    private String views;
    private LatestHold latestHold;
    private LatestMessage latestMessage;

    public Creator getCreator() {
        return creator;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getText() {
        return text;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public String getRid() {
        return rid;
    }

    public long getHeld(){
        return this.held;
    }

    public LatestMessage getLatestMessage(){
        return latestMessage;
    }

    public LatestHold getLatestHold(){return latestHold;}

    public String getViews(){return views;}
}
