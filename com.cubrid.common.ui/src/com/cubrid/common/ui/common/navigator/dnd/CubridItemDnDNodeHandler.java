/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.common.navigator.dnd;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.model.ICubridNode;

/**
 * CubridItemDnDNodeHandler for not group displayed.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-4-1 created by Kevin Cao
 */
public class CubridItemDnDNodeHandler extends CubridDefaultDnDNodeHandler {

    /**
     * CubridItemDnDNodeHandler constructor
     *
     * @param nv CubridNavigatorView
     */
    public CubridItemDnDNodeHandler(CubridNavigatorView nv) {
        super(nv);
    }

    /**
     * Handle the node to be DND.
     *
     * @param dragNode the drag node
     * @param dropNode the drop node
     * @param insertBefore insert into the drop node before or after
     * @param dropOperation the drop operation type <code>DND.DROP_COPY</code> <code>DND.DROP_MOVE
     *     </code>
     * @return boolean whether to handle with the drop
     */
    public boolean handle(
            ICubridNode dragNode, ICubridNode dropNode, boolean insertBefore, int dropOperation) {
        int index = 0;
        if (dropNode == null) {
            index = navigator.getGroupNodeManager().getAllGroupItems().size();
        } else {
            index = navigator.getGroupNodeManager().getAllGroupItems().indexOf(dropNode);
            if (!insertBefore) {
                index++;
            }
        }
        navigator.getGroupNodeManager().changeItemPosition(dragNode, index);
        return true;
    }
}
