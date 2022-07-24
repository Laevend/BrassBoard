package dev.brassboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.brassboard.core.booter.TrinketBooter;
import dev.brassboard.loader.ModuleLoader;
import dev.brassboard.module.Booter;
import dev.brassboard.util.PrintUtils;

public class Brassboard extends JavaPlugin
{
    private static String NMS_VERSION; 
    private static JavaPlugin INSTANCE;
    
    private static Map<String, ModuleLoader> REGISTERED_LOADERS = new HashMap<>();
    private static Booter BOOTER;

    @Override
    public void onEnable()
    {
        INSTANCE = this;
        long time = System.currentTimeMillis();

        PrintUtils.logVerbose("Starting Brassboard...");
        PrintUtils.log("API version is " + getNmsVersion());

        // TODO Load module loaders and MCLs from folders

        setBooter(new TrinketBooter());        

        PrintUtils.log("Passing control over to booter §a" + BOOTER.getClass().getSimpleName() + "§7...");

        BOOTER.boot();

        PrintUtils.log("Brassboard loaded in §a" + (System.currentTimeMillis() - time) + "§7 millis.");
    }

    @Override
    public void onDisable()
    {
        
    }

    public static JavaPlugin getInstance()
    {
        return INSTANCE;
    }

    public static String getNmsVersion()
    {
        if (NMS_VERSION != null)
            return NMS_VERSION;

        String bukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
		NMS_VERSION = bukkitPackage.substring(bukkitPackage.lastIndexOf('.') + 1);

        return getNmsVersion();
    }

    public static void setBooter(Booter booter)
    {
        if (booter == null)
            return;

        PrintUtils.log("Switching booters: " + (BOOTER != null ? "§b" + BOOTER.getClass().getSimpleName() : "§cNo booter") + " §7->§a " + booter.getClass().getSimpleName());
        BOOTER = booter;
    }

    public static ModuleLoader getLoader(String name)
    {
        return REGISTERED_LOADERS.get(name);
    }

    public static void registerLoader(ModuleLoader loader)
    {
        REGISTERED_LOADERS.put(loader.getName(), loader);
    }
}