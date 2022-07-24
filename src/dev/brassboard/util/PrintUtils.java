package dev.brassboard.util;

import org.bukkit.Bukkit;

public class PrintUtils 
{
    public static void logVerbose(String message)
    {
        Bukkit.getConsoleSender().sendMessage("§8[§9bb§8] §8> §eVERBOSE §8>§7 " + message);
    }
    
    public static void log(String message)
    {
        Bukkit.getConsoleSender().sendMessage("§8[§9bb§8] §8> §aLOGGING §8>§7 " + message);
    }    
}
