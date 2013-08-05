/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.model.impl.publicapi;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;

public final class PublicAPIPropertyIds
{
    private PublicAPIPropertyIds() {
    }
 
    // ---- base ----
    public static final String TYPE = PublicAPIConstant.TYPE_VALUE;
    public static final String NAME = PublicAPIConstant.NAME_VALUE;
    public static final String ID = PublicAPIConstant.ID_VALUE;
    public static final String GUID = PublicAPIConstant.GUID_VALUE;
    public static final String TITLE = PublicAPIConstant.TITLE_VALUE;
    public static final String DESCRIPTION = PublicAPIConstant.DESCRIPTION_VALUE;
    public static final String CREATEDBY = PublicAPIConstant.CREATEDBY_VALUE;
    public static final String CREATEDAT = PublicAPIConstant.CREATEDAT_VALUE;
    public static final String MODIFIEDBY = PublicAPIConstant.MODIFIEDBY_VALUE;
    public static final String MODIFIEDAT = PublicAPIConstant.MODIFIEDAT_VALUE;
    
    // ---- document ----
    public static final String VERSIONLABEL = PublicAPIConstant.VERSIONLABEL_VALUE;
    public static final String SIZEINBYTES = PublicAPIConstant.SIZEINBYTES_VALUE;
    public static final String MIMETYPE = PublicAPIConstant.MIMETYPE_VALUE;
    
    // ---- extra ----
    public static final String REQUEST_STATUS = "request_status";
    public static final String REQUEST_TYPE = "request_type";

}
