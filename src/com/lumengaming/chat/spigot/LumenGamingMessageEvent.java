package com.lumengaming.chat.spigot;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LumenGamingMessageEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final byte[] data;
    
    public LumenGamingMessageEvent(byte[] data){
        this.data = data;
    }
    
    public byte[] getData(){
        return this.data;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }  
    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }
    
}
