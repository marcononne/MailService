package com.example.mieproveprogetto.controllers;

import com.example.mieproveprogetto.ClientMain;
import com.example.mieproveprogetto.utils.Message;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class LoginController {
    @FXML
    private TextField emailAddressField;

    private final static StringProperty my_address = new SimpleStringProperty(" ");

    private EmailBoxController ebc;

    public static StringProperty addressProperty() {
    return my_address;
  }
    @FXML
    private void login(MouseEvent event) {
        String address = emailAddressField.getText();
        my_address.set(address);
        try {
            String host = InetAddress.getLocalHost().getHostName();
            Socket mySocket = new Socket(host, 9090);

            ObjectOutputStream writer = new ObjectOutputStream(mySocket.getOutputStream());
            Message message = new Message(address, "Login", address);
            writer.writeObject(message);
            writer.flush();

            ObjectInputStream reader = new ObjectInputStream(mySocket.getInputStream());
            Message response = (Message) reader.readObject();
            Object content = response.getContent();
            mySocket.close();
            if(content instanceof Boolean) {
                boolean check = (Boolean) content;
                if(check) {
                    EmailBoxController ebc = new EmailBoxController();
                    FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("EmailBox.fxml"));
                    Parent emailBoxParent = fxmlLoader.load();

                    Stage emailBoxStage = new Stage();
                    emailBoxStage.setScene(new Scene(emailBoxParent));
                    emailBoxStage.show();
                    ClientMain.closeWindow();
                } else {
                    System.out.println(response.getAction());
                }
            writer.close();
            reader.close();
            } else
                throw new ClassNotFoundException("I didn't receive a boolean value from the server: ");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while loading the EmailBox after logging in: \n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
