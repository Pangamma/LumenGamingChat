package com.lumengaming.chat.bungee;

import com.lumengaming.chat.*;
import java.util.HashMap;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
    public static HashMap<String, String> nicks = new HashMap<String, String>();
    public static HashMap<String, Integer> points = new HashMap<String, Integer>();
    // i know. there is a better way :)

    @Override
    public void onEnable(){
        System.out.println("ENABLED THE THINGY WITH THE LUMEN CHAT.");
        nicks.put("TomiCake", "Mushroom");
        nicks.put("Pangamma", "aBetterName");
  
        points.put("TomiCake", 1223);
        points.put("Pangamma", 124);
        // i know. there is a much better way :)
  
        BungeeCord.getInstance().getPluginManager().registerListener(this, new ChannelListener());

        BungeeCord.getInstance().registerChannel("Return");
    }

}