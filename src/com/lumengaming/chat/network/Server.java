package com.lumengaming.chat.network;

import com.lumengaming.chat.network.RemoteClient.OnDisconnectListener;
import com.lumengaming.chat.network.RemoteClient.OnReceiveListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author SonHa
 */
public class Server extends Thread implements MessageCallback, OnDisconnectListener {

    public int port;
    private ServerSocket serverSocket = null;
    private boolean isRunning;
    private final Map<String, RemoteClient> clients;
    private OnDisconnectListener onDisconnectListener = null;
    //private OnReceiveListener onInputListener = null;
    private MessageCallback onMessageCallback = null;

    public Server() {
        clients = new HashMap<>();
    }

    public Server(int port) {
        this();
        this.port = port;
    }

    public void bind(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            if (serverSocket == null) bind(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        isRunning = true;
        while (isRunning) {
            try {
                // Blocks current thread until connection is made.
                final Socket clientSocket = serverSocket.accept();
                final RemoteClient client = new RemoteClient(clientSocket);
                (new Thread() {
                    @Override
                    public void run() {
                        try {
							if (!tryAuthenticateClient(client)){
                                    client.disconnect();
                                    return;
							}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        synchronized (clients) {
                            if (clients.containsKey(client.username)){
                                disconnect(client.username);
                            }
                        	clients.put(client.username, client);
						}
                        client.setOnMessageCallback(Server.this);
//                        client.setOnReceiveListener(Server.this);
                        client.setOnDisconnectListener(Server.this);
                        client.start();
                    }
                }).start();
            } catch(SocketException e){
				// Socket is disconnecting.
				if ("socket closed".equals(e.getMessage())){
					//TODO: Remove disconnected socket.
				}else{
					e.printStackTrace();
				}
			}
			catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        try {
            isRunning = false;
			synchronized(clients){
				Object[] clientSet = clients.keySet().toArray();
				for(Object obj : clientSet){
					if (clients.containsKey((String)obj)){
						RemoteClient client = clients.get((String)obj);
						client.disconnect();
					}
				}
			}
//			for(RemoteClient client : clients.values()){
//				synchronized(client){
//					client.disconnect();
//				}
//			}
            serverSocket.close();
            serverSocket = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	private String getPasswordForClient(String username){
		return "mega.com";
	}
	
	/**
	 * Sets client's ID if able to authenticate.
	 * @param client
	 * @return 
	 */
	private boolean tryAuthenticateClient(RemoteClient client) {
        System.err.println("Authentication Method");
		SuperScanner scanner = new SuperScanner(client.getInputStream());
		// wait for client identifying
		try {
            String username = scanner.nextLine(); // get the server name
            String password = scanner.nextLine();
			if (getPasswordForClient(username).equals(password)){
				client.username = username;
				// broadcast about the new comer
				System.out.println("Connected " + username + ": " + client.getSocket());
				this.sendToAll("Client " + password + ": " + client.getSocket());
				// send "OK" back
				//client.sendLine("OK");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
    @Override
    protected void finalize() {
        shutdown();
        try {
            super.finalize();
        } catch (Throwable ignored) {
        }
    }

    public void disconnect(String id) {
        RemoteClient client = clients.get(id);
        if (client == null) {
            return;
        }
        client.disconnect();
    }

    @Override
    public void onDisconnect(RemoteClient client, IOException e) {
        clients.remove(client.username);
        if (onDisconnectListener != null) {
            onDisconnectListener.onDisconnect(client, e);
        }
    }
//
//    @Override
//    public void onReceive(RemoteClient client) throws IOException {
//        if (onInputListener != null) {
//            onInputListener.onReceive(client);
//        }
//    }

    public int getClientCount() {
        return clients.size();
    }

    public RemoteClient getClient(String id) {
        return clients.get(id);
    }

    public RemoteClient[] getAllClients() {
    	synchronized (clients) {
    		RemoteClient[] clientsArray = new RemoteClient[getClientCount()];
    		return this.clients.values().toArray(clientsArray);
        }
    }

    public void setOnClientDisconnectListener(OnDisconnectListener onDisconnectedListener) {
        this.onDisconnectListener = onDisconnectedListener;
    }
//
//    public void setOnReceiveListener(OnReceiveListener onInputListener) {
//        this.onInputListener = onInputListener;
//    }

    public synchronized void sendToAll(String message) throws IOException {
        for (RemoteClient client : getAllClients()) {
            client.send(message);
        }
    }

    @Override
    public String toString() {
        return serverSocket.toString();
    }

	@Override
	public void onMessage(String message) throws IOException{
        if (onMessageCallback != null) {
            onMessageCallback.onMessage(message);
        }
	}

	public void setOnMessageCallback(MessageCallback onMessageCallback){
		this.onMessageCallback = onMessageCallback;
	}
	
	public static interface OnClientConnectSuccessListener {
        boolean onSuccess(Client client) throws IOException;
    }
	public static interface OnClientConnectFailListener {
        boolean onFail(Client client,String message) throws IOException;
    }
}