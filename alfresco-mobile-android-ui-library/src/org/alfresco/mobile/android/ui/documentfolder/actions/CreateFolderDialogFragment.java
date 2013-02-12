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
package org.alfresco.mobile.android.ui.documentfolder.actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.asynchronous.FolderCreateLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.documentfolder.listener.OnNodeCreateListener;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.MessengerManager;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public abstract class CreateFolderDialogFragment extends BaseFragment implements LoaderCallbacks<LoaderResult<Folder>>
{

    public static final String TAG = "CreateFolderDialogFragment";

    public static final String ARGUMENT_FOLDER = "folder";

    public static final String ARGUMENT_FOLDER_NAME = "folderName";

    private OnNodeCreateListener onCreateListener;

    public CreateFolderDialogFragment()
    {
    }

    public static Bundle createBundle(Folder folder)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_FOLDER, folder);
        return args;
    }

    @Override
    public void onStart()
    {
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.mime_folder);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle(R.string.folder_create);
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);

        View v = inflater.inflate(R.layout.sdk_create_folder, container, false);
        final EditText tv = (EditText) v.findViewById(R.id.folder_name);

        Button button = (Button) v.findViewById(R.id.cancel);
        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                CreateFolderDialogFragment.this.dismiss();
            }
        });

        final Button bcreate = (Button) v.findViewById(R.id.create_folder);
        bcreate.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Bundle b = new Bundle();
                b.putString(ARGUMENT_FOLDER_NAME, tv.getText().toString());
                getLoaderManager().initLoader(FolderCreateLoader.ID, b, CreateFolderDialogFragment.this);
                getLoaderManager().getLoader(FolderCreateLoader.ID).forceLoad();
                bcreate.setEnabled(false);
            }
        });

        tv.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if (tv.getText().length() == 0)
                {
                    bcreate.setEnabled(false);
                }
                else
                {
                    bcreate.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });

        return v;
    }

    @Override
    public Loader<LoaderResult<Folder>> onCreateLoader(int id, Bundle args)
    {
        getDialog().hide();
        Map<String, Serializable> props = new HashMap<String, Serializable>(2);
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        if (onCreateListener != null)
        {
            onCreateListener.beforeContentCreation((Folder) getArguments().get(ARGUMENT_FOLDER),
                    args.getString(ARGUMENT_FOLDER_NAME), props, null);
        }
        return new FolderCreateLoader(getActivity(), alfSession, (Folder) getArguments().get(ARGUMENT_FOLDER),
                args.getString(ARGUMENT_FOLDER_NAME), props);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Folder>> loader, LoaderResult<Folder> results)
    {
        if (results.hasException())
        {
            MessengerManager.showLongToast(getActivity(), results.getException().getMessage());
            Log.e(TAG, Log.getStackTraceString(results.getException()));
        }

        if (onCreateListener != null)
        {
            if (results.hasException())
            {
                FolderCreateLoader loaderF = (FolderCreateLoader) loader;
                onCreateListener.onExeceptionDuringCreation(results.getException(), loaderF.getParentFolder(),
                        loaderF.getFolderName(), loaderF.getProperties(), null);
            }
            else
            {
                onCreateListener.afterContentCreation(results.getData());
            }
        }
        getDialog().dismiss();
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Folder>> arg0)
    {

    }

    public void setOnCreateListener(OnNodeCreateListener onCreateListener)
    {
        this.onCreateListener = onCreateListener;
    }

    @Override
    public void onDestroyView()
    {
        if (getDialog() != null && getRetainInstance())
        {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }
}
