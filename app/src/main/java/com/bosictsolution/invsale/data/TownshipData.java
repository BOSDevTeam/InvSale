package com.bosictsolution.invsale.data;

public class TownshipData {
    private int TownshipID,DivisionID;
    private String TownshipName;

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

    public String getTownshipName() {
        return TownshipName;
    }

    public void setTownshipName(String townshipName) {
        TownshipName = townshipName;
    }
}
