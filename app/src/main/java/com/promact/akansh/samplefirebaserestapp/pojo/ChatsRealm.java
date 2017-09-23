package com.promact.akansh.samplefirebaserestapp.pojo;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Akansh on 09-09-2017.
 */

public class ChatsRealm extends RealmObject {
    @PrimaryKey
    private String chatId;
    private String userFrom;
    private String userTo;
    private String Msg;
    private String Time;
    private Boolean netAvailable;
    private String uploadCombo;
    private int chatsInNumber;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) { Time = time; }

    public Boolean getNetAvailable() { return netAvailable; }

    public void setNetAvailable(Boolean netAvailable) { this.netAvailable = netAvailable; }

    public String getUploadCombo() { return uploadCombo; }

    public void setUploadCombo(String uploadCombo) { this.uploadCombo = uploadCombo; }

    public int getChatsInNumber() { return chatsInNumber; }

    public void setChatsInNumber(int chatsInNumber) { this.chatsInNumber = chatsInNumber; }
}
