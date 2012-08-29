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
package org.alfresco.mobile.android.ui.utils;

import java.util.Date;

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.ui.R;

import android.content.Context;
import android.content.res.Resources;

public class Formatter
{

    /**
     * Format a date into a relative human readable date.
     * 
     * @param c
     * @param date
     * @return
     */
    public static String formatToRelativeDate(Context c, Date date)
    {
        if (date == null) return null;

        Resources res = c.getResources();

        Date todayDate = new Date();
        long ti = (todayDate.getTime() - date.getTime()) / 1000;

        if (ti < 1)
        {
            return res.getString(R.string.relative_date_just_now);
        }
        else if (ti < 60)
        {
            return res.getString(R.string.relative_date_less_than_a_minute_ago);
        }
        else if (ti < 3600)
        {
            int diff = Math.round(ti / 60);
            return String.format(res.getQuantityString(R.plurals.relative_date_minutes_ago, diff), diff);
        }
        else if (ti < 86400)
        {
            int diff = Math.round(ti / 60 / 60);
            return String.format(res.getQuantityString(R.plurals.relative_date_hours_ago, diff), diff);
        }
        else if (ti < 31536000)
        {
            int diff = Math.round(ti / 60 / 60 / 24);
            return String.format(res.getQuantityString(R.plurals.relative_date_days_ago, diff), diff);
        }
        else
        {
            int diff = Math.round(ti / 60 / 60 / 24 / 365);
            return String.format(res.getQuantityString(R.plurals.relative_date_years_ago, diff), diff);
        }
    }

    /**
     * Format a file size in human readable text.
     * 
     * @param context
     * @param sizeInByte
     * @return
     */
    public static String formatFileSize(Context context, long sizeInByte)
    {
        return android.text.format.Formatter.formatShortFileSize(context, sizeInByte);
    }

    /**
     * Create default bottom text for a node.
     * 
     * @param context
     * @param node
     * @return
     */
    public static String createContentBottomText(Context context, Node node)
    {
        return createContentBottomText(context, node, false);
    }

    public static String createContentBottomText(Context context, Node node, boolean extended)
    {
        StringBuilder s = new StringBuilder();
        if (node.getCreatedAt() != null)
        {
            s.append(Formatter.formatToRelativeDate(context, node.getCreatedAt().getTime()));
            if (node.isDocument())
            {
                Document doc = (Document) node;
                s.append(" - ");
                s.append(Formatter.formatFileSize(context, doc.getContentStreamLength()));

                if (extended)
                {
                    s.append(" - V:");
                    if ("0.0".equals(((Document) node).getVersionLabel()))
                    {
                        s.append("1.0");
                    }
                    else
                    {
                        s.append(((Document) node).getVersionLabel());
                    }
                }
            }
        }
        return s.toString();
    }
}
