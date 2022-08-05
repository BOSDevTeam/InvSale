package com.bosictsolution.invsale.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bosictsolution.invsale.data.SaleMasterData;
import com.bosictsolution.invsale.data.SaleTranData;

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
        cv.put("Tax", data.getTaxAmt());
        cv.put("Charges", data.getChargesAmt());
        cv.put("Total", data.getTotalAmt());
        cv.put("VoucherDiscount", data.getVoucherDis());
        cv.put("AdvancedPay", data.getAdvancedPayAmt());
        cv.put("PayPercentAmt", data.getPayPercentAmt());
        cv.put("Grandtotal", data.getNetAmt());
        cv.put("Remark", data.getRemark());
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
            data.setTaxAmt(cur.getInt(10));
            data.setChargesAmt(cur.getInt(11));
            data.setTotalAmt(cur.getInt(9));
            data.setVoucherDis(cur.getInt(13));
            data.setAdvancedPayAmt(cur.getInt(14));
            data.setPayPercentAmt(cur.getInt(15));
            data.setNetAmt(cur.getInt(16));
            data.setRemark(cur.getString(17));
        }
        return data;
    }
    public boolean insertTranSale(List<SaleTranData> list){
        deleteTranSale();
        database = openHelper.getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            cv.put("ProductID", list.get(i).getProductID());
            cv.put("Quantity", list.get(i).getQuantity());
            cv.put("SalePrice", list.get(i).getSalePrice());
            cv.put("Amount", list.get(i).getTotalAmount());
            cv.put("IsFOC", list.get(i).isFOC());
            cv.put("ProductName",list.get(i).getProductName());
            database.insert("TranSaleTemp", null, cv);
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
            data.setTotalAmount(cur.getInt(3));
            int foc = cur.getInt(4);
            if (foc == 0) data.setFOC(false);
            else if (foc == 1) data.setFOC(true);
            number+=1;
            data.setNumber(number);
            data.setProductName(cur.getString(5));
            list.add(data);
        }
        return list;
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
}
