package org.alfresco.mobile.android.api.model.impl;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.Permissions;
import org.apache.chemistry.opencmis.commons.enums.Action;

/**
 * Permissions represent the actions a person can perform on a node.
 * 
 * @author Jean Marie Pascal
 */
public class PermissionsImpl implements Permissions
{

    private static final long serialVersionUID = 1L;

    private NodeImpl node;

    public PermissionsImpl()
    {
    }

    public PermissionsImpl(Node node)
    {
        this.node = (NodeImpl) node;
    }

    /**
     * @return Determines whether the current user can delete the node.
     */
    public boolean canDelete()
    {
        if (node.isDocument())
            return node.hasAllowableAction(Action.CAN_DELETE_OBJECT);
        else if (node.isFolder())
            return node.hasAllowableAction(Action.CAN_DELETE_TREE);
        else
            return false;
    }

    /**
     * @return Determines whether the current user can edit the node.
     */
    public boolean canEdit()
    {
        return node.hasAllowableAction(Action.CAN_UPDATE_PROPERTIES);
    }

    /**
     * @return Returns true if it's possible to create folder or a document as
     *         child of this folder.
     */
    public boolean canAddChildren()
    {
        if (node.hasAllowableAction(Action.CAN_CREATE_FOLDER) && node.hasAllowableAction(Action.CAN_CREATE_DOCUMENT))
            return true;
        else
            return false;
    }

    /**
     * @return determines whether the given user can comment on the given node.
     */
    public boolean canComment()
    {
        return canEdit();
    }

}
