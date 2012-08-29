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
package org.alfresco.mobile.android.ui.tag;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.TagsLoader;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListFragment;
import org.alfresco.mobile.android.ui.manager.MessengerManager;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

public abstract class TagsFilterFragment extends BaseListFragment implements
        LoaderCallbacks<LoaderResult<PagingResult<Tag>>>
{

    public static final String TAG = "TagsFragment";

    protected Boolean loadAtCreation = true;

    private List<Tag> selectedTags = new ArrayList<Tag>(5);

    public TagsFilterFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null) { return null; }
        View v = inflater.inflate(R.layout.sdk_tags, container, false);

        init(v, R.string.empty_tag);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        if (alfSession == null)
        {
            MessengerManager.showToast(getActivity(), R.string.empty_session);
            setListShown(true);
            lv.setEmptyView(ev);
            return;
        }

        if (!isFullLoad && loadAtCreation)
        {
            getLoaderManager().initLoader(TagsLoader.ID, null, this);
            getLoaderManager().getLoader(TagsLoader.ID).forceLoad();
        }

        EditText filterText = (EditText) getActivity().findViewById(R.id.search_box);
        filterText.addTextChangedListener(filterTextWatcher);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private TextWatcher filterTextWatcher = new TextWatcher()
    {

        public void afterTextChanged(Editable s)
        {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            adapter.getFilter().filter(s);
        }

    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {

        Tag t = (Tag) l.getItemAtPosition(position);
        if (selectedTags.contains(t))
        {
            selectedTags.remove(t);
        }
        else
        {
            selectedTags.add(t);
        }
    }

    @Override
    public Loader<LoaderResult<PagingResult<Tag>>> onCreateLoader(int id, Bundle b)
    {
        setListShown(false);
        adapter = null;
        isFullLoad = Boolean.FALSE;
        return new TagsLoader(getActivity(), alfSession);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Tag>>> arg0,
            LoaderResult<PagingResult<Tag>> results)
    {
        if (adapter == null)
        {
            adapter = new TagsAdapter(getActivity(), R.layout.sdk_list_checkeditem, new ArrayList<Tag>(0),
                    selectedTags);
        }
        if (!checkException(results))
        {
            displayPagingData(results.getData(), loaderId, callback);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<PagingResult<Tag>>> arg0)
    {
        // TODO Auto-generated method stub
    }

}
