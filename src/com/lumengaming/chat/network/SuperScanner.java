package com.lumengaming.chat.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author Taylor
 */
public class SuperScanner{
    private final InputStream input;
    private final BufferedReader reader;
    private final StringBuilder sb;
    
    public SuperScanner(InputStream in) {
        this.input = in;
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.sb = new StringBuilder();
    }
    
    public boolean hasNextLine() throws IOException{
        return reader.ready();
    }
    
    public String nextLine() throws IOException{
        String line = reader.readLine();
        return line;
    }
}
