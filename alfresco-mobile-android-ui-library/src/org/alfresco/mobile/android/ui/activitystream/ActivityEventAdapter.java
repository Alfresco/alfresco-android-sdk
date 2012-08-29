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
package org.alfresco.mobile.android.ui.activitystream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListAdapter;
import org.alfresco.mobile.android.ui.manager.MimeTypeManager;
import org.alfresco.mobile.android.ui.manager.RenditionManager;
import org.alfresco.mobile.android.ui.utils.GenericViewHolder;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;

/**
 * Provides access to activity entries and displays them as a view based on
 * GenericViewHolder.
 * 
 * @author Jean Marie Pascal
 */
public class ActivityEventAdapter extends BaseListAdapter<ActivityEntry, GenericViewHolder>
{

    private RenditionManager renditionManager;

    public ActivityEventAdapter(Activity context, AlfrescoSession session, int textViewResourceId,
            List<ActivityEntry> listItems)
    {
        super(context, textViewResourceId, listItems);
        this.vhClassName = GenericViewHolder.class.getCanonicalName();
        this.renditionManager = new RenditionManager(context, session);
    }

    @Override
    protected void updateTopText(GenericViewHolder vh, ActivityEntry item)
    {
        vh.topText.setMaxLines(3);
        vh.topText.setTypeface(Typeface.DEFAULT);
        vh.topText.setText(Html.fromHtml(getActivityTypeMessage(item)));
    }

    @Override
    protected void updateBottomText(GenericViewHolder vh, ActivityEntry item)
    {
        String s = "";
        if (item.getCreatedAt() != null)
        {
            s = formatDate(getContext(), item.getCreatedAt().getTime());
        }
        vh.bottomText.setText(s);
    }

    @Override
    protected void updateIcon(GenericViewHolder vh, ActivityEntry item)
    {
        switch (iconItemType)
        {
            case DISPLAY_ICON_NONE:
                vh.icon.setVisibility(View.GONE);
                break;
            case DISPLAY_ICON_DEFAULT:
                vh.icon.setImageDrawable(getContext().getResources().getDrawable(getFileDrawableId(item)));
                break;
            case DISPLAY_ICON_CREATOR:
                renditionManager.display(vh.icon, item.getCreatedBy(), getFileDrawableId(item));
                break;
            default:
                break;
        }
    }

    private int getFileDrawableId(ActivityEntry item)
    {
        int drawable = R.drawable.ic_menu_notifications;
        String s = item.getType();

        if (s.startsWith(PREFIX_FILE))
            drawable = MimeTypeManager.getIcon(item.getData(OnPremiseConstant.TITLE_VALUE));
        else
        {
            for (Entry<String, Integer> icon : eventIcon.entrySet())
            {
                if (s.startsWith(icon.getKey()))
                {
                    drawable = icon.getValue();
                    break;
                }
            }
        }
        return drawable;
    }

    public static final String PREFIX_LINK = "org.alfresco.links.link";

    public static final String PREFIX_EVENT = "org.alfresco.calendar.event";

    public static final String PREFIX_WIKI = "org.alfresco.wiki.page";

    public static final String PREFIX_FILE = "org.alfresco.documentlibrary.file";

    public static final String PREFIX_USER = "org.alfresco.site.user";

    public static final String PREFIX_DATALIST = "org.alfresco.datalists.list";

    public static final String PREFIX_DISCUSSIONS = "org.alfresco.discussions";

    public static final String PREFIX_FOLDER = "org.alfresco.documentlibrary.folder";

    public static final String PREFIX_COMMENT = "org.alfresco.comments.comment";

    public static final String PREFIX_BLOG = "org.alfresco.blog";

    // TODO Constant Manager ?
    @SuppressWarnings("serial")
    private static Map<String, Integer> eventIcon = new HashMap<String, Integer>()
    {
        {
            put(PREFIX_LINK, R.drawable.ic_menu_share);
            put(PREFIX_EVENT, R.drawable.ic_menu_today);
            put(PREFIX_WIKI, R.drawable.ic_menu_notifications);
            put(PREFIX_USER, R.drawable.ic_avatar);
            put(PREFIX_DATALIST, R.drawable.ic_menu_notifications);
            put(PREFIX_DISCUSSIONS, R.drawable.ic_menu_start_conversation);
            put(PREFIX_FOLDER, R.drawable.ic_menu_archive);
            put(PREFIX_COMMENT, R.drawable.ic_menu_start_conversation);
            put(PREFIX_BLOG, R.drawable.ic_menu_notifications);
        }
    };

    //
    private static final String PARAM_TITLE = "{0}";

    private static final String PARAM_USER_PROFILE = "{1}";

    private static final String PARAM_CUSTOM = "{2}";

    private static final String PARAM_SITE_LINK = "{4}";

    private static final String PARAM_STATUS = "{6}";

    private String getActivityTypeMessage(ActivityEntry item)
    {
        String s = item.getType();
        try
        {
            if (map.get(s) != null)
            {
                s = getContext().getResources().getString(map.get(item.getType()));

                if (s.contains(PARAM_CUSTOM))
                {
                    s = s.replace(PARAM_CUSTOM, item.getData(OnPremiseConstant.ROLE_VALUE));
                    s = s.replace(PARAM_USER_PROFILE, "<b>" + item.getData(OnPremiseConstant.MEMEBERFIRSTNAME_VALUE)
                            + " " + item.getData(OnPremiseConstant.MEMBERLASTNAME_VALUE) + "</b>");
                }
                else
                {
                    s = s.replace(PARAM_USER_PROFILE, "<b>" + item.getData(OnPremiseConstant.FIRSTNAME_VALUE) + " "
                            + item.getData(OnPremiseConstant.LASTNAME_VALUE) + "</b>");
                }

                if (s.contains(PARAM_TITLE))
                {
                    s = s.replace(PARAM_TITLE, "<b>" + item.getData(OnPremiseConstant.TITLE_VALUE) + "</b>");
                }

                if (s.contains(PARAM_SITE_LINK))
                {
                    s = s.replace(PARAM_SITE_LINK, item.getSiteShortName());
                }

                if (s.contains(PARAM_STATUS))
                {
                    s = s.replace(PARAM_STATUS, item.getData(OnPremiseConstant.STATUS_VALUE));
                }
            }
        }
        catch (Exception e)
        {
        }

        return s;
    }

    // TODO Constant Manager ?
    @SuppressWarnings("serial")
    private static Map<String, Integer> map = new HashMap<String, Integer>()
    {
        {
            put("org.alfresco.blog.post-created", R.string.org_alfresco_blog_post_created);
            put("org.alfresco.blog.post-updated", R.string.org_alfresco_blog_post_updated);
            put("org.alfresco.blog.post-deleted", R.string.org_alfresco_blog_post_deleted);
            put("org.alfresco.comments.comment-created", R.string.org_alfresco_comments_comment_created);
            put("org.alfresco.comments.comment-updated", R.string.org_alfresco_comments_comment_updated);
            put("org.alfresco.comments.comment-deleted", R.string.org_alfresco_comments_comment_deleted);
            put("org.alfresco.discussions.post-created", R.string.org_alfresco_discussions_post_created);
            put("org.alfresco.discussions.post-updated", R.string.org_alfresco_discussions_post_updated);
            put("org.alfresco.discussions.post-deleted", R.string.org_alfresco_discussions_post_deleted);
            put("org.alfresco.discussions.reply-created", R.string.org_alfresco_discussions_reply_created);
            put("org.alfresco.discussions.reply-updated", R.string.org_alfresco_discussions_reply_updated);
            put("org.alfresco.calendar.event-created", R.string.org_alfresco_calendar_event_created);
            put("org.alfresco.calendar.event-updated", R.string.org_alfresco_calendar_event_updated);
            put("org.alfresco.calendar.event-deleted", R.string.org_alfresco_calendar_event_deleted);
            put("org.alfresco.documentlibrary.file-added", R.string.org_alfresco_documentlibrary_file_added);
            put("org.alfresco.documentlibrary.files-added", R.string.org_alfresco_documentlibrary_files_added);
            put("org.alfresco.documentlibrary.file-created", R.string.org_alfresco_documentlibrary_file_created);
            put("org.alfresco.documentlibrary.file-deleted", R.string.org_alfresco_documentlibrary_file_deleted);
            put("org.alfresco.documentlibrary.files-deleted", R.string.org_alfresco_documentlibrary_files_deleted);
            put("org.alfresco.documentlibrary.file-updated", R.string.org_alfresco_documentlibrary_file_updated);
            put("org.alfresco.documentlibrary.files-updated", R.string.org_alfresco_documentlibrary_files_updated);
            put("org.alfresco.documentlibrary.google-docs-checkout",
                    R.string.org_alfresco_documentlibrary_google_docs_checkout);
            put("org.alfresco.documentlibrary.google-docs-checkin",
                    R.string.org_alfresco_documentlibrary_google_docs_checkin);
            put("org.alfresco.documentlibrary.inline-edit", R.string.org_alfresco_documentlibrary_inline_edit);
            put("org.alfresco.documentlibrary.file-liked", R.string.org_alfresco_documentlibrary_file_liked);
            put("org.alfresco.documentlibrary.folder-liked", R.string.org_alfresco_documentlibrary_folder_liked);
            put("org.alfresco.wiki.page-created", R.string.org_alfresco_wiki_page_created);
            put("org.alfresco.wiki.page-edited", R.string.org_alfresco_wiki_page_edited);
            put("org.alfresco.wiki.page-renamed", R.string.org_alfresco_wiki_page_renamed);
            put("org.alfresco.wiki.page-deleted", R.string.org_alfresco_wiki_page_deleted);
            put("org.alfresco.site.group-added", R.string.org_alfresco_site_group_added);
            put("org.alfresco.site.group-removed", R.string.org_alfresco_site_group_removed);
            put("org.alfresco.site.group-role_changed", R.string.org_alfresco_site_group_role_changed);
            put("org.alfresco.site.user-joined", R.string.org_alfresco_site_user_joined);
            put("org.alfresco.site.user-left", R.string.org_alfresco_site_user_left);
            put("org.alfresco.site.user-role-changed", R.string.org_alfresco_site_user_role_changed);
            put("org.alfresco.links.link-created", R.string.org_alfresco_links_link_created);
            put("org.alfresco.links.link-updated", R.string.org_alfresco_links_link_updated);
            put("org.alfresco.links.link-deleted", R.string.org_alfresco_links_link_deleted);
            put("org.alfresco.datalists.list-created", R.string.org_alfresco_datalists_list_created);
            put("org.alfresco.datalists.list-updated", R.string.org_alfresco_datalists_list_updated);
            put("org.alfresco.datalists.list-deleted", R.string.org_alfresco_datalists_list_deleted);
            put("org.alfresco.subscriptions.followed", R.string.org_alfresco_subscriptions_followed);
            put("org.alfresco.subscriptions.subscribed", R.string.org_alfresco_subscriptions_subscribed);
            put("org.alfresco.profile.status-changed", R.string.org_alfresco_profile_status_changed);
        }
    };
}
