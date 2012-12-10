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
package org.alfresco.mobile.android.samples.ui.properties;

import java.io.File;

import org.alfresco.mobile.android.api.asynchronous.DownloadTask;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.impl.DocumentImpl;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.activity.MainActivity;
import org.alfresco.mobile.android.samples.ui.documentfolder.actions.DownloadTaskCallback;
import org.alfresco.mobile.android.samples.ui.documentfolder.actions.IsLikedLoaderCallBack;
import org.alfresco.mobile.android.samples.utils.MenuActionItem;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.MimeTypeManager;
import org.alfresco.mobile.android.ui.manager.RenditionManager;
import org.apache.chemistry.opencmis.commons.enums.Action;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsFragment extends BaseFragment
{

    public static final String TAG = "DetailsFragment";

    public static final String ARGUMENT_NODE = "node";

    protected Node node;

    protected RenditionManager renditionManager;

    private MenuItem likeMenuItem;

    public DetailsFragment()
    {
        // TODO Auto-generated constructor stub
    }

    public static DetailsFragment newInstance(Node n)
    {
        DetailsFragment bf = new DetailsFragment();
        bf.setArguments(createBundleArgs(n));
        return bf;
    }

    public static Bundle createBundleArgs(Node node)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_NODE, node);
        return args;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        alfSession = SessionUtils.getsession(getActivity());
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart()
    {
        UIUtils.setFragmentTitle(getActivity(), node.getName());
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        alfSession = SessionUtils.getsession(getActivity());

        if (container == null) { return null; }
        View v = inflater.inflate(R.layout.sdk_custom_properties, container, false);

        node = (Node) getArguments().get(ARGUMENT_NODE);
        renditionManager = new RenditionManager(getActivity(), alfSession);

        // Header
        TextView tv = (TextView) v.findViewById(R.id.name);
        tv.setText(node.getName());
        tv = (TextView) v.findViewById(R.id.version);
        if (node.isDocument())
        {
            tv.setVisibility(View.VISIBLE);
            // Hardcoded... doesnt work with CMIS only
            if ("v0.0".equals(((Document) node).getVersionLabel()))
            {
                tv.setText("v1.0");
            }
            else
            {
                tv.setText("v" + ((Document) node).getVersionLabel());
            }
        }
        else
        {
            tv.setVisibility(View.GONE);
        }

        // Preview / Icon
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        iv.setImageResource(MimeTypeManager.getIcon(node.getName()));
        if (alfSession != null && node.isDocument() && ((Document) node).getContentStreamLength() > 0)
        {
            renditionManager.display(iv, node, MimeTypeManager.getIcon(node.getName()));
        }

        // PROPERTIES
        ViewGroup grouprootview = (ViewGroup) v.findViewById(R.id.properties);
        addRow(inflater, grouprootview, R.string.metadata_prop_title, node.getTitle());
        addRow(inflater, grouprootview, R.string.metadata_prop_description, node.getDescription());
        addRow(inflater, grouprootview, R.string.metadata_prop_creator, node.getCreatedBy());
        addRow(inflater, grouprootview, R.string.metadata_prop_creationdate, node.getCreatedAt().getTime()
                .toLocaleString());
        addRow(inflater, grouprootview, R.string.metadata_prop_modifier, node.getModifiedBy());
        addRow(inflater, grouprootview, R.string.metadata_prop_modificationdate, node.getModifiedAt().getTime()
                .toLocaleString());

        Button b = (Button) v.findViewById(R.id.display_comments);
        if (alfSession != null && alfSession.getServiceRegistry().getCommentService() != null)
        {
            b.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((MainActivity) getActivity()).showCommentsFragment(node);
                }
            });
        }
        else
        {
            b.setVisibility(View.GONE);
        }

        b = (Button) v.findViewById(R.id.display_all_versions);
        // TODO Permission
        if (((DocumentImpl) node).hasAllowableAction(Action.CAN_GET_ALL_VERSIONS.value()))
        {
            b.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((MainActivity) getActivity()).showVersionsFragment((Document) node);
                }
            });
        }
        else
        {
            b.setVisibility(View.GONE);
        }

        b = (Button) v.findViewById(R.id.display_all_tags);
        b.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).showTagsFragment((Document) node);
            }
        });

        return v;
    }

    private void addRow(LayoutInflater inflater, ViewGroup grouprootview, Integer title, String value)
    {
        View v = inflater.inflate(R.layout.sdk_property_row, null);
        TextView tv = (TextView) v.findViewById(R.id.propertyName);
        tv.setText(title);
        tv = (TextView) v.findViewById(R.id.propertyValue);
        tv.setText(value);
        grouprootview.addView(v);
    }

    public void openin()
    {
        DownloadTask dlt = new DownloadTask(alfSession, (Document) node, getDownloadFile());
        dlt.setDl(new DownloadTaskCallback(this, (Document) node));
        dlt.execute();
    }

    private File getDownloadFile()
    {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(folder + "/AlfrescoMobileSample", node.getName());
    }

    public void like()
    {
        IsLikedLoaderCallBack lcb = new IsLikedLoaderCallBack(alfSession, getActivity(), node);
        lcb.setMenuItem(likeMenuItem);
        lcb.execute(true);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////

    public void getMenu(Menu menu)
    {
        MenuItem mi;

        if (node.isDocument() && (((DocumentImpl) node).hasAllowableAction(Action.CAN_GET_CONTENT_STREAM.value())))
        {
            mi = menu.add(Menu.NONE, MenuActionItem.OPEN_IN, Menu.FIRST + MenuActionItem.OPEN_IN,
                    R.string.download);
            mi.setIcon(R.drawable.ic_download);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        if (alfSession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
        {
            mi = menu.add(Menu.NONE, MenuActionItem.LIKE, Menu.FIRST + MenuActionItem.LIKE, R.string.like);
            mi.setIcon(R.drawable.ic_unlike);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            likeMenuItem = mi;

            IsLikedLoaderCallBack lcb = new IsLikedLoaderCallBack(alfSession, getActivity(), node);
            lcb.setMenuItem(likeMenuItem);
            lcb.execute(false);
        }
    }
}
