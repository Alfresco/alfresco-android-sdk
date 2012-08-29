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

import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.mobile.android.api.constants.CMISMapping;
import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Permissions;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.model.Sorting;
import org.alfresco.mobile.android.api.model.impl.InfoHelper;
import org.alfresco.mobile.android.api.model.impl.PermissionsImpl;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.SessionSettings;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AtomPubParser;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.commons.spi.NavigationService;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;
import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * DocumentFolderService manages Folders and Documents in an Alfresco
 * repository. The service provides methods to create and update nodes. The
 * DocumentFolderService supports the following methods:
 * <ul>
 * <li>Create nodes and set property values</li>
 * <li>Read node properties and content, read and navigate node associations
 * (browse folder)</li>
 * <li>Update properties and content of nodes.</li>
 * <li>Delete nodes. If the archive store is enabled, the node is not deleted
 * but moved from its current node to the archive node store; nodes in the
 * archive store can then be restored or purged.</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public class DocumentFolderServiceImpl extends AlfrescoService implements DocumentFolderService
{
    /** Internal Tag for Logger. */
    private static final String TAG = "DocumentFolderService";

    private Session cmisSession;

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public DocumentFolderServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
        cmisSession = repositorySession.getCmisSession();
    }

    // ////////////////////////////////////////////////////
    // NAVIGATION
    // ////////////////////////////////////////////////////
    private static final String PARAM_NODEREF = "{noderef}";

    /** Specific query used for listFolder() */
    private static final String QUERY_CHILD_FOLDER = "SELECT * FROM cmis:folder WHERE IN_FOLDER('" + PARAM_NODEREF
            + "')";

    /** Specific query used for listDocuments() */
    private static final String QUERY_CHILD_DOCS = "SELECT * FROM cmis:document WHERE IN_FOLDER('" + PARAM_NODEREF
            + "')";

    /**
     * Lists all immediate child nodes of the given context folder. </br> By
     * default, this list contains a maximum of 50 elements. </br> Use
     * {@link DocumentFolderServiceImpl#getChildren(Folder, ListingContext)} to
     * change this behaviour.
     * 
     * @param parentFolder : context folder
     * @return a list of Nodes (could contains Folder and/or Documents objects)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Node> getChildren(Folder parentFolder) throws AlfrescoServiceException
    {
        return getChildren(parentFolder, null).getList();
    }

    /**
     * Lists immediate child nodes of the given context folder.
     * 
     * @param parentFolder : context folder
     * @param lcontext : Listing context that define the behaviour of paging
     *            results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return PagingResult object that contains a list of Nodes (could contains
     *         Folder and/or Documents objects)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Node> getChildren(Folder parentFolder, ListingContext lcontext) throws AlfrescoServiceException
    {
        try
        {
            NavigationService navigationService = cmisSession.getBinding().getNavigationService();
            OperationContext ctxt = new OperationContextImpl(cmisSession.getDefaultContext());
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            // By default Listing context has default value
            String orderBy = null;
            BigInteger maxItems = null;
            BigInteger skipCount = null;

            if (lcontext != null)
            {
                orderBy = getSorting(lcontext.getSortProperty(), lcontext.isSortAscending());
                maxItems = BigInteger.valueOf(lcontext.getMaxItems());
                skipCount = BigInteger.valueOf(lcontext.getSkipCount());
            }
            // get the children
            ObjectInFolderList children = navigationService.getChildren(session.getRepositoryInfo().getIdentifier(),
                    parentFolder.getIdentifier(), ctxt.getFilterString(), orderBy, ctxt.isIncludeAllowableActions(),
                    ctxt.getIncludeRelationships(), ctxt.getRenditionFilterString(), ctxt.isIncludePathSegments(),
                    maxItems, skipCount, null);

            // convert objects
            List<Node> page = new ArrayList<Node>();
            List<ObjectInFolderData> childObjects = children.getObjects();
            if (childObjects != null) Log.d("BrowserFragment", "childObjects : " + childObjects.size());

            if (childObjects != null)
            {
                for (ObjectInFolderData objectData : childObjects)
                {
                    if (objectData.getObject() != null)
                    {
                        Node n = convertNode(objectFactory.convertObject(objectData.getObject(), ctxt));
                        page.add(n);
                        Log.d("BrowserFragment", "ITEMS : " + n.getName());
                    }
                }
            }

            Boolean hasMoreItem = false;
            if (maxItems != null)
                hasMoreItem = children.hasMoreItems() && page.size() == maxItems.intValue();
            else
                hasMoreItem = children.hasMoreItems();

            return new PagingResult<Node>(page, hasMoreItem, children.getNumItems().intValue());
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Gets the node object stored at the specified path.
     * 
     * @param path : path from the root folder.
     * @return a node object available at the specified path.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Node getChildByPath(String path) throws AlfrescoServiceException
    {
        return getChildByPath(getRootFolder(), path);
    }

    /**
     * Gets the node object stored at the relative specified path from the
     * folder object.
     * 
     * @param relativePathFromFolder : relative path from the root folder.
     * @return a node object available at the specified path.
     */
    public Node getChildByPath(Folder folder, String relativePathFromFolder) throws AlfrescoServiceException
    {
        try
        {
            String path = folder.getPropertyValue(PropertyIds.PATH);
            if (path.equals("/")) path = "";

            if (relativePathFromFolder == null) { throw new IllegalArgumentException("Path must be set!"); }

            if (relativePathFromFolder.length() > 1 && relativePathFromFolder.endsWith("/"))
                relativePathFromFolder = relativePathFromFolder.substring(0, relativePathFromFolder.length() - 1);

            if (relativePathFromFolder.length() > 1 && !relativePathFromFolder.startsWith("/"))
                relativePathFromFolder = "/" + relativePathFromFolder;

            path = path.concat(relativePathFromFolder);

            Node result = null;

            OperationContext context = cmisSession.getDefaultContext();
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            // get the object
            ObjectData objectData = objectService.getObjectByPath(session.getRepositoryInfo().getIdentifier(), path,
                    context.getFilterString(), context.isIncludeAllowableActions(), context.getIncludeRelationships(),
                    context.getRenditionFilterString(), context.isIncludePolicies(), context.isIncludeAcls(), null);

            result = convertNode(objectFactory.convertObject(objectData, context));

            return result;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Gets the node object with the specified identifier.
     * 
     * @param identifier : unique identifier
     * @return a node object available with the specified identifier.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Node getNodeByIdentifier(String identifier) throws AlfrescoServiceException
    {
        try
        {
            return getChildById(identifier);
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Get Root Node of the repository.
     * 
     * @return the folder root object.
     */
    public Folder getRootFolder()
    {
        return session.getRootFolder();
    }

    /**
     * Lists all immediate child folders of the given context node Note: this
     * could be a long list
     * 
     * @param parentFolder : Parent Folder
     * @return a list of folder object child of the parent folder
     * @see #getFolders(Folder, ListingContext)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Folder> getFolders(Folder parentFolder) throws AlfrescoServiceException
    {
        return getFolders(parentFolder, null).getList();
    }

    /**
     * Lists all immediate child folders of the given context node </br>Note:
     * this could be a long list
     * 
     * @param parentFolder : Parent Folder
     * @return folder children as a pagingResult
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Folder> getFolders(Folder folder, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        try
        {
            String statement = QUERY_CHILD_FOLDER.replace(PARAM_NODEREF, folder.getIdentifier().toString());
            SearchService searchService = session.getServiceRegistry().getSearchService();
            PagingResult<Node> nodes = searchService.search(statement, SearchLanguage.CMIS_SQL_STRICT, listingContext);
            List<Folder> folders = new ArrayList<Folder>(nodes.getList().size());
            for (Node node : nodes.getList())
            {
                folders.add((Folder) node);
            }

            return new PagingResult<Folder>(folders, nodes.hasMoreItems(), nodes.getTotalItems());
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Lists all immediate child documents of the given context node </br>Note:
     * this could be a long list
     * 
     * @param parentFolder : Parent Folder
     * @return a list of document object child of the parent folder
     * @see #getDocuments(Folder, ListingContext)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Document> getDocuments(Folder folder) throws AlfrescoServiceException
    {
        return getDocuments(folder, null).getList();
    }

    /**
     * Lists all immediate child documents of the given context node Note: this
     * could be a long list
     * 
     * @param parentFolder : Parent Folder
     * @return document children as a pagingResult
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Document> getDocuments(Folder folder, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        try
        {
            String statement = QUERY_CHILD_DOCS.replace(PARAM_NODEREF, folder.getIdentifier().toString());
            SearchService searchService = session.getServiceRegistry().getSearchService();
            PagingResult<Node> nodes = searchService.search(statement, SearchLanguage.CMIS_SQL_STRICT, listingContext);
            List<Document> documents = new ArrayList<Document>(nodes.getList().size());
            for (Node node : nodes.getList())
            {
                documents.add((Document) node);
            }

            PagingResult<Document> docs = new PagingResult<Document>(documents, nodes.hasMoreItems(),
                    nodes.getTotalItems());

            return docs;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Gets the direct parent folder object.
     * 
     * @param node : Node object (Folder or Document).
     * @return parent folder object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Folder getParentFolder(Node node) throws AlfrescoServiceException
    {
        try
        {
            if (getRootFolder().equals(node)) { return null; }

            String objectId = node.getIdentifier();

            OperationContext context = cmisSession.getDefaultContext();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            ObjectData bindingParent = cmisSession.getBinding().getNavigationService()
                    .getFolderParent(session.getRepositoryInfo().getIdentifier(), objectId, null, null);
            Folder result = (Folder) convertNode(objectFactory.convertObject(bindingParent, context));

            return result;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // CREATION
    // ////////////////////////////////////////////////////
    private static final Set<Updatability> CREATE_UPDATABILITY = new HashSet<Updatability>();
    static
    {
        CREATE_UPDATABILITY.add(Updatability.ONCREATE);
        CREATE_UPDATABILITY.add(Updatability.READWRITE);
    }

    /**
     * Creates a folder object in the specified location with specified
     * properties.
     * 
     * @param parentFolder : Parent Folder
     * @param folderName : Name of the future folder
     * @param properties : Map of properties to apply to the new folder
     * @return Returns the newly created folder
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties)
            throws AlfrescoServiceException
    {
        try
        {
            Node n = null;
            if (properties == null)
            {
                properties = new HashMap<String, Serializable>();
            }
            properties.put(ContentModel.PROP_NAME, folderName);

            convertProps(properties, BaseTypeId.CMIS_FOLDER.value());
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            String newId = objectService.createFolder(session.getRepositoryInfo().getIdentifier(),
                    objectFactory.convertProperties(properties, null, CREATE_UPDATABILITY),
                    parentFolder.getIdentifier(), null, null, null, null);

            if (newId == null) { return null; }

            n = getChildById(newId);

            if (!(n instanceof Folder)) { throw new AlfrescoServiceException(
                    "Newly created object is not a folder! New id: " + newId); }
            return (Folder) n;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Creates a document object in the specified location with specified
     * properties. </br> It can launch metadata extraction and thumbnail
     * generation after creation.
     * 
     * @param parentFolder : Future parent folder of a new document
     * @param properties : (Optional) list of property values that must be
     *            applied
     * @param contentFile : (Optional) ContentFile that contains data stream or
     *            file
     * @return the newly created document object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile contentFile) throws AlfrescoServiceException
    {
        try
        {
            if (properties == null) properties = new HashMap<String, Serializable>();

            properties.put(ContentModel.PROP_NAME, documentName);

            convertProps(properties, BaseTypeId.CMIS_DOCUMENT.value());
            if ((properties == null) || (properties.isEmpty())) { throw new IllegalArgumentException(
                    Messagesl18n.getString("DocumentFolderService.0")); }

            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            ContentStream c = null;
            if (contentFile != null)
            {
                c = objectFactory.createContentStream(documentName, contentFile.getLength(), contentFile.getMimeType(),
                        IOUtils.getContentFileInputStream(contentFile));
            }

            String newId = objectService.createDocument(session.getRepositoryInfo().getIdentifier(),
                    objectFactory.convertProperties(properties, null, CREATE_UPDATABILITY),
                    parentFolder.getIdentifier(), c, VersioningState.MAJOR, null, null, null, null);

            // EXTRACT METADATA + Generate Thumbnails
            if (InfoHelper.isAlfrescoProduct(session))
            {
                Log.d("ExtractMetadata", "ExtractMetadata");
                if (session.getParameter(SessionSettings.EXTRACT_METADATA) != null
                        && (Boolean) session.getParameter(SessionSettings.EXTRACT_METADATA)) extractMetadata(newId);
                if (session.getParameter(SessionSettings.CREATE_THUMBNAIL) != null
                        && (Boolean) session.getParameter(SessionSettings.CREATE_THUMBNAIL)) generateThumbnail(newId);
            }

            if (newId == null) { return null; }

            Node n = getChildById(newId);

            if (!(n instanceof Document))
                throw new AlfrescoServiceException(Messagesl18n.getString("DocumentFolderService.19") + newId);
            return (Document) n;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Force metadata extraction for a specific node identifier.
     * 
     * @param identifier : unique identifier of a node (Document)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    private void extractMetadata(String identifier)
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getActionQueue(session));
            url.addParameter(OnPremiseConstant.PARAM_ASYNC, true);
            Log.d("URL", url.toString());

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.ACTIONEDUPONNODE_VALUE, NodeRefUtils.getCleanIdentifier(identifier));
            jo.put(OnPremiseConstant.ACTIONDEFINITIONNAME_VALUE, OnPremiseConstant.ACTION_EXTRACTMETADATA_VALUE);

            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            Response response = post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            });

            if (response.getResponseCode() == HttpStatus.SC_OK)
            {
                Log.d(TAG, "Metadata extraction : ok");
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            convertException(e);
        }
    }

    /**
     * Force creation of the doclib thumbnail.
     * 
     * @param identifier : unique identifier of a node (Document)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    private void generateThumbnail(String identifier)
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getThumbnailUrl(session, identifier));
            url.addParameter(OnPremiseConstant.PARAM_AS, true);
            Log.d("URL", url.toString());

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.THUMBNAILNAME_VALUE, RENDITION_THUMBNAIL);

            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            });
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            convertException(e);
        }
    }

    // ////////////////////////////////////////////////////
    // DELETE
    // ////////////////////////////////////////////////////
    /**
     * Deletes the specified object.
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public void deleteNode(Node node) throws AlfrescoServiceException
    {
        try
        {
            if (node.isDocument())
                delete((Document) node);
            else if (node.isFolder()) delete((Folder) node);
        }
        catch (Throwable e)
        {
            convertException(e);
        }
    }

    /**
     * Deletes the specified document.
     * 
     * @param document : Document to delete.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    private void delete(Document document) throws AlfrescoServiceException
    {
        Permissions perm = getPermissions(document);
        if (!perm.canDelete()) { throw new AlfrescoServiceException(Messagesl18n.getString("DocumentFolderService.1")); }

        try
        {
            
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            objectService.deleteObject(session.getRepositoryInfo().getIdentifier(), document.getIdentifier(), true,
                    null);
            cmisSession.removeObjectFromCache(document.getIdentifier());
        }
        catch (CmisConstraintException e)
        {
            throw new AlfrescoServiceException(e.getErrorContent());
        }
    }

    /**
     * Deletes the specified folder object and all its children.
     * 
     * @param folder : folder to delete.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    private void delete(Folder folder) throws AlfrescoServiceException
    {
        Permissions perm = getPermissions(folder);
        if (!perm.canDelete()) { throw new AlfrescoServiceException(Messagesl18n.getString("DocumentFolderService.24")); }
        try
        {
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            objectService.deleteTree(session.getRepositoryInfo().getIdentifier(), folder.getIdentifier(), true, null,
                    false, null);
        }
        catch (CmisConstraintException e)
        {
            throw new AlfrescoServiceException(e.getErrorContent());
        }
    }

    // ////////////////////////////////////////////////////
    // UPDATE
    // ////////////////////////////////////////////////////

    /**
     * Updates properties for the specified object. Can accept Alfresco Content
     * Model Properties id or cmis properties id.
     * 
     * @param node : Node to update
     * @param properties : Properties to update.
     * @return : Newly update node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Node updateProperties(Node node, Map<String, Serializable> properties) throws AlfrescoServiceException
    {
        try
        {
            convertProps(properties, node.getType());

            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            String objectId = node.getIdentifier().toString();
            Holder<String> objectIdHolder = new Holder<String>(objectId);

            Holder<String> changeTokenHolder = null;
            if (node.getProperty(PropertyIds.CHANGE_TOKEN) != null
                    && node.getProperty(PropertyIds.CHANGE_TOKEN).getValue() != null)
            {
                changeTokenHolder = new Holder<String>(node.getProperty(PropertyIds.CHANGE_TOKEN).getValue().toString());
            }

            Set<Updatability> updatebility = new HashSet<Updatability>();
            updatebility.add(Updatability.READWRITE);

            // check if checked out
            Boolean isCheckedOut = (Boolean) node.getProperty(PropertyIds.IS_VERSION_SERIES_CHECKED_OUT).getValue();
            if ((isCheckedOut != null) && isCheckedOut.booleanValue())
            {
                updatebility.add(Updatability.WHENCHECKEDOUT);
            }

            // it's time to update
            objectService.updateProperties(session.getRepositoryInfo().getIdentifier(), objectIdHolder,
                    changeTokenHolder, objectFactory.convertProperties(properties,
                            cmisSession.getTypeDefinition(node.getType()), updatebility), null);

            return getChildById(objectId);
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // CONTENT
    // ////////////////////////////////////////////////////
    /**
     * Updates the content on the given document using the provided local file.
     * 
     * @param document : Document object
     * @param file : File that is going to replace document content
     * @return newly updated Document.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Document setContent(Document content, ContentFile contentFile) throws AlfrescoServiceException
    {
        Document newContent = null;
        try
        {
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            Holder<String> objectIdHolder = new Holder<String>(content.getIdentifier());
            Holder<String> changeTokenHolder = new Holder<String>((String) content
                    .getProperty(PropertyIds.CHANGE_TOKEN).getValue());

            ContentStream c = null;
            if (contentFile != null)
            {
                c = objectFactory.createContentStream(contentFile.getFileName(), contentFile.getLength(),
                        content.getContentStreamMimeType(), IOUtils.getContentFileInputStream(contentFile));
            }

            objectService.setContentStream(session.getRepositoryInfo().getIdentifier(), objectIdHolder, true,
                    changeTokenHolder, c, null);

            newContent = (Document) getNodeByIdentifier(content.getIdentifier());

        }
        catch (Throwable e)
        {
            convertException(e);
        }

        return newContent;
    }

    /**
     * Retrieves the content stream for the given document.
     * 
     * @param document : Document object
     * @return the contentFile representation that contains file informations +
     *         inputStream of the content.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    @Override
    public ContentFile downloadContent(Document document)
    {
        try
        {
            return saveContentStream(downloadContentStream(document), document.getIdentifier());
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    @Override
    public org.alfresco.mobile.android.api.model.ContentStream downloadContentStream(Document document)
            throws AlfrescoServiceException
    {
        try
        {
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            org.alfresco.mobile.android.api.model.ContentStream cf = new org.alfresco.mobile.android.api.model.ContentStream(
                    objectService.getContentStream(session.getRepositoryInfo().getIdentifier(),
                            document.getIdentifier(), null, null, null, null));
            if (cf.getLength() == -1) { return new org.alfresco.mobile.android.api.model.ContentStream(
                    cf.getInputStream(), cf.getMimeType(), document.getContentStreamLength()); }
            return cf;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    public org.alfresco.mobile.android.api.model.ContentStream downloadContentStream(String identifier)
            throws AlfrescoServiceException
    {
        try
        {
            Document doc = (Document) getChildById(identifier);
            return downloadContentStream(doc);
            
            /*ObjectService objectService = cmisSession.getBinding().getObjectService();
            org.alfresco.mobile.android.api.model.ContentStream cf = new org.alfresco.mobile.android.api.model.ContentStream(
                    objectService.getContentStream(session.getRepositoryInfo().getIdentifier(),
                            identifier, null, null, null, null));
            if (cf.getLength() == -1) { return new org.alfresco.mobile.android.api.model.ContentStream(
                    cf.getInputStream(), cf.getMimeType(), -1); }*/
            //return cf;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Retrieves the downloading url for the given document.
     * 
     * @param document
     * @return
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public String getDownloadUrl(Document document) throws AlfrescoServiceException
    {
        try
        {
            AbstractAtomPubService objectService = (AbstractAtomPubService) cmisSession.getBinding().getObjectService();
            return objectService.loadLink(session.getRepositoryInfo().getIdentifier(), document.getIdentifier(),
                    AtomPubParser.LINK_REL_CONTENT, null);
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // RENDITION
    // ///////////////////////////////////////////////////////////////////////////////
    /**
     * Retrieve a specific type of Rendition for the specified identifier.
     * 
     * @param identifier : Node (Document in general) Identifier
     * @param type : Type of rendition available
     * @return Inputstream wrap inside a contentfile object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public org.alfresco.mobile.android.api.model.ContentStream getRenditionStream(String identifier, String type)
            throws AlfrescoServiceException
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getThumbnailsUrl(session, identifier, type));
            url.addParameter("format", "json");
            HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());
            org.alfresco.mobile.android.api.model.ContentStream cf;
            if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
                cf = null;
            else if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                convertStatusCode(resp);
                cf = null;
            }
            else
                cf = new org.alfresco.mobile.android.api.model.ContentStream(resp.getStream(),
                        resp.getContentTypeHeader() + ";" + resp.getCharset(), resp.getContentLength().longValue());
            return cf;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

    public org.alfresco.mobile.android.api.model.ContentStream getRenditionStream(Node node, String type)
            throws AlfrescoServiceException
    {
        return getRenditionStream(node.getIdentifier(), type);
    }

    /**
     * Retrieve a specific type of Rendition for the specified node.
     * 
     * @param type : Type of rendition available
     * @return Inputstream wrap inside a contentfile object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public ContentFile getRendition(Node node, String type) throws AlfrescoServiceException
    {
        return saveContentStream(getRenditionStream(node.getIdentifier(), type), node.getIdentifier());
    }

    // ////////////////////////////////////////////////////
    // PERMISSIONS
    // ////////////////////////////////////////////////////
    /**
     * Returns all permissions the user have to this node. It includes default
     * CMIS permission (create, update, delete) + Alfresco specficic permission
     * (comment, like...)
     * 
     * @param identifier : Node Identifier
     * @return {@link org.alfresco.mobile.android.api.model.Permissions} object
     *         that contains permissions.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public Permissions getPermissions(Node node)
    {
        return new PermissionsImpl(node);
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INTERNAL UTILS
    // ///////////////////////////////////////////////////////////////////////////////
    /**
     * Internal method to refresh a Node.
     * 
     * @param identifier : Unique identifier to a node object.
     * @return the refresh version of a node object.
     */
    private Node getChildById(String identifier)
    {
        Node result = null;

        /*OperationContext context = cmisSession.getDefaultContext();
        ObjectService objectService = cmisSession.getBinding().getObjectService();
        ObjectFactory objectFactory = cmisSession.getObjectFactory();

        // get the object
        ObjectData objectData = objectService.getObject(session.getRepositoryInfo().getIdentifier(), identifier,
                context.getFilterString(), context.isIncludeAllowableActions(), context.getIncludeRelationships(),
                context.getRenditionFilterString(), context.isIncludePolicies(), context.isIncludeAcls(), null);

        result = convertNode(objectFactory.convertObject(objectData, context));*/
        result = convertNode(cmisSession.getObject(identifier));

        return result;
    }

    @SuppressWarnings("serial")
    private static HashMap<String, String> sortingMap = new HashMap<String, String>(6)
    {
        {
            put(Sorting.NAME, PropertyIds.NAME);
            put(Sorting.CREATION_DATE, PropertyIds.CREATION_DATE);
            put(Sorting.MODIFICATION_DATE, PropertyIds.LAST_MODIFICATION_DATE);
            put(Sorting.TYPE, PropertyIds.BASE_TYPE_ID);
            put(Sorting.SIZE, PropertyIds.CONTENT_STREAM_LENGTH);
            put(Sorting.AUTHOR, PropertyIds.CREATED_BY);
        }
    };

    private String getSorting(String sortingKey, boolean modifier)
    {
        String s;
        if (sortingMap.containsKey(sortingKey))
            s = sortingMap.get(sortingKey);
        else
            return null;
        // s = PropertyIds.NAME;

        if (modifier)
            s += " ASC";
        else
            s += " DESC";

        return s;
    }

    /**
     * Utility method to convert Alfresco Content Model Properties ID into CMIS
     * properties ID.
     * 
     * @param properties : A possible mix of cmis and content model properties
     *            id.
     * @param typeId : Type Id of the node.
     */
    private static void convertProps(Map<String, Serializable> properties, String typeId)
    {
        // Transform Alfresco properties to cmis properties
        for (Entry<String, String> props : CMISMapping.ALFRESCO_TO_CMIS.entrySet())
        {
            if (properties.containsKey(props.getKey()))
            {
                properties.put(props.getValue(), properties.get(props.getKey()));
                properties.remove(props.getKey());
            }
        }

        // Take ObjectId provided in map or the default one provided by the
        // method
        String objectId = null;
        if (properties.containsKey(PropertyIds.OBJECT_TYPE_ID))
            objectId = (String) properties.get(PropertyIds.OBJECT_TYPE_ID);
        else
            objectId = typeId;

        // add aspects flags to objectId
        StringBuffer buf = new StringBuffer(objectId);
        for (Entry<String, String> props : CMISMapping.ALFRESCO_ASPECTS.entrySet())
        {
            if (properties.containsKey(props.getKey()) && !objectId.contains(props.getValue()))
            {
                buf.append(",");
                buf.append(props.getValue());
            }
        }

        properties.put(PropertyIds.OBJECT_TYPE_ID, buf.toString());
    }
}
