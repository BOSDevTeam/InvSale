package com.bosictsolution.invsale.data;

public class CustomerData {
    private int CustomerID,TownshipID,DivisionID;
    private String CustomerName,Contact,Address,Phone,Email;
    private boolean IsDefault;

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public void setDefault(boolean aDefault) {
        IsDefault = aDefault;
    }

    public int getTownshipID() {
        return TownshipID;
    }

    public void setTownshipID(int townshipID) {
        TownshipID = townshipID;
    }

    public int getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(int divisionID) {
        DivisionID = divisionID;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
