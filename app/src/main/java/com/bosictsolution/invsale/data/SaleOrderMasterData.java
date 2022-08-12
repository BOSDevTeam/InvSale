package com.bosictsolution.invsale.data;

public class SaleOrderMasterData {

    private String year,month,day,orderNumber,customerName,Remark;
    private int grandTotal,CustomerID,Tax,TaxAmt,Charges,ChargesAmt,Subtotal,Total;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
