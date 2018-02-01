package com.od.twins.absoftmanager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by user on 1/26/2018.
 */

public class Application extends android.app.Application {

    private Socket mSocket;
    private String mNickName;

    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String mNickName) {
        if (this.mNickName == null && mNickName != null)
            this.mNickName = mNickName;
    }
}
