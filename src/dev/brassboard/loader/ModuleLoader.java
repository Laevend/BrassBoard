package dev.brassboard.loader;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ModuleLoader
{
    private static final Map<String, Class<?>> GLOBAL_CLASS_CACHE = new HashMap<>();
    private static final Map<String, ModuleClassLoader> GLOBAL_LOADER_CACHE = new HashMap<>();

    private static final Map<String, Module> MODULE_CACHE = new HashMap<>();

    private final String name;

    public ModuleLoader(String name)
    {
        this.name = name;
    }

    public abstract boolean loadModule(File file);
    public abstract boolean enableModule(Module module);
    public abstract boolean disableModule(Module module);
    public abstract boolean unloadModule(Module module);

    public String getName()
    {
        return this.name.replace('§', '\u0000');
    }

    public Module getModule(String name)
    {
        return MODULE_CACHE.get(name);
    }

    public Class<?> getCachedClass(String name) throws ClassNotFoundException
    {
        Class<?> clazz = GLOBAL_CLASS_CACHE.get(name);
        
        if (clazz != null)
            return clazz;

        // Check global loader cache
        for (String mod : GLOBAL_LOADER_CACHE.keySet())
        {
            ModuleClassLoader loader = getLoader(mod);

            try {
                clazz = loader.findClass(name);
            } catch (ClassNotFoundException e) {}

            if (clazz != null)
                return clazz;
        }

        return null;
    }

    public void setClass(Class<?> clazz)
    {
        GLOBAL_CLASS_CACHE.put(clazz.getName(), clazz);
    }

    public static Map<String, Class<?>> getGlobalClassCache()
    {
        return GLOBAL_CLASS_CACHE;
    }

    public static ModuleClassLoader getLoader(String moduleName)
    {
        return GLOBAL_LOADER_CACHE.get(moduleName);
    }

    public Collection<ModuleClassLoader> getAllLoaders()
    {
        return GLOBAL_LOADER_CACHE.values();
    }

    public static void addLoader(String name, ModuleClassLoader loader)
    {
        GLOBAL_LOADER_CACHE.put(name, loader);
    }
}
