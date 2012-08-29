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
package org.alfresco.mobile.android.samples.ui.sites;

import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.Sorting;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.activity.MainActivity;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.site.SitesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class BrowserFavoriteSitesFragment extends SitesFragment
{
    
    public static final String TAG = "BrowserFavoriteSitesFragment";

    public BrowserFavoriteSitesFragment()
    {
    }
    
    public static BrowserFavoriteSitesFragment newInstance(String username)
    {
        BrowserFavoriteSitesFragment bf = new BrowserFavoriteSitesFragment();
        ListingContext lc = new ListingContext();
        lc.setSortProperty(Sorting.NAME);
        lc.setIsSortAscending(true);
        Bundle b = createBundleArgs(username, true);
        b.putAll(createBundleArgs(lc, LOAD_AUTO));
        bf.setArguments(b);
        return bf;
    }
    
    @Override
    public void onStart()
    {
        UIUtils.setFragmentTitle(getActivity(), R.string.select_favourite_sites_title);
        super.onStart();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        alfSession = SessionUtils.getsession(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Site s = (Site) l.getItemAtPosition(position);
        ((MainActivity) getActivity()).showBrowserFragment(s);
    }
}
