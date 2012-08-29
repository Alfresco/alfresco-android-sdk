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
package org.alfresco.mobile.android.ui.comment;

import java.util.List;

import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListAdapter;
import org.alfresco.mobile.android.ui.manager.RenditionManager;
import org.alfresco.mobile.android.ui.utils.ViewHolder;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Provides access to comments and displays them as a view based on
 * GenericViewHolder.
 * 
 * @author Jean Marie Pascal
 */
public class CommentAdapter extends BaseListAdapter<Comment, GenericViewHolder>
{
    private RenditionManager renditionManager;

    public CommentAdapter(Activity context, AlfrescoSession session, int textViewResourceId, List<Comment> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.renditionManager = new RenditionManager(context, session);
        this.vhClassName = GenericViewHolder.class.getCanonicalName();
    }

    @Override
    protected void updateTopText(GenericViewHolder vh, Comment item)
    {
        vh.topText.setText(item.getCreatedBy());
    }

    @Override
    protected void updateBottomText(GenericViewHolder vh, Comment item)
    {
        vh.bottomText.setText(formatDate(getContext(), item.getCreatedAt().getTime()));
        if (vh.content != null)
        {
            vh.content.setText(Html.fromHtml(item.getContent().trim()));
        }
    }

    @Override
    protected void updateIcon(GenericViewHolder vh, Comment item)
    {
        renditionManager.display(vh.icon, item.getCreatedBy(), R.drawable.ic_menu_start_conversation);
    }
}

final class GenericViewHolder extends ViewHolder
{
    public TextView topText;

    public TextView bottomText;

    public ImageView icon;

    public TextView content;

    public GenericViewHolder(View v)
    {
        super(v);
        icon = (ImageView) v.findViewById(R.id.icon);
        topText = (TextView) v.findViewById(R.id.toptext);
        bottomText = (TextView) v.findViewById(R.id.bottomtext);
        content = (TextView) v.findViewById(R.id.contentweb);
    }
}
