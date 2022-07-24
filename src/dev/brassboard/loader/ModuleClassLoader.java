package dev.brassboard.loader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import dev.brassboard.Brassboard;
import dev.brassboard.module.Module;

public abstract class ModuleClassLoader extends URLClassLoader
{
    private File file;
    private ModuleLoader loader;

    public ModuleClassLoader(File file, ModuleLoader loader, ClassLoader parent) throws MalformedURLException
    {
        super(file.getName(), new URL[] { file.toURI().toURL() }, parent);
        this.loader = loader;
        this.file = file;
    }

    public ModuleClassLoader(File file, ModuleLoader loader) throws MalformedURLException
    {
        this(file, loader, Brassboard.getInstance().getClass().getClassLoader());
    }

    public abstract void load();
    public abstract Module loadModule();

    @Override
    protected abstract Class<?> findClass(String name) throws ClassNotFoundException;

    protected File getFile()
    {
        return this.file;
    }

    public JarFile getJarFile() throws IOException
    {
        return new JarFile(file);
    }

    public ModuleLoader getLoader()
    {
        return this.loader;
    }

    protected Class<?> superFindClass(String name) throws ClassNotFoundException
    {
        return super.findClass(name);
    }
}
