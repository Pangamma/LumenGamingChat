package com.lumengaming.chat.spigot.listeners;
import com.lumengaming.chat.spigot.LumenGamingMessageEvent;
import com.lumengaming.chat.spigot.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{
    private final Main plugin;
    
    public ChatListener(Main plugin){
        this.plugin = plugin;
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onSpigotMessage(AsyncPlayerChatEvent e){
        plugin.getClient().send("AsyncChat=>"+e.getMessage());
//        if (this.plugin.getMiniPlugin().getFactory().getChannel("LumenChat").getOutgoingChannel().send(e, null)
//            JsonObject obj = new JsonObject();
//            obj.addProperty("UUID", e.getPlayer().getUniqueId().toString());
//            obj.addProperty("NAME", e.getPlayer().getName());
//            obj.addProperty("MSG",e.getMessage());
//            this.plugin.getConnectAPI().sendToAllServers("LumenChat",obj.getAsString());
//        }else{
//            onProxyMessage();
//        }
//        e.setCancelled(true);
    }
    
    @EventHandler
    public void onProxyMessage(LumenGamingMessageEvent e){
        Bukkit.broadcastMessage("§aGOT:"+new String(e.getData()));
    }
}
