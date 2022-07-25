package dev.brassboard.loader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dev.brassboard.module.Module;
import dev.brassboard.util.JarUtils;

/**
 * Simple MCL used to facilitate loading loaders and other MCLs from a file
 */
public class SimpleModuleClassLoader extends ModuleClassLoader
{
    private final Map<String, Class<?>> interiorClasses = new HashMap<>();

    public SimpleModuleClassLoader(File file, ModuleLoader loader) throws MalformedURLException 
    {
        super(file, loader);
    }

    @Override
    public void load() 
    {
        return;
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

    @SuppressWarnings("unchecked") // "Unchecked" cast is checked via Class#isAssignableFrom inside collectObjects
    public <T> List<T> collect(Class<?> type)
    {
        return (List<T>) collectObjects(type).get(type);
    }

    /**
     * Returns a map containing lists of objects in the given JarFile matching the specified type(s). Assumes all types have an empty constructor.
     * @param types A var-args array of classes to match for.
     * @return A map containing lists of objects in the given JarFile.
     */
    public Map<Class<?>, List<Object>> collectObjects(Class<?>... types)
    {
        Map<Class<?>, List<Object>> data = new HashMap<>();

        for (Class<?> t : types)
            data.put(t, new ArrayList<>());

        JarFile jar = JarUtils.getJarFile(this.getFile());
        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements())
        {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory() || !entry.getName().endsWith(".class"))      
                continue;
                
            try {
                Class<?> clazz = Class.forName(entry.getName().replace('/', '.').replace(".class", ""), false, this);

                for (Class<?> type : types)
                {
                    if (!type.isAssignableFrom(clazz))
                        continue;

                    try {
                        data.get(type).add(clazz.getConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }

        return data;
    }
}
