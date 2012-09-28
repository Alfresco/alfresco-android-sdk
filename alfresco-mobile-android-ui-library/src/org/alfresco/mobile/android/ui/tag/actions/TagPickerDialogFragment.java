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
package org.alfresco.mobile.android.ui.tag.actions;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.TagsLoader;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListFragment;
import org.alfresco.mobile.android.ui.tag.TagsAdapter;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

//TODO create activity with intent associated
public class TagPickerDialogFragment extends BaseListFragment implements
        LoaderCallbacks<LoaderResult<PagingResult<Tag>>>
{

    public static final String TAG = "TagPickerDialogFragment";

    protected Boolean loadAtCreation = true;

    private List<Tag> selectedTags = new ArrayList<Tag>();

    private onTagPickerListener onTagPickerListener;

    public TagPickerDialogFragment()
    {
        loaderId = TagsLoader.ID;
        callback = this;
        emptyListMessageId = R.string.empty_tag;
    }

    public static TagPickerDialogFragment newInstance(AlfrescoSession session, List<Tag> selectedTags)
    {
        TagPickerDialogFragment tp = new TagPickerDialogFragment();
        tp.alfSession = session;
        if (selectedTags != null)
        {
            tp.selectedTags = selectedTags;
        }
        return tp;
    }

    @Override
    public void onStart()
    {
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.mime_tags);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle(R.string.tags_pick);
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);

        View v = inflater.inflate(R.layout.sdk_create_content_tag, container, false);
        init(v, emptyListMessageId);

        Button button = (Button) v.findViewById(R.id.cancel_tag);
        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                TagPickerDialogFragment.this.dismiss();
            }
        });

        Button bcreate = (Button) v.findViewById(R.id.validate_tag);
        bcreate.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                onTagPickerListener.onValidateTags(selectedTags);
                dismiss();
            }
        });

        checkSession(true);

        if (loadAtCreation)
        {
            continueLoading(loaderId, callback);
        }

        EditText filterText = (EditText) v.findViewById(R.id.search_box);
        filterText.addTextChangedListener(filterTextWatcher);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setTextFilterEnabled(true);

        return v;
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
            adapter.notifyDataSetChanged();
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
        refreshListView();
    }

    @Override
    public Loader<LoaderResult<PagingResult<Tag>>> onCreateLoader(int id, Bundle b)
    {
        setListShown(false);
        adapter = null;
        isFullLoad = Boolean.FALSE;
        return new TagsLoader(getActivity(), alfSession, null);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Tag>>> arg0, LoaderResult<PagingResult<Tag>> results)
    {
        if (adapter == null)
        {
            adapter = new TagsAdapter(getActivity(), R.layout.sdk_list_checkeditem, new ArrayList<Tag>(0), selectedTags);
        }
        displayPagingData(results.getData(), loaderId, callback);
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<PagingResult<Tag>>> arg0)
    {
        // TODO Auto-generated method stub
    }

    public void setOnTagPickerListener(onTagPickerListener onTagPickerListener)
    {
        this.onTagPickerListener = onTagPickerListener;
    }

    public interface onTagPickerListener
    {
        public void onValidateTags(List<Tag> tags);
    }

}
