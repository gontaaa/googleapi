package com.example.hiroki.testgoogleapi;

import retrofit.RestAdapter;

public class ApiClientManager {

    private static final RestAdapter sRestAdapter = new RestAdapter.Builder()
            .setEndpoint(BuildConfig.API_ENDPOINT)
            .setLogLevel(RestAdapter.LogLevel.valueOf(BuildConfig.RETROFIT_LOG_LEVEL))
            .build();

    private ApiClientManager() {
    }

    public static <T> T create(Class<T> clazz) {
        return sRestAdapter.create(clazz);
    }


}
