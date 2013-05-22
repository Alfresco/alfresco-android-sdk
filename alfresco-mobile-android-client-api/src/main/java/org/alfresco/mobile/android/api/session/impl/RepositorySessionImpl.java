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
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.RepositoryInfo;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.model.impl.onpremise.OnPremiseRepositoryInfoImpl;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseServiceRegistry;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.authentication.impl.PassthruAuthenticationProviderImpl;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * RepositorySession represents a connection to an on-premise repository as a
 * specific user.
 * 
 * @author Jean Marie Pascal
 */
public class RepositorySessionImpl extends RepositorySession
{
    public RepositorySessionImpl()
    {

    }

    /**
     * Creates a new instance of a RepositorySession representing the repository
     * specified in the url parameter. <br>
     * Authenticate and bind with a repository. Initialize all informations and
     * services associated with the repository for a specific user. This method
     * use automatically default session configuration
     * {@link org.alfresco.mobile.android.api.session.SessionSettings
     * SessionSettings}.
     * 
     * @param url : Base URL associated to the repository. For example :
     *            <i>http://hostname:port/alfresco</i>
     * @return a RepositorySession object that is not bind with the repository.
     */
    public RepositorySessionImpl(String url, String username, String password)
    {
        this(url, username, password, null);
    }

    public RepositorySessionImpl(String url, String username, String password, Map<String, Serializable> settings)
    {
        initSettings(url, username, password, settings);
        authenticate();
    }

    /**
     * @see org.alfresco.mobile.android.api.session.RepositorySession#authenticate(String,
     *      String)
     */
    private void authenticate()
    {
        // default factory implementation
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> param = retrieveSessionParameters();

        cmisSession = createSession(sessionFactory, param);

        // Check RepositoryInfo for Alfresco Version
        // If Alfresco is not a V4, bind with CMIS webscript implementation
        boolean isAlfresco = cmisSession.getRepositoryInfo().getProductName()
                .startsWith(OnPremiseConstant.ALFRESCO_VENDOR);
        String version = RepositoryVersionHelper.getVersionString(cmisSession.getRepositoryInfo().getProductVersion(),
                0);

        RepositoryInfo tmpRepositoryInfo = new OnPremiseRepositoryInfoImpl(cmisSession.getRepositoryInfo());

        if (isAlfresco && !hasForceBinding() && version != null && Integer.parseInt(version) >= OnPremiseConstant.ALFRESCO_VERSION_4)
        {
            param.put(SessionParameter.ATOMPUB_URL, baseUrl.concat(OnPremiseUrlRegistry.BINDING_CMISATOM));
            Session cmisSession2 = null;
            try
            {
                cmisSession2 = createSession(sessionFactory, param);
            }
            catch (Exception e)
            {
                cmisSession2 = null;
            }
            cmisSession = (cmisSession2 != null) ? cmisSession2 : cmisSession;
        }

        // Init Services + Object
        rootNode = new FolderImpl(cmisSession.getRootFolder());

        repositoryInfo = new OnPremiseRepositoryInfoImpl(cmisSession.getRepositoryInfo());

        // On cmisatom binding sometimes the edition is not well formated. In
        // this case we use service/cmis binding. MOBSDK-508    
        if (repositoryInfo.getEdition() == OnPremiseConstant.ALFRESCO_EDITION_UNKNOWN
                && tmpRepositoryInfo.getEdition() != OnPremiseConstant.ALFRESCO_EDITION_UNKNOWN)
        {
            repositoryInfo = new OnPremiseRepositoryInfoImpl(cmisSession.getRepositoryInfo(), tmpRepositoryInfo.getEdition());
        }

        // Extension Point to implement and manage services
        create();
    }

    private void create()
    {
        passThruAuthenticator = cmisSession.getBinding().getAuthenticationProvider();
        authenticator = ((PassthruAuthenticationProviderImpl) passThruAuthenticator)
                .getAlfrescoAuthenticationProvider();
        
        // Extension Point to implement and manage services
        if (hasParameter(ONPREMISE_SERVICES_CLASSNAME))
        {
            services = createServiceRegistry((String) getParameter(ONPREMISE_SERVICES_CLASSNAME));
        }
        else
        {
            services = new OnPremiseServiceRegistry(this);
        }
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<RepositorySessionImpl> CREATOR = new Parcelable.Creator<RepositorySessionImpl>()
    {
        public RepositorySessionImpl createFromParcel(Parcel in)
        {
            return new RepositorySessionImpl(in);
        }

        public RepositorySessionImpl[] newArray(int size)
        {
            return new RepositorySessionImpl[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        dest.writeString(baseUrl);
        dest.writeString(userIdentifier);
        dest.writeString(password);
        dest.writeParcelable(rootNode, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeSerializable(repositoryInfo);
        dest.writeSerializable(cmisSession);
        Bundle b = new Bundle();
        b.putSerializable("userParameters", (Serializable) userParameters);
        dest.writeBundle(b);
    }

    @SuppressWarnings("unchecked")
    public RepositorySessionImpl(Parcel o)
    {
        this.baseUrl = o.readString();
        this.userIdentifier = o.readString();
        this.password = o.readString();
        this.rootNode = o.readParcelable(FolderImpl.class.getClassLoader());
        this.repositoryInfo = (RepositoryInfo) o.readSerializable();
        this.cmisSession = (Session) o.readSerializable();
        Bundle b = o.readBundle();
        this.userParameters = (Map<String, Serializable>) b.getSerializable("userParameters");
        create();
    }
}
