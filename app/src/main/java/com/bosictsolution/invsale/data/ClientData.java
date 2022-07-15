package com.bosictsolution.invsale.data;

public class ClientData {
    private int ClientID,DivisionID,TownshipID;
    private String ClientName,ClientPassword,ShopName,Phone,Address;
    private boolean IsSalePerson;

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public int getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(int divisionID) {
        DivisionID = divisionID;
    }

    public int getTownshipID() {
        return TownshipID;
    }

    public void setTownshipID(int townshipID) {
        TownshipID = townshipID;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getClientPassword() {
        return ClientPassword;
    }

    public void setClientPassword(String clientPassword) {
        ClientPassword = clientPassword;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public boolean isSalePerson() {
        return IsSalePerson;
    }

    public void setSalePerson(boolean salePerson) {
        IsSalePerson = salePerson;
    }
}
