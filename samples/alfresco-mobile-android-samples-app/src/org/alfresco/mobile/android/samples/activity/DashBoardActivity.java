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

import org.alfresco.mobile.android.api.Version;
import org.alfresco.mobile.android.samples.R;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Activity that displays general information about Sample app.
 * 
 * @author Jean Marie Pascal
 */
public class DashBoardActivity extends CommonActivity
{

    private static final String TAG = DashBoardActivity.class.getSimpleName();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Version Number
        TextView tv = (TextView) findViewById(R.id.version_number);
        try
        {
            tv.setText("v" + getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        }
        catch (NameNotFoundException e)
        {
            tv.setText("vX.X.X");
        }
        
        Log.i(TAG, "SDK " + Version.SDK);
    }

    /**
     * Navigation : Go to login page to test the sample app.
     */
    public void alfrescosdksamples(View v)
    {
        startActivity(new Intent(this, ChooseServerActivity.class));
    }

    /**
     * Links to the Alfresco documentation site. Open in external browser view.
     */
    public void documentation(View v)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getText(R.string.api_reference_url).toString()));
        startActivity(browserIntent);
    }

    /**
     * Links to the Alfresco documentation site. Open in external browser view.
     */
    public void alfrescomobile(View v)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getText(R.string.learn_more_url).toString()));
        startActivity(browserIntent);
    }

}
