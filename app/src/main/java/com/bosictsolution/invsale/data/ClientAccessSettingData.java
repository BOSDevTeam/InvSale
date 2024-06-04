package com.bosictsolution.invsale.data;

public class ClientAccessSettingData {

    private short IsEditAccessClientApp,IsDeleteAccessClientApp;

    public short isEditAccessClientApp() {
        return IsEditAccessClientApp;
    }

    public void setEditAccessClientApp(short editAccessClientApp) {
        IsEditAccessClientApp = editAccessClientApp;
    }

    public short isDeleteAccessClientApp() {
        return IsDeleteAccessClientApp;
    }

    public void setDeleteAccessClientApp(short deleteAccessClientApp) {
        IsDeleteAccessClientApp = deleteAccessClientApp;
    }
}
