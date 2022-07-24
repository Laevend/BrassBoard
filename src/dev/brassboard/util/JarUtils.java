package dev.brassboard.util;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class JarUtils 
{
    public static JarFile getJarFile(File file)
    {
        try {
            return new JarFile(file);
        } catch (IOException e) {
            return null;
        }
    }   

    public static boolean hasModYaml(File file)
    {
        JarFile jar = getJarFile(file);

        if (jar == null)
            return false;

        return jar.getJarEntry("mod.yml") != null;
    }
}
