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
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.exceptions.impl.ExceptionHelper;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.impl.ContentFileImpl;
import org.alfresco.mobile.android.api.model.impl.DocumentImpl;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.impl.SessionImpl;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Abstract base class for all public Alfresco SDK Services. Contains all
 * utility methods that are common for building a service. </br> Developers can
 * extend this class if they want to add new services inside the SDK.</br> NB :
 * Don't forget to add the newly service to a custom {@link ServiceRegistry}
 * 
 * @author Jean Marie Pascal
 */
public abstract class AlfrescoService implements Parcelable
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
     * @param url : requested URL. @ : if network or internal problems occur
     *            during the process.
     */
    protected HttpUtils.Response read(UrlBuilder url, int errorCode)
    {
        Log.d("URL", url.toString());
        HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_OK)
        {
            convertStatusCode(resp, errorCode);
        }

        return resp;
    }

    /**
     * Performs a POST on an URL, checks the response code and returns the
     * result. @ : if network or internal problems occur during the process.
     */
    protected HttpUtils.Response post(UrlBuilder url, String contentType, HttpUtils.Output writer, int errorCode)
    {
        // make the call
        HttpUtils.Response resp = HttpUtils.invokePOST(url, contentType, writer, getSessionHttp());

        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_OK && resp.getResponseCode() != HttpStatus.SC_CREATED)
        {
            convertStatusCode(resp, errorCode);
        }

        return resp;
    }

    /**
     * Performs a DELETE on an URL, checks the response code and returns the
     * result. @ : if network or internal problems occur during the process.
     */
    protected void delete(UrlBuilder url, int errorCode)
    {
        // make the call
        HttpUtils.Response resp = HttpUtils.invokeDELETE(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_NO_CONTENT && resp.getResponseCode() != HttpStatus.SC_OK)
        {
            convertStatusCode(resp, errorCode);
        }

    }

    /**
     * Performs a PUT on an URL, checks the response code and returns the
     * result. @ : if network or internal problems occur during the process.
     */
    protected HttpUtils.Response put(UrlBuilder url, String contentType, Map<String, String> headers,
            HttpUtils.Output writer, int errorCode)
    {
        HttpUtils.Response resp = HttpUtils.invokePUT(url, contentType, headers, writer, getSessionHttp());

        // check response code
        if ((resp.getResponseCode() < HttpStatus.SC_OK) || (resp.getResponseCode() > 299))
        {
            convertStatusCode(resp, errorCode);
        }

        return resp;
    }

    /**
     * @return Binding session for passing the authenticationProvider to execute
     *         the http request.
     */
    protected BindingSession getSessionHttp()
    {
        BindingSession s = new SessionImpl();
        s.put(CmisBindingsHelper.AUTHENTICATION_PROVIDER_OBJECT,
                ((AbstractAlfrescoSessionImpl) session).getPassthruAuthenticationProvider());
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
        if (isObjectNull(object)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "object")); }

        /* determine type */
        switch (object.getBaseTypeId())
        {
            case CMIS_DOCUMENT:
                return new DocumentImpl(object);
            case CMIS_FOLDER:
                return new FolderImpl(object);
            default:
                throw new AlfrescoServiceException(ErrorCodeRegistry.DOCFOLDER_WRONG_NODE_TYPE,
                        Messagesl18n.getString("AlfrescoService.2") + object.getBaseTypeId());
        }
    }

    /**
     * Utils method to check if an object is null.
     * 
     * @param o : object to check
     * @return true if the object is null.
     */
    protected boolean isObjectNull(Object o)
    {
        return (o == null);
    }

    /**
     * Utils method to check if a string objec is null or empty.
     * 
     * @param s : String to check
     * @return true if the string is null/empty.
     */
    protected boolean isStringNull(String s)
    {
        return (s == null || s.length() == 0 || s.trim().length() == 0);
    }

    /**
     * Utils method to check if a list is null/empty.
     * 
     * @param l : object to check
     * @return true if the list is null or empty.
     */
    @SuppressWarnings("rawtypes")
    protected boolean isListNull(List l)
    {
        return (l == null || l.isEmpty());
    }
    
    /**
     * Utils method to check if a map is null/empty.
     * 
     * @param l : object to check
     * @return true if the list is null or empty.
     */
    @SuppressWarnings("rawtypes")
    protected boolean isMapNull(Map m)
    {
        return (m == null || m.isEmpty());
    }

    protected boolean isOnPremiseSession()
    {
        return (session instanceof RepositorySession);
    }

    protected boolean isCloudSession()
    {
        return (session instanceof CloudSession);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // EXCEPTION
    // /////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Catch all underlying CMIS or not exception and throw them as
     * {@link AlfrescoServiceException}
     * 
     * @param t : exceptions catched
     * @throw AlfrescoServiceException : Reasons why the requested response code
     *        is not valid.
     */
    protected static void convertException(Exception t)
    {
        ExceptionHelper.convertException(t);
    }

    /**
     * Try to convert error response from repository into high level
     * ErrorContent object. This object allow developper to retrieve information
     * on the exception.
     * 
     * @param resp : http response.
     * @param serviceErrorCode : service from which the error occurs.
     */
    public void convertStatusCode(HttpUtils.Response resp, int serviceErrorCode)
    {
        ExceptionHelper.convertStatusCode(session, resp, serviceErrorCode);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // CACHE
    // /////////////////////////////////////////////////////////////////////////////////////////
    protected static final int RENDITION_CACHE = 1;

    protected static final int CONTENT_CACHE = 2;

    /**
     * Allow to save a contentStream inside the devices file system. The content
     * is saved as cache file inside a cache folder. It's possible to determine
     * the subfolders.
     * 
     * @param contentStream : Content stream of any content
     * @param cacheFileName : Name of the cache file
     * @param storageType : Determine in which subfolders the content is stored
     * @return ContentFile associated to the cache file.
     */
    protected ContentFile saveContentStream(ContentStream contentStream, String cacheFileName, int storageType)

    {
        if (contentStream == null || contentStream.getInputStream() == null) { return null; }

        try
        {
            String folderName = (String) session.getParameter(AlfrescoSession.CACHE_FOLDER);
            switch (storageType)
            {
                case RENDITION_CACHE:
                    folderName += "/rendition";
                    break;
                case CONTENT_CACHE:
                    folderName += "/content";
                    break;
                default:
                    break;
            }

            File f = new File(folderName, cacheFileName);
            IOUtils.ensureOrCreatePathAndFile(f);
            IOUtils.copyFile(contentStream.getInputStream(), f);
            return new ContentFileImpl(f, contentStream.getFileName(), contentStream.getMimeType());
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        dest.writeParcelable(session, PARCELABLE_WRITE_RETURN_VALUE);
    }
}
