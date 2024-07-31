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
package com.cubrid.common.ui.spi.part;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;

/**
 * All editor part related with CUBRID node in CUBRID Manager will extend this class
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public abstract class CubridEditorPart extends EditorPart implements ICubridNodeChangedListener {

    private final Logger LOGGER = LogUtil.getLogger(getClass());

    protected ICubridNode cubridNode;

    /**
     * Initializes this editor with the given editor site and input.
     *
     * @param site the editor site
     * @param input the editor input
     * @exception PartInitException if this editor was not initialized successfully
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        if (input != null && input.getToolTipText() != null) {
            setTitleToolTip(input.getToolTipText());
        }
        String title = this.getPartName();
        CubridServer server = null;
        String serverName = "";
        String port = "";
        if (input instanceof ICubridNode) {
            cubridNode = (ICubridNode) input;
            server = cubridNode.getServer();
            if (null != server) {
                serverName = server.getLabel();
                port = server.getMonPort();
            }
        }
        if (input != null) {
            if (title == null) {
                if (null == server) {
                    setPartName(input.getName());
                } else {
                    setPartName(input.getName() + "@" + serverName + ":" + port);
                }
            } else {
                if (null == server) {
                    setPartName(title + " - " + input.getName());
                } else {
                    setPartName(title + " - " + input.getName() + "@" + serverName + ":" + port);
                }
            }
        }
        CubridNodeManager.getInstance().addCubridNodeChangeListener(this);
    }

    /** Dispose the resource and object */
    public void dispose() {
        super.dispose();

        CubridNodeManager.getInstance().removeCubridNodeChangeListener(this);
        // don't open a new now if the close one is last one TOOLS-1079
        //		IWorkbenchPage page =
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        //		if (page != null && page.getEditorReferences().length == 0) {
        //			QueryUnit unit = new QueryUnit();
        //			unit.setDatabase(null);
        //			try {
        //				page.openEditor(unit, QueryEditorPart.ID);
        //			} catch (PartInitException e) {
        //				LOGGER.error(e.getMessage(), e);
        //			}
        //		}
    }

    /** Call this method when this editor is focus */
    public void setFocus() {
        if (null != cubridNode) {
            LayoutManager.getInstance()
                    .getTitleLineContrItem()
                    .changeTitleForViewOrEditPart(cubridNode, this);
            LayoutManager.getInstance()
                    .getStatusLineContrItem()
                    .changeStuatusLineForViewOrEditPart(cubridNode, this);
        }
    }

    /**
     * close the editors which are the same database
     *
     * @param event
     * @param database
     */
    public void close(CubridNodeChangedEvent event, CubridDatabase database) {
        ICubridNode cubridNode = event.getCubridNode();
        CubridNodeChangedEventType eventType = event.getType();
        if (cubridNode == null || eventType == null) {
            return;
        }
        if (event.getSource() instanceof CubridDatabase) {
            CubridDatabase eventCubridDatabase = (CubridDatabase) event.getSource();
            if (eventCubridDatabase.equals(database)) {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window == null) {
                    return;
                }
                window.getActivePage().closeEditor(this, true);
            }
        }
    }

    /**
     * close the editors which are the same server
     *
     * @param event
     * @param database
     */
    public void close(CubridNodeChangedEvent event, CubridServer server) {
        ICubridNode cubridNode = event.getCubridNode();
        CubridNodeChangedEventType eventType = event.getType();
        if (cubridNode == null || eventType == null) {
            return;
        }
        if (event.getSource() instanceof CubridServer) {
            CubridServer eventCubridServer = (CubridServer) event.getSource();
            if (eventCubridServer.equals(server)) {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window == null) {
                    return;
                }
                window.getActivePage().closeEditor(this, true);
            }
        }
    }
}
