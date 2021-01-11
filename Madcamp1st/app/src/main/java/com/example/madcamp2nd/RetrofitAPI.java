package com.example.madcamp2nd;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @POST("sync/contacts")
    Call<Users> contactsave(@Body Users user);

    @POST("sync/gallery")
    Call<Users> gallerysave(@Body Users user);

    @POST("login")
    Call<Users> usersave(@Body Users user); //이건 바디 요청시 사용하는거
// callbooks 는받아오는 애의 타입     book은 보내는거
    //@FormUrlEncoded
    //@POST("/auth/overlapChecker")
    //Call<Model__CheckAlready> postOverlapCheck(@Field("phone") String phoneNum, @Field("message") String message); //이건 요청시 사용하는거 (*데이터를 보낼때)
}