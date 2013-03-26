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

import java.io.IOException;
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

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Permissions;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.PermissionsImpl;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.ObjectType;
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
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.commons.spi.NavigationService;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;
import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * Abstract class implementation of DocumentFolderService. Responsible of
 * sharing common methods between child class (OnPremise and Cloud)
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractDocumentFolderServiceImpl extends AlfrescoService implements DocumentFolderService
{
    /** Internal Tag for Logger. */
    private static final String TAG = "DocumentFolderService";

    protected Session cmisSession;

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractDocumentFolderServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
        this.cmisSession = ((AbstractAlfrescoSessionImpl) repositorySession).getCmisSession();
    }

    // ////////////////////////////////////////////////////
    // NAVIGATION
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public List<Node> getChildren(Folder parentFolder)
    {
        return getChildren(parentFolder, null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Node> getChildren(Folder parentFolder, ListingContext lcontext)
    {

        if (isObjectNull(parentFolder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "parentFolder")); }

        try
        {
            NavigationService navigationService = cmisSession.getBinding().getNavigationService();
            OperationContext ctxt = new OperationContextImpl(cmisSession.getDefaultContext());
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            // By default Listing context has default value
            String orderBy = getSorting(SORT_PROPERTY_NAME, true);
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

            if (childObjects != null)
            {
                ////Log.d(TAG, "childObjects : " + childObjects.size());
                for (ObjectInFolderData objectData : childObjects)
                {
                    if (objectData.getObject() != null)
                    {
                        Node n = convertNode(objectFactory.convertObject(objectData.getObject(), ctxt));
                        page.add(n);
                    }
                }
            }

            Boolean hasMoreItem = false;
            if (maxItems != null)
            {
                hasMoreItem = children.hasMoreItems() && page.size() == maxItems.intValue();
            }
            else
            {
                hasMoreItem = children.hasMoreItems();
            }

            return new PagingResultImpl<Node>(page, hasMoreItem, children.getNumItems().intValue());
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Node getChildByPath(String path)
    {
        return getChildByPath(getRootFolder(), path);
    }

    /** {@inheritDoc} */
    public Node getChildByPath(Folder folder, String relativePathFromFolder)
    {
        if (isObjectNull(folder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "parentFolder")); }

        if (isStringNull(relativePathFromFolder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "relativePathFromFolder")); }

        try
        {
            String path = folder.getPropertyValue(PropertyIds.PATH);
            if (path.equals("/"))
            {
                path = "";
            }

            String tmpPath = relativePathFromFolder;
            if (tmpPath.length() > 1 && tmpPath.endsWith("/"))
            {
                tmpPath = tmpPath.substring(0, tmpPath.length() - 1);
            }

            if (tmpPath.length() > 1 && !tmpPath.startsWith("/"))
            {
                tmpPath = "/" + tmpPath;
            }

            path = path.concat(tmpPath);

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
        catch (CmisObjectNotFoundException e)
        {
            return null;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Node getNodeByIdentifier(String identifier)
    {
        if (isStringNull(identifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "nodeIdentifier")); }

        try
        {
            return getChildById(identifier);
        }
        catch (CmisInvalidArgumentException e)
        {
            // Case OnPremise : Node Not found (Object id is invalid:)
            throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_NODE_NOT_FOUND, e);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Folder getRootFolder()
    {
        return session.getRootFolder();
    }

    /** {@inheritDoc} */
    public List<Folder> getFolders(Folder parentFolder)
    {
        return getFolders(parentFolder, null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Folder> getFolders(Folder folder, ListingContext listingContext)
    {
        if (isObjectNull(folder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "parentFolder")); }

        try
        {
            PagingResult<Node> nodes = getChildren(folder, listingContext);
            List<Folder> folders = new ArrayList<Folder>(nodes.getList().size());
            for (Node node : nodes.getList())
            {
                if (node.isFolder())
                {
                    folders.add((Folder) node);
                }
            }
            return new PagingResultImpl<Folder>(folders, nodes.hasMoreItems(), -1);
        }
        catch (CmisObjectNotFoundException e)
        {
            return null;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<Document> getDocuments(Folder folder)
    {
        return getDocuments(folder, null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Document> getDocuments(Folder folder, ListingContext listingContext)
    {
        if (isObjectNull(folder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "parentFolder")); }
        try
        {

            PagingResult<Node> nodes = getChildren(folder, listingContext);
            List<Document> docs = new ArrayList<Document>(nodes.getList().size());
            for (Node node : nodes.getList())
            {
                if (node.isDocument())
                {
                    docs.add((Document) node);
                }
            }
            return new PagingResultImpl<Document>(docs, nodes.hasMoreItems(), -1);
        }
        catch (CmisObjectNotFoundException e)
        {
            return null;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Folder getParentFolder(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
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
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // CREATION
    // ////////////////////////////////////////////////////
    /** Internal map for OpenCMIS. */
    private static final Set<Updatability> CREATE_UPDATABILITY = new HashSet<Updatability>();
    static
    {
        CREATE_UPDATABILITY.add(Updatability.ONCREATE);
        CREATE_UPDATABILITY.add(Updatability.READWRITE);
    }

    /** {@inheritDoc} */
    public Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties)
    {
        return createFolder(parentFolder, folderName, properties, null, null);
    }

    /** {@inheritDoc} */
    public Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties,
            List<String> aspects)
    {
        return createFolder(parentFolder, folderName, properties, aspects, null);
    }

    /** {@inheritDoc} */
    public Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties,
            List<String> aspects, String type)
    {
        if (isObjectNull(parentFolder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "parentFolder")); }

        if (isStringNull(folderName)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "folderName")); }

        try
        {
            Node n = null;
            Map<String, Serializable> tmpProperties = new HashMap<String, Serializable>();
            if (properties != null)
            {
                tmpProperties.putAll(properties);
            }
            tmpProperties.put(ContentModel.PROP_NAME, folderName);

            if (!isStringNull(type))
            {
                tmpProperties.put(PropertyIds.OBJECT_TYPE_ID, CMISPREFIX_FOLDER + type);
            }

            tmpProperties = convertProps(tmpProperties, BaseTypeId.CMIS_FOLDER.value());

            if (!isListNull(aspects))
            {
                tmpProperties.put(PropertyIds.OBJECT_TYPE_ID,
                        addAspects((String) tmpProperties.get(PropertyIds.OBJECT_TYPE_ID), aspects));
            }

            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            String newId = objectService.createFolder(session.getRepositoryInfo().getIdentifier(),
                    objectFactory.convertProperties(tmpProperties, null, CREATE_UPDATABILITY),
                    parentFolder.getIdentifier(), null, null, null, null);

            if (newId == null) { return null; }

            n = getChildById(newId);

            if (!(n instanceof Folder)) { throw new AlfrescoServiceException(
                    ErrorCodeRegistry.DOCFOLDER_WRONG_NODE_TYPE, Messagesl18n.getString("DocumentFolderService.19")
                            + newId + " : " + n.getType() + " " + n.getName()); }
            return (Folder) n;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile contentFile)
    {
        return createDocument(parentFolder, documentName, properties, contentFile, null, null);
    }

    /** {@inheritDoc} */
    public Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile contentFile, List<String> aspects)
    {
        return createDocument(parentFolder, documentName, properties, contentFile, aspects, null);
    }

    /** {@inheritDoc} */
    public Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile contentFile, List<String> aspects, String type)
    {
        if (isObjectNull(parentFolder)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "parentFolder")); }

        if (isStringNull(documentName)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "documentName")); }

        try
        {
            Map<String, Serializable> tmpProperties = new HashMap<String, Serializable>();
            if (properties != null)
            {
                tmpProperties.putAll(properties);
            }

            tmpProperties.put(ContentModel.PROP_NAME, documentName);

            if (!isStringNull(type))
            {
                tmpProperties.put(PropertyIds.OBJECT_TYPE_ID, CMISPREFIX_DOCUMENT + type);
            }

            tmpProperties = convertProps(tmpProperties, BaseTypeId.CMIS_DOCUMENT.value());

            if (!isListNull(aspects))
            {
                tmpProperties.put(PropertyIds.OBJECT_TYPE_ID,
                        addAspects((String) tmpProperties.get(PropertyIds.OBJECT_TYPE_ID), aspects));
            }

            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            ContentStream c = null;
            if (contentFile != null)
            {
                c = objectFactory.createContentStream(documentName, contentFile.getLength(), contentFile.getMimeType(),
                        IOUtils.getContentFileInputStream(contentFile));
            }

            String newId = objectService.createDocument(session.getRepositoryInfo().getIdentifier(),
                    objectFactory.convertProperties(tmpProperties, null, CREATE_UPDATABILITY),
                    parentFolder.getIdentifier(), c, VersioningState.MAJOR, null, null, null, null);

            // EXTRACT METADATA + Generate Thumbnails
            if (RepositoryVersionHelper.isAlfrescoProduct(session))
            {
                if (session.getParameter(AlfrescoSession.EXTRACT_METADATA) != null
                        && (Boolean) session.getParameter(AlfrescoSession.EXTRACT_METADATA))
                {
                    extractMetadata(newId);
                }
                if (session.getParameter(AlfrescoSession.CREATE_THUMBNAIL) != null
                        && (Boolean) session.getParameter(AlfrescoSession.CREATE_THUMBNAIL))
                {
                    generateThumbnail(newId);
                }
            }

            if (newId == null) { return null; }

            Node n = getChildById(newId);

            if (!(n instanceof Document)) { throw new AlfrescoServiceException(
                    ErrorCodeRegistry.DOCFOLDER_WRONG_NODE_TYPE, Messagesl18n.getString("DocumentFolderService.20")
                            + newId); }
            return (Document) n;
        }
        catch (Exception e)
        {
            //Log.d(TAG, Log.getStackTraceString(e));
            convertException(e);
        }
        return null;
    }

    /**
     * Force metadata extraction for a specific node identifier.
     * 
     * @param identifier : unique identifier of a node (Document) @ : if network
     *            or internal problems occur during the process.
     */
    private void extractMetadata(String identifier)
    {

        if (isStringNull(identifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "Nodeidentifier")); }

        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getActionQueue(session));
            url.addParameter(OnPremiseConstant.PARAM_ASYNC, true);
            //Log.d("URL", url.toString());

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.ACTIONEDUPONNODE_VALUE, NodeRefUtils.getCleanIdentifier(identifier));
            jo.put(OnPremiseConstant.ACTIONDEFINITIONNAME_VALUE, OnPremiseConstant.ACTION_EXTRACTMETADATA_VALUE);

            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            Response response = post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formData.write(out);
                }
            }, ErrorCodeRegistry.DOCFOLDER_GENERIC);

            if (response.getResponseCode() == HttpStatus.SC_OK)
            {
                //Log.d(TAG, "Metadata extraction : ok");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Metadata extraction : KO");
        }
    }

    /**
     * Force creation of the doclib thumbnail.
     * 
     * @param identifier : unique identifier of a node (Document) @ : if network
     *            or internal problems occur during the process.
     */
    private void generateThumbnail(String identifier)
    {
        if (isStringNull(identifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "Nodeidentifier")); }

        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getThumbnailUrl(session, identifier));
            url.addParameter(OnPremiseConstant.PARAM_AS, true);
            //Log.d("URL", url.toString());

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.THUMBNAILNAME_VALUE, RENDITION_THUMBNAIL);

            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formData.write(out);
                }
            }, ErrorCodeRegistry.DOCFOLDER_GENERIC);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Generate Thumbnail : KO");
        }
    }

    // ////////////////////////////////////////////////////
    // DELETE
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public void deleteNode(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        try
        {
            if (node.isDocument() && node instanceof Document)
            {
                delete((Document) node);
            }
            else if (node.isFolder() && node instanceof Folder)
            {
                delete((Folder) node);
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /** {@inheritDoc} */
    private void delete(Document document)
    {
        Permissions perm = getPermissions(document);
        if (!perm.canDelete()) { throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_ACCESS_DENIED,
                Messagesl18n.getString("ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION")); }

        try
        {
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            objectService.deleteObject(session.getRepositoryInfo().getIdentifier(), document.getIdentifier(), true,
                    null);
            cmisSession.removeObjectFromCache(document.getIdentifier());
        }
        catch (CmisConstraintException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    private void delete(Folder folder)
    {
        Permissions perm = getPermissions(folder);
        if (!perm.canDelete()) { throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_ACCESS_DENIED,
                Messagesl18n.getString("DocumentFolderService.24")); }
        try
        {
            ObjectService objectService = cmisSession.getBinding().getObjectService();
            objectService.deleteTree(session.getRepositoryInfo().getIdentifier(), folder.getIdentifier(), true, null,
                    false, null);
        }
        catch (CmisConstraintException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    // ////////////////////////////////////////////////////
    // UPDATE
    // ////////////////////////////////////////////////////

    /** {@inheritDoc} */
    public Node updateProperties(Node node, Map<String, Serializable> properties)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        if (isMapNull(properties)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "properties")); }

        try
        {
            Map<String, Serializable> tmpProperties = convertProps(properties, node.getType());

            // Check Custom type
            if (!ContentModel.TYPE_CONTENT.equals(node.getType()) && !ContentModel.TYPE_FOLDER.equals(node.getType()))
            {
                String objectBaseTypeId = node.getProperty(PropertyIds.BASE_TYPE_ID).getValue();
                if (ObjectType.DOCUMENT_BASETYPE_ID.equals(objectBaseTypeId))
                {
                    tmpProperties.put(PropertyIds.OBJECT_TYPE_ID, CMISPREFIX_DOCUMENT + node.getType());
                }
                else if (ObjectType.FOLDER_BASETYPE_ID.equals(objectBaseTypeId))
                {
                    tmpProperties.put(PropertyIds.OBJECT_TYPE_ID, CMISPREFIX_FOLDER + node.getType());
                }
            }

            // Check Custom Aspects
            tmpProperties.put(PropertyIds.OBJECT_TYPE_ID,
                    addAspects((String) tmpProperties.get(PropertyIds.OBJECT_TYPE_ID), node.getAspects()));

            ObjectService objectService = cmisSession.getBinding().getObjectService();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            String objectId = node.getIdentifier();
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

            String nodeType = node.getProperty(PropertyIds.OBJECT_TYPE_ID).getValue().toString();
            // tmpProperties.put(PropertyIds.OBJECT_TYPE_ID, nodeType);

            // it's time to update
            objectService.updateProperties(session.getRepositoryInfo().getIdentifier(), objectIdHolder,
                    changeTokenHolder, objectFactory.convertProperties(tmpProperties,
                            cmisSession.getTypeDefinition(nodeType), updatebility), null);

            return getChildById(objectId);
        }
        catch (CmisRuntimeException e)
        {
            // Alfresco 3.4 : In case where a null value is provided (definition
            // type property
            // is not null)
            if (e.getErrorContent() != null && e.getErrorContent().contains("cannot be null or empty."))
            {
                throw new IllegalArgumentException(e);
            }
            else
            {
                convertException(e);
            }
        }
        catch (Exception e)
        {
            //Log.d(TAG, Log.getStackTraceString(e));
            // In case where a null value is provided (definition type property
            // is not null)
            if (e.getMessage() != null && e.getMessage().contains("cannot be null or empty."))
            {
                throw new IllegalArgumentException(e);
            }
            else
            {
                convertException(e);
            }
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // CONTENT
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public Document updateContent(Document content, ContentFile contentFile)
    {
        if (isObjectNull(content)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

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
        catch (Exception e)
        {
            convertException(e);
        }

        return newContent;
    }

    /** {@inheritDoc} */
    @Override
    public ContentFile getContent(Document document)
    {
        if (isObjectNull(document)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "document")); }

        try
        {
            return saveContentStream(getContentStream(document),
                    NodeRefUtils.getNodeIdentifier(document.getIdentifier()), CONTENT_CACHE);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public org.alfresco.mobile.android.api.model.ContentStream getContentStream(Document document)
    {
        if (isObjectNull(document)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "document")); }

        try
        {
            if (document.getContentStreamLength() <= 0) { return null; }

            ObjectService objectService = cmisSession.getBinding().getObjectService();
            org.alfresco.mobile.android.api.model.ContentStream cf = new ContentStreamImpl(document.getName(),
                    objectService.getContentStream(session.getRepositoryInfo().getIdentifier(),
                            document.getIdentifier(), null, null, null, null));
            if (cf.getLength() == -1) { return new ContentStreamImpl(document.getName(), cf.getInputStream(),
                    cf.getMimeType(), document.getContentStreamLength()); }
            return cf;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public org.alfresco.mobile.android.api.model.ContentStream downloadContentStream(String identifier)
    {
        try
        {
            Document doc = (Document) getChildById(identifier);
            return getContentStream(doc);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Internal : Retrieves the downloading url for the given document.
     * 
     * @param document
     * @return @ : if network or internal problems occur during the process.
     */
    public String getDownloadUrl(Document document)
    {
        if (isObjectNull(document)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "document")); }

        try
        {
            AbstractAtomPubService objectService = (AbstractAtomPubService) cmisSession.getBinding().getObjectService();
            return objectService.loadLink(session.getRepositoryInfo().getIdentifier(), document.getIdentifier(),
                    AtomPubParser.LINK_REL_CONTENT, null);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // RENDITION
    // ///////////////////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */

    public abstract org.alfresco.mobile.android.api.model.ContentStream getRenditionStream(String identifier,
            String type);

    /** {@inheritDoc} */
    public org.alfresco.mobile.android.api.model.ContentStream getRenditionStream(Node node, String type)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        return getRenditionStream(node.getIdentifier(), type);
    }

    /** {@inheritDoc} */
    public ContentFile getRendition(Node node, String type)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        return saveContentStream(getRenditionStream(node.getIdentifier(), type),
                NodeRefUtils.getNodeIdentifier(node.getIdentifier()), RENDITION_CACHE);
    }

    // ////////////////////////////////////////////////////
    // PERMISSIONS
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public Permissions getPermissions(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

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

        result = convertNode(cmisSession.getObject(identifier));

        return result;
    }

    /** Static Map of all sorting possibility for DocumentFolderService. */
    @SuppressWarnings("serial")
    private static Map<String, String> sortingMap = new HashMap<String, String>()
    {
        {
            put(SORT_PROPERTY_NAME, PropertyIds.NAME);
            put(SORT_PROPERTY_TITLE, SORT_PROPERTY_TITLE);
            put(SORT_PROPERTY_DESCRIPTION, SORT_PROPERTY_DESCRIPTION);
            put(SORT_PROPERTY_CREATED_AT, PropertyIds.CREATION_DATE);
            put(SORT_PROPERTY_MODIFIED_AT, PropertyIds.LAST_MODIFICATION_DATE);
        }
    };

    /**
     * Utility method to create the sorting open cmis extension.
     * 
     * @param sortingKey
     * @param modifier
     * @return
     */
    private String getSorting(String sortingKey, boolean modifier)
    {
        String s;
        if (sortingMap.containsKey(sortingKey))
        {
            s = sortingMap.get(sortingKey);
        }
        else
        {
            s = sortingMap.get(SORT_PROPERTY_NAME);
        }

        if (modifier)
        {
            s += " ASC";
        }
        else
        {
            s += " DESC";
        }

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
    private static Map<String, Serializable> convertProps(Map<String, Serializable> properties, String typeId)
    {

        Map<String, Serializable> tmpProperties = new HashMap<String, Serializable>();
        if (properties != null)
        {
            tmpProperties.putAll(properties);
        }

        // Transform Alfresco properties to cmis properties
        for (Entry<String, String> props : ALFRESCO_TO_CMIS.entrySet())
        {
            if (tmpProperties.containsKey(props.getKey()))
            {
                tmpProperties.put(props.getValue(), tmpProperties.get(props.getKey()));
                tmpProperties.remove(props.getKey());
            }
        }

        // Take ObjectId provided in map or the default one provided by the
        // method
        String objectId = null;
        if (tmpProperties.containsKey(PropertyIds.OBJECT_TYPE_ID))
        {
            objectId = (String) tmpProperties.get(PropertyIds.OBJECT_TYPE_ID);
        }
        else
        {
            if (ContentModel.TYPE_CONTENT.equals(typeId))
            {
                objectId = ObjectType.DOCUMENT_BASETYPE_ID;
            }
            else if (ContentModel.TYPE_FOLDER.equals(typeId))
            {
                objectId = ObjectType.FOLDER_BASETYPE_ID;
            }
            else
            {
                objectId = typeId;
            }
        }

        // add aspects flags to objectId
        for (Entry<String, String> props : ALFRESCO_ASPECTS.entrySet())
        {
            if (tmpProperties.containsKey(props.getKey()) && !objectId.contains(props.getValue()))
            {
                objectId = objectId.concat("," + props.getValue());
            }
        }

        //Log.d(TAG, objectId);

        tmpProperties.put(PropertyIds.OBJECT_TYPE_ID, objectId);

        return tmpProperties;
    }

    private static String addAspects(String objectId, List<String> aspects)
    {
        String objectIdWithAspects = objectId;
        for (String aspect : aspects)
        {
            if (!objectIdWithAspects.contains(aspect))
            {
                objectIdWithAspects = objectIdWithAspects.concat("," + CMISPREFIX_ASPECTS + aspect);
            }
        }

        return objectIdWithAspects;
    }

    // ////////////////////////////////////////////////////////////////
    // Manage mapping between Alfresco ContentModel and CMIS Property
    // ///////////////////////////////////////////////////////////////

    /** Alfresco OpenCMIS extension prefix for all aspects. */
    public static final String CMISPREFIX_ASPECTS = "P:";

    public static final String CMISPREFIX_DOCUMENT = "D:";

    public static final String CMISPREFIX_FOLDER = "F:";

    /** All CMIS properties identifier in one list. */
    private static final Set<String> CMISMODEL_KEYS = new HashSet<String>();
    static
    {
        CMISMODEL_KEYS.add(PropertyIds.NAME);
        CMISMODEL_KEYS.add(PropertyIds.OBJECT_ID);
        CMISMODEL_KEYS.add(PropertyIds.OBJECT_TYPE_ID);
        CMISMODEL_KEYS.add(PropertyIds.BASE_TYPE_ID);
        CMISMODEL_KEYS.add(PropertyIds.CREATED_BY);
        CMISMODEL_KEYS.add(PropertyIds.CREATION_DATE);
        CMISMODEL_KEYS.add(PropertyIds.LAST_MODIFIED_BY);
        CMISMODEL_KEYS.add(PropertyIds.LAST_MODIFICATION_DATE);
        CMISMODEL_KEYS.add(PropertyIds.CHANGE_TOKEN);
        CMISMODEL_KEYS.add(PropertyIds.IS_IMMUTABLE);
        CMISMODEL_KEYS.add(PropertyIds.IS_LATEST_VERSION);
        CMISMODEL_KEYS.add(PropertyIds.IS_MAJOR_VERSION);
        CMISMODEL_KEYS.add(PropertyIds.IS_LATEST_MAJOR_VERSION);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_LABEL);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_SERIES_ID);
        CMISMODEL_KEYS.add(PropertyIds.IS_VERSION_SERIES_CHECKED_OUT);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_SERIES_CHECKED_OUT_BY);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_SERIES_CHECKED_OUT_ID);
        CMISMODEL_KEYS.add(PropertyIds.CHECKIN_COMMENT);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_LENGTH);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_MIME_TYPE);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_FILE_NAME);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_ID);
        CMISMODEL_KEYS.add(PropertyIds.PARENT_ID);
        CMISMODEL_KEYS.add(PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS);
        CMISMODEL_KEYS.add(PropertyIds.SOURCE_ID);
        CMISMODEL_KEYS.add(PropertyIds.TARGET_ID);
        CMISMODEL_KEYS.add(PropertyIds.POLICY_TEXT);
    }

    /**
     * List of Properties Mapping CMIS to Alfresco
     */
    private static final Map<String, String> ALFRESCO_TO_CMIS = new HashMap<String, String>();
    static
    {
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_NAME, PropertyIds.NAME);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_CREATED, PropertyIds.CREATION_DATE);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_CREATOR, PropertyIds.CREATED_BY);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_MODIFIED, PropertyIds.LAST_MODIFICATION_DATE);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_MODIFIER, PropertyIds.LAST_MODIFIED_BY);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_VERSION_LABEL, PropertyIds.VERSION_LABEL);
    }

    /**
     * List of all aspect that are currently supported by SDK services.
     */
    private static final Map<String, String> ALFRESCO_ASPECTS = new HashMap<String, String>();
    static
    {

        // TITLE
        ALFRESCO_ASPECTS.put(ContentModel.PROP_TITLE, CMISPREFIX_ASPECTS + ContentModel.ASPECT_TITLED);
        ALFRESCO_ASPECTS.put(ContentModel.PROP_DESCRIPTION, CMISPREFIX_ASPECTS + ContentModel.ASPECT_TITLED);

        // TAGS
        ALFRESCO_ASPECTS.put(ContentModel.PROP_TAGS, CMISPREFIX_ASPECTS + ContentModel.ASPECT_TAGGABLE);

        // GEOGRAPHIC
        for (String prop : ContentModel.ASPECT_GEOGRAPHIC_PROPS)
        {
            ALFRESCO_ASPECTS.put(prop, CMISPREFIX_ASPECTS + ContentModel.ASPECT_GEOGRAPHIC);
        }

        // EXIF
        for (String prop : ContentModel.ASPECT_EXIF_PROPS)
        {
            ALFRESCO_ASPECTS.put(prop, CMISPREFIX_ASPECTS + ContentModel.ASPECT_EXIF);
        }

        // AUDIO
        for (String prop : ContentModel.ASPECT_AUDIO_PROPS)
        {
            ALFRESCO_ASPECTS.put(prop, CMISPREFIX_ASPECTS + ContentModel.ASPECT_AUDIO);
        }

        // AUTHOR
        ALFRESCO_ASPECTS.put(ContentModel.PROP_AUTHOR, CMISPREFIX_ASPECTS + ContentModel.ASPECT_AUTHOR);

    }

    /**
     * Return the equivalent CMIS property name for a specific alfresco property
     * name.
     * 
     * @param name name of the property.
     * @return the same if equivalent doesnt exist.
     */
    public static String getPropertyName(String name)
    {
        String tmpName = name;
        if (ALFRESCO_TO_CMIS.containsKey(tmpName))
        {
            tmpName = ALFRESCO_TO_CMIS.get(tmpName);
        }
        return tmpName;
    }
}
