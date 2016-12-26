/*
 * Copyright (C) 2008-2016 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * 
 * This action is responsible to parse selected sqlmap query in query editor.
 * 
 * @author Isaiah Choe 2016-12-26
 */
public class ParseSqlmapQueryAction extends
		FocusAction {

	public static final String ID = ParseSqlmapQueryAction.class.getName();

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param focusProvider
	 * @param text
	 * @param icon
	 */
	public ParseSqlmapQueryAction(Shell shell, Control focusProvider, String text,
			ImageDescriptor icon) {
		super(shell, focusProvider, text, icon);
		this.setId(ID);
	}

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param text
	 * @param icon
	 */
	public ParseSqlmapQueryAction(Shell shell, String text, ImageDescriptor icon) {
		this(shell, null, text, icon);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Control control = getFocusProvider();
		if (!(control instanceof StyledText)) {
			showNoSelectionQueryError();
			return;
		}
		StyledText stext = (StyledText) control;
		String data = stext.getSelectionText();
		if (StringUtil.isEmpty(data)) {
			showNoSelectionQueryError();
			return;
		}
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			showNoSelectionQueryError();
			return;
		}
		IEditorPart editor = window.getActivePage().getActiveEditor();
		if (editor == null || !(editor instanceof QueryEditorPart)) {
			showNoSelectionQueryError();
			return;
		}
		((QueryEditorPart) editor).parseSqlmapQuery();
	}

	private void showNoSelectionQueryError() {
		CommonUITool.openErrorBox(Messages.errDidNotSelectedQuery);
	}
}