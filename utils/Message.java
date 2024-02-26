package com.example.mieproveprogetto.utils;

import java.io.Serializable;

public class Message implements Serializable {

    private String address;
    private String action;
    private Object content;


    public Message(String address, String action, Object content) {
        this.address = address;
        this.action = action;
        this.content = content;
    }

    public String getAddress() { return address; }

    public Object getContent() {
        return content;
    }

    public String getAction() {
        return action;
    }
}
