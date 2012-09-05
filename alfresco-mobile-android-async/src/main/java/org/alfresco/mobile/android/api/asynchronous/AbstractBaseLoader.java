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
package org.alfresco.mobile.android.api.asynchronous;

import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Abstract Base Loader that request Alfresco Mobile SDK Services.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractBaseLoader<T> extends AsyncTaskLoader<T>
{
    private T data;

    protected AlfrescoSession session;

    /**
     * Default constructor.
     * 
     * @param context
     */
    public AbstractBaseLoader(Context context)
    {
        super(context);
    }

    @Override
    protected void onStartLoading()
    {
        if (data != null)
        {
            deliverResult(data);
        }

        if (takeContentChanged() || data == null)
        {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(T data)
    {
        if (isReset()) { return; }
        this.data = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading()
    {
        cancelLoad();
    }

    @Override
    protected void onReset()
    {
        super.onReset();

        onStopLoading();

        data = null;
    }
}
