package com.held.retrofit.response;


public class PostChatData {

    private String tag;
    private String text,imageUri,thumbnailUri;
    private User toUser,fromUser,creator,user;
    private String rid;
    private long date;
    private PostData post;

    public String getImageUri() {
        return imageUri;
    }

    public PostData getPost() {
        return post;
    }

    public long getDate(){return date;}

    public User getCreator() {
        return creator;
    }

    public User getUser() {
        return user;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public String getTag() {
        return tag;
    }

    public String getRid() {
        return rid;
    }

    public String getText() {
        return text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}
