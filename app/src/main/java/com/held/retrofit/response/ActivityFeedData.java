package com.held.retrofit.response;


public class ActivityFeedData {

    private String date;
    private ActivityFeedEntity entity;
    private String activityType;
    private String rid;


    public String getActivityType(){
        return activityType;
    }

    public String getDate() {
        return date;
    }

    public String getRid() {
        return rid;
    }

    public ActivityFeedEntity getEntity(){return entity;}
}
