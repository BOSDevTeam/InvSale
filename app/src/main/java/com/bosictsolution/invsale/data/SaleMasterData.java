package com.bosictsolution.invsale.data;

import java.util.List;

public class SaleMasterData {

    private String Date, Voucher, CustomerName,Remark;
    private int NetAmt,CustomerID,LocationID,PaymentID,VoucherDis,AdvancedPayAmt,TaxAmt,ChargesAmt,
                TotalAmt,VouDisPercent,VouDisAmount,PayMethodID,BankPaymentID,PaymentPercent,ClientID,LimitedDayID,
                PayPercentAmt,Subtotal;
    private boolean IsClient,IsDefaultCustomer,IsAdvancedPay;
    private List<SaleTranData> lstSaleTran;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getVoucher() {
        return Voucher;
    }

    public void setVoucher(String voucher) {
        this.Voucher = voucher;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        this.CustomerName = customerName;
    }

    public int getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(int netAmt) {
        this.NetAmt = netAmt;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public int getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(int paymentID) {
        PaymentID = paymentID;
    }

    public int getVoucherDis() {
        return VoucherDis;
    }

    public void setVoucherDis(int voucherDis) {
        VoucherDis = voucherDis;
    }

    public int getAdvancedPayAmt() {
        return AdvancedPayAmt;
    }

    public void setAdvancedPayAmt(int advancedPayAmt) {
        AdvancedPayAmt = advancedPayAmt;
    }

    public int getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(int taxAmt) {
        TaxAmt = taxAmt;
    }

    public int getChargesAmt() {
        return ChargesAmt;
    }

    public void setChargesAmt(int chargesAmt) {
        ChargesAmt = chargesAmt;
    }

    public int getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(int totalAmt) {
        TotalAmt = totalAmt;
    }

    public int getVouDisPercent() {
        return VouDisPercent;
    }

    public void setVouDisPercent(int vouDisPercent) {
        VouDisPercent = vouDisPercent;
    }

    public int getVouDisAmount() {
        return VouDisAmount;
    }

    public void setVouDisAmount(int vouDisAmount) {
        VouDisAmount = vouDisAmount;
    }

    public int getPayMethodID() {
        return PayMethodID;
    }

    public void setPayMethodID(int payMethodID) {
        PayMethodID = payMethodID;
    }

    public int getBankPaymentID() {
        return BankPaymentID;
    }

    public void setBankPaymentID(int bankPaymentID) {
        BankPaymentID = bankPaymentID;
    }

    public int getPaymentPercent() {
        return PaymentPercent;
    }

    public void setPaymentPercent(int paymentPercent) {
        PaymentPercent = paymentPercent;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public int getLimitedDayID() {
        return LimitedDayID;
    }

    public void setLimitedDayID(int limitedDayID) {
        LimitedDayID = limitedDayID;
    }

    public int getPayPercentAmt() {
        return PayPercentAmt;
    }

    public void setPayPercentAmt(int payPercentAmt) {
        PayPercentAmt = payPercentAmt;
    }

    public boolean isClient() {
        return IsClient;
    }

    public void setClient(boolean client) {
        IsClient = client;
    }

    public int getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(int subtotal) {
        Subtotal = subtotal;
    }

    public boolean isDefaultCustomer() {
        return IsDefaultCustomer;
    }

    public void setDefaultCustomer(boolean defaultCustomer) {
        IsDefaultCustomer = defaultCustomer;
    }

    public boolean isAdvancedPay() {
        return IsAdvancedPay;
    }

    public void setAdvancedPay(boolean advancedPay) {
        IsAdvancedPay = advancedPay;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public List<SaleTranData> getLstSaleTran() {
        return lstSaleTran;
    }

    public void setLstSaleTran(List<SaleTranData> lstSaleTran) {
        this.lstSaleTran = lstSaleTran;
    }
}
