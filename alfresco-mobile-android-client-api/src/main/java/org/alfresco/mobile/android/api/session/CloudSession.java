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
package org.alfresco.mobile.android.api.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;

/**
 * Represents a connection to the Alfresco Cloud repository as a specific user.
 * 
 * @author Jean Marie Pascal
 */
public abstract class CloudSession extends AbstractAlfrescoSessionImpl
{

    // ///////////////////////////////////////////////
    // CLOUD SPECIFIC
    // ///////////////////////////////////////////////
    /**
     * Define a different cloud network. <br/>
     * Value must be a String value that represents a valid network identifier.<br/>
     * Default : the home network of the logged user.<br/>
     * <b>This parameter can't be changed after the session creation</b>.
     */
    public static final String CLOUD_NETWORK_ID = "org.alfresco.mobile.bindings.api.cloud.network.id";

    /**
     * Username identifier for all cloud service API request. <br/> Indeed
     * CloudSession doesn't know username of the logged user. Due to OAuth it's
     * not aware of this information. To replace the username information,
     * CloudSession use -me- instead.
     */
    public static final String USER_ME = "-me-";

    /**
     * Connects to the Alfresco in the Cloud server in the context of the users
     * home network.
     * 
     * @param oauthData
     * @return
     */
    public static CloudSession connect(OAuthData oauthData)
    {
        return new CloudSessionImpl(oauthData, null);
    }

    /**
     * Connects to the Alfresco in the Cloud server in the context of the users
     * home network.
     * 
     * @param oauthData
     * @param parameters
     * @return
     */
    public static CloudSession connect(OAuthData oauthData, Map<String, Serializable> parameters)
    {
        return new CloudSessionImpl(oauthData, parameters);
    }

    /**
     * Connects the given user to the Alfresco in the cloud server in the
     * context of the given network.
     * 
     * @param oauthData
     * @param networkId
     * @return
     */
    public static CloudSession connect(OAuthData oauthData, String networkId)
    {
        return connect(oauthData, networkId, null);
    }

    /**
     * Connects the given user to the Alfresco in the cloud server in the
     * context of the given network.
     * 
     * @param oauthData
     * @param networkId
     * @param parameters
     * @return
     */
    public static CloudSession connect(OAuthData oauthData, String networkId, Map<String, Serializable> parameters)
    {
        Map<String, Serializable> tmpParameters = parameters;
        if (tmpParameters == null)
        {
            tmpParameters = new HashMap<String, Serializable>();
        }
        tmpParameters.put(CLOUD_NETWORK_ID, networkId);
        return new CloudSessionImpl(oauthData, tmpParameters);
    }

    /**
     * Returns the list of networks the current user has access to.
     */
    public abstract List<CloudNetwork> getNetworks();

    /**
     * Returns the current network for the session.
     */
    public abstract CloudNetwork getNetwork();

    /**
     * Returns the OAuthData the session is currently using.
     * 
     * @return
     */
    public abstract OAuthData getOAuthData();

    /**
     * Sets the OAuthData object associated with the session, typically used
     * after an access token has been refreshed.
     * 
     * @param oauthData
     */
    public abstract void setOAuthData(OAuthData oauthData);

}
