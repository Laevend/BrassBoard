package dev.brassboard.loader;

import java.io.File;

/**
 * Module loader used to assist in loading other module loaders and MCLs from a file. Not intended for use as a real module loader.
 */
public class SimpleModuleLoader extends ModuleLoader
{
    public SimpleModuleLoader() 
    {
        super("Brassboard");
    }

    @Override
    public boolean loadModule(File file) 
    {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean enableModule(Module module) 
    {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean disableModule(Module module) 
    {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean unloadModule(Module module) 
    {
        throw new IllegalArgumentException();
    }
    
}
