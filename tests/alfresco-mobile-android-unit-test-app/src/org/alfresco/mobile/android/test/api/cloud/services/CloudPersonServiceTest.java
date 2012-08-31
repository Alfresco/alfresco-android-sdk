package org.alfresco.mobile.android.test.api.cloud.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.test.AlfrescoSDKCloudTestCase;
import org.alfresco.mobile.android.test.api.services.PersonServiceTest;

public class CloudPersonServiceTest extends PersonServiceTest
{
    

    protected void initSession()
    {
        alfsession = AlfrescoSDKCloudTestCase.createCloudSession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        personService = alfsession.getServiceRegistry().getPersonService();
        Assert.assertNotNull(personService);
    }
}
