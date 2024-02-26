package com.example.mieproveprogetto.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.mieproveprogetto.ServerRunnable;

public class Server extends Thread {
    public boolean running;
    private ExecutorService executorService;
    ServerSocket serverSocket = null;
    Socket socket = null;

    public Server() {
        running = true;
        start();
        System.out.println("New Server is born");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9090);
            executorService = Executors.newFixedThreadPool(3);
            while(running) {
                socket = serverSocket.accept();
                Runnable r = new ServerRunnable(socket);
                executorService.execute(r);
            }
        } catch (IOException e) {
            System.out.println("Error starting connection: " + e.getMessage());
        } finally {
            executorService.shutdownNow();
        }
    }

}
