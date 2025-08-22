/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.database.erwin.model;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.StringUtil;
import java.util.LinkedList;
import java.util.List;

/**
 * Sub class from SchemaInfo for ERWin entity(table).
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-7-21 created by Yu Guojia
 */
public class ERWinSchemaInfo extends SchemaInfo {

    private String logicalName;
    private List<ERWinDBAttribute> erwinAttributes = new LinkedList<ERWinDBAttribute>();

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String logicalName) {
        this.logicalName = logicalName;
    }

    public List<ERWinDBAttribute> getERWinAttributes() {
        return erwinAttributes;
    }

    public void setERWinAttributes(List<ERWinDBAttribute> erwinAttributes) {
        this.erwinAttributes = erwinAttributes;
    }

    public ERWinDBAttribute getERWinDBAttr(String attrName) {
        for (ERWinDBAttribute attr : erwinAttributes) {
            if (StringUtil.isEqual(attr.getName(), attrName)) {
                return attr;
            }
        }
        return null;
    }

    /**
     * add an instance attribute to list
     *
     * @param attribute ERWinDBAttribute the reference of ERWinDBAttribute object
     */
    public void addAttribute(ERWinDBAttribute attribute) {
        super.addAttribute(attribute);
        erwinAttributes.add(attribute);
        attribute.setClassAttribute(false);
    }
}
