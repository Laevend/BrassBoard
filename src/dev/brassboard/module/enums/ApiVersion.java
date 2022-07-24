package dev.brassboard.module.enums;

import dev.brassboard.Brassboard;

public enum ApiVersion 
{
    V1_13_R1(JavaVersion.JAVA_8),
    V1_13_R2(JavaVersion.JAVA_8),
    V1_14_R1(JavaVersion.JAVA_8),
    V1_15_R1(JavaVersion.JAVA_8),
    V1_16_R1(JavaVersion.JAVA_8),
    V1_16_R2(JavaVersion.JAVA_8),
    V1_16_R3(JavaVersion.JAVA_8),
    V1_17_R1(JavaVersion.JAVA_16),
    V1_17_R2(JavaVersion.JAVA_16),
    V1_18_R1(JavaVersion.JAVA_17),
    V1_18_R2(JavaVersion.JAVA_17),
    V1_19_R1(JavaVersion.JAVA_17),
    CUSTOM(JavaVersion.CUSTOM)
    ;

    private final JavaVersion minSupportedVersion;

    private ApiVersion(JavaVersion minSupportedVersion)
    {
        this.minSupportedVersion = minSupportedVersion;
    }

    public static ApiVersion getRunningVersion()
    {
        return valueOf(Brassboard.getNmsVersion().toUpperCase());
    }

    public JavaVersion getMinimumJavaVersion()
    {
        return this.minSupportedVersion;
    }

    public boolean isSupported(JavaVersion version)
    {
        if (version == null)
            return false;

        if (version.equals(JavaVersion.CUSTOM) || this.equals(ApiVersion.CUSTOM))
            return true;

        return version.getSpec() >= this.minSupportedVersion.getSpec();
    }
}
