package dev.brassboard.module.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.bukkit.Material;

import dev.brassboard.module.enums.ApiVersion;
import dev.brassboard.module.enums.JavaVersion;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleData
{
	String modName();
	String description();
	String version();
	String[] author();
	Material icon();
	String[] dependencies() default { };
	String[] externalLibs() default { };
	String loader() default "Gemstone";
	JavaVersion jdkVersion() default JavaVersion.CUSTOM;
	ApiVersion apiVersions();
}
