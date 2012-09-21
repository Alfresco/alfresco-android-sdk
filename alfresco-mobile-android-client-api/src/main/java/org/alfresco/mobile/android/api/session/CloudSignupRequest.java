package org.alfresco.mobile.android.api.session;

import java.util.GregorianCalendar;

/**
 * Represents a request from a user to sign up to Alfresco Cloud.
 * 
 * @author Jean Marie Pascal
 */
public interface CloudSignupRequest
{

    /**
     * Returns the identifier of this registration.
     */
    String getIdentifier();

    /**
     * Returns the API key used for the signup request.
     */
    String getApiKey();

    /**
     * Returns the registration key.
     */
    String getRegistrationKey();

    /**
     * Returns the email address of the user that requested to signup.
     */
    String getEmailAddress();

    /**
     * Returns the time of the registration request.
     */
    GregorianCalendar getRegistrationTime();

}
