package com.promact.akansh.samplefirebaserestapp.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akansh on 09-09-2017.
 */

public class Chats {
    @SerializedName("userFrom")
    public String userFrom;
    @SerializedName("userTo")
    public String userTo;
    @SerializedName("Msg")
    public String Msg;
    @SerializedName("Time")
    public String Time;

    public Chats(String userFrom, String userTo, String Msg, String Time) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.Msg = Msg;
        this.Time = Time;
    }
}
