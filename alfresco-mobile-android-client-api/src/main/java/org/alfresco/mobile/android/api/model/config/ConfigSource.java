package org.alfresco.mobile.android.api.model.config;

import java.io.File;

public class ConfigSource
{
    private File sourceFile;

    /**
     * Constructs a ConfigSource object from the given profile stored in the
     * repository for the given application.
     */
    public ConfigSource(String applicationId, String profile)
    {

    }

    /**
     * Constructs a ConfigSource object for configuration that resides in a
     * local file.
     */
    public ConfigSource(File file)
    {
        this.sourceFile = file;
    }

    /**
     * Constructs a ConfigSource object for configuration represented by the
     * given string.
     */
    public ConfigSource(String config)
    {

    }

    public File getSourceFile()
    {
        return sourceFile;
    }

}
