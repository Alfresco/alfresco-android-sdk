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
package org.alfresco.mobile.android.samples.activity;

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.intent.RequestCode;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.fragments.FragmentDisplayer;
import org.alfresco.mobile.android.samples.ui.ListUISamplesFragments;
import org.alfresco.mobile.android.samples.ui.comment.CommentsFragment;
import org.alfresco.mobile.android.samples.ui.documentfolder.ChildrenFragment;
import org.alfresco.mobile.android.samples.ui.properties.DetailsFragment;
import org.alfresco.mobile.android.samples.ui.tags.TagsListNodeFragment;
import org.alfresco.mobile.android.samples.ui.versions.VersionFragment;
import org.alfresco.mobile.android.samples.utils.MenuActionItem;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.ui.fragments.BaseFragment;
import org.alfresco.mobile.android.ui.manager.ActionManager;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main activity of the sample application. This activity is responsible to
 * manage all independant fragments.
 * 
 * @author Jean Marie Pascal
 */
public class MainActivity extends CommonActivity
{

    /** Called when the activity is first created. */
    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdkapp_main);

        FragmentDisplayer.loadFragment(this, R.id.body, ListUISamplesFragments.FRAG_TAG);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            ActionBar bar = getActionBar();
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_USE_LOGO);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                bar.setHomeButtonEnabled(true);
            }
        }
    }

    // ///////////////////////////////////////////
    // NAVIGATION
    // ///////////////////////////////////////////

    public void showBrowserFragment(Folder f)
    {
        BaseFragment frag = ChildrenFragment.newInstance(f);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, ChildrenFragment.TAG, true);
    }

    public void showBrowserFragment(String path)
    {
        BaseFragment frag = ChildrenFragment.newInstance(path);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, ChildrenFragment.TAG, true);
    }

    public void showBrowserFragment(Site s)
    {
        BaseFragment frag = ChildrenFragment.newInstance(s);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, ChildrenFragment.TAG, true);
    }

    public void showPropertiesFragment(Node n)
    {
        BaseFragment frag = DetailsFragment.newInstance(n);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, DetailsFragment.TAG, true);
    }

    public void showCommentsFragment(Node n)
    {
        BaseFragment frag = CommentsFragment.newInstance(n);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, CommentsFragment.TAG, true);
    }

    public void showVersionsFragment(Document d)
    {
        BaseFragment frag = VersionFragment.newInstance(d);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, VersionFragment.TAG, true);
    }

    public void showTagsFragment(Document d)
    {
        BaseFragment frag = TagsListNodeFragment.newInstance(d);
        frag.setSession(SessionUtils.getsession(this));
        FragmentDisplayer.replaceFragment(this, frag, R.id.body, TagsListNodeFragment.TAG, true);
    }

    // ///////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        if (isVisible(ChildrenFragment.TAG))
        {
            ((ChildrenFragment) getFragment(ChildrenFragment.TAG)).getMenu(menu);
        }

        if (isVisible(DetailsFragment.TAG))
        {
            ((DetailsFragment) getFragment(DetailsFragment.TAG)).getMenu(menu);
        }

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MenuActionItem.CREATE_FOLDER:
                ((ChildrenFragment) getFragment(ChildrenFragment.TAG)).createFolder();
                return true;
            case MenuActionItem.UPLOAD:
                ActionManager.actionPickFile(getFragment(ChildrenFragment.TAG), RequestCode.REQUESTCODE_FILEPICKER);
                return true;
            case MenuActionItem.OPEN_IN:
                ((DetailsFragment) getFragment(DetailsFragment.TAG)).openin();
                return true;
            case MenuActionItem.LIKE:
                ((DetailsFragment) getFragment(DetailsFragment.TAG)).like();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Utils to determines if a fragment with a specified tag is visible or not.
     * 
     * @param tag : fragment's tag
     * @return true if present.
     */
    private boolean isVisible(String tag)
    {
        return getFragmentManager().findFragmentByTag(tag) != null
                && getFragmentManager().findFragmentByTag(tag).isAdded();
    }
}
