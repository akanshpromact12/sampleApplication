package com.promact.akansh.samplefirebaserestapp.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akansh on 19-09-2017.
 */

public class Users {
    @SerializedName("user")
    public String userName;

    public Users(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
