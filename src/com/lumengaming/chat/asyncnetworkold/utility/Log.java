package com.lumengaming.chat.asyncnetworkold.utility;

import com.google.common.base.Strings;
import java.util.logging.Level;

/**
 *
 * @author Taylor
 */
public class Log {
    private static int enableClient = 1;
    private static int enableServer = 1;
    public static void Client(Level l, String s){
        if (0 == enableClient) return;
        if (l.intValue() > Level.WARNING.intValue()){
            System.err.println("C:"+s+"\t\t("+getLineNumber()+")");
        }else{
            System.out.println("C:"+s+"\t\t("+getLineNumber()+")");
        }
    }
    public static void Server(Level l, String s){
        if (0 == enableServer) return;
        if (l.intValue() > Level.WARNING.intValue()){
            System.err.println("S:"+s+"\t\t("+getLineNumber()+")");
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
    
    private static int tabDepth = 0;
    public static void startTag(String NodeSet, String tag){
        String tabs = Strings.padStart("", tabDepth*4, ' ');
        System.out.println(tabs+"<"+tag+">");
        tabDepth++;
    }
    public static void endTag(String NodeSet, String tag){
        tabDepth--;
        String tabs = Strings.padStart("", tabDepth*4, ' ');
        System.out.println(tabs+"</"+tag+">");
    }
}
