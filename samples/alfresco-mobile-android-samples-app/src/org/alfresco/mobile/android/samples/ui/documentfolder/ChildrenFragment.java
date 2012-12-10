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
package org.alfresco.mobile.android.samples.ui.documentfolder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.asynchronous.DocumentCreateLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.NodeChildrenLoader;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.intent.RequestCode;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.activity.MainActivity;
import org.alfresco.mobile.android.samples.ui.documentfolder.actions.AddContentDialogFragment;
import org.alfresco.mobile.android.samples.ui.documentfolder.actions.AddFolderDialogFragment;
import org.alfresco.mobile.android.samples.utils.MenuActionItem;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.samples.utils.UIUtils;
import org.alfresco.mobile.android.ui.documentfolder.NavigationFragment;
import org.alfresco.mobile.android.ui.documentfolder.NodeAdapter;
import org.alfresco.mobile.android.ui.documentfolder.actions.CreateFolderDialogFragment;
import org.alfresco.mobile.android.ui.documentfolder.listener.OnNodeCreateListener;
import org.alfresco.mobile.android.ui.manager.ActionManager;
import org.alfresco.mobile.android.ui.utils.GenericViewHolder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class ChildrenFragment extends NavigationFragment
{

    public static final String TAG = "ChildrenFragment";

    private ProgressDialog mProgressDialog;

    private Folder savedParentFolder;

    public ChildrenFragment()
    {
    }

    public static ChildrenFragment newInstance(Folder folder)
    {
        return newInstance(folder, null, null);
    }

    public static ChildrenFragment newInstance(String folderPath)
    {
        return newInstance(null, folderPath, null);
    }

    public static ChildrenFragment newInstance(Site site)
    {
        return newInstance(null, null, site);
    }

    public static ChildrenFragment newInstance(Folder parentFolder, String pathFolder, Site site)
    {
        ChildrenFragment bf = new ChildrenFragment();
        ListingContext lc = new ListingContext();
        lc.setMaxItems(10);
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_NAME);
        lc.setIsSortAscending(true);
        Bundle b = createBundleArgs(parentFolder, pathFolder, site);
        b.putAll(createBundleArgs(lc, LOAD_MANUAL));
        bf.setArguments(b);
        return bf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            savedParentFolder = savedInstanceState.getParcelable("parentFolder");
        }

        alfSession = SessionUtils.getsession(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        UIUtils.setFragmentTitle(getActivity(), title);
        super.onStart();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Node n = (Node) l.getItemAtPosition(position);
        if (n.isFolder())
        {
            // Browse
            ((MainActivity) getActivity()).showBrowserFragment((Folder) n);
        }
        else
        {
            // Show properties
            ((MainActivity) getActivity()).showPropertiesFragment(n);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LISTING
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Node>>> loader, LoaderResult<PagingResult<Node>> results)
    {
        if (loader instanceof NodeChildrenLoader)
        {
            parentFolder = ((NodeChildrenLoader) loader).getParentFolder();
        }

        if (adapter == null)
        {
            adapter = new CustomNodeAdapter(getActivity(), alfSession, R.layout.sdk_list_row, new ArrayList<Node>(0),
                    selectedItems);
        }

        if (!checkException(results))
        {
            displayPagingData(results.getData(), loaderId, callback);
        }
    }

    private static class CustomNodeAdapter extends NodeAdapter
    {
        public CustomNodeAdapter(Activity context, AlfrescoSession session, int textViewResourceId,
                List<Node> listItems, List<Node> selectedItems)
        {
            super(context, session, textViewResourceId, listItems, selectedItems);
        }

        @Override
        protected void updateBottomText(GenericViewHolder vh, Node item)
        {
            if (item.getDescription() != null)
            {
                vh.bottomText.setText(item.getDescription());
            }
            else
            {
                vh.bottomText.setVisibility(View.GONE);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // MENU
    // ///////////////////////////////////////////////////////////////////////////

    public void getMenu(Menu menu)
    {
        MenuItem mi;

        if (parentFolder == null && SessionUtils.getsession(getActivity()) != null)
        {
            parentFolder = SessionUtils.getsession(getActivity()).getRootFolder();
        }

        // TODO CanAddchildren
        if (parentFolder != null)
        {
            mi = menu.add(Menu.NONE, MenuActionItem.CREATE_FOLDER, Menu.FIRST + MenuActionItem.CREATE_FOLDER,
                    R.string.folder_create);
            mi.setIcon(R.drawable.ic_add_folder);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            mi = menu.add(Menu.NONE, MenuActionItem.UPLOAD, Menu.FIRST + MenuActionItem.UPLOAD, R.string.content_upload);
            mi.setIcon(R.drawable.ic_upload);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SAVE INSTANCE
    // ///////////////////////////////////////////////////////////////////////////
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("parentFolder", parentFolder);
    }

    public Folder getParentFolder()
    {
        if (savedParentFolder != null)
        {
            return savedParentFolder;
        }
        else if (parentFolder == null)
        {
            parentFolder = SessionUtils.getsession(getActivity()).getRootFolder();
        }
        return parentFolder;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    public void createFolder()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(CreateFolderDialogFragment.TAG);
        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        AddFolderDialogFragment newFragment = AddFolderDialogFragment.newInstance(getParentFolder());

        newFragment.setOnCreateListener(new OnNodeCreateListener()
        {
            @Override
            public void afterContentCreation(Node node)
            {
                mProgressDialog.dismiss();
                refresh();
            }

            @Override
            public void beforeContentCreation(Folder arg0, String arg1, Map<String, Serializable> arg2, ContentFile arg3)
            {
                mProgressDialog = ProgressDialog.show(getActivity(), "Please wait", "Contacting your server...", true,
                        true, new OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                getActivity().getLoaderManager().destroyLoader(DocumentCreateLoader.ID);
                            }
                        });

            }

            @Override
            public void onExeceptionDuringCreation(Exception arg0)
            {
                mProgressDialog.dismiss();
            }
        });

        newFragment.show(ft, CreateFolderDialogFragment.TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RequestCode.REQUESTCODE_FILEPICKER && data != null && data.getData() != null)
        {
            createFile(new File(ActionManager.getPath(getActivity(), data.getData())));
        }
    }

    public void createFile(File f)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(AddContentDialogFragment.TAG);
        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        AddContentDialogFragment newFragment = AddContentDialogFragment.newInstance(getParentFolder(), f);

        newFragment.setOnCreateListener(new OnNodeCreateListener()
        {
            @Override
            public void afterContentCreation(Node node)
            {
                mProgressDialog.dismiss();
                refresh();
            }

            @Override
            public void beforeContentCreation(Folder arg0, String arg1, Map<String, Serializable> arg2, ContentFile arg3)
            {
                mProgressDialog = ProgressDialog.show(getActivity(), getActivity().getText(R.string.dialog_wait),
                        getActivity().getText(R.string.contact_server_progress), true, true, new OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                getActivity().getLoaderManager().destroyLoader(DocumentCreateLoader.ID);
                                dialog.dismiss();
                            }
                        });
            }

            @Override
            public void onExeceptionDuringCreation(Exception arg0)
            {
                mProgressDialog.dismiss();
            }
        });

        newFragment.show(ft, AddContentDialogFragment.TAG);
    }

    @Override
    protected void displayLoadingFooter()
    {
        if (loadState == LOAD_MANUAL)
        {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View footer = inflater.inflate(R.layout.sdk_list_loading, null, false);
            footer.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            footer.findViewById(R.id.loading_label).setVisibility(View.GONE);
            Button b = (Button) footer.findViewById(R.id.loading_button);
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    footer.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
                    footer.findViewById(R.id.loading_label).setVisibility(View.VISIBLE);
                    footer.findViewById(R.id.loading_button).setVisibility(View.GONE);
                    loadMore();
                }
            });
            displayLoadingFooter(footer);
        }
        else
        {
            super.displayLoadingFooter();
        }
    }
}
