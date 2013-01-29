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
package org.alfresco.mobile.android.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * List of static methods to manage date.
 * 
 * @author Jean Marie Pascal
 */
public final class DateUtils
{

    private DateUtils()
    {

    }

    public static final String FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String FORMAT_4 = "MMM dd yyyy HH:mm:ss zzzz";

    public static final String FORMAT_5 = "dd MMM yyyy HH:mm:ss zzzz";

    private static final String[] DATE_FORMATS = { FORMAT_1, FORMAT_2, FORMAT_3, FORMAT_4, FORMAT_5 };

    public static Date parseJsonDate(String jsonDate)
    {
        Date d = null;
        if (jsonDate != null)
        {
            d = parseDate(jsonDate, FORMAT_4);
            if (d == null)
            {
                d = parseDate(jsonDate);
            }
        }
        return d;
    }

    public static Date parseDate(String atomPubDate)
    {
        return parseDate(atomPubDate, Locale.getDefault());
    }

    /**
     * @since 1.0.1
     * @param atomPubDate
     * @return
     */
    public static Date parseDate(String atomPubDate, Locale locale)
    {
        Date d = null;
        SimpleDateFormat sdf;
        for (int i = 0; i < DATE_FORMATS.length; i++)
        {
            sdf = new SimpleDateFormat(DATE_FORMATS[i], locale);
            sdf.setLenient(true);
            try
            {
                d = sdf.parse(atomPubDate);
                break;
            }
            catch (ParseException e)
            {
                continue;
            }
        }

        return d;
    }

    public static Date parseDate(String date, String format)
    {
        return parseDate(date, new SimpleDateFormat(format, Locale.getDefault()));
    }

    /**
     * @since 1.0.1
     * @param atomPubDate
     * @return
     */
    public static Date parseDate(String date, SimpleDateFormat sdf)
    {
        Date d = null;
        sdf.setLenient(true);
        try
        {
            d = sdf.parse(date);
        }
        catch (ParseException e)
        {
            d = parseDate(date);
        }
        return d;
    }
}
