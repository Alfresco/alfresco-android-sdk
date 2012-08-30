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
package org.alfresco.mobile.android.samples.ui.activitystream;

import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.activitystream.ActivityStreamFragment;
import org.alfresco.mobile.android.ui.fragments.BaseListAdapter;

import android.os.Bundle;

public class ActivitiesFragment extends ActivityStreamFragment
{

    public static final String TAG = "ActivitiesFragment";

    
    public ActivitiesFragment()
    {
        super();
    }

    public static ActivitiesFragment newInstance()
    {
        ActivitiesFragment bf = new ActivitiesFragment();
        Bundle settings = new Bundle();
        settings.putInt(BaseListAdapter.DISPLAY_ICON, BaseListAdapter.DISPLAY_ICON_CREATOR);
        settings.putInt(BaseListAdapter.DISPLAY_DATE, BaseListAdapter.DISPLAY_DATE_DATETIME);
        bf.setArguments(settings);
        return bf;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        alfSession = SessionUtils.getsession(getActivity());
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onStart()
    {
        UIUtils.setFragmentTitle(getActivity(), R.string.activities);
        super.onStart();
    }
}
