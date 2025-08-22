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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

/**
 * This is CUBRID node adaptor factory for adapting to CUBRID node
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class NodeAdapterFactory implements IAdapterFactory {
    /**
     * CUBRID node adaptor
     *
     * @author pangqiren 2009-3-2
     */
    private static class CubridNodeAdapter implements IDeferredWorkbenchAdapter {

        /**
         * Called by a job run in a separate thread to fetch the children of
         * this adapter. The adapter should in return notify of new children via
         * the collector. This is generally used when a content provider is
         * getting elements.
         *
         * @param object the object to fetch the children for
         * @param collector the collector to notify about new children. Should
         *        not be <code>null</code>.
         * @param monitor a progress monitor that will never be
         *        <code>null<code> to
         *                   support reporting and cancellation.
         */
        public void fetchDeferredChildren(
                Object object, IElementCollector collector, IProgressMonitor monitor) {
            if (object instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) object;
                monitor.beginTask(
                        Messages.bind(Messages.msgLoadingChildren, node.getLabel()),
                        IProgressMonitor.UNKNOWN);
                ICubridNode[] nodeArr = node.getChildren(monitor);
                if (monitor.isCanceled()) {
                    return;
                }
                if (nodeArr != null && nodeArr.length > 0) {
                    collector.add(nodeArr, monitor);
                }
                collector.done();
            }
            monitor.done();
        }

        /**
         * Returns the children of this object. When this object is displayed in a tree, the
         * returned objects will be this element's children. Returns an empty array if this object
         * has no children.
         *
         * @param object The object to get the children for.
         * @return Object[]
         */
        public Object[] getChildren(Object object) {
            return new Object[] {};
        }

        /**
         * Returns an image descriptor to be used for displaying an object in the workbench. Returns
         * <code>null</code> if there is no appropriate image.
         *
         * @param object The object to get an image descriptor for.
         * @return ImageDescriptor
         */
        public ImageDescriptor getImageDescriptor(Object object) {
            return null;
        }

        /**
         * Returns the label text for this element.
         *
         * @param obj The object to get a label for.
         * @return String
         */
        public String getLabel(Object obj) {
            if (obj instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) obj;
                return node.getLabel();
            }
            return null;
        }

        /**
         * Returns the logical parent of the given object in its tree. Returns <code>null</code> if
         * there is no parent, or if this object doesn't belong to a tree.
         *
         * @param obj The object to get the parent for.
         * @return Object
         */
        public Object getParent(Object obj) {
            if (obj instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) obj;
                return node.getParent();
            }
            return null;
        }

        /**
         * Returns the rule used to schedule the deferred fetching of children for this adapter.
         *
         * @param object the object whose children are being fetched
         * @return the scheduling rule. May be <code>null</code>.
         * @see org.eclipse.core.runtime.jobs.Job#setRule(ISchedulingRule)
         */
        public ISchedulingRule getRule(final Object object) {
            return null;
        }

        /**
         * Returns whether this adapter may have children. This is an optimized method used by
         * content providers to allow showing the [+] expand icon without having yet fetched the
         * children for the element.
         *
         * @return <code>true</code>if the adapter may have childen, and <code>false</code>
         *     otherwise.
         */
        public boolean isContainer() {
            return true;
        }
    }

    private final CubridNodeAdapter cubridNodeAdapter = new CubridNodeAdapter();

    /**
     * Get adapter object
     *
     * @param object the source object
     * @param type the adapter type
     * @return the adapter object
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Object object, Class type) {
        if (object instanceof ICubridNode
                && (type == ICubridNode.class
                        || type == IDeferredWorkbenchAdapter.class
                        || type == IWorkbenchAdapter.class)) {
            return cubridNodeAdapter;
        }
        return null;
    }

    /**
     * Get the adapter list
     *
     * @return the adapter array
     */
    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
        return new Class[] {
            ICubridNode.class, IDeferredWorkbenchAdapter.class, IWorkbenchAdapter.class
        };
    }
}
