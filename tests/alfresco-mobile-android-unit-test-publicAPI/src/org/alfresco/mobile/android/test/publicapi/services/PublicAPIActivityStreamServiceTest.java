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
package org.alfresco.mobile.android.test.publicapi.services;

import org.alfresco.mobile.android.test.api.services.ActivityStreamServiceTest;

import android.util.Log;

/**
 * Test class for ActivityStreamService. This test requires an Alfresco session
 * and the default sample share site Sample: Web Site Design Project.
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPIActivityStreamServiceTest extends ActivityStreamServiceTest
{

    /**
     * Impossible to retrieve total items for cloud.
     * 
     * @return -1
     */
    protected int getTotalItems(int value)
    {
        return -1;
    }

    /**
     * In case of cloud, there's no limitation.
     * 
     * @return false for onpremise, true for cloud.
     */
    protected Boolean hasMoreItem()
    {
        return true;
    }
    
    @Override
    public void testActivityServiceMethodsError()
    {
        try
        {
            super.testActivityServiceMethodsError();
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e)); 
        }
    }
    
    
    @Override
    public void testActivityStreamService()
    {
        try
        {
            super.testActivityServiceMethodsError();
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e)); 
        }
    }
}
