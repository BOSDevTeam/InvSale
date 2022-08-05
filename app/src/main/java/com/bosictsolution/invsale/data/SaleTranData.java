package com.bosictsolution.invsale.data;

public class SaleTranData {

    private String ProductName;
    private int ProductID,Number,Quantity, SalePrice, TotalAmount, DefaultSalePrice,TranID;
    private boolean IsFOC;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        this.Number = number;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        this.Quantity = quantity;
    }

    public int getSalePrice() {
        return SalePrice;
    }

    public void setSalePrice(int salePrice) {
        this.SalePrice = salePrice;
    }

    public int getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.TotalAmount = totalAmount;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public boolean isFOC() {
        return IsFOC;
    }

    public void setFOC(boolean FOC) {
        IsFOC = FOC;
    }

    public int getDefaultSalePrice() {
        return DefaultSalePrice;
    }

    public void setDefaultSalePrice(int defaultSalePrice) {
        DefaultSalePrice = defaultSalePrice;
    }

    public int getTranID() {
        return TranID;
    }

    public void setTranID(int tranID) {
        TranID = tranID;
    }
}
