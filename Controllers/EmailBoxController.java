package com.example.mieproveprogetto.controllers;

import com.example.mieproveprogetto.ClientMain;
import com.example.mieproveprogetto.model.Email;
import com.example.mieproveprogetto.model.User;
import com.example.mieproveprogetto.utils.Message;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class EmailBoxController {
  @FXML
    private ListView<Email> sideMailList;
    @FXML
    private TextArea txtArea;
    @FXML
    private Label lblUser;
    @FXML
    private Label lblSubject;
    @FXML
    private Label lblFrom;
    @FXML
    private Label lblDate;
    private static Email selectedEmail;
    private final Email emptyEmail = new Email(-1, "", new ArrayList<>(), "", "", "");
    private static User usr;
    private static String my_address;
    private static int action;
    Timeline five = new Timeline(new KeyFrame(Duration.seconds(5),
        new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                requestFillInbox();
            }
        }
    ));

    public EmailBoxController() {
        selectedEmail = null;
        sideMailList = new ListView<>();
        lblUser = new Label();
        lblFrom = new Label();
        lblSubject = new Label();
        lblDate = new Label();
        txtArea = new TextArea();
    }

    public void initialize() {
        usr = new User(LoginController.addressProperty().get());
        my_address = usr.getAddress();

        requestFillInbox();

        sideMailList.itemsProperty().bind(usr.inboxProperty());
        sideMailList.setOnMouseClicked(this::showSelectedEmail);
        lblUser.textProperty().bind(usr.addressProperty());
        updateDetailView(emptyEmail);

        five.setCycleCount(Timeline.INDEFINITE);
        five.play();
    }

    private void requestFillInbox() {
        try {
            String host = InetAddress.getLocalHost().getHostName();
            Socket mySocket = new Socket(host, 9090);

            ObjectOutputStream writer = new ObjectOutputStream(mySocket.getOutputStream());
            Message message = new Message(my_address,"fillInbox", my_address);
            writer.writeObject(message);
            writer.flush();

            ObjectInputStream reader = new ObjectInputStream(mySocket.getInputStream());
            Object condition = reader.readObject();
            if(condition instanceof Email)
                usr.clearAll();
            while(condition instanceof Email) {
                Email newMail = (Email) condition;
                condition = reader.readObject();
                usr.add(newMail);
            }
            if(condition instanceof Message) {
                System.out.println("Errore nel fillInbox: " + ((Message)condition).getAction() );
                reader.close();
                writer.close();
                mySocket.close();
            } else {
                reader.close();
                writer.close();
                mySocket.close();
            }
        } catch (Exception e) {
            System.out.println("Error client side while trying to fill the inbox: " + e.getMessage());
        }
    }

    private boolean requestDeleteMail(Email target, String address) {
        try {
            String host = InetAddress.getLocalHost().getHostName();
            Socket mySocket = new Socket(host, 9090);

            ObjectOutputStream writer = new ObjectOutputStream(mySocket.getOutputStream());
            Message message = new Message(my_address, "DeleteMail", target);
            writer.writeObject(message);
            writer.flush();

            ObjectInputStream reader = new ObjectInputStream(mySocket.getInputStream());
            Object condition = reader.readObject();
            if(condition instanceof Boolean) {
                boolean cond = (Boolean) condition;
                if(cond) {
                    usr.clearAll();
                    requestFillInbox();
                    updateDetailView(emptyEmail);
                    return true;
                } else {
                    throw new Exception();
                }
            }

            reader.close();
            writer.close();
            mySocket.close();
            return false;
        } catch (Exception e) {
            System.out.println("Error while trying to delete an email from " + address + " 's inbox");
        }
        return false;
    }
    @FXML
    private void delete() {
        if(requestDeleteMail(selectedEmail, my_address)) {
            usr.inboxProperty().remove(selectedEmail);
            usr.decCounterMail();
        }
    }
    @FXML
    private void newMail() {
        try {
            NewEmailController newEmailController = new NewEmailController();
            Parent newEmailParent = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("NewEmail.fxml")));
            Stage newEmailStage = new Stage();
            newEmailStage.setScene(new Scene(newEmailParent));
            newEmailStage.show();
        } catch (IOException e) {
            System.out.println("An error occurred while creating a new mail: \n" + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void reply() {
        try {
            action = 1;
            NewEmailController newEmailController = new NewEmailController();
            Parent replyParent = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("NewEmail.fxml")));

            selectedEmail = sideMailList.getSelectionModel().getSelectedItem();

            Stage replyStage = new Stage();
            replyStage.setScene(new Scene(replyParent));
            replyStage.show();
        } catch (IOException e) {
            System.out.println("An error occurred while replying to a mail: \n" + e.getMessage());
        }
    }
    @FXML
    private void replyAll() {
        try {
            action = 2;
            NewEmailController newEmailController = new NewEmailController();
            Parent replyAllParent = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("NewEmail.fxml")));

            selectedEmail = sideMailList.getSelectionModel().getSelectedItem();

            Stage replyAllStage = new Stage();
            replyAllStage.setScene(new Scene(replyAllParent));
            replyAllStage.show();
        } catch (IOException e) {
            System.out.println("An error occurred while replying to everyone: \n" + e.getMessage());
        }
    }
    @FXML
    private void forward() {
        try {
            action = 3;
            NewEmailController newEmailController = new NewEmailController();
            Parent forwardParent = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("NewEmail.fxml")));

            selectedEmail = sideMailList.getSelectionModel().getSelectedItem();

            Stage forwardStage = new Stage();
            forwardStage.setScene(new Scene(forwardParent));
            forwardStage.show();
        } catch (IOException e) {
            System.out.println("An error occurred while forwarding an email: \n" + e.getMessage());
        }
    }

    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = sideMailList.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        updateDetailView(email);
    }
    private void updateDetailView(Email mail) {
        if(mail != null) {
            lblFrom.setText(mail.getSender());
            lblSubject.setText(mail.getSubject());
            lblDate.setText(mail.getDate());
            txtArea.setText(mail.getText());
        }
    }
    public static User getCurrentUser() { return usr; }

    public static int getAction() { return action; }

    public static String getSender() {
        return selectedEmail.getSender();
    }

    public static String getSubject() {
        return "RE: " + selectedEmail.getSubject();
    }

    public static String getReceiver() {
        String ris = "(";
        for(String rec : selectedEmail.getReceivers())
            ris += rec + ",";
        ris += ")";
        ris = ris.replace(",)", ")").replace("(", "").replace(")", "");
        ris = ris.replace(my_address, "");
        return ris;
    }

    public static String getText() {
        return "Sender: " + selectedEmail.getSender() + "\n Text: " + selectedEmail.getText();
    }
}
