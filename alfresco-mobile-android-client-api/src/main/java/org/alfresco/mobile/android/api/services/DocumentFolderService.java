/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.*;

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
public interface DocumentFolderService extends Service
{

    /**
     * Allowable sorting property : Name of the document or folder.
     */
    String SORT_PROPERTY_NAME = ContentModel.PROP_NAME;

    /**
     * Allowable sorting property : Title of the document or folder.
     */
    String SORT_PROPERTY_TITLE = ContentModel.PROP_TITLE;

    /**
     * Allowable sorting property : Description
     */
    String SORT_PROPERTY_DESCRIPTION = ContentModel.PROP_DESCRIPTION;

    /**
     * Allowable sorting property : Creation Date
     */
    String SORT_PROPERTY_CREATED_AT = ContentModel.PROP_CREATED;

    /**
     * Allowable sorting property : Modification Date
     */
    String SORT_PROPERTY_MODIFIED_AT = ContentModel.PROP_MODIFIED;

    // ////////////////////////////////////////////////////////////////
    // FILTERS
    // ////////////////////////////////////////////////////////////////
    /**
     * Filter to include links object in listing.
     */
    String FILTER_INCLUDE_LINKS = "filterIncludeLinks";

    /**
     * Lists all immediate child nodes of the given context folder. </br> By
     * default, this list contains a maximum of 50 elements. </br> Use
     * {@link #getChildren(Folder, ListingContext)} to change this behaviour.
     * 
     * @param folder : context folder
     * @return Returns a list of the immediate child nodes of the given folder.
     * @see #getChildren(Folder, ListingContext)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Node> getChildren(Folder folder);

    /**
     * Lists immediate child nodes of the given context folder.
     * 
     * @param folder : context folder
     * @param listingContext : Listing context that define the behaviour of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of the immediate child nodes of the given
     *         folder.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Node> getChildren(Folder folder, ListingContext listingContext);

    /**
     * Gets the node object stored at the specified path.
     * 
     * @param path : path from the root folder.
     * @return Returns the node object stored at the specified path.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Node getChildByPath(String path);

    /**
     * Gets the node object stored at the relative specified path from the
     * folder object.
     * 
     * @param folder : context folder
     * @param relativePath : relative path from the root folder.
     * @return Returns the node object stored at the given path relative from
     *         the given folder.
     */
    Node getChildByPath(Folder folder, String relativePath);

    /**
     * @param identifier
     * @return Returns the node object with the specified identifier.
     */
    Node getNodeByIdentifier(String identifier);

    /**
     * Lists all immediate child documents of the given context node </br>Note:
     * this could be a long list
     * 
     * @param folder : context folder
     * @return Returns a list of all immediate child documents of the given
     *         folder.
     * @see #getDocuments(Folder, ListingContext)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Document> getDocuments(Folder folder);

    /**
     * Lists all immediate child documents of the given context folder.
     * 
     * @param folder : context folder
     * @param listingContext : defines the behavior of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of all immediate child documents of the
     *         given folder.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Document> getDocuments(Folder folder, ListingContext listingContext);

    /**
     * Lists all immediate child folders of the given context folder.
     * 
     * @param folder : Parent Folder
     * @return Returns a list of all immediate child folders of the given
     *         folder.
     * @see #getFolders(Folder, ListingContext)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Folder> getFolders(Folder folder);

    /**
     * Lists all immediate child folders of the given context folder.
     * 
     * @param folder : Parent Folder
     * @param listingContext : : defines the behaviour of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of all immediate child folders of the given
     *         folder.
     * @see #getFolders(Folder)
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Folder> getFolders(Folder folder, ListingContext listingContext);

    /**
     * @return Returns the root folder of the repository.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Folder getRootFolder();

    /**
     * Gets the direct parent folder object.
     * 
     * @param node : Node object (Folder or Document).
     * @return Returns the parent folder object of the given node, null if the
     *         node does not have a parent.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Folder getParentFolder(Node node);

    /**
     * Creates a folder object in the specified location with an optional set of
     * properties and aspects. <br/>
     * </p> <b>Properties</b> keys must respect the format :
     * namespace_prefix:property_name like cm:name / cm:description<br/>
     * </p> A collection of default Alfresco aspects and properties are
     * available inside the
     * {@link org.alfresco.mobile.android.api.constants.ContentModel
     * ContentModel}</p>
     * 
     * @param folder : Parent Folder
     * @param folderName : Name of the future folder
     * @param properties : Map of properties to apply to the new folder
     * @return the newly created folder
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties);

    /**
     * Creates a folder object in the specified location with an optional set of
     * properties and aspects. <br/>
     * </p> <b>Properties</b> keys must respect the format :
     * namespace_prefix:property_name like cm:name / cm:description<br/>
     * <b>Aspects</b> must be a list of String that respect the format :
     * namespace_name:aspect_name like cm:titled / cm:geographic. <br/>
     * </p> A collection of default Alfresco aspects and properties are
     * available inside the
     * {@link org.alfresco.mobile.android.api.constants.ContentModel
     * ContentModel}</p>
     * 
     * @param folder : Parent Folder
     * @param folderName : Name of the future folder
     * @param properties : Map of properties to apply to the new folder
     * @param aspects : List of aspects to apply to the new folder
     * @return the newly created folder
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties,
            List<String> aspects);

    /**
     * Creates a folder object in the specified location with an optional set of
     * properties and aspects. <br/>
     * </p> <b>Properties</b> keys must respect the format :
     * namespace_prefix:property_name like cm:name / cm:description<br/>
     * <b>Aspects</b> must be a list of String that respect the format :
     * namespace_name:aspect_name like cm:titled / cm:geographic. <br/>
     * <b>Type</b> value must respect the format :
     * namespace_prefix:property_name like my:customfolder / cm:folder<br/>
     * </p> A collection of default Alfresco aspects and properties are
     * available inside the
     * {@link org.alfresco.mobile.android.api.constants.ContentModel
     * ContentModel}</p>
     * 
     * @param folder : Parent Folder
     * @param folderName : Name of the future folder
     * @param properties : Map of properties to apply to the new folder
     * @param aspects : List of aspects to apply to the new folder
     * @param type : custom type of the folder. If null it creates a default
     *            cm:folder.
     * @return the newly created folder
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Folder createFolder(Folder parentFolder, String folderName, Map<String, Serializable> properties,
            List<String> aspects, String type);

    /**
     * Creates a document object in the specified location with an optional set
     * of properties. The content for the node is taken from the provided file.<br/>
     * If the file parameter is not provided the document is created without
     * content. <br/>
     * If the session is bind to an onPremise server and depending on
     * sessionSettings, this method can launch metadata extraction and thumbnail
     * generation after creation. </p> <b>Properties</b> keys must respect the
     * format : namespace_prefix:property_name like cm:name / cm:description<br/>
     * </p> A collection of default Alfresco aspects and properties are
     * available inside the
     * {@link org.alfresco.mobile.android.api.constants.ContentModel
     * ContentModel}</p>
     * 
     * @param folder: Future parent folder of a new document
     * @param documentName
     * @param properties : (Optional) list of property values that must be
     *            applied
     * @param file : (Optional) ContentFile that contains data stream or file
     * @return the newly created document object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile file);

    /**
     * Creates a document object in the specified location with an optional set
     * of properties. The content for the node is taken from the provided file.<br/>
     * If the file parameter is not provided the document is created without
     * content. <br/>
     * If the session is bind to an onPremise server and depending on
     * sessionSettings, this method can launch metadata extraction and thumbnail
     * generation after creation. </p> <b>Properties</b> keys must respect the
     * format : namespace_prefix:property_name like cm:name / cm:description<br/>
     * <b>Aspects</b> must be a list of String that respect the format :
     * namespace_name:aspect_name like cm:titled / cm:geographic. <br/>
     * </p> A collection of default Alfresco aspects and properties are
     * available inside the
     * {@link org.alfresco.mobile.android.api.constants.ContentModel
     * ContentModel}</p>
     * 
     * @param folder: Future parent folder of a new document
     * @param documentName
     * @param properties : (Optional) list of property values that must be
     *            applied
     * @param aspects : List of aspects to apply to the new folder
     * @param file : (Optional) ContentFile that contains data stream or file
     * @return the newly created document object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile file, List<String> aspects);

    /**
     * Creates a document object in the specified location with an optional set
     * of properties. The content for the node is taken from the provided file.<br/>
     * If the file parameter is not provided the document is created without
     * content. <br/>
     * If the session is bind to an onPremise server and depending on
     * sessionSettings, this method can launch metadata extraction and thumbnail
     * generation after creation. </p><b>Properties</b> keys must respect the
     * format : namespace_prefix:property_name like cm:name / cm:description<br/>
     * <b>Aspects</b> must be a list of String that respect the format :
     * namespace_name:aspect_name like cm:titled / cm:geographic. <br/>
     * <b>Type</b> value must respect the format :
     * namespace_prefix:property_name like my:customfolder / cm:folder<br/>
     * </p> A collection of default Alfresco aspects and properties are
     * available inside the
     * {@link org.alfresco.mobile.android.api.constants.ContentModel
     * ContentModel}</p>
     * 
     * @param folder: Future parent folder of a new document
     * @param documentName
     * @param properties : (Optional) list of property values that must be
     *            applied
     * @param aspects : List of aspects to apply to the new folder
     * @param type : custom type of the folder. If null it creates a default
     *            cm:folder.
     * @param file : (Optional) ContentFile that contains data stream or file
     * @return the newly created document object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Document createDocument(Folder parentFolder, String documentName, Map<String, Serializable> properties,
            ContentFile file, List<String> aspects, String type);

    /**
     * Deletes the specified node.
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    void deleteNode(Node node);

    /**
     * Updates the properties of the specified node. Can accept Alfresco Content
     * Model Properties id or cmis properties id.
     * 
     * @param node : Node to update
     * @param properties : Properties to update.
     * @return Returns Newly update node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Node updateProperties(Node node, Map<String, Serializable> properties);

    /**
     * Updates the properties for the given node and applies the given aspects.
     * Can accept Alfresco Content Model Properties id or cmis properties id.
     * 
     * @param node : Node to update
     * @param properties : Properties to update.
     * @param aspects : list of aspects to apply
     * @since 1.4
     * @return Returns Newly update node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Node updateProperties(Node node, Map<String, Serializable> properties, List<String> aspects);

    /**
     * Adds the given aspects to the given node.
     * 
     * @param node Node to update
     * @param aspects : list of aspects to apply
     * @since 1.4
     * @return Returns Newly update node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Node addAspects(Node node, List<String> aspects);

    /**
     * Updates the content on the given document using the provided local file.
     * 
     * @param document : Document object
     * @param file : File that is going to replace document content
     * @return newly updated Document.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Document updateContent(Document document, ContentFile file);

    /**
     * Downloads the content for the given document.
     * 
     * @param document : Document object
     * @return the contentFile representation that contains file informations +
     *         inputStream of the content.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    ContentFile getContent(Document document);

    /**
     * Downloads the content for the given document as InputStream.
     * 
     * @param document : Document object
     * @return the contentFile representation that contains file informations +
     *         inputStream of the content.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    ContentStream getContentStream(Document document);

    /**
     * @param node
     * @return Returns a Permissions object representing the allowed actions for
     *         the current user on the given node.
     */
    Permissions getPermissions(Node node);

    /**
     * Represent the unique identifier for thumbnail rendition.
     * 
     * @see #getRendition(Node, String)
     */
    String RENDITION_THUMBNAIL = "doclib";

    String RENDITION_PREVIEW = "imgpreview";

    /**
     * Retrieve a specific type of Rendition for the specified identifier.
     * 
     * @param node : Node (Document in general)
     * @param type : : Type of rendition available
     * @return Returns a ContentFile object representing a rendition of the
     *         given node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    ContentFile getRendition(Node node, String type);

    /**
     * Retrieve a specific type of Rendition for the specified identifier as
     * InputStream
     * 
     * @param node : Node (Document in general)
     * @param type : : Type of rendition available
     * @return Returns a ContentFile object representing a rendition of the
     *         given node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    ContentStream getRenditionStream(Node node, String type);

    /**
     * Returns a list of the current users favorite documents.
     * 
     * @return
     * @since 1.2
     */
    List<Document> getFavoriteDocuments();

    /**
     * Returns a paged list of the current users favorite documents.
     * 
     * @param listingContext
     * @return
     * @since 1.2
     */
    PagingResult<Document> getFavoriteDocuments(ListingContext listingContext);

    /**
     * Returns a list of the current users favorite folders.
     * 
     * @return
     * @since 1.2
     */
    List<Folder> getFavoriteFolders();

    /**
     * Returns a paged list of the current users favorite folders.
     * 
     * @param listingContext
     * @return
     * @since 1.2
     */
    PagingResult<Folder> getFavoriteFolders(ListingContext listingContext);

    /**
     * Returns a list of the current users favorite nodes.
     * 
     * @return
     * @since 1.2
     */
    List<Node> getFavoriteNodes();

    /**
     * Returns a paged list of the current users favorite nodes.
     * 
     * @param listingContext
     * @return
     * @since 1.2
     */
    PagingResult<Node> getFavoriteNodes(ListingContext listingContext);

    /**
     * Determines whether the given node has been marked as a favorite by the
     * current user.
     * 
     * @param node
     * @return
     * @since 1.2
     */
    boolean isFavorite(Node node);

    /**
     * Marks the provided node as a favorite node.
     * 
     * @param node
     * @since 1.2
     */
    void addFavorite(Node node);

    /**
     * Removes the provided node from the current users favorites.
     * 
     * @param node
     * @since 1.2
     */
    void removeFavorite(Node node);

    /**
     * Returns the latest (and complete) metadata for the provided node.
     * 
     * @param node
     * @return
     * @since 1.2
     */
    Node refreshNode(Node node);

}
