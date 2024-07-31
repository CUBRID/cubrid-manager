/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.dialog;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.IMultiQueryExecuter;
import com.cubrid.common.ui.query.control.MultiDBQueryResultComposite;
import com.cubrid.common.ui.query.control.MultiSQLQueryResultComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * MultiQuMultiQueryResultDialog Author Kevin.Wang
 *
 * <p>Create at 2014-4-10
 */
public class MultiQueryResultDialog {

    private Shell shell;
    private IMultiQueryExecuter multiQueryExecuter;
    private final QueryEditorPart queryEditorPart;
    private Composite container;

    /** @param parentShell */
    public MultiQueryResultDialog(Shell parentShell, QueryEditorPart queryEditorPart) {
        shell = new Shell(parentShell, SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX | SWT.MIN);
        shell.setLayoutData(new GridData(GridData.FILL_BOTH));
        shell.setText(Messages.qedit_multiSQLQueryComp_title_shell);
        shell.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_multi_run.png"));
        shell.setLayout(new FillLayout());

        container = new Composite(shell, SWT.None);
        container.setLayout(new GridLayout());
        this.queryEditorPart = queryEditorPart;
    }

    public void runQueries(CubridDatabase database, String queries) {
        if (multiQueryExecuter != null && !multiQueryExecuter.getControl().isDisposed()) {
            multiQueryExecuter.getControl().dispose();
        }

        multiQueryExecuter =
                new MultiSQLQueryResultComposite(
                        container, SWT.NONE, queryEditorPart, database, queries);

        multiQueryExecuter.getControl().setLayout(new GridLayout());
        multiQueryExecuter.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        multiQueryExecuter.initialize();
        /*Layout*/
        shell.setMinimized(false);
        container.layout();

        multiQueryExecuter.runQueries();
    }

    public void runQueries(List<CubridDatabase> queryDatabaseList, String queries) {
        if (multiQueryExecuter != null && !multiQueryExecuter.getControl().isDisposed()) {
            multiQueryExecuter.getControl().dispose();
        }

        multiQueryExecuter =
                new MultiDBQueryResultComposite(
                        container, SWT.NONE, queryEditorPart, queryDatabaseList, queries);

        multiQueryExecuter.getControl().setLayout(new GridLayout());
        multiQueryExecuter.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        multiQueryExecuter.initialize();
        /*Layout*/
        shell.setMinimized(false);
        container.layout();

        multiQueryExecuter.runQueries();
    }

    public boolean isDisposed() {
        if (shell != null && !shell.isDisposed()) {
            return false;
        }
        return true;
    }

    public void open() {
        if (shell == null || shell.isDisposed()) {
            return;
        }

        shell.open();
    }
}
