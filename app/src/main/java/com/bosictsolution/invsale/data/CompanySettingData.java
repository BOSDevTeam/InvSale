package com.bosictsolution.invsale.data;

public class CompanySettingData {
    private int Tax;
    private int ServiceCharges;

    public int getIsClientPhoneVerify() {
        return IsClientPhoneVerify;
    }

    public void setIsClientPhoneVerify(int isClientPhoneVerify) {
        IsClientPhoneVerify = isClientPhoneVerify;
    }

    private int IsClientPhoneVerify;
    private String HomeCurrency;

    public int getTax() {
        return Tax;
    }

    public void setTax(int tax) {
        Tax = tax;
    }

    public int getServiceCharges() {
        return ServiceCharges;
    }

    public void setServiceCharges(int serviceCharges) {
        ServiceCharges = serviceCharges;
    }

    public String getHomeCurrency() {
        return HomeCurrency;
    }

    public void setHomeCurrency(String homeCurrency) {
        HomeCurrency = homeCurrency;
    }
}
