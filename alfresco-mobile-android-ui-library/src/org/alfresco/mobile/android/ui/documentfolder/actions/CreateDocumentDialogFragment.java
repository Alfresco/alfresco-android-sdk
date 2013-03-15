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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.asynchronous.DocumentCreateLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.model.impl.TagImpl;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.documentfolder.listener.OnNodeCreateListener;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.MessengerManager;
import org.alfresco.mobile.android.ui.utils.Formatter;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import android.app.LoaderManager.LoaderCallbacks;
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
import android.widget.TextView;

public abstract class CreateDocumentDialogFragment extends BaseFragment implements
        LoaderCallbacks<LoaderResult<Document>>
{
    public static final String TAG = "CreateContentDialogFragment";

    public static final String ARGUMENT_FOLDER = "folder";

    public static final String ARGUMENT_CONTENT_FILE = "contentFileURI";

    public static final String ARGUMENT_CONTENT_NAME = "contentName";

    public static final String ARGUMENT_CONTENT_DESCRIPTION = "contentDescription";

    public static final String ARGUMENT_CONTENT_TAGS = "contentTags";

    private EditText editTags;

    private List<Tag> selectedTags = new ArrayList<Tag>();

    private OnNodeCreateListener onCreateListener;

    public CreateDocumentDialogFragment()
    {
    }

    public static Bundle createBundle(Folder folder)
    {
        return createBundle(folder, null);
    }

    public static Bundle createBundle(Folder folder, ContentFile f)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_FOLDER, folder);
        args.putSerializable(ARGUMENT_CONTENT_FILE, f);
        return args;
    }

    @Override
    public void onStart()
    {
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.mime_file);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle(R.string.content_upload);
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);

        View v = inflater.inflate(R.layout.sdk_create_content_props, container, false);
        final EditText tv = (EditText) v.findViewById(R.id.content_name);
        final EditText desc = (EditText) v.findViewById(R.id.content_description);
        TextView tsize = (TextView) v.findViewById(R.id.content_size);

        editTags = (EditText) v.findViewById(R.id.content_tags);

        Button button = (Button) v.findViewById(R.id.cancel);
        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                CreateDocumentDialogFragment.this.dismiss();
            }
        });

        final Button bcreate = (Button) v.findViewById(R.id.create_content);
        bcreate.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Bundle b = new Bundle();
                b.putString(ARGUMENT_CONTENT_NAME, tv.getText().toString());
                if (desc.getText() != null && desc.getText().length() > 0)
                {
                    b.putString(ARGUMENT_CONTENT_DESCRIPTION, desc.getText().toString());
                }
                onValidateTags();
                if (selectedTags != null && !selectedTags.isEmpty())
                {
                    ArrayList<String> listTagValue = new ArrayList<String>(selectedTags.size());
                    for (Tag tag : selectedTags)
                    {
                        listTagValue.add(tag.getValue());
                    }
                    b.putStringArrayList(ARGUMENT_CONTENT_TAGS, listTagValue);
                }
                b.putSerializable(ARGUMENT_CONTENT_FILE, getArguments().getSerializable(ARGUMENT_CONTENT_FILE));
                getLoaderManager().initLoader(DocumentCreateLoader.ID, b, CreateDocumentDialogFragment.this);
                getLoaderManager().getLoader(DocumentCreateLoader.ID).forceLoad();
                bcreate.setEnabled(false);
            }
        });

        if (getArguments().getSerializable(ARGUMENT_CONTENT_FILE) != null)
        {
            ContentFile f = (ContentFile) getArguments().getSerializable(ARGUMENT_CONTENT_FILE);
            tv.setText(f.getFileName());
            tsize.setText(Formatter.formatFileSize(getActivity(), f.getLength()));
            tsize.setVisibility(View.VISIBLE);
            bcreate.setEnabled(true);
        }
        else
        {
            tsize.setVisibility(View.GONE);
        }

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
    public Loader<LoaderResult<Document>> onCreateLoader(int id, Bundle args)
    {
        getDialog().hide();
        Map<String, Serializable> props = new HashMap<String, Serializable>(3);
        props.put(ContentModel.PROP_DESCRIPTION, args.getString(ARGUMENT_CONTENT_DESCRIPTION));
        props.put(ContentModel.PROP_TAGS, args.getStringArrayList(ARGUMENT_CONTENT_TAGS));
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        if (onCreateListener != null)
        {
            onCreateListener.beforeContentCreation((Folder) getArguments().get(ARGUMENT_FOLDER),
                    args.getString(ARGUMENT_CONTENT_NAME), props,
                    (ContentFile) args.getSerializable(ARGUMENT_CONTENT_FILE));
        }

        return new DocumentCreateLoader(getActivity(), alfSession, (Folder) getArguments().get(ARGUMENT_FOLDER),
                args.getString(ARGUMENT_CONTENT_NAME), props, (ContentFile) args.getSerializable(ARGUMENT_CONTENT_FILE));
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Document>> loader, LoaderResult<Document> results)
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
                DocumentCreateLoader loaderD = (DocumentCreateLoader) loader;
                onCreateListener.onExeceptionDuringCreation(results.getException(), loaderD.getParentFolder(),
                        loaderD.getDocumentName(), loaderD.getProperties(), loaderD.getContentFile());
            }
            else
            {
                onCreateListener.afterContentCreation(results.getData());
            }
        }
        getDialog().dismiss();
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Document>> arg0)
    {
    }

    public void onValidateTags()
    {
        String s = editTags.getText().toString();
        String[] listValues = s.split(",");
        for (int i = 0; i < listValues.length; i++)
        {
            if (listValues[i] != null && !listValues[i].isEmpty())
            {
                selectedTags.add(new TagImpl(listValues[i].trim()));
            }
        }
    }

    public void setOnCreateListener(OnNodeCreateListener onCreateListener)
    {
        this.onCreateListener = onCreateListener;
    }
}
