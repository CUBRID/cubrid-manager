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
package com.cubrid.cubridmanager.ui.replication.editor.parts;

import com.cubrid.cubridmanager.ui.replication.editor.ReplicationEditor;
import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Diagram;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * This part factory is responsible to create editpart according to model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class PartFactory implements EditPartFactory {

    private final ReplicationEditor replEditor;

    public PartFactory(ReplicationEditor editor) {
        replEditor = editor;
    }

    /**
     * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
     *     java.lang.Object)
     * @param context The context in which the EditPart is being created, such as its parent.
     * @param model the model of the EditPart being created
     * @return EditPart the new EditPart
     */
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;
        if (model instanceof Diagram) {
            part = new DiagramPart(replEditor);
        } else if (model instanceof ContainerNode) {
            part = new ContainerNodePart(replEditor);
        } else if (model instanceof LeafNode) {
            part = new LeafNodePart(replEditor);
        } else if (model instanceof ArrowConnection) {
            {
                part = new ConnectionPart(replEditor);
            }
        }
        if (part != null) {
            part.setModel(model);
        }
        return part;
    }
}
