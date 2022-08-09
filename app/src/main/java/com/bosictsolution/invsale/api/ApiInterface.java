package com.bosictsolution.invsale.api;

import com.bosictsolution.invsale.data.BankPaymentData;
import com.bosictsolution.invsale.data.ClientData;
import com.bosictsolution.invsale.data.CompanySettingData;
import com.bosictsolution.invsale.data.CustomerData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.LimitedDayData;
import com.bosictsolution.invsale.data.LocationData;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.PaymentData;
import com.bosictsolution.invsale.data.PaymentMethodData;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.data.TownshipData;
import com.bosictsolution.invsale.data.VoucherSettingData;

import java.util.Date;
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

    @GET("township")
    Call<List<TownshipData>> getTownshipByDivision(@Query("divisionId") int divisionId);

    @Headers("Content-type: application/json")
    @POST("client/InsertClient")
    Call<Integer> insertClient(@Body ClientData clientData);

    @Headers("Content-type: application/json")
    @POST("client/UpdateClientPassword")
    Call<Void> updateClientPassword(@Query("clientId") int clientId, @Query("password") String password);

    @GET("client")
    Call<ClientData> checkClient(@Query("phone") String phone);

    @Headers("Content-type: application/json")
    @POST("client/UpdateClient")
    Call<Void> updateClient(@Query("clientId") int clientId,@Body ClientData clientData);

    @GET("product/SearchProductByValue")
    Call<List<ProductData>> searchProductByValue(@Query("value") String value);

    @GET("companysetting")
    Call<CompanySettingData> getCompanySetting();

    @GET("mainmenu")
    Call<List<MainMenuData>> getMainMenu();

    @GET("submenu/GetSubMenuByMainMenu")
    Call<List<SubMenuData>> getSubMenuByMainMenu(@Query("mainMenuId") int mainMenuId);

    @GET("submenu/GetSubMenu")
    Call<List<SubMenuData>> getSubMenu();

    @GET("product/GetProductBySubMenuList")
    Call<List<ProductData>> getProductBySubMenuList(@Query("subMenuIdList") String subMenuIdList);

    @GET("customer")
    Call<List<CustomerData>> getCustomer();

    @GET("location")
    Call<List<LocationData>> getLocation();

    @GET("payment")
    Call<List<PaymentData>> getPayment();

    @GET("paymentmethod")
    Call<List<PaymentMethodData>> getPaymentMethod();

    @GET("bankpayment")
    Call<List<BankPaymentData>> getBankPayment();

    @GET("limitedday")
    Call<List<LimitedDayData>> getLimitedDay();

    @Headers("Content-type: application/json")
    @POST("customer")
    Call<Integer> insertCustomer(@Body CustomerData customerData);

    @Headers("Content-type: application/json")
    @POST("sale")
    Call<Integer> insertSale(@Body SaleMasterData model);

    @GET("vouchersetting")
    Call<VoucherSettingData> getVoucherSetting(@Query("locationId") int locationId);

    @GET("sale/GetMasterSaleByDate")
    Call<List<SaleMasterData>> getMasterSaleByDate(@Query("date") String date,@Query("clientId") int clientId);

    @GET("sale/GetMasterSaleByFromToDate")
    Call<List<SaleMasterData>> getMasterSaleByFromToDate(@Query("fromDate") String fromDate,@Query("toDate") String toDate,@Query("clientId") int clientId);

    @GET("sale/GetSaleItemByDate")
    Call<List<SaleTranData>> getSaleItemByDate(@Query("date") String date, @Query("clientId") int clientId, @Query("mainMenuId") int mainMenuId, @Query("subMenuId") int subMenuId);

    @GET("sale/GetSaleItemByFromToDate")
    Call<List<SaleTranData>> getSaleItemByFromToDate(@Query("fromDate") String fromDate,@Query("toDate") String toDate,@Query("clientId") int clientId, @Query("mainMenuId") int mainMenuId, @Query("subMenuId") int subMenuId);
}
