package com.lumengaming.chat.asyncnetworkold.server;

import com.lumengaming.chat.asyncnetworkold.utility.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
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
    
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Server() {
        this("127.0.0.1", 8989);
    }

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.clients = new ConcurrentLinkedQueue<>();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Start/Stop">
    public boolean start() {
        try {
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
                final ByteBuffer bufferForNewClient = ByteBuffer.allocate(1024);
                client.read(bufferForNewClient, client, new OnClientMessageGetHandler(this.server,bufferForNewClient));
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable e, Void v) {
            Log.Server(Level.WARNING,"S:Failed to accept a  connection.");
            e.printStackTrace();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="OnClientMessageGet">
    private class OnClientMessageGetHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        private final Server server;
        private final ByteBuffer buffer;
        
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
            String msg = new String(buffer.array()).trim();
            buffer.clear();
            buffer.flip();
            buffer.clear();
            this.server.sendMessageToAll(msg);
//            // callback 3
//            client.write((ByteBuffer) this.buffer.flip(), this.buffer, new CompletionHandler<Integer, ByteBuffer>() {		
//                @Override
//                public void completed(Integer result, ByteBuffer bbAttachment) {
//                    if (bbAttachment.hasRemaining()) {
//                        Log.Server(Level.FINE,"ECHO:"+new String(bbAttachment.array()));
//                        client.write(bbAttachment, bbAttachment, this);
//                    } else {
//                        Log.Server(Level.FINE,"ECHO2:"+new String(bbAttachment.array()));
//                        bbAttachment.clear();
//                    }
//                }		
//                @Override
//                public void failed(Throwable t, ByteBuffer bbAttachment) {
//                    t.printStackTrace();
//                }										
//            });
            client.read(this.buffer, client, this);
        }
        
        @Override
        public void failed(Throwable t,  AsynchronousSocketChannel client) {
            t.printStackTrace();
        }
    }
    //</editor-fold>
    
}
