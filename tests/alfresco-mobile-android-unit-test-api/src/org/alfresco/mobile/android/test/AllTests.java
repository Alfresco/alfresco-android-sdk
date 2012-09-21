/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.test;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite
{
    public static Test executeAllTests()
    {
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }

    public static Test executeServicesTests()
    {
        return new TestSuiteBuilder(AllTests.class).includePackages("org.alfresco.mobile.android.test.api.services")
                .build();
    }

    public static Test executeModelTests()
    {
        return new TestSuiteBuilder(AllTests.class).includePackages("org.alfresco.mobile.android.test.api.model")
                .build();
    }

    public static Test executeSessionTests()
    {
        return new TestSuiteBuilder(AllTests.class).includePackages("org.alfresco.mobile.android.test.api.session")
                .build();
    }
}
