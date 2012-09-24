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
package org.alfresco.mobile.android.api.exceptions.impl;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoErrorContent;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.http.HttpStatus;

public class OAuthErrorContent implements AlfrescoErrorContent
{
    private String message;

    private String description;

    public static OAuthErrorContent parseJson(String errorContentValue)
    {
        Map<String, Object> json = null;
        try
        {
            json = JsonUtils.parseObject(errorContentValue);
        }
        catch (Exception e)
        {
            return null;
        }

        OAuthErrorContent errorContent = null;
        if (json.containsKey(CloudConstant.ERROR_VALUE) && json.containsKey(CloudConstant.ERRORDESCRIPTION_VALUE))
        {
            errorContent = new OAuthErrorContent();
            errorContent.message = JSONConverter.getString(json, CloudConstant.ERROR_VALUE);
            errorContent.description = JSONConverter.getString(json, CloudConstant.ERRORDESCRIPTION_VALUE);
        }
        return errorContent;
    }

    @Override
    public int getCode()
    {
        return HttpStatus.SC_UNAUTHORIZED;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public String getStackTrace()
    {
        return null;
    }

    public String getDescription()
    {
        return description;
    }
}
