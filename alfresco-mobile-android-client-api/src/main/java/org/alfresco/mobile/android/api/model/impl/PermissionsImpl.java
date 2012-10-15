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

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The node. */
    private NodeImpl node;

    /**
     * Instantiates a new permissions impl.
     */
    public PermissionsImpl()
    {
    }

    /**
     * Instantiates a new permissions impl.
     *
     * @param node the node
     */
    public PermissionsImpl(Node node)
    {
        this.node = (NodeImpl) node;
    }

    /** {@inheritDoc} */
    public boolean canDelete()
    {
        if (node.isDocument())
        {
            return node.hasAllowableAction(Action.CAN_DELETE_OBJECT);
        }
        else if (node.isFolder())
        {
            return node.hasAllowableAction(Action.CAN_DELETE_TREE);
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    public boolean canEdit()
    {
        return node.hasAllowableAction(Action.CAN_UPDATE_PROPERTIES);
    }

    /** {@inheritDoc} */
    public boolean canAddChildren()
    {
        return (node.hasAllowableAction(Action.CAN_CREATE_FOLDER) && node.hasAllowableAction(Action.CAN_CREATE_DOCUMENT));
    }

    /** {@inheritDoc} */
    public boolean canComment()
    {
        return canEdit() || canAddChildren();
    }

}
