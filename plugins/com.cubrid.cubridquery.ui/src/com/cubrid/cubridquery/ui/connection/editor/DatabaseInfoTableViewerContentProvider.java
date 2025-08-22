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
package com.cubrid.cubridquery.ui.connection.editor;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridquery.ui.spi.model.DatabaseUIWrapper;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DatabaseInfoTableViewerContentProvider implements IStructuredContentProvider {
    public void dispose() {
        noOp();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof List) {
            List<DatabaseUIWrapper> outputs = new ArrayList<DatabaseUIWrapper>();

            List list = (List) newInput;
            for (Object obj : list) {
                if (obj instanceof DatabaseUIWrapper) {
                    break;
                } else if (obj instanceof CubridDatabase) {
                    DatabaseUIWrapper wrapper = new DatabaseUIWrapper((CubridDatabase) obj);
                    outputs.add(list.indexOf(obj), wrapper);
                }
            }
            if (!outputs.isEmpty()) {
                list.clear();
                list.addAll(outputs);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List) inputElement).toArray();
        }
        return new DatabaseUIWrapper[0];
    }
}
