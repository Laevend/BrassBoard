package dev.brassboard.module;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dev.brassboard.Brassboard;
import dev.brassboard.module.annotations.ModuleData;
import dev.brassboard.module.exceptions.InvalidModuleException;
import dev.brassboard.util.JarUtils;

public abstract class Module 
{
    public abstract void onEnable();
    public abstract void onDisable();

    public static ModuleData getModuleDataFromFile(File file) throws InvalidModuleException
    {
        if (!file.getName().endsWith(".jar"))
            throw new InvalidModuleException("File " + file.getName() + " is not a java archive");

        try {
            SimpleClassLoader scl = new SimpleClassLoader(file);
            Class<? extends Module> clazz = scl.getModuleClass();

            if (!clazz.isAnnotationPresent(ModuleData.class))
            {
                scl.close();
                throw new InvalidModuleException("Module class \"" + clazz.getSimpleName() + "\" does not contain an @ModuleData annotation");
            }

            scl.close();
            return clazz.getAnnotation(ModuleData.class);
        } catch (IOException e) {
            throw new InvalidModuleException("Failed to obtain Module data from file", e);
        }
    } 

    private static class SimpleClassLoader extends URLClassLoader
    {
        private final File file;

        public SimpleClassLoader(File file) throws MalformedURLException
        {
            super(file.getName(), new URL[] { file.toURI().toURL() }, Brassboard.getInstance().getClass().getClassLoader());
            this.file = file;
        }

        @SuppressWarnings("unchecked") // Cast is checked
        Class<? extends Module> getModuleClass()
        {
            JarFile jar = JarUtils.getJarFile(file);

            if (jar == null)
            {
                return null;
            }

            Enumeration<JarEntry> entries = jar.entries();
            JarEntry entry;
            while (entries.hasMoreElements())
            {
                entry = entries.nextElement();

                if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                    continue;
                
                try {
                    Class<?> clazz = Class.forName(entry.getName().replace("/", ".").replace(".class", ""), false, this);

                    if (Module.class.isAssignableFrom(clazz))
                    {
                        jar.close();
                        return (Class<? extends Module>) clazz;
                    }
                } catch (ClassNotFoundException | IOException e) { continue; }
            }

            try {
                jar.close();
            } catch (IOException e) {
                e.printStackTrace();    
            }
            return null;
        }
    }
}
