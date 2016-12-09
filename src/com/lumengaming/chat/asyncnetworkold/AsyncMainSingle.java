/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.chat.asyncnetworkold;

import com.lumengaming.chat.asyncnetworkold.client.Client;
import com.lumengaming.chat.asyncnetworkold.server.Server;
import com.lumengaming.chat.asyncnetworkold.utility.Log;
import com.lumengaming.chat.asyncnetworkold.utility.TagTree;
import java.util.logging.Level;

/**
 *
 * @author Taylor
 */
public class AsyncMainSingle {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TagTree tree = new TagTree("Test");
        String s = tree.in("A").append("1").append("2").append("3").in("B").append("C").out("N").toString();
        System.err.println(s);
        Server server = new Server();
        Log.Server(Level.INFO, "Server Start");
        server.start();
        Log.Server(Level.INFO, "Server Started");
        
        Client client = new Client();
        client.start();
        for(int i = 0; i < 50; i++){
            client.send("Client("+0+") I=>"+i);
            try {
                Thread.sleep(5L);
            } catch (InterruptedException ex) {}
        }

        client.stop();
        
    }
    
}
