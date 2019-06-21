package com.held.gcm;

import java.util.concurrent.atomic.AtomicInteger;

public class GCMConstants {
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */

//    public enum NOTIFICATION {
//        NOTIFICATION_DEFAULT(0, AppConstants.AllSCREENS.LAUNCH_DASHBOARD_SCREEN),
//        NOTIFICATION_1(1, AppConstants.AllSCREENS.LAUNCH_EVENT_PICKER_SCREEN),   // Event Picker
//        NOTIFICATION_2(2, AppConstants.AllSCREENS.LAUNCH_SCHEDULE_MANAGER_SCREEN), // Schedule manager
//        NOTIFICATION_3(3, AppConstants.AllSCREENS.LAUNCH_DASHBOARD_SCREEN),  // Dashboard
//        NOTIFICATION_4(4, AppConstants.AllSCREENS.LAUNCH_LIVE_SEAT_SCREEN),  // Live seat
//        NOTIFICATION_9(9, AppConstants.AllSCREENS.LAUNCH_EVENT_CLASH_SCREEN), // Schedule clash screen
//        NOTIFICATION_10(10, AppConstants.AllSCREENS.LAUNCH_COUPON_DETAIL); // Wallet screen  <Coupon detail screen>
//
//        protected final int ID;
//        protected final AppConstants.AllSCREENS LAUNCH_SCREEN_NAME;
//
//        NOTIFICATION(int id, AppConstants.AllSCREENS screenName) {
//            this.ID = id;
//            this.LAUNCH_SCREEN_NAME = screenName;
//        }
//
//        public static NOTIFICATION getNotificationFromNotId(int id) {
//            for (NOTIFICATION notification : NOTIFICATION.values()) {
//                if (id == notification.ID) return notification;
//            }
//            return NOTIFICATION_DEFAULT;
//        }
//    }

    public static class NotificationID {
        private static final AtomicInteger c = new AtomicInteger(0);

        public static int getID() {
            return c.incrementAndGet();
        }
    }
}
