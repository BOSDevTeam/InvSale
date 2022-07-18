package com.bosictsolution.invsale.api;

import com.bosictsolution.invsale.data.ClientData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.TownshipData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("division")  // API's endpoint
    Call<List<DivisionData>> getDivision();

    @GET("township")  // API's endpoint
    Call<List<TownshipData>> getTownshipByDivision(@Query("divisionId") int divisionId);

    @Headers("Content-type: application/json")
    @POST("client/InsertClient")
    Call<Integer> insertClient(@Body ClientData clientData);

    @Headers("Content-type: application/json")
    @POST("client/UpdateClientPassword")
    Call<Void> updateClientPassword(@Query("clientId") int clientId, @Query("password") String password);

}
