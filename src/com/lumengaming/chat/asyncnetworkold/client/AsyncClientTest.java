/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.chat.asyncnetworkold.client;

import com.lumengaming.chat.asyncnetworkold.AsyncMainMany;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Taylor
 */
public class AsyncClientTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        Client client = new Client();
        System.out.println("C:Client Start");
        client.start();
        System.out.println("C:Client Started");
        
        try {
            Thread.sleep(30000L);
            client.stop();
        } catch (InterruptedException ex) {
            Logger.getLogger(AsyncMainMany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
