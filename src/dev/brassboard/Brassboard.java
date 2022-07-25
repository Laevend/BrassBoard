package dev.brassboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.brassboard.loader.ModuleLoader;
import dev.brassboard.loader.SimpleModuleClassLoader;
import dev.brassboard.loader.SimpleModuleLoader;
import dev.brassboard.module.Booter;
import dev.brassboard.util.PrintUtils;

public class Brassboard extends JavaPlugin
{
    private static String NMS_VERSION; 
    private static JavaPlugin INSTANCE;
    
    private static Map<String, Booter> REGISTERED_BOOTERS = new HashMap<>();
    private static Map<String, ModuleLoader> REGISTERED_LOADERS = new HashMap<>();

    private static Booter BOOTER;

    private static final String BASE_FILEPATH = "plugins/Brassboard/";

    private static FileConfiguration CONFIG;

    @Override
    public void onEnable()
    {
        INSTANCE = this;
        long time = System.currentTimeMillis();

        PrintUtils.logVerbose("Starting Brassboard...");
        PrintUtils.log("API version is " + getNmsVersion());


        // Load configuration
        File base = new File(BASE_FILEPATH);
        File configFile = new File(BASE_FILEPATH, "Brassboard.yml");
        if (!configFile.exists())
            this.saveResource("Brassboard.yml", false);

        CONFIG = YamlConfiguration.loadConfiguration(configFile);

        // Generate file structure
        if (!base.exists())      
            base.mkdirs();
            
        File booterPath = new File(base, "booters");
        File loaderPath = new File(base, "loaders");
        File modulePath = new File(base, "modules");

        if (!booterPath.exists())
            booterPath.mkdirs();
        if (!loaderPath.exists())
            loaderPath.mkdirs();
        if (!modulePath.exists())
            modulePath.mkdirs();

        // Load booters and loaders
        registerLoader(new SimpleModuleLoader());
        loadBooters(booterPath);
        loadModuleLoaders(loaderPath);

        PrintUtils.log("The following " + PrintUtils.plural(REGISTERED_BOOTERS.size(), "booter is", "booters are") + " available:");
        REGISTERED_BOOTERS.values().forEach((booter) -> PrintUtils.log("- " + booter.getClass().getSimpleName()));
        PrintUtils.log("The following " + PrintUtils.plural(REGISTERED_LOADERS.size(), "module loader is", "module loaders are") + " available:");
        REGISTERED_LOADERS.values().forEach((loader) -> PrintUtils.log("- " + loader.getName()));

        // Select booter based on config
        if (CONFIG.contains("booter"))
            BOOTER = REGISTERED_BOOTERS.get(CONFIG.getString("booter"));

        // Perform boot
        if (BOOTER == null)
        {
            PrintUtils.log("§cFailed to find booter \"" + CONFIG.getString("booter") + "\"");
            PrintUtils.log("§cBrassboard failed to load. Please supply a valid booter.");
            return;
        }    
        
        PrintUtils.log("Booting using booter §a" + BOOTER.getClass().getSimpleName() + "§7...");

        BOOTER.boot();

        PrintUtils.log("Brassboard loaded in §a" + (System.currentTimeMillis() - time) + "§7 millis.");
    }

    @Override
    public void onDisable()
    {
        
    }

    //
    // Loading
    //

    private void loadBooters(File booterPath)
    {
        for (File f : booterPath.listFiles())
        {
            try {
                SimpleModuleClassLoader scl = new SimpleModuleClassLoader(f, getLoader("Brassboard"));

                scl.collect(Booter.class).forEach((booter) -> registerBooter((Booter) booter));

                scl.close();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private void loadModuleLoaders(File loaderPath)
    {
        for (File f : loaderPath.listFiles())
        {
            try {
                SimpleModuleClassLoader scl = new SimpleModuleClassLoader(f, getLoader("Brassboard"));

                scl.collect(ModuleLoader.class).forEach((loader) -> registerLoader((ModuleLoader) loader));

                scl.close();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    //
    // Getters
    //

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

    public static ModuleLoader getLoader(String name)
    {
        return REGISTERED_LOADERS.get(name);
    }

    public static void registerLoader(ModuleLoader loader)
    {
        REGISTERED_LOADERS.put(loader.getName(), loader);
    }

    public static void registerBooter(Booter booter)
    {
        REGISTERED_BOOTERS.put(booter.getClass().getSimpleName(), booter);
    }
}