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
package com.cubrid.common.ui.er.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.ConnectionCreationTool;

/**
 * The creation tool for connections in ER. With this tool, the user donot need to click and release
 * the left mouse button on the source edit part and then click and release the left mouse button on
 * the target edit part. When user click a space on the canvas, the tool will be release and the
 * canvas will be set by a default tool.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-3-11 created by Yu Guojia
 */
public class ERConnectionCreationTool extends ConnectionCreationTool {

    public ERConnectionCreationTool() {
        super();
    }

    /**
     * Constructs a new ConnectionCreationTool with the given factory.
     *
     * @param factory the creation factory
     */
    public ERConnectionCreationTool(CreationFactory factory) {
        super(factory);
    }

    /**
     * If use click a space, the tool will be released.
     *
     * @param button the button that was pressed
     * @return <code>true</code> if the button down was processed
     */
    public boolean handleButtonDown(int button) {
        EditPart editPart = getTargetEditPart();
        if (editPart == null) {
            getDomain().setActiveTool(getDomain().getDefaultTool());
            return true;
        }

        return super.handleButtonDown(button);
    }
}
