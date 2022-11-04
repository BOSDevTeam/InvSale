package com.bosictsolution.invsale.api;

import com.bosictsolution.invsale.data.BankPaymentData;
import com.bosictsolution.invsale.data.ClientData;
import com.bosictsolution.invsale.data.CompanySettingData;
import com.bosictsolution.invsale.data.CustomerData;
import com.bosictsolution.invsale.data.DivisionData;
import com.bosictsolution.invsale.data.LimitedDayData;
import com.bosictsolution.invsale.data.LocationData;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.NotificationData;
import com.bosictsolution.invsale.data.PaymentData;
import com.bosictsolution.invsale.data.PaymentMethodData;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleOrderMasterData;
import com.bosictsolution.invsale.data.SaleOrderTranData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.data.TownshipData;
import com.bosictsolution.invsale.data.VoucherSettingData;

import java.util.List;

import io.reactivex.Observable;
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

    @GET("customer")
    Call<List<CustomerData>> getCustomer();

    @GET("location")
    Observable<List<LocationData>> getLocation();

    @GET("payment")
    Observable<List<PaymentData>> getPayment();

    @GET("paymentmethod")
    Observable<List<PaymentMethodData>> getPaymentMethod();

    @GET("bankpayment")
    Observable<List<BankPaymentData>> getBankPayment();

    @GET("limitedday")
    Observable<List<LimitedDayData>> getLimitedDay();

    @Headers("Content-type: application/json")
    @POST("customer")
    Call<Integer> insertCustomer(@Body CustomerData customerData);

    @Headers("Content-type: application/json")
    @POST("sale")
    Call<Integer> insertSale(@Body SaleMasterData model);

    @GET("vouchersetting")
    Observable<List<VoucherSettingData>> getVoucherSetting();

    @GET("sale/GetMasterSaleByDate")
    Call<List<SaleMasterData>> getMasterSaleByDate(@Query("date") String date,@Query("clientId") int clientId);

    @GET("sale/GetMasterSaleByFromToDate")
    Call<List<SaleMasterData>> getMasterSaleByFromToDate(@Query("fromDate") String fromDate,@Query("toDate") String toDate,@Query("clientId") int clientId);

    @GET("sale/GetMasterSaleByValue")
    Call<List<SaleMasterData>> getMasterSaleByValue(@Query("value") String value,@Query("clientId") int clientId);

    @GET("sale/GetSaleItemByDate")
    Call<List<SaleTranData>> getSaleItemByDate(@Query("date") String date, @Query("clientId") int clientId, @Query("mainMenuId") int mainMenuId, @Query("subMenuId") int subMenuId);

    @GET("sale/GetSaleItemByFromToDate")
    Call<List<SaleTranData>> getSaleItemByFromToDate(@Query("fromDate") String fromDate,@Query("toDate") String toDate,@Query("clientId") int clientId, @Query("mainMenuId") int mainMenuId, @Query("subMenuId") int subMenuId);

    @GET("sale/GetSaleItemByValue")
    Call<List<SaleTranData>> getSaleItemByValue(@Query("value") String value, @Query("clientId") int clientId);

    @Headers("Content-type: application/json")
    @POST("saleorder")
    Call<String> insertSaleOrder(@Body SaleOrderMasterData model);

    @GET("saleorder/GetMasterSaleOrderByDate")
    Call<List<SaleOrderMasterData>> getMasterSaleOrderByDate(@Query("date") String date,@Query("clientId") int clientId,@Query("isOrderFinished") boolean isOrderFinished);

    @GET("saleorder/GetMasterSaleOrderByFromToDate")
    Call<List<SaleOrderMasterData>> getMasterSaleOrderByFromToDate(@Query("fromDate") String fromDate,@Query("toDate") String toDate,@Query("clientId") int clientId,@Query("isOrderFinished") boolean isOrderFinished);

    @GET("saleorder/GetMasterSaleOrderByValue")
    Call<List<SaleOrderMasterData>> getMasterSaleOrderByValue(@Query("value") String value,@Query("clientId") int clientId,@Query("isOrderFinished") boolean isOrderFinished);

    @GET("saleorder/GetMasterSaleOrderBySaleOrderID")
    Call<SaleOrderMasterData> getMasterSaleOrderBySaleOrderID(@Query("saleOrderId") int saleOrderId);

    @GET("saleorder/GetTranSaleOrderBySaleOrderID")
    Call<List<SaleOrderTranData>> getTranSaleOrderBySaleOrderID(@Query("saleOrderId") int saleOrderId);

    @GET("saleorder/GetSaleOrderItemByDate")
    Call<List<SaleOrderTranData>> getSaleOrderItemByDate(@Query("date") String date, @Query("clientId") int clientId, @Query("mainMenuId") int mainMenuId, @Query("subMenuId") int subMenuId);

    @GET("saleorder/GetSaleOrderItemByFromToDate")
    Call<List<SaleOrderTranData>> getSaleOrderItemByFromToDate(@Query("fromDate") String fromDate,@Query("toDate") String toDate,@Query("clientId") int clientId, @Query("mainMenuId") int mainMenuId, @Query("subMenuId") int subMenuId);

    @GET("saleorder/GetSaleOrderItemByValue")
    Call<List<SaleOrderTranData>> getSaleOrderItemByValue(@Query("value") String value, @Query("clientId") int clientId);

    @GET("companysetting/GetCompanySetting")
    Call<CompanySettingData> getCompanySetting();

    @GET("mainmenu/GetMainMenu")
    Observable<List<MainMenuData>> getMainMenu();

    @GET("submenu/GetSubMenu")
    Observable<List<SubMenuData>> getSubMenu();

    @GET("product/GetProduct")
    Observable<List<ProductData>> getProduct();

    @GET("clientnoti/GetClientNotiCount")
    Call<Integer> getClientNotiCount(@Query("clientId") int clientId);

    @GET("clientnoti/GetClientNotification")
    Call<List<NotificationData>> getClientNotification(@Query("clientId") int clientId);

    @Headers("Content-type: application/json")
    @POST("clientnoti/DeleteClientNotification")
    Call<Void> deleteClientNotification(@Query("clientId") int clientId,@Query("notiId") int notiId,@Query("notiType") int notiType);

    @Headers("Content-type: application/json")
    @POST("clientnoti/DeleteAllClientNotification")
    Call<Void> deleteAllClientNotification(@Query("clientId") int clientId);

    @GET("product/GetProduct")
    Call<ProductData> getProduct(@Query("productId") int productId);
}
