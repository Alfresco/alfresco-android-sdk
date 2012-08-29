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

import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;

/**
 * DRAFT Represents a connection to the Alfresco Cloud repository as a specific
 * user.
 * 
 * @author Jean Marie Pascal
 */
public abstract class CloudSession extends AbstractAlfrescoSessionImpl implements AlfrescoSession
{

    /**
     * Registers a new user for an account on the Alfresco in the cloud server.
     * 
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param password
     * @param apiKey
     * @return
     */
    public static CloudSignupRequest signup(String firstName, String lastName, String emailAddress, String password,
            String apiKey)
    {
        return CloudSessionImpl.signup(firstName, lastName, emailAddress, password, apiKey);
    }

    /**
     * Determines whether an account represented by a previous signup request
     * has been verified yet.
     * 
     * @param signupRequest
     * @return
     */
    public static boolean isAccountVerified(CloudSignupRequest signupRequest)
    {
        return false;
    }

    /**
     * Connects the given user to the Alfresco in the cloud server in the
     * context of the users home network.
     * 
     * @param emailAddress
     * @param password
     * @param apiKey
     * @param settings
     * @return
     */
    public static CloudSession connect(String emailAddress, String password, String apiKey,
            Map<String, Serializable> settings)
    {
        return new CloudSessionImpl(emailAddress, password, settings);
    }

    /**
     * Connects the given user to the Alfresco in the cloud server in the
     * context of the given network.
     * 
     * @param emailAddress
     * @param password
     * @param apiKey
     * @param networkId
     * @param settings
     * @return
     */
    public static CloudSession connect(String emailAddress, String password, String apiKey, String networkId,
            Map<String, Serializable> settings)
    {
        if (settings == null) settings = new HashMap<String, Serializable>();
        settings.put(SessionSettings.CLOUD_NETWORK_ID, networkId);
        return new CloudSessionImpl(emailAddress, password, settings);
    }

    /**
     * Returns the list of networks the current user has access to.
     * 
     * @return
     */
    public abstract List<CloudNetwork> getNetworks();

    /**
     * Returns the current network for the session.
     * 
     * @return
     */
    public abstract CloudNetwork getNetwork();

}
