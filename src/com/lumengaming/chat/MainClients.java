/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.chat;

import com.lumengaming.chat.network.Client;

/**
 *
 * @author Taylor
 */
public class MainClients {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		try {
            int numClients = 10;
            Client[] clients = new Client[numClients];
            for(int i = 0; i < numClients; i++){
                clients[i] = new Client("localhost",2121,"Client_"+i,"mega.com");
                clients[i].start();
            }
            int n = 0;
            while (true){
                for(int i = 1; i <= numClients; i++){
                    if (n % i*i*i*10 == 0){
                        clients[i-1].send("C"+i+",n="+n+",t="+System.currentTimeMillis()+"',\tElapsed'="+(((double)i*i*i*10)/2)+"s");
                    }
                }
                n++;
				Thread.sleep(500);
            }
//			//for (int i = 0; i < 2; ++i) {
//				final int n = 0;
//				Client client = new Client("localhost", PORT, "CLIENT_A","lumengaming.com");
//				Client client2 = new Client("localhost", PORT, "CLIENT_B","lumengaming.com");
//				// wait for server's "OK"
//				client.setMessageCallback(new MessageCallback() {
//					@Override
//					public void onMessage(String message) throws IOException{
//						//client.send("Client_"+n+": Hello Server. Time is: "+System.currentTimeMillis());
//					}
//				});
//				// could have more complicated handshaking protocol
//				// eg: publishing listeing port for p2p
//				
//				client.start();
//				client2.start();
			//}
			//server.shutdown();
			//client.shutdown(); 
			//client2.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
