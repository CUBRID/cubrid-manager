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
package com.cubrid.common.ui.cubrid.trigger.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.trigger.Messages;
import com.cubrid.common.ui.cubrid.trigger.action.AlterTriggerAction;
import com.cubrid.common.ui.cubrid.trigger.action.DropTriggerAction;
import com.cubrid.common.ui.cubrid.trigger.action.NewTriggerAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.OpenTriggerDetailInfoPartProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * Trigger Dashboard
 *
 * @author fulei
 * @version 1.0 - 2013-1-9 created by fulei
 */
public class TriggerDashboardEditorPart extends CubridEditorPart {

	public static final String ID = TriggerDashboardEditorPart.class.getName();
	private boolean triggerChangeFlag;
	private CubridDatabase database;
	private List<Trigger> triggerList = null;
	private TableViewer triggersDetailInfoTable;
	
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT | SWT.FLAT);
		toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		
		ToolItem refreshItem = new ToolItem(toolBar,SWT.PUSH);  
		refreshItem.setText(com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn); 
		refreshItem.setToolTipText(com.cubrid.common.ui.cubrid.table.Messages.tablesDetailInfoPartRefreshBtn);
		refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				refresh();
			}
		});
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem addSerailItem = new ToolItem(toolBar, SWT.NONE);
		addSerailItem.setText(Messages.triggersDetailInfoPartCreateTriggerBtn);
		addSerailItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_add.png"));
		addSerailItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				addTrigger();
			}
		});
		
		ToolItem editSerailItem = new ToolItem(toolBar, SWT.NONE);
		editSerailItem.setText(Messages.triggersDetailInfoPartEditTriggerBtn);
		editSerailItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_edit.png"));
		editSerailItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				editTrigger();
			}
		});
		
		ToolItem dropSerailItem = new ToolItem(toolBar, SWT.NONE);
		dropSerailItem.setText(Messages.triggersDetailInfoPartDropTriggerBtn);
		dropSerailItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_delete.png"));
		dropSerailItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				dropTrigger();
			}
		});;
		
		createTriggersDetailInfoTable(parent);
		this.setInputs();
		
	}
	
	/**
	 * create table
	 * @param parent
	 */
	public void createTriggersDetailInfoTable(Composite parent) {
		final Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayout(new FillLayout());
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));

		triggersDetailInfoTable = new TableViewer(tableComposite, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		triggersDetailInfoTable.getTable().setHeaderVisible(true);
		triggersDetailInfoTable.getTable().setLinesVisible(true);
		
		final TableViewerColumn nameColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		nameColumn.getColumn().setWidth(150);
		nameColumn.getColumn().setText(Messages.triggersDetailInfoPartTableNameCol);
		
		final TableViewerColumn targetTableColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		targetTableColumn.getColumn().setWidth(120);
		targetTableColumn.getColumn().setText(Messages.triggersDetailInfoPartTableTargetTableCol);
		
		final TableViewerColumn eventTypeColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		eventTypeColumn.getColumn().setWidth(150);
		eventTypeColumn.getColumn().setText(Messages.triggersDetailInfoPartTableEventTypeCol);
		
		final TableViewerColumn triggerStatusColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		triggerStatusColumn.getColumn().setWidth(100);
		triggerStatusColumn.getColumn().setText(Messages.triggersDetailInfoPartTableTriggerStatusCol);
		
		final TableViewerColumn triggerPriorityColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		triggerPriorityColumn.getColumn().setWidth(120);
		triggerPriorityColumn.getColumn().setText(Messages.triggersDetailInfoPartTableTriggerPriorityCol);
		
		final TableViewerColumn executionTimeColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		executionTimeColumn.getColumn().setWidth(100);
		executionTimeColumn.getColumn().setText(Messages.triggersDetailInfoPartTableExecutionTimeCol);
		
		final TableViewerColumn actionTypeColumn = new TableViewerColumn(
				triggersDetailInfoTable, SWT.LEFT);
		actionTypeColumn.getColumn().setWidth(150);
		actionTypeColumn.getColumn().setText(Messages.triggersDetailInfoPartTableActionTypeCol);
		
		triggersDetailInfoTable.setComparator(new ColumnViewerSorter());
		
		triggersDetailInfoTable.setContentProvider(new TriggersDetailTableViewerContentProvider());
		triggersDetailInfoTable.setLabelProvider(new TriggersTableViewerLabelProvider());
		
		registerContextMenu();
	}
	
	/**
	 * register context menu
	 */
	private void registerContextMenu() {
		triggersDetailInfoTable.getTable().addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				ActionManager.getInstance().changeFocusProvider(triggersDetailInfoTable.getTable());
			}
		});
		
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		
		Menu contextMenu = menuManager.createContextMenu(triggersDetailInfoTable.getTable());
		triggersDetailInfoTable.getTable().setMenu(contextMenu);
		
		Menu menu = new Menu(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP);
		
		final MenuItem addSerialItem = new MenuItem(menu, SWT.PUSH);
		addSerialItem.setText(Messages.triggersDetailInfoPartCreateTriggerBtn);
		addSerialItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_add.png"));
		addSerialItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addTrigger();
			}
		});
		
		final MenuItem editSerialItem = new MenuItem(menu, SWT.PUSH);
		editSerialItem.setText(Messages.triggersDetailInfoPartEditTriggerBtn);
		editSerialItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_edit.png"));
		editSerialItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editTrigger();
			}
		});
		
		final MenuItem dropSerialItem = new MenuItem(menu, SWT.PUSH);
		dropSerialItem.setText(Messages.triggersDetailInfoPartDropTriggerBtn);
		dropSerialItem.setImage(CommonUIPlugin.getImage("icons/action/trigger_delete.png"));
		dropSerialItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				dropTrigger();
			}
		});
		
		
		
		triggersDetailInfoTable.getTable().setMenu(menu);
	}
	
	/**
	 * addTrigger
	 */
	public void addTrigger () {
		NewTriggerAction action = (NewTriggerAction) ActionManager.getInstance().getAction(
				NewTriggerAction.ID);
		action.run(database);
		refresh();
	}
	
	/**
	 * editTrigger
	 */
	public void editTrigger () {
		TableItem[] items = triggersDetailInfoTable.getTable().getSelection();
		if (items.length != 0) {
			TableItem item = items[0];
			Trigger trigger = (Trigger) item.getData();
			Set<String> typeSet = new HashSet<String>();
			typeSet.add(NodeType.TRIGGER);

			ICubridNode triggerNode = CommonUITool.findNode(database, typeSet,
					trigger.getName());
			if (triggerNode != null) {
				AlterTriggerAction action = (AlterTriggerAction) ActionManager.getInstance().getAction(
						AlterTriggerAction.ID);
				if (action.run(database, (ISchemaNode) triggerNode) == IDialogConstants.OK_ID) {
					refresh();
				}
			}
		} else {
			CommonUITool.openWarningBox(Messages.errTriggerNoSelection);
		}
	}
	
	/**
	 * dropTrigger
	 */
	public void dropTrigger () {
		TableItem[] items = triggersDetailInfoTable.getTable().getSelection();
		if (items.length > 0) {
			List<ISchemaNode> selectNodeList = new ArrayList<ISchemaNode>();
			for (TableItem item : items) {
				Trigger trigger = (Trigger) item.getData();
				Set<String> typeSet = new HashSet<String>();
				typeSet.add(NodeType.TRIGGER);

				ICubridNode triggerNode = CommonUITool.findNode(database, typeSet,
						trigger.getName());
				selectNodeList.add((ISchemaNode)triggerNode);
			}
			
			if (selectNodeList.size() > 0) {
				DropTriggerAction action = (DropTriggerAction) ActionManager.getInstance().getAction(
						DropTriggerAction.ID);
				
				ISchemaNode[] nodeArr = new ISchemaNode[selectNodeList.size()];
				action.run(selectNodeList.toArray(nodeArr));
				refresh();
			}
		} else {
			CommonUITool.openWarningBox(Messages.errTriggerNoSelection);
		}
	}
	
	/**
	 * refresh data
	 */
	public void refresh () {
		OpenTriggerDetailInfoPartProgress progress = new OpenTriggerDetailInfoPartProgress(database);
		progress.loadTriggerInfoList();
		if (progress.isSuccess()) {
			triggerList = progress.getTriggerList();
			setInputs();
		}
		triggerChangeFlag = false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@SuppressWarnings("unchecked")
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.database = (CubridDatabase)input.getAdapter(CubridDatabase.class);
		this.triggerList = (List<Trigger>)input.getAdapter(List.class);
		
		StringBuilder partName = new StringBuilder(
				Messages.triggersDetailInfoPartTitle);
		partName.append(" [").append(database.getUserName()).append("@")
				.append(database.getName()).append(":")
				.append(database.getDatabaseInfo().getBrokerIP()).append("]");
		setPartName(partName.toString());
	}
	
	public void setInputs() {
		triggersDetailInfoTable.setInput(triggerList);
		triggersDetailInfoTable.refresh();
		pack();
	}
	
	public void pack () {
		for (int i = 0; i < triggersDetailInfoTable.getTable().getColumnCount(); i++) {
			TableColumn column = triggersDetailInfoTable.getTable().getColumn(i);
			if (column.getWidth() > 600) {
				column.setWidth(600);
			}
			if (column.getWidth() < 100) {
				column.setWidth(100);
			}
		}
	}
	
	public void nodeChanged(CubridNodeChangedEvent event) {
		if (event.getSource() instanceof DefaultSchemaNode) {
			DefaultSchemaNode node = (DefaultSchemaNode)event.getSource();
			if ((node.getType().equals(NodeType.TRIGGER_FOLDER)
					||node.getType().equals(NodeType.TRIGGER)
					&& node.getDatabase().equals(database) )) {
				triggerChangeFlag = true;
			}
		}
		if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
			close(event, database.getServer());
		}
		
		if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
				|| CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
			close(event, database);
		}
	}

	public void setFocus() {
		//if view info chaned, ask whether refresh
		if (triggerChangeFlag) {
			if (CommonUITool.openConfirmBox(com.cubrid.common.ui.common.Messages.dashboardConfirmRefreshDataMsg)) {
				refresh();
			}
			triggerChangeFlag = false;
		}
	}
	
	/**
	 * trigger table label provider
	 * @author Administrator
	 *
	 */
	public class TriggersTableViewerLabelProvider extends LabelProvider implements
	ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Trigger) {
				Trigger trigger = (Trigger)element;
				if (trigger != null) {
					switch (columnIndex) {
						case 0 : return trigger.getName();
						case 1 : return trigger.getTargetClass();
						case 2 : return trigger.getEventType();
						case 3 : 
							return trigger.getStatus();
						case 4 : 
							return trigger.getPriority();
						case 5 :
							return trigger.getActionTime();
						case 6 :
							return trigger.getActionType();
					}
				}
			}
			return null;
		}
	}
	
	/**
	 * trigger table content provider
	 * @author fulei
	 *
	 */
	public class TriggersDetailTableViewerContentProvider implements IStructuredContentProvider {
		/**
		 * getElements
		 *
		 * @param inputElement Object
		 * @return Object[]
		 */
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				List<Trigger> list = (List<Trigger>) inputElement;
				Trigger[] nodeArr = new Trigger[list.size()];
				return list.toArray(nodeArr);
			}
		
			return new Object[]{};
		}
		
		/**
		 * dispose
		 */
		public void dispose() {
		}
		
		/**
		 * inputChanged
		 *
		 * @param viewer Viewer
		 * @param oldInput Object
		 * @param newInput Object
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// do nothing
		}
	}
	
	/**
	 * Column Viewer Sorter
	 * 
	 * @author fulei
	 * @version 1.0 - 2013-1-9 fulei
	 */
	class ColumnViewerSorter extends
			ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			Trigger t1 = (Trigger)e1;
			Trigger t2 = (Trigger)e2;
			return t1.getName().compareTo(t2.getName());
		}
	}
	
	public void doSave(IProgressMonitor monitor) {

	}

	public void doSaveAs() {

	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public CubridDatabase getDatabase() {
		return database;
	}
}
