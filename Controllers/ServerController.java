package com.example.mieproveprogetto.controllers;

import com.example.mieproveprogetto.model.Server;
import com.example.mieproveprogetto.model.ServerModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class ServerController implements Initializable {

    @FXML
    private ListView listLog;

    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;

    private Server server;

    private static ServerModel model;
    private static boolean running;

    public static ServerModel getCurrentModel() {
        return model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        server = new Server();
        model = new ServerModel();
        listLog.setItems(model.getObservableList());
        btnStop.setDisable(true);
        listLog.refresh();
    }

    @FXML
    public void startServer(MouseEvent event) {
        model.addToListLog("Server started");
        running = true;
        btnStart.setDisable(true);
        btnStop.setDisable(false);
    }

    @FXML
    public void stopServer(MouseEvent event) {
        model.addToListLog("Server stopped");
        running = false;
        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }

    public static boolean isRunning() {
        return running;
    }

}
