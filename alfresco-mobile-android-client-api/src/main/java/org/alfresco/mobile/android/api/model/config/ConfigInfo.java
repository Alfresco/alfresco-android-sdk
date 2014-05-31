package org.alfresco.mobile.android.api.model.config;

public interface ConfigInfo extends Config
{
    // TODO ADD Version Number & Version Schema
    String SCHEMA_VERSION_BETA = "0.0";

    String getSchemaVersion();

    String getServiceVersion();

    long getLastModificationDate();
}
