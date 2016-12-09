package com.lumengaming.chat.asyncnetworkold.buffer;

import java.nio.ByteBuffer;

/**
 *
 * @author Taylor
 */
public class BufferTests {

    private static ByteBuffer buffer = ByteBuffer.allocate(16);
    public static void main(String[] args) {
        write("Hi.".getBytes());
        write("Hello.".getBytes());
        System.out.println(new String(read()));
        write("Hello World.".getBytes());
        write("Hello Universe.".getBytes());
        System.out.println(new String(read()));
    }
    
    public static void write(byte[] message){
        //System.out.println(buffer.order());
        System.out.println("Len="+message.length+",Pos="+buffer.position()+",Capacity="+buffer.capacity()+",Offset="+buffer.arrayOffset());
        buffer.put(message);
        
    }
    
    public static byte[] read(){
        byte[] array = buffer.array().clone();
        buffer.clear();
        return array;
    }
}
