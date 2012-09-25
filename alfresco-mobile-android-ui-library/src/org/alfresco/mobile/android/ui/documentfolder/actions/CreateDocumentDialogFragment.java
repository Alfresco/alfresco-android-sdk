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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.alfresco.mobile.android.api.asynchronous.DocumentCreateLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.documentfolder.listener.OnNodeCreateListener;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.tag.actions.TagPickerDialogFragment;
import org.alfresco.mobile.android.ui.tag.actions.TagPickerDialogFragment.onTagPickerListener;
import org.alfresco.mobile.android.ui.utils.ContentFileProgressImpl;
import org.alfresco.mobile.android.ui.utils.Formatter;
import org.alfresco.mobile.android.ui.utils.ProgressNotification;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CreateDocumentDialogFragment extends BaseFragment implements LoaderCallbacks<LoaderResult<Document>>, Observer
{
    public static final String TAG = "CreateContentDialogFragment";

    public static final String ARGUMENT_FOLDER = "folder";

    public static final String ARGUMENT_CONTENT_FILE = "contentFileURI";

    public static final String ARGUMENT_CONTENT_NAME = "contentName";

    public static final String ARGUMENT_CONTENT_DESCRIPTION = "contentDescription";

    public static final String ARGUMENT_CONTENT_TAGS = "contentTags";

    private EditText editTags;

    private ArrayList<Tag> selectedTags;

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
        getDialog().setTitle(R.string.content_create);
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);

        View v = inflater.inflate(R.layout.sdk_create_content_props, container, false);
        final EditText tv = (EditText) v.findViewById(R.id.content_name);
        final EditText desc = (EditText) v.findViewById(R.id.content_description);
        TextView tsize = (TextView) v.findViewById(R.id.content_size);

        editTags = (EditText) v.findViewById(R.id.content_tags);

        ImageButton ib = (ImageButton) v.findViewById(R.id.pick_tag);
        ib.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                createPickTag();
            }
        });

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
                if (selectedTags != null && !selectedTags.isEmpty())
                {
                    ArrayList<String> listTagValue = new ArrayList<String>(selectedTags.size());
                    for (Tag tag : selectedTags)
                    {
                        listTagValue.add(tag.getValue());
                    }
                    b.putStringArrayList(ARGUMENT_CONTENT_TAGS, listTagValue);
                }
                
                if (getArguments().getSerializable(ARGUMENT_CONTENT_FILE) != null)
                {
                    //Initiate progress notification
                
                    Bundle progressBundle = new Bundle();
                    ContentFile f = (ContentFile) getArguments().getSerializable(ARGUMENT_CONTENT_FILE);
                    
                    if (f.getClass() == ContentFileProgressImpl.class)
                    {
                        ((ContentFileProgressImpl)f).setFilename (tv.getText().toString());
                        progressBundle.putString ("name", tv.getText().toString());
                    }
                    else
                        progressBundle.putString ("name", f.getFile().getName());
                    
                    progressBundle.putInt ("dataSize", (int) f.getFile().length());
                    progressBundle.putInt ("dataIncrement", (int) (f.getFile().length() / 10));
                    
                    ProgressNotification.createProgressNotification (getActivity(), progressBundle);
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
            onCreateListener.beforeContentCreation(args.getString(ARGUMENT_CONTENT_NAME));
        }
        
        return new DocumentCreateLoader(getActivity(), alfSession, (Folder) getArguments().get(ARGUMENT_FOLDER),
                args.getString(ARGUMENT_CONTENT_NAME), props, (ContentFile) args.getSerializable(ARGUMENT_CONTENT_FILE));
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Document>> arg0, LoaderResult<Document> content)
    {
        if (onCreateListener != null)
        {
            onCreateListener.afterContentCreation(content.getData());
        }
        getDialog().dismiss();
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Document>> arg0)
    {
    }

    @TargetApi(13)
    public void createPickTag()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TagPickerDialogFragment.TAG);
        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        TagPickerDialogFragment newFragment = TagPickerDialogFragment.newInstance(alfSession, selectedTags);
        newFragment.setOnTagPickerListener(new onTagPickerListener()
        {
            @Override
            public void onValidateTags(List<Tag> tags)
            {
                selectedTags = (ArrayList<Tag>) tags;
                String s = "";
                for (int i = 0; i < tags.size(); i++)
                {
                    if (i == 0)
                    {
                        s = tags.get(i).getValue();
                    }
                    else
                    {
                        s += "," + tags.get(i).getValue();
                    }
                }
                editTags.setText(s);
            }
        });

        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        newFragment.show(ft, TagPickerDialogFragment.TAG);
    }

    public void setOnCreateListener(OnNodeCreateListener onCreateListener)
    {
        this.onCreateListener = onCreateListener;
    }

    @Override
    public void update (Observable observable, Object data)
    {
        // TODO Auto-generated method stub
        
    }
}
