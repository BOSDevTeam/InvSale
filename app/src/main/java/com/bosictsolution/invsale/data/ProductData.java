package com.bosictsolution.invsale.data;

public class ProductData {
    private String productName,productCode;
    private int salePrice,SubMenuID;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getSubMenuID() {
        return SubMenuID;
    }

    public void setSubMenuID(int subMenuID) {
        SubMenuID = subMenuID;
    }
}
