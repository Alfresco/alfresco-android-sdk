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
package org.alfresco.mobile.android.api.model;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;

/**
 * Provides constant for sorting.</br>
 * 
 * @author Jean Marie Pascal
 * @see ListingContext
 */
public interface Sorting
{
    /** Sorting based on the name of the object. */
    public static final String NAME = ContentModel.PROP_NAME;

    /** Sorting based on the name of the object. */
    public static final String SHORTNAME = OnPremiseConstant.SHORTNAME_VALUE;

    /** Sorting based on the name of the object. */
    public static final String TITLE = ContentModel.PROP_TITLE;

    /** Sorting based on the name of the object. */
    public static final String DESCRIPTION = ContentModel.PROP_DESCRIPTION;

    /** Sorting based on the creation date of the object. */
    public static final String CREATED_AT = ContentModel.PROP_CREATED;

    /** Sorting based on the modification date of the object. */
    public static final String MODIFIED_AT = ContentModel.PROP_MODIFIED;

}
