package com.bosictsolution.invsale.data;

public class VoucherSettingData {
    private String HeaderName,HeaderDesp,HeaderPhone,HeaderAddress,OtherHeader1,OtherHeader2,
                    FooterMessage1,FooterMessage2,FooterMessage3,VoucherLogoUrl;
    private byte[] VoucherLogo;

    public String getHeaderName() {
        return HeaderName;
    }

    public void setHeaderName(String headerName) {
        HeaderName = headerName;
    }

    public String getHeaderDesp() {
        return HeaderDesp;
    }

    public void setHeaderDesp(String headerDesp) {
        HeaderDesp = headerDesp;
    }

    public String getHeaderPhone() {
        return HeaderPhone;
    }

    public void setHeaderPhone(String headerPhone) {
        HeaderPhone = headerPhone;
    }

    public String getHeaderAddress() {
        return HeaderAddress;
    }

    public void setHeaderAddress(String headerAddress) {
        HeaderAddress = headerAddress;
    }

    public String getOtherHeader1() {
        return OtherHeader1;
    }

    public void setOtherHeader1(String otherHeader1) {
        OtherHeader1 = otherHeader1;
    }

    public String getOtherHeader2() {
        return OtherHeader2;
    }

    public void setOtherHeader2(String otherHeader2) {
        OtherHeader2 = otherHeader2;
    }

    public String getFooterMessage1() {
        return FooterMessage1;
    }

    public void setFooterMessage1(String footerMessage1) {
        FooterMessage1 = footerMessage1;
    }

    public String getFooterMessage2() {
        return FooterMessage2;
    }

    public void setFooterMessage2(String footerMessage2) {
        FooterMessage2 = footerMessage2;
    }

    public String getFooterMessage3() {
        return FooterMessage3;
    }

    public void setFooterMessage3(String footerMessage3) {
        FooterMessage3 = footerMessage3;
    }

    public byte[] getVoucherLogo() {
        return VoucherLogo;
    }

    public void setVoucherLogo(byte[] voucherLogo) {
        VoucherLogo = voucherLogo;
    }

    public String getVoucherLogoUrl() {
        return VoucherLogoUrl;
    }

    public void setVoucherLogoUrl(String voucherLogoUrl) {
        VoucherLogoUrl = voucherLogoUrl;
    }
}
