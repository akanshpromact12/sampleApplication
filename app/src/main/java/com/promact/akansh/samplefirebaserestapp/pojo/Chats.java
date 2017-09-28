package com.promact.akansh.samplefirebaserestapp.pojo;

import com.google.gson.annotations.SerializedName;

public class Chats {
    @SerializedName("userFrom")
    public String userFrom;
    @SerializedName("userTo")
    public String userTo;
    @SerializedName("Msg")
    public String Msg;
    @SerializedName("sendRecvPair")
    public String sendRecvPair;
    @SerializedName("Time")
    public String Time;
    @SerializedName("unread")
    public String unread;

    public Chats(String userFrom, String userTo, String Msg, String Time,
                 String sendRecvPair, String unread) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.Msg = Msg;
        this.Time = Time;
        this.sendRecvPair = sendRecvPair;
        this.unread = unread;
    }
}
