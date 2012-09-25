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
package org.alfresco.mobile.android.samples.utils;

import android.app.Activity;

public final class UIUtils
{

    private UIUtils()
    {
    }

    /**
     * Display the title at the specific location depending an Android version.
     * 
     * @param activity : android activity
     * @param titleId : unique resouce identifier for title
     */
    public static void setFragmentTitle(Activity activity, int titleId)
    {
        setFragmentTitle(activity, activity.getResources().getText(titleId).toString());
    }

    /**
     * Display the title at the specific location depending an Android version.
     * 
     * @param activity : android activity
     * @param text : Title text
     */
    public static void setFragmentTitle(Activity activity, String text)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            activity.getActionBar().setTitle(text);
            activity.invalidateOptionsMenu();
        }
        else
        {
            activity.setTitle(text);
        }
    }

}