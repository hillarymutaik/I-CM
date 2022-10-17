package com.musicind.dukamoja.inter;



import com.musicind.dukamoja.models.AccessToken;
import com.musicind.dukamoja.response.CommentResponse;
import com.musicind.dukamoja.response.JSONResponse;
import com.musicind.dukamoja.response.ProfileResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RequestInterface {

    @GET("show")
    Call<JSONResponse> getJSON();

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name,
                               @Field("email") String email,
                               @Field("phone") String phone,
                               @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username,
                            @Field("password") String password);
    @POST("social_auth")
    @FormUrlEncoded
    Call<AccessToken> socialAuth(@Field("name") String name,
                                 @Field("email") String email,
                                 @Field("provider") String provider,
                                 @Field("provider_user_id") String providerUserId);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("postComment")
    @FormUrlEncoded
    Call<AccessToken> postComment(@Field("body") String body,
                                  @Field("song_id") int song_id,
                                  @Field("user_id") int user_id
    );
    @GET("getComments/{slug}")
    Call<CommentResponse> getComments(
            @Path("slug") int slug);

    @GET("getProfile/{user}")
    Call<ProfileResponse> getUserProfile(
            @Path("user") int user);

    @POST("updateName/{id}")
    @FormUrlEncoded
    Call<AccessToken> updateUserName(
            @Field("name") String name,
            @Path("id") int user);

    @POST("updateEmail/{id}")
    @FormUrlEncoded
    Call<AccessToken> updateUserEmail(
            @Field("email") String email,
            @Path("id") int user);

    @POST("updateDesc/{id}")
    @FormUrlEncoded
    Call<AccessToken> updateUserDesc(
            @Field("description") String description,
            @Path("id") int user);

    @POST("updatePwd/{id}")
    @FormUrlEncoded
    Call<AccessToken> updateUserPwd(
            @Field("password") String password,
            @Path("id") int user);

    @Multipart
    @POST("updateProfile/{id}")
    Call<AccessToken> updateProfile(@Part("profile\"; filename=\"myfile.jpg\" ") RequestBody profile,
                                  @Path("id") int user);

}
