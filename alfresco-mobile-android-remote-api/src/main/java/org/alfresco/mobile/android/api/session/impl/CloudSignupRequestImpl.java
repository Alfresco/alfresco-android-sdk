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
package org.alfresco.mobile.android.api.session.impl;

import java.util.GregorianCalendar;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.session.CloudSignupRequest;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class CloudSignupRequestImpl implements CloudSignupRequest
{

    private String identifier;

    private String apiKey;

    private String registrationKey;

    private String emailAdress;

    private String registrationTime;

    private Boolean isRegistered;

    private Boolean isActivated;

    private Boolean isPreRegistered;

    public static CloudSignupRequest parsePublicAPIJson(Map<String, Object> json)
    {
        CloudSignupRequestImpl request = new CloudSignupRequestImpl();

        request.identifier = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        request.apiKey = JSONConverter.getString(json, CloudConstant.CLOUD_KEY);
        request.registrationKey = JSONConverter.getString(json, CloudConstant.CLOUD_REGISTRATION_KEY);
        request.emailAdress = JSONConverter.getString(json, CloudConstant.CLOUD_EMAIL_VALUE);
        request.registrationTime = JSONConverter.getString(json, CloudConstant.CLOUD_REGISTRATIONDATE);

        if (json.containsKey(CloudConstant.CLOUD_ISACTIVATED))
            request.isActivated = JSONConverter.getBoolean(json, CloudConstant.CLOUD_ISACTIVATED);
        if (json.containsKey(CloudConstant.CLOUD_ISREGISTERED))
            request.isRegistered = JSONConverter.getBoolean(json, CloudConstant.CLOUD_ISREGISTERED);
        if (json.containsKey(CloudConstant.CLOUD_ISPREREGISTERED))
            request.isPreRegistered = JSONConverter.getBoolean(json, CloudConstant.CLOUD_ISPREREGISTERED);

        return request;
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getApiKey()
    {
        return apiKey;
    }

    @Override
    public String getRegistrationKey()
    {
        return registrationKey;
    }

    @Override
    public String getEmailAddress()
    {
        return emailAdress;
    }

    @Override
    public GregorianCalendar getRegistrationTime()
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.parseJsonDate(registrationTime));
        return cal;
    }

    public Boolean isRegistered()
    {
        return isRegistered;
    }

    public Boolean isActivated()
    {
        return isActivated;
    }

    public Boolean isPreRegistered()
    {
        return isPreRegistered;
    }

}
