package com.bosictsolution.invsale.data;

public class SubMenuData {
    private int SubMenuID,MainMenuID;
    private String SubMenuName,PhotoUrl;

    public int getSubMenuID() {
        return SubMenuID;
    }

    public void setSubMenuID(int subMenuID) {
        SubMenuID = subMenuID;
    }

    public int getMainMenuID() {
        return MainMenuID;
    }

    public void setMainMenuID(int mainMenuID) {
        MainMenuID = mainMenuID;
    }

    public String getSubMenuName() {
        return SubMenuName;
    }

    public void setSubMenuName(String subMenuName) {
        SubMenuName = subMenuName;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }
}
