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
package com.cubrid.cubridquery.ui.spi;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.ui.spi.model.NodeType;

/**
 * This class is responsible to manage cubrid node type
 *
 * @author pangqiren
 * @version 1.0 - 2009-7-14 created by pangqiren
 */
public final class CubridNodeTypeManager {
    private CubridNodeTypeManager() {
        noOp();
    }

    static final String[] CAN_REFRESH_NODE_TYPE_ARR = {
        NodeType.DATABASE,
        NodeType.TABLE_FOLDER,
        NodeType.USER_PARTITIONED_TABLE_FOLDER,
        NodeType.USER_PARTITIONED_TABLE,
        NodeType.VIEW_FOLDER,
        NodeType.SYSTEM_TABLE_FOLDER,
        NodeType.SYSTEM_TABLE,
        NodeType.USER_TABLE,
        NodeType.SYSTEM_VIEW_FOLDER,
        NodeType.SYSTEM_VIEW,
        NodeType.USER_VIEW,
        NodeType.STORED_PROCEDURE_FOLDER,
        NodeType.STORED_PROCEDURE_FUNCTION_FOLDER,
        NodeType.STORED_PROCEDURE_PROCEDURE_FOLDER,
        NodeType.TRIGGER_FOLDER,
        NodeType.SYNONYM_FOLDER,
        NodeType.SERIAL_FOLDER,
        NodeType.USER_FOLDER
    };

    /**
     * Return whether this node type can be refresh
     *
     * @param nodeType the String
     * @return <code>true</code> if it can be refreshed;<code>false</code> otherwise
     */
    public static boolean isCanRefresh(String nodeType) {
        for (String type : CAN_REFRESH_NODE_TYPE_ARR) {
            if (type.equals(nodeType)) {
                return true;
            }
        }
        return false;
    }
}
