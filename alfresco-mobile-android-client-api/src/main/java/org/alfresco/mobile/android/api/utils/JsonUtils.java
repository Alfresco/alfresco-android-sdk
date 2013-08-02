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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.commons.impl.json.parser.ContainerFactory;
import org.apache.chemistry.opencmis.commons.impl.json.parser.JSONParser;

/**
 * List of static methods to manage json object.
 * 
 * @author Jean Marie Pascal
 */
public final class JsonUtils
{
    private JsonUtils()
    {

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static final ContainerFactory SIMPLE_CONTAINER_FACTORY = new ContainerFactory()
    {

        public Map createObjectContainer()
        {
            return new LinkedHashMap();
        }

        public List creatArrayContainer()
        {
            return new ArrayList();
        }
    };

    /**
     * Parses an object from an input stream.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(InputStream stream, String charset)
    {
        Object obj = parse(stream, charset, SIMPLE_CONTAINER_FACTORY);

        if (obj instanceof Map) { return (Map<String, Object>) obj; }

        throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_GENERIC, Messagesl18n.getString("JsonUtils.0"));
    }

    /**
     * Parses an array from an input stream.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> parseArray(InputStream stream, String charset)
    {
        Object obj = parse(stream, charset, SIMPLE_CONTAINER_FACTORY);

        if (obj instanceof List) { return (List<Object>) obj; }

        throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_GENERIC, Messagesl18n.getString("JsonUtils.0"));
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String value)
    {
        Object obj = null;
        try
        {
            JSONParser parser = new JSONParser();
            obj = parser.parse(value, SIMPLE_CONTAINER_FACTORY);
            if (obj instanceof Map) { return (Map<String, Object>) obj; }
        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_GENERIC, e);
        }
        return null;
    }

    /**
     * Parses an input stream.
     */
    public static Object parse(InputStream stream, String charset, ContainerFactory containerFactory)
    {

        InputStreamReader reader = null;

        Object obj = null;
        try
        {
            reader = new InputStreamReader(stream, charset);
            JSONParser parser = new JSONParser();
            obj = parser.parse(reader, containerFactory);
        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_GENERIC, e);
        }
        finally
        {
            IOUtils.closeStream(reader);
            IOUtils.closeStream(stream);
        }

        return obj;
    }

    public static String convertStreamToString(InputStream is)
    {
        String line = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_GENERIC, e);
        }
        finally
        {
            IOUtils.closeStream(is);
        }
        return sb.toString();
    }
    
    /**
     * Utility method to help creating a default cmis query.
     * 
     * @param sb
     * @param delimiter
     * @param tokens
     */
    public static void join(StringBuilder sb, CharSequence delimiter, Object[] tokens)
    {
        boolean firstTime = true;
        for (Object token : tokens)
        {
            if (firstTime)
            {
                firstTime = false;
            }
            else
            {
                sb.append(delimiter);
            }
            sb.append("'" + token + "'");
        }
    }
    
    
    

}
