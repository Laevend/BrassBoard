package dev.brassboard.core.loader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.event.Listener;

import dev.brassboard.command.Command;
import dev.brassboard.loader.ModuleClassLoader;
import dev.brassboard.loader.ModuleLoader;
import dev.brassboard.module.Module;
import dev.brassboard.module.exceptions.InvalidModuleException;
import dev.brassboard.util.PrintUtils;

/**
 * Implementation of a classloader, nicknamed "carat". Loads in two stages:
 * Load: Locate module class. 
 * Finish: Locate and load all listener and command classes
 * @author Yuki_emeralis (Hailey)
 */
public class CaratClassLoader extends ModuleClassLoader
{
    private Map<String, Class<?>> interiorClasses = new HashMap<>();
    private Class<? extends Module> moduleClass;

    public CaratClassLoader(File file, ModuleLoader loader) throws MalformedURLException
    {
        super(file, loader);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load()
    {
        Map<Class<?>, List<Class<?>>> output;

        try {
            output = this.collect(Module.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } 

        if (output.size() == 0)
            throw new InvalidModuleException("File \"" + this.getFile().getName() + "\" does not contain a module class.");

        this.moduleClass = (Class<? extends Module>) output.get(Module.class).get(0);
    }

    public boolean finishLoad()
    {
        Map<Class<?>, List<Class<?>>> output;

        try {
            output = this.collect(Command.class, Listener.class);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (output.containsKey(Command.class))
        {
            output.get(Command.class).forEach((commandClass) -> {
                // Load commands
            });

            PrintUtils.logVerbose("Loaded " + output.get(Command.class).size() + " commands");
        }

        if (output.containsKey(Listener.class))
        {
            output.get(Listener.class).forEach((listenerClass) -> {
                // Load listeners
            });
            
            PrintUtils.logVerbose("Loaded " + output.get(Listener.class).size() + " listeners");
        }

        return true;
    }

    public Class<? extends Module> getModuleClass()
    {
        return this.moduleClass;
    }

    @Override
    public Module loadModule() 
    {
        return null;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return findClass(name, false);
    }

    private Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException
	{
        PrintUtils.logVerbose("Attempting to locate class " + name);

        Class<?> result = interiorClasses.get(name);

		if (result == null) 
		{
			if (checkGlobal)
			{
				result = this.getLoader().getCachedClass(name);
			}

			if (result == null)
			{
				result = super.superFindClass(name);

				if (result != null)
				{
					this.getLoader().setClass(result);
				}
			}

			interiorClasses.put(name, result);
		}

		return result;
	}

    private Map<Class<?>, List<Class<?>>> collect(Class<?>... types) throws IOException
    {
        if (types.length == 0)
            throw new IllegalArgumentException("At least one type must be specified.");

        Map<Class<?>, List<Class<?>>> data = new HashMap<>();

        // Initialize data
        for (Class<?> t : types)
        {
            List<Class<?>> list = new ArrayList<>();
            data.put(t, list);
        }

        JarFile jar = this.getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        JarEntry entry;

        while (entries.hasMoreElements())
        {
            entry = entries.nextElement();

            // Skip folders and non-class files
            if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                continue;

            Class<?> clazz;
            try {
                clazz = Class.forName(entry.getName().replace("/", ".").replace(".class", ""), false, this);

                for (Class<?> t : types)
                    if (t.isAssignableFrom(clazz))
                        data.get(t).add(clazz);
                    
            } catch (NoClassDefFoundError | ClassNotFoundException e) {
                continue;
            }
        }

        jar.close();

        // Remove empty keys
        data.keySet().removeIf((in) -> {
            return data.get(in).size() == 0;
        });

        return data;
    }
}