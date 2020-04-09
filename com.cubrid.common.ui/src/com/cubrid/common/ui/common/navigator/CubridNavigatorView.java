/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.action.CollapseAllAction;
import com.cubrid.common.ui.common.action.FilterSettingAction;
import com.cubrid.common.ui.common.action.GroupSettingAction;
import com.cubrid.common.ui.common.action.NodeFilterAction;
import com.cubrid.common.ui.common.action.OpenTargetAction;
import com.cubrid.common.ui.common.action.ShowToolTipAction;
import com.cubrid.common.ui.common.action.TopGroupAction;
import com.cubrid.common.ui.common.action.TopGroupItemAction;
import com.cubrid.common.ui.common.navigator.dnd.CubridDnDNodeHandler;
import com.cubrid.common.ui.common.navigator.dnd.CubridGroupDnDNodeHandler;
import com.cubrid.common.ui.common.navigator.dnd.CubridItemDnDNodeHandler;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.common.sqlrunner.dialog.RunSQLFileDialogDNDController;
import com.cubrid.common.ui.er.dnd.ERDNDController;
import com.cubrid.common.ui.perspective.IPerspectiveConstance;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.control.MultiDBQueryDNDController;
import com.cubrid.common.ui.query.editor.QueryEditorDNDController;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * CUBRID navigator view part for create navigator panel
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-1 created by pangqiren
 */
public abstract class CubridNavigatorView extends ViewPart {
	public static final String ID_CQB = "com.cubrid.cubridquery.connection.navigator";
	public static final String ID_CM = "com.cubrid.cubridmanager.host.navigator";
	private static final Logger LOGGER = LogUtil.getLogger(CubridNavigatorView.class);
	private static final String COM_CUBRID_COMMON_SHOWGROUP = "com.cubrid.common.showgroup";
	protected TreeViewer tv = null;
	private ToolTip toolTip = null;
	private boolean isShowGroup;
	private long lastKeyInputTimestamp;
	private DatabaseInfo currentDatabaseInfo;
	private SchemaInfo currentSchemaInfo;
	private String currentSchemaDDL;

	private List<CubridDatabase> multiDBQuerySelectedDBList = new ArrayList<CubridDatabase>();

	/**
	 * Create the part control
	 *
	 * @param parent Composite
	 */
	public void createPartControl(Composite parent) {
		ViewForm viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new GridLayout());
		tv = new TreeViewer(viewForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tv.setFilters(NodeFilterManager.getInstance().getViewerFilter());
		//create the navigator
		createNavigator();

		//get the isShowGroup configuration
		isShowGroup = savedIsShowGroup();
		//set the tree view's input.
		setTreeInput();
		toolTip = new ToolTip(tv.getTree().getShell(), SWT.BALLOON);
		toolTip.setAutoHide(true);

		//Create the context menu
		MenuManager contextMenuManager = new MenuManager("#PopupMenu", "navigatorContextMenu");
		contextMenuManager.setRemoveAllWhenShown(true);
		contextMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				buildPopupMenu(manager);
			}
		});
		Menu contextMenu = contextMenuManager.createContextMenu(tv.getControl());
		tv.getControl().setMenu(contextMenu);
		// register the context menu for providing extension by extension point
		IWorkbenchPartSite site = getSite();
		site.registerContextMenu(contextMenuManager, tv);
		site.setSelectionProvider(tv);

		//add the select the object text composite to top left of toolbar
		ToolBar toolBar = new ToolBar(viewForm, SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		SelectTreeObjContrItem textContrItem = new SelectTreeObjContrItem(tv);
		toolBarManager.add(textContrItem);
		toolBarManager.update(true);
		viewForm.setContent(tv.getControl());
		viewForm.setTopLeft(toolBar);

		//add the other actions to the top right of toolbar
		toolBar = new ToolBar(viewForm, SWT.FLAT | SWT.CENTER);
		toolBarManager = new ToolBarManager(toolBar);
		buildToolBar(toolBarManager);
		toolBarManager.update(true);
		viewForm.setTopRight(toolBar);

		//Add the actions to view menu bar, you can add them by extension point or code define
		IActionBars actionBar = getViewSite().getActionBars();
		final IMenuManager menuManager = actionBar.getMenuManager();
		menuManager.addMenuListener(new IMenuListener2() {
			//reserve these actions by code define,these codes rebuild every time.
			//hence when hide, remove them not including these actions by extension point
			private IMenuManager lastMenuManager = new MenuManager();

			public void menuAboutToShow(IMenuManager manager) {
				lastMenuManager.removeAll();
				//build the code defined actions
				buildViewMenu(lastMenuManager);
				for (IContributionItem item : lastMenuManager.getItems()) {
					manager.add(item);
				}
			}

			public void menuAboutToHide(IMenuManager manager) {
				for (IContributionItem item : lastMenuManager.getItems()) {
					manager.remove(item);
				}
			}
		});
		menuManager.add(new Separator());

		activeContext();
		addListener();
		setFocus();
	}
	
	

	/**
	 * Create the navigator tree
	 */
	protected abstract void createNavigator();

	/**
	 * Build the context menu
	 *
	 * @param menuManager IMenuManager
	 */
	protected abstract void buildPopupMenu(final IMenuManager menuManager);

	/**
	 * Build the view toolbar
	 *
	 * @param toolBarManager IToolBarManager
	 */
	protected abstract void buildToolBar(IToolBarManager toolBarManager);

	/**
	 * Active the navigator context
	 */
	protected abstract void activeContext();

	/**
	 * Build the view menu
	 *
	 * @param menuManager IMenuManager
	 */
	protected void buildViewMenu(final IMenuManager menuManager) {
		ActionManager actionManager = ActionManager.getInstance();
		if (isSupportGroup()) {
			IMenuManager topLevel = new MenuManager(Messages.topLevelElements);
			menuManager.add(topLevel);
			{
				TopGroupItemAction groupAction = (TopGroupItemAction) actionManager.getAction(TopGroupItemAction.ID);
				if (groupAction == null) {
					LOGGER.warn("The groupAction is a null.");
					return;
				}
				groupAction.setNavigatorView(this);
				topLevel.add(groupAction);

				TopGroupAction connAction = (TopGroupAction) actionManager.getAction(TopGroupAction.ID);
				if (connAction == null) {
					LOGGER.warn("The connAction is a null.");
					return;
				}
				connAction.setNavigatorView(this);
				topLevel.add(connAction);

			}
			menuManager.add(new Separator());
			GroupSettingAction gsAction = (GroupSettingAction) actionManager.getAction(GroupSettingAction.ID);
			if (gsAction == null) {
				LOGGER.warn("The gsAction is a null.");
				return;
			}
			gsAction.setNavigatorView(this);
			menuManager.add(gsAction);
		}
		//add the showTooTip action
		menuManager.add(new Separator());

		ShowToolTipAction showToolTipAction = new ShowToolTipAction(
				Messages.showToolTipActionName, null);
		menuManager.add(showToolTipAction);

		menuManager.add(new Separator());
		CollapseAllAction collapseAllAction = (CollapseAllAction) actionManager.getAction(CollapseAllAction.ID);
		if (collapseAllAction == null) {
			LOGGER.warn("The collapseAllAction is a null.");
			return;
		}
		collapseAllAction.setTargetTreeViewer(tv);
		menuManager.add(collapseAllAction);

		//add the root node filter action
		menuManager.add(new Separator());
		//add filter setting action
		IAction action = actionManager.getAction(FilterSettingAction.ID);
		if (action instanceof FilterSettingAction) {
			((FilterSettingAction) action).setTv(tv);
			menuManager.add(action);
		}

		Object inputObj = tv.getInput();
		if (!(inputObj instanceof List<?>)) {
			return;
		}
		List<?> list = (List<?>) inputObj;
		int i = 1;
		for (Object obj : list) {
			if (!(obj instanceof ICubridNode)) {
				continue;
			}
			ICubridNode node = (ICubridNode) obj;
			String label = "&" + i + " " + node.getLabel();
			if (node.getLabel().indexOf("@") >= 0) {
				//if text have @ character, the sub string of the last @ will be as accelerator, hence add @
				label += "@";
			}
			NodeFilterAction nodeFilterAction = new NodeFilterAction(label, null, tv, node);
			nodeFilterAction.setChecked(NodeFilterManager.getInstance().isExistIdFilter(node.getId()));
			menuManager.add(nodeFilterAction);
			i++;
		}
	}

	/**
	 * Call this method when focus
	 */
	public void setFocus() {
		if (tv == null || tv.getControl() == null || tv.getControl().isDisposed()) {
			return;
		}
		tv.getControl().setFocus();
		// select the first element by default
		int count = tv.getTree().getSelectionCount();
		TreeItem[] items = tv.getTree().getItems();
		if (count == 0 && items != null && items.length > 0) {
			tv.getTree().select(items[0]);
		}
		ActionManager.getInstance().changeSelectionProvider(tv);
		LayoutManager.getInstance().changeSelectionProvider(tv);
		CubridNodeManager.getInstance().addCubridNodeChangeListener(LayoutManager.getInstance());
	}

	/**
	 *
	 * Add listener
	 *
	 */
	protected void addListener() {
		tv.getTree().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				if (toolTip.isVisible()) {
					toolTip.setVisible(false);
				}
			}

			public void mouseUp(MouseEvent event) {
				if (event.button == 1 && LayoutManager.getInstance().isUseClickOnce()) {
					ISelection selection = tv.getSelection();
					if (selection == null || selection.isEmpty()) {
						return;
					}
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					if (!(obj instanceof ICubridNode)) {
						return;
					}
					ICubridNode cubridNode = (ICubridNode) obj;
					LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(cubridNode);
				}
			}
		});

		tv.getTree().addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseHover(MouseEvent event) {
				if (toolTip.isVisible()) {
					toolTip.setVisible(false);
				}
				int x = event.x;
				int y = event.y;
				TreeItem item = tv.getTree().getItem(new Point(x, y));
				if (item == null) {
					return;
				}
				showToolTip(item);
			}
		});

		tv.getTree().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				if (toolTip.isVisible()) {
					toolTip.setVisible(false);
				}
				TreeItem[] items = tv.getTree().getSelection();
				if (items == null || items.length == 0) {
					return;
				}
				showToolTip(items[0]);

				if (items.length == 1) {
					showSchemaInfo(items[0]);
				} else {
					showSchemaInfo(null);
				}
			}

		});

		addDragDropSupport(tv.getTree());

		tv.getTree().addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				// prevent to make events by key press for a long time
				if (e.character == ' ') {
					TreeItem[] items = tv.getTree().getSelection();
					if (items == null || items.length == 0) {
						return;
					}

					if (items.length == 1) {
						showSchemaInfo(items[0]);
					} else {
						showSchemaInfo(null);
					}
				}

				lastKeyInputTimestamp = System.currentTimeMillis();

				// If you press F1 key, it is shown table information
				// by showing object information tab on the query editor.
				if (e.keyCode == SWT.F2) {
					TreeItem[] items = tv.getTree().getSelection();
					if (items == null || items.length == 0) {
						return;
					}

					for (TreeItem item : items) {
						Object obj = item.getData();
						if (obj instanceof ICubridNode) {
							ICubridNode node = (ICubridNode) obj;
							if (NodeType.USER_TABLE.equals(node.getType()) ||
									NodeType.SYSTEM_TABLE.equals(node.getType()) ||
									NodeType.USER_VIEW.equals(node.getType()) ||
									NodeType.SYSTEM_VIEW.equals(node.getType())) {
								if (!Util.isWindows()) {
									DefaultSchemaNode table = (DefaultSchemaNode) obj;
									OpenTargetAction action = new OpenTargetAction();
									action.showObjectInfo(table);
								}
							}

							else if (NodeType.TABLE_FOLDER.equals(node.getType())) {
								CubridNavigatorView view = CubridNavigatorView.getNavigatorView(ID_CQB);
								if (view == null) {
									view = CubridNavigatorView.getNavigatorView(ID_CM);
								}
								if (view == null) {
									return;
								}

								TreeViewer treeViewer = view.getViewer();
								//if not expand ,expand the node and wait until all children be added
								if (!treeViewer.getExpandedState(node)) {
									treeViewer.expandToLevel(node, 1);
									while (node.getChildren().size() == 0) {
										try {
											Thread.sleep(500);
										} catch (Exception ignored) {
										}
									}
								}

								CubridDatabase database = (CubridDatabase)node.getParent();
								OpenTargetAction action = new OpenTargetAction();
								action.showTableDashboard(database);
							}
						}
					}
				}
			}
		});
	}

	/**
	 *
	 * Show tooltip
	 *
	 * @param item TreeItem
	 */
	private void showToolTip(TreeItem item) {
		String isShowToolTip = PersistUtils.getPreferenceValue(
				CommonUIPlugin.PLUGIN_ID, ShowToolTipAction.SHOW_TOOLTIP,
				"true");
		if (!"true".equals(isShowToolTip)) {
			return;
		}
		Object obj = item.getData();
		if (obj instanceof ICubridNode) {
			ICubridNode node = (ICubridNode) obj;
			Rectangle rect = item.getBounds();
			Point pt = new Point(rect.x + rect.width, rect.y + rect.height);
			String str = getToolTip(node);
			if (str != null && str.length() > 0) {
				pt = tv.getTree().toDisplay(pt);
				toolTip.setText(node.getName());
				toolTip.setMessage(str);
				toolTip.setLocation(pt);
				toolTip.setVisible(true);
			}
		}
	}

	private void showSchemaInfo(TreeItem item) {
		if (System.currentTimeMillis() - lastKeyInputTimestamp <= 50) {
			return;
		}

		boolean isAutoShowSchemaInfo = GeneralPreference.isAutoShowSchemaInfo();
		if (!isAutoShowSchemaInfo) {
			return;
		}

		Object obj = null;
		if (item != null) {
			obj = item.getData();
		}

		DefaultSchemaNode table = null;
		if (obj != null) {
			if ((obj instanceof ICubridNode)) {
				ICubridNode node = (ICubridNode) obj;
				if (NodeType.USER_TABLE.equals(node.getType()) ||
						NodeType.USER_VIEW.equals(node.getType()) ||
						NodeType.USER_PARTITIONED_TABLE_FOLDER.equals(node.getType()) ||
						NodeType.SYSTEM_TABLE.equals(node.getType()) ||
						NodeType.SYSTEM_VIEW.equals(node.getType())
						) {
					table = (DefaultSchemaNode) node;
				}
			}
		}

		final CubridDatabase database = table == null ? null : table.getDatabase();
		currentDatabaseInfo = database == null ? null : database.getDatabaseInfo();
		final String schemaName = table == null ? null : table.getName();
		boolean isTable = (ActionSupportUtil.isSupportSingleSelection(table,
				new String[]{NodeType.USER_TABLE, NodeType.SYSTEM_TABLE,
						NodeType.USER_PARTITIONED_TABLE_FOLDER }));
		showQuickView(currentDatabaseInfo, schemaName, isTable);

		lastKeyInputTimestamp = System.currentTimeMillis();
	}

	public void showQuickView(final DatabaseInfo databaseInfo, final String schemaName, final boolean isTable) {
		final CubridColumnNavigatorView columnNav = CubridColumnNavigatorView.getInstance();
		final CubridDdlNavigatorView ddlNav = CubridDdlNavigatorView.getInstance();
		final CubridIndexNavigatorView indexNav = CubridIndexNavigatorView.getInstance();
		if (databaseInfo == null || schemaName == null) {
			if (columnNav != null) {
				columnNav.cleanView();
			}
			if (indexNav != null) {
				indexNav.cleanView();
			}
			if (ddlNav != null) {
				ddlNav.cleanView();
			}
		} else {
			Job job = new Job("Getting the schema data...") {
				protected IStatus run(IProgressMonitor monitor) {
					currentSchemaInfo = databaseInfo.getSchemaInfo(schemaName);
					GetSchemaDDLTask task = new GetSchemaDDLTask(databaseInfo, schemaName, isTable, monitor);
					task.execute();
					if (task.isSuccess()) {
						currentSchemaDDL = task.getCreateDDL();
					}

					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (columnNav != null) {
								columnNav.updateView(databaseInfo, currentSchemaInfo);
							}
							if (indexNav != null) {
								indexNav.updateView(currentSchemaInfo);
							}
							if (ddlNav != null) {
								ddlNav.updateView(currentSchemaDDL == null ? "" : currentSchemaDDL);
							}
						}
					});

					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	/**
	 * Get tooltip string
	 *
	 * @param cubridNode ICubridNode
	 * @return String
	 */
	protected abstract String getToolTip(ICubridNode cubridNode);

	public TreeViewer getViewer() {
		return tv;
	}

	/**
	 * Does the node support DND.
	 *
	 * @param source the dragged node.
	 * @return support or not.
	 */
	protected abstract boolean dataSupportDND(TreeItem[] source);

	/**
	 * Does the node support DND.
	 *
	 * @param target the dragged node.
	 * @return support or not.
	 */
	protected abstract boolean dataSupportDragOver(ICubridNode target);

	/**
	 * Add the drag drop support of tree.
	 *
	 * @param tree the tree need to drag and drop.
	 */
	protected void addDragDropSupport(final Tree tree) {
		// Define 'Transfer Type', 'Operation'
		Transfer[] types = new Transfer[]{TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY;

		// DragSource
		final DragSource source = new DragSource(tree, operations);
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				event.doit = false;
				if (!dataSupportDND(tree.getSelection())) {
					return;
				}
				event.doit = true;
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = ((ICubridNode) tree.getSelection()[0].getData()).getLabel();
			}

			public void dragFinished(DragSourceEvent event) {
			}
		});

		addTreeDropTarget(tree);
		String perspectiveId = PerspectiveManager.getInstance().getCurrentPerspectiveId();
		// DropTarget for query editor.
		QueryEditorDNDController.registerDragSource(perspectiveId, tv);

		// DropTarget for multiple query
		MultiDBQueryDNDController.registerDragSource(perspectiveId, tv);

		// DropTarget for Run SQL file
		RunSQLFileDialogDNDController.registerDragSource(perspectiveId, tv);

		//DropTarget for ERD
		ERDNDController.registerDragSource(perspectiveId, tv);
	}

	/**
	 * Add drop target
	 *
	 * @param tree Tree
	 */
	private void addTreeDropTarget(final Tree tree) {
		// DropTarget for tree
		Transfer[] types = new Transfer[]{TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY;

		DropTarget target = new DropTarget(tree, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			/**
			 * Drag item enter the tree items
			 */
			public void dragEnter(DropTargetEvent event) {
				TreeItem[] selectedItems = tree.getSelection();
				//do not support multi DROP_COPY
				if (event.detail == DND.DROP_COPY && selectedItems.length > 1) {
					event.detail = DND.DROP_NONE;
					event.feedback = DND.FEEDBACK_NONE;
				}
			}

			/**
			 * When drag operation change, check whether to support this operation
			 */
			public void dragOperationChanged(DropTargetEvent event) {
				dragEnter(event);
			}

			/**
			 * Drag item over the tree items.
			 */
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item == null) {
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
				//do not support multi DROP_COPY
				TreeItem[] selectedItems = tree.getSelection();
				if (event.detail == DND.DROP_COPY && selectedItems.length > 1) {
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
				//Target TreeItem
				TreeItem targetTreeItem = (TreeItem) event.item;
				ICubridNode data = (ICubridNode) targetTreeItem.getData();
				if (dataSupportDragOver(data)) {
					if (data instanceof CubridGroupNode) {
						event.feedback |= DND.FEEDBACK_SELECT;
						return;
					}
					//Convert drop coordinate from Display to Tree
					Point pt = Display.getCurrent().map(null, tree, event.x, event.y);
					Rectangle bounds = targetTreeItem.getBounds();
					if (pt.y < (bounds.y + bounds.height / 2)) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else {
						//if (pt.y > bounds.y + 2 * bounds.height / 3)
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					}
				} else {
					event.feedback = DND.FEEDBACK_NONE;
				}
			}

			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				TreeItem[] selectedItems = tree.getSelection();
				if (event.detail == DND.DROP_COPY && selectedItems.length > 1) {
					event.detail = DND.DROP_NONE;
					return;
				}
				final int dropOperation = event.detail;

				ICubridNode dropNode = null;
				boolean insertBefore = false;
				if (event.item == null) {
					List<CubridGroupNode> allGroup = getGroupNodeManager().getAllGroupNodes();
					dropNode = allGroup.get(allGroup.size() - 1);
				} else {
					//Move under a TreeItem node
					TreeItem dropItem = (TreeItem) event.item;
					dropNode = (ICubridNode) dropItem.getData();
					Point pt = Display.getCurrent().map(null, tree, event.x, event.y);
					Rectangle bounds = dropItem.getBounds();
					if (pt.y < bounds.y + bounds.height / 2) {
						insertBefore = true;
					}
				}

				CubridDnDNodeHandler handler = getCubridDnDNodeHandler();
				boolean isSuccess = false;
				if (insertBefore) {
					for (TreeItem si : selectedItems) {
						ICubridNode dragNode = (ICubridNode) si.getData();
						isSuccess = handler.handle(dragNode, dropNode, insertBefore, dropOperation) || isSuccess;
					}
				} else {
					for (int i = selectedItems.length - 1; i >= 0; i--) {
						TreeItem si = selectedItems[i];
						ICubridNode dragNode = (ICubridNode) si.getData();
						isSuccess = handler.handle(dragNode, dropNode, insertBefore, dropOperation) || isSuccess;
					}
				}
				if (isSuccess) {
					Object[] objs = tv.getExpandedElements();
					setTreeInput();
					tv.setExpandedElements(objs);
				}
			}
		});
	}

	/**
	 * Drop handler
	 *
	 * @return the CubridDnDNodeHandler;
	 */
	protected CubridDnDNodeHandler getCubridDnDNodeHandler() {
		return isShowGroup ? new CubridGroupDnDNodeHandler(this) : new CubridItemDnDNodeHandler(this);
	}

	/**
	 * Retrieves whether the group nodes is displayed in the tree view
	 *
	 * @return true:show group nodes;false:do not show.
	 */
	public boolean isShowGroup() {
		return isShowGroup;
	}

	/**
	 * The view does support the group node. Subclass can override this method
	 * if it does't support group nodes.
	 *
	 * @return true:support.
	 */
	protected abstract boolean isSupportGroup();

	/**
	 * Set the visible attribute of group nodes.
	 *
	 * @param isShowGroup true:show;false:don't show.
	 */
	public void setShowGroup(boolean isShowGroup) {
		this.isShowGroup = isShowGroup;
		setTreeInput();
		PersistUtils.setPreferenceValue(CommonUIPlugin.PLUGIN_ID,
				COM_CUBRID_COMMON_SHOWGROUP, String.valueOf(isShowGroup));
	}

	/**
	 * Retrieves the CUBRID GroupNode manager.
	 *
	 * @return ICubridGroupNodeManager
	 */
	public abstract ICubridGroupNodeManager getGroupNodeManager();

	/**
	 * Set the tree view's input
	 */
	protected void setTreeInput() {
		if (isShowGroup()) {
			List<CubridGroupNode> inputList = this.getGroupNodeManager().getAllGroupNodes();
			tv.setInput(inputList);
		} else {
			tv.setInput(getGroupItems());
		}
	}

	public Object getServerList() {
		return getGroupItems();
	}

	/**
	 * Set the tree view's input
	 */
	public void setTreeInput(TreeViewer tv) {
		if (isShowGroup()) {
			List<CubridGroupNode> inputList = this.getGroupNodeManager().getAllGroupNodes();
			tv.setInput(inputList);
		} else {
			tv.setInput(getGroupItems());
		}
	}

	public Object getRootList() {
		return getGroupItems();
	}

	/**
	 * Get the saved items such as hosts or connections.
	 *
	 * @return used to tree view's input.
	 */
	protected abstract Object getGroupItems();

	/**
	 * Retrieves the show group option in local configuration file.
	 *
	 * @return true:show,false:don't show.
	 */
	public boolean savedIsShowGroup() {
		String show = PersistUtils.getPreferenceValue(CommonUIPlugin.PLUGIN_ID, COM_CUBRID_COMMON_SHOWGROUP);
		if (show == null || show.trim().length() == 0) {
			return false;
		} else {
			return Boolean.parseBoolean(show);
		}
	}

	/**
	 * Get the default group's tree item.
	 *
	 * @return TreeItem
	 */
	public TreeItem getDefaultGroupTreeItem() {
		if (tv == null || tv.getTree() == null || tv.getTree().getItems() == null) {
			return null;
		}

		if (!isShowGroup()) {
			return null;
		}

		TreeItem[] items = tv.getTree().getItems();
		for (TreeItem ti : items) {
			if (ti.getData().equals(getGroupNodeManager().getDefaultGroup())) {
				return ti;
			}
		}

		return null;
	}

	/**
	 * Get the tree item by item's data.
	 *
	 * @param data the tree item's data
	 * @return the tree item.
	 */
	public TreeItem getTreeItemByData(Object data) {
		if (tv == null || tv.getTree() == null || tv.getTree().getItems() == null) {
			return null;
		}

		TreeItem[] items = tv.getTree().getItems();
		for (TreeItem ti : items) {
			if (ti.getData().equals(data)) {
				return ti;
			}
		}

		return null;
	}

	/**
	 * Get navigator view
	 *
	 * @param viewId String
	 * @return CubridNavigatorView
	 */
	public static CubridNavigatorView getNavigatorView(String viewId) {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			if (window != null) {
				for (IWorkbenchPage page : window.getPages()) {
					if (page != null) {
						IViewReference[] viewReferences = page.getViewReferences();
						for (IViewReference reference : viewReferences) {
							if (StringUtil.isEqual(viewId, reference.getId()) && reference.getPart(false) instanceof CubridNavigatorView) {
								return (CubridNavigatorView)reference.getPart(false);
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * add database to multiDBQuerySelectedDBList
	 *
	 * @param database
	 */
	public void addMultiDBQuerySelectedDB(CubridDatabase database) {
		multiDBQuerySelectedDBList.add(database);
	}

	/**
	 * getMultiDBQuerySelectedDBList
	 *
	 * @return
	 */
	public List<CubridDatabase> getMultiDBQuerySelectedDBList() {
		return multiDBQuerySelectedDBList;
	}

	public TreeItem[] getSelectedItems() {
		return tv.getTree().getSelection();
	}

	public static CubridNavigatorView findNavigationView() {
		String id = PerspectiveManager.getInstance().getCurrentPerspectiveId();
		if (IPerspectiveConstance.CM_PERSPECTIVE_ID.equals(id)) {
			CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(ID_CM);
			if (navigatorView != null) {
				return navigatorView;
			}
		} else {
			CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView(ID_CQB);
			if (navigatorView != null) {
				return navigatorView;
			}
		}
		return null;
	}

	/**
	 * Return the ddl of recently selected schema
	 *
	 * @return String
	 */
	public String getCurrentSchemaDDL() {
		return currentSchemaDDL;
	}

	/**
	 * Return recently selected SchemaInfo
	 *
	 * @return String
	 */
	public SchemaInfo getCurrentSchemaInfo() {
		return currentSchemaInfo;
	}

	/**
	 * Return recently selected DatabaseInfo
	 *
	 * @return String
	 */
	public DatabaseInfo getCurrentDatabaseInfo() {
		return currentDatabaseInfo;
	}
}
