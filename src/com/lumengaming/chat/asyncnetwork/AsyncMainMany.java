package com.lumengaming.chat.asyncnetwork;

import com.lumengaming.chat.asyncnetwork.*;
import com.lumengaming.chat.asyncnetwork.utility.Log;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Taylor
 */
public class AsyncMainMany {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread s = new Thread(()->{
            Server server = new Server();
            Log.Server(Level.INFO, "Server Start");
            server.start();
            Log.Server(Level.INFO, "Server Started");
        }); s.start();
        
        Thread c = new Thread(()->{
            ArrayList<Client> clients = new ArrayList<Client>();
            for(int cl = 0; cl < 10; cl++){
                Client client = new Client();
                client.start();
                clients.add(client);
            }
            for(int i = 0; i < 300; i++){
                for(int cl = 0; cl < 10; cl++){
                    Client client = clients.get(cl);
                    client.send("Client("+cl+") I=>"+i);
                }
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException ex) {}
            }
            for(Client client : clients){
                client.stop();
            }
        }); c.start();
        
        try {
            Thread.sleep(500L);
            c.interrupt();
            Thread.sleep(1000L);
            s.interrupt();
        } catch (InterruptedException ex) {
            Logger.getLogger(AsyncMainMany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
