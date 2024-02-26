package com.example.mieproveprogetto.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.LinkedList;

public class User {
    private final StringProperty address;

    private static ListProperty<Email> inbox;

    private final ObservableList<Email> inboxContent;

    private static int counterMail = 0;

    public User(String address) {
        this.address = new SimpleStringProperty(address);
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);
    }
    public StringProperty addressProperty() {
        return address;
    }

    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    public ObservableList<Email> inboxContentProperty() { return inboxContent; }

    public void add(Email e) {
        inboxContent.add(e);
    }

    public static void removeMail(Email e) { inbox.remove(e); }

    public String getAddress() {
      return address.get();
    }

    public void setAddress(String emailAddress) {
    this.address.set(emailAddress);
  }

    public static void incCounterMail() {
        counterMail++;
    }

    public void decCounterMail() { counterMail--; }

    public void clearAll() {
    inbox.clear();
  }

}
