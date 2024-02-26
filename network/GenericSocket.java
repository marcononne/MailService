package com.example.miaprova.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe astratta dove ci sono le funzioni per la socket
 * connessione, inizializzazione degli stream di input e output
 * send per invio della mail, la read dell'input e la close della connessione del socket
 * Classe astratta cioè non si può istanziare, possiamo avere socket diversi e ogni socket ha questi metodi
 */

public abstract class GenericSocket {

    private String host;
    private int port;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    //costruttore
    public GenericSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //Aprire la connessione
    //Lancia eccezione che viene gestita nel try catch dove sarà chiamata la funzione
    void openConnection() throws IOException {
        System.out.println("Opening Connection .... ");
        this.socket = new Socket(this.host, this.port);
        //socket.setSoTimeout(10000); //se non riceve nulla dopo 10sec smette
        System.out.println(socket);

        /**Preparazione degli stream*/
        System.out.println("Streams preparation ... ");
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(this.socket.getInputStream()); //canali che prendiamo dalla socket
    }

    //Funzione per inviare oggetto email
    void send(String email) throws IOException {
        System.out.println("Sending String");
        this.objectOutputStream.writeObject(email);
        this.objectOutputStream.flush();
    }

    void read() throws IOException, ClassNotFoundException {
        String email = (String) this.objectInputStream.readObject();
        System.out.println("Ho effettuato la read:" + email);
    }

    void close() throws IOException {
        this.objectOutputStream.close();
        this.objectInputStream.close();
        this.socket.close();
    }

}
