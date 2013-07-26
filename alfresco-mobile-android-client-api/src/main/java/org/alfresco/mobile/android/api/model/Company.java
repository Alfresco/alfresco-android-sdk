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
package org.alfresco.mobile.android.api.model;


/**
 * @since 1.3.0
 * @author Jean Marie Pascal
 */
public interface Company
{

    /**
     * Returns the name of the company. Returns null if not available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getName();

    /**
     * Returns the first line of the company adress. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getAddress1();

    /**
     * Returns the second line of the company adress. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getAddress2();

    /**
     * Returns the third line of the company adress. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getAddress3();

    /**
     * Returns the adress post code company. Returns null if not available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getPostCode();

    /**
     * Returns the telephone number of the company. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getTelephoneNumber();

    /**
     * Returns the fax number of the company. Returns null if not available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getFaxNumber();

    /**
     * Returns the email of the company. Returns null if not available.
     * 
     * @since 1.3.0
     * @return
     */
    public String getEmail();
    
    /**
     * Returns the full address of the company. Null if nothing.
     * 
     * @since 1.3.0
     * @return
     */
    public String getFullAddress();
    
    
}
