/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.chat.asyncnetworkold.server;

import com.lumengaming.chat.asyncnetworkold.AsyncMainMany;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Taylor
 */
public class AsyncServerTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("S:Server Start");
        server.start();
        System.out.println("S:Server Started");
        
        try {
            Thread.sleep(30000L);
            server.stop();
        } catch (InterruptedException ex) {
            Logger.getLogger(AsyncMainMany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
