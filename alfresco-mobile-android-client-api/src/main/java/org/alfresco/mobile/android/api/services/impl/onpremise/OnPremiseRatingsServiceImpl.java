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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.impl.AbstractRatingsService;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
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
public class OnPremiseRatingsServiceImpl extends AbstractRatingsService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public OnPremiseRatingsServiceImpl(RepositorySession repositorySession)
    {
        super(repositorySession);
    }

    protected UrlBuilder getRatingsUrl(Node node)
    {
        return new UrlBuilder(OnPremiseUrlRegistry.getRatingsUrl(session, node.getIdentifier()));
    }

    protected JSONObject getRatingsObject()
    {
        JSONObject jo = new JSONObject();
        jo.put(OnPremiseConstant.RATING_VALUE, "1");
        jo.put(OnPremiseConstant.RATINGSCHEME_VALUE, OnPremiseConstant.LIKERATINGSSCHEME_VALUE);
        return jo;
    }

    protected UrlBuilder getUnlikeUrl(Node node)
    {
        return new UrlBuilder(OnPremiseUrlRegistry.getUnlikeUrl(session, node.getIdentifier()));
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected int computeRatingsCount(UrlBuilder url)
    {
        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        if (json == null) { return -1; }

        Map<String, Object> j = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
        if (j.size() == 0 && j.get(OnPremiseConstant.NODESTATISTICS_VALUE) == null) { return -1; }

        Map<String, Object> js = (Map<String, Object>) j.get(OnPremiseConstant.NODESTATISTICS_VALUE);
        if (js.size() == 0 && js.get(OnPremiseConstant.LIKERATINGSSCHEME_VALUE) == null) { return -1; }

        Map<String, Object> jso = (Map<String, Object>) js.get(OnPremiseConstant.LIKERATINGSSCHEME_VALUE);
        if (jso.size() != 0 && jso.get(OnPremiseConstant.RATINGSCOUNT_VALUE) != null) { return Integer
                .parseInt(JSONConverter.getString(jso, OnPremiseConstant.RATINGSCOUNT_VALUE)); }

        return -1;
    }

    @SuppressWarnings("unchecked")
    protected boolean computeIsRated(UrlBuilder url)
    {
        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        if (json == null) { return false; }

        Map<String, Object> j = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
        if (j.size() == 0 && j.get(OnPremiseConstant.RATINGS_VALUE) == null) { return false; }

        Map<String, Object> js = (Map<String, Object>) j.get(OnPremiseConstant.RATINGS_VALUE);
        if (js.size() == 0 && js.get(OnPremiseConstant.LIKERATINGSSCHEME_VALUE) == null) { return false; }

        Map<String, Object> jso = (Map<String, Object>) js.get(OnPremiseConstant.LIKERATINGSSCHEME_VALUE);
        if (jso.size() != 0 && jso.get(OnPremiseConstant.APPLIEDBY_VALUE) != null) { return session
                .getPersonIdentifier().equals(JSONConverter.getString(jso, OnPremiseConstant.APPLIEDBY_VALUE)); }

        return false;
    }

}
