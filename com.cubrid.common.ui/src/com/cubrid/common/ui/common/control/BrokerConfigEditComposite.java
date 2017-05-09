package com.cubrid.common.ui.common.control;

import static com.cubrid.common.core.util.NoOp.noOp;
import static com.cubrid.common.core.util.StringUtil.NEWLINE;
import static com.cubrid.common.ui.spi.ResourceManager.getColor;
import static com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil.ANNOTATION;
import static com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil.BROKERNAMECOLUMNTITLE;
import static com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil.UNIFORMCONFIG;
import static com.cubrid.common.ui.spi.util.CommonUITool.createGridLayout;
import static com.cubrid.common.ui.spi.util.CommonUITool.findSelectItem;
import static com.cubrid.common.ui.spi.util.CommonUITool.openErrorBox;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.dialog.CubridBrokerConfEditAnnotationDialog;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.tool.editor.property.PropEditor;

/**
 * CUBRID broker.conf editor composite.
 *
 * @author CUBRID Tool developer
 */
public class BrokerConfigEditComposite extends
		Composite {
	private static final Logger LOGGER = LogUtil.getLogger(BrokerConfigEditComposite.class);
	private final TableContentProvider confTableContentProvider = new TableContentProvider();
	private final TableLabelProvider confTableLabelProvider = new BrokerConfTableLabelProvider();
	private final List<Map<String, String>> confListData = new ArrayList<Map<String, String>>();
	private TableViewer confTableViewer;
	private AbstractBrokerConfigEditorPart editorPart;
	protected PropEditor propEditor;
	private CTabFolder confTabFolder;
	private CTabItem tableCTabItem;
	private CTabItem sourceCTabItem;
	private Point clickPoint;
	private BrokerConfig brokerConfig;
	private long clickPointTiming;
	// mark whether switch tab
	private boolean switchTab = false;
	private String inputName;

	public BrokerConfigEditComposite(Composite parent, int style,
			AbstractBrokerConfigEditorPart editorPart, String inputName) {
		super(parent, style);
		this.inputName = inputName;
		this.editorPart = editorPart;

		setLayoutData(new GridData(FILL_HORIZONTAL));
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = -1;
		super.setLayout(layout);

		createCubridBrokerConfComp(parent);
	}

	public void createCubridBrokerConfComp(Composite parent) {
		confTabFolder = new CTabFolder(parent, SWT.BOTTOM | SWT.BORDER);
		confTabFolder.setLayout(new FillLayout());
		confTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		confTabFolder.setUnselectedImageVisible(true);
		confTabFolder.setUnselectedCloseVisible(true);
		confTabFolder.setSelectionBackground(getColor(SWT.COLOR_TITLE_FOREGROUND));
		confTabFolder.setSelectionForeground(getColor(SWT.COLOR_TITLE_BACKGROUND));

		createCubridBrokerConfPropTable(confTabFolder);
		createCubridBrokerConfPropEditor(confTabFolder);

		confTabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (event.item == sourceCTabItem) {
					editorPart.setEditTableItemEnabled(false);
					brokerConfig = editorPart.parseCommonTableValueToBrokerConfig(
							confListData, confTableViewer.getTable().getColumnCount());
					createCubridBrokerConfDocumnetContent();
				} else if (event.item == tableCTabItem) {
					editorPart.setEditTableItemEnabled(true);
					brokerConfig = editorPart.parseStringLineToBrokerConfig(propEditor.getDocument().get());
					createBrokerConfTableData();
				}
			}
		});

		confTabFolder.setSelection(tableCTabItem);
	}

	/**
	 * Create property table
	 *
	 * @param cubridBrokerConfTabFolder
	 */
	public void createCubridBrokerConfPropTable(CTabFolder cubridBrokerConfTabFolder) {
		final Composite comp = new Composite(cubridBrokerConfTabFolder, SWT.NONE);
		comp.setLayoutData(new GridData(FILL_HORIZONTAL));
		comp.setLayout(new GridLayout(1, false));

		tableCTabItem = new CTabItem(cubridBrokerConfTabFolder, SWT.NONE);
		tableCTabItem.setText(inputName);

		confTableViewer = new TableViewer(comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		final Table confTable = confTableViewer.getTable();
		confTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		confTable.setHeaderVisible(true);
		confTable.setLinesVisible(true);
		confTableViewer.setUseHashlookup(true);
		CommonUITool.hackForYosemite(confTable);
		
		// create column
		final TableViewerColumn column = new TableViewerColumn(confTableViewer, SWT.LEFT);
		column.getColumn().setWidth(0);
		column.getColumn().setText("");

		confTableViewer.setContentProvider(confTableContentProvider);
		confTableViewer.setLabelProvider(confTableLabelProvider);
		confTableViewer.setSorter(new BrokerConfTableViewerSorter());

		// use to mark click point, the right click menu use this point
		confTable.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				clickPoint = new Point(event.x, event.y);
				clickPointTiming = System.currentTimeMillis();
			}
		});
		registerContextMenu();

		tableCTabItem.setControl(comp);
	}

	/**
	 * Create property editor
	 *
	 * @param cubridBrokerConfTabFolder
	 */
	public void createCubridBrokerConfPropEditor(CTabFolder cubridBrokerConfTabFolder) {
		final Composite comp = new Composite(cubridBrokerConfTabFolder, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp.setLayout(new GridLayout(1, false));

		sourceCTabItem = new CTabItem(cubridBrokerConfTabFolder, SWT.NONE);
		sourceCTabItem.setText(Messages.cubridBrokerConfEditorCTabItemSource);

		final Composite editorComp = new Composite(comp, SWT.BORDER);
		editorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		final GridLayout gridLayout = createGridLayout(1, 0, 0);
		gridLayout.horizontalSpacing = 0;
		editorComp.setLayout(gridLayout);

		propEditor = new PropEditor();
		try {
			propEditor.init(editorPart.getEditorSite(), editorPart.getEditorInput());
		} catch (PartInitException e) {
			LOGGER.error(e.getMessage(), e);
		}
		propEditor.createPartControl(editorComp);
		propEditor.getDocument().addDocumentListener(new DocumentAdpater());

		sourceCTabItem.setControl(comp);
	}

	/**
	 * Register context menu
	 */
	private void registerContextMenu() {
		final Table confTable = confTableViewer.getTable();
		confTable.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent event) {
				ActionManager.getInstance().changeFocusProvider(confTable);
			}
		});

		final MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		final Menu contextMenu = menuManager.createContextMenu(confTable);
		confTable.setMenu(contextMenu);

		final Menu menu = new Menu(editorPart.getSite().getShell(), SWT.POP_UP);
		final MenuItem itemEditAnnotation = new MenuItem(menu, SWT.PUSH);
		itemEditAnnotation.setText(Messages.cubridBrokerConfEditorTableMenuEditAnnotation);
		itemEditAnnotation.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("all")
			public void widgetSelected(SelectionEvent event) {
				// It seems like MenuEvent can't get the mouse click Point
				// so use the point which table MouseDown event marked
				final Point pt = clickPoint;

				int selectIndex = confTable.getSelectionIndex();
				if (selectIndex < 0) {
					return;
				}

				final TableItem item = confTable.getItem(selectIndex);
				if (item == null) {
					return;
				}

				for (int i = 0, len = confTable.getColumnCount(); i < len; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						if (i == 0) {
							openErrorBox(editorPart.getSite().getShell(),
									Messages.cubridBrokerConfEditAnnotationDialogOpenErrorMsg);
							return;
						}

						IStructuredSelection selection = (IStructuredSelection) confTableViewer.getSelection();
						HashMap<String, String> valueMap = (HashMap<String, String>) selection.getFirstElement();
						String brokerName = confListData.get(0).get(String.valueOf(i));
						String parentPropertyKey = valueMap.get("0");
						String parentKey = " ";
						if (selectIndex == 0) {
							parentKey += brokerName;
						} else {
							parentKey += brokerName + "->" + parentPropertyKey;
						}

						String annotationKey = i + ANNOTATION;
						CubridBrokerConfEditAnnotationDialog dialog = new CubridBrokerConfEditAnnotationDialog(
								editorPart.getSite().getShell(), parentKey, annotationKey, valueMap);
						if (dialog.open() == OK_ID) {
							editorPart.setDirty(true);
						}
					}
				}
			}
		});

		final MenuItem itemAddBrokerConf = new MenuItem(menu, SWT.PUSH);
		itemAddBrokerConf.setText(Messages.cubridBrokerConfEditorAddBrokerConfItemLabel);
		itemAddBrokerConf.setImage(CommonUIPlugin.getImage("icons/action/column_insert.png"));
		itemAddBrokerConf.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addBrokerConfColumn();
				editorPart.setDirty(true);
			}
		});

		final MenuItem itemDeleteBrokerConf = new MenuItem(menu, SWT.PUSH);
		itemDeleteBrokerConf.setText(Messages.cubridBrokerConfEditorDeleteBrokerConfItemLabel);
		itemDeleteBrokerConf.setImage(CommonUIPlugin.getImage("icons/action/column_delete.png"));
		itemDeleteBrokerConf.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// It seems like MenuEvent can't get the mouse click Point
				// so use the point which table MouseDown event marked
				final Point pt = clickPoint;

				final TableItem item = findSelectItem(confTable);
				if (item == null) {
					return;
				}

				int columnCount = confTable.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						Map<String, String> valueMap = confListData.get(0);
						String confName = valueMap.get(String.valueOf(i));
						String msg = Messages.bind(
								Messages.cubridBrokerConfEditorDeleteBrokerConfConfirm, confName);
						if (!CommonUITool.openConfirmBox(msg)) {
							return;
						}

						confTable.getColumn(i).dispose();
						for (int j = 1; j < columnCount; j++) {
							confTable.getColumn(j).setText(
									Messages.cubridBrokerConfEditorBrokerTitle + (j - 1));
						}

						// delete data from cubridBrokerConfig, so regenerate cubridBrokerConfListData from cubridBrokerConfig
						editorPart.getBrokerConfPersistUtil().deleteBrokerPropertyByBrokerName(
								brokerConfig, confName);
						confListData.clear();
						confListData.addAll(editorPart.parseBrokerConfigToCommonTableValue(brokerConfig));
						confTableViewer.refresh();
						editorPart.setDirty(true);
						return;
					}
				}

			}
		});

		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent event) {
				// It seems like MenuEvent can't get the mouse click Point
				// so use the point which table MouseDown event marked
				final Point pt = clickPoint;

				// It will allow that the click timing is more than 300 msec.
				if (System.currentTimeMillis() - clickPointTiming > 300) {
					itemEditAnnotation.setEnabled(false);
					itemDeleteBrokerConf.setEnabled(false);
					itemAddBrokerConf.setEnabled(false);
					return;
				}

				int selectIndex = confTable.getSelectionIndex();
				if (selectIndex < 0) {
					itemEditAnnotation.setEnabled(false);
					itemDeleteBrokerConf.setEnabled(false);
					itemAddBrokerConf.setEnabled(true);
					return;
				}

				final TableItem item = confTable.getItem(selectIndex);
				if (item == null) {
					return;
				}
				for (int i = 0, len = confTable.getColumnCount(); i < len; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						boolean enableToAccess = i > 0;
						itemEditAnnotation.setEnabled(enableToAccess);
						itemDeleteBrokerConf.setEnabled(enableToAccess);
					}
				}
				itemAddBrokerConf.setEnabled(true);
			}
		});
		confTable.setMenu(menu);
	}

	public void addPropData() {
		final Table confTable = confTableViewer.getTable();

		HashMap<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("0", "new property");
		for (int i = 1, len = confTable.getColumnCount(); i < len; i++) {
			dataMap.put(String.valueOf(i), "");
		}
		confListData.add(dataMap);
		confTableViewer.refresh();
		editorPart.setDirty(true);
		confTable.showItem(confTable.getItem(confTable.getItemCount() - 1));
	}

	@SuppressWarnings("unchecked")
	public void deletePropData() {
		int selectionIndex = confTableViewer.getTable().getSelectionIndex();
		if (selectionIndex < 0) {
			CommonUITool.openErrorBox(Messages.cubridBrokerConfEditorDeletePropertyMsg);
		}
		IStructuredSelection selection = (IStructuredSelection) confTableViewer.getSelection();
		HashMap<String, String> valueMap = (HashMap<String, String>) selection.getFirstElement();
		if (!CommonUITool.openConfirmBox(Messages.bind(
				Messages.cubridBrokerConfEditorDeleteBrokerPropConfirm, valueMap.get("0")))) {
			return;
		}
		confListData.remove(valueMap);
		confTableViewer.refresh();
		editorPart.setDirty(true);
	}

	public void addBrokerConfColumn() {
		int columnCount = confTableViewer.getTable().getColumnCount();
		String brokerName = "broker" + (columnCount - 1);
		String brokerTitle = Messages.cubridBrokerConfEditorBrokerTitle + (columnCount - 1);

		final TableViewerColumn column = new TableViewerColumn(confTableViewer, SWT.LEFT);
		column.getColumn().setWidth(150);
		column.getColumn().setText(brokerTitle);
		column.setEditingSupport(new PropValueEditingSupport(confTableViewer, columnCount));

		confTableViewer.setLabelProvider(confTableLabelProvider);
		if (confListData.size() > 0) {
			Map<String, String> valueMap = confListData.get(0);
			String columnCountStr = String.valueOf(columnCount);
			valueMap.put(columnCountStr, "[%" + brokerName + "]");
			// add a empty new line annotation
			valueMap.put(columnCountStr + ANNOTATION, NEWLINE);
		}
		confTableViewer.getTable().showColumn(column.getColumn());
		confTableViewer.refresh();
	}

	/**
	 * Set table or editor content to CubridBrokerConfig
	 *
	 * @return
	 */
	public BrokerConfig getBrokerConfig() {
		if (confTabFolder.getSelection() == sourceCTabItem) {
			brokerConfig = editorPart.parseStringLineToBrokerConfig(propEditor.getDocument().get());
		} else if (confTabFolder.getSelection() == tableCTabItem) {
			brokerConfig = editorPart.parseCommonTableValueToBrokerConfig(confListData,
					confTableViewer.getTable().getColumnCount());
			createBrokerConfTableData();
		}

		return brokerConfig;
	}

	/**
	 * Create documnet content by CubridBrokerConf model
	 */
	public void createCubridBrokerConfDocumnetContent() {
		String contents = editorPart.parseBrokerConfigToDocumnetString(brokerConfig);
		switchTab = true;
		propEditor.setDocumentString(contents);
	}

	/**
	 * Create table data by CubridBrokerConf model
	 */
	public void createBrokerConfTableData() {
		// remove column
		int oldCount = confTableViewer.getTable().getColumnCount();
		for (int i = 0; i < oldCount; i++) {
			confTableViewer.getTable().getColumn(0).dispose();
		}

		// create column
		TableViewerColumn column = new TableViewerColumn(confTableViewer, SWT.LEFT);
		column.getColumn().setWidth(150);
		column.getColumn().setText(Messages.cubridBrokerConfEditorColumnPropName);
		column.setEditingSupport(new PropValueEditingSupport(confTableViewer, 0));

		for (int i = 0; i < brokerConfig.getPropertyList().size(); i++) {
			TableViewerColumn propColumn = new TableViewerColumn(confTableViewer, SWT.LEFT);
			propColumn.getColumn().setWidth(160);
			propColumn.getColumn().setText(Messages.cubridBrokerConfEditorBrokerTitle + i);
			propColumn.setEditingSupport(new PropValueEditingSupport(confTableViewer, i + 1));
			propColumn.getColumn().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					noOp();
				}
			});
		}

		confTableViewer.setLabelProvider(confTableLabelProvider);
		confTableViewer.setInput(confListData);
		confListData.clear();
		confListData.addAll(editorPart.parseBrokerConfigToCommonTableValue(brokerConfig));
		confTableViewer.refresh();
	}

	/**
	 * PropValueEditingSupport
	 *
	 * @author fulei
	 */
	public class PropValueEditingSupport extends
			EditingSupport {
		private TableViewer brokerConfTableViewer;
		private TextCellEditor propTextCellEditor;
		private final int columnIndex;

		public PropValueEditingSupport(TableViewer brokerConfTableViewer, int columnIndex) {
			super(brokerConfTableViewer);
			this.brokerConfTableViewer = brokerConfTableViewer;
			this.columnIndex = columnIndex;
		}

		@SuppressWarnings("unchecked")
		protected boolean canEdit(Object element) {
			if (element instanceof HashMap) {
				HashMap<String, String> valueMap = (HashMap<String, String>) element;
				if (valueMap.get("0").equals(BROKERNAMECOLUMNTITLE) && columnIndex == 0) {
					return false;
				}
			}
			return true;
		}

		protected CellEditor getCellEditor(Object element) {
			// normal type use textCellEditor
			if (propTextCellEditor == null) {
				propTextCellEditor = new TextCellEditor(brokerConfTableViewer.getTable());
				propTextCellEditor.addListener(new ICellEditorListener() {
					public void applyEditorValue() {
						noOp();
					}

					public void cancelEditor() {
						noOp();
					}

					public void editorValueChanged(boolean oldValidState, boolean newValidState) {
						noOp();
					}
				});
			}

			return propTextCellEditor;
		}

		@SuppressWarnings("unchecked")
		protected Object getValue(Object element) {
			if (element instanceof HashMap) {
				HashMap<String, String> valueMap = (HashMap<String, String>) element;
				return defaultString(valueMap.get(String.valueOf(columnIndex)), "");
			}

			return null;
		}

		@SuppressWarnings("unchecked")
		protected void setValue(Object element, Object value) {
			if (element instanceof HashMap && value != null) {
				HashMap<String, String> valueMap = (HashMap<String, String>) element;
				valueMap.put(String.valueOf(columnIndex), (String) value);
				editorPart.setDirty(true);
			}
			brokerConfTableViewer.refresh();
		}
	}

	/**
	 * Document change listener
	 *
	 * @author pangqiren
	 * @version 1.0 - 2011-2-17 created by pangqiren
	 */
	private class DocumentAdpater implements
			IDocumentListener {
		/**
		 * Listen to document about to be changed event
		 *
		 * @param event DocumentEvent
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
			noOp();
		}

		/**
		 * Listen to document changed event
		 *
		 * @param event DocumentEvent
		 */
		public void documentChanged(DocumentEvent event) {
			editorPart.fireChanged();
			// Switch tab also change document data so if switch tab not set dirty to true
			if (!switchTab) {
				editorPart.setDirty(true);
			}
			switchTab = false;
		}
	}

	/**
	 * View sorter
	 *
	 * @author fulei
	 */
	public class BrokerConfTableViewerSorter extends
			ViewerSorter {
		private final int UNIMPORTANTINDEXVALUE = 10000;

		/**
		 * Compares the object for sorting
		 *
		 * @param viewer the Viewer object
		 * @param e1 the object
		 * @param e2 the object
		 * @return the compared value
		 */
		@SuppressWarnings("unchecked")
		public int compare(Viewer viewer, Object e1, Object e2) {
			HashMap<String, String> valueMap1 = null;
			HashMap<String, String> valueMap2 = null;

			if (e1 instanceof HashMap && e2 instanceof HashMap) {
				valueMap1 = (HashMap<String, String>) e1;
				valueMap2 = (HashMap<String, String>) e2;
				int brokerNameIndex1 = getNameIndexOrder(valueMap1.get("0"));
				int brokerNameIndex2 = getNameIndexOrder(valueMap2.get("0"));
				if (brokerNameIndex1 == brokerNameIndex2) {
					return 0;
				}

				return brokerNameIndex1 < brokerNameIndex2 ? -1 : 1;
			}

			return 0;
		}

		/**
		 * Get broker name index
		 *
		 * @param brokerName
		 * @return
		 */
		public int getNameIndexOrder(String brokerName) {
			// BROKERNAMECOLUMNTITLE is the first data
			if (BROKERNAMECOLUMNTITLE.equals(brokerName)) {
				return -1;
			}

			for (int i = 0; i < ConfConstants.brokerParameters.length; i++) {
				String brokerNameInbrokerParameters = ConfConstants.brokerParameters[i][0];
				if (brokerNameInbrokerParameters.equals(brokerName)) {
					return i;
				}
			}

			return UNIMPORTANTINDEXVALUE;
		}
	}

	/**
	 * Validate
	 *
	 * @return error messages
	 */
	public String validate() {
		List<String> nameList = new ArrayList<String>();
		// check duplicate property name
		for (int i = 0, len = confListData.size(); i < len; i++) {
			Map<String, String> valueMap = confListData.get(i);
			String propName = valueMap.get("0");
			if (isBlank(propName)) {
				return Messages.bind(Messages.cubridBrokerConfEditorErrMsg4, i - 1);
			}
			if (nameList.contains(propName)) {
				return Messages.bind(Messages.cubridBrokerConfEditorErrMsg2, propName);
			}
			nameList.add(propName);
		}
		nameList.clear();

		// check duplicate broker name
		Map<String, String> brokerMap = confListData.get(0);
		for (int i = 1, len = confTableViewer.getTable().getColumnCount(); i < len; i++) {
			String brokerName = brokerMap.get(Integer.toString(i));
			if (isBlank(brokerName)) {
				return Messages.bind(Messages.cubridBrokerConfEditorErrMsg3, i);
			}
			if (nameList.contains(brokerName)) {
				return Messages.bind(Messages.cubridBrokerConfEditorErrMsg1, brokerName.trim());
			}
			nameList.add(brokerName);
		}

		return null;
	}

	/**
	 * BrokerConfTableLabelProvider
	 *
	 * @author fulei
	 */
	public class BrokerConfTableLabelProvider extends
			TableLabelProvider implements
			ITableColorProvider,
			ITableFontProvider {
		private Color[] bg = new Color[] { new Color(null, 255, 255, 255),
				new Color(null, 247, 247, 240) };
		private Color[] force = new Color[] { new Color(null, 0, 0, 0), new Color(null, 0, 0, 0) };
		private Object current = null;
		private int currentColor = 0;
		private Font brokerNameRowFont = new Font(Display.getCurrent(), "SansSerif", 10, SWT.BOLD);

		public void dispose() {
			if (bg != null) {
				for (Color color : bg) {
					if (color != null && !color.isDisposed()) {
						color.dispose();
					}
				}
			}
			if (force != null) {
				for (Color color : force) {
					if (color != null && !color.isDisposed()) {
						color.dispose();
					}
				}
			}
			if (brokerNameRowFont != null && !brokerNameRowFont.isDisposed()) {
				brokerNameRowFont.dispose();
			}

			super.dispose();
		}

		@SuppressWarnings("unchecked")
		public Color getForeground(Object element, int columnIndex) {
			if (element instanceof HashMap) {
				HashMap<String, String> valueMap = (HashMap<String, String>) element;
				if (BROKERNAMECOLUMNTITLE.equals(valueMap.get("0"))) {
					return getColor(SWT.COLOR_BLUE);
				}
			}

			return force[currentColor];
		}

		public Color getBackground(Object element, int columnIndex) {
			if (current != element) {
				currentColor = 1 - currentColor;
				current = element;
			}

			return bg[currentColor];
		}

		@SuppressWarnings("unchecked")
		public Font getFont(Object element, int columnIndex) {
			if (element instanceof HashMap) {
				HashMap<String, String> valueMap = (HashMap<String, String>) element;
				if (BROKERNAMECOLUMNTITLE.equals(valueMap.get("0")) && columnIndex == 0) {
					return brokerNameRowFont;
				}
			}

			return null;
		}
	}

	public class TableContentProvider implements
			IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof List)) {
				return new Object[] {};
			}

			List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
			// Do not display the uniform broker property
			for (Map<String, String> valueMap : (ArrayList<Map<String, String>>) inputElement) {
				if (!isUniformBrokerProp(valueMap.get("0"))) {
					newList.add(valueMap);
				}
			}

			return newList.toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			noOp();
		}

		public void dispose() {
			noOp();
		}
	}

	/**
	 * Check whether a prop is a uniform prop
	 *
	 * @param propName
	 * @return
	 */
	public boolean isUniformBrokerProp(String propName) {
		for (String uniformPropName : UNIFORMCONFIG) {
			if (uniformPropName.equalsIgnoreCase(propName)) {
				return true;
			}
		}

		return false;
	}

	public PropEditor getPropEditor() {
		return propEditor;
	}

	public void setPropEditor(PropEditor propEditor) {
		this.propEditor = propEditor;
	}

	public BrokerConfig getCubridBrokerConfig() {
		return brokerConfig;
	}

	public void setBrokerConfig(BrokerConfig cubridBrokerConfig) {
		this.brokerConfig = cubridBrokerConfig;
	}
}
