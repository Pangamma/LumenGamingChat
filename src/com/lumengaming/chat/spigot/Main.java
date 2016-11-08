package com.lumengaming.chat.spigot;

import com.lumengaming.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Taylor
 */
public class Main extends JavaPlugin{
    private PluginChannelListener pcl;
    @Override
    public void onEnable(){
        this.pcl = new PluginChannelListener();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        // allow to send to BungeeCord
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "Return", pcl);
        // gets a Message from Bungee
 
        getCommand("get").setExecutor(this);
    } 
    
    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if(sender instanceof Player){ // p.s. its only possible if its playerbinded not server!
            Player p = (Player) sender;
            if(args.length == 1){
                p.sendMessage(ChatColor.AQUA + "Sending... ");
                String s = (String) this.pcl.get(p, args[0].equalsIgnoreCase("nick"));
                p.sendMessage(ChatColor.BLUE + "Got: " + "\n" + ChatColor.GREEN + s);
            }else{
                p.sendMessage("/get nick");
            }
        }
        return true;
 
    }
}
