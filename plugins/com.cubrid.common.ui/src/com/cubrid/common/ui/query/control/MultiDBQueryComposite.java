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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.progress.PendingUpdateAdapter;

public class MultiDBQueryComposite extends Composite { // FIXME remove useless

    private final QueryEditorPart editor;

    private final CTabFolder resultTabFolder;
    private CTabFolder multiDBQueryCompTabFolder;
    private CTabItem multiDBQueryCompTabItem;
    private ContainerCheckedTreeViewer ctv;
    public static final String INDEXKEY = "INDEX";
    private MultiDBQueryDNDController dragController;
    private Set<ICubridNode> selectedNodes = new HashSet<ICubridNode>();
    private long lastSelectedTime = 0L; // for TreeView bug
    private static final int SASH_WIDTH = 3;

    /*database index will be set at 2 place with following order
     * 1.when first set input to the tree ,if the server can be connected,
     * it's database will be set the index
     * 2.when expand the database folder on the tree ,if the database not set index,
     * it will be set
     */
    private static int databaseIndex = 1;
    private ToolItem runItem;

    /**
     * The constructor
     *
     * @param parent
     * @param style
     */
    public MultiDBQueryComposite(CTabFolder parent, int style, QueryEditorPart editor) {
        super(parent, style);
        this.resultTabFolder = parent;
        this.editor = editor;
        GridLayout tLayout = new GridLayout(1, true);
        tLayout.verticalSpacing = 0;
        tLayout.horizontalSpacing = 0;
        tLayout.marginWidth = 0;
        tLayout.marginHeight = 0;

        setLayout(tLayout);
    }

    public ToolItem getRunItem() {
        return runItem;
    }

    public void setMainDatabase(CubridDatabase database) {
        if (database != null && database.isLogined()) {
            selectedNodes.add(database);
            setInput();
        }
    }

    /** Create the SQL history composite */
    public void initialize() {
        Composite toolBarComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 10;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        toolBarComposite.setLayout(gridLayout);
        toolBarComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

        ToolItem refreshToolItem = new ToolItem(toolBar, SWT.PUSH);
        refreshToolItem.setText(Messages.lblItemRefreshMulti);
        refreshToolItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_refresh.png"));
        refreshToolItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_refresh.png"));
        refreshToolItem.setToolTipText(Messages.refresh);
        refreshToolItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        // refresh may set tree to group type or not
                        // so mark the select db then set it to checked after set input
                        //				Object[] checkedObject = ctv.getCheckedElements();
                        setInput();
                        //				for (Object o : checkedObject) {
                        //					if (o instanceof CubridDatabase) {
                        //						ctv.setChecked(o, true);
                        //					}
                        //				}

                        //				refresh(editor.getSelectedDatabase());
                    }
                });

        runItem = new ToolItem(toolBar, SWT.PUSH);
        runItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run.png"));
        runItem.setDisabledImage(
                CommonUIPlugin.getImage("icons/queryeditor/query_run_disabled.png"));
        runItem.setText(Messages.lblItemRunMulti);
        runItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editor.runMultiQuery();
                    }
                });

        Label lblNotice = new Label(toolBarComposite, SWT.None);
        lblNotice.setText(Messages.qedit_multiDBQueryComp_noticeToolbarMsg);
        lblNotice.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, true, false));

        // create the query result tab folder
        multiDBQueryCompTabFolder = new CTabFolder(this, SWT.BOTTOM);
        multiDBQueryCompTabFolder.setSimple(false);
        multiDBQueryCompTabFolder.setUnselectedImageVisible(true);
        multiDBQueryCompTabFolder.setUnselectedCloseVisible(true);
        multiDBQueryCompTabFolder.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
        multiDBQueryCompTabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
        multiDBQueryCompTabFolder.setLayout(new GridLayout(1, true));
        multiDBQueryCompTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        // TabContextMenuManager tabContextMenuManager = new
        // TabContextMenuManager(multiDBQueryCompTabFolder);
        // tabContextMenuManager.createContextMenu();

        multiDBQueryCompTabItem = new CTabItem(resultTabFolder, SWT.NONE);
        multiDBQueryCompTabItem.setText(Messages.qedit_multiDBQueryComp_folder);
        multiDBQueryCompTabItem.setControl(this);
        multiDBQueryCompTabItem.setShowClose(false);

        SashForm bottomSash = new SashForm(multiDBQueryCompTabFolder, SWT.VERTICAL);
        bottomSash.SASH_WIDTH = SASH_WIDTH;
        bottomSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        SashForm tailSash = new SashForm(bottomSash, SWT.HORIZONTAL);
        tailSash.SASH_WIDTH = SASH_WIDTH;
        tailSash.setBackground(CombinedQueryEditorComposite.BACK_COLOR);

        Composite treeComp = new Composite(tailSash, SWT.NONE);
        {
            treeComp.setLayoutData(new GridData(GridData.FILL_BOTH));
            treeComp.setLayout(new GridLayout());
        }

        ctv =
                new ContainerCheckedTreeViewer(
                        treeComp,
                        SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        ctv.getControl()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
        ctv.setContentProvider(new MultiDBQueryTreeContentProvider());
        ctv.addCheckStateListener(
                new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        if (lastSelectedTime != 0
                                && lastSelectedTime > System.currentTimeMillis()) {
                            ctv.setChecked(event.getElement(), true);
                            return;
                        }

                        ctv.setChecked(event.getElement(), false);
                        lastSelectedTime = System.currentTimeMillis() + 100;

                        if (getQueryDatabaseList().size() > 0
                                && editor.getAllQueries().trim().length() != 0) {
                            editor.setMultiQueryRunItemStatus(true);
                        } else if (getQueryDatabaseList().size() == 0) {
                            editor.setMultiQueryRunItemStatus(false);
                        }

                        // if (!event.getChecked()) {
                        selectedNodes.remove(event.getElement());
                        // }

                        if (event.getElement() instanceof CubridDatabase) {
                            CubridDatabase database = (CubridDatabase) event.getElement();
                            if (database.getRunningType() == DbRunningType.STANDALONE
                                    || !database.isLogined()) {
                                // ctv.setChecked(event.getElement(), false);
                                selectedNodes.remove(database);
                            } else {
                                if (database.getData(INDEXKEY) == null) {
                                    database.setData(INDEXKEY, String.valueOf(databaseIndex++));
                                }
                            }
                        } else if (event.getElement() instanceof CubridServer) {
                            CubridServer serverNode = (CubridServer) event.getElement();
                            for (ICubridNode dbFolderNode : serverNode.getChildren()) {
                                for (ICubridNode dbNode : dbFolderNode.getChildren()) {
                                    if (dbNode instanceof CubridDatabase) {
                                        CubridDatabase database = (CubridDatabase) dbNode;
                                        if (database.getRunningType() == DbRunningType.STANDALONE
                                                || !database.isLogined()) {
                                            // ctv.setChecked(dbNode, false);
                                            selectedNodes.remove(database);
                                        } else if (ctv.getChecked(dbNode)) {
                                            ctv.setChecked(dbNode, true);
                                            if (dbNode.getData(INDEXKEY) == null) {
                                                dbNode.setData(
                                                        INDEXKEY, String.valueOf(databaseIndex++));
                                            }
                                        } else {
                                            ctv.setChecked(dbNode, true);
                                        }
                                    }
                                }
                                // only has one db folder  so first time break it
                                break;
                            }
                        } else if (event.getElement() instanceof ICubridNode) {
                            ICubridNode node = (ICubridNode) event.getElement();
                            if (node.getType().equals(NodeType.DATABASE_FOLDER)) {
                                for (ICubridNode dbNode : node.getChildren()) {
                                    if (dbNode instanceof CubridDatabase) {
                                        CubridDatabase database = (CubridDatabase) dbNode;
                                        if (database.getRunningType() == DbRunningType.STANDALONE
                                                || !database.isLogined()) {
                                            ctv.setChecked(dbNode, false);
                                        } else if (ctv.getChecked(dbNode)) {
                                            ctv.setChecked(dbNode, true);
                                            if (dbNode.getData(INDEXKEY) == null) {
                                                dbNode.setData(
                                                        INDEXKEY, String.valueOf(databaseIndex++));
                                            }
                                        } else {
                                            ctv.setChecked(dbNode, false);
                                        }
                                    }
                                }
                            }
                            //					else if (node.getType().equals(NodeType.GROUP)) {
                            //						for (ICubridNode childNode : node.getChildren()) {
                            //							//CQB tree
                            //							if(childNode instanceof CubridDatabase) {
                            //								CubridDatabase database = (CubridDatabase) childNode;
                            //								if (database.getRunningType() == DbRunningType.STANDALONE
                            //										|| !database.isLogined()) {
                            //									if (childNode.getData(INDEXKEY) == null) {
                            //										childNode.setData(INDEXKEY,
                            // String.valueOf(databaseIndex++));
                            //									}
                            //								}
                            //								continue;
                            //							}
                            //							//CM tree
                            //							for (ICubridNode dbFolderNode : childNode.getChildren()) {
                            //								for (ICubridNode dbNode : dbFolderNode.getChildren()) {
                            //									if(dbNode instanceof CubridDatabase) {
                            //										CubridDatabase database = (CubridDatabase) dbNode;
                            //										if (database.getRunningType() == DbRunningType.STANDALONE
                            //												|| !database.isLogined()) {
                            //											ctv.setChecked(dbNode, false);
                            //										} else if (ctv.getChecked(dbNode)) {
                            //											ctv.setChecked(dbNode, true);
                            //											if (dbNode.getData(INDEXKEY) == null) {
                            //												dbNode.setData(INDEXKEY, String.valueOf(databaseIndex++));
                            //											}
                            //										} else {
                            //											ctv.setChecked(dbNode, false);
                            //										}
                            //									}
                            //								}
                            //							}
                            //							//only has one db folder  so first time break it
                            //							break;
                            //						}
                            //					}
                        }
                        ctv.refresh();
                    }
                });

        ctv.getTree()
                .addKeyListener(
                        new KeyAdapter() {
                            public void keyReleased(KeyEvent event) {
                                // mac can't drag and drop,so support copy and paste db node
                                if (((event.stateMask & SWT.CTRL) != 0)
                                        || ((event.stateMask & SWT.COMMAND) != 0)
                                                && event.keyCode == 'v') {
                                    final Clipboard cb = new Clipboard(getShell().getDisplay());

                                    String plainText =
                                            (String) cb.getContents(TextTransfer.getInstance());
                                    String[] databaseNameArray = plainText.split(",");
                                    selectedNodes.addAll(getDatabaseNode(databaseNameArray));
                                    setInput();
                                }
                            }
                        });
        //		ctv.addDoubleClickListener(LayoutManager.getInstance());
        //		ctv.addTreeListener(new ITreeViewerListener() {
        //			public void treeCollapsed(TreeExpansionEvent event) {
        //				CommonTool.clearExpandedElements(ctv);
        //			}
        //
        //			public void treeExpanded(TreeExpansionEvent event) {
        //				CommonTool.clearExpandedElements(ctv);
        //			}
        //		});

        final Tree tableTree = ctv.getTree();
        tableTree.setHeaderVisible(true);
        tableTree.setLinesVisible(true);

        final TreeViewerColumn dbCol = new TreeViewerColumn(ctv, SWT.NONE);
        dbCol.setLabelProvider(new MultiDBQueryTreeColumnLabelProvider());
        dbCol.getColumn().setWidth(250);
        dbCol.getColumn().setText(Messages.qedit_multiDBQueryComp_tree_dbCol);

        final TreeViewerColumn indexCol = new TreeViewerColumn(ctv, SWT.NONE);
        indexCol.setLabelProvider(new MultiDBQueryTreeColumnLabelProvider());
        indexCol.getColumn().setWidth(50);
        indexCol.getColumn().setText(Messages.qedit_multiDBQueryComp_tree_indexCol);
        indexCol.setEditingSupport(
                new EditingSupport(ctv) {
                    TextCellEditor textCellEditor;

                    protected boolean canEdit(Object element) {
                        if (element instanceof ICubridNode) {
                            ICubridNode node = (ICubridNode) element;
                            if (node.getType() == NodeType.DATABASE) {
                                CubridDatabase database = (CubridDatabase) element;
                                if ((database.getRunningType() == DbRunningType.CS
                                        && database.isLogined())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }

                    protected CellEditor getCellEditor(Object element) {
                        if (textCellEditor == null) {
                            textCellEditor = new TextCellEditor(ctv.getTree());
                            textCellEditor.setValidator(new IndexCellEditorValidator());
                            textCellEditor.addListener(
                                    new ICellEditorListener() {

                                        public void applyEditorValue() {}

                                        public void cancelEditor() {}

                                        public void editorValueChanged(
                                                boolean oldValidState, boolean newValidState) {}
                                    });
                        }
                        return textCellEditor;
                    }

                    protected Object getValue(Object element) {
                        final ICubridNode node = (ICubridNode) element;
                        String index = (String) node.getData(INDEXKEY);
                        if (index == null) {
                            return "";
                        } else {
                            return index;
                        }
                    }

                    protected void setValue(Object element, Object value) {
                        if (value == null) {
                            return;
                        }

                        try {
                            Integer.valueOf((String) value);
                        } catch (Exception e) {
                            return;
                        }
                        final ICubridNode node = (ICubridNode) element;
                        node.setData(INDEXKEY, value);
                        //				setAllParentExpandOrCollapse(node.getParent(), true);
                        ctv.refresh();
                    }

                    /**
                     * MyCellEditorValidator
                     *
                     * @author fulei
                     */
                    class IndexCellEditorValidator implements ICellEditorValidator {

                        /**
                         * isValid
                         *
                         * @param value Object
                         * @return String
                         */
                        public String isValid(Object value) {
                            try {
                                Integer.valueOf((String) value);
                            } catch (Exception e) {
                                CommonUITool.openErrorBox(
                                        Messages.qedit_multiDBQueryComp_tree_indexErr);
                                return Messages.qedit_multiDBQueryComp_tree_indexErr;
                            }
                            return null;
                        }
                    }
                });

        final TreeViewerColumn commentCol = new TreeViewerColumn(ctv, SWT.NONE);
        commentCol.setLabelProvider(new MultiDBQueryTreeColumnLabelProvider());
        commentCol.getColumn().setWidth(200);
        commentCol.getColumn().setText(Messages.qedit_multiDBQueryComp_tree_commentCol);

        CTabItem tabItem = new CTabItem(multiDBQueryCompTabFolder, SWT.NONE);
        tabItem.setText(Messages.qedit_multiDBQueryComp_tabItem);
        tabItem.setControl(bottomSash);
        tabItem.setShowClose(false);
        setInput();
        //		HostNodePersistManager.getInstance().getAllServer();
        multiDBQueryCompTabFolder.setSelection(tabItem);

        dragController = new MultiDBQueryDNDController(this, ctv);
        dragController.registerDropTarget();
    }

    public void refresh(CubridDatabase database) {
        // for selected database on the query editor
        try {
            ctv.setChecked(database, true);
            ctv.refresh();
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setInput() {
        // on CM
        CubridNavigatorView navigatorView = CubridNavigatorView.findNavigationView();

        if (navigatorView != null) {
            // navigatorView.setTreeInputNoGroup(ctv);
            List<ICubridNode> newItems = null;
            List<ICubridNode> items = (List<ICubridNode>) navigatorView.getRootList();
            if (items.size() > 0 && items.get(0) instanceof CubridServer) {
                newItems = new ArrayList<ICubridNode>();
                for (Object citem : (List) navigatorView.getRootList()) {
                    CubridServer cubridServer = (CubridServer) citem;
                    if (cubridServer.getChildren() == null
                            || cubridServer.getChildren().size() == 0) {
                        continue;
                    }
                    ICubridNode databaseFolder = cubridServer.getChildren().get(0);
                    for (ICubridNode cubridDatabase : databaseFolder.getChildren()) {
                        newItems.add(cubridDatabase);
                    }
                }
                items = newItems;
            }

            Collections.sort(
                    items,
                    new Comparator<ICubridNode>() {
                        public int compare(ICubridNode db1, ICubridNode db2) {
                            if (db1.getLabel() == null && db2.getLabel() == null) {
                                return 0;
                            } else if (db2.getLabel() == null) {
                                return -1;
                            } else if (db1.getLabel() == null) {
                                return 1;
                            }

                            return (db1.getLabel().compareTo(db2.getLabel()));
                        }
                    });

            int index = 1;
            for (ICubridNode item : items) {
                if (selectedNodes.contains(item)) {
                    item.setData(INDEXKEY, String.valueOf(index++));
                } else {
                    item.setData(INDEXKEY, "0");
                }
            }

            ctv.setInput(items);

            initialIndex();
            //			setExpand();
            //			refresh(editor.getSelectedDatabase());
            //			setCheckedDatabase(navigatorView.getMultiDBQuerySelectedDBList());
        }
    }

    //	public void  setCheckedDatabase(List<CubridDatabase> databaseList) {
    //		for (CubridDatabase cubridDatabase : databaseList) {
    //			ctv.setChecked(cubridDatabase, true);
    //		}
    //		ctv.refresh();
    //	}

    //	@SuppressWarnings("unchecked")
    //	public void setExpand() {
    //		ctv.expandAll();
    //		Object inputElement= ctv.getInput();
    //		//check the CM tree ,group tree whether the group has available database
    //		//if not the this group node expand to false
    //		if (inputElement instanceof List) {
    //			List<ICubridNode> list = (List<ICubridNode>) inputElement;
    //			for (ICubridNode node : list) {
    //				if (node.getType() == NodeType.GROUP) {
    //					boolean flag = false;
    //					outer:
    //					//CM tree
    //					for (ICubridNode childNode : node.getChildren()) {
    //						if (childNode.getType().equals(NodeType.SERVER)) {
    //							for(ICubridNode folderNode : childNode.getChildren()) {
    //								if (folderNode.getType().equals(NodeType.DATABASE_FOLDER)) {
    //									for(ICubridNode dbNode : folderNode.getChildren()) {
    //										if(dbNode.getType().equals(NodeType.DATABASE)) {
    //											CubridDatabase database = (CubridDatabase) dbNode;
    //											if ((database.getRunningType() == DbRunningType.CS
    //													&& database.isLogined())) {
    //												flag = true;
    //												break outer;
    //											}
    //										}
    //									}
    //									break;
    //								}
    //							}
    //						} else if(childNode.getType().equals(NodeType.DATABASE)) {//CQB tree
    //							CubridDatabase database = (CubridDatabase) childNode;
    //							if ((database.getRunningType() == DbRunningType.CS
    //									&& database.isLogined())) {
    //								flag = true;
    //								break outer;
    //							}
    //						}
    //						if (!flag) {
    //							ctv.setExpandedState(node, false);
    //						}
    //					}
    //
    //				} else if (node.getType() == NodeType.SERVER) {
    //					boolean flag = false;
    //					outer:
    //					for(ICubridNode folderNode : node.getChildren()) {
    //						if (folderNode.getType().equals(NodeType.DATABASE_FOLDER)) {
    //							for(ICubridNode dbNode : folderNode.getChildren()) {
    //								if(dbNode.getType().equals(NodeType.DATABASE)) {
    //									CubridDatabase database = (CubridDatabase) dbNode;
    //									if ((database.getRunningType() == DbRunningType.CS
    //											&& database.isLogined())) {
    //										flag = true;
    //										break outer;
    //									}
    //								}
    //							}
    //							break;
    //						}
    //					}
    //					if (!flag) {
    //						ctv.setExpandedState(node, false);
    //					}
    //				}
    //			}
    //		}
    //	}

    public List<CubridDatabase> getQueryDatabaseList() {
        List<CubridDatabase> queryDatabaseList = new ArrayList<CubridDatabase>();
        Object[] objects = ctv.getCheckedElements();
        for (Object object : objects) {
            if (object instanceof ICubridNode) {
                if (!selectedNodes.contains(object)) {
                    continue;
                }
                ICubridNode node = (ICubridNode) object;
                if (node.getType() == NodeType.DATABASE) {
                    CubridDatabase database = (CubridDatabase) node;
                    if (database.getRunningType() == DbRunningType.CS && database.isLogined()) {
                        queryDatabaseList.add(database);
                    }
                }
            }
        }

        Collections.sort(
                queryDatabaseList,
                new Comparator<CubridDatabase>() {
                    public int compare(CubridDatabase db1, CubridDatabase db2) {
                        if (db1.getData(INDEXKEY) == null && db2.getData(INDEXKEY) == null) {
                            return 0;
                        } else if (db2.getData(INDEXKEY) == null) {
                            return -1;
                        } else if (db1.getData(INDEXKEY) == null) {
                            return 1;
                        }
                        int index1 = Integer.valueOf((String) db1.getData(INDEXKEY));
                        int index2 = Integer.valueOf((String) db2.getData(INDEXKEY));
                        if (index1 == index2) {
                            return 0;
                        }
                        return index1 < index2 ? -1 : 1;
                    }
                });
        return queryDatabaseList;
    }

    public void initialIndex() {
        @SuppressWarnings("unchecked")
        List<ICubridNode> dataList = (List<ICubridNode>) ctv.getInput();
        if (dataList == null) {
            return;
        }

        for (int i = 0; i < dataList.size(); i++) {
            ICubridNode node = dataList.get(i);
            ctv.setChecked(node, true);

            //			if (!selectedNodes.contains(node)) {
            //				continue;
            //			}

            //			if (node.getType().equals(NodeType.SERVER)) {
            //				for (ICubridNode dbFolderNode : node.getChildren()) {
            //					for (ICubridNode dbNode : dbFolderNode.getChildren()) {
            //						if (dbNode.getType() == NodeType.DATABASE) {
            //							CubridDatabase database = (CubridDatabase) dbNode;
            //							if ((database.getRunningType() == DbRunningType.CS
            //									&& database.isLogined())) {
            //								if (dbNode.getData(INDEXKEY) == null) {
            //									dbNode.setData(INDEXKEY, String.valueOf(databaseIndex++));
            //								}
            //								ctv.setChecked(dbNode, true);
            //							}
            //						}
            //
            //					}
            //					//only has one db folder  so first time break it
            //					break;
            //				}
            //			}
            //			if (node.getType().equals(NodeType.DATABASE)) {
            //				CubridDatabase database = (CubridDatabase)node;
            //				if ((database.getRunningType() == DbRunningType.CS
            //						&& database.isLogined())) {
            //					if (node.getData(INDEXKEY) == null) {
            //						node.setData(INDEXKEY, String.valueOf(databaseIndex++));
            //					}
            //					ctv.setChecked(node, true);
            //				}
            //			}
            //
            //			if (node.getType().equals(NodeType.GROUP)) {
            //				//CQB
            //				for (ICubridNode dbNode : node.getChildren()) {
            //					if (dbNode.getType() == NodeType.DATABASE) {
            //						CubridDatabase database = (CubridDatabase) dbNode;
            //						if ((database.getRunningType() == DbRunningType.CS
            //								&& database.isLogined())) {
            //							if (dbNode.getData(INDEXKEY) == null) {
            //								dbNode.setData(INDEXKEY, String.valueOf(databaseIndex++));
            //							}
            //							ctv.setChecked(dbNode, true);
            //						}
            //					}
            //				}
            //			}
        }
    }

    public class MultiDBQueryTreeContentProvider implements ITreeContentProvider {
        /**
         * Return the elements to display in the tree viewer when its input is set to the given
         * element.
         *
         * @param inputElement the input element
         * @return the array of elements to display in the viewer
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<ICubridNode> list = (List<ICubridNode>) inputElement;
                // CQB tree
                List<ICubridNode> dbList = new ArrayList<ICubridNode>();
                for (ICubridNode node : list) {
                    if (!selectedNodes.contains(node)) {
                        continue;
                    }

                    if (node.getType() == NodeType.DATABASE) {
                        CubridDatabase database = (CubridDatabase) node;
                        if ((database.getRunningType() == DbRunningType.CS
                                && database.isLogined())) {
                            dbList.add(node);
                            //							if (node.getData(INDEXKEY) == null) {
                            //								node.setData(INDEXKEY, String.valueOf(databaseIndex++));
                            //							}
                        }
                    }
                }
                // CQB tree the first level is database
                if (!dbList.isEmpty()) {
                    ICubridNode[] nodeArr = new ICubridNode[dbList.size()];
                    return dbList.toArray(nodeArr);
                }
                //				else {
                //					ICubridNode[] nodeArr = new ICubridNode[list.size()];
                //					return list.toArray(nodeArr);
                //				}
            }
            return new Object[] {};
        }

        /**
         * Return the children list
         *
         * @param element the input element
         * @return the array of elements to display in the viewer
         */
        public Object[] getChildren(Object element) {
            if (element instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) element;
                /* because server 's children contains databasefolder/broker/monitor/log ,so if the
                 * node is server .only add the databasefolder
                 */
                List<ICubridNode> childList = new ArrayList<ICubridNode>();
                if (node.getType().equals(NodeType.GROUP)) {
                    for (ICubridNode childNode : node.getChildren()) {
                        if (!selectedNodes.contains(childNode)) {
                            continue;
                        }

                        // CQB tree
                        if (childNode.getType().equals(NodeType.DATABASE)) {
                            CubridDatabase database = (CubridDatabase) childNode;
                            if ((database.getRunningType() != DbRunningType.CS
                                    || !database.isLogined())) {
                                continue;
                            }
                        }
                        childList.add(childNode);
                    }
                } else if (node.getType().equals(NodeType.SERVER)) {
                    for (ICubridNode childNode : node.getChildren()) {
                        if (!selectedNodes.contains(childNode)) {
                            continue;
                        }

                        if (childNode.getType().equals(NodeType.DATABASE_FOLDER)) {
                            childList.add(childNode);
                        }
                    }
                } else if (node.getType().equals(NodeType.DATABASE_FOLDER)) {
                    // database node
                    for (ICubridNode dbNode : node.getChildren()) {
                        if (!selectedNodes.contains(dbNode)) {
                            continue;
                        }

                        if (dbNode.getType() == NodeType.DATABASE) {
                            CubridDatabase database = (CubridDatabase) dbNode;
                            if ((database.getRunningType() == DbRunningType.CS
                                    && database.isLogined())) {
                                if (dbNode.getData(INDEXKEY) == null) {
                                    dbNode.setData(INDEXKEY, String.valueOf(databaseIndex++));
                                }
                                childList.add(dbNode);
                            }
                        }
                    }
                } else if (node.getType().equals(NodeType.DATABASE)) {
                    if (selectedNodes.contains(node)) {
                        CubridDatabase database = (CubridDatabase) node;
                        if ((database.getRunningType() == DbRunningType.CS
                                && database.isLogined())) {
                            if (node.getData(INDEXKEY) == null) {
                                node.setData(INDEXKEY, String.valueOf(databaseIndex++));
                            }
                            childList.add(node);
                        }
                    }
                }
                ICubridNode[] nodeArr = new ICubridNode[childList.size()];
                return childList.toArray(nodeArr);
            }

            return new Object[] {};
        }

        /**
         * Return whether the given element has children.
         *
         * @param element the element
         * @return <code>true</code> if the given element has children, and <code>false</code> if it
         *     has no children
         */
        public boolean hasChildren(Object element) {
            if (element instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) element;
                if (node.getType().equals(NodeType.DATABASE_FOLDER)) {
                    return true;
                }
                if (node.getType().equals(NodeType.GROUP)) {
                    // CQB tree
                    boolean cqbFlag = false;
                    for (ICubridNode childNode : node.getChildren()) {
                        if (childNode.getType().equals(NodeType.DATABASE)) {
                            cqbFlag = true;
                            CubridDatabase database = (CubridDatabase) childNode;
                            if ((database.getRunningType() == DbRunningType.CS
                                    && database.isLogined())) {
                                return true;
                            }
                        }
                    }
                    // if CQB tree but under the group has no available db ,return false
                    if (cqbFlag) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (node.getType().equals(NodeType.SERVER)) {
                    CubridServer serverNode = (CubridServer) element;
                    return serverNode.isConnected() ? true : false;
                }
            }
            return false;
        }

        /**
         * Return the parent for the given element, or <code>null</code>
         *
         * @param element the element
         * @return the parent element, or <code>null</code> if it has none or if the parent cannot
         *     be computed
         */
        public Object getParent(Object element) {
            if (element instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) element;
                return node.getParent();
            }
            return null;
        }

        /** Disposes of this content provider. This is called by the viewer when it is disposed. */
        public void dispose() {
            // ignore
        }

        /**
         * Notifies this content provider that the given viewer's input has been switched to a
         * different element.
         *
         * @param viewer the viewer
         * @param oldInput the old input element, or <code>null</code> if the viewer did not
         *     previously have an input
         * @param newInput the new input element, or <code>null</code> if the viewer does not have
         *     an input
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    /** CUBIRD manager navigator treeviewer label provider */
    static class TreeLabelProvider extends LabelProvider
            implements ITableLabelProvider, ITableColorProvider {
        private boolean isCMMode = PerspectiveManager.getInstance().isManagerMode();
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
         */
        public Color getForeground(Object element, int columnIndex) {
            return ResourceManager.getColor(SWT.COLOR_BLACK);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
         */
        public Color getBackground(Object element, int columnIndex) {
            if (element instanceof CubridDatabase) {
                CubridDatabase database = (CubridDatabase) element;
                if (database != null && database.getServer() != null) {
                    DatabaseEditorConfig editorConfig =
                            QueryOptions.getEditorConfig(database, isCMMode);
                    if (editorConfig != null && editorConfig.getBackGround() != null) {
                        return ResourceManager.getColor(editorConfig.getBackGround());
                    }
                }
            }
            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            String iconPath = "";
            if (element instanceof CubridServer) {
                CubridServer server = (CubridServer) element;
                if (server.isConnected()) {
                    iconPath = server.getConnectedIconPath();
                } else {
                    iconPath = server.getDisConnectedIconPath();
                }
            } else if (element instanceof CubridDatabase) {
                CubridDatabase database = (CubridDatabase) element;
                if (database.getRunningType() == DbRunningType.STANDALONE && database.isLogined()) {
                    iconPath = database.getStopAndLoginIconPath();
                } else if (database.getRunningType() == DbRunningType.STANDALONE
                        && !database.isLogined()) {
                    iconPath = database.getStopAndLogoutIconPath();
                } else if (database.getRunningType() == DbRunningType.CS && database.isLogined()) {
                    iconPath = database.getStartAndLoginIconPath();
                } else if (database.getRunningType() == DbRunningType.CS && !database.isLogined()) {
                    iconPath = database.getStartAndLogoutIconPath();
                }
            } else if (element instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) element;
                iconPath = node.getIconPath();
            }
            if (iconPath != null && iconPath.length() > 0) {
                //				return CubridManagerUIPlugin.getImage(iconPath.trim());
            }
            return super.getImage(element);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ICubridNode) {
                return ((ICubridNode) element).getLabel();
            } else if (element instanceof PendingUpdateAdapter) {
                return "Loading...";
            }
            return element == null ? "" : element.toString();
        }
    }

    static class MultiDBQueryTreeColumnLabelProvider extends CellLabelProvider {
        private boolean isCMMode = PerspectiveManager.getInstance().isManagerMode();
        /**
         * update
         *
         * @param cell ViewerCell
         */
        public void update(ViewerCell cell) {
            if (cell.getColumnIndex() == 0) {
                if (cell.getElement() instanceof ICubridNode) {
                    ICubridNode node = (ICubridNode) cell.getElement();
                    cell.setImage(CommonUIPlugin.getImage("/icons/queryeditor/qe_panel_down.png"));
                    cell.setText(
                            node.getName()
                                    + "@"
                                    + node.getServer().getHostAddress()
                                    + ":"
                                    + node.getServer().getMonPort());
                }

                String iconPath = "";
                if (cell.getElement() instanceof CubridServer) {
                    CubridServer server = (CubridServer) cell.getElement();
                    if (server.isConnected()) {
                        iconPath = server.getConnectedIconPath();
                    } else {
                        iconPath = server.getDisConnectedIconPath();
                    }
                } else if (cell.getElement() instanceof CubridDatabase) {
                    CubridDatabase database = (CubridDatabase) cell.getElement();
                    if (database.getRunningType() == DbRunningType.STANDALONE
                            && database.isLogined()) {
                        iconPath = database.getStopAndLoginIconPath();
                    } else if (database.getRunningType() == DbRunningType.STANDALONE
                            && !database.isLogined()) {
                        iconPath = database.getStopAndLogoutIconPath();
                    } else if (database.getRunningType() == DbRunningType.CS
                            && database.isLogined()) {
                        iconPath = database.getStartAndLoginIconPath();
                    } else if (database.getRunningType() == DbRunningType.CS
                            && !database.isLogined()) {
                        iconPath = database.getStartAndLogoutIconPath();
                    }
                } else if (cell.getElement() instanceof ICubridNode) {
                    ICubridNode node = (ICubridNode) cell.getElement();
                    iconPath = node.getIconPath();
                }
                if (iconPath != null && iconPath.length() > 0) {
                    cell.setImage(CommonUIPlugin.getImage(iconPath.trim()));
                }
            } else if (cell.getColumnIndex() == 1 && cell.getElement() instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) cell.getElement();
                String index = (String) node.getData(INDEXKEY);
                if (index != null) {
                    cell.setText(index);
                }

            } else if (cell.getColumnIndex() == 2 && cell.getElement() instanceof CubridDatabase) {
                CubridDatabase database = (CubridDatabase) cell.getElement();
                if (database != null) {
                    DatabaseEditorConfig editorConfig =
                            QueryOptions.getEditorConfig(database, isCMMode);
                    if (editorConfig != null) {
                        cell.setText(editorConfig.getDatabaseComment());
                    }
                }
            }
        }
    }

    /**
     * find database node by db name array
     *
     * @param databaseNameArray String[]
     * @return database node list
     */
    @SuppressWarnings({"unchecked"})
    public List<ICubridNode> getDatabaseNode(String[] databaseNameArray) {
        ArrayList<ICubridNode> databaseNodeList = new ArrayList<ICubridNode>();
        Set<String> typeSet = new HashSet<String>();
        typeSet.add(NodeType.DATABASE);
        CubridNavigatorView navigatorView =
                CubridNavigatorView.getNavigatorView("com.cubrid.cubridmanager.host.navigator");
        if (navigatorView == null) {
            // on CQB
            navigatorView =
                    CubridNavigatorView.getNavigatorView(
                            "com.cubrid.cubridquery.connection.navigator");
        }
        List<ICubridNode> items = (List<ICubridNode>) navigatorView.getRootList();
        for (ICubridNode cubridNode : items) {
            for (String databaseName : databaseNameArray) {
                ICubridNode databaseNode = CommonUITool.findNode(cubridNode, typeSet, databaseName);
                if (databaseNode != null) {
                    databaseNodeList.add(databaseNode);
                }
            }
        }
        return databaseNodeList;
    }

    public Set<ICubridNode> getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(Set<ICubridNode> selectedNodes) {
        this.selectedNodes = selectedNodes;
    }
}
