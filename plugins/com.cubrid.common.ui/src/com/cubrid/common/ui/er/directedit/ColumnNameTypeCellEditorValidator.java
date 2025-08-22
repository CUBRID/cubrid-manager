/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.directedit;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.er.ERException;
import com.cubrid.common.ui.er.ValidationMessageHandler;
import com.cubrid.common.ui.er.model.ERTableColumn;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.slf4j.Logger;

/**
 * ICellValidator to validate direct edit values in the column label Collaborates with an instance
 * of ValidationMessageHandler.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class ColumnNameTypeCellEditorValidator implements ICellEditorValidator {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(ColumnNameTypeCellEditorValidator.class);

    private final ValidationMessageHandler messageHandler;
    private ColumnLabelHandler lableHandler;

    public ColumnNameTypeCellEditorValidator(
            ValidationMessageHandler validationMessageHandler, ERTableColumn erColumn) {
        this.messageHandler = validationMessageHandler;
        lableHandler = new ColumnLabelHandler(erColumn);
    }

    public String isValid(Object value) {
        try {
            lableHandler.checkFormat((String) value);
            lableHandler.setLatestData((String) value);
            return unsetMessageText();
        } catch (ERException e) {
            return setMessageText(e.getMessage());
        }
    }

    private String unsetMessageText() {
        messageHandler.reset();
        return null;
    }

    private String setMessageText(String text) {
        messageHandler.setMessageText(text);
        return text;
    }
}
