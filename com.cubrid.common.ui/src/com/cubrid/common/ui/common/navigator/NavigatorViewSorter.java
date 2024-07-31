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

import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Navigator view sorter
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-13 created by pangqiren
 */
public class NavigatorViewSorter extends ViewerSorter {

    /**
     * Do category, if in the same category and returned value bigger than 0,these ICubridNode do
     * not sort, their sorter is added sorter
     *
     * @param node ICubridNode
     * @return int
     */
    protected int category(ICubridNode node) {
        String type = node.getType();
        if (NodeType.TABLE_FOLDER.equals(type)
                || NodeType.VIEW_FOLDER.equals(type)
                || NodeType.SERIAL_FOLDER.equals(type)
                || NodeType.TRIGGER_FOLDER.equals(type)
                || NodeType.SYNONYM_FOLDER.equals(type)
                || NodeType.STORED_PROCEDURE_FOLDER.equals(type)) {
            return 1;
        }
        if (NodeType.STORED_PROCEDURE_FUNCTION_FOLDER.equals(type)
                || NodeType.STORED_PROCEDURE_PROCEDURE_FOLDER.equals(type)) {
            return 2;
        }
        if (NodeType.TABLE_COLUMN_FOLDER.equals(type)
                || NodeType.TABLE_INDEX_FOLDER.equals(type)
                || NodeType.USER_PARTITIONED_TABLE.equals(type)) {
            return 3;
        }
        if (NodeType.TABLE_COLUMN.equals(type)) {
            return 4;
        }
        return -1;
    }

    private int order = 1;

    public void changeOrder() {
        order *= -1;
    }

    /**
     * @see <code>ViewerSorter</code>
     * @param viewer Viewer
     * @param e1 Object
     * @param e2 Object
     * @return int
     */
    public int compare(Viewer viewer, Object e1, Object e2) { // FIXME reduce code

        if (e1 == null && e2 == null) {
            return 0;
        }
        if (e1 == null) {
            return -1;
        }
        if (e2 == null) {
            return -1;
        }
        if (!(e1 instanceof ICubridNode) || !(e2 instanceof ICubridNode)) {
            return 0;
        }
        ICubridNode node1 = (ICubridNode) e1;
        ICubridNode node2 = (ICubridNode) e2;

        // If group node, not sorting
        if (node1 instanceof CubridGroupNode) {
            return 0;
        }

        // If cubrid group node ,use the list's order
        else if ((node1 instanceof CubridServer) || (node1 instanceof CubridDatabase)) {
            return order * node1.getLabel().compareTo(node2.getLabel());
        }
        int cat1 = category(node1);
        int cat2 = category(node2);
        if (cat1 > 0 && cat2 > 0) {
            return cat1 - cat2;
        }
        if (cat1 != cat2) {
            return cat1 - cat2;
        }
        return node1.compareTo(node2);
    }
}
