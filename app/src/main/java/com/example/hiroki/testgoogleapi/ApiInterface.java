package com.example.hiroki.testgoogleapi;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ApiInterface {

    @GET("/gourmet/v1/?format=json")
    void gourmet(@Query("key") String key,
                 @Query("keyword") String keyword,
                 @Query("lat") double lat,
                 @Query("lng") double lng,
                 @Query("range") int range,
                 Callback<ApiGourmetResponse> apiGourmetResponseCallBack);


}
