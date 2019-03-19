package com.newage.letstalk.api;

import com.newage.letstalk.dataLayer.local.tables.Friend;
import com.newage.letstalk.model.request.CheckContactRequest;
import com.newage.letstalk.model.request.ClearContactRequest;
import com.newage.letstalk.model.request.LoginRequest;
import com.newage.letstalk.model.request.RegisterRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @POST("register.php")
    Call<String> registerUser(@Body RegisterRequest registerRequest);

    @POST("register.php")
    Call<String> registerUser2(@QueryMap Map<String, String> registerRequest);

    @POST("login.php")
    Call<String> loginUser(@Body LoginRequest loginRequest);

    @POST("clear_contacts.php")
    Call<String> clearContacts(@Body ClearContactRequest clearContactRequest);

    @GET("chat_contacts.php/")
    Call<List<Friend>> getFriendList(@Query("user") String user);

    @POST("check_contact.php")
    Call<String> checkContact(@Body CheckContactRequest checkContactRequest);

    @POST("check_contact.php")
    Call<String> checkContact2(@QueryMap Map<String, String> checkContactRequest);

    @POST("invite.php")
    Call<String> inviteContact(@QueryMap Map<String, String> inviteContactRequest);


//    @GET("api/v1/user/my_profile/")
//    Call<UserDataResponse> fetchUserProfile(@Header("Authorization") String token);


}