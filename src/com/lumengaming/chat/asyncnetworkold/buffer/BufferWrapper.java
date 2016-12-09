package com.lumengaming.chat.asyncnetworkold.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author Taylor
 */
public class BufferWrapper {
    ArrayList<Byte> bytes = new ArrayList<Byte>();
    private final ByteBuffer buffer;
    public BufferWrapper(){
        this.buffer = ByteBuffer.allocate(1024);
    }
    
    public void put(byte[] data){
        System.out.println("Len="+data.length+",Pos="+buffer.position()+",Capacity="+buffer.capacity()+",Offset="+buffer.arrayOffset());
        buffer.put(data);
    }
    
    public byte[] read(){
        byte[] array = buffer.array().clone();
        buffer.clear();
        return array;
    }
}
