package com.example.mieproveprogetto.controllers;

import com.example.mieproveprogetto.model.Email;
import com.example.mieproveprogetto.model.User;
import com.example.mieproveprogetto.utils.Message;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewEmailController {
    @FXML
    public Label lblFrom;
    @FXML
    public TextField toField;
    @FXML
    public TextField ccField;
    @FXML
    public TextField subjectField;
    @FXML
    public HTMLEditor txtEmail;

    private User usr;

    private MouseEvent toClose;

    public NewEmailController() {
        lblFrom = new Label();
        toField = new TextField();
        ccField = new TextField();
        subjectField = new TextField();
        txtEmail = new HTMLEditor();
    }

    @FXML
    public void initialize(){
        usr = EmailBoxController.getCurrentUser();
        lblFrom.textProperty().bind(usr.addressProperty());
        if(EmailBoxController.getAction() == 1) {
            toField.setText(EmailBoxController.getSender());
            subjectField.setText(EmailBoxController.getSubject());
        }
        if(EmailBoxController.getAction() == 2) {
            toField.setText(EmailBoxController.getSender());
            subjectField.setText(EmailBoxController.getSubject());
            ccField.setText(EmailBoxController.getReceiver());
        }
        if(EmailBoxController.getAction() == 3) {
            subjectField.setText(EmailBoxController.getSubject());
            txtEmail.setHtmlText(EmailBoxController.getText());
        }
    }

    @FXML
    public void sendEmail(MouseEvent event) {
        String sender = lblFrom.getText();
        String rec = toField.getText();
        ArrayList<String> receivers = new ArrayList<>();
        if(rec.contains(",")) {
            String[] temp = rec.split(",");
            for(String s : temp)
                receivers.add(s);
        } else {
            receivers.add(rec);
        }
        String subject = subjectField.getText();
        String text = getContent(txtEmail.getHtmlText());
        Email mailToSend = new Email(sender, receivers, subject, text);

        Message message = new Message(mailToSend.getSender(),"Mail", mailToSend);
        try {
            String host = InetAddress.getLocalHost().getHostName();
            Socket mySocket = new Socket(host, 9090);

            ObjectOutputStream writer = new ObjectOutputStream(mySocket.getOutputStream());
            writer.writeObject(message);
            writer.flush();

            ObjectInputStream reader = new ObjectInputStream(mySocket.getInputStream());
            Object response = reader.readObject();

            /* TODO: Chiusura finestra??? */

            if(response instanceof Message) {
                Message m = (Message) response;
                boolean content = (Boolean) m.getContent();
                if(content) {
                    writer.close();
                    reader.close();
                    mySocket.close();
                    cancel(event);
                    if(toClose != null) /// TODO: QUI
                        cancel(toClose);
                } else {
                    System.out.println(m.getAction()); /// TODO: QUI
                    this.toClose = event;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getContent(String htmlText) {
        String result = "";
        Pattern pattern = Pattern.compile("<[^>]*>");
        Matcher matcher = pattern.matcher(htmlText);
        final StringBuffer text = new StringBuffer(htmlText.length());

        while (matcher.find()) {
            matcher.appendReplacement(text, " ");
        }
        matcher.appendTail(text);
        result = text.toString().trim();
        return result;
    }
    @FXML
    public void cancel(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
