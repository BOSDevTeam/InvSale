package com.bosictsolution.invsale.data;

public class ConnectionData {
    private int type;
    private boolean isConnected;

    public ConnectionData(int type, boolean isConnected) {
        this.type = type;
        this.isConnected = isConnected;
    }

    public int getType() {
        return type;
    }

    public boolean getIsConnected() {
        return isConnected;
    }
}
