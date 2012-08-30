package org.alfresco.mobile.android.test.api.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

public class PersonServiceTest extends AlfrescoSDKTestCase
{
    
    protected PersonService personService;
    
    protected void initSession()
    {
        alfsession = createRepositorySession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        personService = alfsession.getServiceRegistry().getPersonService();
        Assert.assertNotNull(personService);
    }
    
    public void testPersonService() throws AlfrescoException
    {
        initSession();
        
        // ///////////////////////////////////////////////////////////////////////////
        // Get Person
        // ///////////////////////////////////////////////////////////////////////////
        Person p = personService.getPerson(alfsession.getPersonIdentifier());
        
        Assert.assertNotNull(p);
        Assert.assertEquals(alfsession.getPersonIdentifier(), p.getIdentifier());
        Assert.assertNotNull(p.getFirstName());
        Assert.assertNotNull(p.getLastName());
        Assert.assertNotNull(p.getFullName());
        
        if (p.getAvatarIdentifier() != null)
            Assert.assertNotNull(p.getAvatarIdentifier());
        
        ContentFile avatar = personService.getAvatar(p);
        Assert.assertNotNull(avatar.getFile());
    }
}
