package com.example.mieproveprogetto;

import com.example.mieproveprogetto.controllers.EmailBoxController;
import com.example.mieproveprogetto.controllers.ServerController;
import com.example.mieproveprogetto.model.Email;
import com.example.mieproveprogetto.model.ServerModel;
import com.example.mieproveprogetto.model.User;
import com.example.mieproveprogetto.utils.Message;
import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerRunnable implements Runnable {
    private User user;
    private ServerModel model;
    private static Socket socket;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;

    public ServerRunnable(Socket socket) throws IOException {
        this.socket = socket;
        this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.user = EmailBoxController.getCurrentUser();
        this.model = ServerController.getCurrentModel();
    }
    @Override
    public void run() {
        try {
            Object obj = objectInputStream.readObject();
            if(obj instanceof Message) {
                Message message = (Message) obj;
                String action = message.getAction();
                if (action.equals("Login")) {
                    if(ServerController.isRunning()) {
                        String address = (String) message.getContent();
                        boolean result = authenticate(address);
                        if (result) {
                            objectOutputStream.writeObject(new Message(address, "Login successful", true));
                            Platform.runLater(
                                () -> model.addToListLog("User " + address + " just logged in")
                            );
                        }
                        else
                            objectOutputStream.writeObject(new Message(address,"Login unsuccessful", false));
                    } else {
                        System.out.println("Messaggio di errore LOGIN che poi sarà un alert");
                        objectOutputStream.writeObject(new Message(message.getAddress(), "Login unsuccessful", false));
                    }
                }
                if(action.equals("fillInbox")) {
                    if(ServerController.isRunning()) {
                        String address = (String) message.getContent();
                        Platform.runLater(
                            () -> model.addToListLog("Filling " + address + " 's inbox...")
                        );
                        fillInbox(address);
                    } else {
                        System.out.println("Messaggio di errore FILLINBOX che poi sarà un alert");
                        this.objectOutputStream.writeObject("End of communications");
                    }
                }
                if(action.equals("Mail")) {
                    if(ServerController.isRunning()) {
                        Email emailToSend =  (Email) message.getContent();
                        for(String dest : emailToSend.getReceivers()) {
                            updateUserFile(dest, emailToSend);
                            fillInbox(dest);
                            Platform.runLater(
                                () -> model.addToListLog(dest + " just received an email from " + emailToSend.getSender())
                            );
                        }
                    } else {
                        System.out.println("Messaggio di errore MAIL che poi sarà un alert");
                        this.objectOutputStream.writeObject("Error");
                    }
                }
                if(action.equals("DeleteMail")) {
                    if(ServerController.isRunning()) {
                        Email emailToDelete = (Email) message.getContent();
                        boolean ris = deleteMailFromInbox(emailToDelete, message.getAddress());
                        if(ris)
                            objectOutputStream.writeObject(true);
                        else
                            objectOutputStream.writeObject(false);
                    } else {
                        System.out.println("Messaggio di errore DELETE che poi sarà un alert");
                        objectOutputStream.writeObject(false);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateUserFile(String address, Email content) {
        File inputFile = new File("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/receipts/"+address+".txt");
        File tempFile = new File("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/receipts/temp.txt");

        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new BufferedReader(new FileReader(inputFile)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            JSONObject obj = new JSONObject();
            obj.put("id", content.getId());
            obj.put("sender", content.getSender());
            obj.put("receiver", content.getReceivers());
            obj.put("subject", content.getObject());
            obj.put("text", content.getText());
            array.add(obj);

            writer.write("[\n");
            for(Object o : array) {
                JSONObject json_mail = (JSONObject) o;
                writer.write(json_mail.toJSONString() + "\n,");
            }
            writer.write("]\n");
            writer.close();
            if(!tempFile.renameTo(inputFile))
                throw new IOException("Couldn't rename the file");
        } catch (IOException | ParseException e) {
            System.out.println("Error while reading from " + address + ".txt in order to delete a mail. \n" + e.getMessage());
        }
    }
    private void fillInbox(String address) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new BufferedReader(new FileReader("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/receipts/"+address+".txt")));

            for(Object obj : array) {
                JSONObject mail = (JSONObject) obj;

                int id = ( (Long) mail.get("id") ).intValue();
                String sender = (String) mail.get("sender");
                String subject = (String) mail.get("subject");
                String text = (String) mail.get("text");

                ArrayList<String> receivers = new ArrayList<>();
                JSONArray json_receivers = (JSONArray) mail.get("receiver");
                for(Object o : json_receivers) {
                    String rec = (String) o;
                    receivers.add(rec);
                }

                Email new_email = new Email(id, sender, receivers, subject, text);
                this.objectOutputStream.writeObject(new_email);
                User.incCounterMail();
            }
            this.objectOutputStream.writeObject("End of communications");
        } catch (IOException | ParseException e) {
            System.out.println("Error in filling the inbox while reading " + address + " file: \n" + e.getMessage());
        }
    }
    public boolean deleteMailFromInbox(Email mail, String address) {
        File inputFile = new File("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/receipts/"+ address +".txt");
        File tempFile = new File("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/receipts/temp.txt");

        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new BufferedReader(new FileReader(inputFile)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            writer.write("[\n");
            for(Object o : array) {
                JSONObject json_mail = (JSONObject) o;
                int id = ( (Long) json_mail.get("id") ).intValue();
                if(id != mail.getId()) {
                  writer.write(json_mail.toJSONString() + "\n,");
                }
            }
            writer.write("]\n");
            writer.close();
            boolean check = tempFile.renameTo(inputFile);
            if(check) {
                return true;
            } else {
                throw new IOException("Couldn't rename the file");
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error while reading from " + address + ".txt in order to delete a mail. \n" + e.getMessage());
        }
        return false;
    }
    private boolean authenticate(String mail) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new BufferedReader(new FileReader("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/accounts.txt")));

            for(Object o : array) {
                JSONObject obj = (JSONObject) o;
                String email = (String) obj.get("email");
                if(email.equals(mail)) {
                    return true;
                }
            }

          } catch (IOException | ParseException e) {
              System.out.println("Error while trying to authenticate the user: \n" + e.getMessage());
          }
        return false;
    }
}
