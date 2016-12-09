package com.lumengaming.chat.asyncnetwork;

import com.lumengaming.chat.asyncnetwork.utility.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.WritePendingException;
import java.nio.charset.Charset;
import java.util.TreeMap;
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
    private ByteBuffer sendBuffer;
    private final int BUFFER_SIZE = 2048;
    protected TreeMap<String,AsyncCallback<byte[]>> readListeners;
    private final String clientId;
    
    
    public Client() {
        this(System.currentTimeMillis()+"");
    }
    
    public Client(String clientId){
        this("127.0.0.3", 8991,clientId);
    }
    
    public Client(String remoteHost, int remotePort, String clientIdentifier) {
        this.host = remoteHost;
        this.port = remotePort;
        this.sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.readListeners = new TreeMap<>();
        this.clientId = clientIdentifier;
    }

    public void start(){
        try {
            this.channel = AsynchronousSocketChannel.open();
            SocketAddress serverAddr = new InetSocketAddress(this.host, this.port);
            Future<Void> result = channel.connect(serverAddr);
            Void v = result.get(10L, TimeUnit.SECONDS);
            Log.Client(Level.INFO, "Connected to server");
            ByteBuffer recBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            Log.Client(Level.FINER, "Reading");
            this.send(this.clientId.replace("::", ";;")+"::"+"password");
            this.channel.read(recBuffer,this.channel,new ClientReadHandler(this,recBuffer));
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
    
    public static byte[] getBufferContents(ByteBuffer buffer){
        byte[] oArr = buffer.array();
        byte[] nArr = new byte[buffer.position()-buffer.arrayOffset()];
        
        for(int i = buffer.arrayOffset(), n = 0; i < buffer.position();i++,n++){
            nArr[n] = oArr[i];
        }
        return nArr;
    }
    
    public boolean stop(){
        try {
            try{ this.channel.shutdownInput();}catch(NotYetConnectedException ex){}
            try{ this.channel.shutdownOutput();}catch(NotYetConnectedException ex){}
            this.readListeners.clear();
            this.channel.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    //</editor-fold>
    
    public void send(String message){
        if (this.channel.isOpen()){
            Log.Client(Level.FINE, "Sending:"+message);
            Charset cs = Charset.forName("UTF-8");
            final byte[] data = (message).getBytes(cs); // null will be the terminator char.
            Log.Client(Level.FINEST, "putting...");
            sendBuffer.clear();     // Reset array positions to start.
            sendBuffer.put(data);   // Add the data
            Log.Client(Level.FINEST, "flipping...");
            sendBuffer.flip();      // Prepare to READ from the start.
            ClientWriteHandler onWrite = new ClientWriteHandler(this);
            Log.Client(Level.FINEST, "writing to channel...");
            try{
                channel.write(sendBuffer, null, onWrite);
                Log.Client(Level.FINEST, "finished sending");
            }catch(WritePendingException ex){
                // We don't care. It isn't too important.
                Log.Client(Level.WARNING, "WritePendingException");
            }
        }else{
            Log.Client(Level.WARNING, "Channel is closed.");
        }
            
    }

    public void addReadListener(String key, AsyncCallback<byte[]> callback){
        this.readListeners.put(key,callback);
    }
    
    public void clearReadListeners(){
        this.readListeners.clear();
    }
    
    public void removeReadListener(String key){
        this.readListeners.remove(key);
    }
    
    public AsynchronousSocketChannel getChannel(){
        return this.channel;
    }
    
    private class ClientReadHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        private final Client client;
        private ByteBuffer buffer;
        
        public ClientReadHandler(Client c, ByteBuffer b){
            this.client = c;
            this.buffer = b;
        }
        
        @Override
        public void completed(Integer result,  AsynchronousSocketChannel incomingChannel) {
            Log.Client(Level.FINE, "Read completed results...?");
            if (result == -1) {
                this.client.stop();
                return;
            }
            Log.Client(Level.FINE, "Read completed results...?");
            if (!incomingChannel.isOpen()){ return;}
            Log.Client(Level.FINEST, "Read completed, channel is open.");
            for(String key : this.client.readListeners.keySet()){
                if (this.client.readListeners.containsKey(key)){
                    AsyncCallback<byte[]> callback = this.client.readListeners.get(key);
                    byte[] data = Client.getBufferContents(buffer);
                    callback.doCallback(data);
                }
            }
            String msg = new String(Client.getBufferContents(buffer));
            Log.Client(Level.FINEST, "Read completed, msg="+msg);
            buffer.clear();
            Log.Client(Level.INFO, "GOT=> "+msg);
            buffer = null;
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            incomingChannel.read(this.buffer, incomingChannel, this);
            Log.Client(Level.FINEST, "Post READ, next READ");
        }
        
        @Override
        public void failed(Throwable t,  AsynchronousSocketChannel client) {
            t.printStackTrace();
        }
    }
    
    private class ClientWriteHandler implements CompletionHandler<Integer, Void> {

        private final Client client;

        public ClientWriteHandler(Client client) {
            this.client = client;
        }

        @Override
        public void completed(Integer result, Void attach) {
            Log.Client(Level.FINER, "Completed WRITE, result=" + result);
        }

        @Override
        public void failed(Throwable e, Void attach) {
            e.printStackTrace();
        }
    }
}
