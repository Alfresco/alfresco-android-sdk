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
package org.alfresco.mobile.android.ui.properties;

import java.util.GregorianCalendar;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PropertyType;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.ActionManager;
import org.alfresco.mobile.android.ui.manager.MimeTypeManager;
import org.alfresco.mobile.android.ui.manager.PropertyManager;
import org.alfresco.mobile.android.ui.manager.RenditionManager;
import org.alfresco.mobile.android.ui.utils.Formatter;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class PropertiesFragment extends BaseFragment
{

    public static final String TAG = "PropertiesFragment";

    public static final String ARGUMENT_NODE = "node";

    protected Node node;

    protected RenditionManager renditionManager;

    public PropertiesFragment()
    {
        // TODO Auto-generated constructor stub
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
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null) { return null; }
        View v = inflater.inflate(R.layout.sdk_details, container, false);

        node = (Node) getArguments().get(ARGUMENT_NODE);
        renditionManager = new RenditionManager(getActivity(), alfSession);

        // Header
        TextView tv = (TextView) v.findViewById(R.id.title);
        tv.setText(node.getName());
        tv = (TextView) v.findViewById(R.id.details);
        tv.setText(Formatter.createContentBottomText(getActivity(), node, true));

        // Preview
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        int iconId = R.drawable.mime_folder;
        if (node.isDocument())
        {
            iconId = MimeTypeManager.getIcon(node.getName());
            renditionManager.display(iv, node, iconId);
        }
        else
        {
            iv.setImageResource(iconId);
        }

        // Description
        tv = (TextView) v.findViewById(R.id.description);
        if (node.getDescription() != null && node.getDescription().length() > 0)
        {
            tv.setVisibility(View.VISIBLE);
            tv.setText(node.getDescription());
        }
        else
        {
            tv.setVisibility(View.GONE);
        }

        // ASPECTS
        ViewGroup parent = (ViewGroup) v.findViewById(R.id.metadata);
        parent.removeAllViews();

        createAspectPanel(inflater, parent, node, ContentModel.ASPECT_GENERAL, null, null, false);
        createAspectPanel(inflater, parent, node, ContentModel.ASPECT_GEOGRAPHIC, R.drawable.ic_location,
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ActionManager.actionShowMap(PropertiesFragment.this, node.getName(),
                                node.getProperty(ContentModel.PROP_LATITUDE).getValue().toString(),
                                node.getProperty(ContentModel.PROP_LONGITUDE).getValue().toString());
                    }
                });
        createAspectPanel(inflater, parent, node, ContentModel.ASPECT_EXIF, null, null);
        createAspectPanel(inflater, parent, node, ContentModel.ASPECT_AUDIO, null, null);

        return v;
    }

    protected void createAspectPanel(LayoutInflater inflater, ViewGroup parentview, Node node, String aspect,
            Integer iconId, OnClickListener clikListener, boolean check)
    {
        if (!check || node.hasAspect(aspect))
        {
            View v = null;
            TextView tv = null;
            Button b = null;

            ViewGroup grouprootview = (ViewGroup) inflater.inflate(R.layout.sdk_property_title, null);
            tv = (TextView) grouprootview.findViewById(R.id.title);
            tv.setText(PropertyManager.getAspectLabel(aspect));

            b = (Button) grouprootview.findViewById(R.id.title_action);
            if (iconId != null)
            {
                b.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(iconId), null, null);
                b.setOnClickListener(clikListener);
            }
            else
            {
                b.setVisibility(View.GONE);
            }

            ViewGroup groupview = (ViewGroup) grouprootview.findViewById(R.id.group_panel);
            for (Entry<String, Integer> map : PropertyManager.getPropertyLabel(aspect).entrySet())
            {
                if (node.getProperty(map.getKey()) != null && node.getProperty(map.getKey()).getValue() != null)
                {
                    v = inflater.inflate(R.layout.sdk_property_row, null);
                    tv = (TextView) v.findViewById(R.id.propertyName);
                    tv.setText(map.getValue());
                    tv = (TextView) v.findViewById(R.id.propertyValue);
                    if (PropertyType.DATETIME.equals(node.getProperty(map.getKey()).getType()))
                    {
                        tv.setText(DateFormat.getTimeFormat(getActivity()).format(
                                ((GregorianCalendar) node.getProperty(map.getKey()).getValue()).getTime()));
                    }
                    else
                    {
                        tv.setText(node.getProperty(map.getKey()).getValue().toString());
                    }
                    groupview.addView(v);
                }
            }
            parentview.addView(grouprootview);
        }
    }

    protected void createAspectPanel(LayoutInflater inflater, ViewGroup parentview, Node node, String aspect,
            Integer iconId, OnClickListener clikListener)
    {
        createAspectPanel(inflater, parentview, node, aspect, iconId, clikListener, true);
    }
}
