package org.alfresco.mobile.android.test.api.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.impl.PersonImpl;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.services.impl.AbstractPersonService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

/**
 * Test class for PersonService.
 * 
 * @author Jean Marie Pascal
 */
public class PersonServiceTest extends AlfrescoSDKTestCase
{
    protected PersonService personService;

    // TODO
    // Need to create a user without Avatar
    // Need to create a user with foreign character
    // Need to create a user with avatar based on gif image
    // Need to create a user outside the network

    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        personService = alfsession.getServiceRegistry().getPersonService();
        Assert.assertNotNull(personService);
    }

    /**
     * Success Test for getPerson / getAvatar & Person methods
     */
    public void testGetPerson()
    {
        Person p = personService.getPerson(alfsession.getPersonIdentifier());

        Assert.assertNotNull(p);
        Assert.assertEquals(alfsession.getPersonIdentifier(), p.getIdentifier());
        Assert.assertNotNull(p.getFirstName());
        Assert.assertNotNull(p.getLastName());
        Assert.assertNotNull(p.getFullName());

        if (p.getAvatarIdentifier() != null)
        {
            Assert.assertNotNull(p.getAvatarIdentifier());
        }

        ContentFile avatar = personService.getAvatar(p);
        Assert.assertNotNull(avatar.getFile());
    }

    /**
     * Failure Tests for getPerson Method.
     */
    public void testGetPersonFailure()
    {
        try
        {
            personService.getPerson(null);
            Assert.fail("null personIdentifier return an object");
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_INVALID_ARG, e.getErrorCode());
        }

        try
        {
            personService.getPerson("FAKE");
            Assert.fail("Fake personIdentifier return an object");
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.PERSON_NOT_FOUND, e.getErrorCode());
        }
    }

    /**
     * Failure Tests for getAvatar Method.
     */
    public void testGetAvatarFailure()
    {
        try
        {
            personService.getAvatar(null);
            Assert.fail("null personIdentifier return an object");
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_INVALID_ARG, e.getErrorCode());
        }

        try
        {
            personService.getAvatar(new PersonImpl());
            Assert.fail("Fake personIdentifier return an object");
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_INVALID_ARG, e.getErrorCode());
        }
    }

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
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_INVALID_ARG, e.getErrorCode());
        }

        // Specific behaviour for Alfresco V4 that doesn't require to check the
        // person
        if (alfsession.getRepositoryInfo().getMajorVersion() < OnPremiseConstant.ALFRESCO_VERSION_4)
        {
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
        else
        {
            try
            {
                ContentStream cs = ((AbstractPersonService) personService).getAvatarStream("FAKE");
                Assert.assertNotNull(cs);
            }
            catch (AlfrescoServiceException e)
            {
                Assert.fail("Fake personIdentifier return an object");
            }
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        alfsession = null;
        personService = null;
        super.tearDown();
    }
}
