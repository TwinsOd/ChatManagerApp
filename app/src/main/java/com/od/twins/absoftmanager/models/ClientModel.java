package com.od.twins.absoftmanager.models;

/**
 * Created by user on 1/26/2018.
 */

public class ClientModel {
    private String id;
    private String nickname;
    private boolean isConnected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
