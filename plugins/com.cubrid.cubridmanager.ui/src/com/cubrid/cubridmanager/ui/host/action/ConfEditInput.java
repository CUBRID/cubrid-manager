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
package com.cubrid.cubridmanager.ui.host.action;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * This is a edit input for Configuration editor input
 *
 * @author lizhiqiang
 * @version 1.0 - 2011-4-2 created by lizhiqiang
 */
public class ConfEditInput implements IEditorInput {
    private String id;
    private String name;
    private String toolTipText;
    private String serverName;
    private String serverPort;
    private ServerInfo serverInfo;

    /**
     * Returns whether the editor input exists.
     *
     * @return <code>true</code> if the editor input exists; <code>false</code> otherwise
     */
    public boolean exists() {
        return false;
    }

    /**
     * Returns an object that can be used to save the state of this editor input.
     *
     * @return the persistable element, or <code>null</code> if this editor input cannot be
     *     persisted
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * Returns the name of this editor input for display purposes.
     *
     * @return the name string; never <code>null</code>;
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the tool tip text for this editor input. This text is used to differentiate between
     * two input with the same name. For instance, MyClass.java in folder X and MyClass.java in
     * folder Y. The format of the text varies between input types.
     *
     * @return the tool tip text; never <code>null</code>.
     */
    public String getToolTipText() {
        return toolTipText;
    }

    /**
     * Returns the image descriptor for this input.
     *
     * @return the image descriptor for this input; may be <code>null</code> if there is no image.
     */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * Get adapter object
     *
     * @param adapter the adapter
     * @return the adapter object
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }

    /**
     * Get the serverName
     *
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /** @param serverName the serverName to set */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the serverPort
     *
     * @return the serverPort
     */
    public String getServerPort() {
        return serverPort;
    }

    /** @param serverPort the serverPort to set */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Get the serverInfo
     *
     * @return the serverInfo
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    /** @param serverInfo the serverInfo to set */
    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * Get the id
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /** @param id the id to set */
    public void setId(String id) {
        this.id = id;
    }

    /** @param toolTipText the toolTipText to set */
    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }

    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }
}
