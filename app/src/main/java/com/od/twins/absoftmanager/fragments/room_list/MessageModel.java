package com.od.twins.absoftmanager.fragments.room_list;

/**
 * Created by user on 1/26/2018.
 */

public class MessageModel {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    public MessageModel(String msg, String name, int type) {
        mMessage = msg;
        mUsername = name;
        mType = type;
    }

    private int mType;
    private String mMessage;
    private String mUsername;

    public int getType() {
        return mType;
    }

    ;

    public String getMessage() {
        return mMessage;
    }

    ;

    public String getUsername() {
        return mUsername;
    }

    ;
}
