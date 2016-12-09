/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.chat.asyncnetwork.tests;

import com.lumengaming.chat.asyncnetwork.Client;
import com.lumengaming.chat.asyncnetwork.Server;
import com.lumengaming.chat.asyncnetwork.utility.Log;
import java.util.logging.Level;

/**
 *
 * @author Taylor
 */
public class AsyncMainSingle1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server();
        Log.Server(Level.INFO, "Server Start");
        server.start();
        Log.Server(Level.INFO, "Server Started");
        
        Client client = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();
        client4.addReadListener("TEST", (byte[] data) ->{
            System.err.println(new String(data));
        });
        Log.Client(Level.INFO, "Client Starting");
        client.start();
        client2.start();
        client3.start();
        client4.start();
        Log.Client(Level.INFO, "Client Started");
        client.send("Hi.");
        for(int i = 0; i < 50; i++){
            client.send("a="+i);
            try { Thread.sleep(10L);} catch (InterruptedException ex) {}
//            client2.send("b="+i);
//            client3.send("c="+i);
            try { Thread.sleep(50L);} catch (InterruptedException ex) {}
        }
        client.send("Hello.");
        client.send("BufferOverflow!~~~~~OneTwoThreeFour");
        Log.Client(Level.INFO, "Client sent messages");
        
        try { Thread.sleep(10000L);} catch (InterruptedException ex) {}
        Log.Client(Level.INFO, "Client stopping");
        if (client.stop()){ Log.Client(Level.INFO, "Client stopped"); } else {Log.Client(Level.WARNING, "Client had issues stopping.");} 
        if (client2.stop()){ Log.Client(Level.INFO, "Client stopped"); } else {Log.Client(Level.WARNING, "Client had issues stopping.");} 
        if (server.stop()){ Log.Server(Level.INFO, "Server stopped"); } else {Log.Server(Level.WARNING, "Server had issues stopping.");} 
    }
    
}
