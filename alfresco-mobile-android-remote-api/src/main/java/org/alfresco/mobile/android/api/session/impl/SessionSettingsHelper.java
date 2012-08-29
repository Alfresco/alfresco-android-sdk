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
package org.alfresco.mobile.android.api.session.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.session.SessionSettings;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

/**
 * Provides methods and utils to manage session settings.
 * 
 * @author Jean Marie Pascal
 */
public class SessionSettingsHelper
{

    private Map<String, String> sessionParameters = new HashMap<String, String>(5);

    private Map<String, Serializable> settings;

    private ListingContext lc;

    public SessionSettingsHelper(Map<String, Serializable> parameters)
    {
        this.settings = parameters;
    }

    public void init()
    {
        int type = SessionSettings.BINDING_TYPE_ALFRESCO_CMIS;
        if (settings.get(SessionSettings.BINDING_TYPE) != null)
        {
            type = (Integer) settings.get(SessionSettings.BINDING_TYPE);
        }

        switch (type)
        {
            case SessionSettings.BINDING_TYPE_CMIS:
                createCmisSettings();
                break;
            case SessionSettings.BINDING_TYPE_ALFRESCO_CMIS:
                createAlfrescoCmisSettings();
                break;
            default:
                createAlfrescoCmisSettings();
                break;
        }

        lc = createListingContext();
    }

    public static Map<String, Serializable> createDefaultSettings(String username, String password)
    {
        Map<String, Serializable> settings = new HashMap<String, Serializable>(2);
        settings.put(SessionSettings.USER, username);
        settings.put(SessionSettings.PASSWORD, password);
        return settings;
    }

    private void createCmisSettings()
    {
        // Credentials
        sessionParameters.put(SessionParameter.USER, (String) settings.get(SessionSettings.USER));
        sessionParameters.put(SessionParameter.PASSWORD, (String) settings.get(SessionSettings.PASSWORD));
        sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        sessionParameters.put(SessionParameter.CLIENT_COMPRESSION, "true");

        // connection settings
        addParameterIfExist(SessionSettings.BINDING_URL, SessionParameter.ATOMPUB_URL);
        addParameterIfExist(SessionSettings.BASE_URL, SessionSettings.BASE_URL);
        addParameterIfExist(SessionSettings.REPOSITORY_ID, SessionParameter.REPOSITORY_ID);
        addParameterIfExist(SessionSettings.CONNECT_TIMEOUT, SessionParameter.CONNECT_TIMEOUT);
        addParameterIfExist(SessionSettings.READ_TIMEOUT, SessionParameter.READ_TIMEOUT);
        addParameterIfExist(SessionSettings.PROXY_USER, SessionParameter.PROXY_USER);
        addParameterIfExist(SessionSettings.PROXY_PASSWORD, SessionParameter.PROXY_PASSWORD);
    }

    private void addParameterIfExist(String keySettings, String keyParameters)
    {
        if (settings.containsKey(keySettings))
            sessionParameters.put(keyParameters, (String) settings.get(keySettings));
    }

    private void createAlfrescoCmisSettings()
    {
        createCmisSettings();

        // Binding with Alfresco Webscript CMIS implementation
        if (settings.containsKey(SessionSettings.BASE_URL)
                && !sessionParameters.containsKey(SessionParameter.ATOMPUB_URL))
            sessionParameters.put(SessionParameter.ATOMPUB_URL,
                    ((String) settings.get(SessionSettings.BASE_URL)).concat(OnPremiseUrlRegistry.BINDING_CMIS));

        // Object Factory
        sessionParameters.put(SessionParameter.OBJECT_FACTORY_CLASS,
                "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
    }

    private ListingContext createListingContext()
    {
        lc = new ListingContext();

        if (settings.get(SessionSettings.LISTING_MAX_ITEMS) != null)
        {
            lc.setMaxItems((Integer) settings.get(SessionSettings.LISTING_MAX_ITEMS));
        }

        if (settings.get(SessionSettings.LISTING_SORTING) != null)
        {
            lc.setSortProperty((String) settings.get(SessionSettings.LISTING_SORTING));
            lc.setIsSortAscending(true);
        }

        return lc;
    }

    public Serializable getValue(String key)
    {
        return settings.get(key);
    }

    public Map<String, Serializable> getSettings()
    {
        return settings;
    }

    public void removeParameter(String key)
    {
        settings.remove(key);
    }

    public List<String> getParameterKeys()
    {
        return new ArrayList<String>(settings.keySet());
    }

    public Map<String, String> cleanPassword()
    {
        Map<String, String> sparam = new HashMap<String, String>(sessionParameters.size());
        settings.remove(SessionSettings.PASSWORD);
        sessionParameters = null;
        return sparam;
    }

    public Map<String, String> getSessionParameters()
    {
        init();
        return sessionParameters;
    }

    public void bindCmisAtom(Map<String, String> param)
    {
        param.put(SessionParameter.ATOMPUB_URL,
                ((String) settings.get(SessionSettings.BASE_URL)).concat(OnPremiseUrlRegistry.BINDING_CMISATOM));
    }

    public void bindPublicAPI()
    {
        if (!sessionParameters.containsKey(SessionSettings.BINDING_URL))
            sessionParameters.put(SessionParameter.ATOMPUB_URL,
                    ((String) settings.get(SessionSettings.BASE_URL)).concat(CloudUrlRegistry.BINDING_CMISATOM));
    }

    // TODO delete !
    public void bindPublicAPI(Map<String, String> param)
    {
        if (!param.containsKey(SessionParameter.ATOMPUB_URL))
            param.put(SessionParameter.ATOMPUB_URL,
                    ((String) settings.get(SessionSettings.BASE_URL)).concat(CloudUrlRegistry.BINDING_CMISATOM));
    }

    public ListingContext getDefaultListingContext()
    {
        return lc;
    }

    public void addParameter(String key, Serializable value)
    {
        settings.put(key, value);
    }
}
