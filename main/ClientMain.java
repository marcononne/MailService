package com.example.mieproveprogetto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * TODO: Notifiche per nuove mail
 * TODO: Messaggi di errore in generale e per il server spento -> sia gli alert che da far comparire nel log
 * TODO: CC
 * */

public class ClientMain extends Application {
    private static Stage my_Stage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        my_Stage = stage;
        stage.setTitle("LoginWindow");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void closeWindow() {
        my_Stage.close();
    }
}
