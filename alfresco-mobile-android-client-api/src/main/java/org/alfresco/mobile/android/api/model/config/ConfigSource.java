package org.alfresco.mobile.android.api.model.config;

import java.io.File;

import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;

import android.text.TextUtils;

public class ConfigSource
{
    private String applicationId;

    private String profileId;

    private File sourceFile;

    private String sourceAsString;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Constructs a ConfigSource object from the given profile stored in the
     * repository for the given application.
     * 
     * @param applicationId : match generally your application packageName
     * @param profile can be null. It will use the default profile.
     */
    public ConfigSource(String applicationId, String profile)
    {
        if (TextUtils.isEmpty(applicationId)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "applicationId")); }

        this.applicationId = applicationId;
        this.profileId = profile;
    }

    /**
     * Constructs a ConfigSource object for configuration that resides in a
     * local file. Use only for custom configuration file inside the device
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
        this.sourceAsString = config;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public File getSourceFile()
    {
        return sourceFile;
    }

    public String getApplicationId()
    {
        return applicationId;
    }

    public String getProfileId()
    {
        return profileId;
    }

    public String getSourceAsString()
    {
        return sourceAsString;
    }
}
