package com.lumengaming.chat.asyncnetworkold.client;

import com.lumengaming.chat.asyncnetworkold.utility.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Taylor
 */
public class Client {
    
    private final int port;
    private final String host;
    private AsynchronousSocketChannel channel;
    private ByteBuffer buffer;
    
    public Client() {
        this("localhost", 8989);
    }

    public Client(String remoteHost, int remotePort) {
        this.host = remoteHost;
        this.port = remotePort;
        this.buffer = ByteBuffer.allocate(1024);
    }

    public void start(){
        try {
            this.channel = AsynchronousSocketChannel.open();
            SocketAddress serverAddr = new InetSocketAddress(this.host, this.port);
            Future<Void> result = channel.connect(serverAddr);
            result.get(5L, TimeUnit.SECONDS);
            Log.Client(Level.INFO, "Connected to "+serverAddr.toString());
//            this.send("Hello World");
//            this.send("Hello Solar System!");
//            this.send("Hello Universe!");
//            this.send("Hello Multiverse!");
//            ClientAttachment attach = new ClientAttachment();
//            attach.channel = channel;
//            attach.buffer = ByteBuffer.allocate(2048);
//            attach.isRead = false;
//            //attach.mainThread = Thread.currentThread();
//            
//            Charset cs = Charset.forName("UTF-8");
//            String msg = "Hello";
//            byte[] data = msg.getBytes(cs);
//            attach.buffer.put(data);
//            attach.buffer.flip();
//            ClientReadWriteHandler readWriteHandler = new ClientReadWriteHandler();
//            channel.write(attach.buffer, attach, readWriteHandler);
//            //attach.mainThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE,"Failed to connect to server. Timeout exceeded.");
        }
    }
    
    public boolean stop(){
        try {
            this.channel.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void send(String message){
        if (this.channel.isOpen()){
            Log.Client(Level.INFO, "Sending:"+message);
            Charset cs = Charset.forName("UTF-8");
            byte[] data = (message).getBytes(cs);
            //ByteBuffer buffer = ByteBuffer.allocate(1024);
            Log.Client(Level.FINER, "putting...");
            buffer.put(data);
            Log.Client(Level.FINER, "flipping...");
            buffer.flip();
            ClientReadWriteHandler readWriteHandler = new ClientReadWriteHandler(this,buffer);
            Log.Client(Level.FINER, "writing to channel...");
            channel.write(buffer, null, readWriteHandler);
            Log.Client(Level.FINER, "clearing channel...");
            buffer.clear();
            Log.Client(Level.FINER, "finished sending");
        }else{
            Log.Client(Level.WARNING, "Channel is closed.");
        }
    }
    public AsynchronousSocketChannel getChannel(){
        return this.channel;
    }
	
    private class OnMessageReadHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        private final Client client;
        private final ByteBuffer buffer;
        
        public OnMessageReadHandler(Client c, ByteBuffer b){
            this.client = c;
            this.buffer = b;
        }
        
        @Override
        public void completed(Integer result,  AsynchronousSocketChannel incomingChannel) {
            if (result == -1) {
                this.client.stop();
                return;
            }
            if (!incomingChannel.isOpen()){ return;}
            String msg = new String(buffer.array());
            buffer.flip();
            buffer.clear();
            Log.Client(Level.INFO, "GOT=> "+msg);
//            this.client.send(msg);
            
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
            incomingChannel.read(this.buffer, incomingChannel, this);
        }
        
        @Override
        public void failed(Throwable t,  AsynchronousSocketChannel client) {
            t.printStackTrace();
        }
    }
}
