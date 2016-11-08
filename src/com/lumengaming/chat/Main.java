package com.lumengaming.chat;

import com.lumengaming.chat.network.*;
import java.io.IOException;

/**
 *
 * @author Taylor
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args){
		final int PORT = 2121; 
		final SuperServer server = new SuperServer(PORT);
		server.setOnMessageCallback(new MessageCallback(){
			@Override
			public void onMessage(String message) throws IOException{
				System.err.println(message);
				//server.sendToAll("From a client: "+message);
			}
		});
		server.start();
	}
}
