package com.bosictsolution.invsale.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Retrofit.Builder builder=null;

    public static ApiInterface getClient() {  // this is manual dependency injection

        String BASE_URL = "http://192.168.100.11/InventoryWebService/api/";
//        String BASE_URL = "http://bosasp-001-site15.gtempurl.com/api/";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES);

        builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        builder.client(httpClient.build());

        Retrofit retrofit = builder.build();

        ApiInterface api = retrofit.create(ApiInterface.class);
        return api;
    }
}
