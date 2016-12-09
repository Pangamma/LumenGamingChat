package com.lumengaming.chat.asyncnetworkold.server;

import com.lumengaming.chat.asyncnetworkold.utility.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

    public class RemoteClient {
        private ByteBuffer buffer;
        private AsynchronousSocketChannel channel;
        private String key;
        
        public RemoteClient(AsynchronousSocketChannel clientChannel) throws IOException{
            this.channel = clientChannel;
            this.key = clientChannel.getRemoteAddress().toString();
            this.buffer = ByteBuffer.allocate(1024);
        }
		
        public void sendMessage(byte[] data){
            this.buffer.put(data);
            //this.buffer.flip();
            channel.write((ByteBuffer) this.buffer.flip(), this.buffer, new CompletionHandler<Integer, ByteBuffer>() {		
                @Override
                public void completed(Integer result, ByteBuffer bbAttachment) {
                    if (bbAttachment.hasRemaining()) {
                        Log.Server(Level.FINE,"ECHO1:"+new String(bbAttachment.array()));
                        channel.write(bbAttachment, bbAttachment, this);
                    } else {
                        Log.Server(Level.FINE,"ECHO2:"+key+new String(bbAttachment.array()));
                        bbAttachment.clear();
                    }
                }		
                @Override
                public void failed(Throwable t, ByteBuffer bbAttachment) {
                    t.printStackTrace();
                }										
            });
        }
        
        public void sendMessage(String message){
            Charset cs = Charset.forName("UTF-8");
            byte[] data = (message).getBytes(cs);
            sendMessage(data);
        }
//        
//        public void sendMessage(byte[] data){
//            this.buffer.put(data);
//            this.buffer.flip();
//            channel.write((ByteBuffer) this.buffer.flip(), this.buffer, new CompletionHandler<Integer, ByteBuffer>() {		
//                @Override
//                public void completed(Integer result, ByteBuffer bbAttachment) {
//                    if (bbAttachment.hasRemaining()) {
//                        channel.write(bbAttachment, bbAttachment, this);
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
//        }
        
        @Override
        public boolean equals(Object o){
            if (o == null) return false;
            if (o instanceof RemoteClient) return false;
            RemoteClient rc = (RemoteClient) o;
            if (rc.key != null){
                if (this.key.equalsIgnoreCase(rc.key)){
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
    
    public static String getKey(AsynchronousSocketChannel channel){
        String key = "";
        if (!channel.isOpen()){
            key = "CLOSED";
        }else{
            try {
                key = channel.getRemoteAddress().toString();
            } catch (IOException ex) {
                key = "CLOSED";
            }
        }
        return key;
    }
}