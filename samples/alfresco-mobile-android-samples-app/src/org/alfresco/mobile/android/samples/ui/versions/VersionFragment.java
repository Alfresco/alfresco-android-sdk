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
package org.alfresco.mobile.android.samples.ui.versions;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.fragments.BaseListAdapter;
import org.alfresco.mobile.android.ui.version.VersionsFragment;

import android.os.Bundle;

public class VersionFragment extends VersionsFragment
{

    public static final String TAG = "VersionFragment";

    public VersionFragment()
    {
    }

    public static VersionFragment newInstance(Node n)
    {
        VersionFragment bf = new VersionFragment();
        Bundle settings = createBundleArgs(n);
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
        UIUtils.setFragmentTitle(getActivity(), getText(R.string.document_version_header).toString());
        super.onStart();
    }
}
