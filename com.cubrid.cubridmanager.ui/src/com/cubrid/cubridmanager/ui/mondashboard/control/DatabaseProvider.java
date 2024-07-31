/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.mondashboard.control;

import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.core.mondashboard.model.HostStatusType;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.MonitorConnection;
import java.util.List;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

/**
 * The label and content provider for adding dash board dialog tree viewer
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class DatabaseProvider extends LabelProvider
        implements ITableLabelProvider, ITreeContentProvider {

    /**
     * Returns the label image for the given column of the given element.
     *
     * @param element the object representing the entire row, or <code>null</code> indicating that
     *     no input object is set in the viewer
     * @param columnIndex the zero-based index of the column in which the label appears
     * @return Image or <code>null</code> if there is no image for the given object at columnIndex
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /**
     * Returns the label text for the given column of the given element.
     *
     * @param element the object representing the entire row, or <code>null</code> indicating that
     *     no input object is set in the viewer
     * @param columnIndex the zero-based index of the column in which the label appears
     * @return String or or <code>null</code> if there is no text for the given object at
     *     columnIndex
     */
    public String getColumnText(Object element, int columnIndex) {
        HostNode hostNode = null;
        if (element instanceof HostNode) {
            hostNode = (HostNode) element;
        } else if (element instanceof DatabaseNode) {
            hostNode = ((DatabaseNode) element).getParent();
        } else if (element instanceof BrokerNode) {
            hostNode = ((BrokerNode) element).getParent();
        }
        switch (columnIndex) {
            case 0:
                if (hostNode != null) {
                    return hostNode.getIp();
                }
                break;
            case 1:
                if (hostNode != null) {
                    return hostNode.getPort();
                }
                break;
            case 2:
                if (hostNode != null) {
                    ServerType serverType =
                            hostNode.getServerInfo() == null
                                    ? null
                                    : hostNode.getServerInfo().getServerType();
                    if (serverType == null) {
                        return "Unknown";
                    } else if (serverType == ServerType.BOTH) {
                        return "Server,Broker";
                    } else if (serverType == ServerType.DATABASE) {
                        return "Server";
                    } else {
                        return "Broker";
                    }
                }
                break;
            case 3:
                if (hostNode != null) {
                    if (hostNode.getServerInfo() == null) {
                        return "Disconnected";
                    }
                    if (hostNode.getHostStatusInfo() != null) {
                        return HostStatusType.getShowText(
                                hostNode.getHostStatusInfo().getStatusType());
                    }
                }
                break;
            case 4:
                if (element instanceof DatabaseNode) {
                    DatabaseNode dbNode = (DatabaseNode) element;
                    return dbNode.getDbName();
                } else if (element instanceof BrokerNode) {
                    BrokerNode brokerNode = (BrokerNode) element;
                    return brokerNode.getBrokerName();
                }
                break;
            case 5:
                if (element instanceof DatabaseNode) {
                    DatabaseNode dbNode = (DatabaseNode) element;
                    return DBStatusType.getShowText(dbNode.getDbStatusType());
                } else if (element instanceof BrokerNode) {
                    BrokerNode brokerNode = (BrokerNode) element;
                    return brokerNode.getBrokerInfo() == null
                                    || brokerNode.getParent() == null
                                    || brokerNode.getParent().getServerInfo() == null
                            ? DBStatusType.UNKNOWN.getText()
                            : brokerNode.getBrokerInfo().getState();
                }
                break;
            case 6:
                if (element instanceof DatabaseNode) {
                    return "Database";
                } else if (element instanceof BrokerNode) {
                    return "Broker";
                }
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * Returns the child elements of the given parent element.
     *
     * <p>The difference between this method and <code>IStructuredContentProvider.getElements</code>
     * is that <code>getElements</code> is called to obtain the tree viewer's root elements, whereas
     * <code>getChildren</code> is used to obtain the children of a given parent element in the tree
     * (including a root). The result is not modified by the viewer.
     *
     * @param parentElement the parent element
     * @return an array of child elements
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof HostNode) {
            return ((HostNode) parentElement).getCopyedHaNodeList().toArray();
        }
        return new Object[] {};
    }

    /**
     * Returns the parent for the given element, or <code>null</code> indicating that the parent
     * can't be computed. In this case the tree-structured viewer can't expand a given node
     * correctly if requested.
     *
     * @param element the element
     * @return the parent element, or <code>null</code> if it has none or if the parent cannot be
     *     computed
     */
    public Object getParent(Object element) {
        if (element instanceof DatabaseNode) {
            DatabaseNode dbNode = (DatabaseNode) element;
            if (dbNode.getDbStatusType() == DBStatusType.ACTIVE
                    || dbNode.getDbStatusType() == DBStatusType.TO_BE_ACTIVE) {
                return dbNode.getParent();
            }
            if (dbNode.getDbStatusType() == DBStatusType.STANDBY
                    || dbNode.getDbStatusType() == DBStatusType.TO_BE_STANDBY
                    || dbNode.getDbStatusType() == DBStatusType.MAINTENANCE) {
                List<MonitorConnection> connList = dbNode.getHAIncomingConnections();
                if (connList != null && !connList.isEmpty()) {
                    return connList.get(0).getSource();
                }
            }
        }
        return null;
    }

    /**
     * Returns whether the given element has children.
     *
     * <p>Intended as an optimization for when the viewer does not need the actual children. Clients
     * may be able to implement this more efficiently than <code>getChildren</code>.
     *
     * @param element the element
     * @return <code>true</code> if the given element has children, and <code>false</code> if it has
     *     no children
     */
    public boolean hasChildren(Object element) {
        if (element instanceof HostNode) {
            return true;
        }
        return false;
    }

    /**
     * Returns the elements to display in the viewer when its input is set to the given element.
     * These elements can be presented as rows in a table, items in a list, etc. The result is not
     * modified by the viewer.
     *
     * <p><b>NOTE:</b> For instances where the viewer is displaying a tree containing a single
     * 'root' element it is still necessary that the 'input' does not return <i>itself</i> from this
     * method. This leads to recursion issues (see bug 9262).
     *
     * @param inputElement the input element
     * @return the array of elements to display in the viewer
     */
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            List<HostNode> list = (List<HostNode>) inputElement;
            return list.toArray();
        }
        return new Object[] {};
    }

    /**
     * Notifies this content provider that the given viewer's input has been switched to a different
     * element.
     *
     * <p>A typical use for this method is registering the content provider as a listener to changes
     * on the new input (using model-specific means), and deregistering the viewer from the old
     * input. In response to these change notifications, the content provider should update the
     * viewer (see the add, remove, update and refresh methods on the viewers).
     *
     * <p>The viewer should not be updated during this call, as it might be in the process of being
     * disposed.
     *
     * @param viewer the viewer
     * @param oldInput the old input element, or <code>null</code> if the viewer did not previously
     *     have an input
     * @param newInput the new input element, or <code>null</code> if the viewer does not have an
     *     input
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do not need implement
    }
}
