package com.held.retrofit.response;


import java.util.List;

public class FeedData {

    private String date;
    private String imageUri;
    private long held;
    private String text;
    private String profilePic;
    private String thumbnailUri;
    private User user;
    private Creator creator;
    private String rid,views,friendCount,postCount;
    SubFeedData latestMessage;
    SubFeedData latestHold;

    public SubFeedData getLatestHold() {
        return latestHold;
    }

    public SubFeedData getLatestMessage() {
        return latestMessage;
    }

    public String getFriendCount() {
        return friendCount;
    }

    public String getPostCount() {
        return postCount;
    }

    public String getViews() {
        return views;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }
    public String getDate() {
        return date;
    }
    public String getImageUri() {
        return imageUri;
    }
    public long getHeld() {
        return held;
    }

    public String getRid() {
        return rid;
    }

    public String getText() {
        return text;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public User getUser() {
        return user;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setHeld(long held){
        this.held = held;
    }
    public void setViews(String views){
        this.views = views;
    }
}

