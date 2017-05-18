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

import java.sql.SQLException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.query.control.EditorToolBar;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * A Toolbar Control to show the ERD toolItem
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-11-29 created by Yu Guojia
 */
public class ERSchemaToolBar extends
		ToolBar {
	private final Logger LOGGER = LogUtil.getLogger(getClass());
	private ERSchemaEditor erSchemaEditor;
	private ToolItem selectDbItem;
	private CLabel selectModelLabel;
	private Button dropDownButton;
	private PhysicalLogicalNavigatorMenu physicalLogicalModelViewsMenu;
	private ToolItem physicalLogicalMapItem;

	private ToolItem openItem;
	private ToolItem saveItem;
	private ToolItem saveAsItem;
	private ToolItem ddlCompareItem;
	private ToolItem syncCommentItem;
	private ToolItem generateSyncCommentSQLItem;
	private ToolItem connectLineItem;
	private ToolItem newTableItem;
	private ToolItem zoomInItem;
	private ToolItem zoomOutItem;
	private ToolItem autoLayoutItem;
	private CLabel selectDbLabel;
	private ToolItem searchItem;
	private boolean loginState;

	/**
	 * Constructor of ER tool bar
	 *
	 * @param parent
	 * @param style
	 */
	public ERSchemaToolBar(Composite parent, int style, ERSchemaEditor erSchemaEditor) {
		super(parent, style);
		this.erSchemaEditor = erSchemaEditor;
	}

	protected void checkSubclass() {
	}

	/**
	 * Init items on the tool bar
	 */
	public void init() {
		loginState = erSchemaEditor.getDatabase().isLogined();
		selectDbItem = new ToolItem(this, SWT.SEPARATOR);
		Composite comp = createSelectDbLabel();
		selectDbItem.setControl(comp);
		selectDbItem.setWidth(180);

		//model change
		ToolItem selectModelItem = new ToolItem(this, SWT.SEPARATOR);
		Composite selectModelComp = createDropDownComp();
		selectModelItem.setControl(selectModelComp);
		selectModelItem.setWidth(100);
		physicalLogicalModelViewsMenu = new PhysicalLogicalNavigatorMenu(this, erSchemaEditor,
				selectModelLabel);
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);

		//physical/logical data type map item
		physicalLogicalMapItem = new ToolItem(this, SWT.PUSH);
		physicalLogicalMapItem.setImage(CommonUIPlugin.getImage("/icons/er/set_global_view_model.png"));
		physicalLogicalMapItem.setToolTipText(Messages.btnNameSetPhysicalLogicalMap);
		physicalLogicalMapItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.openPhysicalLogicalMapDialog();
			}
		});
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);

		// open erwin xml file
		openItem = new ToolItem(this, SWT.PUSH);
		openItem.setImage(CommonUIPlugin.getImage("/icons/queryeditor/file_open.png"));
		openItem.setToolTipText(Messages.btnTipOpenFile);
		openItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.doOpen();
			}
		});

		// save erwin xml file
		saveItem = new ToolItem(this, SWT.PUSH);
		saveItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_save.png"));
		saveItem.setToolTipText(Messages.btnTipSaveFile);
		saveItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.doSave(new NullProgressMonitor());
			}
		});

		// save as erwin xml file
		saveAsItem = new ToolItem(this, SWT.PUSH);
		saveAsItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_saveas.png"));
		saveAsItem.setToolTipText(Messages.btnTipSaveAsFile);
		saveAsItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.doSaveAs();
			}
		});
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);

		newTableItem = new ToolItem(this, SWT.PUSH);
		newTableItem.setToolTipText(Messages.btnTipCreateTable);
		final CombinedTemplateCreationEntry tableEntry = PaletteViewerCreator.getTableEntry(erSchemaEditor.getERSchema());
		newTableItem.setImage(tableEntry.getSmallIcon().createImage());
		newTableItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.getEditDomain().setActiveTool(tableEntry.createTool());
			}
		});
		connectLineItem = new ToolItem(this, SWT.PUSH);
		connectLineItem.setToolTipText(Messages.btnTipConnection);
		final ConnectionCreationToolEntry conn = PaletteViewerCreator.getConnectionEntry();
		connectLineItem.setImage(conn.getSmallIcon().createImage());
		connectLineItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.getEditDomain().setActiveTool(conn.createTool());
			}
		});
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);

		// Zoom In
		zoomInItem = new ToolItem(this, SWT.PUSH);
		zoomInItem.setImage(CommonUIPlugin.getImage("icons/action/zoom_in.png"));
		zoomInItem.setToolTipText(Messages.actionZoomIn);
		zoomInItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.doZoomIn();
			}
		});

		// Zoom Out
		zoomOutItem = new ToolItem(this, SWT.PUSH);
		zoomOutItem.setImage(CommonUIPlugin.getImage("icons/action/zoom_out.png"));
		zoomOutItem.setToolTipText(Messages.actionZoomOut);
		zoomOutItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				erSchemaEditor.doZoomOut();
			}
		});
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);

		if(!erSchemaEditor.getDatabase().isVirtual()){
			// compare ddl sql
			ddlCompareItem = new ToolItem(this, SWT.PUSH);
			ddlCompareItem.setImage(CommonUIPlugin.getImage("icons/action/schema_compare_wizard.png"));
			ddlCompareItem.setToolTipText(Messages.btnCompareDDL);
			ddlCompareItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					erSchemaEditor.compareDDL2DB();
				}
			});
			//sync comment to db
			syncCommentItem = new ToolItem(this, SWT.PUSH);
			syncCommentItem.setImage(CommonUIPlugin.getImage("icons/er/sync_comments_to_db.png"));
			syncCommentItem.setToolTipText(Messages.btnTipSyncComments);
			syncCommentItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					erSchemaEditor.syncComments();
				}
			});
		}
		//generate SQL for synchronizing comments to db
		generateSyncCommentSQLItem = new ToolItem(this, SWT.PUSH);
		generateSyncCommentSQLItem.setImage(CommonUIPlugin.getImage("icons/er/generate_sync_comments_sqls.png"));
		generateSyncCommentSQLItem.setToolTipText(Messages.btnTipGenerateSyncCommentsSQL);
		generateSyncCommentSQLItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					erSchemaEditor.generateSyncCommentSQL();
				} catch (PartInitException e) {
					LOGGER.error(e.getMessage());
					CommonUITool.openErrorBox(erSchemaEditor.getSite().getShell(), e.getMessage());
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
					CommonUITool.openErrorBox(erSchemaEditor.getSite().getShell(), e.getMessage());
				}
			}
		});
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);
		
		// automatic layout
		autoLayoutItem = new ToolItem(this, SWT.SEPARATOR);
		Composite autoLayoutComp = createAutoLayoutComp();
		autoLayoutItem.setControl(autoLayoutComp);
		autoLayoutItem.setWidth(120);
		new ToolItem(this, SWT.SEPARATOR | SWT.VERTICAL);

		// search comp
		searchItem = new ToolItem(this, SWT.SEPARATOR);
		Composite searchcomp = createSearchComp();
		searchItem.setControl(searchcomp);
		searchItem.setWidth(150);

	}

	/**
	 * create searching table text composite
	 *
	 * @return comp composite
	 */
	private Composite createSearchComp() {

		Composite comp = new Composite(this, SWT.NONE);
		final GridLayout gdLayout = new GridLayout(1, true);
		gdLayout.marginHeight = 1;
		gdLayout.marginWidth = 1;
		gdLayout.horizontalSpacing = 1;
		gdLayout.verticalSpacing = 1;
		comp.setLayout(gdLayout);
		// comp.setLayoutData(new GridData(GridData.RIGHT, SWT.CENTER, false,
		// false));
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END,
				GridData.VERTICAL_ALIGN_CENTER, false, false));

		final Text text = new Text(comp, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		text.setToolTipText(Messages.tipSearchTableName);
		text.setLayoutData(CommonUITool.createGridData(1, 1, 140, 14));
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String keyWord = text.getText().trim();
				erSchemaEditor.doSearchTables(keyWord);
			}
		});

		return comp;
	}

	/**
	 * create drop down composite
	 *
	 * @return comp composite
	 */
	private Composite createDropDownComp() {
		Composite comp = new Composite(this, SWT.LEFT_TO_RIGHT);
		final GridLayout gdLayout = new GridLayout(2, false);
		gdLayout.marginHeight = 0;
		gdLayout.marginWidth = 0;
		gdLayout.horizontalSpacing = -1;
		gdLayout.verticalSpacing = 0;
		comp.setLayout(gdLayout);
		comp.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
		selectModelLabel = new CLabel(comp, SWT.CENTER | SWT.SHADOW_OUT);
		selectModelLabel.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));

		dropDownButton = new Button(comp, SWT.FLAT | SWT.ARROW | SWT.DOWN);
		dropDownButton.setLayoutData(CommonUITool.createGridData(1, 1, 20, -1));
		dropDownButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleSelectionEvent();
			}
		});
		return comp;
	}

	/**
	 * Handle selection event.
	 *
	 */
	private void handleSelectionEvent() {
		Rectangle rect = selectModelLabel.getBounds();
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = selectModelLabel.toDisplay(pt);
		physicalLogicalModelViewsMenu.getModelSelectionMenu().setLocation(pt);
		physicalLogicalModelViewsMenu.getModelSelectionMenu().setVisible(true);
	}

	/**
	 * create select Db Label composite
	 *
	 * @return comp composite
	 */
	private Composite createSelectDbLabel() {
		Composite comp = new Composite(this, SWT.NONE);
		final GridLayout gdLayout = new GridLayout(2, false);
		gdLayout.marginHeight = 0;
		gdLayout.marginWidth = 0;
		gdLayout.horizontalSpacing = -1;
		gdLayout.verticalSpacing = 0;
		comp.setLayout(gdLayout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		selectDbLabel = new CLabel(comp, SWT.CENTER | SWT.SHADOW_OUT);
		selectDbLabel.setLayoutData(CommonUITool.createGridData(1, 1, 180, -1));
		selectDbLabel.setText(getDBLabel());
		setDBLabelColor();

		return comp;
	}

	private void setDBLabelColor() {
		boolean isDbaAuth = false;
		String userName = erSchemaEditor.getDatabase().getUserName();
		if (userName != null && userName.trim().length() > 0) {
			if (userName.equalsIgnoreCase(erSchemaEditor.DBAUSER_NAME)) {
				isDbaAuth = true;
			}
		}

		if (isDbaAuth) {
			selectDbLabel.setBackground(
					new Color[] { Display.getDefault().getSystemColor(SWT.COLOR_WHITE),
							Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
							Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
							Display.getDefault().getSystemColor(SWT.COLOR_WHITE) }, new int[] { 33,
							67, 100 });
		} else {
			selectDbLabel.setBackground((Color) null);
		}
	}

	public void refresh() {
		String label = selectDbLabel.getText();
		boolean loginStateChanged = loginState ^ erSchemaEditor.getDatabase().isLogined();
		boolean labelChanged = !StringUtil.isEqual(label, getDBLabel());
		if (loginStateChanged || labelChanged) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					selectDbLabel.setText(getDBLabel());
					setDBLabelColor();
				}
			});
		}
		loginState = erSchemaEditor.getDatabase().isLogined();
	}

	/**
	 * Get label string for the ERD tool bar on the current database.
	 *
	 * @return
	 */
	private String getDBLabel() {
		return EditorToolBar.loadDbNavigatorMenu().getDatabaseLabel(erSchemaEditor.getDatabase());
	}

	/**
	 * Create auto layout button
	 *
	 * @return comp composite
	 */
	private Composite createAutoLayoutComp() {
		Composite comp = new Composite(this, SWT.NONE);
		final GridLayout gdLayout = new GridLayout(1, false);
		gdLayout.marginHeight = 0;
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		
		comp.setLayout(gdLayout);
		comp.setLayoutData(gridData);

		final Button autoLayoutButton = new Button(comp, SWT.PUSH | SWT.CENTER | SWT.FILL);
		autoLayoutButton.setLayoutData(gridData);
		autoLayoutButton.setText(Messages.btnAutoLayout);
		autoLayoutButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ERSchema er = erSchemaEditor.getERSchema();
				er.setTmpAutoLayoutAndFire();
			}
		});

		return comp;
	}
}
