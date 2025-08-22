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

import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import com.cubrid.common.ui.spi.model.ICubridNode;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * All view part related with CUBRID node will extend this class
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public abstract class CubridViewPart extends ViewPart implements ICubridNodeChangedListener {

    protected ICubridNode cubridNode = null;

    /**
     * Initializes this view with the given view site.
     *
     * @param site the view site
     * @exception PartInitException if this view was not initialized successfully
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        setCubridNode(LayoutManager.getInstance().getCurrentSelectedNode());
        CubridNodeManager.getInstance().addCubridNodeChangeListener(this);
        String title = this.getPartName();
        if (null != cubridNode) {
            String serverName = cubridNode.getServer().getLabel();
            String port = cubridNode.getServer().getMonPort();
            if (title != null) {
                this.setPartName(
                        title + " - " + cubridNode.getLabel() + "@" + serverName + ":" + port);
            }
        }
    }

    /**
     * Get the CUBRID node of this view part
     *
     * @return the CUBRID node
     */
    public ICubridNode getCubridNode() {
        return this.cubridNode;
    }

    /**
     * Set cubrid node
     *
     * @param cubridNode the ICubridNode object
     */
    public void setCubridNode(ICubridNode cubridNode) {
        this.cubridNode = cubridNode;
    }

    /** Dispose the resource */
    public void dispose() {
        CubridNodeManager.getInstance().removeCubridNodeChangeListener(this);
    }

    /** Call this method when this viewpart is focus */
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
}
