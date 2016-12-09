package com.lumengaming.chat.asyncnetwork;

import com.lumengaming.chat.asyncnetwork.utility.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Taylor
 */
public class Server {

    private final int port;
    private final String host;
    private AsynchronousServerSocketChannel serverChannel;
    private ConcurrentLinkedQueue<RemoteClient> clients;
    ConcurrentLinkedQueue<Byte[]> messagesQueue;
    protected boolean isStopping;
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Server() {
        this("127.0.0.3", 8991);
    }

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.clients = new ConcurrentLinkedQueue<>();
        this.isStopping = false;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Start/Stop">
    public boolean start() {
        try {
            //java.security.Security.setProperty("networkaddress.cache.ttl", "0");
            this.serverChannel = AsynchronousServerSocketChannel.open()
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(this.host, this.port));
            Log.Server(Level.INFO,"Server is listening at " + host + ":" + port);
            serverChannel.accept(null,new OnClientConnectHandler(this));
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean stop() {
        try {
            this.isStopping = true;
            this.serverChannel.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    //</editor-fold>
    
    
    public void sendMessageToAll(String message){
        for(RemoteClient rc : this.clients){
            rc.sendMessage(message);
        }
    }
    
    public static byte[] getBufferContents(ByteBuffer buffer){
        byte[] oArr = buffer.array();
        byte[] nArr = new byte[buffer.position()-buffer.arrayOffset()];
        
        for(int i = buffer.arrayOffset(), n = 0; i < buffer.position();i++,n++){
            nArr[n] = oArr[i];
        }
        return nArr;
    }
    
    protected boolean addClient(AsynchronousSocketChannel client){
        try {
            RemoteClient rc = new RemoteClient(client);
            removeClient(client);   // remove pre-existing client if present. (Finds by remote addr)
            this.clients.add(rc);
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    protected boolean removeClient(AsynchronousSocketChannel client){
//        try {
////            if (client.getRemoteAddress() != null){
////                RemoteClient rc = null;
////                for(RemoteClient rct : this.clients){
////                    if (rct.getKey().equalsIgnoreCase(client.toString())){
////                        rc = rct;
////                    }
////                }
////                Optional<RemoteClient> rco = this.clients.stream()
////                        .filter((RemoteClient rc)-> rc.getKey().equals(client.getRemoteAddress().toString()))
////                        .findFirst();
////                if (rco.isPresent()){
////                    RemoteClient rc = rco.get();
////                    this.clients.remove(rc);
////                    AsynchronousSocketChannel origClient = rc.getChannel();
////                    try{
////                        origClient.shutdownInput();
////                        origClient.shutdownOutput();
////                        origClient.close();
////                    }catch(Exception ex){
////                        //return false;
////                    } 
////                }
////            }
//        } catch (IOException ex) {
//            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return false;
    }
    
    //<editor-fold defaultstate="collapsed" desc="OnClientConnect">
    private class OnClientConnectHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
        private final Server server;
        private OnClientConnectHandler(Server s){
            this.server = s;
        }
        
        @Override
        public void completed(AsynchronousSocketChannel client, Void v) {
            try {
                this.server.serverChannel.accept(null, this);   // get ready to accept more connections
                this.server.addClient(client);
                SocketAddress clientAddr = client.getRemoteAddress();
                Log.Server(Level.INFO, String.format("S:Accepted a connection from %s%n", clientAddr));
                final ByteBuffer bufferForNewClient = ByteBuffer.allocate(2048);
                client.read(bufferForNewClient, client, new OnClientMessageGetHandler(this.server,bufferForNewClient));
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable e, Void v) {
            if (!this.server.isStopping){
                Log.Server(Level.WARNING,"S:Failed to accept a  connection.");
                e.printStackTrace();
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="OnClientMessageGet">
    private class OnClientMessageGetHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        private final Server server;
        private ByteBuffer buffer;
        
        public OnClientMessageGetHandler(Server s,ByteBuffer b){
            this.server = s;
            this.buffer = b;
        }
        
        @Override
        public void completed(Integer result,  AsynchronousSocketChannel client) {
            if (result == -1) {
                server.removeClient(client);
                return;
            }
            if (!client.isOpen()){ return;}
            Log.Server(Level.FINER, "RC is open.");
            String msg = new String(buffer.array(),buffer.arrayOffset(),buffer.position());
            Log.Server(Level.FINE, "RC.bufferLength="+buffer.position()+",msg="+msg.length());
            this.server.sendMessageToAll(msg);
            //buffer.flip();
            buffer.clear();
            this.buffer = ByteBuffer.allocate(2048);
            client.read(this.buffer, client, this);
        }
        
        @Override
        public void failed(Throwable t,  AsynchronousSocketChannel client) {
            t.printStackTrace();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="RemoteClient">
    private class RemoteClient {

        private ByteBuffer sendBuffer;
        private AsynchronousSocketChannel channel;
        private LinkedList<byte[]> messageQueue;
        private String key;
        private int numWriting = 0;

        public RemoteClient(AsynchronousSocketChannel clientChannel) throws IOException {
            this.channel = clientChannel;
            this.key = clientChannel.getRemoteAddress().toString();
            this.sendBuffer = ByteBuffer.allocate(2048);
            this.messageQueue = new LinkedList<>();
        }

        private void write(byte[] data){
            if (data.length > 2048){ Log.Server(Level.SEVERE, "Had to drop a packet to prevent a buffer overflow."); return;}
            try{
                if (numWriting == 0){
                    numWriting++;
                    sendBuffer.clear();        
                    sendBuffer.put(data);
                    Log.Server(Level.FINE, "Writing data. (byte[]) bufPos="+sendBuffer.position()+"/2048, dataLen="+data.length+",limit="+sendBuffer.limit());
                    sendBuffer.flip();
                    Log.Server(Level.INFO, "WRITE=>"+new String(data));
                    channel.write(this.sendBuffer, null, new CompletionHandler<Integer, Void>() {
                        @Override
                        public void completed(Integer result, Void v) {
                            byte[] next = messageQueue.pollFirst();
                            if (next != null){
                                Log.Server(Level.FINE, "next is not null.");
                                write(next);
                            }
                            numWriting--;
                        }

                        @Override
                        public void failed(Throwable t, Void v) {
                            numWriting--;
                            t.printStackTrace();
                        }
                    });
                }else{
                    Log.Server(Level.FINE, "Message added to messageQueue to be sent later.");
                    messageQueue.addLast(data);
                }
            }catch(WritePendingException ex){
                // Another process will handle this message.
                messageQueue.addLast(data);
                numWriting--;
            }
        }

        public void sendMessage(byte[] data) {
            write(data);
        }

        public void sendMessage(String message) {
            Charset cs = Charset.forName("UTF-8");
            byte[] data = (message).getBytes(cs);
            write(data);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof RemoteClient) {
                return false;
            }
            RemoteClient rc = (RemoteClient) o;
            if (rc.key != null) {
                if (this.key.equalsIgnoreCase(rc.key)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.key);
            return hash;
        }

        public String getKey() {
            return this.key;
        }

        public AsynchronousSocketChannel getChannel() {
            return channel;
        }

//        static String getKey(AsynchronousSocketChannel channel) {
//            String key = "";
//            if (!channel.isOpen()) {
//                key = "CLOSED";
//            } else {
//                try {
//                    key = channel.getRemoteAddress().toString();
//                } catch (IOException ex) {
//                    key = "CLOSED";
//                }
//            }
//            return key;
//        }
    }
    //</editor-fold>
}
