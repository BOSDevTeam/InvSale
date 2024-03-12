package com.bosictsolution.invsale.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bosictsolution.invsale.data.BankPaymentData;
import com.bosictsolution.invsale.data.CompanySettingData;
import com.bosictsolution.invsale.data.LimitedDayData;
import com.bosictsolution.invsale.data.LocationData;
import com.bosictsolution.invsale.data.MainMenuData;
import com.bosictsolution.invsale.data.PaymentData;
import com.bosictsolution.invsale.data.PaymentMethodData;
import com.bosictsolution.invsale.data.ProductData;
import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleOrderMasterData;
import com.bosictsolution.invsale.data.SaleOrderTranData;
import com.bosictsolution.invsale.data.SaleTranData;
import com.bosictsolution.invsale.data.StaffData;
import com.bosictsolution.invsale.data.SubMenuData;
import com.bosictsolution.invsale.data.VoucherSettingData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    private static SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    public DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Start Database Function
     */
    public boolean insertMasterSale(SaleMasterData data){
        deleteMasterSale();
        database = openHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CustomerID", data.getCustomerID());
        cv.put("LocationID", data.getLocationID());
        cv.put("PaymentID", data.getPaymentID());
        cv.put("LimitedDayID", data.getLimitedDayID());
        cv.put("VouDisPercent", data.getVouDisPercent());
        cv.put("VouDisAmount", data.getVouDisAmount());
        cv.put("PayMethodID", data.getPayMethodID());
        cv.put("BankPaymentID", data.getBankPaymentID());
        cv.put("PaymentPercent", data.getPaymentPercent());
        cv.put("Subtotal", data.getSubtotal());
        cv.put("Tax", data.getTax());
        cv.put("TaxAmt", data.getTaxAmt());
        cv.put("Charges", data.getCharges());
        cv.put("ChargesAmt", data.getChargesAmt());
        cv.put("Total", data.getTotal());
        cv.put("VoucherDiscount", data.getVoucherDiscount());
        cv.put("AdvancedPay", data.getAdvancedPay());
        cv.put("PayPercentAmt", data.getPayPercentAmt());
        cv.put("Grandtotal", data.getGrandtotal());
        cv.put("Remark", data.getRemark());
        cv.put("StaffID", data.getStaffID());
        database.insert("MasterSaleTemp", null, cv);
        return true;
    }
    public SaleMasterData getMasterSale(){
        database = openHelper.getReadableDatabase();
        SaleMasterData data=new SaleMasterData();
        Cursor cur=database.rawQuery("SELECT * FROM MasterSaleTemp",null);
        if(cur.moveToFirst()){
            data.setCustomerID(cur.getInt(0));
            data.setLocationID(cur.getInt(1));
            data.setPaymentID(cur.getInt(2));
            data.setLimitedDayID(cur.getInt(3));
            data.setVouDisPercent(cur.getInt(4));
            data.setVouDisAmount(cur.getInt(5));
            data.setPayMethodID(cur.getInt(6));
            data.setBankPaymentID(cur.getInt(7));
            data.setPaymentPercent(cur.getInt(8));
            data.setSubtotal(cur.getInt(9));
            data.setTax(cur.getInt(10));
            data.setTaxAmt(cur.getInt(11));
            data.setCharges(cur.getInt(12));
            data.setChargesAmt(cur.getInt(13));
            data.setTotal(cur.getInt(14));
            data.setVoucherDiscount(cur.getInt(15));
            data.setAdvancedPay(cur.getInt(16));
            data.setPayPercentAmt(cur.getInt(17));
            data.setGrandtotal(cur.getInt(18));
            data.setRemark(cur.getString(19));
            data.setStaffID(cur.getInt(20));
        }
        return data;
    }
    public boolean insertTranSale(List<SaleTranData> list,boolean isSaleEdit,List<SaleTranData> lstLog){
        deleteTranSale();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("ProductID", list.get(i).getProductID());
            cv.put("Quantity", list.get(i).getQuantity());
            cv.put("SalePrice", list.get(i).getSalePrice());
            cv.put("Amount", list.get(i).getAmount());
            cv.put("IsFOC", list.get(i).isFOC());
            cv.put("ProductName",list.get(i).getProductName());
            cv.put("Discount", list.get(i).getDiscount());
            cv.put("DiscountPercent", list.get(i).getDiscountPercent());
            database.insert("TranSaleTemp", null, cv);
        }
        if(isSaleEdit){
            deleteTranSaleLog();
            for(int i=0;i<lstLog.size();i++){
                ContentValues cv = new ContentValues();
                cv.put("ProductID", lstLog.get(i).getProductID());
                cv.put("Quantity", lstLog.get(i).getQuantity());
                cv.put("SalePrice", lstLog.get(i).getSalePrice());
                cv.put("Amount", lstLog.get(i).getAmount());
                cv.put("IsFOC", lstLog.get(i).isFOC());
                cv.put("ProductName",lstLog.get(i).getProductName());
                cv.put("Discount", lstLog.get(i).getDiscount());
                cv.put("DiscountPercent", lstLog.get(i).getDiscountPercent());
                cv.put("ActionCode",lstLog.get(i).getActionCode());
                cv.put("ActionName", lstLog.get(i).getActionName());
                cv.put("OrginalQuantity", lstLog.get(i).getOrginalQuantity());
                database.insert("TranSaleLogTemp", null, cv);
            }
        }
        return true;
    }
    public List<SaleTranData> getTranSale() {
        database = openHelper.getReadableDatabase();
        List<SaleTranData> list = new ArrayList<>();
        int number=0;
        Cursor cur = database.rawQuery("SELECT * FROM TranSaleTemp", null);
        while (cur.moveToNext()) {
            SaleTranData data = new SaleTranData();
            data.setProductID(cur.getInt(0));
            data.setQuantity(cur.getInt(1));
            data.setSalePrice(cur.getInt(2));
            data.setAmount(cur.getInt(3));
            int foc = cur.getInt(4);
            if (foc == 0) data.setFOC(false);
            else if (foc == 1) data.setFOC(true);
            number+=1;
            data.setNumber(number);
            data.setProductName(cur.getString(5));
            data.setDiscount(cur.getInt(6));
            data.setDiscountPercent(cur.getInt(7));
            list.add(data);
        }
        return list;
    }
    public List<SaleTranData> getTranSaleLog() {
        database = openHelper.getReadableDatabase();
        List<SaleTranData> list = new ArrayList<>();
        int number=0;
        Cursor cur = database.rawQuery("SELECT * FROM TranSaleLogTemp", null);
        while (cur.moveToNext()) {
            SaleTranData data = new SaleTranData();
            data.setProductID(cur.getInt(0));
            data.setQuantity(cur.getInt(1));
            data.setSalePrice(cur.getInt(2));
            data.setAmount(cur.getInt(3));
            int foc = cur.getInt(4);
            if (foc == 0) data.setFOC(false);
            else if (foc == 1) data.setFOC(true);
            number+=1;
            data.setNumber(number);
            data.setProductName(cur.getString(5));
            data.setDiscount(cur.getInt(6));
            data.setDiscountPercent(cur.getInt(7));
            data.setActionCode(cur.getString(8));
            data.setActionName(cur.getString(9));
            data.setOrginalQuantity(cur.getInt(10));
            list.add(data);
        }
        return list;
    }
    public boolean insertMainMenu(List<MainMenuData> list){
        deleteMainMenu();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("MainMenuID", list.get(i).getMainMenuID());
            cv.put("MainMenuName", list.get(i).getMainMenuName());
            cv.put("PhotoUrl", list.get(i).getPhotoUrl());
            database.insert("MainMenuTemp", null, cv);
        }
        return true;
    }
    public List<MainMenuData> getMainMenu() {
        database = openHelper.getReadableDatabase();
        List<MainMenuData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM MainMenuTemp", null);
        while (cur.moveToNext()) {
            MainMenuData data = new MainMenuData();
            data.setMainMenuID(cur.getInt(0));
            data.setMainMenuName(cur.getString(1));
            data.setPhotoUrl(cur.getString(2));
            list.add(data);
        }
        return list;
    }
    public boolean insertSubMenu(List<SubMenuData> list){
        deleteSubMenu();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("SubMenuID", list.get(i).getSubMenuID());
            cv.put("MainMenuID", list.get(i).getMainMenuID());
            cv.put("SubMenuName", list.get(i).getSubMenuName());
            cv.put("PhotoUrl", list.get(i).getPhotoUrl());
            database.insert("SubMenuTemp", null, cv);
        }
        return true;
    }
    public List<SubMenuData> getSubMenu() {
        database = openHelper.getReadableDatabase();
        List<SubMenuData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM SubMenuTemp", null);
        while (cur.moveToNext()) {
            SubMenuData data = new SubMenuData();
            data.setSubMenuID(cur.getInt(0));
            data.setMainMenuID(cur.getInt(1));
            data.setSubMenuName(cur.getString(2));
            data.setPhotoUrl(cur.getString(3));
            list.add(data);
        }
        return list;
    }
    public List<SubMenuData> getSubMenuByMainMenu(int mainMenuId) {
        database = openHelper.getReadableDatabase();
        List<SubMenuData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT SubMenuID,SubMenuName FROM SubMenuTemp WHERE MainMenuID="+mainMenuId, null);
        while (cur.moveToNext()) {
            SubMenuData data = new SubMenuData();
            data.setSubMenuID(cur.getInt(0));
            data.setSubMenuName(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public List<SubMenuData> getSubMenuForCategoryFilter() {
        int mainMenuId = 0;
        database = openHelper.getReadableDatabase();
        List<SubMenuData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT SubMenuID,MainMenuID,SubMenuName FROM SubMenuTemp ORDER BY MainMenuID", null);
        while (cur.moveToNext()) {
            if (mainMenuId != cur.getInt(1)) {
                SubMenuData data = new SubMenuData();
                data.setSubMenuID(0);
                data.setMainMenuID(cur.getInt(1));
                data.setSubMenuName("All");
                list.add(data);
            }
            SubMenuData data = new SubMenuData();
            data.setSubMenuID(cur.getInt(0));
            data.setMainMenuID(cur.getInt(1));
            data.setSubMenuName(cur.getString(2));
            list.add(data);

            mainMenuId = cur.getInt(1);
        }
        return list;
    }
    public boolean insertProduct(List<ProductData> list){
        deleteProduct();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("ProductID", list.get(i).getProductID());
            cv.put("SubMenuID", list.get(i).getSubMenuID());
            cv.put("Code", list.get(i).getCode());
            cv.put("ProductName", list.get(i).getProductName());
            cv.put("SalePrice", list.get(i).getSalePrice());
            cv.put("PhotoUrl", list.get(i).getPhotoUrl());
            cv.put("Description",list.get(i).getDescription());
            database.insert("ProductTemp", null, cv);
        }
        return true;
    }
    public List<ProductData> searchProductByValue(String value) {
        database = openHelper.getReadableDatabase();
        List<ProductData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT ProductID,Code,ProductName,SalePrice FROM ProductTemp WHERE Code LIKE '" + value + "%' OR ProductName LIKE '" + value + "%'", null);
        while (cur.moveToNext()) {
            ProductData data = new ProductData();
            data.setProductID(cur.getInt(0));
            data.setCode(cur.getString(1));
            data.setProductName(cur.getString(2));
            data.setSalePrice(cur.getInt(3));
            list.add(data);
        }
        return list;
    }
    public List<ProductData> getProductBySubMenuList(String subMenuIdList) {
        database = openHelper.getReadableDatabase();
        List<ProductData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT ProductID,ProductName,SalePrice,SubMenuID FROM ProductTemp WHERE SubMenuID IN (" + subMenuIdList + ")", null);
        while (cur.moveToNext()) {
            ProductData data = new ProductData();
            data.setProductID(cur.getInt(0));
            data.setProductName(cur.getString(1));
            data.setSalePrice(cur.getInt(2));
            data.setSubMenuID(cur.getInt(3));
            list.add(data);
        }
        return list;
    }
    public List<ProductData> getProductBySubMenu(int subMenuId) {
        database = openHelper.getReadableDatabase();
        List<ProductData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT p.ProductID,ProductName,SalePrice,SubMenuID,Quantity,PhotoUrl,Description FROM ProductTemp p LEFT JOIN TranSaleOrderTemp ts ON p.ProductID=ts.ProductID WHERE SubMenuID =" + subMenuId, null);
        while (cur.moveToNext()) {
            ProductData data = new ProductData();
            data.setProductID(cur.getInt(0));
            data.setProductName(cur.getString(1));
            data.setSalePrice(cur.getInt(2));
            data.setSubMenuID(cur.getInt(3));
            data.setQuantity(cur.getInt(4));
            data.setPhotoUrl(cur.getString(5));
            data.setDescription(cur.getString(6));
            list.add(data);
        }
        return list;
    }
    public boolean insertCompanySetting(CompanySettingData data){
        deleteCompanySetting();
        database = openHelper.getWritableDatabase();
        if(data!=null){
            ContentValues cv = new ContentValues();
            cv.put("Tax", data.getTax());
            cv.put("ServiceCharges", data.getServiceCharges());
            cv.put("HomeCurrency",data.getHomeCurrency());
            cv.put("IsClientPhoneVerify",data.getIsClientPhoneVerify());
            cv.put("ShopTypeCode",data.getShopTypeCode());
            database.insert("CompanySettingTemp", null, cv);
        }
        return true;
    }
    public CompanySettingData getTaxServiceCharges() {
        database = openHelper.getReadableDatabase();
        CompanySettingData data = new CompanySettingData();
        Cursor cur = database.rawQuery("SELECT Tax,ServiceCharges FROM CompanySettingTemp", null);
        if (cur.moveToFirst()) {
            data.setTax(cur.getInt(0));
            data.setServiceCharges(cur.getInt(1));
        }
        return data;
    }
    public String getHomeCurrency() {
        database = openHelper.getReadableDatabase();
        String homeCurrency = "";
        Cursor cur = database.rawQuery("SELECT HomeCurrency FROM CompanySettingTemp", null);
        if (cur.moveToFirst())
            homeCurrency = cur.getString(0);
        return homeCurrency;
    }
    public int getIsClientPhoneVerify() {
        database = openHelper.getReadableDatabase();
        int result = 0;
        Cursor cur = database.rawQuery("SELECT IsClientPhoneVerify FROM CompanySettingTemp", null);
        if (cur.moveToFirst())
            result = cur.getInt(0);
        return result;
    }
    public String getShopTypeCode() {
        database = openHelper.getReadableDatabase();
        String shopTypeCode = "";
        Cursor cur = database.rawQuery("SELECT ShopTypeCode FROM CompanySettingTemp", null);
        if (cur.moveToFirst())
            shopTypeCode = cur.getString(0);
        return shopTypeCode;
    }
    public boolean insertLocation(List<LocationData> list){
        deleteLocation();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("LocationID", list.get(i).getLocationID());
            cv.put("ShortName", list.get(i).getShortName());
            database.insert("LocationTemp", null, cv);
        }
        return true;
    }
    public List<LocationData> getLocation() {
        database = openHelper.getReadableDatabase();
        List<LocationData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM LocationTemp", null);
        while (cur.moveToNext()) {
            LocationData data = new LocationData();
            data.setLocationID(cur.getInt(0));
            data.setShortName(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public boolean insertPayment(List<PaymentData> list){
        deletePayment();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("PaymentID", list.get(i).getPaymentID());
            cv.put("Keyword", list.get(i).getKeyword());
            database.insert("PaymentTemp", null, cv);
        }
        return true;
    }
    public List<PaymentData> getPayment() {
        database = openHelper.getReadableDatabase();
        List<PaymentData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM PaymentTemp", null);
        while (cur.moveToNext()) {
            PaymentData data = new PaymentData();
            data.setPaymentID(cur.getInt(0));
            data.setKeyword(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public boolean insertPaymentMethod(List<PaymentMethodData> list){
        deletePaymentMethod();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("PayMethodID", list.get(i).getPayMethodID());
            cv.put("PayMethodName", list.get(i).getPayMethodName());
            database.insert("PaymentMethodTemp", null, cv);
        }
        return true;
    }
    public List<PaymentMethodData> getPaymentMethod() {
        database = openHelper.getReadableDatabase();
        List<PaymentMethodData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM PaymentMethodTemp", null);
        while (cur.moveToNext()) {
            PaymentMethodData data = new PaymentMethodData();
            data.setPayMethodID(cur.getInt(0));
            data.setPayMethodName(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public boolean insertLimitedDay(List<LimitedDayData> list){
        deleteLimitedDay();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("LimitedDayID", list.get(i).getLimitedDayID());
            cv.put("LimitedDayName", list.get(i).getLimitedDayName());
            database.insert("LimitedDayTemp", null, cv);
        }
        return true;
    }
    public List<LimitedDayData> getLimitedDay() {
        database = openHelper.getReadableDatabase();
        List<LimitedDayData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM LimitedDayTemp", null);
        while (cur.moveToNext()) {
            LimitedDayData data = new LimitedDayData();
            data.setLimitedDayID(cur.getInt(0));
            data.setLimitedDayName(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public boolean insertBankPayment(List<BankPaymentData> list){
        deleteBankPayment();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("BankPaymentID", list.get(i).getBankPaymentID());
            cv.put("BankPaymentName", list.get(i).getBankPaymentName());
            database.insert("BankPaymentTemp", null, cv);
        }
        return true;
    }
    public List<BankPaymentData> getBankPayment() {
        database = openHelper.getReadableDatabase();
        List<BankPaymentData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM BankPaymentTemp", null);
        while (cur.moveToNext()) {
            BankPaymentData data = new BankPaymentData();
            data.setBankPaymentID(cur.getInt(0));
            data.setBankPaymentName(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public String getBankPaymentName(int bankPaymentId) {
        database = openHelper.getReadableDatabase();
        String bankPaymentName = "";
        Cursor cur = database.rawQuery("SELECT BankPaymentName FROM BankPaymentTemp WHERE BankPaymentID=" + bankPaymentId, null);
        if (cur.moveToFirst())
            bankPaymentName = cur.getString(0);
        return bankPaymentName;
    }
    public boolean insertVoucherSetting(List<VoucherSettingData> list){
        deleteVoucherSetting();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("LocationID", list.get(i).getLocationID());
            cv.put("HeaderName", list.get(i).getHeaderName());
            cv.put("HeaderDesp", list.get(i).getHeaderDesp());
            cv.put("HeaderPhone", list.get(i).getHeaderPhone());
            cv.put("HeaderAddress", list.get(i).getHeaderAddress());
            cv.put("OtherHeader1", list.get(i).getOtherHeader1());
            cv.put("OtherHeader2", list.get(i).getOtherHeader2());
            cv.put("FooterMessage1", list.get(i).getFooterMessage1());
            cv.put("FooterMessage2", list.get(i).getFooterMessage2());
            cv.put("FooterMessage3", list.get(i).getFooterMessage3());
            cv.put("VoucherLogoUrl", list.get(i).getVoucherLogoUrl());
            database.insert("VoucherSettingTemp", null, cv);
        }
        return true;
    }
    public VoucherSettingData getVoucherSettingByLocation(int locationId) {
        database = openHelper.getReadableDatabase();
        VoucherSettingData data = new VoucherSettingData();
        Cursor cur = database.rawQuery("SELECT * FROM VoucherSettingTemp WHERE LocationID="+locationId, null);
        if (cur.moveToFirst()) {
            data.setHeaderName(cur.getString(1));
            data.setHeaderDesp(cur.getString(2));
            data.setHeaderPhone(cur.getString(3));
            data.setHeaderAddress(cur.getString(4));
            data.setOtherHeader1(cur.getString(5));
            data.setOtherHeader2(cur.getString(6));
            data.setFooterMessage1(cur.getString(7));
            data.setFooterMessage2(cur.getString(8));
            data.setFooterMessage3(cur.getString(9));
            data.setVoucherLogoUrl(cur.getString(10));
        }
        return data;
    }
    public boolean insertStaff(List<StaffData> list){
        deleteStaff();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("StaffID", list.get(i).getStaffID());
            cv.put("StaffName", list.get(i).getStaffName());
            database.insert("StaffTemp", null, cv);
        }
        return true;
    }
    public List<StaffData> getStaff() {
        database = openHelper.getReadableDatabase();
        List<StaffData> list = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT * FROM StaffTemp", null);
        while (cur.moveToNext()) {
            StaffData data = new StaffData();
            data.setStaffID(cur.getInt(0));
            data.setStaffName(cur.getString(1));
            list.add(data);
        }
        return list;
    }
    public boolean insertMasterSaleOrder(SaleOrderMasterData data){
        deleteMasterSaleOrder();
        database = openHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CustomerID", data.getCustomerID());
        cv.put("Subtotal", data.getSubtotal());
        cv.put("Tax", data.getTax());
        cv.put("TaxAmt", data.getTaxAmt());
        cv.put("Charges", data.getCharges());
        cv.put("ChargesAmt", data.getChargesAmt());
        cv.put("Total", data.getTotal());
        cv.put("Remark", data.getRemark());
        database.insert("MasterSaleOrderTemp", null, cv);
        return true;
    }
    public SaleOrderMasterData getMasterSaleOrder(){
        database = openHelper.getReadableDatabase();
        SaleOrderMasterData data=new SaleOrderMasterData();
        Cursor cur=database.rawQuery("SELECT * FROM MasterSaleOrderTemp",null);
        if(cur.moveToFirst()){
            data.setCustomerID(cur.getInt(0));
            data.setSubtotal(cur.getInt(1));
            data.setTax(cur.getInt(2));
            data.setTaxAmt(cur.getInt(3));
            data.setCharges(cur.getInt(4));
            data.setChargesAmt(cur.getInt(5));
            data.setTotal(cur.getInt(6));
            data.setRemark(cur.getString(7));
        }
        return data;
    }
    public boolean insertUpdateTranSaleOrder(int productId, int quantity) {
        boolean isAlreadyExist = false;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT * FROM TranSaleOrderTemp WHERE ProductID=" + productId, null);
        if (cur.moveToFirst()) isAlreadyExist = true;

        database = openHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ProductID", productId);
        cv.put("Quantity", quantity);
        if (!isAlreadyExist) database.insert("TranSaleOrderTemp", null, cv);
        else database.update("TranSaleOrderTemp", cv, "ProductID=" + productId, null);
        return true;
    }
    public int getSaleOrderQuantityByProduct(int productId) {
        int quantity = 0;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT Quantity FROM TranSaleOrderTemp WHERE ProductID=" + productId, null);
        if (cur.moveToFirst())
            quantity = cur.getInt(0);
        return quantity;
    }
    public int getTotalSaleOrderItem(){
        int count = 0;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT COUNT(*) FROM TranSaleOrderTemp", null);
        if (cur.moveToFirst())
            count = cur.getInt(0);
        return count;
    }
    public int getTotalSaleOrderQty(){
        int qty = 0;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT SUM(Quantity) FROM TranSaleOrderTemp", null);
        if (cur.moveToFirst())
            qty = cur.getInt(0);
        return qty;
    }
    public int getTotalSaleOrderAmount() {
        int totalAmount = 0;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT Quantity,SalePrice FROM TranSaleOrderTemp so INNER JOIN ProductTemp p ON so.ProductID=p.ProductID", null);
        while (cur.moveToNext()) {
            totalAmount += cur.getInt(0) * cur.getInt(1);
        }
        return totalAmount;
    }
    public List<SaleOrderTranData> getTranSaleOrder() {
        List<SaleOrderTranData> list = new ArrayList<>();
        SaleOrderTranData data;
        int number = 0;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT so.ProductID,ProductName,Quantity,SalePrice FROM TranSaleOrderTemp so INNER JOIN ProductTemp p ON so.ProductID=p.ProductID", null);
        while (cur.moveToNext()) {
            data = new SaleOrderTranData();
            number += 1;
            data.setNumber(number);
            data.setProductID(cur.getInt(0));
            data.setProductName(cur.getString(1));
            data.setQuantity(cur.getInt(2));
            data.setSalePrice(cur.getInt(3));
            data.setAmount(cur.getInt(2) * cur.getInt(3));
            list.add(data);
        }
        return list;
    }
    public boolean insertBluetoothPrinter(String printerAddress,int paperWidth) {
        deleteBluetoothPrinter();
        database = openHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PrinterAddress", printerAddress);
        cv.put("PaperWidth", paperWidth);
        database.insert("BluetoothPrinter", null, cv);
        return true;
    }
    public String getPrinterAddress() {
        String printerAddress = "";
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT PrinterAddress FROM BluetoothPrinter", null);
        if (cur.moveToFirst()) printerAddress = cur.getString(0);
        return printerAddress;
    }
    public int getPaperWidth() {
        int paperWidth = 0;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT PaperWidth FROM BluetoothPrinter", null);
        if (cur.moveToFirst()) paperWidth = cur.getInt(0);
        return paperWidth;
    }
    public boolean isSetupBluetoothPrinter() {
        boolean result = false;
        database = openHelper.getReadableDatabase();
        Cursor cur = database.rawQuery("SELECT * FROM BluetoothPrinter", null);
        if (cur.moveToFirst()) result = true;
        return result;
    }
    private boolean deleteMasterSale(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM MasterSaleTemp");
        return true;
    }
    private boolean deleteTranSale(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM TranSaleTemp");
        return true;
    }
    private boolean deleteTranSaleLog(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM TranSaleLogTemp");
        return true;
    }
    private boolean deleteMainMenu(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM MainMenuTemp");
        return true;
    }
    private boolean deleteSubMenu(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM SubMenuTemp");
        return true;
    }
    private boolean deleteProduct(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM ProductTemp");
        return true;
    }
    private boolean deleteCompanySetting(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM CompanySettingTemp");
        return true;
    }
    private boolean deleteLocation(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM LocationTemp");
        return true;
    }
    private boolean deletePayment(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM PaymentTemp");
        return true;
    }
    private boolean deletePaymentMethod(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM PaymentMethodTemp");
        return true;
    }
    private boolean deleteLimitedDay(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM LimitedDayTemp");
        return true;
    }
    private boolean deleteBankPayment(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM BankPaymentTemp");
        return true;
    }
    private boolean deleteVoucherSetting(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM VoucherSettingTemp");
        return true;
    }
    private boolean deleteStaff(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM StaffTemp");
        return true;
    }
    public boolean deleteMasterSaleOrder(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM MasterSaleOrderTemp");
        return true;
    }
    public boolean deleteTranSaleOrder(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM TranSaleOrderTemp");
        return true;
    }
    public boolean deleteTranSaleOrderByProduct(int productId){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM TranSaleOrderTemp WHERE ProductID="+productId);
        return true;
    }
    public boolean deleteBluetoothPrinter(){
        database=openHelper.getWritableDatabase();
        database.execSQL("DELETE FROM BluetoothPrinter");
        return true;
    }
}
