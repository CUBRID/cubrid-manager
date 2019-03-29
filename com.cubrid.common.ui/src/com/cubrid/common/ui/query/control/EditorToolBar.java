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
package com.cubrid.common.ui.query.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * 
 * A Toolbar Control to show the query editor toolItem and database selection
 * menu
 * 
 * @author pangqiren 2009-3-2
 */
public final class EditorToolBar extends ToolBar {
	
	private CLabel selectDbLabel;
	private final DatabaseNavigatorMenu dbMenu;
	private int SELECTDBLABEL_LENTH = 180;

	/**
	 * Create the composite
	 * 
	 * @param parent Composite
	 * @param editor QueryEditorPart
	 */
	public EditorToolBar(Composite parent, QueryEditorPart editor) {
		super(parent, SWT.WRAP | SWT.FLAT);
		CreateSelectItem(parent);
		dbMenu = loadDbNavigatorMenu();
		dbMenu.setEditor(editor);
		init(parent);
	}

	private void CreateSelectItem(Composite parent) {
		ToolItem selectDbItem = new ToolItem(this, SWT.SEPARATOR);
		Composite comp = createDropDownComp();
		selectDbItem.setControl(comp);
		selectDbItem.setWidth(SELECTDBLABEL_LENTH);
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);
	}

	private void init(Composite parent) {
		dbMenu.setParent(parent);
		dbMenu.setSelectDbLabel(selectDbLabel);
		dbMenu.loadDatabaseMenu();
	}

	/**
	 * 
	 * Load the database selection pop-up menu from extension points
	 * 
	 */
	public static DatabaseNavigatorMenu loadDbNavigatorMenu() {
		return ActionManager.getInstance().getMenuProvider().getDatabaseNavigatorMenu();
	}

	/**
	 * create drop down composite
	 * 
	 * @return comp composite
	 */
	private Composite createDropDownComp() {
		Composite comp = new Composite(this, SWT.NONE);
		final GridLayout gdLayout = new GridLayout(2, false);
		gdLayout.marginHeight = 0;
		gdLayout.marginWidth = 0;
		gdLayout.horizontalSpacing = -1;
		gdLayout.verticalSpacing = 0;
		comp.setLayout(gdLayout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		selectDbLabel = new CLabel(comp, SWT.CENTER | SWT.SHADOW_OUT);
		selectDbLabel.setLayoutData(CommonUITool.createGridData(1, 1, SELECTDBLABEL_LENTH, -1));
		selectDbLabel.setText(DatabaseNavigatorMenu.NO_DATABASE_SELECTED_LABEL);
		return comp;
	}

	/**
	 * when tree node in navigation view change, refresh the database list
	 */
	public void refresh() {
		dbMenu.refresh();
	}

	/**
	 * @see org.eclipse.swt.widgets.ToolBar#checkSubclass()
	 */
	
	protected void checkSubclass() {
		// do not check subclass
	}

	/**
	 * set the database
	 * 
	 * @param database CubridDatabase
	 */
	public void setDatabase(CubridDatabase database) {
		dbMenu.setDatabase(database);
	}

	/**
	 * get selected database
	 * 
	 * @return dbSelectd
	 */
	public CubridDatabase getSelectedDb() {
		return (CubridDatabase)dbMenu.getSelectedDb();
	}

	public CubridDatabase[] getDatabaseOnMenu() {
		List<CubridDatabase> databases = new ArrayList<CubridDatabase>();
		for (MenuItem item : dbMenu.getDbSelectionMenu().getItems()) {
			if (item.getStyle() != SWT.RADIO)
				continue;

			if (!item.getEnabled())
				continue;
			databases.add(((DatabaseMenuItem)item).getDatabase());
		}
		return databases.toArray(new CubridDatabase[0]);
	}

	/**
	 * inject custom operation when database changed
	 * 
	 * @param listener Listener
	 */
	public void addDatabaseChangedListener(Listener listener) {
		dbMenu.addDatabaseChangedListener(listener);
	}

	/**
	 * if no database selected
	 * 
	 * @return boolean
	 */
	public boolean isNull() {
		return dbMenu.isNull();
	}

}