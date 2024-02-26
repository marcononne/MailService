package com.example.mieproveprogetto.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerModel {
    private ObservableList<String> observableList;
    public ServerModel() {
        observableList = FXCollections.observableArrayList();
    }
    public void addToListLog(String serverString) {
        observableList.add(serverString);
    }
    public ObservableList<String> getObservableList() {
        return observableList;
    }

}
