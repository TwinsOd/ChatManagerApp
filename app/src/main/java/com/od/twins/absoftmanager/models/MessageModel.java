package com.od.twins.absoftmanager.models;

import static com.od.twins.absoftmanager.Constants.FILE_TYPE;
import static com.od.twins.absoftmanager.Constants.IMAGE_TYPE;
import static com.od.twins.absoftmanager.Constants.TEXT_TYPE;

/**
 * Created by user on 1/31/2018.
 */

public class MessageModel {
    private String type;
    private String name_client;
    private String name_image;
    private String path_local_image;
    private String text;
    private String time;

    public MessageModel(String name_client, String text, String type) {
        this.name_client = name_client;
        this.text = text;
        this.type = type;
    }

    public MessageModel(String name) {
        name_client = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName_client() {
        return name_client;
    }

    public void setName_client(String name_client) {
        this.name_client = name_client;
    }

    public String getName_image() {
        return name_image;
    }

    public void setName_image(String name_image) {
        this.name_image = name_image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIntType() {
        if (getType() == null)
            return TEXT_TYPE;
        switch (getType()) {
            case "text":
                return TEXT_TYPE;
            case "image":
                return IMAGE_TYPE;
            case "file":
                return FILE_TYPE;
            default:
                return 0;
        }
    }

    public String getPath_local_image() {
        return path_local_image;
    }

    public void setPath_local_image(String path_local_image) {
        this.path_local_image = path_local_image;
    }
}
