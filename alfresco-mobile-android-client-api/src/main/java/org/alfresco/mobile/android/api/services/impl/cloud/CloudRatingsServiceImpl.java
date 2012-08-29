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
package org.alfresco.mobile.android.api.services.impl.cloud;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.impl.AbstractRatingsService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

/**
 * The RatingsService can be used to manage like (as ratings) on any content
 * node in the repository.<br>
 * Like can be applied or removed.
 * 
 * @author Jean Marie Pascal
 */
public class CloudRatingsServiceImpl extends AbstractRatingsService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public CloudRatingsServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    protected UrlBuilder getRatingsUrl(Node node)
    {
        return new UrlBuilder(CloudUrlRegistry.getRatingsUrl((CloudSession) session, node.getIdentifier()));
    }

    protected JSONObject getRatingsObject()
    {
        JSONObject jo = new JSONObject();
        jo.put(CloudConstant.MYRATING_VALUE, true);
        jo.put(CloudConstant.ID_VALUE, CloudConstant.LIKES_VALUE);
        return jo;
    }

    protected UrlBuilder getUnlikeUrl(Node node)
    {
        return new UrlBuilder(CloudUrlRegistry.getUnlikeUrl((CloudSession) session, node.getIdentifier()));
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected int computeRatingsCount(UrlBuilder url)
    {
        // read and parse
        HttpUtils.Response resp = read(url);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            if (data.containsKey(CloudConstant.ID_VALUE)
                    && CloudConstant.LIKES_VALUE.equals(data.get(CloudConstant.ID_VALUE))
                    && data.containsKey(CloudConstant.AGGREGATE_VALUE)) { return JSONConverter.getInteger(
                    (Map<String, Object>) data.get(CloudConstant.AGGREGATE_VALUE), CloudConstant.NUMBEROFRATINGS_VALUE)
                    .intValue();

            }
        }

        return -1;
    }

    @SuppressWarnings("unchecked")
    protected boolean computeIsRated(UrlBuilder url)
    {
        // read and parse
        HttpUtils.Response resp = read(url);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            if (data.containsKey(CloudConstant.ID_VALUE)
                    && CloudConstant.LIKES_VALUE.equals(data.get(CloudConstant.ID_VALUE))
                    && data.containsKey(CloudConstant.MYRATING_VALUE)) { return true; }
        }

        return false;
    }

}
