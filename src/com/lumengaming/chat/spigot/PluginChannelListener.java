package com.lumengaming.chat.spigot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Taylor
 */
public class PluginChannelListener implements PluginMessageListener {

    public PluginChannelListener() {
    }
    private static HashMap<Player, Object> obj = new HashMap<Player, Object>();

    @Override
    public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
        System.out.println("RECIEVED,C="+channel+"str="+new String(message));
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String subchannel = in.readUTF();
            if (subchannel.equals("get")) {
                String input = in.readUTF();
                obj.put(player, input);

                notifyAll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized Object get(Player p, boolean integer) {  // here you can add parameters (e.g. String table, String column, ...)
        System.out.println("get");
        this.sendToBungeeCord(p, "get", integer ? "points" : "nickname");
        try {
            wait();
        } catch (InterruptedException e) {
        }

        return obj.get(p);
    }

    public void sendToBungeeCord(Player p, String channel, String sub) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(channel);
            out.writeUTF(sub);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(Main.getPlugin(Main.class), "BungeeCord", b.toByteArray());
    }
}
