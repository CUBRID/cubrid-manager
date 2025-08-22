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
package com.cubrid.cubridmanager.ui.replication.editor.model;

import com.cubrid.cubridmanager.core.common.model.PropertyChangeProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * The top model object with the other container node
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class Diagram extends PropertyChangeProvider {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3257847701181051954L;

    public static final String PROP_STRUCTURE = "PROP_STRUCTURE";
    protected List<ContainerNode> childNodeList = new ArrayList<ContainerNode>();

    /**
     * add the node
     *
     * @param node ContainerNode
     */
    public void addNode(ContainerNode node) {
        node.setParent(this);
        childNodeList.add(node);
        fireStructureChange(PROP_STRUCTURE, childNodeList);
    }

    /**
     * remove the node
     *
     * @param node ContainerNode
     */
    public void removeNode(ContainerNode node) {
        childNodeList.remove(node);
        fireStructureChange(PROP_STRUCTURE, childNodeList);
    }

    public List<ContainerNode> getChildNodeList() {
        return childNodeList;
    }

    public void setChildNodeList(List<ContainerNode> childNodeList) {
        this.childNodeList = childNodeList;
    }
}
