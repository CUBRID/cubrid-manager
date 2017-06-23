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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.common.navigator.NodeFilterManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;

/**
 * 
 * A pop-up menu in query editor toolbar, it can be override to customize the
 * pop-up menu items
 * 
 * @author pangqiren 2009-3-2
 */
public class DatabaseNavigatorMenu {

	public static final String NULL_DATABASE_ID = "__null__";
	public static final String SELF_DATABASE_ID = "self_connection";
	public static final String IND_DATABASE_ID = "indicate_database_on_tree";

	public static final String NO_DATABASE_SELECTED_LABEL = Messages.noDbSelected;
	public static final String SELF_DATABASE_SELECTED_LABEL = Messages.selfDefinedDb;
	public static final String IND_DATABASE_SELECTED_LABEL = Messages.indDefinedDb;
	// default database selection
	public static final CubridDatabase NULL_DATABASE = new CubridDatabase(NULL_DATABASE_ID, NO_DATABASE_SELECTED_LABEL);

	// self filled database
	public static final CubridDatabase SELF_DATABASE = new CubridDatabase(SELF_DATABASE_ID,
		SELF_DATABASE_SELECTED_LABEL);

	// indicate the database on the left tree
	public static final CubridDatabase IND_DATABASE = new CubridDatabase(IND_DATABASE_ID,
		IND_DATABASE_SELECTED_LABEL);

	protected CubridDatabase selectdDb = NULL_DATABASE;
	
	protected CubridDatabase lastSelectdDb = NULL_DATABASE;
	protected String lastUser;

	protected Menu dbSelectionMenu;
	
	protected Listener listener;

	protected DatabaseMenuItem nullDbMenuItem;
	protected DatabaseMenuItem selfDbMenuItem;
	protected DatabaseMenuItem indDbMenuItem;
	/*Current selected menu item*/
	protected DatabaseMenuItem selectedMenuItem;

	protected Composite parent = null;
	protected CLabel selectDbLabel;
	protected QueryEditorPart editor;
	protected DatabaseMenuItem lastSelectedDatabaseMenu = null;
	protected Image selectedImage;

	public Image getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(Image selectedImage) {
		this.selectedImage = selectedImage;
	}

	/**
	 * load pop-up menu in query editor toolbar
	 */
	protected void loadDatabaseMenu() {
		if (dbSelectionMenu != null) {
			dbSelectionMenu.dispose();
			parent.update();
		}

		dbSelectionMenu = new Menu(parent.getShell(), SWT.POP_UP);
		loadNavigatorMenu();

		nullDbMenuItem = new DatabaseMenuItem(NULL_DATABASE.getId(), dbSelectionMenu, SWT.RADIO);
		nullDbMenuItem.setText(NULL_DATABASE.getLabel());
		nullDbMenuItem.setDatabase(NULL_DATABASE);

		selfDbMenuItem = new DatabaseMenuItem(SELF_DATABASE.getId(), dbSelectionMenu, SWT.RADIO);
		selfDbMenuItem.setText(SELF_DATABASE.getLabel());
		selfDbMenuItem.setDatabase(SELF_DATABASE);

		new MenuItem(dbSelectionMenu, SWT.SEPARATOR);

		// indicate current database on the left tree
		indDbMenuItem = new DatabaseMenuItem(IND_DATABASE.getId(), dbSelectionMenu, SWT.RADIO);
		indDbMenuItem.setText(IND_DATABASE.getLabel());
		indDbMenuItem.setDatabase(IND_DATABASE);

		//when reload, get the new selected menu item
		if (selectedMenuItem != null
				&& !NodeFilterManager.getInstance().isHidden(
						selectedMenuItem.getDatabase())) {
			selectedMenuItem = findById(selectedMenuItem.getId());
			if (selectedMenuItem != null && selectedMenuItem.isEnabled()) {
				selectedMenuItem.setSelection(true);
				if (selectedMenuItem.getParent() != null && selectedMenuItem.getParent().getParentItem() != null) {
					selectedMenuItem.getParent().getParentItem().setImage(getSelectedImage());
					MenuItem hostParent = selectedMenuItem.getParent().getParentItem();
					if (hostParent != null
							&& hostParent.getParent() != null
							&& hostParent.getParent().getParentItem() != null) {
						hostParent.getParent().getParentItem()
								.setImage(getSelectedImage());
					}
				}
			}
		}

		addSelectionListener();
	}

	/**
	 * 
	 * Load the pop-up menu
	 * 
	 */
	public void loadNavigatorMenu() {
		//empty
	}

	/**
	 * load all database on all server. if not login or database not started,
	 * the item disabled.
	  * 
	  * @param dbSelectionMenu
	 */
	public void loadNavigatorMenu(Menu dbSelectionMenu) {
		//empty
	}
	
	/**
	 * Prepare all sub nodes
	 * 
	 * @param items
	 * @param menu
	 */
	private void preparedItems(List<MenuItem> items, Menu menu) {
		if (menu == null) {
			return;
		}

		MenuItem[] oItems = menu.getItems();
		for (final MenuItem item : oItems) {
			if (item.getMenu() == null || item.getMenu() == menu) {
				items.add(item);
			} else {
				preparedItems(items, item.getMenu());
			}
		}
	}
	
	protected MenuItem[] getAllMenuNodes() {
		List<MenuItem> allNodes = new ArrayList<MenuItem>();
		preparedItems(allNodes, dbSelectionMenu);
//		for (final MenuItem item : allNodes) {
//			System.err.println(">"+item.getText());
//		}
		return allNodes.toArray(new MenuItem[0]);
	}

	/**
	 * Add selection listener
	 * 
	 */
	protected void addSelectionListener() {
		MenuItem[] allNodes = getAllMenuNodes();

		for (final MenuItem item : allNodes) {
			item.addSelectionListener(new SelectionAdapter() {

				
				public void widgetSelected(SelectionEvent event) {
					if (item.getStyle() != SWT.CHECK && item.getStyle() != SWT.RADIO) {
						CommonUITool.openErrorBox(parent.getShell(), Messages.plsSelectDb);
						return;
					}
					if (editor.isRunning()) {
						CommonUITool.openErrorBox(parent.getShell(), Messages.errEditorRunning);
						item.setSelection(false);
						return;
					}
					CubridDatabase oldSelectedDb = (CubridDatabase)getSelectedDb();
					final DatabaseMenuItem dbItem = (DatabaseMenuItem)item;
					CubridDatabase selectedDb = dbItem.getDatabase();

					if (selectedDb != null && selectedDb.getId().equals(IND_DATABASE_ID)) {
						if (oldSelectedDb == null) {
							return;
						}

						dbItem.setSelection(false);

						// on CM
						CubridNavigatorView navigatorView = CubridNavigatorView.getNavigatorView("com.cubrid.cubridmanager.host.navigator");
						if (navigatorView != null) {
							TreeViewer treeViewer = navigatorView.getViewer();
							Tree tree = treeViewer.getTree();
							if (tree == null) {
								return;
							}
							for (int i = 0; i < tree.getItemCount(); i++) {
								TreeItem itm = tree.getItem(i);
								if (itm == null) {
									continue;
								}

								showDatabaseOnEditingWithHostOnGroup(dbItem, oldSelectedDb, treeViewer, tree, itm);
							}
							return;
						}

						// on CQB
						navigatorView = CubridNavigatorView.getNavigatorView("com.cubrid.cubridquery.connection.navigator");
						if (navigatorView != null) {
							TreeViewer treeViewer = navigatorView.getViewer();
							Tree tree = treeViewer.getTree();
							if (tree == null) {
								return;
							}
							for (int i = 0; i < tree.getItemCount(); i++) {
								TreeItem itm = tree.getItem(i);
								if (itm == null) {
									continue;
								}

								showDatabaseOnEditingOnGroup(oldSelectedDb, treeViewer, tree, itm);
							}
							return;
						}

						return;
					} else if (oldSelectedDb != null && selectedDb != null && oldSelectedDb.getId().equals(selectedDb.getId())) {
						return;
					} else if (selectedDb != null && selectedDb.getId().equals(SELF_DATABASE_ID)) {
						if (!NULL_DATABASE_ID.equals(oldSelectedDb.getId())) {
							boolean confirm = CommonUITool.openConfirmBox(
									editor.getSite().getShell(),
									Messages.changeDbConfirm);
							if (!confirm) {
								dbItem.setSelection(false);
								return;
							}
						}
						if (!handleWithSelfConn(dbItem)) {
							return;
						}
					} else if (oldSelectedDb != null && !oldSelectedDb.getId().equals(NULL_DATABASE_ID)) {
						boolean confirm = CommonUITool.openConfirmBox(editor.getSite().getShell(),
							Messages.changeDbConfirm);
						if (!confirm) {
							dbItem.setSelection(false);
							return;
						}
					}

					boolean valid = editor.resetJDBCConnection();
					if (valid) {
						selectMenuItem(dbItem);
						editor.getCombinedQueryComposite().getMultiDBQueryComp().setMainDatabase(dbItem.getDatabase());
						editor.refreshQueryOptions();
					} else {
						dbItem.setSelection(false);
					}
				}

				// on cm
				private void showDatabaseOnEditingWithHost(
						final CubridDatabase oldSelectedDb, final TreeViewer treeViewer, final Tree tree, final TreeItem itm) {
					if (itm != null && itm.getData() != null
							&& itm.getData() instanceof ICubridNode
							&& NodeType.SERVER.equals(((ICubridNode) itm.getData()).getType())) {
						String serverName = ((ICubridNode) itm.getData()).getServer().getServerName();
						if (serverName == null || oldSelectedDb == null || oldSelectedDb.getServer() == null) {
							return;
						}

						if (!serverName.equals(oldSelectedDb.getServer().getName())) {
							return;
						}
						
						if (treeViewer != null && !itm.getExpanded()) {
							treeViewer.expandToLevel(itm.getData(), 1);
						}
						
						Display.getDefault().timerExec(100, new Runnable() {
							
							public void run() {
								showDatabaseOnEditingWithHostLoop(oldSelectedDb, tree, itm, 1);
							}
						});
					}
				}

				// on cqb
				private void showDatabaseOnEditing(
						CubridDatabase oldSelectedDb, TreeViewer treeViewer, Tree tree, TreeItem itm) {
					ISchemaNode cNode = (ISchemaNode) itm.getData();
					String user = cNode.getDatabase().getUserName();
					String dbName = cNode.getDatabase().getDatabaseInfo().getDbName();
					String hostName = cNode.getDatabase().getDatabaseInfo().getBrokerIP();
					if (user == null || dbName == null || hostName == null) {
						return;
					}
					if (user.equals(oldSelectedDb.getUserName())
							&& dbName.equals(oldSelectedDb.getDatabaseInfo().getDbName())
							&& hostName.equals(oldSelectedDb.getDatabaseInfo().getBrokerIP())) {
						tree.setSelection(itm);
						tree.setTopItem(itm);

						final String origDbName = itm.getText();
						final TreeItem updatableItem = itm;
						itm.setText("[ [ [ " + origDbName + " ] ] ]");
						Display.getDefault().timerExec(500,
								new Runnable() {
									
									public void run() {
										updatableItem.setText("[ [ " + origDbName + " ] ]");
									}
								});
						Display.getDefault().timerExec(530,
								new Runnable() {
									
									public void run() {
										updatableItem.setText("[ " + origDbName + " ]");
									}
								});
						Display.getDefault().timerExec(560,
								new Runnable() {
									
									public void run() {
										updatableItem.setText(origDbName);
									}
								});
					}
				}

				private void showDatabaseOnEditingWithHostLoop(final CubridDatabase oldSelectedDb,
						final Tree tree, final TreeItem itm, final int count) {
					if (count > 10) {
						return;
					}

					boolean ok = false;
					try {
						for (int j = 0; j < itm.getItemCount(); j++) {
							TreeItem sub = itm.getItem(j);
							if (sub.getData() instanceof ICubridNode
									&& NodeType.DATABASE_FOLDER.equals(((ICubridNode) sub.getData()).getType())) {
								ok = true;
								for (int k = 0; k < sub.getItemCount(); k++) {
									TreeItem dbNode = sub.getItem(k);
									if (dbNode == null || dbNode.getData() == null) {
										continue;
									}
									String dbName = ((ICubridNode) dbNode.getData()).getName();
									if (dbName != null && dbName.equals(oldSelectedDb.getName())) {
										tree.setSelection(dbNode);
										tree.setTopItem(dbNode);
										
										final String origDbName = dbNode.getText();
										final TreeItem updatableItem = dbNode;
										dbNode.setText("[ [ [ " + origDbName + " ] ] ]");
										Display.getDefault().timerExec(500,
												new Runnable() {
													public void run() {
														updatableItem.setText("[ [ " + origDbName + " ] ]");
													}
												});
										Display.getDefault().timerExec(530,
												new Runnable() {
													public void run() {
														updatableItem.setText("[ " + origDbName + " ]");
													}
												});
										Display.getDefault().timerExec(560,
												new Runnable() {
													public void run() {
														updatableItem.setText(origDbName);
													}
												});
									}
								}
							}
						}
					} catch (Exception ignored) {
						ok = false;
					}
					
					if (!ok) {
						final int nextCount = count + 1;
						Display.getDefault().timerExec(500, new Runnable() {
							public void run() {
								showDatabaseOnEditingWithHostLoop(oldSelectedDb, tree, itm, nextCount);
							}
						});
					}
				}

				// on group mode
				private void showDatabaseOnEditingWithHostOnGroup(final DatabaseMenuItem dbItem, 
						final CubridDatabase oldSelectedDb, final TreeViewer treeViewer, final Tree tree, final TreeItem item) {
					if (item == null || item.getData() == null) {
						return;
					}
					if (item.getData() instanceof CubridGroupNode) {
						CubridGroupNode grp = (CubridGroupNode) item.getData();
						if (lastSelectedDatabaseMenu == null || lastSelectedDatabaseMenu.getGroupName() == null
								|| grp == null || !lastSelectedDatabaseMenu.getGroupName().equals(grp.getName())) {
							return;
						}

						if (treeViewer != null && !item.getExpanded()) {
							treeViewer.expandToLevel(item.getData(), 1);
							Display.getDefault().timerExec(500, new Runnable() {
								public void run() {
									for (int i = 0; i < item.getItemCount(); i++) {
										final TreeItem itm = item.getItem(i);
										if (itm == null) {
											continue;
										}
										
										showDatabaseOnEditingWithHost(oldSelectedDb,
												treeViewer, tree, itm);
									}
								}
							});
						} else {
							for (int i = 0; i < item.getItemCount(); i++) {
								final TreeItem itm = item.getItem(i);
								if (itm == null) {
									continue;
								}

								showDatabaseOnEditingWithHost(oldSelectedDb,
										treeViewer, tree, itm);
							}
						}
					} else {
						showDatabaseOnEditingWithHost(oldSelectedDb, treeViewer, tree, item);
					}
				}

				// on group mode
				private void showDatabaseOnEditingOnGroup(final CubridDatabase oldSelectedDb, final TreeViewer treeViewer, final Tree tree, final TreeItem item) {
					if (item == null || item.getData() == null) {
						return;
					}
					if (item.getData() instanceof CubridGroupNode) {
						CubridGroupNode grp = (CubridGroupNode) item.getData();
						if (lastSelectedDatabaseMenu == null || lastSelectedDatabaseMenu.getGroupName() == null
								|| grp == null || !lastSelectedDatabaseMenu.getGroupName().equals(grp.getName())) {
							return;
						}

						if (treeViewer != null && !item.getExpanded()) {
							treeViewer.expandToLevel(item.getData(), 1);
							Display.getDefault().timerExec(500, new Runnable() {
								public void run() {
									for (int i = 0; i < item.getItemCount(); i++) {
										final TreeItem itm = item.getItem(i);
										if (itm == null) {
											continue;
										}

										showDatabaseOnEditing(oldSelectedDb,
												treeViewer, tree, itm);
									}
								}
							});
						} else {
							for (int i = 0; i < item.getItemCount(); i++) {
								final TreeItem itm = item.getItem(i);
								if (itm == null) {
									continue;
								}

								showDatabaseOnEditing(oldSelectedDb,
										treeViewer, tree, itm);
							}
						}
					} else {
						showDatabaseOnEditing(oldSelectedDb, treeViewer, tree, item);
					}
				}
			});
		}

//		multiSelectItem.addSelectionListener(new SelectionAdapter() {
//			
//			public void widgetSelected(SelectionEvent e) {
//				DatabaseSelectionDialog dialog = new DatabaseSelectionDialog(multiSelectItem.getParent().getShell());
//				dialog.setItems(dbSelectionMenu.getItems());
//				dialog.open();
//				CubridDatabase[] databases = dialog.getSelectedDbItem();
//				if (databases.length == 0) {
//					return;
//				}
//				Map<CubridDatabase, DatabaseMenuItem> allItems = new HashMap<CubridDatabase, DatabaseMenuItem>();
//				for (MenuItem item : dbSelectionMenu.getItems()) {
//					if (item instanceof DatabaseMenuItem) {
//						DatabaseMenuItem dbItem = (DatabaseMenuItem)item;
//						dbItem.setSelection(false);
//						allItems.put(dbItem.getDatabase(), dbItem);
//					}
//				}
//				List<DatabaseMenuItem> selected = new ArrayList<DatabaseMenuItem>();
//				for (CubridDatabase db : databases) {
//					if (!allItems.containsKey(db)) {
//						continue;
//					}
//					allItems.get(db).setSelection(true);
//					selected.add(allItems.get(db));
//				}
//				selectMenuItem(selected.toArray(new DatabaseMenuItem[0]));
//			}
//		});
	}

	/**
	 * 
	 * When click self-connection menu item, handle with this event
	 * 
	 * @param dbItem DatabaseMenuItem
	 * @return boolean
	 */
	public boolean handleWithSelfConn(DatabaseMenuItem dbItem) {
		return false;
	}

	/**
	 * When tree node in navigation view change, refresh the database list
	 */
	public void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				loadDatabaseMenu();
				if (selectedMenuItem == null || !selectedMenuItem.isEnabled()
						/*For bug TOOLS-1292 After change the user, the query editor hasn't been logout */
						|| isChangedDBUser()) {
					editor.shutDownConnection();
					selectMenuItem(nullDbMenuItem);
				}
				editor.refreshQueryOptions();		
			}
		});
	}
	
	/**
	 * Judge the selectDB is changed
	 * 
	 * @return
	 */
	protected boolean isChangedDB() {
		if ((selectdDb != null && lastSelectdDb != null && StringUtil.isEqualNotIgnoreNull(
				selectdDb.getId(), lastSelectdDb.getId()))
				|| (selectdDb == null && lastSelectdDb == null)) {
			return false;
		}

		return true;
	}
	
	/**
	 * Judge the selectDB'user is changed
	 * 
	 * @return
	 */
	protected boolean isChangedDBUser() {
		if (isChangedDB()) {
			return true;
		}

		if (selectdDb != null && lastSelectdDb != null
				&& !StringUtil.isEqualNotIgnoreNull(selectdDb.getUserName(),
						lastUser)) {
			return true;
		}
		return false;
	}
	/**
	 * Set the database
	 * 
	 * @param database CubridDatabase
	 */
	public void setDatabase(CubridDatabase database) {
		//show the selected database menu item
		if ((database.isLogined() && database.getRunningType() == DbRunningType.CS)) {
			DatabaseMenuItem item = findById(database.getId());
			selectMenuItem(item);
		}
		editor.refreshQueryOptions();
	}

	/**
	 * get selected database
	 * 
	 * @return dbSelectd
	 */
	public ICubridNode getSelectedDb() {
		return selectdDb;
	}

	/**
	 * inject custom operation when database changed
	 * 
	 * @param listener Listener
	 */
	public void addDatabaseChangedListener(Listener listener) {
		this.listener = listener;
	}

	/**
	 * target a database selection change
	 * 
	 * @param item DatabaseMenuItem
	 */
	public void selectMenuItem(DatabaseMenuItem item) {		
		DatabaseMenuItem tmpItem = item;
		if (tmpItem != null) {
			if (selectedMenuItem != null && !selectedMenuItem.isDisposed()) {
				selectedMenuItem.setSelection(false);
			}

			if (listener != null) {
				Event e = new Event();
				e.data = tmpItem.getDatabase();
				listener.handleEvent(e);
			}

			if (editor.getConnection() == null) {
				tmpItem = nullDbMenuItem;
			}

			tmpItem.setSelection(true);
			selectedMenuItem = tmpItem;
			setText(tmpItem);
			selectdDb = tmpItem.getDatabase();

			// [TOOLS-2425]Support shard broker
			if (CubridDatabase.hasValidDatabaseInfo(selectdDb)) {
				DatabaseInfo dbInfo = selectdDb.getDatabaseInfo();
				if (dbInfo.isShard()) {
					editor.setShardId(dbInfo.getCurrentShardId());
					editor.setShardVal(dbInfo.getCurrentShardVal());
					editor.setShardQueryType(dbInfo.getShardQueryType());
				}
			}

			editor.changeQueryEditorPartName(selectDbLabel.getText());
			editor.changeQueryEditorPartNameWithShard();

			/*For bug Tools-1250 Update the auto commit status by select db*/
			if (selectdDb != null
					&& lastSelectdDb != null
					&& !StringUtil.isEqualNotIgnoreNull(selectdDb.getId(),
							lastSelectdDb.getId())) {
				editor.setAutocommit(true);
			}

			/*Save current selectDB*/
			lastSelectdDb = selectdDb;
			lastUser = (selectdDb == null ? null : selectdDb.getUserName());
			
			LayoutManager.getInstance().getTitleLineContrItem().changeTitleForQueryEditor(
					selectdDb);
			LayoutManager.getInstance().getStatusLineContrItem().changeStuatusLineForViewOrEditPart(
					selectdDb, editor);
		}
	}

	/**
	 * Set the text.
	 * 
	 * @param item DatabaseMenuItem
	 */
	public void setText(DatabaseMenuItem item) {
		if (item == null) {
			return;
		}

		String text = item.getText();
		CubridDatabase cubridDatabase = item.getDatabase();
		boolean isDbaAuth = false;
		boolean isNullDb = false;
		if (cubridDatabase != null) {
			if (NULL_DATABASE_ID.equals(cubridDatabase.getId())) {
				isNullDb = true;
			} else {
				DatabaseInfo dbInfo = cubridDatabase.getDatabaseInfo();
				text = getDatabaseLabel(cubridDatabase);
				String userName = cubridDatabase.getUserName();
				if (userName != null && userName.trim().length() > 0) {
					if (userName.equalsIgnoreCase("dba")) {
						isDbaAuth = true;
					}
				}
				DbUserInfo dbusrInfo = null;
				if (dbInfo != null) {
					dbusrInfo = dbInfo.getAuthLoginedDbUserInfo();
				}
				if (!isDbaAuth && dbusrInfo != null) {
					isDbaAuth = dbusrInfo.isDbaAuthority();
				}
			}
		}

		if (isNullDb) {
			selectDbLabel.setBackground(
					new Color[] {Display.getDefault().getSystemColor(SWT.COLOR_WHITE),
						Display.getDefault().getSystemColor(SWT.COLOR_RED),
						Display.getDefault().getSystemColor(SWT.COLOR_RED),
						Display.getDefault().getSystemColor(SWT.COLOR_WHITE)}, new int[] {33, 67, 100});
		} else if (isDbaAuth) {
			selectDbLabel.setBackground(
				new Color[] {Display.getDefault().getSystemColor(SWT.COLOR_WHITE),
					Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
					Display.getDefault().getSystemColor(SWT.COLOR_YELLOW),
					Display.getDefault().getSystemColor(SWT.COLOR_WHITE)}, new int[] {33, 67, 100});
		} else {
			selectDbLabel.setBackground((Color)null);
		}
		if (text.length() > 28) {
			String showText = text.substring(0, 25) + "...";
			selectDbLabel.setText(showText);
		} else {
			selectDbLabel.setText(text);
		}
		selectDbLabel.setToolTipText(text);
	}

	/**
	 * return a text of selected database on the query editor
	 *
	 * @param database
	 * @return
	 */
	public String getDatabaseLabel(CubridDatabase database) {
		return null;
	}

	/**
	 * find menu item by menu item id
	 * 
	 * @param id String
	 * @return databaseMenuItem
	 */
	protected DatabaseMenuItem findById(String id) {
		MenuItem[] items = getAllMenuNodes();
		if (items.length > 0) {
			for (MenuItem item : items) {
				if (item instanceof DatabaseMenuItem && ((DatabaseMenuItem)item).getId().equals(id)) {
					return (DatabaseMenuItem)item;
				}
			}
		}
		return null;
	}

	/**
	 * if no database selected
	 * 
	 * @return boolean
	 */
	public boolean isNull() {
		return getSelectedDb() == NULL_DATABASE;
	}

	public void setEditor(QueryEditorPart editor) {
		this.editor = editor;
	}

	public void setParent(Composite parent) {
		this.parent = parent;
	}

	public DatabaseMenuItem getNullDbMenuItem() {
		return nullDbMenuItem;
	}

	public void setNullDbMenuItem(DatabaseMenuItem nullDbMenuItem) {
		this.nullDbMenuItem = nullDbMenuItem;
	}

	public void setSelectDbLabel(CLabel selectDbLabel) {
		this.selectDbLabel = selectDbLabel;
	}

	public Menu getDbSelectionMenu() {
		return dbSelectionMenu;
	}

	public void setDbSelectionMenu(Menu dbSelectionMenu) {
		this.dbSelectionMenu = dbSelectionMenu;
	}
}