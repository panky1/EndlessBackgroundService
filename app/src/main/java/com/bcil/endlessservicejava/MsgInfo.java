package com.bcil.endlessservicejava;

public class MsgInfo {
    private String data;
    private String timeinfo;

    public MsgInfo() {
    }

    public MsgInfo(String data, String timeinfo) {
        this.data = data;
        this.timeinfo = timeinfo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimeinfo() {
        return timeinfo;
    }

    public void setTimeinfo(String timeinfo) {
        this.timeinfo = timeinfo;
    }
}
