package org.alfresco.mobile.android.test.api.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Company;
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

    private static final String JOBTITLE = "Admin";

    private static final String LOCATION = "Maidenhead";

    private static final String SUMMARY = "This is a summary.";

    private static final String TELEPHONE = "0102030405";

    private static final String MOBILE = "0102030405";

    private static final String EMAIL = "user_manage@rediff.com";

    private static final String SKYPE = "skype";

    private static final String IM = "im";

    private static final String GOOGLEID = "google";

    private static final String COMPANY = "Alfresco";

    private static final String ADRESS1 = "Bridge Avenue";

    private static final String ADRESS2 = "The Place";

    private static final String ADRESS3 = "Maidenhead";

    private static final String POSTALCODE = "SL6 1AF";

    private static final String COMPANYTEL = "+44(0)1628876600";

    private static final String COMPANYEMAIL = "info@alfresco.com";

    private static final String COMPANYFAX = "+44(0)1628876501";

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

    /**
     * Success Test for getPerson
     * 
     * @since 1.3.0
     */
    public void testFullPropertiesPerson()
    {
        Person p = personService.getPerson(USER_MANAGER_ID);
        Assert.assertNotNull(p);
        Assert.assertEquals(USER_MANAGER_ID, p.getIdentifier());
        Assert.assertEquals(USER_MANAGER_FIRST, p.getFirstName());
        Assert.assertEquals(USER_MANAGER_LAST, p.getLastName());
        Assert.assertEquals(USER_MANAGER_FULL, p.getFullName());
        Assert.assertEquals(JOBTITLE, p.getJobTitle());
        Assert.assertEquals(LOCATION, p.getLocation());
        Assert.assertEquals(SUMMARY, p.getSummary());
        Assert.assertEquals(TELEPHONE, p.getTelephoneNumber());
        Assert.assertEquals(MOBILE, p.getMobileNumber());
        Assert.assertEquals(EMAIL, p.getEmail());
        Assert.assertEquals(SKYPE, p.getSkypeId());
        Assert.assertEquals(IM, p.getInstantMessageId());
        Assert.assertEquals(GOOGLEID, p.getGoogleId());

        Company company = p.getCompany();
        Assert.assertNotNull(company);
        Assert.assertEquals(COMPANY, company.getName());
        Assert.assertEquals(ADRESS1, company.getAddress1());
        Assert.assertEquals(ADRESS2, company.getAddress2());
        Assert.assertEquals(ADRESS3, company.getAddress3());
        Assert.assertEquals(POSTALCODE, company.getPostCode());
        Assert.assertEquals(COMPANYTEL, company.getTelephoneNumber());
        Assert.assertEquals(COMPANYFAX, company.getFaxNumber());
        Assert.assertEquals(COMPANYEMAIL, company.getEmail());
        Assert.assertNotNull(company.getFullAddress());
        
        //Test Null values
        p = personService.getPerson(USER_COLLABORATOR_ID);

        Assert.assertNotNull(p);
        Assert.assertEquals(USER_COLLABORATOR_ID, p.getIdentifier());
        Assert.assertEquals(USER_COLLABORATOR_FIRST, p.getFirstName());
        Assert.assertEquals(USER_COLLABORATOR_LAST, p.getLastName());
        Assert.assertEquals(USER_COLLABORATOR_FULL, p.getFullName());

        Assert.assertNull(p.getJobTitle());
        Assert.assertNull(p.getLocation());
        Assert.assertNull(p.getSummary());
        Assert.assertNull(p.getTelephoneNumber());
        Assert.assertNull(p.getMobileNumber());
        Assert.assertEquals(USER_COLLABORATOR_ID, p.getEmail());
        Assert.assertNull(p.getSkypeId());
        Assert.assertNull(p.getInstantMessageId());
        Assert.assertNull(p.getGoogleId());

        company = p.getCompany();
        Assert.assertNotNull(company);
        Assert.assertNull(company.getName());
        Assert.assertNull(company.getAddress1());
        Assert.assertNull(company.getAddress2());
        Assert.assertNull(company.getAddress3());
        Assert.assertNull(company.getPostCode());
        Assert.assertNull(company.getTelephoneNumber());
        Assert.assertNull(company.getFaxNumber());
        Assert.assertNull(company.getEmail());
        Assert.assertNull(company.getFullAddress());
        
    }
    

    @Override
    protected void tearDown() throws Exception
    {
        alfsession = null;
        personService = null;
        super.tearDown();
    }
}
