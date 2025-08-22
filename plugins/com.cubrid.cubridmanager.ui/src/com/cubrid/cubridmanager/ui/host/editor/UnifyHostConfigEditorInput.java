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
package com.cubrid.cubridmanager.ui.host.editor;

import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.cubridmanager.ui.host.Messages;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author fulei
 * @version 1.0 - 2013-1-31 created by fulei
 */
public class UnifyHostConfigEditorInput implements IEditorInput {

    private final CubridServer[] cubridServers;
    private boolean editCubridConf = false;
    private boolean editBrokerConf = false;
    private boolean editCMConf = false;
    private boolean editHAConf = false;
    private boolean editACLConf = false;

    private int brokerConfPropertyCount = 0;
    private int cubridConfPropertyCount = 0;
    private int cubridCMConfPropertyCount = 0;

    public UnifyHostConfigEditorInput(final CubridServer[] cubridServers) {
        this.cubridServers = cubridServers;
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter.equals(CubridServer[].class)) {
            return cubridServers;
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return Messages.unifyHostConfigEditorTitle;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText() {
        return this.getName();
    }

    public boolean isEditCubridConf() {
        return editCubridConf;
    }

    public void setEditCubridConf(boolean editCubridConf) {
        this.editCubridConf = editCubridConf;
    }

    public boolean isEditBrokerConf() {
        return editBrokerConf;
    }

    public void setEditBrokerConf(boolean editBrokerConf) {
        this.editBrokerConf = editBrokerConf;
    }

    public boolean isEditCMConf() {
        return editCMConf;
    }

    public void setEditCMConf(boolean editCMConf) {
        this.editCMConf = editCMConf;
    }

    public boolean isEditHAConf() {
        return editHAConf;
    }

    public void setEditHAConf(boolean editHAConf) {
        this.editHAConf = editHAConf;
    }

    public boolean isEditACLConf() {
        return editACLConf;
    }

    public void setEditACLConf(boolean editACLConf) {
        this.editACLConf = editACLConf;
    }

    public CubridServer[] getCubridServers() {
        return cubridServers;
    }

    public int getBrokerConfPropertyCount() {
        return brokerConfPropertyCount;
    }

    public void setBrokerConfPropertyCount(int brokerConfPropertyCount) {
        this.brokerConfPropertyCount = brokerConfPropertyCount;
    }

    public int getCubridConfPropertyCount() {
        return cubridConfPropertyCount;
    }

    public void setCubridConfPropertyCount(int cubridConfPropertyCount) {
        this.cubridConfPropertyCount = cubridConfPropertyCount;
    }

    public int getCubridCMConfPropertyCount() {
        return cubridCMConfPropertyCount;
    }

    public void setCubridCMConfPropertyCount(int cubridCMConfPropertyCount) {
        this.cubridCMConfPropertyCount = cubridCMConfPropertyCount;
    }

    /**
     * get loading data task count
     *
     * @return
     */
    public int getTaskCountValue() {
        int editTypeCount = 0;
        if (isEditCubridConf()) {
            editTypeCount++;
        }
        if (isEditBrokerConf()) {
            editTypeCount++;
        }
        if (isEditCMConf()) {
            editTypeCount++;
        }
        if (isEditHAConf()) {
            editTypeCount++;
        }
        if (isEditACLConf()) {
            editTypeCount++;
        }

        return editTypeCount * cubridServers.length;
    }
}
