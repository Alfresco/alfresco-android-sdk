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
package org.alfresco.mobile.android.extension.api.services.impl;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseRatingsServiceImpl;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.extension.api.constant.CustomConstant;
import org.alfresco.mobile.android.extension.api.model.StarRating;
import org.alfresco.mobile.android.extension.api.services.CustomRatingsService;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

/**
 * CustomRatingsService overrides SDK ratings to add support of 5 star scheme. There are various methods relating to the CustomRatingsService, including the ability to:
 * <ul>
 * <li>apply ratings</li>
 * <li>retrieve ratings</li>
 * <li>retrieve my ratings</li>
 * 
 * @author Jean Marie Pascal
 */
public class CustomRatingsServiceImpl extends OnPremiseRatingsServiceImpl implements CustomRatingsService {

	/**
	 * Default constructor. Used by {@link ServiceRegistry}
	 * @param repositorySession
	 */
	public CustomRatingsServiceImpl(RepositorySession repositorySession) {
		super(repositorySession);
	}

	/**
	 * Apply 5 star rating to the specified node.
     * @param node : Node object (Folder or Document).
	 * @param rating : Scale 0 to 5 stars.
	 * @throws AlfrescoServiceException
	 *             : If comment is not defined or If network problems occur during the process.
	 * */
	public void applyStarRating(Node node, float rating) throws AlfrescoServiceException {
		try {
			// build URL
			UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getRatingsUrl(session, node.getIdentifier()));

			// prepare json data
			JSONObject jo = new JSONObject();
			jo.put(OnPremiseConstant.RATING_VALUE, rating);
			jo.put(OnPremiseConstant.RATINGSCHEME_VALUE, CustomConstant.STARSRATINGSCHEME_VALUE);

			final JsonDataWriter formData = new JsonDataWriter(jo);

			// send and parse
			post(url, formData.getContentType(), new HttpUtils.Output() {
				public void write(OutputStream out) throws Exception {
					formData.write(out);
				}
			}, ErrorCodeRegistry.RATING_GENERIC);
		} catch (Exception e) {
			convertException(e);
		}
	}

	/**
	 * Get the star ratings value for the specified node (Document or Folder)
     * @param node : Node object (Folder or Document).
	 * @return StarRatings object that contains all informations about ratings (average value, number...)
	 * @throws AlfrescoServiceException
	 *             : If comment is not defined or If network problems occur during the process.
	 * 
	 */
	public StarRating getStarRating(Node node) throws AlfrescoServiceException {
		try {
			String link = OnPremiseUrlRegistry.getRatingsUrl(session, node.getIdentifier());
			UrlBuilder url = new UrlBuilder(link);
			return computeRating(url);
		} catch (Exception e) {
			convertException(e);
		}
		return null;
	}

	/**
	 * Get the user star ratings value for the specified node (Document or Folder)
     * @param node : Node object (Folder or Document).
	 * @return rating value between 0 to 5.
	 * @throws AlfrescoServiceException
	 *             : If comment is not defined or If network problems occur during the process.
	 */
	public float getUserStarRatingValue(Node node) throws AlfrescoServiceException {
		try {
			String link = OnPremiseUrlRegistry.getRatingsUrl(session, node.getIdentifier());
			UrlBuilder url = new UrlBuilder(link);
			return computeMyRating(url);
		} catch (Exception e) {
			convertException(e);
		}
		return -1f;

	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// / INTERNAL
	// ////////////////////////////////////////////////////////////////////////////////////
	private StarRating computeRating(UrlBuilder url) {
		HttpUtils.Response resp = read(url, ErrorCodeRegistry.RATING_GENERIC);
		Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

		if (json == null) {
			return null;
		}

		return StarRating.parseJson(json);
	}

	@SuppressWarnings("unchecked")
	private float computeMyRating(UrlBuilder url) {
		// read and parse
		HttpUtils.Response resp = read(url, ErrorCodeRegistry.RATING_GENERIC);
		Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

		if (json == null) {
			return -1;
		}

		Map<String, Object> j = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
		if (j.size() == 0 && j.get(OnPremiseConstant.RATINGS_VALUE) == null) {
			return -1;
		}

		Map<String, Object> jso = null;
		if (j.get(OnPremiseConstant.RATINGS_VALUE) instanceof Map) {
			Map<String, Object> js = (Map<String, Object>) j.get(OnPremiseConstant.RATINGS_VALUE);
			if (js == null || (js.size() == 0 && js.get(CustomConstant.STARSRATINGSCHEME_VALUE) == null)) {
				return -1;
			}
			jso = (Map<String, Object>) js.get(CustomConstant.STARSRATINGSCHEME_VALUE);
		} else {
			List<Object> js = ((List<Object>) j.get(OnPremiseConstant.RATINGS_VALUE));
			if (js == null || js.isEmpty()) {
				return -1;
			}
			jso = (Map<String, Object>) js.get(0);
		}

		if (jso.size() != 0 && jso.get(OnPremiseConstant.APPLIEDBY_VALUE) != null && session.getPersonIdentifier().equals(JSONConverter.getString(jso, OnPremiseConstant.APPLIEDBY_VALUE))) {
			return Float.parseFloat(JSONConverter.getString(jso, OnPremiseConstant.RATING_VALUE));
		}

		return -1;
	}
}
