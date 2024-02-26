package com.example.miaprova.network;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ClientSocket extends GenericSocket {

    //fetch chiede al server di aggiornare la inbox quindi che prende le nuove email
    //client fa una fetch tipo ogni 5/10 secondi fa chiamata al server
    //il server ha le email e poi dovrà far aggiornare la inbox
    public ClientSocket(String host, int port) {
        super(host, port);
    }

    public void sendEmail(String email) {
        //creo executor con un siongolo thread passandoli un runnable anonimo
        Executors.newSingleThreadExecutor().execute(() -> {
                    //qui crei la connessione quinid passi la funzione
                    try {
                        openConnection();
                        send(email);
                        read();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                        //System.out.println("Error in send email");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }


}
