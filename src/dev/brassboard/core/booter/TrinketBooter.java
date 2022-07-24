package dev.brassboard.core.booter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dev.brassboard.Brassboard;
import dev.brassboard.core.loader.GemstoneLoader;
import dev.brassboard.loader.ModuleLoader;
import dev.brassboard.loader.TwoStagedLoader;
import dev.brassboard.module.Booter;
import dev.brassboard.module.Module;
import dev.brassboard.module.annotations.ModuleData;
import dev.brassboard.module.exceptions.InvalidModuleException;
import dev.brassboard.util.JarUtils;
import dev.brassboard.util.PrintUtils;

/**
 * Implementation of a booter, nicknamed "trinket", with support for two-staged loaders
 * @author Yuki_emeralis (Hailey)
 */
public class TrinketBooter implements Booter
{
    @Override
    public void boot() 
    {
        PrintUtils.log("Booting with §aTrinket§7...");

        PrintUtils.log("Force-registering Gemstone loader for debugging...");
        Brassboard.registerLoader(new GemstoneLoader());

        PrintUtils.logVerbose("Ensuring folder structure...");

        File modsFolder = new File("./plugins/Brassboard/modules/");
        
        if (!modsFolder.exists())
            modsFolder.mkdirs();

        Map<String, ModuleLoader> selectedLoaders = new HashMap<>();

        PrintUtils.logVerbose("Performing load...");
        for (File f : modsFolder.listFiles())
        {
            if (!f.getName().endsWith(".jar"))
                continue;

            PrintUtils.logVerbose("Parsing file " + f.getName());

            if (JarUtils.hasModYaml(f))
            {
                // Load using YAML, skipping having to use a segmented MCL system
                PrintUtils.logVerbose("Module file " + f.getName() + " uses a mod.yml file, using that...");
                continue;
            }

            PrintUtils.logVerbose("Module file " + f.getName() + " does not appear to use a mod.yml file, attempting loading via segmented MCL system...");

            // Load using segmented MCL system
            try {
                ModuleData data = Module.getModuleDataFromFile(f);
                ModuleLoader loader = Brassboard.getLoader(data.loader());

                if (loader == null)
                    throw new InvalidModuleException("Module \"" + data.modName() + "\" declared that it uses an unknown module loader \"" + data.loader() + "\"");

                PrintUtils.logVerbose("Module \"" + data.modName() + "\" declares it uses loader \"" + loader.getName() + "\"");
                PrintUtils.logVerbose("Segmented loader successful, transferring over to " + loader.getName());

                if (!loader.loadModule(f)) // Preload
                    throw new InvalidModuleException("Failed to load module from file");                

                selectedLoaders.put(data.modName(), loader);
            } catch (InvalidModuleException e) {
                PrintUtils.log("§cFailed to preload file \"" + f.getName() + "\"!");
                e.printStackTrace();
            }
            continue;
        }
        PrintUtils.logVerbose("Loading complete");

        PrintUtils.logVerbose("Going back for two-stage loaders...");
        selectedLoaders.forEach((name, loader) -> {
            if (loader instanceof TwoStagedLoader)
            {
                PrintUtils.logVerbose("Performing second-stage load for " + name);
                ((TwoStagedLoader) loader).loadSecondStage();
            }
        });

        PrintUtils.logVerbose("Load complete. Begin enabling...");

        selectedLoaders.forEach((name, loader) -> {
            PrintUtils.logVerbose("Enabling " + name + "...");
            loader.enableModule(loader.getModule(name));
        });
    }
}
