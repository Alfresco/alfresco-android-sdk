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
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

/**
 * Test class for PersonService.
 * 
 * @author Jean Marie Pascal
 */
public class PersonServiceTest extends AlfrescoSDKTestCase
{
    protected PersonService personService;

    private static final String USER_MANAGER_ID = "user_manage@rediff.com";

    private static final String USER_MANAGER_FIRST = "User";

    private static final String USER_MANAGER_LAST = "Manager";

    private static final String USER_MANAGER_FULL = "User Manager";

    private static final String USER_COLLABORATOR_ID = "user_collaborator@rediff.com";

    private static final String USER_COLLABORATOR_FIRST = "User";

    private static final String USER_COLLABORATOR_LAST = "Collaborator";

    private static final String USER_COLLABORATOR_FULL = "User Collaborator";

    // TODO
    // Need to create a user with foreign character
    // Need to create a user with avatar based on gif image
    // Need to create a user outside the network

    protected void initSession()
    {
        if (alfsession == null)
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
     * 
     * @Requirement 34S1, 34S6, 35S1, 35S2, 36S1, 36S2
     */
    public void testGetPerson()
    {
        Person p = personService.getPerson(USER_MANAGER_ID);

        Assert.assertNotNull(p);
        Assert.assertEquals(USER_MANAGER_ID, p.getIdentifier());
        Assert.assertEquals(USER_MANAGER_FIRST, p.getFirstName());
        Assert.assertEquals(USER_MANAGER_LAST, p.getLastName());
        Assert.assertEquals(USER_MANAGER_FULL, p.getFullName());

        if (p.getAvatarIdentifier() != null)
        {
            Assert.assertNotNull(p.getAvatarIdentifier());
        }

        ContentFile avatar = personService.getAvatar(p);
        Assert.assertNotNull(avatar.getFile());

        p = personService.getPerson(USER_COLLABORATOR_ID);

        Assert.assertNotNull(p);
        Assert.assertEquals(USER_COLLABORATOR_ID, p.getIdentifier());
        Assert.assertEquals(USER_COLLABORATOR_FIRST, p.getFirstName());
        Assert.assertEquals(USER_COLLABORATOR_LAST, p.getLastName());
        Assert.assertEquals(USER_COLLABORATOR_FULL, p.getFullName());

        Assert.assertNull(p.getAvatarIdentifier());
    }

    /**
     * Failure Tests for getPerson Method.
     * 
     * @Requirement 34F1,
     */
    public void testGetPersonFailure()
    {
        try
        {
            personService.getPerson(null);
            Assert.fail("null personIdentifier return an object");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
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
     * 
     * @Requirement 35F1,
     */
    public void testGetAvatarFailure()
    {
        try
        {
            personService.getAvatar(null);
            Assert.fail("null personIdentifier return an object");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            personService.getAvatar(new PersonImpl());
            Assert.fail("Fake personIdentifier return an object");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
    }

    /**
     * Failure Tests for getAvatarStream Method.
     * 
     * @Requirement 36F1
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
