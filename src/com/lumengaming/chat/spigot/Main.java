package com.lumengaming.chat.spigot;

import com.lumengaming.chat.asyncnetwork.Client;
import com.lumengaming.chat.spigot.listeners.ChatListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Taylor
 */
public class Main extends JavaPlugin{

    private Client client;

    /**
     * We're going to register a channel, and send a test piece of info to BungeeCord.
     * The classic ping pong!
     */
    @Override
    public void onEnable(){
        this.client = new Client();
        this.client.addReadListener(this.getName(), (byte[] data)->{
            LumenGamingMessageEvent e = new LumenGamingMessageEvent(data);
            Bukkit.getPluginManager().callEvent(e);
        });
        client.start();
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }
    
    @Override
    public void onDisable(){
        client.removeReadListener(this.getName());
        client.stop();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("test")) {
            
            Bukkit.broadcastMessage("<Sleeping12>");
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            Bukkit.broadcastMessage("</Sleeping12>");
            return true;
        }
        return false;
    }
    
    public Client getClient() {
        return this.client;
    }
}
