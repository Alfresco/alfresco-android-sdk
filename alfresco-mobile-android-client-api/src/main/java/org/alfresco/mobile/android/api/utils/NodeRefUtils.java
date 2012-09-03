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
package org.alfresco.mobile.android.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * List of static methods to manage Alfresco NodeRef.
 * 
 * @author Jean Marie Pascal
 */
public class NodeRefUtils
{

    public static final int IDENTIFIER_LENGTH = 36;
    
    public static final String URI_FILLER = "://";

    private static final Pattern nodeRefPattern = Pattern.compile(".+://.+/.+");

    private static final Pattern identifierPattern = Pattern
            .compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");

    private static final Pattern identifierVersionPattern = Pattern
            .compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12};.+");

    public static final String PROTOCOL_WORKSPACE = "workspace";

    public static final String IDENTIFIER_SPACESSTORE = "SpacesStore";

    public static final String STORE_REF_WORKSPACE_SPACESSTORE = PROTOCOL_WORKSPACE + URI_FILLER
            + IDENTIFIER_SPACESSTORE;

    /**
     * Determine if passed string conforms to the pattern of a node reference
     * 
     * @param nodeRef the node reference as a string
     * @return true => it matches the pattern of a node reference
     */
    public static boolean isNodeRef(String nodeRef)
    {
        Matcher matcher = nodeRefPattern.matcher(nodeRef);
        return matcher.matches();
    }

    public static String getStoreRef(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            return nodeRef.substring(0, lastForwardSlash);
        }
        return null;
    }

    public static String getStoreProtocol(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int dividerPatternPosition = nodeRef.indexOf(URI_FILLER);
            return nodeRef.substring(0, dividerPatternPosition);
        }
        return null;
    }

    public static String getStoreIdentifier(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int dividerPatternPosition = nodeRef.indexOf(URI_FILLER);
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            return nodeRef.substring(dividerPatternPosition + 3, lastForwardSlash);
        }
        return null;
    }

    /**
     * Returns the identifier of a nodeRef(extract the version number if added
     * by cmis)
     * 
     * @param nodeRef
     * @return
     */
    public static String getNodeIdentifier(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            int versionNumber = (nodeRef.lastIndexOf(';') == -1) ? nodeRef.length() : nodeRef.lastIndexOf(';');
            return nodeRef.substring(lastForwardSlash + 1, versionNumber);
        }
        else if (isIdentifier(nodeRef) || isVersionIdentifier(nodeRef))
        {
            return nodeRef;
        }
        return null;
    }

    public static String createNodeRefByIdentifier(String identifier)
    {
        return STORE_REF_WORKSPACE_SPACESSTORE + "/" + identifier;
    }

    public static String getCleanIdentifier(String nodeRef)
    {
        int versionNumber = (nodeRef.lastIndexOf(';') == -1) ? nodeRef.length() : nodeRef.lastIndexOf(';');
        return nodeRef.substring(0, versionNumber);
    }

    public static boolean isIdentifier(String id)
    {
        Matcher matcher = identifierPattern.matcher(id);
        return matcher.matches();
    }

    public static boolean isVersionIdentifier(String id)
    {
        Matcher matcher = identifierVersionPattern.matcher(id);
        return matcher.matches();
    }

    public static String getVersionIdentifier(String nodeRef)
    {
        if (isNodeRef(nodeRef))
        {
            int lastForwardSlash = nodeRef.lastIndexOf('/');
            return nodeRef.substring(lastForwardSlash + 1, nodeRef.length());
        }
        else if (isVersionIdentifier(nodeRef))
        {
            return nodeRef;
        }
        else if (isIdentifier(nodeRef)) { return nodeRef; }
        return null;
    }
}
