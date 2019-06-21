package com.held.retrofit.response;


public class HoldResponse {

    private String start_time;
    private PostData post;
    private String rid;

    public PostData getPost() {
        return post;
    }

    public void setPost(PostData post) {
        this.post = post;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getRid() {
        return rid;
    }
}
