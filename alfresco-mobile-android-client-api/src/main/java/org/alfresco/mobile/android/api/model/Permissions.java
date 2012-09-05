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

import java.io.Serializable;

/**
 * Permissions represent the actions a person can perform on a node.
 * 
 * @author Jean Marie Pascal
 */
public interface Permissions extends Serializable
{

    /**
     * Returns Determines whether the current user can edit the node.
     */
    boolean canEdit();

    /**
     * Returns Determines whether the current user can delete the node.
     */
    boolean canDelete();

    /**
     * Returns Determines whether the current user can add children to the node.
     */
    boolean canAddChildren();

    /**
     * Returns Determines whether the current user can comment on the node.
     */
    boolean canComment();

}
