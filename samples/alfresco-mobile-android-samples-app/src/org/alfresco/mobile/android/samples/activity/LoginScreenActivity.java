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
package org.alfresco.mobile.android.samples.activity;

import org.alfresco.mobile.android.samples.fragments.FragmentDisplayer;
import org.alfresco.mobile.android.samples.ui.accounts.AccountDetailsFragment;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;

import android.os.Bundle;

/**
 * Displays a sample form to connect to the specified server.
 * 
 * @author Jean Marie Pascal
 */
public class LoginScreenActivity extends CommonActivity
{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdkapp_main);
        loadLoginFragmentForm();
    }

    /**
     * Load the login fragment form.
     */
    public void loadLoginFragmentForm()
    {
        BaseFragment frag = new AccountDetailsFragment();
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, AccountDetailsFragment.TAG, false);
    }

}
