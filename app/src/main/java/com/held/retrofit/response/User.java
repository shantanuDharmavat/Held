package com.held.retrofit.response;

/**
 * Created by MAHESH on 9/15/2015.
 */
public class User {

        String rid;
        String pin;
        String profilePic,imageUri;
        String displayName;
        String phone;
        String joinDate,friendCount,postCount;
        int availableInvites, unseenNotificationsCount, unseenMessagesCount;
        long profileLastNotificationViewTime, profileLastChatViewTime;

        public int getUnseenNotificationsCount() { return unseenNotificationsCount; }

        public int getUnseenMessagesCount(){ return unseenMessagesCount; }

        public int getAvailableInvites() {
                return availableInvites;
        }

        public String getPostCount() {
                return postCount;
        }

        public String getFriendCount() {
                return friendCount;
        }

        public String getJoinDate() {
                return joinDate;
        }

        public String getDisplayName() {
                return displayName;
        }

        public String getProfilePic() {
                return profilePic;
        }

        public String getPhone() {
                return phone;
        }

        public String getPin() {
                return pin;
        }

        public String getRid(){return rid;}

        public void setDisplayName(String displayName) {
                this.displayName = displayName;
        }

        public void setProfilePic(String profilePic) {
                this.profilePic = profilePic;
        }

        public String getImageUri() {
                return imageUri;
        }

        public void setFriendCount(String friendCount) {
                this.friendCount = friendCount;
        }

        public void setPostCount(String postCount) {
                this.postCount = postCount;
        }
}
