package com.bosictsolution.invsale.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ClientData implements Parcelable {
    private int ClientID,DivisionID,TownshipID;
    private String ClientName,ClientPassword,ShopName,Phone,Address,DivisionName,TownshipName;
    private boolean IsSalePerson;

    public ClientData(Parcel in) {
        ClientID = in.readInt();
        DivisionID = in.readInt();
        TownshipID = in.readInt();
        ClientName = in.readString();
        ClientPassword = in.readString();
        ShopName = in.readString();
        Phone = in.readString();
        Address = in.readString();
        DivisionName = in.readString();
        TownshipName = in.readString();
        IsSalePerson = in.readByte() != 0;
    }

    public static final Creator<ClientData> CREATOR = new Creator<ClientData>() {
        @Override
        public ClientData createFromParcel(Parcel in) {
            return new ClientData(in);
        }

        @Override
        public ClientData[] newArray(int size) {
            return new ClientData[size];
        }
    };

    public ClientData() {

    }

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

    public String getDivisionName() {
        return DivisionName;
    }

    public void setDivisionName(String divisionName) {
        DivisionName = divisionName;
    }

    public String getTownshipName() {
        return TownshipName;
    }

    public void setTownshipName(String townshipName) {
        TownshipName = townshipName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ClientID);
        parcel.writeInt(DivisionID);
        parcel.writeInt(TownshipID);
        parcel.writeString(ClientName);
        parcel.writeString(ClientPassword);
        parcel.writeString(ShopName);
        parcel.writeString(Phone);
        parcel.writeString(Address);
        parcel.writeString(DivisionName);
        parcel.writeString(TownshipName);
        parcel.writeByte((byte) (IsSalePerson ? 1 : 0));
    }
}
