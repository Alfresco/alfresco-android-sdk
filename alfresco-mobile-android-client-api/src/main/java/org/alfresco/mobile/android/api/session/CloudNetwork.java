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
package org.alfresco.mobile.android.api.session;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * A network is a group of users and sites that belong to a particular
 * organization. Networks are organized by email domain. When you sign up for an
 * Alfresco account, your email domain becomes your Home Network. If the network
 * already exists for your domain, you're added to it. If the network does not
 * exist, it's created. Colleagues with the same domain are instantly connected
 * to each other when they sign up for an Alfresco account.
 * 
 * @author Jean Marie Pascal
 */
public interface CloudNetwork extends Serializable
{
    /**
     * Returns the identifier of the network.
     */
    String getIdentifier();

    /**
     * Returns the subscription level of of the network, will be one of “Free”,
     * “Standard” or “Enterprise”.
     */
    String getSubscriptionLevel();

    /**
     * Returns true if this network is a “paid for” account.
     */
    boolean isPaidNetwork();

    /**
     * Returns true if this is the current users home network.
     */
    boolean isHomeNetwork();

    /**
     * Returns the time this network was created.
     */
    GregorianCalendar getCreatedAt();

}
