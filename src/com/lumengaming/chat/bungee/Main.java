package com.lumengaming.chat.bungee;

import com.lumengaming.chat.*;
import com.lumengaming.chat.asyncnetwork.Server;
import java.util.HashMap;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
    private Server server;

    @Override
    public void onEnable(){
        this.server = new Server();
        this.server.start();
        System.out.println("ENABLED THE THINGY WITH THE LUMEN CHAT.");
        BungeeCord.getInstance().getPluginManager().registerListener(this, new ChannelListener(this));
    }
    
    @Override
    public void onDisable(){
        this.server.stop();
    }

    public Server getServer() {
        return server;
    }

}