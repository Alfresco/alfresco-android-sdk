package org.alfresco.mobile.android.test.api.cloud.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.test.AlfrescoSDKCloudTestCase;
import org.alfresco.mobile.android.test.api.services.SearchServiceTest;

public class CloudSearchServiceTest extends SearchServiceTest
{
    protected void initSession()
    {
        alfsession = AlfrescoSDKCloudTestCase.createCloudSession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        searchService = alfsession.getServiceRegistry().getSearchService();
        Assert.assertNotNull(searchService);
    }
    
    
}
