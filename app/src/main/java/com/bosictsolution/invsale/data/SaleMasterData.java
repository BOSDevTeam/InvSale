package com.bosictsolution.invsale.data;

import java.util.List;

public class SaleMasterData {

    private String SaleDateTime, SystemVoucherNo, CustomerName,Remark,UserVoucherNo,ClientName;
    private int Grandtotal;
    private int CustomerID;
    private int LocationID;

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    private int PaymentID;
    private int VoucherDiscount;
    private int AdvancedPay;
    private int TaxAmt;
    private int ChargesAmt;
    private int Total;
    private int VouDisPercent;
    private int VouDisAmount;
    private int PayMethodID;
    private int BankPaymentID;
    private int PaymentPercent;
    private int ClientID;
    private int LimitedDayID;
    private int PayPercentAmt;
    private int Subtotal;
    private int Tax;
    private int Charges;
    private int StaffID;
    private int SaleID;

    public int getSaleID() {
        return SaleID;
    }

    public void setSaleID(int saleID) {
        SaleID = saleID;
    }

    public int getSlipID() {
        return SlipID;
    }

    public void setSlipID(int slipID) {
        SlipID = slipID;
    }

    private int SlipID;
    private boolean IsClientSale,IsDefaultCustomer,IsAdvancedPay;
    private List<SaleTranData> LstSaleTran,LstSaleTranLog;

    public List<SaleTranData> getLstSaleTranLog() {
        return LstSaleTranLog;
    }

    public void setLstSaleTranLog(List<SaleTranData> lstSaleTranLog) {
        LstSaleTranLog = lstSaleTranLog;
    }

    public String getSaleDateTime() {
        return SaleDateTime;
    }

    public void setSaleDateTime(String saleDateTime) {
        this.SaleDateTime = saleDateTime;
    }

    public String getSystemVoucherNo() {
        return SystemVoucherNo;
    }

    public void setSystemVoucherNo(String systemVoucherNo) {
        this.SystemVoucherNo = systemVoucherNo;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        this.CustomerName = customerName;
    }

    public int getGrandtotal() {
        return Grandtotal;
    }

    public void setGrandtotal(int grandtotal) {
        this.Grandtotal = grandtotal;
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

    public int getVoucherDiscount() {
        return VoucherDiscount;
    }

    public void setVoucherDiscount(int voucherDiscount) {
        VoucherDiscount = voucherDiscount;
    }

    public int getAdvancedPay() {
        return AdvancedPay;
    }

    public void setAdvancedPay(int advancedPay) {
        AdvancedPay = advancedPay;
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

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
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

    public boolean isClientSale() {
        return IsClientSale;
    }

    public void setClientSale(boolean clientSale) {
        IsClientSale = clientSale;
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
        return LstSaleTran;
    }

    public void setLstSaleTran(List<SaleTranData> lstSaleTran) {
        this.LstSaleTran = lstSaleTran;
    }

    public String getUserVoucherNo() {
        return UserVoucherNo;
    }

    public void setUserVoucherNo(String userVoucherNo) {
        UserVoucherNo = userVoucherNo;
    }

    public int getTax() {
        return Tax;
    }

    public void setTax(int tax) {
        Tax = tax;
    }

    public int getCharges() {
        return Charges;
    }

    public void setCharges(int charges) {
        Charges = charges;
    }

    public int getStaffID() {
        return StaffID;
    }

    public void setStaffID(int staffID) {
        StaffID = staffID;
    }
}
