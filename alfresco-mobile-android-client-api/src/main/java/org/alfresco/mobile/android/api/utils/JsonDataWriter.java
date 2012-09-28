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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.json.JSONArray;

/**
 * Allow to create a JSON writer to HTTP POST/PUT/DELETE operations.
 * 
 * @author Jean Marie Pascal
 */
public class JsonDataWriter
{
    private static final String CONTENT_TYPE_URLENCODED = "application/json;charset=utf-8";

    private static final String CRLF = "\r\n";

    private JSONObject json;

    private JSONArray jsonArray;

    public JsonDataWriter(JSONObject json)
    {
        this.json = json;
    }

    public JsonDataWriter(JSONArray jsonArray)
    {
        this.jsonArray = jsonArray;
    }

    public String getContentType()
    {
        return CONTENT_TYPE_URLENCODED;
    }

    public void write(OutputStream out) throws IOException 
    {
        if (json != null)
        {
            writeLine(out, json.toJSONString());
        }
        else if (jsonArray != null)
        {
            writeLine(out, jsonArray.toString());
        }
    }

    private void writeLine(OutputStream out, String s) throws IOException 
    {
        String tmpString = (s == null ? CRLF : s + CRLF);
        out.write(tmpString.getBytes("UTF-8"));
    }
}
