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
package org.alfresco.mobile.android.api.services.impl;

import java.io.File;
import java.util.Map;

import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.impl.DocumentImpl;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.SessionSettings;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.impl.SessionImpl;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * Abstract base class for all public Alfresco SDK Services. Contains all
 * utility methods that are common for building a service. </br> Developers can
 * extend this class if they want to add new services inside the SDK.</br> NB :
 * Don't forget to add the newly service to a custom {@link ServiceRegistry}
 * 
 * @author Jean Marie Pascal
 */
public abstract class AlfrescoService
{
    /** Repository Session. */
    protected AlfrescoSession session;

    /**
     * Default empty Constructor.
     */
    public AlfrescoService()
    {
    }

    /**
     * Default constructor for service. </br> Used by the
     * {@link ServiceRegistry}.
     * 
     * @param repositorySession : Repository Session.
     * @param cmisSession : CMIS session.
     */
    public AlfrescoService(AlfrescoSession repositorySession)
    {
        this.session = repositorySession;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // HTTP using CMIS httpUtils
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Performs a GET on an URL, checks the response code and returns the
     * result.
     * 
     * @param url : requested URL.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    protected HttpUtils.Response read(UrlBuilder url)
    {
        Log.d("URL", url.toString());
        HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_OK) convertStatusCode(resp);

        return resp;
    }

    /**
     * Performs a POST on an URL, checks the response code and returns the
     * result.
     * 
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    protected HttpUtils.Response post(UrlBuilder url, String contentType, HttpUtils.Output writer)
    {
        // make the call
        HttpUtils.Response resp = HttpUtils.invokePOST(url, contentType, writer, getSessionHttp());

        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_OK && resp.getResponseCode() != 201) convertStatusCode(resp);

        return resp;
    }

    /**
     * Performs a DELETE on an URL, checks the response code and returns the
     * result.
     * 
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    protected void delete(UrlBuilder url) throws AlfrescoServiceException
    {
        // make the call
        HttpUtils.Response resp = HttpUtils.invokeDELETE(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_NO_CONTENT && resp.getResponseCode() != HttpStatus.SC_OK)
            convertStatusCode(resp);
    }

    /**
     * Performs a PUT on an URL, checks the response code and returns the
     * result.
     * 
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    protected HttpUtils.Response put(UrlBuilder url, String contentType, Map<String, String> headers,
            HttpUtils.Output writer) throws AlfrescoServiceException
    {
        HttpUtils.Response resp = HttpUtils.invokePUT(url, contentType, headers, writer, getSessionHttp());

        // check response code
        if ((resp.getResponseCode() < HttpStatus.SC_OK) || (resp.getResponseCode() > 299)) convertStatusCode(resp);

        return resp;
    }

    /**
     * @return Binding session for passing the authenticationProvider to execute
     *         the http request.
     */
    protected BindingSession getSessionHttp()
    {
        BindingSession s = new SessionImpl();
        s.put(CmisBindingsHelper.AUTHENTICATION_PROVIDER_OBJECT, session.getCmisSession().getBinding()
                .getAuthenticationProvider());
        return s;
    }

    public static BindingSession getBindingSessionHttp(AlfrescoSession session)
    {
        BindingSession s = new SessionImpl();
        s.put(CmisBindingsHelper.AUTHENTICATION_PROVIDER_OBJECT, session.getCmisSession().getBinding()
                .getAuthenticationProvider());
        return s;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // UTILS
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Wrap and transform cmisobject into NodeObject
     * 
     * @param object : Underlying OpenCMIS Object
     * @return Alfresco Node Object.
     */
    protected Node convertNode(CmisObject object)
    {
        if (object == null) { throw new IllegalArgumentException(Messagesl18n.getString("AlfrescoService.1")); }

        /* determine type */
        switch (object.getBaseTypeId())
        {
            case CMIS_DOCUMENT:
                return new DocumentImpl(object);
            case CMIS_FOLDER:
                return new FolderImpl(object);
            default:
                throw new AlfrescoServiceException(Messagesl18n.getString("AlfrescoService.2") + object.getBaseTypeId());
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // EXCEPTION
    // /////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Catch all underlying CMIS or not exception and throw them as
     * {@link AlfrescoServiceException}
     * 
     * @param t : exceptions catched
     * @throws AlfrescoServiceException : Reasons why the requested response
     *             code is not valid.
     */
    protected static void convertException(Throwable t)
    {
        try
        {
            throw t;
        }
        catch (AlfrescoException e)
        {
            throw e;
        }
        catch (CmisBaseException cmisException)
        {
            throw new AlfrescoServiceException(cmisException.getMessage(), cmisException.getErrorContent());
        }
        catch (Throwable e)
        {
            throw new AlfrescoServiceException(e.getMessage(), e);
        }
    }

    /**
     * Throws exception if http response doesn't match the expected response
     * code.
     * 
     * @param resp : http response.
     * @throws AlfrescoServiceException : Reasons why the requested response
     *             code is not valid.
     */
    public static void convertStatusCode(HttpUtils.Response resp) throws AlfrescoServiceException
    {
        Map<String, Object> json = JsonUtils.parseObject(resp.getErrorContent());
        throw new AlfrescoServiceException((String) json.get("message"), resp.getErrorContent());
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // EXCEPTION
    // /////////////////////////////////////////////////////////////////////////////////////////
    protected ContentFile saveContentStream(ContentStream contentStream, String cacheFileName) throws AlfrescoServiceException
    {
        if (contentStream == null || contentStream.getInputStream() == null)
            return null;
        
        try
        {
            File f = new File((String) session.getParameter(SessionSettings.CACHE_FOLDER), cacheFileName);
            IOUtils.ensureOrCreatePathAndFile(f);
            IOUtils.copyFile(contentStream.getInputStream(), contentStream.getLength(), f);
            return new ContentFile(f, contentStream.getFileName(), contentStream.getMimeType());
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;

    }
}
