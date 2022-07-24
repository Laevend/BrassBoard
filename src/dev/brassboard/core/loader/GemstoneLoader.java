package dev.brassboard.core.loader;

import java.io.File;
import java.io.IOException;

import dev.brassboard.loader.ModuleLoader;
import dev.brassboard.loader.TwoStagedLoader;
import dev.brassboard.module.annotations.ModuleData;
import dev.brassboard.module.exceptions.InvalidModuleException;
import dev.brassboard.util.PrintUtils;

/**
 * Implementation of a module loader, nicknamed "gemstone", which uses the two-stage Carat MCL to perform loading
 * @author Yuki_emeralis (Hailey)
 */
public class GemstoneLoader extends ModuleLoader implements TwoStagedLoader
{
    CaratClassLoader ccl;

    public GemstoneLoader() 
    {
        super("Gemstone");
    }

    @Override
    public boolean loadModule(File file) 
    {
        PrintUtils.logVerbose("Using Carat to preload module from file \"" + file.getName() + "\"...");

        try {
            ccl = new CaratClassLoader(file, this);

            ccl.load();
            ccl.close();
        } catch (IOException e) {
            PrintUtils.log("§c- Failed to open .jar file " + file.getName());
            e.printStackTrace();
            return false;
        } catch (InvalidModuleException e) {
            PrintUtils.log("§c- " + e.getMessage());
            return false;
        }

        if (!ccl.getModuleClass().isAnnotationPresent(ModuleData.class))
            return false;

        String modName = ccl.getModuleClass().getAnnotation(ModuleData.class).modName().replace('§', '\u0000');
        ModuleLoader.addLoader(modName, ccl);
        PrintUtils.logVerbose("§a- Preloaded " + modName);

        return true;
    }

    @Override
    public boolean loadSecondStage() 
    {
        return ccl.finishLoad();
    }

    @Override
    public boolean enableModule(Module module) 
    {
        return true;
    }

    @Override
    public boolean disableModule(Module module) 
    {
        return true;
    }

    @Override
    public boolean unloadModule(Module module) 
    {
        return true;
    }

}
