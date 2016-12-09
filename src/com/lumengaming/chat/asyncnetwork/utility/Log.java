package com.lumengaming.chat.asyncnetwork.utility;

import com.lumengaming.chat.asyncnetwork.utility.*;
import com.google.common.base.Strings;
import java.util.logging.Level;

/**
 *
 * @author Taylor
 */
public class Log {
    private static int enableClient = 0;
    private static int enableServer = 1;
    private static Level minLevel = Level.INFO;
    public static void Client(Level l, String s){
        if (l.intValue() < minLevel.intValue()){ return; }
        if (0 == enableClient) return;
        if (l.intValue() > Level.WARNING.intValue()){
            System.err.println("C:"+s+"\t\t("+getLineNumber()+")");
        }else{
            System.out.println("C:"+s+"\t\t("+getLineNumber()+")");
        }
    }    
    public static void Client(Level l, String s, Exception ex){
        if (l.intValue() < minLevel.intValue()){ return; }
        if (0 == enableClient) return;
        if (l.intValue() > Level.WARNING.intValue()){
            System.err.println("C:"+s+"\t\t("+getLineNumber()+")");
            if (ex != null) ex.printStackTrace();
        }else{
            System.out.println("C:"+s+"\t\t("+getLineNumber()+")");
        }
    }
    public static void Server(Level l, String s){
        if (l.intValue() < minLevel.intValue()){ return; }
        if (0 == enableServer) return;
        if (l.intValue() > Level.WARNING.intValue()){
            System.err.println("S:"+s+"\t\t("+getLineNumber()+")");
        }else{
            System.out.println("S:"+s+"\t\t("+getLineNumber()+")");
        }
    }
    public static void Server(Level l, String s, Exception ex){
        if (l.intValue() < minLevel.intValue()){ return; }
        if (0 == enableServer) return;
        if (l.intValue() > Level.WARNING.intValue()){
            System.err.println("S:"+s+"\t\t("+getLineNumber()+")");
            if (ex != null) ex.printStackTrace();
        }else{
            System.out.println("S:"+s+"\t\t("+getLineNumber()+")");
        }
    }
    public static String getLineNumber() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        String s = e.getClassName();
        s = s.split("\\.")[s.split("\\.").length-1] +"::"+e.getLineNumber();;
        return s;
    }
}
