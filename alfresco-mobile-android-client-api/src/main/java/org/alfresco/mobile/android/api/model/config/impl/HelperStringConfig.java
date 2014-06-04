package org.alfresco.mobile.android.api.model.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.alfresco.mobile.android.api.constants.ConfigConstants;

import android.util.Log;

public class HelperStringConfig
{
    private static final String TAG = HelperStringConfig.class.getSimpleName();

    private Properties properties;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    static HelperStringConfig load(File file)
    {
        HelperStringConfig config = null;
        try
        {
            config = load(new FileInputStream(file));
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        return config;
    }

    public static HelperStringConfig load(InputStream inputStream)
    {
        HelperStringConfig config = null;
        try
        {
            Properties properties = new Properties();
            properties.load(inputStream);
            config = new HelperStringConfig(properties);
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        return config;
    }

    private HelperStringConfig(Properties properties)
    {
        this.properties = properties;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILITIES
    // ///////////////////////////////////////////////////////////////////////////
    public static String getLocalizedFileName()
    {
        String filename = ConfigConstants.DATA_DICTIONNARY_MOBILE_LOCALIZATION_FILE;
        if (!Locale.ENGLISH.equals(Locale.getDefault().getLanguage()))
        {
            filename = String.format(ConfigConstants.MOBILE_LOCALIZATION_FILE_PATTERN, Locale.getDefault()
                    .getLanguage());
        }
        return filename;
    }
    
    public static String getDefaultLocalizedFileName()
    {
        return ConfigConstants.DATA_DICTIONNARY_MOBILE_LOCALIZATION_FILE;
    }

    public static String getRepositoryLocalizedFilePath()
    {
        return ConfigConstants.DATA_DICTIONNARY_MOBILE_LOCALIZATION_PATH.concat(getLocalizedFileName());
    }

    public static String getDefaultRepositoryLocalizedFilePath()
    {
        return ConfigConstants.DATA_DICTIONNARY_MOBILE_LOCALIZATION_PATH
                .concat(ConfigConstants.DATA_DICTIONNARY_MOBILE_LOCALIZATION_FILE);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public String getString(String key)
    {
        String value = key;
        if (properties == null) { return key; }
        value = properties.getProperty(key);
        return (value == null) ? key : value;
    }
}
