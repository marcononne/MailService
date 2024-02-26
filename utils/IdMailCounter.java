package com.example.mieproveprogetto.utils;

import java.io.*;

public class IdMailCounter {

    private static final File f = new File("/Users/marcononne/Desktop/MieProveProgetto/src/main/java/com/example/mieproveprogetto/utils/idMailCounter.txt");
    public static int getCounter() {
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            String curr_id = r.readLine();
            BufferedWriter w = new BufferedWriter(new FileWriter(f));
            int new_id = Integer.parseInt(curr_id);
            w.write(Integer.toString(new_id + 1));
            w.close();
            r.close();
            return new_id;
        } catch (IOException e) {
            System.out.println("Couldn't get the id from idMailCounter");
        }
        return -1;
    }
}
