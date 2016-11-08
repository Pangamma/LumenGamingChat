package com.lumengaming.chat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChannelListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
                String channel = in.readUTF(); // channel we delivered
                if(channel.equals("get")){
                    ServerInfo server = BungeeCord.getInstance().getPlayer(e.getReceiver().toString()).getServer().getInfo();
                    String input = in.readUTF(); // the inputstring
                    if(input.equals("nickname")){
                        sendToBukkit(channel, Main.nicks.get(e.getReceiver().toString()), server);
                    } else {
                        sendToBukkit(channel, Main.points.get(e.getReceiver().toString()).toString(), server);
                    }
              
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
      
        }
    }

    public void sendToBukkit(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("Return", stream.toByteArray());

    }
}