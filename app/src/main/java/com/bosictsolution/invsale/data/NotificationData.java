package com.bosictsolution.invsale.data;

public class NotificationData {
    private short NotiType;
    private int NotiID;
    private String NotiMessage,NotiDateTime;

    public short getNotiType() {
        return NotiType;
    }

    public void setNotiType(short notiType) {
        NotiType = notiType;
    }

    public String getNotiMessage() {
        return NotiMessage;
    }

    public void setNotiMessage(String notiMessage) {
        NotiMessage = notiMessage;
    }

    public String getNotiDateTime() {
        return NotiDateTime;
    }

    public void setNotiDateTime(String notiDateTime) {
        NotiDateTime = notiDateTime;
    }

    public int getNotiID() {
        return NotiID;
    }

    public void setNotiID(int notiID) {
        NotiID = notiID;
    }
}
