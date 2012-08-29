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
/*
 * Copyright (C) 2011 JM.PASCAL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.mobile.android.api.model.impl;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * ActivityEntry :
 * <ul>
 * <li>represents an action that has taken place within an Alfresco repository</li>
 * <li>is typically initiated by the Alfresco app/tool/component/service on
 * behalf of a user</li>
 * <li>is of a given/named type specified by the Alfresco app/tool (eg. document
 * added)</li>
 * <li>is performed at a particular point in time (post date)</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public class ActivityEntryImpl implements ActivityEntry
{

    /** Unique identifier to a specific activity entry. */
    private String identifier;

    /** Site associated to a specific activity entry. */
    private String siteShortName;

    /** UserID who create this specific activity entry. */
    private String postUserId;

    /** Creation date of this specific activity entry. */
    private GregorianCalendar postDate;

    /** Specific Type of this activity. */
    private String type;

    /**
     * Extra data map that contains all information about the specific activity.
     */
    private Map<String, String> data;

    /**
     * Parse Json Response from Alfresco REST API to create an ActivityEntry.
     * 
     * @param jo : json response that contains data from the repository
     * @return ActivityEntry that contains informations about the activity.
     */
    public static ActivityEntryImpl parseJson(Map<String, Object> jo)
    {
        ActivityEntryImpl activityItem = new ActivityEntryImpl();

        activityItem.identifier = JSONConverter.getString(jo, OnPremiseConstant.ID_VALUE);
        activityItem.siteShortName = JSONConverter.getString(jo, OnPremiseConstant.SITENETWORK_VALUE);
        activityItem.postUserId = JSONConverter.getString(jo, OnPremiseConstant.POSTUSERID_VALUE);
        String postDate = JSONConverter.getString(jo, OnPremiseConstant.POSTDATE_VALUE);

        GregorianCalendar g = new GregorianCalendar();
        g.setTime(DateUtils.parseDate(postDate, DateUtils.FORMAT_3));
        activityItem.postDate = g;

        activityItem.type = JSONConverter.getString(jo, OnPremiseConstant.ACTIVITYTYPE_VALUE);

        String activitySummaryValue = JSONConverter.getString(jo, OnPremiseConstant.SUMMARY_VALUE);
        Map<String, Object> activitySummary = JsonUtils.parseObject(activitySummaryValue);

        activityItem.data = new HashMap<String, String>(10);
        activityItem.data.put(OnPremiseConstant.FEEDUSERID_VALUE,
                JSONConverter.getString(jo, OnPremiseConstant.FEEDUSERID_VALUE));
        activityItem.data.put(OnPremiseConstant.FORMAT_VALUE,
                JSONConverter.getString(jo, OnPremiseConstant.FORMAT_VALUE));
        activityItem.data.put(OnPremiseConstant.LASTNAME_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.LASTNAME_VALUE));
        activityItem.data.put(OnPremiseConstant.FIRSTNAME_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.FIRSTNAME_VALUE));
        activityItem.data.put(OnPremiseConstant.TITLE_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.TITLE_VALUE));
        activityItem.data.put(OnPremiseConstant.PAGE_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.PAGE_VALUE));
        activityItem.data.put(OnPremiseConstant.NODEREF_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.NODEREF_VALUE));
        activityItem.data.put(OnPremiseConstant.MEMBERLASTNAME_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.MEMBERLASTNAME_VALUE));
        activityItem.data.put(OnPremiseConstant.ROLE_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.ROLE_VALUE));
        activityItem.data.put(OnPremiseConstant.STATUS_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.STATUS_VALUE));
        activityItem.data.put(OnPremiseConstant.MEMEBERFIRSTNAME_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.MEMEBERFIRSTNAME_VALUE));
        activityItem.data.put(OnPremiseConstant.MEMEBERUSERNAME_VALUE,
                JSONConverter.getString(activitySummary, OnPremiseConstant.MEMEBERUSERNAME_VALUE));

        return activityItem;
    }
    
    @SuppressWarnings("unchecked")
    public static ActivityEntryImpl parsePublicAPIJson(Map<String, Object> jo){
        ActivityEntryImpl activityItem = new ActivityEntryImpl();

        activityItem.identifier = JSONConverter.getString(jo, CloudConstant.ID_VALUE);
        activityItem.siteShortName = JSONConverter.getString(jo, CloudConstant.SITEID_VALUE);
        activityItem.postUserId = JSONConverter.getString(jo, CloudConstant.POSTPERSONID_VALUE);
        String postDate = JSONConverter.getString(jo, CloudConstant.POSTDATE_VALUE);

        GregorianCalendar g = new GregorianCalendar();
        g.setTime(DateUtils.parseDate(postDate, DateUtils.FORMAT_3));
        activityItem.postDate = g;

        activityItem.type = JSONConverter.getString(jo, CloudConstant.ACTIVITYTYPE_VALUE);
        
        //String activitySummaryValue = JSONConverter.getString(jo, CloudConstant.ACTIVITYSUMMARY_VALUE);
        Map<String, Object> activitySummary = (Map<String, Object>) ((Map<String, Object>) jo).get(CloudConstant.ACTIVITYSUMMARY_VALUE);

        activityItem.data = new HashMap<String, String>(10);
        activityItem.data.put(CloudConstant.FEEDUSERID_VALUE,
                JSONConverter.getString(jo, CloudConstant.FEEDUSERID_VALUE));
        activityItem.data.put(CloudConstant.FORMAT_VALUE,
               JSONConverter.getString(jo, CloudConstant.FORMAT_VALUE));
        activityItem.data.put(CloudConstant.LASTNAME_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.LASTNAME_VALUE));
        activityItem.data.put(CloudConstant.FIRSTNAME_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.FIRSTNAME_VALUE));
        activityItem.data.put(CloudConstant.TITLE_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.TITLE_VALUE));
        activityItem.data.put(CloudConstant.PAGE_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.PAGE_VALUE));
        activityItem.data.put(CloudConstant.OBJECTID_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.OBJECTID_VALUE));
        activityItem.data.put(CloudConstant.NETWORKID_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.NETWORKID_VALUE));
        activityItem.data.put(CloudConstant.MEMBERLASTNAME_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.MEMBERLASTNAME_VALUE));
        activityItem.data.put(CloudConstant.ROLE_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.ROLE_VALUE));
        activityItem.data.put(CloudConstant.STATUS_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.STATUS_VALUE));
        activityItem.data.put(CloudConstant.MEMEBERFIRSTNAME_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.MEMEBERFIRSTNAME_VALUE));
        activityItem.data.put(CloudConstant.MEMEBERUSERNAME_VALUE,
                JSONConverter.getString(activitySummary, CloudConstant.MEMEBERUSERNAME_VALUE));

        return activityItem; 
    }


    /**
     * @return Returns a map of all extra data specific to a certain type of
     *         activity.
     */
    public Map<String, String> getData()
    {
        return data;
    }

    /**
     * Return the value of a specific key available in extra data.
     * 
     * @param key : reference
     * @return value associated to this key. Null if not present.
     */
    public String getData(String key)
    {
        if (data == null || !data.containsKey(key)) return null;
        return data.get(key);
    }

    /**
     * @return Returns the identifier of this specific activity.
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * @return Returns the site associated to a specific activity entry
     */
    public String getSiteShortName()
    {
        return siteShortName;
    }

    /**
     * @return Returns Username who create this specific activity entry
     */
    public String getCreatedBy()
    {
        return postUserId;
    }

    /**
     * @return Returns Creation date of this specific activity entry
     */
    public GregorianCalendar getCreationTime()
    {
        return postDate;
    }

    /**
     * @return Returns Specific Type of this activity.
     */
    public String getType()
    {
        return type;
    }

}
