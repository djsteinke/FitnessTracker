package com.rn5.libstrava.common.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rn5.libstrava.common.model.Token;
import com.rn5.libstrava.common.typeAdapter.TokenTypeAdapter;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class Config {

    private Retrofit retrofit;

    public Config(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    protected static Retrofit createRetrofit(boolean debug, String baseURL, Interceptor... interceptors) {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (interceptors != null) {
            for (Interceptor interceptor : interceptors)
                httpClientBuilder.addInterceptor(interceptor);
        }

        if(debug){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(interceptor);
        }

        OkHttpClient client = httpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(makeGson()))
                .build();

        return retrofit;
    }

    private static Gson makeGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .registerTypeAdapter(Token.class, new TokenTypeAdapter())
                .create();
    }
}
