package com.example.mieproveprogetto.model;

import com.example.mieproveprogetto.utils.IdMailCounter;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class Email implements Serializable {
    private int id;
    private String sender;
    private ArrayList<String> receivers;
    private String subject;
    private String date;

    private String text;

    public Email(String sender, ArrayList<String> receivers, String subject, String text) {
        this.id = IdMailCounter.getCounter();
        this.sender = sender;
        this.receivers = receivers;
        this.subject = subject;
        this.date = new Date().toString().replace("CEST ", "");
        this.text = text;
    }

    public Email(int id, String sender, ArrayList<String> receivers, String subject, String text, String date) {
        this.id = id;
        this.sender = sender;
        this.receivers = receivers;
        this.subject = subject;
        this.date = date;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public ArrayList<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public int getId() { return id; }

    public String getText() { return text; }

    public String toString() {
        String rec = "[ ";
        for(String s : receivers)
            rec += ( s + " ], ");

        return "Sender: " + sender + ", Subject: " + subject + ", Receivers: " + rec +
                " Date :" + date.toString() + " Text: " + text + "";
    }
}
