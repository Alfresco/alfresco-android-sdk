package org.alfresco.mobile.android.api.session;

import java.util.GregorianCalendar;

public interface CloudSignupRequest
{

    /**
     * Returns the identifier of this registration.
     */
    public String getIdentifier();

    /**
     * Returns the API key used for the signup request.
     */
    public String getApiKey();

    /**
     * Returns the registration key.
     */
    public String getRegistrationKey();

    /**
     * Returns the email address of the user that requested to signup.
     */
    public String getEmailAddress();

    /**
     * Returns the time of the registration request.
     */
    public GregorianCalendar getRegistrationTime();

}
