/*
o * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi;

import com.cubrid.common.ui.spi.contribution.StatusLineContrItem;
import com.cubrid.common.ui.spi.contribution.TitleLineContrItem;
import com.cubrid.common.ui.spi.contribution.WorkbenchContrItem;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * A layout manager which implement these interface <code>ISelectionChangedListener</code> and
 * <code>IDoubleClickListener</code> and <code>ICubridNodeChangedListener</code>.
 *
 * <p>It is responsible to manage workbench part(close and open and refresh editor part and view
 * part) and title line and status line.Workben part is managed by <code>WorkbenchContrItem</code>
 * object. Title line is managed by <code>TitleLineContrItem</code> object. Status line is managed
 * by <code>StatusLineContrItem</code> object.
 *
 * <p>When select the node in the navigator, it will change the title line and status line content.
 *
 * <p>When remove and refresh node in the navigator, if this node is deleted, it will close and
 * editor part and view part of this node
 *
 * <p>When double click the node in the navigator, it will open the editor and view part
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class LayoutManager
        implements ISelectionChangedListener, IDoubleClickListener, ICubridNodeChangedListener {
    private static LayoutManager manager = new LayoutManager();
    private ISelectionProvider provider = null;
    private WorkbenchContrItem workbenchContrItem = new WorkbenchContrItem();
    private StatusLineContrItem statusLineContrItem = new StatusLineContrItem();
    private TitleLineContrItem titleLineContrItem = new TitleLineContrItem();
    private boolean isUseClickOnce = false;

    /** The constructor */
    private LayoutManager() {}

    /**
     * Get the only LayoutManager instance
     *
     * @return the LayoutManager instance
     */
    public static LayoutManager getInstance() {
        return manager;
    }

    /**
     * Set current selected node
     *
     * @param node the ICubridNode object
     */
    public void setCurrentSelectedNode(ICubridNode node) {
        if (this.provider != null) {
            this.provider.setSelection(new StructuredSelection(node));
        }
    }

    /**
     * Get current selected CUBRID node
     *
     * @return the selected ICubridNode object
     */
    public ICubridNode getCurrentSelectedNode() {
        if (this.provider == null) {
            return null;
        }
        ISelection selection = this.provider.getSelection();
        if (selection == null || selection.isEmpty()) {
            return null;
        }
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (!(obj instanceof ICubridNode)) {
            return null;
        }
        return (ICubridNode) obj;
    }

    /**
     * Get SelectionProvider
     *
     * @return the ISelectionProvider object
     */
    public ISelectionProvider getSelectionProvider() {
        return this.provider;
    }

    /**
     * Change selection provider
     *
     * @param provider the ISelectionProvider object
     */
    public void changeSelectionProvider(ISelectionProvider provider) {
        if (provider != null) {
            if (this.provider != null) {
                this.provider.removeSelectionChangedListener(this);
            }
            this.provider = provider;
            this.provider.addSelectionChangedListener(this);
            statusLineContrItem.changeStuatusLineForNavigator(this.provider.getSelection());
            titleLineContrItem.changeTitleForNavigator(this.provider.getSelection());
        }
    }

    /**
     * Fire selection change event
     *
     * @param selection the ISelection object
     */
    public void fireSelectionChanged(ISelection selection) {
        SelectionChangedEvent event = new SelectionChangedEvent(getSelectionProvider(), selection);
        selectionChanged(event);
    }

    /**
     * Notifies that the selection has changed.
     *
     * @param event the SelectionChangedEvent object
     */
    public void selectionChanged(SelectionChangedEvent event) {
        if (this.provider == null) {
            return;
        }
        titleLineContrItem.changeTitleForNavigator(event.getSelection());
        statusLineContrItem.changeStuatusLineForNavigator(event.getSelection());
        workbenchContrItem.processSelectionChanged(event);
    }

    /**
     * Notifies of a double click.
     *
     * @param event event object describing the double-click
     */
    public void doubleClick(DoubleClickEvent event) {
        workbenchContrItem.processDoubleClickNavigatorEvent(event);
    }

    /**
     * When refresh container node,check the viewpart and the editorPart of it's children,if the
     * children are deleted,close it,or refresh it
     *
     * @param event the CubridNodeChangedEvent object
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        workbenchContrItem.processCubridNodeChangeEvent(event);
    }

    public WorkbenchContrItem getWorkbenchContrItem() {
        return workbenchContrItem;
    }

    public void setWorkbenchContrItem(WorkbenchContrItem workbenchContrItem) {
        this.workbenchContrItem = workbenchContrItem;
    }

    public StatusLineContrItem getStatusLineContrItem() {
        return statusLineContrItem;
    }

    public void setStatusLineContrItem(StatusLineContrItem statusLineContrItem) {
        this.statusLineContrItem = statusLineContrItem;
    }

    public TitleLineContrItem getTitleLineContrItem() {
        return titleLineContrItem;
    }

    public void setTitleLineContrItem(TitleLineContrItem titleLineContrItem) {
        this.titleLineContrItem = titleLineContrItem;
    }

    /**
     * Get whether use click once operaiton
     *
     * @return <code>true</code> if use click once operation;<code>false</code> otherwise
     */
    public boolean isUseClickOnce() {
        return false;
    }
}
