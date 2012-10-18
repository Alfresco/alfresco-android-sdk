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
package org.alfresco.mobile.android.samples.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.fragments.FragmentDisplayer;
import org.alfresco.mobile.android.samples.ui.activitystream.ActivitiesFragment;
import org.alfresco.mobile.android.samples.ui.documentfolder.ChildrenFragment;
import org.alfresco.mobile.android.samples.ui.search.SimpleSearchFragment;
import org.alfresco.mobile.android.samples.ui.sites.BrowserAllSitesFragment;
import org.alfresco.mobile.android.samples.ui.sites.BrowserFavoriteSitesFragment;
import org.alfresco.mobile.android.samples.ui.sites.BrowserMySitesFragment;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListUISamplesFragments extends ListFragment
{

    public static final String FRAG_TAG = ListUISamplesFragments.class.getName();

    private List<String> listText = new ArrayList<String>();
    
    public ListUISamplesFragments()
    {
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        listText.clear();
        for (Entry<String, String> map : getListItems().entrySet())
        {
            listText.add(map.getKey());
        }

        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listText));
    }

    @Override
    public void onStart()
    {
        UIUtils.setFragmentTitle(getActivity(), R.string.sample_title);
        super.onStart();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        BaseFragment f = null;
        String tag = getListItems().get(((TextView) v).getText());
        if (ChildrenFragment.TAG.equals(tag))
        {
            f = ChildrenFragment.newInstance("/");
            f.setSession(SessionUtils.getsession(getActivity()));
        }
        else if (BrowserAllSitesFragment.TAG.equals(tag))
        {
            f = BrowserAllSitesFragment.newInstance();
            f.setSession(SessionUtils.getsession(getActivity()));
        }
        else if (BrowserFavoriteSitesFragment.TAG.equals(tag))
        {
            f = BrowserFavoriteSitesFragment.newInstance(SessionUtils.getsession(getActivity()).getPersonIdentifier());
            f.setSession(SessionUtils.getsession(getActivity()));
        }
        else if (BrowserMySitesFragment.TAG.equals(tag))
        {
            f = BrowserMySitesFragment.newInstance(SessionUtils.getsession(getActivity()).getPersonIdentifier());
            f.setSession(SessionUtils.getsession(getActivity()));
        } 
         else if (ActivitiesFragment.TAG.equals(tag))
        {
            f = ActivitiesFragment.newInstance();
            f.setSession(SessionUtils.getsession(getActivity()));
        }

        FragmentDisplayer.replaceFragment(getActivity(), f, R.id.body, tag, true);
    }

    private  Map<String, String> getListItems(){
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(getText(R.string.sample_browse_favourite_sites_option).toString(), BrowserFavoriteSitesFragment.TAG);
        values.put(getText(R.string.sample_browse_my_sites_option).toString(), BrowserMySitesFragment.TAG);
        values.put(getText(R.string.sample_browse_all_sites_option).toString(), BrowserAllSitesFragment.TAG);
        values.put(getText(R.string.sample_browse_company_home_option).toString(), ChildrenFragment.TAG);
        values.put(getText(R.string.sample_browse_activities_option).toString(), ActivitiesFragment.TAG);
        values.put(getText(R.string.sample_search_option).toString(), SimpleSearchFragment.TAG);
        return values;
    }
}
