package com.lumengaming.chat.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SonHa
 */
public class Client extends Thread {
    public static final String SOCKET_INPUTSTREAM_ENDED = "end of input stream reached";

    public String username; // basically just "server" name.
    private String password;

    private PushbackInputStream inputStream = null;
    private Socket socket = null;
    private boolean isRunning;
    private OnDisconnectListener onDisconnectListener = null;
	private MessageCallback onMessageCallback = null;
	private String hostName;
	private int port;

    public Client(String hostName, int portNumber) {
        super();
		this.username = "User_"+System.currentTimeMillis();
		this.password = "mega.com";
		this.hostName = hostName;
		this.port = portNumber;
    }
	
	public Client(String hostName, int portNumber, String username, String password) {
        super();
		this.username = username;
		this.password = password;
		this.hostName = hostName;
		this.port = portNumber;
    }

    public Socket getSocket() {
        return socket;
    }

    private final void setSocket(Socket socket) {
        this.socket = socket;
        try {
            inputStream = new PushbackInputStream(socket.getInputStream(), 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	public SuperScanner getSuperScanner(){
		return new SuperScanner(inputStream);
	}
    
    public InputStream getInputStream() {
        return inputStream;
    }
	
	@Override
	public void start(){
		try{
			System.out.println("Starting Client:"+username);
			setSocket(new Socket(hostName, port));
			send(username);
			send(password);
			super.start();
		}
		catch (IOException ex){
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Failed to start the client.", ex);
		}
	}

    @Override
    public void run() {
        isRunning = true;
        try {
            int b;
            while (isRunning) {
                if ((b = inputStream.read()) == -1) {
                    throw new SocketException(SOCKET_INPUTSTREAM_ENDED);
                } else {
                    inputStream.unread(b);
					if (onMessageCallback != null){
						SuperScanner scanner = new SuperScanner(getInputStream());
						while(scanner.hasNextLine()){
							String line = scanner.nextLine();
							onMessageCallback.onMessage(line);
						}
					}
                }
            }
        } catch (IOException ex) {
            System.err.println(this.username+" disconnected from server.");
//            ex.printStackTrace();
            fireDisconnected(ex);
        }
    }

    protected void fireDisconnected(IOException e) {
        if (onDisconnectListener != null) {
            onDisconnectListener.onDisconnect(this, e);
        }
    }
	

    protected void disconnect() {
        try {
            isRunning = false;
            fireDisconnected(null);
            inputStream = null;
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() {
        disconnect();
        try {
            super.finalize();
        } catch (Throwable ignored) {
        }
    }

    /**
     * Safely stops running client thread and disconnects.
     * TODO: Maybe consolidate methods to reduce code duplication and mess.
     */
    public void shutdown() {
        try {
            super.finalize();
            this.interrupt();
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
    }

    public static interface OnDisconnectListener {
        void onDisconnect(Client client, IOException e);
    }

    public void setOnDisconnectListener(OnDisconnectListener onDisconnectedListener) {
        this.onDisconnectListener = onDisconnectedListener;
    }

    public static interface OnReceiveListener {
        void onReceive(Client client) throws IOException;
    }

    public void setMessageCallback(MessageCallback onMessageCallback) {
        this.onMessageCallback = onMessageCallback;
    }

    private void send(byte[] bytes, int offset, int count) throws IOException {
        socket.getOutputStream().write(bytes, offset, count);
		socket.getOutputStream().flush();
    }

    public synchronized void send(String message) throws IOException {
        byte[] bytes = (message.replace("\n", "")+"\n").getBytes();
        send(bytes, 0, bytes.length);
    }
}
 
