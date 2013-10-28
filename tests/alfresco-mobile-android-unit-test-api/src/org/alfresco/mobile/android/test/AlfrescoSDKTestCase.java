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
package org.alfresco.mobile.android.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.SiteVisibility;
import org.alfresco.mobile.android.api.model.impl.ContentFileImpl;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import android.content.Context;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;
import android.util.Log;

public abstract class AlfrescoSDKTestCase extends InstrumentationTestCase implements ServerConfiguration, TestConstant
{
    public static final String TAG = "AlfrescoSDKTestCase";

    // //////////////////////////////////////////////////////////////////////
    // Internal test extension point
    // //////////////////////////////////////////////////////////////////////
    protected static final String BINDING_URL = "org.alfresco.mobile.binding.internal.url";

    protected static final String BASE_URL = "org.alfresco.mobile.binding.internal.baseurl";

    protected static final String USER = "org.alfresco.mobile.internal.credential.user";

    protected static final String PASSWORD = "org.alfresco.mobile.internal.credential.password";

    protected static final String CLOUD_BASIC_AUTH = "org.alfresco.mobile.binding.internal.cloud.basic";

    // //////////////////////////////////////////////////////////////////////
    // Members
    // //////////////////////////////////////////////////////////////////////
    protected AlfrescoSession alfsession;

    protected ServerConfigFile config;

    // //////////////////////////////////////////////////////////////////////
    // CREATE CMIS SESSION
    // //////////////////////////////////////////////////////////////////////
    public RepositorySession createCMISSession()
    {
        config = new ServerConfigFile(CMIS_SERVER_ATOMPUB_URL, CMIS_SERVER_USERNAME, CMIS_SERVER_PASSWORD);
        // Check Properties available inside the device
        if (ENABLE_CONFIG_FILE)
        {
            config.parseFile(CMIS_CONFIG_PATH);
        }

        RepositorySession session = null;
        try
        {
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BINDING_URL, config.getUrl());
            session = RepositorySession.connect(config.getUrl(), config.getUser(), config.getPassword(), settings);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return session;
    }

    // //////////////////////////////////////////////////////////////////////
    // CREATE ON PREMISE SESSION
    // //////////////////////////////////////////////////////////////////////
    /**
     * Default creation of a repositorySession
     * 
     * @return
     */
    public RepositorySession createRepositorySession()
    {
        return createRepositorySession(null);
    }

    /**
     * Creation with a specific username
     * 
     * @param username
     * @param password
     * @param parameters
     * @return
     */
    protected AlfrescoSession createRepositorySession(String username, String password,
            Map<String, Serializable> parameters)
    {
        AlfrescoSession session = null;
        Map<String, Serializable> tmp = parameters;
        try
        {
            if (tmp == null)
            {
                tmp = new HashMap<String, Serializable>();
            }
            tmp.put(USER, username);
            tmp.put(PASSWORD, password);
            session = createRepositorySession(tmp);
        }
        catch (Exception e)
        {
            alfsession = null;
        }
        return session;
    }

    /**
     * Create a repositorySession with parameters.
     * 
     * @param parameters
     * @return
     */
    public RepositorySession createRepositorySession(Map<String, Serializable> parameters)
    {
        config = new ServerConfigFile(ALFRESCO_CMIS_BASE_URL, ALFRESCO_CMIS_USER, ALFRESCO_CMIS_PASSWORD);

        if (ENABLE_CONFIG_FILE)
        {
            config.parseFile(ONPREMISE_CONFIG_PATH);
        }

        Map<String, Serializable> tmp = parameters;
        RepositorySession session = null;
        try
        {
            if (tmp == null)
            {
                tmp = new HashMap<String, Serializable>();
            }

            String user = (tmp.containsKey(USER)) ? (String) tmp.remove(USER) : config.getUser();
            String password = (tmp.containsKey(PASSWORD)) ? (String) tmp.remove(PASSWORD) : config.getPassword();
            String url = (tmp.containsKey(BASE_URL)) ? (String) tmp.remove(BASE_URL) : config.getUrl();

            tmp.put(SessionParameter.CONNECT_TIMEOUT, "180000");
            tmp.put(SessionParameter.READ_TIMEOUT, "180000");

            session = RepositorySession.connect(url, user, password, parameters);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return session;
    }

    // //////////////////////////////////////////////////////////////////////
    // CREATE CLOUD SESSION
    // //////////////////////////////////////////////////////////////////////
    public String getUsername(String username)
    {
        String tmpusername = username;
        if (ENABLE_CONFIG_FILE)
        {
            if (config.getExtraProperties().containsKey(username))
            {
                tmpusername = config.getExtraProperties().getProperty(username);
            }
        }
        return tmpusername;
    }
    
    public AlfrescoSession createSession(String username, String password, Map<String, Serializable> parameters)
    {
        String tmpusername = username, tmppassword = password;
        if (ENABLE_CONFIG_FILE)
        {
            if (config.getExtraProperties().containsKey(username))
            {
                tmpusername = config.getExtraProperties().getProperty(username);
            }
            if (config.getExtraProperties().containsKey(password))
            {
                tmppassword = config.getExtraProperties().getProperty(password);
            }
        }

        AlfrescoSession session;
        if (isOnPremise())
        {
            session = createRepositorySession(tmpusername, tmppassword, parameters);
        }
        else
        {
            session = createCloudSession(tmpusername, tmppassword, parameters);
        }
        return session;
    }

    public CloudSession createCloudSession()
    {
        return createCloudSession(null);
    }

    protected AlfrescoSession createCloudSession(String username, String password, Map<String, Serializable> parameters)
    {
        AlfrescoSession session = null;
        Map<String, Serializable> tmp = parameters;
        try
        {
            if (parameters == null)
            {
                tmp = new HashMap<String, Serializable>();
            }
            tmp.put(USER, username);
            tmp.put(PASSWORD, password);
            session = createCloudSession(tmp);
        }
        catch (Exception e)
        {
            alfsession = null;
        }
        return session;
    }

    public CloudSession createCloudSession(Map<String, Serializable> parameters)
    {
        config = new ServerConfigFile(ALFRESCO_CLOUD_URL, ALFRESCO_CLOUD_USER, ALFRESCO_CLOUD_PASSWORD);

        if (ENABLE_CONFIG_FILE)
        {
            config.parseFile(CLOUD_CONFIG_PATH);
        }

        Map<String, Serializable> tmp = parameters;
        CloudSession session = null;
        try
        {
            if (tmp == null)
            {
                tmp = new HashMap<String, Serializable>();
            }

            String user = (tmp.containsKey(USER)) ? (String) tmp.remove(USER) : config.getUser();
            String password = (tmp.containsKey(PASSWORD)) ? (String) tmp.remove(PASSWORD) : config.getPassword();
            String url = (tmp.containsKey(BASE_URL)) ? (String) tmp.remove(BASE_URL) : config.getUrl();

            tmp.put(SessionParameter.CONNECT_TIMEOUT, "180000");
            tmp.put(SessionParameter.READ_TIMEOUT, "180000");

            tmp.put(BASE_URL, url);
            tmp.put(USER, user);
            tmp.put(PASSWORD, password);
            tmp.put(CLOUD_BASIC_AUTH, true);
            tmp.put(SessionParameter.CLIENT_COMPRESSION, "true");
            tmp.put(AlfrescoSession.HTTP_ACCEPT_ENCODING, "false");
            tmp.put(AlfrescoSession.HTTP_CHUNK_TRANSFERT, "true");

            session = CloudSession.connect(null, tmp);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return session;
    }

    // //////////////////////////////////////////////////////////////////////////
    // UTILS TO CREATE SAMPLES
    // //////////////////////////////////////////////////////////////////////////
    /**
     * Create Root Folder Sandbox for unit Test.<br>
     * 
     * @return folder object.
     */
    public Folder createUnitTestFolder(AlfrescoSession session)
    {
        return createFolderInSite(alfsession, getSiteName(session));
    }

    private Folder createFolderInSite(AlfrescoSession session, String sitename)
    {
        SiteService siteService = session.getServiceRegistry().getSiteService();
        Folder container = siteService.getDocumentLibrary(siteService.getSite(sitename));
        return createNewFolder(session, container, ROOT_TEST_FOLDER_NAME, null);
    }

    /**
     * Create new folder (delete if already exists and if possible)
     * 
     * @param session : session currently use to create the new Folder.
     * @param parentFolder : parent Folder
     * @param properties : map of properties that folder must include.
     * @return newly created folder.
     */
    public Folder createNewFolder(AlfrescoSession session, Folder parentFolder, String folderName,
            Map<String, Serializable> properties)
    {
        DocumentFolderService docfolderservice = session.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
        Assert.assertNotNull(parentFolder);

        Folder folder = null;
        try
        {
            folder = (Folder) docfolderservice.getChildByPath(parentFolder, folderName);
            if (folder != null)
            {
                List<Node> children = docfolderservice.getChildren(folder);
                for (Node node : children)
                {
                    docfolderservice.deleteNode(node);
                }
                docfolderservice.deleteNode(folder);
                wait(3000);
            }
            folder = docfolderservice.createFolder(parentFolder, folderName, properties);
        }
        catch (AlfrescoException e)
        {
            // In case of folder already presents, we delete it and try to
            // recreate it.
            try
            {
                wait(3000);
                folder = (Folder) docfolderservice.getChildByPath(parentFolder, folderName);
                if (folder != null){
                    docfolderservice.deleteNode(folder);
                }
                wait(3000);
                folder = docfolderservice.createFolder(parentFolder, folderName, properties);
            }
            catch (AlfrescoException e1)
            {
                Log.e(TAG, Log.getStackTraceString(e1));
                Assert.fail(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            Assert.fail(e.getMessage());
        }
        return folder;
    }

    /**
     * Creates a list of sample folder
     * 
     * @param root - parent folder
     * @param size - number of folders to create
     * @throws AlfrescoException
     */
    protected void createFolders(Folder root, int size)
    {
        DocumentFolderService docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        for (int i = 0; i < size; i++)
        {
            try
            {
                Map<String, Serializable> properties = new HashMap<String, Serializable>();
                properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_NAME + "-" + i);
                properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION + "-" + i);
                docfolderservice.createFolder(root, SAMPLE_FOLDER_NAME + "-" + i, properties);
            }
            catch (AlfrescoException e)
            {
                Assert.fail();
            }
        }
    }

    /**
     * Create a list of sample Documents.
     * 
     * @param root
     * @param size
     * @throws AlfrescoException
     */
    protected void createDocuments(Folder root, int size)
    {

        DocumentFolderService docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        HashMap<String, Serializable> newFolderProps = new HashMap<String, Serializable>();

        File f = null;
        String content = null;
        byte[] buf = null;
        ByteArrayInputStream input = null;

        for (int i = 0; i < size; i++)
        {
            newFolderProps.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME + "-" + i + ".txt");
            newFolderProps.put(ContentModel.PROP_DESCRIPTION, "Description" + SAMPLE_DOC_NAME + "-" + i);

            f = new File(getTargetContext().getCacheDir(), SAMPLE_DOC_NAME + ".txt");

            content = SAMPLE_DOC_NAME + "-" + i;
            buf = null;
            try
            {
                buf = content.getBytes("UTF-8");
                input = new ByteArrayInputStream(buf);
                IOUtils.copyFile(input, f);
            }
            catch (Exception e)
            {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            docfolderservice.createDocument(root, SAMPLE_DOC_NAME + "-" + i + ".txt", newFolderProps,
                    new ContentFileImpl(f));
            // wait(3000);
        }
    }

    protected ContentFile createContentFile(String contentValue)
    {
        File f = null;
        byte[] buf = null;
        ByteArrayInputStream input = null;

        f = new File(getTargetContext().getCacheDir(), SAMPLE_DOC_NAME + ".txt");
        buf = null;
        try
        {
            buf = contentValue.getBytes("UTF-8");
            input = new ByteArrayInputStream(buf);
            IOUtils.copyFile(input, f);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        ContentFile cf = new ContentFileImpl(f);
        return cf;
    }

    protected Document createDocument(Folder folder, String docName)
    {
        return alfsession.getServiceRegistry().getDocumentFolderService().createDocument(folder, docName, null, null);
    }

    /**
     * Reads the content from a content stream into a byte array.
     * 
     * @throws IOException
     */
    protected String readContent(ContentStream contentStream) throws IOException
    {
        assertNotNull(contentStream);
        assertNotNull(contentStream.getInputStream());

        InputStream stream = contentStream.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int b;
        while ((b = stream.read(buffer)) > -1)
        {
            baos.write(buffer, 0, b);
        }

        return baos.toString();
    }

    protected Document createEmptyDocument(Folder root, String docName)
    {
        HashMap<String, Serializable> newFolderProps = new HashMap<String, Serializable>();
        newFolderProps.put(ContentModel.PROP_TITLE, docName);
        newFolderProps.put(ContentModel.PROP_DESCRIPTION, "Description : " + docName);
        return alfsession.getServiceRegistry().getDocumentFolderService()
                .createDocument(root, docName, newFolderProps, null);
    }

    protected Document createDeletedDocument(Folder root, String docName)
    {
        Document doc = createEmptyDocument(root, docName);
        alfsession.getServiceRegistry().getDocumentFolderService().deleteNode(doc);
        return doc;
    }

    protected Folder createDeletedFolder(Folder root, String folderName)
    {
        Folder folder = alfsession.getServiceRegistry().getDocumentFolderService().createFolder(root, folderName, null);
        alfsession.getServiceRegistry().getDocumentFolderService().deleteNode(folder);
        return folder;
    }

    protected Document createDocumentFromAsset(Folder root, String assetName)
    {
        DocumentFolderService docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        AssetManager assetManager = getContext().getAssets();
        ContentFile cf = null;
        try
        {
            File f = new File(AbstractAlfrescoSessionImpl.DEFAULT_CACHE_FOLDER_PATH, assetName);
            IOUtils.copyFile(assetManager.open(assetName), f);
            cf = new ContentFileImpl(f);
        }
        catch (IOException e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            Assert.fail();
        }

        HashMap<String, Serializable> newFolderProps = new HashMap<String, Serializable>();
        newFolderProps.put(ContentModel.PROP_TITLE, assetName);
        newFolderProps.put(ContentModel.PROP_DESCRIPTION, "Description" + SAMPLE_DOC_NAME);

        return docfolderservice.createDocument(root, assetName, newFolderProps, cf);
    }

    // //////////////////////////////////////////////////////////////////////////
    // UTILS TO GET SAMPLE DATA ROOT FOLDER
    // //////////////////////////////////////////////////////////////////////////
    public static String getUnitTestFolderPath(AlfrescoSession session)
    {
        return getFolderPath(session);
    }

    public static String getSitePath(AlfrescoSession session)
    {
        return "Sites/" + getSiteName(session);
    }

    public static String getSitePath(String siteShortName)
    {
        return "Sites/" + siteShortName;
    }

    public static String getFolderPath(AlfrescoSession session)
    {
        return getSitePath(session) + "/documentLibrary/" + ROOT_TEST_FOLDER_NAME;
    }

    public static String getSampleDataPath(AlfrescoSession session)
    {
        return getSitePath(session) + "/documentLibrary/" + ROOT_TEST_SAMPLE_DATA;
    }

    public static String getSiteName(AlfrescoSession session)
    {
        String siteName = null;
        if (session instanceof RepositorySession)
        {
            siteName = ONPREMISE_SITENAME;
        }
        else if (session instanceof CloudSession)
        {
            siteName = CLOUD_SITENAME;
        }
        return siteName;
    }

    public static SiteVisibility getSiteVisibility(AlfrescoSession session)
    {
        return SiteVisibility.PRIVATE;
    }

    // //////////////////////////////////////////////////////////////////////////
    // UTILS TO GET CONTEXT
    // //////////////////////////////////////////////////////////////////////////
    /**
     * Retrieve the context of the test application.
     * 
     * @return test context
     */
    protected Context getContext()
    {
        return getInstrumentation().getContext();
    }

    /**
     * Retrieve the context of the sample application.
     * 
     * @return tested application context
     */
    protected Context getTargetContext()
    {
        return getInstrumentation().getTargetContext();
    }

    // //////////////////////////////////////////////////////////////////////////
    // UTILS
    // //////////////////////////////////////////////////////////////////////////
    /**
     * Sometimes we have to wait...
     * 
     * @param milliseconds : milliseconds you want to wait before continuing
     *            test.
     */
    protected void wait(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException ex)
        {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
    }

    public boolean compareDate(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2
                .get(Calendar.DAY_OF_YEAR));
    }

    /**
     * Detect if Alfresco repository server is on version 4 or above.
     * 
     * @return true if version 4 or below. False if version below or not
     *         Alfresco server.
     */
    protected boolean isAlfrescoV4()
    {
        if (!RepositoryVersionHelper.isAlfrescoProduct(alfsession)) { return false; }
        return (alfsession.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4);
    }

    /**
     * Detect if it's an Alfresco Repository server.
     * 
     * @return true if alfresco vendor is Alfresco.
     */
    protected boolean isAlfresco()
    {
        return RepositoryVersionHelper.isAlfrescoProduct(alfsession);
    }

    /**
     * Detect if the repository test server is OnPremise
     * 
     * @param session : specific alfresco session.
     * @return true if on premise, else cloud.
     */
    protected boolean isOnPremise(AlfrescoSession session)
    {
        return (session instanceof RepositorySession);
    }

    /**
     * Detect if the repository test server is OnPremise
     * 
     * @return true if on premise, else cloud.
     */
    protected boolean isOnPremise()
    {
        return isOnPremise(alfsession);
    }
    
    protected boolean hasPublicAPI()
    {
       return isOnPremise() && ((RepositorySessionImpl) alfsession).hasPublicAPI();
    }

    protected AuthenticationProvider getAuthenticationProvider()
    {
        return getAuthenticationProvider(alfsession);
    }

    protected AuthenticationProvider getAuthenticationProvider(AlfrescoSession session)
    {
        return ((AbstractAlfrescoSessionImpl) session).getAuthenticationProvider();
    }

    protected void checkSession()
    {
        Log.w(TAG, "---------------------------------------------");
        Log.w(TAG, "Unable to test : No extra users available");
        Log.w(TAG, "---------------------------------------------");
    }

    protected void checkSession(AlfrescoSession session)
    {
        if (session == null)
        {
            checkSession();
        }
    }

    // //////////////////////////////////////////////////////////////////////////
    // AFTER / BEFORE TEST CASE
    // //////////////////////////////////////////////////////////////////////////
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        initSession();
    }

    /**
     * Generic initialization method to create an AlfrescoSession and retrieve
     * some services.
     */
    protected abstract void initSession();

    @Override
    protected void tearDown() throws Exception
    {
        alfsession = null;
        super.tearDown();
    }
}
