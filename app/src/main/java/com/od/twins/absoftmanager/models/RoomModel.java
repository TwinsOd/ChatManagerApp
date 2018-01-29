package com.od.twins.absoftmanager.models;

import java.util.List;

/**
 * Created by user on 1/26/2018.
 */

public class RoomModel {
    private String roomName;
    private String nicknameCreator;
    private List<ClientModel> listClient;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getNicknameCreator() {
        return nicknameCreator;
    }

    public void setNicknameCreator(String nicknameCreator) {
        this.nicknameCreator = nicknameCreator;
    }

    public List<ClientModel> getListClient() {
        return listClient;
    }

    public void setListClient(List<ClientModel> listClient) {
        this.listClient = listClient;
    }
}
