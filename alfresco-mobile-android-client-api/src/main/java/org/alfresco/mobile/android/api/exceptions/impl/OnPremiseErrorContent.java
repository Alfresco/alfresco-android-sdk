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

import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoErrorContent;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * OnPremise Implementation of AlfrescoErrorContent.
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseErrorContent implements AlfrescoErrorContent
{
    private String status;

    private int code;

    private String name;

    private String description;

    private String message;

    private String exception;

    private String callstack;

    @SuppressWarnings("unchecked")
    public static OnPremiseErrorContent parseJson(String errorContentValue)
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

        OnPremiseErrorContent errorContent = new OnPremiseErrorContent();

        Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.STATUS_VALUE);
        errorContent.code = JSONConverter.getInteger(jo, OnPremiseConstant.CODE_VALUE).intValue();
        errorContent.name = JSONConverter.getString(jo, OnPremiseConstant.DELETE_VALUE);
        errorContent.description = JSONConverter.getString(jo, OnPremiseConstant.DELETE_VALUE);

        errorContent.message = JSONConverter.getString(json, OnPremiseConstant.MESSAGE_VALUE);
        errorContent.exception = JSONConverter.getString(json, OnPremiseConstant.EXCEPTION_VALUE);
        List<String> s = (List<String>) json.get(OnPremiseConstant.CALLSTACK_VALUE);
        if (s != null)
        {
            errorContent.callstack = s.toString();
        }
        return errorContent;
    }

    public String getStatus()
    {
        return status;
    }

    public int getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getMessage()
    {
        return message;
    }

    public String getException()
    {
        return exception;
    }

    public String getStackTrace()
    {
        return callstack;
    }

}
