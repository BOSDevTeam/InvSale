package com.bosictsolution.invsale.data;

import java.util.List;

public class BillData {
    String title1,title2,title3,title4,message1,message2,date,userName,customerName,saleNumber;
    int subtotal,total,grandTotal,charges,advancedPay,tax,voucherDiscount;
    List<SaleTranData> lstTranSale;

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public String getTitle4() {
        return title4;
    }

    public void setTitle4(String title4) {
        this.title4 = title4;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public int getAdvancedPay() {
        return advancedPay;
    }

    public void setAdvancedPay(int advancedPay) {
        this.advancedPay = advancedPay;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(int voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public List<SaleTranData> getLstTranSale() {
        return lstTranSale;
    }

    public void setLstTranSale(List<SaleTranData> lstTranSale) {
        this.lstTranSale = lstTranSale;
    }
}
