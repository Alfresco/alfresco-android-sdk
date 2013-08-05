package org.alfresco.mobile.android.test.publicapi.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.services.impl.AbstractPersonService;
import org.alfresco.mobile.android.test.api.services.PersonServiceTest;

public class PublicAPIPersonServiceTest extends PersonServiceTest
{
    /**
     * Failure Tests for getAvatarStream Method.
     */
    public void testGetAvatarStreamFailure()
    {
        try
        {
            ((AbstractPersonService) personService).getAvatarStream(null);
            Assert.fail("null personIdentifier return an object");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            ((AbstractPersonService) personService).getAvatarStream("FAKE");
            Assert.fail("Fake personIdentifier return an object");
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.PERSON_NOT_FOUND, e.getErrorCode());
        }
    }
}
