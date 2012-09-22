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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.SiteVisibility;
import org.alfresco.mobile.android.api.model.impl.ContentFileImpl;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.test.constant.ConfigurationConstant;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.impl.SessionImpl;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.test.InstrumentationTestCase;
import android.util.Log;

public abstract class AlfrescoSDKTestCase extends InstrumentationTestCase
{
    public static final String TAG = "AlfrescoSDKTestCase";

    // //////////////////////////////////////////////////////////////////////
    // CONFIGURATION FILE CONFIG
    // //////////////////////////////////////////////////////////////////////
    /**
     * Flag to enable config file inside any device or emulator to override this
     * file constant.
     */
    protected static final boolean ENABLE_CONFIG_FILE = true;

    private static final String CMIS_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/alfresco-mobile/cmis-config.properties";

    private static final String ONPREMISE_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/alfresco-mobile/" + ConfigurationConstant.ONPREMISE_FILENAME;

    // //////////////////////////////////////////////////////////////////////
    // SERVER TEST CONFIG
    // //////////////////////////////////////////////////////////////////////

    public static final String CHEMISTRY_INMEMORY_ATOMPUB_URL = "http://repo.opencmis.org/inmemory/atom/";

    public static final String CHEMISTRY_INMEMORY_BASE_URL = "http://repo.opencmis.org/inmemory/";

    public static final String CHEMISTRY_INMEMORY_USER = "admin";

    public static final String CHEMISTRY_INMEMORY_PASSWORD = "admin";

    public static final String ALFRESCO_CMIS_ATOMPUB_URL = "http://cmis.alfresco.com/cmisatom";

    public static final String ALFRESCO_CMIS_BASE_URL = "http://cmis.alfresco.com";

    public static final String ALFRESCO_CMIS_USER = "admin";

    public static final String ALFRESCO_CMIS_PASSWORD = "admin";

    protected static final String BINDING_URL = "org.alfresco.mobile.binding.internal.url";

    protected static final String BASE_URL = "org.alfresco.mobile.binding.internal.baseurl";

    protected static final String USER = "org.alfresco.mobile.credential.user";

    protected static final String PASSWORD = "org.alfresco.mobile.credential.password";

    // //////////////////////////////////////////////////////////////////////
    // CONSTANT
    // //////////////////////////////////////////////////////////////////////
    public static final String ALFRESCO_CMIS_NAME = "Main Repository";

    protected static final String FAKE_USERNAME = "FAKE_USERNAME";

    protected static final String FAKE_SITENAME = "FAKE_SITENAME";

    public static final String CMIS_VERSION = "1.0";

    public static final String ROOT_TEST_FOLDER_NAME = "android-mobile-test";

    /** Default Site available in Alfresco. */
    public static final String SITENAME = "swsdp";

    public static final String FOREIGN_CHARACTER = "ß";

    public static final String FOREIGN_CHARACTER_DOUBLE_BYTE = "平";

    // //////////////////////////////////////////////////////////////////////
    // Members
    // //////////////////////////////////////////////////////////////////////
    protected AlfrescoSession alfsession;

    // //////////////////////////////////////////////////////////////////////
    // USERS
    // //////////////////////////////////////////////////////////////////////
    public static final String USER1 = "user1";

    public static final String USER2 = "user2";

    public static final String USER1_PASSWORD = "user1Alfresco";

    public static final String USER2_PASSWORD = "user2Alfresco";

    // //////////////////////////////////////////////////////////////////////
    // CONSTANT
    // //////////////////////////////////////////////////////////////////////
    protected static final String SAMPLE_DATA_COMMENT_FOLDER = "Comment";

    protected static final String SAMPLE_DATA_COMMENT_FILE = "file.txt";

    protected static final String SAMPLE_DATA_PATH_COMMENT_FILE = "/" + SAMPLE_DATA_COMMENT_FOLDER + "/"
            + SAMPLE_DATA_COMMENT_FILE;

    // //////////////////////////////////////////////////////////////////////
    // MANAGE SESSION METHODS
    // //////////////////////////////////////////////////////////////////////
    /**
     * Create a default CMIS Session defined by </br> Constant :
     * CHEMISTRY_INMEMORY_ATOMPUB_URL, CHEMISTRY_INMEMORY_USER,
     * CHEMISTRY_INMEMORY_PASSWORD </br> or </br> by properties file inside your
     * device/emulator : /sdcard/alfresco-mobile/cmis-config.properties</br>
     * 
     * @return
     */
    public static RepositorySession createCMISSession()
    {

        String url = CHEMISTRY_INMEMORY_ATOMPUB_URL;
        String binding = CHEMISTRY_INMEMORY_ATOMPUB_URL;
        String user = CHEMISTRY_INMEMORY_USER;
        String password = CHEMISTRY_INMEMORY_PASSWORD;

        // Check Properties available inside the device
        File f = new File(CMIS_CONFIG_PATH);
        if (f.exists() && ENABLE_CONFIG_FILE)
        {
            Properties prop = new Properties();
            InputStream is = null;
            try
            {
                is = new FileInputStream(f);
                // load a properties file
                prop.load(is);

                url = prop.getProperty("url");
                binding = prop.getProperty("binding");
                user = prop.getProperty("user");
                password = prop.getProperty("password");
            }
            catch (IOException ex)
            {
                Log.e(TAG, Log.getStackTraceString(ex));
            }
            finally
            {
                IOUtils.closeStream(is);
            }
        }

        RepositorySession session = null;
        try
        {
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BINDING_URL, binding);
            session = RepositorySession.connect(url, user, password, settings);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            Assert.fail(e.getMessage());
        }

        if (session == null)
        {
            Assert.fail("Error during creating session. Check if cmis.alfresco.com is available");
        }

        return session;
    }

    /**
     * Create a default Alfresco Repository Session defined by : </br> Constant
     * : MY_ALFRESCO_URL, MY_ALFRESCO_USER, MY_ALFRESCO_PASSWORD </br> or </br>
     * by properties file inside your device/emulator :
     * /sdcard/alfresco-mobile/config.properties</br>
     * 
     * @return Repository session
     */
    public RepositorySession createRepositorySession()
    {
        return createRepositorySession(null);
    }

    public RepositorySession createRepositorySession(Map<String, Serializable> parameters)
    {
        String url = ALFRESCO_CMIS_BASE_URL;
        String user = ALFRESCO_CMIS_USER;
        String password = ALFRESCO_CMIS_PASSWORD;

        // Check Properties available inside the device
        File f = new File(ONPREMISE_CONFIG_PATH);
        if (f.exists() && ENABLE_CONFIG_FILE)
        {
            Properties prop = new Properties();
            InputStream is = null;
            try
            {
                is = new FileInputStream(f);
                // load a properties file
                prop.load(is);

                url = prop.getProperty("url");
                user = prop.getProperty("user");
                password = prop.getProperty("password");
            }
            catch (IOException ex)
            {
                Log.e(TAG, Log.getStackTraceString(ex));
            }
            finally
            {
                IOUtils.closeStream(is);
            }
        }

        RepositorySession session = null;
        try
        {
            if (parameters != null)
            {
                if (parameters.containsKey(USER))
                {
                    user = (String) parameters.remove(USER);
                }
                if (parameters.containsKey(PASSWORD))
                {
                    password = (String) parameters.remove(PASSWORD);
                }
            }
            else
            {
                parameters = new HashMap<String, Serializable>();
            }

            parameters.put(SessionParameter.CONNECT_TIMEOUT, "180000");
            parameters.put(SessionParameter.READ_TIMEOUT, "180000");

            session = RepositorySession.connect(url, user, password, parameters);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            Assert.fail(e.getMessage());
        }

        if (session == null)
        {
            Assert.fail("Error during creating session. Check if " + ALFRESCO_CMIS_BASE_URL + " is available");
        }

        return session;
    }

    protected AlfrescoSession createCustomRepositorySession(String username, String password,
            Map<String, Serializable> parameters)
    {
        AlfrescoSession session = null;
        try
        {
            if (parameters == null)
            {
                parameters = new HashMap<String, Serializable>();
            }
            parameters.put(USER, username);
            parameters.put(PASSWORD, password);
            session = createRepositorySession(parameters);
        }
        catch (Exception e)
        {
            alfsession = null;
        }

        return session;
    }

    /**
     * TODO to remove...
     * 
     * @param alfsession
     * @return
     */
    protected BindingSession getBindingSessionHttp(AlfrescoSession alfsession)
    {
        BindingSession s = new SessionImpl();
        s.put(CmisBindingsHelper.AUTHENTICATION_PROVIDER_OBJECT,
                ((AbstractAlfrescoSessionImpl) alfsession).getPassthruAuthenticationProvider());
        return s;
    }

    // //////////////////////////////////////////////////////////////////////////
    // UTILS TO CREATE SAMPLES
    // //////////////////////////////////////////////////////////////////////////
    protected static final String SAMPLE_FOLDER_NAME = "sampleFolder";

    protected static final String SAMPLE_FOLDER_DESCRIPTION = "sampleFolderDescription";

    protected static final String SAMPLE_DOC_NAME = "sampleDocuments";

    /**
     * Create Root Folder Sandbox for unit Test.<br>
     * Name : ROOT_TEST_FOLDER_NAME<br>
     * Path : /ROOT_TEST_FOLDER_NAME<br>
     * 
     * @return folder object.
     */
    public Folder createUnitTestFolder(AlfrescoSession session)
    {
        if (session instanceof RepositorySession)
        {
            return createNewFolder(session, session.getRootFolder(), ROOT_TEST_FOLDER_NAME, null);
        }
        else if (session instanceof CloudSession) { return AlfrescoSDKCloudTestCase.createCloudFolder(alfsession); }
        return null;
    }

    /**
     * Create new folder (delete if already exists and if possible)
     * 
     * @param session : session currently use to create the new Folder.
     * @param parentFolder : parent Folder
     * @param properties : map of properties that folder must include.
     * @return newly created folder.
     */
    public static Folder createNewFolder(AlfrescoSession session, Folder parentFolder, String folderName,
            Map<String, Serializable> properties)
    {
        DocumentFolderService docfolderservice = session.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
        Assert.assertNotNull(parentFolder);

        Folder folder = null;
        try
        {
            folder = docfolderservice.createFolder(parentFolder, folderName, properties);
        }
        catch (AlfrescoException e)
        {
            // In case of folder already presents, we delete it and try to
            // recreate it.
            try
            {
                folder = (Folder) docfolderservice.getChildByPath(parentFolder, folderName);
                docfolderservice.deleteNode(folder);
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

    /**
     * Reads the content from a content stream into a byte array.
     */
    protected String readContent(ContentStream contentStream) throws Exception
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

    protected Document createDocumentFromAsset(Folder root, String assetName) throws AlfrescoException
    {

        DocumentFolderService docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        AssetManager assetManager = getContext().getAssets();
        ContentFile cf = null;
        try
        {
            File f = new File(AbstractAlfrescoSessionImpl.CACHE_FOLDER_PATH, assetName);
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

    public static String getUnitTestFolderPath(AlfrescoSession session)
    {
        if (session instanceof RepositorySession)
        {
            return getFolderPath();
        }
        else if (session instanceof CloudSession) { return AlfrescoSDKCloudTestCase.getCloudFolderPath(); }
        return null;
    }

    private static String getFolderPath()
    {
        return "/" + ROOT_TEST_FOLDER_NAME;
    }

    // //////////////////////////////////////////////////////////////////////////
    // UTILS TO GET SAMPLE DATA ROOT FOLDER
    // //////////////////////////////////////////////////////////////////////////
    public static String getSampleDataPath(AlfrescoSession session)
    {
        if (session instanceof RepositorySession)
        {
            return getOnPremiseSampleDataPath();
        }
        else if (session instanceof CloudSession) { return AlfrescoSDKCloudTestCase.getCloudSampleDataFolderPath(); }
        return null;

    }

    private static String getOnPremiseSampleDataPath()
    {
        return "/Sample data";
    }

    public static String getSiteName(AlfrescoSession session)
    {
        if (session instanceof RepositorySession)
        {
            return SITENAME;
        }
        else if (session instanceof CloudSession) { return AlfrescoSDKCloudTestCase.SITENAME; }
        return null;
    }

    public static String getSitePath(AlfrescoSession session)
    {
        if (session instanceof RepositorySession)
        {
            return "Sites/" + SITENAME;
        }
        else if (session instanceof CloudSession) { return "Sites/" + AlfrescoSDKCloudTestCase.SITENAME; }
        return null;
    }

    public static SiteVisibility getSiteVisibility(AlfrescoSession session)
    {
        if (session instanceof RepositorySession)
        {
            return SiteVisibility.PUBLIC;
        }
        else if (session instanceof CloudSession) { return SiteVisibility.PRIVATE; }
        return null;
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

    public boolean compareDate(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2
                .get(Calendar.DAY_OF_YEAR));
    }

}
