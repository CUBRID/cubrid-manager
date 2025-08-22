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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * This is a container node model object with other leafnodes
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ContainerNode extends Node {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3257847675427893304L;

    public static final String PROP_STRUCTURE = "PROP_STRUCTURE";
    protected List<LeafNode> childNodeList = new ArrayList<LeafNode>();

    public ContainerNode() {
        super();
        setSize(new Dimension(200, 220));
    }

    /**
     * add the childNode
     *
     * @param leafNode LeafNode
     */
    public void addChildNode(LeafNode leafNode) {
        leafNode.setParent(this);
        this.childNodeList.add(leafNode);
        fireStructureChange(PROP_STRUCTURE, leafNode);
    }

    /**
     * add the childNode
     *
     * @param leafNode LeafNode
     * @param index int
     */
    public void addChildNode(int index, LeafNode leafNode) {
        leafNode.setParent(this);
        this.childNodeList.add(index, leafNode);
        fireStructureChange(PROP_STRUCTURE, leafNode);
    }

    /**
     * remove the childNode
     *
     * @param leafNode LeafNode
     */
    public void removeChildNode(LeafNode leafNode) {
        this.childNodeList.remove(leafNode);
        fireStructureChange(PROP_STRUCTURE, leafNode);
    }

    public List<LeafNode> getChildNodeList() {
        return childNodeList;
    }

    public void setChildNodeList(List<LeafNode> childNodeList) {
        this.childNodeList = childNodeList;
    }
}
