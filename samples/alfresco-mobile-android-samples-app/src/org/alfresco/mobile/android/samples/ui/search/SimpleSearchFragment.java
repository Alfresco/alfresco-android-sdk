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
package org.alfresco.mobile.android.samples.ui.search;

import org.alfresco.mobile.android.api.asynchronous.SearchLoader;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.activity.MainActivity;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.search.SearchFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

public class SimpleSearchFragment extends SearchFragment
{

    public static final String TAG = "SimpleSearchFragment";
    
    private static final int MAX_RESULT_ITEMS = 30;

    public SimpleSearchFragment()
    {
    }
    
    public static SimpleSearchFragment newInstance(String keywords)
    {
        SimpleSearchFragment ssf = new SimpleSearchFragment();
        ssf.setArguments(createBundleArgs(keywords));
        return ssf;
    }
    
    @Override
    public void onStart()
    {
        UIUtils.setFragmentTitle(getActivity(), R.string.search_hint);
        super.onStart();
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        alfSession = SessionUtils.getsession(getActivity());
        setActivateThumbnail(false);
        View v = inflater.inflate(R.layout.sdk_search_form, container, false);
        
        init(v, R.string.empty_child);
        
        final EditText query = (EditText) v.findViewById(R.id.search_query);
        Button b = (Button) v.findViewById(R.id.launch_search);
        final CheckBox cbFulltext = (CheckBox) v.findViewById(R.id.fulltext_value);
        final CheckBox cbIsExact = (CheckBox) v.findViewById(R.id.exact_value);

        b.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (query.getText().length() > 0)
                {
                    Bundle b = new Bundle();
                    b.putString(KEYWORDS, query.getText().toString());
                    b.putBoolean(INCLUDE_CONTENT, cbFulltext.isChecked());
                    b.putBoolean(EXACTMATCH, cbIsExact.isChecked());

                    // Reduce voluntary result list for cloud.
                    if (alfSession instanceof CloudSession)
                    {
                        b.putSerializable(ARGUMENT_LISTING, new ListingContext("", MAX_RESULT_ITEMS, 0, false));
                    }
                    reload(b, SearchLoader.ID, SimpleSearchFragment.this);
                }
            }
        });

        return v;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Node n = (Node) l.getItemAtPosition(position);
        if (n.isDocument())
        {
            // Show properties
            ((MainActivity) getActivity()).showPropertiesFragment(n);
        }
    }
  
}
