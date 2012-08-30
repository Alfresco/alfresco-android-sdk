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
package org.alfresco.mobile.android.extension.api.model;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.alfresco.mobile.android.extension.api.constant.CustomConstant;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Represents a ratings.
 * 
 * @author Jean Marie Pascal
 * 
 */
public class StarRating {

	private float average = -1;
	private float total = 0;
	private int count = 0;
	private boolean isRated = false;
	private String appliedAt;
	private float myRating = -1;

	/**
	 * Parse server json response.
	 * 
	 * @param json
	 * @return StarRating value.
	 */
	@SuppressWarnings("unchecked")
	public static StarRating parseJson(Map<String, Object> json) {
		StarRating rating = new StarRating();

		Map<String, Object> j, js, jso = null;

		j = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
		if (j.size() != 0 && j.get(OnPremiseConstant.RATINGS_VALUE) != null) {

			if (j.get(OnPremiseConstant.RATINGS_VALUE) instanceof Map) {
				js = (Map<String, Object>) j.get(OnPremiseConstant.RATINGS_VALUE);
				if (js.size() != 0 && js.get(CustomConstant.STARSRATINGSCHEME_VALUE) != null)
					jso = (Map<String, Object>) js.get(CustomConstant.STARSRATINGSCHEME_VALUE);
			} else if (j.get(OnPremiseConstant.RATINGS_VALUE) instanceof List) {
				List<Object> jsa = ((List<Object>) j.get(OnPremiseConstant.RATINGS_VALUE));
				if (jsa != null && !jsa.isEmpty())
					jso = (Map<String, Object>) jsa.get(0);
			}
			if (jso != null) {
				rating.myRating = Float.parseFloat(JSONConverter.getString(jso, OnPremiseConstant.RATING_VALUE));
				rating.appliedAt = JSONConverter.getString(jso, OnPremiseConstant.APPLIEDAT_VALUE);
				rating.isRated = true;
			}
		}

		if (j.size() != 0 && j.get(OnPremiseConstant.NODESTATISTICS_VALUE) != null) {
			js = (Map<String, Object>) j.get(OnPremiseConstant.NODESTATISTICS_VALUE);
			if (js.size() != 0 && js.get(CustomConstant.STARSRATINGSCHEME_VALUE) != null) {
				jso = (Map<String, Object>) js.get(CustomConstant.STARSRATINGSCHEME_VALUE);
				rating.average = Float.parseFloat(JSONConverter.getString(jso, OnPremiseConstant.AVERAGERATING_VALUE));
				rating.total = Float.parseFloat(JSONConverter.getString(jso, OnPremiseConstant.RATINGSTOTAL_VALUE));
				rating.count = Integer.parseInt(JSONConverter.getString(jso, OnPremiseConstant.RATINGSCOUNT_VALUE));
			}
		}

		return rating;
	}

	/**
	 * @return the average
	 */
	public float getAverage() {
		return average;
	}

	/**
	 * @return the total
	 */
	public float getTotal() {
		return total;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the isRated
	 */
	public boolean isRated() {
		return isRated;
	}

	/**
	 * @return the appliedAt
	 */
	public GregorianCalendar getAppliedAt() {
		if (appliedAt == null || DateUtils.parseJsonDate(appliedAt) == null)
			return null;
		GregorianCalendar g = new GregorianCalendar();
		g.setTime(DateUtils.parseJsonDate(appliedAt));
		return g;
	}

	public float getMyRating() {
		return myRating;
	}

}
