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

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.documentfolder.listener.OnNodeUpdateListener;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.MimeTypeManager;
import org.alfresco.mobile.android.ui.utils.Formatter;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class UpdateNodeDialogFragment extends BaseFragment
{
    public static final String TAG = "UpdateNodeDialogFragment";

    public static final String ARGUMENT_NODE = "node";

    public static final String ARGUMENT_CONTENT_NAME = "contentName";

    public static final String ARGUMENT_CONTENT_DESCRIPTION = "contentDescription";

    public static final String ARGUMENT_CONTENT_TAGS = "contentTags";

    protected OnNodeUpdateListener onUpdateListener;

    private Node node;

    public UpdateNodeDialogFragment()
    {
    }

    public static Bundle createBundle(Node node)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_NODE, node);
        return args;
    }

    @Override
    public void onStart()
    {
        if (node != null)
        {
            getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, MimeTypeManager.getIcon(node.getName()));
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle(R.string.edit_metadata);
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);

        node = (Node) getArguments().getSerializable(ARGUMENT_NODE);

        View v = inflater.inflate(R.layout.sdk_create_content_props, container, false);
        final EditText tv = (EditText) v.findViewById(R.id.content_name);
        final EditText desc = (EditText) v.findViewById(R.id.content_description);
        TextView tsize = (TextView) v.findViewById(R.id.content_size);

        v.findViewById(R.id.tags_line).setVisibility(View.GONE);
        desc.setImeOptions(EditorInfo.IME_ACTION_DONE);

        Button button = (Button) v.findViewById(R.id.cancel);
        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                UpdateNodeDialogFragment.this.dismiss();
            }
        });

        final Button bcreate = (Button) v.findViewById(R.id.create_content);
        bcreate.setText(R.string.update);
        bcreate.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                updateNode(tv, desc, bcreate);
            }
        });

        desc.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    updateNode(tv, desc, bcreate);
                    handled = true;
                }
                return handled;
            }
        });

        if (node != null)
        {
            tv.setText(node.getName());
            if (node.isDocument())
            {
                tsize.setText(Formatter.formatFileSize(getActivity(), ((Document) node).getContentStreamLength()));
                tsize.setVisibility(View.VISIBLE);
            }

            if (RepositoryVersionHelper.isAlfrescoProduct(alfSession)
                    && node.getProperty(ContentModel.PROP_DESCRIPTION) != null
                    && node.getProperty(ContentModel.PROP_DESCRIPTION).getValue() != null)
            {
                desc.setText(node.getProperty(ContentModel.PROP_DESCRIPTION).getValue().toString());
            }

            bcreate.setEnabled(true);

        }
        else
        {
            tsize.setVisibility(View.GONE);
        }

        return v;
    }

    private void updateNode(EditText tv, EditText desc, Button bcreate)
    {
        Map<String, Serializable> props = new HashMap<String, Serializable>(2);
        props.put(ContentModel.PROP_NAME, tv.getText().toString());
        if (desc.getText() != null && desc.getText().length() > 0)
        {
            props.put(ContentModel.PROP_DESCRIPTION, desc.getText().toString());
        }
        UpdateLoaderCallback up = new UpdateLoaderCallback(alfSession, getActivity(), node, props);
        up.setOnUpdateListener(onUpdateListener);
        up.start();
        bcreate.setEnabled(false);
    }
}
