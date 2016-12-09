package com.lumengaming.chat.asyncnetworkold.client;

import com.lumengaming.chat.asyncnetworkold.utility.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class ClientReadWriteHandler implements CompletionHandler<Integer, Void> {
    private final Client client;
    private final ByteBuffer buffer;
    public ClientReadWriteHandler(Client client, ByteBuffer buffer){
        this.client = client;
        this.buffer = buffer;
    }
  @Override
  public void completed(Integer result, Void attach) {
      Log.Client(Level.FINE, "Completed, result="+result);      
      String s = new String(buffer.array());
      Log.Client(Level.FINE, "Completed, buffer="+s);

      //System.out.println("C:Completed READWRITE, r="+result);
//    if (attach.isRead) {
//      attach.buffer.flip();
//      Charset cs = Charset.forName("UTF-8");
//      int limits = attach.buffer.limit();
//      byte bytes[] = new byte[limits];
//      attach.buffer.get(bytes, 0, limits);
//      String msg = new String(bytes, cs);
//      System.out.format("C:Server Responded: "+ msg);
//      try {
//        msg = this.getTextFromUser();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      if (msg.equalsIgnoreCase("bye")) {
//        //attach.mainThread.interrupt();
//        return;
//      }
//      attach.buffer.clear();
//      byte[] data = msg.getBytes(cs);
//      attach.buffer.put(data);
//      attach.buffer.flip();
//      attach.isRead = false; // It is a write
//      attach.channel.write(attach.buffer, attach, this);
//    }else {
//      attach.isRead = true;
//      attach.buffer.clear();
//      attach.channel.read(attach.buffer, attach, this);
//    }
  }
  @Override
  public void failed(Throwable e, Void attach) {
    e.printStackTrace();
  }
}