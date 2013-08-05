package org.alfresco.mobile.android.api.services.impl.cloud;

import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIWorkflowServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;

import android.os.Parcel;
import android.os.Parcelable;

public class CloudWorkflowServiceImpl extends PublicAPIWorkflowServiceImpl
{

    public CloudWorkflowServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }


    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<CloudWorkflowServiceImpl> CREATOR = new Parcelable.Creator<CloudWorkflowServiceImpl>()
    {
        public CloudWorkflowServiceImpl createFromParcel(Parcel in)
        {
            return new CloudWorkflowServiceImpl(in);
        }

        public CloudWorkflowServiceImpl[] newArray(int size)
        {
            return new CloudWorkflowServiceImpl[size];
        }
    };

    public CloudWorkflowServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(CloudSessionImpl.class.getClassLoader()));
    }

}
