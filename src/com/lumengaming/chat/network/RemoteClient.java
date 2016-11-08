package com.lumengaming.chat.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;

/** 
 * For tracking clients a server owns.
 * @author Taylor
 */
public class RemoteClient extends Thread {

    public static final String SOCKET_INPUTSTREAM_ENDED = "end of input stream reached";

    public String username; // basically just "server" name.

    private PushbackInputStream inputStream = null;
    private Socket socket = null;
    private boolean isRunning;
    private OnDisconnectListener onDisconnectListener = null;
    private MessageCallback onMessageCallback = null;


    public RemoteClient(Socket socket) {
        super();
        setSocket(socket);
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
		return new SuperScanner(this.inputStream);
	}
    public InputStream getInputStream() {
        return inputStream;
    }

	@Override
	public void start(){
		super.start();
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
                        //System.err.println("Pre-ScanLoop");
                        SuperScanner scanner = this.getSuperScanner();
						while(scanner.hasNextLine()){
                            //System.err.println("__ScanLoop");
							String line = scanner.nextLine();
                            System.err.println("line="+line);
							//onMessageCallback.onMessage(line);
						}
                        //System.err.println("Post-ScanLoop");
					}
                }
            }
        } catch (IOException ex) {
            System.err.println("Disconnected: "+this.username);
//            ex.printStackTrace();
            fireDisconnected(ex);
        } catch(NoSuchElementException ex){
            System.err.println("Could not get any data from RemoteClient. Possible closed socket on server or client.");
        }
    }

    void fireDisconnected(IOException e) {
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

	public void setOnMessageCallback(MessageCallback callback){
		this.onMessageCallback = callback;
	}

    public static interface OnDisconnectListener {
        void onDisconnect(RemoteClient client, IOException e);
    }

    public void setOnDisconnectListener(OnDisconnectListener onDisconnectedListener) {
        this.onDisconnectListener = onDisconnectedListener;
    }

    public static interface OnReceiveListener {
        void onReceive(RemoteClient client) throws IOException;
    }
//
//    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
//        this.onReceiveListener = onReceiveListener;
//    }

    private void send(byte[] bytes, int offset, int count) throws IOException {
        socket.getOutputStream().write(bytes, offset, count);
		socket.getOutputStream().flush();
    }

    public void send(String message) throws IOException {
        byte[] bytes = (message.replace("\n", "")+"\n").getBytes();
        send(bytes, 0, bytes.length);
    }
    
}
