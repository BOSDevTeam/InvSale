package com.bosictsolution.invsale.data;

public class ProductData {
    private String ProductName, Code;
    private int ProductID,SalePrice,SubMenuID;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public int getSalePrice() {
        return SalePrice;
    }

    public void setSalePrice(int salePrice) {
        this.SalePrice = salePrice;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    public int getSubMenuID() {
        return SubMenuID;
    }

    public void setSubMenuID(int subMenuID) {
        SubMenuID = subMenuID;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }
}
