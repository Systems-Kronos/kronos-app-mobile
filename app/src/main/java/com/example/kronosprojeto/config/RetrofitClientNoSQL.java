package com.example.kronosprojeto.config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientNoSQL {
    private static final String BASE_URL = "https://spring-api-nosql.onrender.com";
    private static Retrofit retrofit;

    private RetrofitClientNoSQL() {
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofitInstance().create(serviceClass);
    }
}
