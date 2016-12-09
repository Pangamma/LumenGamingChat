package com.lumengaming.chat.bungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChannelListener implements Listener {

    private final Main plugin;
    public ChannelListener(Main main){
        this.plugin = main;
    }
    
    @EventHandler
    public void onChat(net.md_5.bungee.api.event.ChatEvent e){
        String message = e.getMessage();
        plugin.getServer().sendMessageToAll(message);
    }
}