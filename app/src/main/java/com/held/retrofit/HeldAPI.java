package com.held.retrofit;

import com.held.retrofit.response.ActivityFeedDataResponse;
import com.held.retrofit.response.AddFriendResponse;
import com.held.retrofit.response.ApproveDownloadResponse;
import com.held.retrofit.response.ApproveFriendResponse;
import com.held.retrofit.response.ChatHeadResponse;
import com.held.retrofit.response.CreateUserResponse;
import com.held.retrofit.response.DeclineDownloadResponse;
import com.held.retrofit.response.DeclineFriendResponse;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.DownloadRequestListResponse;
import com.held.retrofit.response.Engager;
import com.held.retrofit.response.EngagersResponse;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.FriendDeclineResponse;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.retrofit.response.FriendsResponse;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.InviteListResponse;
import com.held.retrofit.response.InviteResponse;
import com.held.retrofit.response.LoginUserResponse;
import com.held.retrofit.response.LogoutUserResponse;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.retrofit.response.UnDeclineFriendResponse;
import com.held.retrofit.response.UnFriendResponse;
import com.held.retrofit.response.VerificationResponse;
import com.held.retrofit.response.VoiceCallResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by YMediaLabs on 04/02/15.
 */
public interface HeldAPI {

    String CREATE_USER = "/registrations/";
    String RESEND_SMS = "/registrations/{registration_id}/sms/";
    String VOICE_CALL = "/registrations/{registration_id}/call/";
    String LOGIN_USER = "/sessions/";
    String VERIFY = "/registrations/{registration_id}/";

    @POST(CREATE_USER)
    void createUser(@Query("name") String name,@Query("phone") String phoneNo, @Body()String empty, Callback<CreateUserResponse> createUserResponseCallback);

    @GET(RESEND_SMS)
    void resendSms(@Header("Authorization")String auth,@Path("registration_id") String RegId , Callback<CreateUserResponse> createUserResponseCallback);

    @GET(VOICE_CALL)
    void voiceCall(@Header("Authorization")String auth,@Path("registration_id") String RegId ,Callback<VoiceCallResponse> voiceCallResponseCallback);

    @POST(LOGIN_USER)
    void loginUser(@Query("phone") String phoneNo, @Query("pin") String pin,@Body()String empty, Callback<LoginUserResponse> loginUserResponseCallback);

    @PUT(VERIFY)
    void verifyUser(@Header("Authorization")String auth,@Path("registration_id") String RegId , @Query("pin") String pin,@Body()String empty ,Callback<VerificationResponse> verificationResponseCallback);

    @POST("/sessions/sms/")
    void loginSessionSendPinSmsApi(@Query("phone") String phoneNo,@Body()String empty, Callback<CreateUserResponse> createUserResponseCallback);
    @POST("/sessions/call")
    void loginSessionSendPinCallApi(@Query("phone") String phoneNo,@Body()String empty, Callback<VoiceCallResponse> voiceCallResponseCallback);

    @Multipart
    @POST("/posts/")
    void uploadFile(@Header("Authorization") String header,@Query("text") String text, @Part("file") TypedFile photoFile,@Query("url") String url , Callback<PostResponse> postResponseCallback);


    @DELETE("/posts/{post_id}/report")
    void reportAbuse(@Header("Authorization") String header, @Path("post_id") String postId, @Query("message") String text, Callback<SearchUserResponse> searchUserResponseCallback);

    @PUT("/users/{user_id}/")
    void updateNotificationToken(@Header("Authorization") String token,@Path("user_id")String uid, @Query("field") String fieldName, @Query("value") String fieldValue,
                          @Body() String empty, Callback<ProfilPicUpdateResponse> profilPicUpdateResponseCallback);

    @PUT("/users/{user_id}/")
    void updateSeenTime(@Header("Authorization") String token,@Path("user_id")String uid, @Query("field") String fieldName, @Query("value") String fieldValue,
                                 @Body() String empty, Callback<SearchUserResponse> searchUserResponseCallback);

    @Multipart
    @POST("/registrations/{registration_id}/pic")
    void updateProfilePic(@Header("Authorization") String token,@Path("registration_id")String uid,@Part("file") TypedFile photoFile,
                          Callback<ProfilPicUpdateResponse> profilPicUpdateResponseCallback);
    @GET("/users/{user_id}/")
    void searchUser(@Header("Authorization") String token, @Path("user_id") String uid, Callback<SearchUserResponse> searchUserResponseCallback);


    @PUT("/posts/{post_id}/holds/{hold_id}/")
    void releasePost(@Header("Authorization") String token,@Path("post_id") String postId,@Path("hold_id") String holdId,@Query("start_time") String start_tm,@Query("end_time") String end_tm,@Body()String empty, Callback<ReleaseResponse> releaseResponseCallback);

    @POST("/posts/{post_id}/holds/")
    void holdPost(@Header("Authorization") String token,@Path("post_id") String postId,@Query("start_time") String start_tm, @Body()String empty,Callback<HoldResponse> holdResponseCallback);


    @GET("/posts/")
    void feedPostWithPage(@Header("Authorization") String token, @Query("limit") int limit, @Query("start") long start, Callback<FeedResponse> feedResponseCallback);

    @GET("/users/{user_id}/posts/")
    void getUserPosts(@Header("Authorization") String token, @Path("user_id") String userId,@Query("start") long start,@Query("limit") int limit, Callback<FeedResponse> feedResponseCallback);

    @GET("/posts/{post_id}/holds/{hold_id}/")
    void releasePostProfile(@Header("Authorization") String token, @Path("hold_id")String hid, Callback<ReleaseResponse> callback);


   @GET("/users/")
   void searchByName(@Header("Authorization") String token,@Query("query") String frndName,Callback<Engager> engagerCallback);

    @PUT("/friendshiprequests/{request_id}/")
    void declineFriend(@Header("Authorization") String token,@Path("request_id")String rid, @Query("decline") String decline,@Query("approve") String approve,@Body()String body ,Callback<DeclineFriendResponse> declineFriendResponseCallback);

    @PUT("/friendshiprequests/{request_id}/")
    void approveFriend(@Header("Authorization") String token, @Path("request_id")String rid,@Query("decline") String decline,@Query("approve") String approve,@Body()String body, Callback<ApproveFriendResponse> approveFriendResponseCallback);

    @GET("/friendshiprequests/")
    void getFriendRequests(@Header("Authorization") String token, @Query("limit") int limit, @Query("start") long start, Callback<FriendRequestResponse> friendRequestResponseCallback);

    @POST("/friendshiprequests/")
    void sendRequests(@Header("Authorization") String token, @Query("user") String userId,@Body()String empty ,Callback<FriendRequestResponse> friendRequestResponseCallback);

    @GET("/users/{user_id}/friends/")
    void getFriendsList(@Header("Authorization") String token, @Query("limit") int limit,@Path("user_id") String mUserId, @Query("start") long start, Callback<FriendsResponse> friendsResponseCallback);

    @DELETE("/friends/{friend_id}/")
    void removeFriend(@Header("Authorization") String token, @Path("friend_id")String fid, Callback<DeclineFriendResponse> removeFriendCallback);

    @GET("//chatheads/")
    void getChatHeadList(@Header("Authorization") String token, @Query("limit") int limit, @Query("start") long start, Callback<ChatHeadResponse> chatHeadResponseCallback);

    @GET("/downloadrequests/")
    void getDownLoadRequestList(@Header("Authorization") String token, @Query("limit") int limit, @Query("start") long start, Callback<DownloadRequestListResponse> downloadRequestListResponseCallback);

    @POST("/posts/{post_id}/downloadrequests/")
    void sendDownloadRequest(@Header("Authorization") String token, @Path("post_id")String postId, @Body() String empty, Callback<DownloadRequestData> downloadRequestResponseCallback);

    @PUT("/posts/{post_id}/downloadrequests/{request_id}/")
    void declineDownloadRequest(@Header("Authorization") String token,@Path("post_id")String postId,@Path("request_id")String rid, @Query("decline") String decline,@Query("approve") String approve,@Body()String body, Callback<DeclineDownloadResponse> declineDownloadResponseCallback);

    @PUT("/posts/{post_id}/downloadrequests/{request_id}/")
    void approveDownloadRequest(@Header("Authorization") String token,@Path("post_id")String postId,@Path("request_id")String rid, @Query("decline") String decline,@Query("approve") String approve,@Body()String body, Callback<ApproveDownloadResponse> approveDownloadResponseCallback);

    @DELETE("/posts/{post_id}/downloadrequests/{request_id}/")
    void deleteDownloadRequest(@Header("Authorization") String token,@Path("post_id")String postId,@Path("request_id")String rid,Callback<DeclineDownloadResponse> DeclineResponseCallback);

    @POST("//users/{user_id}/messages/")
    void sendfriendChat(@Header("Authorization") String token, @Path("user_id") String userId, @Query("text") String message,@Body()String body, Callback<PostMessageResponse> postMessageResponseCallback);

    @GET("//users/{user_id}/messages/")
    void getFriendChat(@Header("Authorization") String token, @Path("user_id") String userId, @Query("start") long start, @Query("limit") int limit, Callback<PostChatResponse> postChatResponseCallback);

    @GET("/posts/{post_id}/holds/{hold_id}/")
    void getHold(@Header("Authorization") String token, @Path("post_id") String postId,@Path("hold_id") String holdId,Callback<HoldResponse> getHoldResponce);

    @GET("/posts/{post_id}/messages/")
    void getPostChat(@Header("Authorization") String token, @Path("post_id") String postId,@Query("start") long start, @Query("limit") int limit, Callback<PostChatResponse> postChatResponseCallback);

    @POST("/posts/{post_id}/messages/")
    void postChat(@Header("Authorization") String token, @Path("post_id") String postId, @Query("text") String message,@Body() String body, Callback<PostChatData> postMessageResponseCallback);

    @GET("/posts/{post_id}/messages/{message_id}")
    void getPostChatMsg(@Header("Authorization") String token, @Path("post_id") String postId,@Path("message_id")String messageId, Callback<PostChatData> postMessageResponseCallback);

    @GET("/posts/{post_id}/holds/")
    void getPostEngagers(@Header("Authorization") String token, @Path("post_id") String postId, @Query("limit") int limit, @Query("shuffle") boolean shuffle, Callback<EngagersResponse> engagersResponseCallback);

    @GET("/posts/{post_id}/")
    void getSearchCurrentPost(@Header("Authorization") String token, @Path("post_id") String postId,Callback<PostResponse> postResponseCallback);

    @POST("/invites/")
    void sendInvitation(@Header("Authorization") String token,@Query("phone") String phone_no,@Body() String body, Callback<InviteResponse> postInviteResponseCallBack);

    @GET("/invites/")
    void getInvitationList(@Header("Authorization") String token,@Query("start") long start,@Query("limit") int limit, Callback<InviteListResponse> getInviteListCallBack);

    @PUT("/invites/")
    void askForMoreInvites(@Header("Authorization") String token,@Body() String body, Callback<InviteResponse> postInviteResponseCallBack);

    @DELETE("/invites/{invite_id}")
    void deleteInvite(@Header("Authorization") String token,@Path("invite_id")String id,Callback<InviteResponse> postInviteResponseCallBack);


    @PUT("/users/{user_id}")
    void uploadNewProfilePic(@Header("Authorization") String token,@Path("user_id")String uid,@Query("field")String fld,@Query("value")String value,
                          @Body()String body,Callback<ProfilPicUpdateResponse> profilPicUpdateResponseCallback);

    @GET("/activities/")
    void getActivitiesFeed(@Header("Authorization") String token,@Query("limit") int limit,@Query("start") long start, Callback<ActivityFeedDataResponse> getActivityFeedCallBack);
//@Path("u_id") String userid,



    ///////////////////************OLD APIs**************///////////////////////////////
    @GET("/friends/add")
    void addFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<AddFriendResponse> addFriendResponseCallback);

    @GET("/friends/undecline")
    void undeclineFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<UnDeclineFriendResponse> unDeclineFriendResponseCallback);

    @GET("/friends/unfriend")
    void unFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<UnFriendResponse> unFriendResponseCallback);

    @GET("/users/logout")
    void logoutUser(@Header("X-HELD-TOKEN") String token, @Query("phone") String phone, @Query("pin") int pin, Callback<LogoutUserResponse> logoutUserResponseCallback);


    @GET("/posts/request_download")
    void requestDownLoadPost(@Header("X-HELD-TOKEN") String token, @Query("post") String fieldValue, Callback<DownloadRequestData> downloadRequestDataCallback);

    /*@GET("/activities/")
    void getActivitiesFeed(@Header("X-HELD-TOKEN") String token, @Query("limit") int limit, @Query("start") long start, @Query("user") String uid, Callback<ActivityFeedDataResponse> activityFeedDataResponseCallback);
*/
    @GET("/posts/search")
    void postSearch(@Header("X-HELD-TOKEN") String token, @Query("post") String postId, Callback<PostResponse> postResponseCallback);

    @GET("/posts/")
    void feedPost(@Header("X-HELD-TOKEN") String token, Callback<FeedResponse> feedResponseCallback);

    @GET("/friends/declined")
    void getDeclinedFriends(@Header("X-HELD-TOKEN") String token, Callback<FriendDeclineResponse> friendDeclineResponseCallback);

}