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
package org.alfresco.mobile.android.test.api.utils;

import java.util.Locale;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.Version;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;

import android.os.PatternMatcher;
import android.test.AndroidTestCase;

public class UtilsTest extends AndroidTestCase
{

    public void testSDKVersion()
    {
        Assert.assertNotNull(Version.SDK);
        Assert.assertEquals("1.4", Version.SDK);
    }

    public void testPattern()
    {

        // NodeRef
        String nodeRef = "workspace://SpacesStore/2485c3f5-533d-4ab7-b409-ef6daee9cf29";

        Assert.assertTrue(NodeRefUtils.isNodeRef(nodeRef));
        Assert.assertFalse(NodeRefUtils.isIdentifier(nodeRef));
        Assert.assertFalse(NodeRefUtils.isVersionIdentifier(nodeRef));

        // Identifier
        String identifier = "2485c3f5-533d-4ab7-b409-ef6daee9cf29";

        Assert.assertFalse(NodeRefUtils.isNodeRef(identifier));
        Assert.assertTrue(NodeRefUtils.isIdentifier(identifier));
        Assert.assertFalse(NodeRefUtils.isVersionIdentifier(identifier));

        // Version + Identifier
        String verisonNodeRef = "workspace://SpacesStore/2485c3f5-533d-4ab7-b409-ef6daee9cf29;1.4";
        Assert.assertTrue(NodeRefUtils.isNodeRef(verisonNodeRef));
        Assert.assertFalse(NodeRefUtils.isIdentifier(verisonNodeRef));
        Assert.assertFalse(NodeRefUtils.isVersionIdentifier(verisonNodeRef));

        nodeRef = NodeRefUtils.getCleanIdentifier(verisonNodeRef);
        Assert.assertTrue(NodeRefUtils.isNodeRef(nodeRef));
        Assert.assertFalse(NodeRefUtils.isIdentifier(nodeRef));
        Assert.assertFalse(NodeRefUtils.isVersionIdentifier(nodeRef));

        identifier = NodeRefUtils.getNodeIdentifier(verisonNodeRef);
        Assert.assertFalse(NodeRefUtils.isNodeRef(identifier));
        Assert.assertTrue(NodeRefUtils.isIdentifier(identifier));
        Assert.assertFalse(NodeRefUtils.isVersionIdentifier(identifier));
    }

    public void testPatternMAtcher()
    {
        String path = "/share/page/site/MobileEngineeringTeam/folder-details?nodeRef=workspace://SpacesStore/24e858f4-d2de-439f-88a7-608ea5bbb4ef";

        PatternMatcher pm = new PatternMatcher("/share/page/site", PatternMatcher.PATTERN_PREFIX);
        Assert.assertTrue(pm.match(path));
    }

    public void testLocaleToLowerCase()
    {
        Assert.assertTrue("i".equals("I".toLowerCase(Locale.ENGLISH)));
        Assert.assertTrue("i".equals("I".toLowerCase(Locale.CHINESE)));
        Assert.assertTrue("i".equals("I".toLowerCase(Locale.FRENCH)));
        Assert.assertTrue("i".equals("I".toLowerCase(Locale.KOREA)));
        Assert.assertTrue("i".equals("I".toLowerCase(Locale.JAPANESE)));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("ar_SA"))));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("iw_IL"))));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("hi_IN"))));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("sv_SE"))));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("ru_RU"))));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("no_NO"))));
        Assert.assertTrue("i".equals("I".toLowerCase(new Locale("uk_UA"))));
        Assert.assertFalse("i is not equal to " + "I".toLowerCase(new Locale("tr")),
                "i".equals("I".toLowerCase(new Locale("tr"))));
    }
}
