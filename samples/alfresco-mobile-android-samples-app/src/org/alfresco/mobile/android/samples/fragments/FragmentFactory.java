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
package org.alfresco.mobile.android.samples.fragments;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.samples.ui.ListUISamplesFragments;
import org.alfresco.mobile.android.samples.ui.activitystream.ActivitiesFragment;
import org.alfresco.mobile.android.samples.ui.properties.DetailsFragment;
import org.alfresco.mobile.android.samples.ui.search.SimpleSearchFragment;
import org.alfresco.mobile.android.samples.ui.sites.BrowserAllSitesFragment;
import org.alfresco.mobile.android.samples.ui.tags.TagsListNodeFragment;

import android.app.Fragment;
import android.util.Log;

public final class FragmentFactory
{

    private static final String TAG = "FragmentFactory";

    private FragmentFactory()
    {
    }

    public static Fragment createInstance(String tag)
    {
        try
        {
            if (FRAGMENT_REGISTRY.containsKey(tag))
            {
                return (Fragment) FRAGMENT_REGISTRY.get(tag).newInstance();
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "serial" })
    public static final Map<String, Class> FRAGMENT_REGISTRY = new HashMap<String, Class>()
    {
        {
            put(ListUISamplesFragments.FRAG_TAG, ListUISamplesFragments.class);
            put(ActivitiesFragment.TAG, ActivitiesFragment.class);
            put(BrowserAllSitesFragment.TAG, BrowserAllSitesFragment.class);
            put(DetailsFragment.TAG, DetailsFragment.class);
            put(SimpleSearchFragment.TAG, SimpleSearchFragment.class);
            put(TagsListNodeFragment.TAG, TagsListNodeFragment.class);
        }
    };

}
