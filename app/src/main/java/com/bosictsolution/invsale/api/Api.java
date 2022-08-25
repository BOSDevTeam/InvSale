package com.bosictsolution.invsale.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static Retrofit retrofit=null;

    public static ApiInterface getClient(){  // this is manual dependency injection
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
//                    .baseUrl("http://192.168.100.14/InventoryWebService/api/")
                    .baseUrl("http://bosasp-001-site15.gtempurl.com/api/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        ApiInterface api=retrofit.create(ApiInterface.class);
        return api;
    }
}
