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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Filter setting action for tree
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 */
public class TopGroupAction extends Action {

    public static final String ID = TopGroupAction.class.getName();
    private CubridNavigatorView cubridNavigatorView;

    public TopGroupAction(
            String text, ImageDescriptor image, CubridNavigatorView cubridNavigatorView) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
        this.cubridNavigatorView = cubridNavigatorView;
    }

    /**
     * Set the navigator view
     *
     * @param cubridNavigatorView CubridNavigatorView
     */
    public void setNavigatorView(CubridNavigatorView cubridNavigatorView) {
        this.cubridNavigatorView = cubridNavigatorView;
        if (cubridNavigatorView != null) {
            this.setChecked(cubridNavigatorView.isShowGroup());
        }
    }

    /** Filter */
    public void run() {
        cubridNavigatorView.setShowGroup(true);
        this.setChecked(true);
    }
}
