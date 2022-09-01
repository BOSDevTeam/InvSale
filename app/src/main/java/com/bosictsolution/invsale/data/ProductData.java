package com.bosictsolution.invsale.data;

public class ProductData {
    private String Code,ProductName,PhotoUrl,Description;
    private int ProductID,SalePrice,SubMenuID,Quantity;

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

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
