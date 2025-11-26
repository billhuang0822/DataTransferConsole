package com.tsb.console.model;

public enum ServiceType {
    SK_FileRecv("檔案接收"),
    SK_CIF_Import("CIF檔匯入"),
    SK_DesigAcctImport("指定帳戶匯入"),
    SK_FreqAcctImport("常用帳戶匯入"),
    SKDesigAcct2TSB("指定帳戶轉TSB"),
    SKFreqAcct2TSB("常用帳戶轉TSB");

    private final String displayName;

    ServiceType(String displayName){
        this.displayName = displayName;
    }
    public String getDisplayName(){
        return displayName;
    }
}