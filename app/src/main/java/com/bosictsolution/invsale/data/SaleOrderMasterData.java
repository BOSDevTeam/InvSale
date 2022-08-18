package com.bosictsolution.invsale.data;

import java.util.List;

public class SaleOrderMasterData {

    private String Year, Month, Day, OrderNumber, CustomerName,Remark,OrderDateTime;
    private int CustomerID,Tax,TaxAmt,Charges,ChargesAmt,Subtotal,Total,ClientID,SaleOrderID;
    private List<SaleOrderTranData> lstSaleOrderTran;

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        this.Year = year;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        this.Month = month;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        this.Day = day;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.OrderNumber = orderNumber;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        this.CustomerName = customerName;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public int getTax() {
        return Tax;
    }

    public void setTax(int tax) {
        Tax = tax;
    }

    public int getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(int taxAmt) {
        TaxAmt = taxAmt;
    }

    public int getCharges() {
        return Charges;
    }

    public void setCharges(int charges) {
        Charges = charges;
    }

    public int getChargesAmt() {
        return ChargesAmt;
    }

    public void setChargesAmt(int chargesAmt) {
        ChargesAmt = chargesAmt;
    }

    public int getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(int subtotal) {
        Subtotal = subtotal;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public List<SaleOrderTranData> getLstSaleOrderTran() {
        return lstSaleOrderTran;
    }

    public void setLstSaleOrderTran(List<SaleOrderTranData> lstSaleOrderTran) {
        this.lstSaleOrderTran = lstSaleOrderTran;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public int getSaleOrderID() {
        return SaleOrderID;
    }

    public void setSaleOrderID(int saleOrderID) {
        SaleOrderID = saleOrderID;
    }

    public String getOrderDateTime() {
        return OrderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        OrderDateTime = orderDateTime;
    }
}
