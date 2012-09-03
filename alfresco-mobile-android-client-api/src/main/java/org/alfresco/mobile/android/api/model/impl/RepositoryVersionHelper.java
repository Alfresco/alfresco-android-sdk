/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.api.model.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;

/**
 * Provides static methods to have more information about
 * 
 * @author Jean Marie Pascal
 */
public final class RepositoryVersionHelper
{

    // ////////////////////////////////////////////////////////////////////////////////////
    // Internal
    // ////////////////////////////////////////////////////////////////////////////////////
    private static final Pattern VERSIONPATTERN = Pattern.compile("\\d*\\d*\\d*.*");

    private RepositoryVersionHelper()
    {

    }

    /**
     * Internal methods to have the specific number version (major, minor,
     * maintenance)
     * 
     * @param level : Major : 0, Minor : 1, Maintenance : 2, Build number : 3
     * @return Version Label of the specific level.
     */
    public static String getVersionString(String productVersion, int level)
    {
        if (!isVersion(productVersion)) { return null; }
        String[] versions = productVersion.split("\\.");
        if (versions.length >= level + 1) { return versions[level]; }
        return null;
    }

    /**
     * Internal methods to have the specific number version (major, minor,
     * maintenance)
     * 
     * @param productVersion : Product version of the repository.
     * @param level : Major : 0, Minor : 1, Maintenance : 2, Build number : 3
     * @return Version int of the specific level.
     */
    public static Integer getVersion(String productVersion, int level)
    {
        String version = getVersionString(productVersion, level);
        if (version != null)
        {
            return Integer.parseInt(version);
        }
        else
        {
            return null;
        }
    }

    /**
     * Determine if version respect the pattern. If not, it's not possible to
     * get the version number.
     */
    private static boolean isVersion(String productVersion)
    {
        Matcher matcher = VERSIONPATTERN.matcher(productVersion);
        return matcher.matches();
    }

    /**
     * @param repoSession : Session associated to the repository.
     * @return true if it's the repository is an Alfresco repository.
     */
    public static boolean isAlfrescoProduct(AlfrescoSession repoSession)
    {
        if (repoSession instanceof CloudSession)
        {
            return true;
        }
        else
        {
            return (((OnPremiseRepositoryInfoImpl) repoSession.getRepositoryInfo()).isAlfrescoProduct());
        }
    }

}
