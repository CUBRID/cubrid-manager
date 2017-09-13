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
package com.cubrid.common.ui.query.editor;

import static com.cubrid.common.core.util.NoOp.noOp;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.QueryTypeCounts;
import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.util.CubridUtil;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.dialog.ShardIdSelectionDialog;
import com.cubrid.common.ui.common.navigator.FavoriteQueryNavigatorView;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtSQLDialog;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.CutAction;
import com.cubrid.common.ui.query.action.FindReplaceAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.action.QueryOpenAction;
import com.cubrid.common.ui.query.action.RedoAction;
import com.cubrid.common.ui.query.action.UndoAction;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.query.control.DatabaseNavigatorMenu;
import com.cubrid.common.ui.query.control.EditorToolBar;
import com.cubrid.common.ui.query.control.MultiDBQueryComposite;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.query.control.QueryResultComposite;
import com.cubrid.common.ui.query.control.SQLEditorComposite;
import com.cubrid.common.ui.query.control.SQLHistoryDetail;
import com.cubrid.common.ui.query.control.SqlParser;
import com.cubrid.common.ui.query.control.tunemode.TuneModeModel;
import com.cubrid.common.ui.query.control.tunemode.TuneModeResultWindow;
import com.cubrid.common.ui.query.dialog.MultiQueryResultDialog;
import com.cubrid.common.ui.query.dialog.MultiShardQueryDialog;
import com.cubrid.common.ui.query.sqlmap.BindParameter;
import com.cubrid.common.ui.query.sqlmap.BindParameter.BindParameterType;
import com.cubrid.common.ui.query.sqlmap.SqlmapNavigatorView;
import com.cubrid.common.ui.query.sqlmap.SqlmapPersistUtil;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.persist.RecentlyUsedSQLContentPersistUtils;
import com.cubrid.common.ui.spi.persist.RecentlyUsedSQLDetailPersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FieldHandlerUtils;
import com.cubrid.common.ui.spi.util.TabContextMenuManager;
import com.cubrid.common.ui.spi.util.UIQueryUtil;
import com.cubrid.cubridmanager.core.common.jdbc.DBConnection;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.jdbc.proxy.driver.CUBRIDCommandType;
import com.cubrid.jdbc.proxy.driver.CUBRIDConnectionProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDOIDProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDPreparedStatementProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDResultSetProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDStatementProxy;
import com.navercorp.dbtools.sqlmap.parser.MapperFile;
import com.navercorp.dbtools.sqlmap.parser.MapperParser;
import com.navercorp.dbtools.sqlmap.parser.MapperParserImpl;
import com.navercorp.dbtools.sqlmap.parser.QueryCondition;

/**
 * This query editor part is responsible to execute sql
 *
 * @author pangqiren 2009-3-2
 */
public class QueryEditorPart extends
		CubridEditorPart implements
		ICopiableFromTable,
		ISaveablePart2,
		ITextListener,
		IDatabaseProvider,
		IInformationWindowNotifier {

	private static final Logger LOGGER = LogUtil.getLogger(QueryEditorPart.class);
	public static final String ID = QueryEditorPart.class.getName();

	public static final String SQL_FORMAT = "com.cubrid.query.command.format";
	public static final String CONTENT_ASSIST_PROPOSALS = "com.cubrid.query.command.contentAssist.proposals";

	/*The composite*/
	private Composite topComposite;
	private CombinedQueryEditorComposite combinedQueryComposite;
	private CTabFolder combinedQueryEditortabFolder;
	/*Connection*/
	private DBConnection connection  = new DBConnection();;
	private CUBRIDResultSetProxy rs;
	public CUBRIDPreparedStatementProxy pStmt;
	/*Editor toolbar*/
	private EditorToolBar qeToolBar;
	private boolean isAutocommit;
	private ToolItem rollbackItem;
	private ToolItem commitItem;
	private ToolItem queryPlanItem;
	private ToolItem tuneModeItem;
	private ToolItem runItem;
	private ToolItem multiRunItem;
	private ToolItem changeShardIdValItem;
	private ToolItem showResultItem;
	private ToolItem autoCommitItem;
	private ToolItem setPstmtParaItem;
	private ToolItem runBatchItem;
	private boolean isActive;
	/*Query thread*/
	private Thread queryThread;
	private int line;
	private boolean isRunning = false;
	private AtomicInteger runningCount = new AtomicInteger(0);

	private QueryExecuter result = null;
	private QueryEditorDNDController dragController;
	// to support the Schema Info Viewer in the Query Explain
	//private String currentSchemaName = null;
	// include oid information
	private boolean isIncludeOidInfo = false;
	protected boolean showStatistical = false;
	private boolean isUpdatePartName = true;

	private String editorTabNameOriginal;

	private Label noticeMessageArea;
	private Date dateOnBeginingTransaction = null;
	private int countQueryOnBeginingTransaction = 0;

	private GridData hiddenGridDataForNotice;
	private GridData shownGridDataForNotice;

	private int shardId = 0;
	private int shardVal = 0;
	private int shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_VAL;

	private boolean isSkip = false;
	private boolean dontTipNext = false;
	private boolean collectExecStats = false;

	private boolean willClose = false;
	private TuneModeResultWindow tuneModeView;

	/*Record the editor index*/
	private static int lastEditorIndex = 1;
	private int currentEditorIndex = 1;
	private int sqlEditorCounter = 1;

	private ToolTip tooltip;

	/*Multi query dialog*/
	private MultiQueryResultDialog multiQueryResultDialog;
	private boolean isCMMode = PerspectiveManager.getInstance().isManagerMode();
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		currentEditorIndex = lastEditorIndex++;
		this.setSite(site);
		this.setInput(input);
		this.setPartName(input.getName());
		this.setTitleToolTip(input.getToolTipText());
		if (input.getImageDescriptor() != null) {
			this.setTitleImage(input.getImageDescriptor().createImage());
		}
		hookRetragetActions();
		this.getSite().getPage().addPartListener(new IPartListener() {

			public void partOpened(IWorkbenchPart part) {
			}

			public void partDeactivated(IWorkbenchPart part) {
				InfoWindowManager.setVisible(false);
			}

			public void partClosed(IWorkbenchPart part) {
				InfoWindowManager.setVisible(false);
			}

			public void partBroughtToTop(IWorkbenchPart part) {
			}

			public void partActivated(IWorkbenchPart part) {
				QueryEditorPart queryEditor = CommonUITool.getActiveQueryEditorPart();
				InfoWindowManager.getInstance().updateContent(queryEditor);
			}
		});
	}

	public void showToolTip(Control baseControl, ToolItem toolItem, String title, String message, int timeoutSec) {
		if (tooltip == null) {
			tooltip = new ToolTip(Display.getCurrent().getActiveShell(), SWT.None);
			tooltip.setAutoHide(true);
		} else {
			tooltip.setVisible(false);
		}

		Point pt = baseControl.toDisplay(topComposite.getLocation());
		pt.x += toolItem.getBounds().x;
		pt.y += toolItem.getBounds().height;
		tooltip.setText(title);
		tooltip.setMessage(message);
		tooltip.setLocation(pt);
		tooltip.setVisible(true);

		if (timeoutSec > 0) {
			final Long eventTime = new Long(System.currentTimeMillis());
			tooltip.setData(eventTime);
			tooltip.getDisplay().timerExec(timeoutSec * 1000, new Runnable() {
				public void run() {
					if (tooltip != null && tooltip.getData() instanceof Long) {
						Long eventTimeTmp = (Long) tooltip.getData();
						if (eventTimeTmp.longValue() == eventTime.longValue()) {
							tooltip.setVisible(false);
						}
					}
				}
			});
		}
	}

	public void showToolTip(Control baseControl, ToolItem toolItem, String title, String message) {
		showToolTip(baseControl, toolItem, title, message, 0);
	}

	public void hideToolTip() {
		if (tooltip != null) {
			tooltip.setVisible(false);
		}
	}

	public QueryExecuter getQueryExecuter() {
		return result;
	}

	public int promptToSaveOnClose() {
		ServerInfo serverInfo = getSelectedServer() == null ? null
				: getSelectedServer().getServerInfo();
		boolean isWithoutPrompt = QueryOptions.getWithoutPromptSave(serverInfo);

		CTabItem[] items = combinedQueryEditortabFolder.getItems();
		if (items.length == 0) {
			willClose = true;
			return ISaveablePart2.NO;
		}
		if (items.length > 1) {
			String msg = Messages.bind(Messages.msgConfirmEditorClose, items.length);
			if (!CommonUITool.openConfirmBox(msg)) {
				return ISaveablePart2.CANCEL;
			}
		}

		if (isWithoutPrompt) {
			willClose = true;
			return ISaveablePart2.NO;
		}
		willClose = true;

		int dirtyCount = 0;
		for (CombinedQueryEditorComposite combinedQueryEditorComposite : getAllCombinedQueryEditorComposite()) {
			if (combinedQueryEditorComposite.isDirty()) {
				dirtyCount++;
			}
		}

		String msg = Messages.bind(Messages.msgConfirmEditorSave, dirtyCount);
		if (dirtyCount > 0 && !CommonUITool.openConfirmBox(msg)) {
			return ISaveablePart2.NO;
		}

		int cancelCount = 0;
		for (CTabItem item : items) {
			if (item instanceof SubQueryEditorTabItem) {
				CombinedQueryEditorComposite combinedQueryEditorComposite = ((SubQueryEditorTabItem) item).getControl();
				try {
					if (combinedQueryEditorComposite.isDirty()) {
						if (combinedQueryEditorComposite.getSqlEditorComp().save()) {
							combinedQueryEditorComposite.dispose();
							item.dispose();
						} else {
							cancelCount++;
						}
					}
				} catch (IOException e) {
					LOGGER.error("", e);
				}
			}
		}

		if (cancelCount > 0) {
			return ISaveablePart2.CANCEL;
		}

		return ISaveablePart2.NO;
	}

	/**
	 * When dispose query editor, interrupt query thread, clear result and query
	 * plan, reset query connection
	 */
	public void dispose() {
		if (isTransaction()) {
			String msg = Messages.bind(Messages.connCloseConfirm,
					new String[] { this.getSelectedDatabase().getLabel() });
			String[] buttons = new String[] { Messages.btnYes, Messages.btnNo };
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
					com.cubrid.common.ui.common.Messages.titleConfirm, null, msg,
					MessageDialog.QUESTION, buttons, 0) {
				protected void buttonPressed(int buttonId) {
					switch (buttonId) {
					case 0:
						try {
							queryAction(QUERY_ACTION.COMMIT);
						} catch (SQLException ex) {
							CommonUITool.openErrorBox(Messages.bind(
									com.cubrid.common.ui.common.Messages.errCommonTip,
									ex.getErrorCode(), ex.getMessage()));
							LOGGER.error("", ex);
						}
						setReturnCode(0);
						close();
						break;
					case 1:
						try {
							queryAction(QUERY_ACTION.ROLLBACK);
						} catch (SQLException ex) {
							CommonUITool.openErrorBox(Messages.bind(
									com.cubrid.common.ui.common.Messages.errCommonTip,
									ex.getErrorCode(), ex.getMessage()));
							LOGGER.error("", ex);
						}
						setReturnCode(1);
						close();
						break;
					default:
						break;
					}
				}
			};
			int returnVal = dialog.open();
			if (returnVal != 0 && returnVal != 1) {
				try {
					queryAction(QUERY_ACTION.ROLLBACK);
				} catch (SQLException ex) {
					LOGGER.error("", ex);
					String errmsg = Messages.bind(
							com.cubrid.common.ui.common.Messages.errCommonTip, ex.getErrorCode(),
							ex.getMessage());
					CommonUITool.openErrorBox(errmsg);
				}
			}
		}

		try {
			if (connection.hasConnection()) {
				queryAction(QUERY_ACTION.CLOSE);
			}
			if (queryThread != null && !queryThread.isInterrupted()) {
				queryThread.interrupt();
				queryThread = null;
			}
			for (CombinedQueryEditorComposite combinedQueryEditorComposite : getAllCombinedQueryEditorComposite()) {
				clearResult(combinedQueryEditorComposite);
				clearPlan(combinedQueryEditorComposite);
				combinedQueryEditorComposite.getSqlEditorComp().release();
			}

		} catch (Exception event) {
			LOGGER.error("", event);
		} finally {
			connection.close();
			connection = null;
		}
		if (result != null) {
			result.dispose();
		}

		super.dispose();
	}

	/**
	 * Set the query editor database connection
	 *
	 * @param database CubridDatabase
	 */
	public void connect(CubridDatabase database) {
		if (database != null && database.getDatabaseInfo() != null) {
			connection.changeDatabaseInfo(database.getDatabaseInfo());
		}
		if (qeToolBar == null) {
			return;
		}
		qeToolBar.setDatabase(database);
		if (CubridDatabase.hasValidDatabaseInfo(database)) {
			DatabaseInfo dbInfo = database.getDatabaseInfo();
			setShardId(dbInfo.getCurrentShardId());
			setShardVal(dbInfo.getCurrentShardVal());
			setShardQueryType(dbInfo.getShardQueryType());
		}
	}

	protected void hookRetragetActions() {
		ActionManager actionManager = ActionManager.getInstance();
		IActionBars bar = this.getEditorSite().getActionBars();

		bar.setGlobalActionHandler(UndoAction.ID, actionManager.getAction(UndoAction.ID));
		bar.setGlobalActionHandler(RedoAction.ID, actionManager.getAction(RedoAction.ID));

		IAction action = actionManager.getAction(CutAction.ID);
		action.setEnabled(true);
		bar.setGlobalActionHandler(CutAction.ID, action);

		IAction copyAction = actionManager.getAction(CopyAction.ID);
		bar.setGlobalActionHandler(CopyAction.ID, copyAction);
		copyAction.setEnabled(true);

		IAction pasteAction = actionManager.getAction(PasteAction.ID);
		bar.setGlobalActionHandler(PasteAction.ID, pasteAction);
		pasteAction.setEnabled(true);

		bar.setGlobalActionHandler(FindReplaceAction.ID,
				actionManager.getAction(FindReplaceAction.ID));
		bar.setGlobalActionHandler(QueryOpenAction.ID, actionManager.getAction(QueryOpenAction.ID));
		bar.updateActionBars();
	}

	public void createPartControl(Composite parent) {
		ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		FillLayout flayout = new FillLayout();
		scrolledComp.setLayout(flayout);

		topComposite = new Composite(scrolledComp, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		topComposite.setLayout(gridLayout);
		topComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		scrolledComp.setContent(topComposite);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);

		dragController = new QueryEditorDNDController(this);

		hiddenGridDataForNotice = new GridData(0, 0);
		shownGridDataForNotice = CommonUITool.createGridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING, 1, 1, -1, -1);
		noticeMessageArea = new Label(topComposite, SWT.None);
		noticeMessageArea.setText("");
		noticeMessageArea.setLayoutData(hiddenGridDataForNotice);

		//create tool bar
		createToolBar();
		//create SQL editor tab folder
		createCombinedQueryEditorCTabFolder();
	}

	public void createToolBar() {
		final Composite toolBarComposite = new Composite(topComposite, SWT.NONE);
		toolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		toolBarComposite.setLayout(gridLayout);
		qeToolBar = new EditorToolBar(toolBarComposite, this);
		fillInToolbar();
	}

	private void createTuneModeView() {
		tuneModeView = new TuneModeResultWindow(this);
		tuneModeView.open();
	}

	public void fillInToolbar() {
		qeToolBar.addDatabaseChangedListener(new Listener() {
			public void handleEvent(Event event) {
				Object data = event.data;
				if (data instanceof CubridDatabase) {
					CubridDatabase database = (CubridDatabase) data;
					if (database == DatabaseNavigatorMenu.NULL_DATABASE) {
						resetJDBCConnection();
					} else {
						initConnection(database);
					}

					ServerInfo serverInfo = database.getServer() == null ? null
							: database.getServer().getServerInfo();
					boolean autoCommit = QueryOptions.getAutoCommit(serverInfo);
					setAutocommit(autoCommit);

				}
			}
		});

		final ToolBar toolBar = qeToolBar;

		// [TOOLS-2425]Support shard broker
		changeShardIdValItem = new ToolItem(toolBar, SWT.PUSH);
		changeShardIdValItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/change_shard_id.png"));
		changeShardIdValItem.setDisabledImage(CommonUIPlugin.getImage("icons/queryeditor/change_shard_id_disabled.png"));
		changeShardIdValItem.setToolTipText(Messages.changeShardId);
		changeShardIdValItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				CubridDatabase cubridDatabase = getSelectedDatabase();
				if (cubridDatabase != null) {
					DatabaseInfo dbInfo = cubridDatabase.getDatabaseInfo();
					if (dbInfo != null && dbInfo.isShard()) {
						ShardIdSelectionDialog dialog = new ShardIdSelectionDialog(
								Display.getDefault().getActiveShell());
						dialog.setDatabaseInfo(dbInfo);
						dialog.setShardId(shardId);
						dialog.setShardVal(shardVal);
						dialog.setShardQueryType(shardQueryType);
						if (dialog.open() == IDialogConstants.OK_ID) {
							shardId = dialog.getShardId();
							shardVal = dialog.getShardVal();
							shardQueryType = dialog.getShardQueryType();
							changeQueryEditorPartNameWithShard();
						}
					}
				}
			}
		});

		CubridDatabase cubridDatabase = getSelectedDatabase();
		if (cubridDatabase == null || CubridDatabase.hasValidDatabaseInfo(cubridDatabase)
				&& cubridDatabase.getDatabaseInfo().isShard()) {
			changeShardIdValItem.setEnabled(false);
		}

		new ToolItem(toolBar, SWT.SEPARATOR);

		runItem = new ToolItem(toolBar, SWT.PUSH);
		runItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_run.png"));
		runItem.setDisabledImage(CommonUIPlugin.getImage("icons/queryeditor/query_run_disabled.png"));
		runItem.setToolTipText(Messages.run + "(F5)");
		runItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				runQuery(false);
			}
		});

		multiRunItem = new ToolItem(toolBar, SWT.PUSH);
		multiRunItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_multi_run.png"));
		multiRunItem.setToolTipText(Messages.runMultiQuery + "(F8)");
		multiRunItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				changeMultiQueryMode();
			}
		});

		queryPlanItem = new ToolItem(toolBar, SWT.PUSH);
		queryPlanItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_execution_plan.png"));
		queryPlanItem.setToolTipText(Messages.queryPlanTip + "(F6)");
		queryPlanItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				runQuery(true);
			}
		});

		ToolItem historyItem = new ToolItem(toolBar, SWT.PUSH);
		historyItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_history.png"));
		historyItem.setToolTipText(Messages.queryHistory + "(F7)");
		historyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				getCombinedQueryComposite().select(2, -1);
			}
		});

		setPstmtParaItem = new ToolItem(toolBar, SWT.PUSH);
		setPstmtParaItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_set_param.png"));
		setPstmtParaItem.setToolTipText(Messages.tipSetPstmt);
		setPstmtParaItem.setEnabled(false);
		setPstmtParaItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
					return;
				}

				PstmtSQLDialog dialog = new PstmtSQLDialog(getSite().getShell(),
						getSelectedDatabase());
				dialog.create();
				dialog.setEditorInput((QueryUnit) getEditorInput());
				String queries = getSelectedText();
				dialog.setSql(queries);
				dialog.open();
			}
		});

		tuneModeItem = new ToolItem(toolBar, SWT.CHECK);
		tuneModeItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/tune_mode.png"));
		tuneModeItem.setToolTipText(Messages.tipExecStat + "(F9)");
		tuneModeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				boolean isTuneMode = tuneModeItem.getSelection();
				changeCollectExecStats(isTuneMode);
				if (isTuneMode) {
					showToolTip(qeToolBar, tuneModeItem, Messages.ttQeToolbarTuneModeTitle,
							Messages.ttQeToolbarTuneModeMSg);
				} else {
					hideToolTip();
				}
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		final ToolItem addEditorItem = new ToolItem(toolBar, SWT.PUSH);
		addEditorItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/tab_item_add.png"));
		addEditorItem.setToolTipText(Messages.queryEditorAddTabItemTooltip + "(Ctrl+Shift+T)");
		addEditorItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				addEditorTab();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		runBatchItem = new ToolItem(toolBar, SWT.PUSH);
		runBatchItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/run_batch_sql.png"));
		runBatchItem.setToolTipText(Messages.batchRun);
		runBatchItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				try {
					// FIXME extract utility
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart view = page.findView(FavoriteQueryNavigatorView.ID);
					if (view != null) {
						page.hideView(view);
					} else {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
								FavoriteQueryNavigatorView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
					}
				} catch (PartInitException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem itemFormatterr = new ToolItem(toolBar, SWT.PUSH);
		itemFormatterr.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_format.png"));
		itemFormatterr.setToolTipText(Messages.formatTip + "(Ctrl+Shift+F)");
		itemFormatterr.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				combinedQueryComposite.getSqlEditorComp().format();
			}
		});

		ToolItem itemIndent = new ToolItem(toolBar, SWT.PUSH);
		itemIndent.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_indent_add.png"));
		itemIndent.setToolTipText(Messages.indentTip + "(Tab)");
		itemIndent.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
					return;
				}
				combinedQueryComposite.getSqlEditorComp().indent();
			}
		});

		ToolItem itemUnindent = new ToolItem(toolBar, SWT.PUSH);
		itemUnindent.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_indent_delete.png"));
		itemUnindent.setToolTipText(Messages.unIndentTip + "(Shift+Tab)");
		itemUnindent.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
					return;
				}
				combinedQueryComposite.getSqlEditorComp().unindent();
			}
		});

		ToolItem clearItem = new ToolItem(toolBar, SWT.PUSH);
		clearItem.setImage(CommonUIPlugin.getImage("/icons/queryeditor/clear_sql.png"));
		clearItem.setToolTipText(Messages.clear);
		clearItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
					return;
				}

				if (!CommonUITool.openConfirmBox(Messages.msgClear)) {
					return;
				}

				combinedQueryComposite.getSqlEditorComp().setQueries("");
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem itemComment = new ToolItem(toolBar, SWT.PUSH);
		itemComment.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_comment_add.png"));
		itemComment.setToolTipText(Messages.commentTip + "(Ctrl+/)");
		itemComment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
					return;
				}
				combinedQueryComposite.getSqlEditorComp().comment();
			}
		});

		ToolItem itemUncomment = new ToolItem(toolBar, SWT.PUSH);
		itemUncomment.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_comment_delete.png"));
		itemUncomment.setToolTipText(Messages.unCommentTip + "(Ctrl+/)");
		itemUncomment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
					return;
				}
				combinedQueryComposite.getSqlEditorComp().uncomment();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		commitItem = new ToolItem(toolBar, SWT.PUSH);
		commitItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_commit.png"));
		commitItem.setDisabledImage(CommonUIPlugin.getImage("icons/queryeditor/query_commit_disabled.png"));
		commitItem.setToolTipText(Messages.commit);
		commitItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				commit();
			}
		});
		commitItem.setEnabled(false);

		rollbackItem = new ToolItem(toolBar, SWT.PUSH);
		rollbackItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_rollback.png"));
		rollbackItem.setDisabledImage(CommonUIPlugin.getImage("icons/queryeditor/query_rollback_disabled.png"));
		rollbackItem.setToolTipText(Messages.rollback);
		rollbackItem.setEnabled(false);
		rollbackItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				rollback();
			}
		});

		autoCommitItem = new ToolItem(toolBar, SWT.CHECK);
		autoCommitItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_auto_false.png"));
		autoCommitItem.setToolTipText(Messages.autoCommit);
		autoCommitItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				if (checkActive()) {
					return;
				}
				setAutocommit(autoCommitItem.getSelection());
				if (connection != null) {
					try {
						connection.setAutoCommit(isAutocommit());
					} catch (SQLException e) {
					}
				}
			}
		});
		setAutocommit(true);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem openItem = new ToolItem(toolBar, SWT.PUSH);
		openItem.setImage(CommonUIPlugin.getImage("/icons/queryeditor/file_open.png"));
		openItem.setToolTipText(Messages.open);
		openItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				doOpen();
			}
		});

		ToolItem saveItem = new ToolItem(toolBar, SWT.PUSH);
		saveItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_save.png"));
		saveItem.setToolTipText(Messages.save);
		saveItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				doSave(new NullProgressMonitor());
			}
		});

		ToolItem saveAsItem = new ToolItem(toolBar, SWT.PUSH);
		saveAsItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/file_saveas.png"));
		saveAsItem.setToolTipText(Messages.saveAs);
		saveAsItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				doSaveAs();
			}
		});

		//Fill in the show or hide result pane tool item
		new ToolItem(toolBar, SWT.SEPARATOR);
		showResultItem = new ToolItem(toolBar, SWT.PUSH);
		showResultItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png"));
		showResultItem.setToolTipText(Messages.tooltip_qedit_result_show_hide);
		showResultItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				hideToolTip();
				CTabFolder resultTabFolder = combinedQueryComposite.getResultTabFolder();
				SashForm topSash = combinedQueryComposite.getTopSash();
				if (resultTabFolder.getMinimized()) {
					resultTabFolder.setMinimized(false);
					resultTabFolder.setMaximized(false);
					topSash.setMaximizedControl(null);
					showResultItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_down.png"));
					topSash.layout(true);
				} else if (!resultTabFolder.getMaximized() && !resultTabFolder.getMinimized()) {
					resultTabFolder.setMinimized(true);
					topSash.setMaximizedControl(combinedQueryComposite.getSqlEditorComp().getParent());
					showResultItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/qe_panel_up.png"));
					topSash.layout(true);
				}
			}
		});

		topComposite.pack();
		packToolBar();
	}

	/**
	 * create editor CTabFolder all sql editor tab will add to this
	 */
	public void createCombinedQueryEditorCTabFolder() {
		combinedQueryEditortabFolder = new CTabFolder(topComposite, SWT.TOP);
		combinedQueryEditortabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		combinedQueryEditortabFolder.setUnselectedImageVisible(true);
		combinedQueryEditortabFolder.setUnselectedCloseVisible(false);
		combinedQueryEditortabFolder.setBorderVisible(true);
		combinedQueryEditortabFolder.setSimple(false);
		combinedQueryEditortabFolder.setSelectionBackground(CombinedQueryEditorComposite.BACK_COLOR);
		combinedQueryEditortabFolder.setSelectionForeground(ResourceManager.getColor(SWT.COLOR_BLACK));
		combinedQueryEditortabFolder.setMinimizeVisible(false);
		combinedQueryEditortabFolder.setMaximizeVisible(false);
		combinedQueryEditortabFolder.setTabHeight(22);
		combinedQueryEditortabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = combinedQueryEditortabFolder.getSelection();
				if (item instanceof SubQueryEditorTabItem) {
					SubQueryEditorTabItem queryResultTabItem = (SubQueryEditorTabItem) item;
					combinedQueryComposite = queryResultTabItem.getControl();
					combinedQueryComposite.refreshEditorComposite();
					InfoWindowManager.getInstance().updateContent(QueryEditorPart.this);
				}
			}
		});

		TabContextMenuManager ctxmenu = new TabContextMenuManager(combinedQueryEditortabFolder);
		ctxmenu.createContextMenu();

		//add a default SQL Tab item
		addEditorTab();
	}

	public void updateTabName(SubQueryEditorTabItem tabItem, boolean dirty) {
		String tabName = (dirty ? "*" : "") + Messages.queryEditorTabItemName + " "
				+ tabItem.getTabIndex();
		tabItem.setText(tabName);
	}

	public void updateTabName(SubQueryEditorTabItem tabItem, String tabName, boolean dirty) {
		tabItem.setText((dirty ? "*" : "") + tabName);
	}

	public CombinedQueryEditorComposite addEditorTab() {
		final SubQueryEditorTabItem subQueryEditorTabItem = new SubQueryEditorTabItem(combinedQueryEditortabFolder, SWT.NONE);
		subQueryEditorTabItem.setTabIndex(sqlEditorCounter ++);

		updateTabName(subQueryEditorTabItem, false);
		subQueryEditorTabItem.setToolTipText(Messages.queryEditorTabItemTooltip);
		subQueryEditorTabItem.setShowClose(true);

		// When clicking the close button on a tab
		subQueryEditorTabItem.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				String brokerName = getDatabaseInfo().getBrokerName();
				getServerInfo().releaseCasCount(brokerName);

				if (!willClose && combinedQueryEditortabFolder.getItemCount() == 0) {
					sqlEditorCounter = 1;
					addEditorTab();
				}
			}
		});
		combinedQueryComposite = new CombinedQueryEditorComposite(combinedQueryEditortabFolder,
				SWT.None, this, subQueryEditorTabItem);
		subQueryEditorTabItem.setControl(combinedQueryComposite);
		combinedQueryComposite.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1,
				-1, -1));
		combinedQueryComposite.setTopSashWidth();
		combinedQueryComposite.refreshEditorComposite();
		dragController.registerDropTarget(combinedQueryComposite);
		combinedQueryEditortabFolder.setSelection(subQueryEditorTabItem);
		if (combinedQueryEditortabFolder.getItemCount() > 1) {
			InfoWindowManager.getInstance().updateContent(QueryEditorPart.this);
		}

		refreshQueryOptions();
		return combinedQueryComposite;
	}

	public boolean isAutocommit() {
		return isAutocommit;
	}

	public void setAutocommit(boolean autocommit) {
		isAutocommit = autocommit;
		if (autoCommitItem == null) {
			return;
		}

		autoCommitItem.setSelection(autocommit);
		if (isAutocommit) {
			autoCommitItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_auto_active.png"));
			autoCommitItem.setToolTipText(Messages.autoCommit + " : " + Messages.autoCommitLabelOn);
		} else {
			autoCommitItem.setImage(CommonUIPlugin.getImage("icons/queryeditor/query_auto_false.png"));
			autoCommitItem.setToolTipText(Messages.autoCommit + " : " + Messages.autoCommitLabelOff);
		}
		setHaveActiveTransaction(false);
		try {
			if (connection.hasConnection()) {
				queryAction(QUERY_ACTION.AUTOCOMMIT, isAutocommit);
			}
		} catch (SQLException e) {
			String msg = Messages.cantChangeStatus + StringUtil.NEWLINE + e.getErrorCode()
					+ StringUtil.NEWLINE + Messages.errorHead + e.getMessage();
			CommonUITool.openErrorBox(msg);
			LOGGER.error("", e);
		}
	}

	/**
	 * Check whether the activated
	 *
	 * @return boolean
	 */
	private boolean checkActive() {
		if (!isAutocommit && isActive) {
			MessageBox mb = new MessageBox(getSite().getShell(), SWT.OK | SWT.ICON_WARNING);
			mb.setText(Messages.info);
			mb.setMessage(Messages.transActive);
			mb.open();
			autoCommitItem.setSelection(false);
			return true;
		}

		return false;
	}

	/**
	 * Is there any transaction not committed ?
	 *
	 * @return boolean
	 */
	public boolean isTransaction() {
		return !isAutocommit && isActive;
	}

	/**
	 * Return current editing query
	 *
	 * @return
	 */
	public String getCurrentQuery() {
		if (combinedQueryComposite == null || combinedQueryComposite.getSqlEditorComp() == null) {
			return null;
		}
		return combinedQueryComposite.getSqlEditorComp().getText().getText();
	}

	/**
	 * Set and run query
	 *
	 * @param query
	 * @param isAppend
	 * @param isRun
	 * @param isOnlyQueryPlan
	 */
	public void setQuery(final String query, final boolean isAppend, final boolean isRun,
			final boolean isOnlyQueryPlan) {
		/* Update queries in the UI JOB */
		UIJob job = new UIJob(Messages.settingQueries) {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (isAvilableEditor()) {
					if (isAppend) {
						combinedQueryComposite.getSqlEditorComp().getText().append(query);
						combinedQueryComposite.getSqlEditorComp().gotoButtom();
					} else {
						combinedQueryComposite.getSqlEditorComp().setQueries(query);
					}

					if (isRun) {
						runQuery(false);
					} else if (isOnlyQueryPlan) {
						runQuery(true);
					}
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(UIJob.SHORT);
		job.schedule();
	}

	/**
	 * Set and run queries with prepared parameters
	 *
	 * @param displayQueries
	 * @param runQueries
	 * @param rowParameterList
	 * @param isAppend
	 * @param isRun
	 * @param isOnlyQueryPlan
	 */
	public void setQuery(final String displayQueries, final String runQueries,
			final List<List<PstmtParameter>> rowParameterList, final boolean isAppend,
			final boolean isRun, final boolean isOnlyQueryPlan) {
		// Update queries in the UI JOB
		UIJob job = new UIJob(Messages.settingQueries) {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (isAvilableEditor()) {
					if (isAppend) {
						String allQuery = getAllQueries();
						int start = allQuery.indexOf(displayQueries);
						if (start == -1) {
							combinedQueryComposite.getSqlEditorComp().getText().append(
									displayQueries);
						} else {
							setSelection(start, start + displayQueries.length());
						}
					} else {
						combinedQueryComposite.getSqlEditorComp().setQueries(displayQueries);
					}
					if (isRun) {
						runQuery(false, runQueries, rowParameterList);
					} else if (isOnlyQueryPlan) {
						runQuery(true, runQueries, rowParameterList);
					}
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(UIJob.SHORT);
		job.schedule();
	}

	public void newQueryTab(final String query, final boolean run) {
		UIJob job = new UIJob(Messages.settingQueries) {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (isAvilableEditor()) {
					String sql = combinedQueryComposite.getSqlEditorComp().getText().getText();
					if (!StringUtil.isEmpty(sql)) {
						addEditorTab();
					}
					combinedQueryComposite.getSqlEditorComp().setQueries(query);
					if (run) {
						runQueryInCursorLine();
					}
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(UIJob.SHORT);
		job.schedule();
	}

	private boolean isAvilableEditor() {
		return combinedQueryComposite != null && combinedQueryComposite.getSqlEditorComp() != null
				&& !combinedQueryComposite.getSqlEditorComp().isDisposed();
	}

	private boolean isAvailablePlanResult() {
		return combinedQueryComposite != null
				&& combinedQueryComposite.getQueryPlanResultComp() != null
				&& !combinedQueryComposite.getQueryPlanResultComp().isDisposed();
	}

	private boolean isAvailableQueryResult() {
		return combinedQueryComposite != null
				&& combinedQueryComposite.getQueryResultComp() != null
				&& !combinedQueryComposite.getQueryResultComp().isDisposed();
	}

	public void setSelection(int startPos, int endPos) {
		if (startPos >= 0 && endPos > startPos) {
			getSqlTextEditor().setSelection(startPos);
			getSqlTextEditor().setFocus();
		}
	}

	/**
	 * Parse the selected query. If it has a sqlmap fashion syntax,
	 * it will show the sqlmap parameter editor with parsed query on the right side.
	 */
	public void parseSqlmapQuery() {
		runQuery(false, true);
	}
	
	/**
	 * Execute all the selected SQL script on editor, if not, execute all the
	 * script on editor
	 *
	 * @param isOnlyQueryPlan boolean
	 */
	public void runQuery(boolean isOnlyQueryPlan) {
		runQuery(isOnlyQueryPlan, false);
	}
	
	/**
	 * Execute all the selected SQL script on editor, if not, execute all the
	 * script on editor
	 *
	 * @param isOnlyQueryPlan boolean
	 * @parma isSqlmapQuery boolean whether or not it should handled as a sqlmap query
	 */
	private void runQuery(boolean isOnlyQueryPlan, boolean isSqlmapQuery) {
		final DBConnection dbConnection = getConnection();
		if (dbConnection != null
				&& dbConnection.isAutoCommit()
				&& dbConnection.getConnectionQuietly() != null
				&& dbConnection.isExpiredConnection()
				&& !dbConnection.testConnectionAlived()) {
			if (!CommonUITool.openConfirmBox(Messages.msgConnectionTimeOut)) {
				return;
			}

			dbConnection.close();

			try {
				dbConnection.checkAndConnect();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
				String msg = Messages.bind(Messages.msgConnectionError, e.getMessage());
				CommonUITool.openErrorBox(msg);
			}
		}

		if (!isOnlyQueryPlan && isAvailableQueryResult()) {
			combinedQueryComposite.getQueryResultComp().setSelection();
		} else if (isOnlyQueryPlan && isAvailablePlanResult()) {
			combinedQueryComposite.getQueryPlanResultComp().setSelection();
		}

		if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
			return;
		}

		String queries = combinedQueryComposite.getSqlEditorComp().getSelectedQueries();
		runQuery(isOnlyQueryPlan, isSqlmapQuery, queries, null);
	}

	/**
	 * Execute all the selected SQL script on editor, if not, execute all the
	 * script on editor
	 *
	 * @param isOnlyQueryPlan boolean
	 * @param queries String
	 * @param rowParameterList List<List<PstmtParameter>>
	 */
	private void runQuery(boolean isOnlyQueryPlan, String queries,
			List<List<PstmtParameter>> rowParameterList) {
		runQuery(isOnlyQueryPlan, false, queries, rowParameterList);
	}
	
	/**
	 * Execute all the selected SQL script on editor, if not, execute all the
	 * script on editor
	 *
	 * @param isOnlyQueryPlan boolean
	 * @param isSqlmapQuery boolean whether or not it needs parsing a sqlmap query
	 * @param queries String
	 * @param rowParameterList List<List<PstmtParameter>>
	 */
	private void runQuery(boolean isOnlyQueryPlan, boolean isSqlmapQuery, String queries,
			List<List<PstmtParameter>> rowParameterList) { // FIXME move this logic to core module
		if (!isConnected()) {
			CommonUITool.openErrorBox(Messages.qedit_tip_run_query);
			return;
		}
		if (StringUtil.isEmpty(queries)) {
			return;
		}

		try {
			connection.getConnection(true);
		} catch (SQLException e) {
			CommonUITool.openErrorBox(e.getLocalizedMessage());
			return;
		}

		try {
			connection.setAutoCommit(isAutocommit);
		} catch (SQLException e) {
			CommonUITool.openErrorBox(e.getLocalizedMessage());
			return;
		}
		if (!connection.hasConnection()) {
			return;
		}

		MapperParser mapperParser = null;
		boolean isXmlQueries = isSqlmapQuery && QueryUtil.isXml(queries);
		if (isXmlQueries) {
			try {
				mapperParser = new MapperParserImpl();
			} catch (Exception ignored) {
				isXmlQueries = false;
			}
		}

		if (isXmlQueries) {
			boolean isNotFirstDisplayed = false;
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart view = page.findView(SqlmapNavigatorView.ID);
				if (view == null) {
					showToolTip(qeToolBar, runItem, Messages.titleSqlmapSupports,
							Messages.msgSqlmapSupports, 3);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
							SqlmapNavigatorView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
					isNotFirstDisplayed = true;
				}
			} catch (PartInitException e) {
				LOGGER.error(e.getMessage(), e);
			}

			// TODO #664 find query id on current position of xml content
			String queryId = QueryUtil.findNearbyQueryId(queries, queries.length() / 2); // TODO #664 position
			MapperFile mapperFile = null;
			try {
				mapperFile = mapperParser.parse(getAllQueries());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				CommonUITool.openErrorBox(e.getLocalizedMessage()); // TODO #664 message
			}

			List<String> queryIdList = mapperFile.getQueryIdList();
			SqlmapNavigatorView.getInstance().updateQueryIdList(queryIdList, queryId);

			List<QueryCondition> condList = mapperFile.getConditionList(queryId);
			boolean isXmlUpdated = SqlmapPersistUtil.getInstance().isChanged(queryId, condList);
			if (isXmlUpdated) {
				SqlmapPersistUtil.getInstance().setConditions(queryId, condList);
			}

			String generatedQuery = SqlmapPersistUtil.getInstance().generateQuery(mapperFile,
					queryId);

			String rawQuery = mapperFile.generateRawQuery(queryId);
			if (rawQuery == null) {
				showToolTip(qeToolBar, runItem, Messages.titleSqlmapUpdated,
						"Cannot find a query by the query id.\nIt seems to be commented out a query.", 3); // TODO i18n
			}
			List<String> bindParameters = QueryUtil.extractBindParameters(rawQuery);
			for (String parameterOfSqlmap : bindParameters) {
				String parameterName = QueryUtil.extractBindParameterName(parameterOfSqlmap);
				SqlmapPersistUtil.getInstance().addOrModifyBindParameter(queryId, parameterName, null,
						BindParameterType.STRING.name());

				BindParameter bindParameter = SqlmapPersistUtil.getInstance().getBindParameter(queryId,
						parameterName);
				if (bindParameter != null) {
					String value = bindParameter.getType().wrap(bindParameter.getValue());
					generatedQuery = generatedQuery.replace(parameterOfSqlmap, value);
				}
			}

			SqlmapNavigatorView.getInstance().refreshView();

			if (!isNotFirstDisplayed) {
				showToolTip(qeToolBar, runItem, Messages.titleSqlmapUpdated,
						Messages.msgSqlmapUpdated, 1);
			}
			return;
		}

		Vector<String> queryVector = QueryUtil.queriesToQuery(queries);

		if (GeneralPreference.isShowAlertModifiedQueryOnAutoCommit() && isAutocommit()
				&& !isOnlyQueryPlan && checkModifiedQueriesAndAlert(queryVector)) {
			return;
		}

		if (isOnlyQueryPlan) {
			if (!queryPlanItem.isEnabled()) {
				return;
			}
		} else {
			if (!runItem.isEnabled()) {
				return;
			}
		}

		isRunning = true;
		queryPlanItem.setEnabled(false);
		autoCommitItem.setEnabled(false);
		setPstmtParaItem.setEnabled(false);

		if (isOnlyQueryPlan) {
			runQueryPlanOnly(queryVector);
		} else {
			runQuery(queryVector, rowParameterList, connection);
		}
	}

	private boolean checkModifiedQueriesAndAlert(Vector<String> queryVector) {
		QueryTypeCounts typeCounts = QueryUtil.analyzeQueryTypes(queryVector);
		if (typeCounts.existModifyingQuery()) {
			StringBuilder detailMsg = new StringBuilder();
			if (typeCounts.getInserts() > 0) {
				detailMsg.append("\n > INSERT : ").append(typeCounts.getInserts()).append(
						Messages.msgQueryEach);
			}
			if (typeCounts.getUpdates() > 0) {
				detailMsg.append("\n > UPDATE : ").append(typeCounts.getUpdates()).append(
						Messages.msgQueryEach);
			}
			if (typeCounts.getDeletes() > 0) {
				detailMsg.append("\n > DELETE : ").append(typeCounts.getDeletes()).append(
						Messages.msgQueryEach);
			}
			if (typeCounts.getCreates() > 0) {
				detailMsg.append("\n > CREATE : ").append(typeCounts.getCreates()).append(
						Messages.msgQueryEach);
			}
			if (typeCounts.getAlters() > 0) {
				detailMsg.append("\n > ALTER : ").append(typeCounts.getAlters()).append(
						Messages.msgQueryEach);
			}
			if (typeCounts.getDrops() > 0) {
				detailMsg.append("\n > DROP : ").append(typeCounts.getDrops()).append(
						Messages.msgQueryEach);
			}

			String msg = Messages.warnModifiableQueryOnAutoCommitMode + detailMsg.toString();
			if (!CommonUITool.openConfirmBox(msg)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Execute the selected SQL script
	 *
	 * @param queries String
	 * @param rowParameterList List<List<PstmtParameter>>
	 */
	private void runQuery(final Vector<String> queries,
			List<List<PstmtParameter>> rowParameterList, DBConnection con) {
		clearResult(combinedQueryComposite);
		makeProgressBar();
		runningCount.incrementAndGet();
		
		if (result != null) {
			result.initBeforeRunQuery();
		}

		queryThread = new Thread(new QueryThread(queries, this, rowParameterList, con,
				qeToolBar.getSelectedDb()));
		queryThread.setName(Messages.proRunQuery);
		queryThread.start();
	}

	public void changeMultiQueryMode() {
		if (combinedQueryComposite.getSqlEditorComp().isDisposed()) {
			return;
		}

		combinedQueryComposite.getResultTabFolder().setSelection(3);
		showToolTip(qeToolBar, multiRunItem, Messages.ttMultiQueryTitle,
				Messages.qedit_multiDBQueryComp_run_err);
	}

	public void runMultiQuery() {
		String queries = combinedQueryComposite.getSqlEditorComp().getSelectedQueries();
		MultiDBQueryComposite multiDbQueryComposite = combinedQueryComposite.getMultiDBQueryComp();
		List<CubridDatabase> queryDatabaseList = multiDbQueryComposite.getQueryDatabaseList();
		combinedQueryComposite.getResultTabFolder().setSelection(3);

		int selectedDatabases = queryDatabaseList.size();
		Vector<String> queryVector = QueryUtil.queriesToQuery(queries);
		int countOfQueries = queryVector.size();

		// [TOOLS-2425]Support shard broker
		if (selectedDatabases < 2) {
			if (countOfQueries < 2) {
				if (selectedDatabases == 1 && queryDatabaseList.get(0).getDatabaseInfo().isShard()) {
					runMultiShardQuery(queries);
				} else {
					showToolTip(multiDbQueryComposite, multiDbQueryComposite.getRunItem(),
							Messages.ttMultiQueryTitle, Messages.qedit_multiDBQueryComp_run_err);
				}
			} else {
				// one database multiple SQL query
				runMultiSQLQuery(queries);
			}
		} else {
			runMultiDBQuery(queries, queryDatabaseList);
		}
	}

	private void runMultiShardQuery(String query) {
		Pattern pattern = Pattern.compile(QueryUtil.REGEX_PATTERN_SHARD_ID,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(query);
		String newQuery = matcher.replaceAll("");

		MultiShardQueryDialog dialog = new MultiShardQueryDialog(
				Display.getDefault().getActiveShell());
		if (dialog.open() == IDialogConstants.OK_ID) {
			int minShardId = dialog.getMinShardId();
			int maxShardId = dialog.getMaxShardId();
			StringBuilder queryBuf = new StringBuilder();
			for (int i = minShardId; i <= maxShardId; i++) {
				queryBuf.append(QueryUtil.wrapShardQueryWithId(newQuery, i));
			}
			runMultiSQLQuery(queryBuf.toString());
		}
	}

	/**
	 * Run queries on multi db
	 *
	 * @param queries
	 * @param queryDatabaseList
	 */
	public void runMultiDBQuery(String queries, List<CubridDatabase> queryDatabaseList) {
		if (StringUtil.isEmpty(queries)) {
			CommonUITool.openErrorBox(Messages.msgInputSqlText);
			return;
		}
		if (getSelectedDatabase() == null || getSelectedDatabase().getServer() == null) {
			CommonUITool.openErrorBox(Messages.qedit_tip_run_query);
			return;
		}

		if (!GeneralPreference.isAlertMultiQueryAlwaysConfirm()) {
			int result = CommonUITool.openConfirmBoxWithAlways(Messages.qedit_multiDBQueryComp_runConfirm);
			if (result == CommonUITool.CONFIRM_BOX_RESULT_ALWAYS) {
				GeneralPreference.setAlertMultiQueryAlwaysConfirm(true);
			} else if (result == CommonUITool.CONFIRM_BOX_RESULT_CANCEL) {
				return;
			}
		}

		if (multiQueryResultDialog == null || multiQueryResultDialog.isDisposed()) {
			multiQueryResultDialog = new MultiQueryResultDialog(this.getSite().getShell(), this);
		}

		multiQueryResultDialog.open();
		multiQueryResultDialog.runQueries(queryDatabaseList, queries);
	}

	/**
	 * Run queries on current db
	 *
	 * @param queries
	 */
	public void runMultiSQLQuery(String queries) {
		if (queries == null || queries.length() == 0) {
			CommonUITool.openErrorBox(Messages.msgInputSqlText);
			return;
		}

		if (!GeneralPreference.isAlertMultiQueryAlwaysConfirm()) {
			int result = CommonUITool.openConfirmBoxWithAlways(Messages.qedit_multiDBQueryComp_runConfirm);
			if (result == CommonUITool.CONFIRM_BOX_RESULT_ALWAYS) {
				GeneralPreference.setAlertMultiQueryAlwaysConfirm(true);
			} else if (result == CommonUITool.CONFIRM_BOX_RESULT_CANCEL) {
				return;
			}
		}

		if (multiQueryResultDialog == null || multiQueryResultDialog.isDisposed()) {
			multiQueryResultDialog = new MultiQueryResultDialog(this.getSite().getShell(), this);
		}

		multiQueryResultDialog.open();
		multiQueryResultDialog.runQueries(qeToolBar.getSelectedDb(), queries);
	}

	/**
	 * Run the the SQL which cursor position
	 */
	public void runQueryInCursorLine() {
		if (!isConnected()) {
			CommonUITool.openErrorBox(Messages.qedit_tip_run_query);
			return;
		}

		String queries = combinedQueryComposite.getSqlEditorComp().getSelectedQueries();
		if (StringUtil.isEmpty(queries)) {
			return;
		}

		try {
			connection.getConnection(true);
		} catch (SQLException e) {
			CommonUITool.openErrorBox(e.getLocalizedMessage());
			return;
		}
		if (!connection.hasConnection()) {
			return;
		}

		// TODO #644 extend to parse xml queries on the cursor location
		SQLDocument document = combinedQueryComposite.getSqlEditorComp().getDocument();
		StyledText sqlText = getSqlTextEditor();

		// get cursor position offset
		int cursorOffset = sqlText.getCaretOffset();
		// get cursor position line
		int lineNumber = sqlText.getLineAtOffset(cursorOffset);

		// get cursor position line first char offset
		int firstCharOffset = sqlText.getOffsetAtLine(lineNumber);
		if (firstCharOffset > 0) {
			firstCharOffset = firstCharOffset - 1;
		}

		int sqlStartPos = getQuerySQLStartPos(document, queries, firstCharOffset);
		String query = getQuery(queries, sqlStartPos);
		sqlText.setSelectionRange(sqlStartPos, query.length());

		runQuery(false, query.trim(), null);
	}

	/**
	 * Run the the query plan which cursor position
	 */
	public void runQueryPlanInCursorLine() {
		if (!isConnected()) {
			CommonUITool.openErrorBox(Messages.qedit_tip_run_query);
			return;
		}

		String queries = combinedQueryComposite.getSqlEditorComp().getSelectedQueries();
		if (queries == null || queries.trim().equals("")) {
			return;
		}

		try {
			connection.getConnection(true);
		} catch (SQLException e) {
			CommonUITool.openErrorBox(e.getLocalizedMessage());
			return;
		}
		if (!connection.hasConnection()) {
			return;
		}

		SQLDocument document = combinedQueryComposite.getSqlEditorComp().getDocument();
		StyledText sqlText = getSqlTextEditor();

		// get cursor position offset
		int cursorOffset = sqlText.getCaretOffset();

		// get cursor position line
		int lineNumber = sqlText.getLineAtOffset(cursorOffset);

		// get cursor position line first char offset
		int firstCharOffset = sqlText.getOffsetAtLine(lineNumber);
		if (firstCharOffset > 0) {
			firstCharOffset = firstCharOffset - 1;
		}
		int sqlStartPos = getQuerySQLStartPos(document, queries, firstCharOffset);

		String query = QueryUtil.isXml(queries) ? QueryUtil.findNearbyQuery(queries, sqlStartPos)
				: getQuery(queries, sqlStartPos);
		sqlText.setSelectionRange(sqlStartPos, query.length());

		runQuery(true, query.trim(), null);
	}

	/**
	 * Get query SQL start position
	 *
	 * @param document PersistentDocument
	 * @param queries String
	 * @param cursorPosOffset int
	 * @return int
	 */
	private static int getQuerySQLStartPos(SQLDocument document, String queries, int cursorPosOffset) {
		// TODO #664 check whether xml queries?
		char[] buffer = queries.toCharArray();
		IDocumentPartitioner docPartitioner = document.getDocumentPartitioner(ISQLPartitions.SQL_PARTITIONING);
		int start = cursorPosOffset;
		for (int i = start; i >= 0; i--) {
			if (buffer[i] == ';') {
				String contentType = docPartitioner == null ? IDocument.DEFAULT_CONTENT_TYPE
						: docPartitioner.getContentType(i);
				boolean isBreak = !ISQLPartitions.SQL_MULTI_LINE_COMMENT.equals(contentType)
						&& !ISQLPartitions.SQL_SINGLE_LINE_COMMENT.equals(contentType)
						&& !ISQLPartitions.SQL_STRING.equals(contentType);
				if (isBreak) {
					break;
				}
			}
			start--;
		}
		start++;
		for (int i = start; i < buffer.length; i++) {
			if (buffer[i] == '\n' || buffer[i] == '\r' || buffer[i] == ' ') {
				start++;
			} else {
				break;
			}
		}

		return start;
	}

	/**
	 * Make the progress bar
	 */
	private void makeProgressBar() {
		Runnable runable = new Runnable() {
			public void run() {
				if (queryThread != null) {
					if (result != null) {
						result.dispose();
					}
					try {
						if (pStmt != null) {
							pStmt.cancel();
						}
					} catch (SQLException e1) {
						LOGGER.error("Query Cancel:", e1);
					}
					QueryUtil.freeQuery(pStmt, rs);
					pStmt = null;
					rs = null;
					queryThread = null;
				}
			}
		};
		if (combinedQueryComposite.getQueryResultComp().isDisposed()) {
			combinedQueryComposite.newQueryResultComp();
		}
		combinedQueryComposite.getQueryResultComp().makeProgressBar(runable);
	}

	/**
	 * Clear the result content.
	 */
	private void clearResult(CombinedQueryEditorComposite combinedQueryComposite) {
		if (combinedQueryComposite != null && combinedQueryComposite.getQueryResultComp() != null) {
			combinedQueryComposite.getQueryResultComp().disposeAllResult();
		}
	}

	/**
	 * Dispose plan tabs
	 */
	private void clearPlan(CombinedQueryEditorComposite combinedQueryComposite) {
		if (isAvailableQueryResult()) {
			combinedQueryComposite.getQueryPlanResultComp().clearPlanTabs();
		}
	}

	/**
	 * Get statement
	 *
	 * @param conn Connection
	 * @param isSecond check the connection time out
	 * @return CUBRIDStatementProxy
	 * @throws SQLException The exception
	 */
	private CUBRIDStatementProxy getStatement(Connection conn, boolean isSecond) throws SQLException {
		try {
			return (CUBRIDStatementProxy) conn.createStatement();
		} catch (SQLException e) {
			if (!isSecond && (e.getErrorCode() == -2003 || e.getErrorCode() == -1003)) {
				return getStatement(conn, true);
			} else {
				throw e;
			}
		}
	}

	/**
	 * Fetch execution plans while running SQLs
	 *
	 * @param queries String
	 */
	private void runQueryPlanOnly(Vector<String> qVector) {
		try {
			connection.getConnection(true);
		} catch (SQLException e) {
			CommonUITool.openErrorBox(e.getLocalizedMessage());
			return;
		}
		if (!connection.hasConnection()) {
			return;
		}

		// clearPlan();
		CUBRIDStatementProxy statement = null;
		int i = 0;
		try {
			int len = qVector.size();
			for (i = 0; i < len; i++) {
				String sql = qVector.get(i).toString();
				statement = getStatement(connection.getConnection(), false);
				StructQueryPlan sq = new StructQueryPlan(sql, statement.getQueryplan(sql),
						new Date());
				if (combinedQueryComposite.getQueryPlanResultComp().isDisposed()) {
					combinedQueryComposite.newQueryPlanComp();
				}
				combinedQueryComposite.getQueryPlanResultComp().makePlan(sq, i);
				QueryUtil.freeQuery(statement);
				statement = null;

				if (collectExecStats) {
					displayTuneModeResult(new TuneModeModel(sq, null));
				}
			}
		} catch (Exception ee) {
			int errorCode = 0;
			if (SQLException.class.isInstance(ee)) {
				errorCode = ((SQLException) ee).getErrorCode();
			}
			String errmsg = "";

			if (isAutocommit) {
				try {
					queryAction(QUERY_ACTION.ROLLBACK);
				} catch (SQLException e1) {
					LOGGER.error("", e1);
				}
			}

			SQLEditorComposite sqlEditorComp = combinedQueryComposite.getSqlEditorComp();
			sqlEditorComp.txtFind((String) qVector.get(i), 0, false, false, true, false);
			StyledText txaEdit = sqlEditorComp.getText();

			int line = txaEdit.getLineAtOffset(txaEdit.getSelection().x) + 1;

			String errorLineMsg = Messages.bind(Messages.errWhere, line);
			errmsg += Messages.runError + errorCode + StringUtil.NEWLINE + errorLineMsg
					+ StringUtil.NEWLINE + Messages.errorHead + ee.getMessage();

			CTabFolder queryResultTabFolder = combinedQueryComposite.getQueryResultComp().getQueryResultTabFolder();
			StyledText logMessagesArea = combinedQueryComposite.getQueryResultComp().getLogMessagesArea();
			QueryResultComposite queryResult = combinedQueryComposite.getQueryResultComp();

			queryResultTabFolder.setSelection(0);
			String logMessage = logMessagesArea.getText();
			if (logMessage != null && logMessage.length() > 0) {
				logMessage += StringUtil.NEWLINE;
			}

			logMessagesArea.setText(logMessage + StringUtil.NEWLINE + errmsg);
			logMessagesArea.setTopIndex(logMessagesArea.getLineCount() - 1);

			queryResult.setSelection();
			LOGGER.error(ee.getMessage(), ee);
		} finally {
			QueryUtil.freeQuery(statement);
			statement = null;
		}
		autoCommitItem.setEnabled(true);
		queryPlanItem.setEnabled(true);
		setPstmtParaItem.setEnabled(true);
		isRunning = false;
	}

	/**
	 * Get a query clause from start position
	 *
	 * @param queries String
	 * @param startPos int
	 * @return String
	 */
	private static String getQuery(String queries, int startPos) {
		char[] buffer = queries.toCharArray();

		boolean sglQuote = false;
		boolean dblQuote = false;
		boolean isLineComment = false;
		boolean isBlockComment = false;
		char prevChar = '\0';

		int start = startPos;
		int end = startPos;

		for (int i = start; i < buffer.length; i++) {
			if (buffer[i] == '\'' && !dblQuote) {
				sglQuote = !sglQuote;
			}
			if (buffer[i] == '"' && !sglQuote) {
				dblQuote = !dblQuote;
			}

			if (!dblQuote && !sglQuote) {
				if (!isLineComment && prevChar == '-' && buffer[i] == '-' && !isBlockComment) {
					isLineComment = true;
				} else if (!isLineComment && prevChar == '/' && buffer[i] == '/' && !isBlockComment) {
					isLineComment = true;
				}

				if (isLineComment && buffer[i] == '\n') {
					isLineComment = false;
				}

				if (!isBlockComment && prevChar == '/' && buffer[i] == '*' && !isLineComment) {
					isBlockComment = true;
				}

				if (isBlockComment && prevChar == '*' && buffer[i] == '/') {
					isBlockComment = false;
				}
			}

			prevChar = buffer[i];

			if (!isLineComment && !isBlockComment && !dblQuote && !sglQuote && buffer[i] == ';') {
				end = i + 1;
				String aQuery = queries.substring(start, end);
				if (QueryUtil.isNotEmptyQuery(aQuery)) {
					return aQuery;
				}
			}
		}
		if (end < buffer.length) {
			String aQuery = queries.substring(end, buffer.length);
			if (QueryUtil.isNotEmptyQuery(aQuery)) {
				return aQuery;
			}
		}

		return "";
	}

	public String getAllQueries() {
		return combinedQueryComposite.getSqlEditorComp().getText().getText();
	}

	/**
	 * On the editor focused, show the selected server name on window title
	 */
	public void setFocus() {
		combinedQueryComposite.getSqlEditorComp().getText().setFocus();
		CubridDatabase database = getSelectedDatabase();
		LayoutManager.getInstance().getTitleLineContrItem().changeTitleForQueryEditor(database);
		LayoutManager.getInstance().getStatusLineContrItem().changeStuatusLineForViewOrEditPart(
				database, this);
	}

	/**
	 * Open SQL script
	 */
	public void doOpen() {
		try {
			combinedQueryComposite.getSqlEditorComp().doOpen();
		} catch (Exception e) {
			LOGGER.error("", e);
			CommonUITool.openErrorBox(getSite().getShell(), e.getMessage());
		}
	}

	/**
	 * Save SQL script
	 *
	 * @param monitor IProgressMonitor
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			boolean success = combinedQueryComposite.getSqlEditorComp().save();
			if (!success) {
				if (monitor != null) {
					monitor.setCanceled(true);
				}
				return;
			}
		} catch (IOException event) {
			LOGGER.error(event.getMessage());
			CommonUITool.openErrorBox(getSite().getShell(), event.getMessage());
		}
	}

	public void doSaveAs() {
		try {
			combinedQueryComposite.getSqlEditorComp().doSaveAs();
		} catch (IOException event) {
			LOGGER.error(event.getMessage());
			CommonUITool.openErrorBox(getSite().getShell(), event.getMessage());
		}
	}

	/**
	 * Get saved file
	 *
	 * @return file File
	 */
	public static File getSavedFile() {
		String filterPath = CommonUIPlugin.getSettingValue("SAVE_SQL_SCRIPT_FILE");
		FileDialog dialog = new FileDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE
						| SWT.APPLICATION_MODAL);
		dialog.setFilterExtensions(new String[] { "*.sql", "*.txt", "*.*" });
		dialog.setFilterNames(new String[] { "SQL File", "Text File", "All" });
		if (null != filterPath) {
			dialog.setFilterPath(filterPath);
		}
		String filePath = dialog.open();
		if (filePath == null) {
			return null;
		} else {
			File file = new File(filePath);
			if (file != null) {
				CommonUIPlugin.putSettingValue("SAVE_SQL_SCRIPT_FILE", file.getParent());
			}

			return file;
		}
	}

	/**
	 * Open a dialog to set the save file
	 *
	 * @return dialog FileDialog
	 */
	public static FileDialog openFileSavePlanDialog() {
		FileDialog dialog = new FileDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE
						| SWT.APPLICATION_MODAL);
		dialog.setFilterExtensions(new String[] { "*.xml" });
		dialog.setFilterNames(new String[] { "XML File" });
		File curdir = new File(".");
		try {
			dialog.setFilterPath(curdir.getCanonicalPath());
		} catch (Exception event) {
			dialog.setFilterPath(".");
		}

		return dialog;
	}

	/**
	 * Get opened file
	 *
	 * @return File File
	 */
	public static File getOpenedSQLFile() {
		String filterPath = CommonUIPlugin.getSettingValue("OPEN_SQL_SCRIPT_FILE");
		FileDialog dialog = new FileDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN
						| SWT.APPLICATION_MODAL);
		dialog.setFilterExtensions(new String[] { "*.sql", "*.txt", "*.*" });
		dialog.setFilterNames(new String[] { "SQL File", "Text File", "All" });
		if (null != filterPath) {
			dialog.setFilterPath(filterPath);
		}
		String filePath = dialog.open();
		if (filePath == null) {
			return null;
		} else {
			File file = new File(filePath);
			if (file != null) {
				CommonUIPlugin.putSettingValue("OPEN_SQL_SCRIPT_FILE", file.getParent());
			}

			return file;
		}
	}

	/**
	 * Open a dialog to open plan contents
	 *
	 * @return dialog FileDialog
	 */
	public static FileDialog openFileOpenPlanDialog() {
		FileDialog dialog = new FileDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN
						| SWT.APPLICATION_MODAL);
		dialog.setFilterExtensions(new String[] { "*.xml" });
		dialog.setFilterNames(new String[] { "XML File" });
		File curdir = new File(".");
		try {
			dialog.setFilterPath(curdir.getCanonicalPath());
		} catch (Exception event) {
			dialog.setFilterPath(".");
		}

		return dialog;
	}

	/**
	 * Is the query editor not save
	 *
	 * @return boolean
	 */
	public boolean isDirty() {
		return true;
		//		if (saveFileMap.get(combinedQueryComposite) != null) {
		//			return dirty;
		//		}
		//
		//		if (combinedQueryComposite.isDirty()) {
		//			return true;
		//		}
		//		if (editorsMap.size() > 0) {
		//			CTabItem[] items = combinedQueryEditortabFolder.getItems();
		//			for (CTabItem item : items) {
		//				CombinedQueryEditorComposite comp = editorsMap.get(item);
		//				if (comp.isDirty()) {
		//					return true;
		//				}
		//			}
		//
		//			return false;
		//		}
		//
		//		ServerInfo serverInfo = getSelectedServer() == null ? null : getSelectedServer().getServerInfo();
		//		boolean isWithoutPrompt = QueryOptions.getWithoutPromptSave(serverInfo);
		//		if (isWithoutPrompt) {
		//			return false;
		//		} else {
		//			return dirty;
		//		}
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Find action
	 */
	public void find() {
		combinedQueryComposite.getSqlEditorComp().find();
	}

	/**
	 * Initialize the query database
	 *
	 * @param database CubridDatabase
	 * @return boolean
	 */
	private boolean initConnection(CubridDatabase database) {
		// [TOOLS-2425]Support shard broker
		if (database == null || CubridDatabase.hasValidDatabaseInfo(database)
				&& database.getDatabaseInfo().isShard()) {
			changeShardIdValItem.setEnabled(true);
		} else {
			changeShardIdValItem.setEnabled(false);
		}
		shardId = 0;
		shardVal = 0;
		shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_VAL;

		connection.changeDatabaseInfo(database.getDatabaseInfo());

		//		try {
		//			connection.close();
		//			connection.checkAndConnect();
		//		} catch (SQLException event) {
		//			String errorMsg = Messages.errDbConnect;
		//			if (event.getMessage() != null) {
		//				errorMsg = Messages.bind(
		//						com.cubrid.common.ui.common.Messages.errCommonTip,
		//						event.getErrorCode(), event.getMessage());
		//			}
		//			LOGGER.error(errorMsg);
		//			queryPlanItem.setEnabled(false);
		//			setPstmtParaItem.setEnabled(false);
		//			CommonUITool.openErrorBox(errorMsg);
		//
		//			return false;
		//		}

		return true;
	}

	/**
	 *
	 * Change query editor part name
	 *
	 * @param text String
	 *
	 */
	public void changeQueryEditorPartName(String text) {
		IEditorInput input = getEditorInput();
		if (!(input instanceof QueryUnit) || !isUpdatePartName) {
			return;
		}

		QueryUnit queryUnit = (QueryUnit) input;
		queryUnit.setToolTip(text);
		setPartName("[" + currentEditorIndex + "] " + text);
		editorTabNameOriginal = text;
	}

	public void changeQueryEditorPartNameWithShard() {
		IEditorInput input = getEditorInput();
		if (!(input instanceof QueryUnit)) {
			return;
		}

		// [TOOLS-2425]Support shard broker
		boolean isShard = false;
		QueryUnit queryUnit = (QueryUnit) input;
		if (CubridDatabase.hasValidDatabaseInfo(queryUnit.getDatabase())) {
			DatabaseInfo dbInfo = queryUnit.getDatabase().getDatabaseInfo();
			isShard = dbInfo.isShard();
		}
		if (isShard) {
			String text = null;
			if (shardQueryType == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
				text = editorTabNameOriginal + "(SHARD_ID:" + shardId + ")";
			} else {
				text = editorTabNameOriginal + "(SHARD_VAL:" + shardVal + ")";
			}
			queryUnit.setToolTip(text);
			setPartName(text);
		}
	}

	/**
	 * Reset the query connection
	 *
	 * @return boolean
	 */
	public boolean resetJDBCConnection() {
		if (connection.hasConnection() && isActive) {
			String title = com.cubrid.common.ui.common.Messages.titleConfirm;
			String msg = Messages.bind(Messages.connCloseConfirm,
					new String[] { this.getSelectedDatabase().getLabel() });
			String[] buttons = new String[] { Messages.btnYes, Messages.btnNo, Messages.cancel };
			MessageDialog dialog = new MessageDialog(getSite().getShell(), title, null, msg,
					MessageDialog.QUESTION, buttons, 0) {
				protected void buttonPressed(int buttonId) {
					switch (buttonId) {
					case 0:
						commit();
						setReturnCode(0);
						close();
						break;
					case 1:
						rollback();
						setReturnCode(1);
						close();
						break;
					case 2:
						setReturnCode(2);
						close();
						break;
					default:
						break;
					}
				}
			};
			int returnVal = dialog.open();
			if (returnVal == 2 || returnVal == -1) {
				return false;
			}
		}
		if (connection != null) {
			connection.close();
		}
		return true;
	}

	/**
	 * Shut down the current connection
	 */
	public void shutDownConnection() {
		if (connection.hasConnection()) {
			try {
				queryAction(QUERY_ACTION.CLOSE);
			} catch (SQLException e) {
				CommonUITool.openErrorBox(StringUtil.getStackTrace(e, "\n"));
			}
		}
	}

	/**
	 * If the database selection successful
	 *
	 * @return boolean
	 */
	public boolean isConnected() {
		return qeToolBar != null && !qeToolBar.isNull();
	}

	/**
	 * Get selected server
	 *
	 * @return CubridServer
	 */
	public CubridServer getSelectedServer() {
		if (qeToolBar == null) {
			return null;
		}

		return qeToolBar.getSelectedDb().getServer();
	}

	/**
	 * Get selected database
	 *
	 * @return CubridDatabase
	 */
	public CubridDatabase getSelectedDatabase() {
		if (qeToolBar == null) {
			return null;
		}

		return qeToolBar.getSelectedDb();
	}

	/**
	 * Update row data on result table
	 *
	 * @param strOid String
	 * @param column String[]
	 * @param value String[]
	 * @throws SQLException if failed
	 */
	public void updateResult(String strOid, String[] column, Object[] value) throws SQLException {
		connection.checkAndConnectQuietly();
		if (isAutocommit) {
			queryAction(QUERY_ACTION.AUTOCOMMIT, true);
		} else {
			queryAction(QUERY_ACTION.AUTOCOMMIT, false);
		}
		CUBRIDOIDProxy oid = CUBRIDOIDProxy.getNewInstance(
				(CUBRIDConnectionProxy) connection.getConnection(), strOid);
		for (int i = 0; value != null && i < value.length; i++) {
			if (DataType.VALUE_NULL.equals(value[i])) {
				value[i] = null;
			}
		}
		oid.setValues(column, value);
		if (isAutocommit) {
			queryAction(QUERY_ACTION.COMMIT);
		}
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				setHaveActiveTransaction(true);
			}
		});
	}

	/**
	 * Set active status
	 *
	 * @param isActive boolean
	 */
	public void setHaveActiveTransaction(boolean isActive) {
		if (isAutocommit) {
			this.isActive = false;
			rollbackItem.setEnabled(false);
			commitItem.setEnabled(false);
			noticeMessageArea.setLayoutData(shownGridDataForNotice);
			noticeMessageArea.setText(" " + Messages.msgAutocommitStateNoticeAuto);
			noticeMessageArea.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			noticeMessageArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
		} else {
			this.isActive = isActive;
			rollbackItem.setEnabled(isActive);
			commitItem.setEnabled(isActive);

			// It should be indicated while it has transactions.
			if (isActive) {
				if (dateOnBeginingTransaction == null) {
					dateOnBeginingTransaction = new Date();
					countQueryOnBeginingTransaction = 0;
				}

				countQueryOnBeginingTransaction++;
				String datetime = DateUtil.getDatetimeString(dateOnBeginingTransaction, "HH:mm:ss");
				String message = Messages.bind(" " + Messages.msgAutocommitStateNoticeTranx,
						datetime, String.valueOf(countQueryOnBeginingTransaction));
				noticeMessageArea.setLayoutData(shownGridDataForNotice);
				noticeMessageArea.setText(message);
				noticeMessageArea.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				noticeMessageArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
			} else {
				countQueryOnBeginingTransaction = 0;
				dateOnBeginingTransaction = null;
				noticeMessageArea.setLayoutData(hiddenGridDataForNotice);
				noticeMessageArea.setText("");
			}
		}
		topComposite.pack();
		packToolBar();
	}

	/**
	 * Pack the tool bar
	 */
	private void packToolBar() {
		qeToolBar.pack();
	}

	public boolean isActive() {
		return isActive;
	}

	public static boolean isNotNeedQuote(String type) {
		if (DataType.DATATYPE_BIGINT.equals(type)
				//				|| DataType.DATATYPE_BIT.equals(type)
				//				|| DataType.DATATYPE_BIT_VARYING.equals(type)
				//				|| DataType.DATATYPE_BLOB.equals(type)
				//				|| DataType.DATATYPE_CLASS.equals(type)
				//				|| DataType.DATATYPE_CLOB.equals(type)
				|| DataType.DATATYPE_CURRENCY.equals(type)
				//				|| DataType.DATATYPE_DATE.equals(type)
				//				|| DataType.DATATYPE_DATETIME.equals(type)
				|| DataType.DATATYPE_DECIMAL.equals(type)
				|| DataType.DATATYPE_DOUBLE.equals(type)
				|| DataType.DATATYPE_FLOAT.equals(type)
				|| DataType.DATATYPE_INT.equals(type)
				|| DataType.DATATYPE_INTEGER.equals(type)
				|| DataType.DATATYPE_MONETARY.equals(type)
				//				|| DataType.DATATYPE_MULTISET.equals(type)
				|| DataType.DATATYPE_NATIONAL_CHARACTER.equals(type)
				|| DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(type)
				|| DataType.DATATYPE_NCHAR.equals(type)
				|| DataType.DATATYPE_NCHAR_VARYING.equals(type)
				|| DataType.DATATYPE_NUMERIC.equals(type)
				//				|| DataType.DATATYPE_OBJECT.equals(type)
				//				|| DataType.DATATYPE_OID.equals(type)
				|| DataType.DATATYPE_REAL.equals(type)
				//				|| DataType.DATATYPE_SEQUENCE.equals(type)
				//				|| DataType.DATATYPE_SET.equals(type)
				|| DataType.DATATYPE_SHORT.equals(type) || DataType.DATATYPE_SMALLINT.equals(type)
				//				|| DataType.DATATYPE_TIME.equals(type)
				//				|| DataType.DATATYPE_TIMESTAMP.equals(type)
				|| DataType.DATATYPE_TINYINT.equals(type)) {
			return true;
		}

		return false;
	}

	public static boolean isNullEmpty(String type, String value) {
		if ((value == null || value.length() == 0)
				&& (DataType.DATATYPE_BIGINT.equals(type) || DataType.DATATYPE_BIT.equals(type)
						|| DataType.DATATYPE_BIT_VARYING.equals(type)
						|| DataType.DATATYPE_BLOB.equals(type)
						|| DataType.DATATYPE_CHAR.equals(type)
						|| DataType.DATATYPE_CLASS.equals(type)
						|| DataType.DATATYPE_CLOB.equals(type)
						|| DataType.DATATYPE_CURRENCY.equals(type)
						|| DataType.DATATYPE_DATE.equals(type)
						|| DataType.DATATYPE_DATETIME.equals(type)
						|| DataType.DATATYPE_DECIMAL.equals(type)
						|| DataType.DATATYPE_DOUBLE.equals(type)
						|| DataType.DATATYPE_FLOAT.equals(type)
						|| DataType.DATATYPE_INT.equals(type)
						|| DataType.DATATYPE_INTEGER.equals(type)
						|| DataType.DATATYPE_MONETARY.equals(type)
						|| DataType.DATATYPE_MULTISET.equals(type)
						|| DataType.DATATYPE_NATIONAL_CHARACTER.equals(type)
						|| DataType.DATATYPE_NATIONAL_CHARACTER_VARYING.equals(type)
						|| DataType.DATATYPE_NCHAR.equals(type)
						|| DataType.DATATYPE_NCHAR_VARYING.equals(type)
						|| DataType.DATATYPE_NUMERIC.equals(type)
						|| DataType.DATATYPE_OBJECT.equals(type)
						|| DataType.DATATYPE_OID.equals(type)
						|| DataType.DATATYPE_REAL.equals(type)
						|| DataType.DATATYPE_SEQUENCE.equals(type)
						|| DataType.DATATYPE_SET.equals(type)
						|| DataType.DATATYPE_SHORT.equals(type)
						|| DataType.DATATYPE_SMALLINT.equals(type)
						|| DataType.DATATYPE_TIME.equals(type)
						|| DataType.DATATYPE_TIMESTAMP.equals(type) || DataType.DATATYPE_TINYINT.equals(type))) {
			return true;
		}

		return false;
	}

	/**
	 * the type can't deal on result type
	 *
	 * @param type
	 * @return boolean
	 */
	public boolean isIgnoreType(String type) {
		if (type == null || type.equals("")) {
			return false;
		}

		if (DataType.DATATYPE_CURSOR.equals(type) || DataType.DATATYPE_OID.equals(type)
				|| DataType.DATATYPE_CLASS.equals(type) || DataType.DATATYPE_OBJECT.equals(type)
				|| DataType.DATATYPE_CURSOR.equals(type)) {
			return true;
		}

		return false;
	}

	/**
	 * Delete data through data OID
	 *
	 * @param strOid String[]
	 * @throws SQLException if failed
	 */
	public void deleteResult(String[] strOid) throws SQLException {
		if (strOid == null || strOid.length <= 0)
			return;

		connection.checkAndConnectQuietly();
		for (int i = 0; i < strOid.length; i++) {
			CUBRIDOIDProxy.getNewInstance((CUBRIDConnectionProxy) connection.getConnection(),
					strOid[i]).remove();
		}

		if (isAutocommit) {
			queryAction(QUERY_ACTION.COMMIT);
		}

		setHaveActiveTransaction(true);
	}

	public boolean isCollectExecStats() {
		return collectExecStats;
	}

	public DBConnection getConnection() {
		return connection;
	}

	/**
	 * A thread focus on execute query operation
	 *
	 * @author wangsl 2009-6-5
	 */
	class QueryThread implements
			Runnable {
		private final Vector<String> queries;
		private final QueryEditorPart queryEditor;
		private int lineSkip;
		private boolean[] value = null;
		private final List<List<PstmtParameter>> rowParameterList;
		private DBConnection con;
		private CubridDatabase database;
		private boolean multi;

		public QueryThread(Vector<String> query, QueryEditorPart part,
				List<List<PstmtParameter>> rowParameterList, DBConnection con,
				CubridDatabase database) {
			this.queries = query;
			this.queryEditor = part;
			this.rowParameterList = rowParameterList;
			this.con = con;
			this.database = database;
		}

		public QueryThread(Vector<String> query, QueryEditorPart part,
				List<List<PstmtParameter>> rowParameterList, DBConnection con,
				CubridDatabase database, boolean multi) {
			this.queries = query;
			this.queryEditor = part;
			this.rowParameterList = rowParameterList;
			this.con = con;
			this.database = database;
			this.multi = multi;
		}

		public QueryExecuter createQueryExecutor(QueryEditorPart queryEditor, int cntResults,
				String sql, CubridDatabase database, List<PstmtParameter> parameterList,
				String orignSQL, List<String> columnTableNameList) {
			QueryExecuter executer = null;
			if (multi) {
				executer = new QueryExecuter(queryEditor, cntResults, sql, database, this.con,
						parameterList, orignSQL);
			} else {
				executer = new QueryExecuter(queryEditor, cntResults, sql, database, parameterList,
						orignSQL);
			}
			executer.setColumnTableNames(columnTableNameList);
			return executer;
		}

		/**
		 * Execute query
		 */
		public void run() { // FIXME very complicated
			int i = 0;
			int cntResults = 0;
			String noSelectSql = "";
			StringBuilder logs = new StringBuilder(1024);
			StringBuilder log = new StringBuilder(256);
			boolean hasModifyQuery = false;
			boolean isIsolationHigher = false;
			boolean resolvedTransaction = false;
			long beginTimestamp = 0;
			double elapsedTime = 0.0;
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(3);
			result = null;
			String multiQuerySql = null;
			final Vector<QueryExecuter> curResult = new Vector<QueryExecuter>();
			final List<String> tableNames = new ArrayList<String>(); // it should be a not null.
			if (database == null) {
				database = ((QueryUnit) queryEditor.getEditorInput()).getDatabase();
			}

			TuneModeModel tuneModeModel = null;
			try {
				// As the sql history saving speed are so slow, it should be used the in-memory-management as a queue.
				RecentlyUsedSQLDetailPersistUtils.load(database);

				if (queries.isEmpty()) {
					return;
				} else if (database.getDatabaseInfo().isShard()) {
					// Shard didn't support to get an isolation yet
				} else {
					isIsolationHigher = isIsolationHigherThanRepeatableRead(con.checkAndConnect(),
							isActive);
				}

				ServerInfo serverInfo = null;
				if (database != null) {
					serverInfo = this.database.getServer().getServerInfo();
				}

				boolean enableSearchUnit = QueryOptions.getEnableSearchUnit(serverInfo);
				int unitCount = QueryOptions.getSearchUnitCount(serverInfo);
				int sqlTotalCount = queries.size();

				for (i = 0; i < sqlTotalCount; i++) {
					log.delete(0, log.length());
					long endTimestamp = 0;

					SQLHistoryDetail sqlHistoryDetail = new SQLHistoryDetail();
					sqlHistoryDetail.setExecuteTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));

					String sql = queries.get(i).toString();
					if (sql != null && sql.trim().lastIndexOf(";") == -1) {
						sql += ";";
					}

					// [TOOLS-2425]Support shard broker
					if (database.getDatabaseInfo().isShard()) {
						Pattern idPattern = Pattern.compile(QueryUtil.REGEX_PATTERN_SHARD_ID,
								Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
						Matcher idMatcher = idPattern.matcher(sql);

						Pattern valPattern = Pattern.compile(QueryUtil.REGEX_PATTERN_SHARD_VAL,
								Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
						Matcher valMatcher = valPattern.matcher(sql);

						if (!idMatcher.find() && !valMatcher.find()) {
							if (shardQueryType == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
								sql = QueryUtil.wrapShardQueryWithId(sql, queryEditor.getShardId());
							} else {
								sql = QueryUtil.wrapShardQueryWithVal(sql,
										queryEditor.getShardVal());
							}
						}
					}

					String orignSQL = sql;

					List<PstmtParameter> parameterList = null;
					if (rowParameterList != null && rowParameterList.size() > i) {
						parameterList = rowParameterList.get(i);
					}

					if (enableSearchUnit && unitCount > 0 && rowParameterList == null) {
						multiQuerySql = SqlParser.getPaginatingSqlClause(sql);
					} else {
						multiQuerySql = null;
					}

					String order = StringUtil.getOrdinalFromCardinalNumber(i+1);
					if (multiQuerySql == null) {
						sql = SqlParser.convertComment(sql);
						beginTimestamp = System.currentTimeMillis();
						try {
							pStmt = QueryExecuter.getStatement(con.checkAndConnect(), sql,
									queryEditor.isIncludeOidInfo(), false);
							tableNames.addAll(UIQueryUtil.loadColumnTableNameList(pStmt));

							if (parameterList != null) {
								String charset = getSelectedDatabase().getDatabaseInfo().getCharSet();
								for (PstmtParameter pstmtParameter : parameterList) {
									FieldHandlerUtils.setPreparedStatementValue(pstmtParameter,
											pStmt, charset);
								}
							}
							resolvedTransaction = hasResolvedTransactionQuery(sql);
						} catch (final SQLException ee) {
							LOGGER.error("", ee);
							logs.append(makeSqlErrorOnResult(i, sql, ee));

							if (i == sqlTotalCount - 1) {
								continue;
							}

							lineSkip = i;
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									if (dontTipNext) {
										return;
									}
									value = queryErrSkipOrNot(ee, queries.get(lineSkip).toString());
									dontTipNext = value[0];
									isSkip = value[1];
								}
							});

							if (isSkip) {
								continue;
							} else {
								throw ee;
							}
						}

						String queryPlan = null;
						//						StringBuilder statLogs = new StringBuilder();
						if (pStmt.hasResultSet()) {
							pStmt.setQueryInfo(false);
							pStmt.setOnlyQueryPlan(false);
							try {
								// begin tune mode
								if (collectExecStats) {
									beginCollectExecStats();
								}
								pStmt.execute();
								rs = (CUBRIDResultSetProxy) pStmt.getResultSet();

								endTimestamp = System.currentTimeMillis();
							} catch (final SQLException ee) {
								LOGGER.error(ee.getMessage(), ee);
								logs.append(makeSqlErrorOnResult(i, sql, ee));

								if (i == sqlTotalCount - 1) {
									continue;
								}

								lineSkip = i;
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										if (dontTipNext) {
											return;
										}
										value = queryErrSkipOrNot(ee,
												queries.get(lineSkip).toString());
										dontTipNext = value[0];
										isSkip = value[1];
									}
								});

								if (isSkip) {
									continue;
								} else {
									throw ee;
								}
							}

							elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
							String elapsedTimeStr = nf.format(elapsedTime);
							if (elapsedTime < 0.001) {
								elapsedTimeStr = "0.000";
							}

							result = createQueryExecutor(queryEditor, cntResults, sql, database,
									parameterList, orignSQL, tableNames);

							result.makeResult(rs);

							// collect statistics and query plan on tune mode
							StringBuilder statLogs = new StringBuilder();
							if (collectExecStats) {
								Map<String, String> stat = CubridUtil.fetchStatistics(connection.checkAndConnect());
								String statResult = CubridUtil.makeStatisticsWithRawText(stat);
								if (statResult != null) {
									statLogs.append(statResult);
								}

								final StructQueryPlan sq = new StructQueryPlan(sql,
										pStmt.getQueryplan(sql), new Date());
								queryPlan = sq.getPlanRaw();

								final int planIndex = i;
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										if (combinedQueryComposite.getQueryPlanResultComp().isDisposed()) {
											combinedQueryComposite.newQueryPlanComp();
										}
										combinedQueryComposite.getQueryPlanResultComp().makePlan(
												sq, planIndex);
									}
								});

								result.setQueryPlanLog(queryPlan);
								result.setStatsLog(statLogs.toString());

								tuneModeModel = new TuneModeModel(sq, stat);
							}

							String queryMsg = Messages.bind(Messages.querySeq, order) + "[ " + elapsedTimeStr
									+ " " + Messages.second + " , " + Messages.totalRows + " : "
									+ result.cntRecord + " ]" + StringUtil.NEWLINE;

							result.setQueryMsg(queryMsg);
							sqlHistoryDetail.setExecuteInfo(queryMsg);
							sqlHistoryDetail.setElapseTime(elapsedTimeStr);
							if (pStmt.getStatementType() == CUBRIDCommandType.CUBRID_STMT_EVALUATE
									|| pStmt.getStatementType() == CUBRIDCommandType.CUBRID_STMT_CALL) {
								hasModifyQuery = true;
							}
							curResult.addElement(result);
							cntResults++;
						} else { // DML
							byte execType = pStmt.getStatementType();
							/*
							 * the previous version , the variable
							 * threadExecResult is class field, but why ? is it
							 * necessary?
							 */
							int threadExecResult = 0;
							try {
								if (collectExecStats) {
									beginCollectExecStats();
								}

								threadExecResult = pStmt.executeUpdate();
								endTimestamp = System.currentTimeMillis();

								if (collectExecStats) {
									final StructQueryPlan sq = new StructQueryPlan(sql,
											pStmt.getQueryplan(sql), new Date());
									Map<String, String> stat = CubridUtil.fetchStatistics(connection.checkAndConnect());
									//									String statResult = CubridUtil.fetchStatisticsWithRawText(getConnection());
									//									if (statResult != null) {
									//										statLogs.append(statResult);
									//									}
									tuneModeModel = new TuneModeModel(sq, stat);
								}
							} catch (final SQLException ee) {
								LOGGER.error(ee.getMessage(), ee);
								logs.append(makeSqlErrorOnResult(i, sql, ee));

								if (i == sqlTotalCount - 1) {
									continue;
								}

								lineSkip = i;
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										if (dontTipNext) {
											return;
										}
										value = queryErrSkipOrNot(ee,
												queries.get(lineSkip).toString());
										dontTipNext = value[0];
										isSkip = value[1];
									}
								});
								if (isSkip) {
									continue;
								} else {
									throw ee;
								}

							}
							elapsedTime = (endTimestamp - beginTimestamp) * 0.001;
							int cntModify = threadExecResult;
							noSelectSql += sql + StringUtil.NEWLINE;
							hasModifyQuery = true;

							log.append(Messages.bind(Messages.querySeq, order)).append(" ");
							switch (execType) {
							case CUBRIDCommandType.CUBRID_STMT_ALTER_CLASS:
							case CUBRIDCommandType.CUBRID_STMT_ALTER_SERIAL:
							case CUBRIDCommandType.CUBRID_STMT_RENAME_CLASS:
							case CUBRIDCommandType.CUBRID_STMT_RENAME_TRIGGER:
								log.append(Messages.alterOk);
								break;
							case CUBRIDCommandType.CUBRID_STMT_CREATE_CLASS:
							case CUBRIDCommandType.CUBRID_STMT_CREATE_INDEX:
							case CUBRIDCommandType.CUBRID_STMT_CREATE_TRIGGER:
							case CUBRIDCommandType.CUBRID_STMT_CREATE_SERIAL:
								log.append(Messages.createOk);
								break;
							case CUBRIDCommandType.CUBRID_STMT_DROP_DATABASE:
							case CUBRIDCommandType.CUBRID_STMT_DROP_CLASS:
							case CUBRIDCommandType.CUBRID_STMT_DROP_INDEX:
							case CUBRIDCommandType.CUBRID_STMT_DROP_LABEL:
							case CUBRIDCommandType.CUBRID_STMT_DROP_TRIGGER:
							case CUBRIDCommandType.CUBRID_STMT_DROP_SERIAL:
							case CUBRIDCommandType.CUBRID_STMT_REMOVE_TRIGGER:
								log.append(Messages.dropOk);
								break;
							case CUBRIDCommandType.CUBRID_STMT_INSERT:
								log.append(Messages.bind(Messages.insertOk, cntModify));
								break;
							case CUBRIDCommandType.CUBRID_STMT_SELECT:
								break;
							case CUBRIDCommandType.CUBRID_STMT_UPDATE:
								log.append(Messages.bind(Messages.updateOk2, cntModify));
								break;
							case CUBRIDCommandType.CUBRID_STMT_DELETE:
								log.append(Messages.bind(Messages.deleteOk, cntModify));
								break;
							/* others are 'Successfully execution' */
							/*
							 * Under two line works disable button when query's
							 * last command is commit/rollback
							 */
							case CUBRIDCommandType.CUBRID_STMT_COMMIT_WORK:
							case CUBRIDCommandType.CUBRID_STMT_ROLLBACK_WORK:
								hasModifyQuery = false;
							default:
								log.append(Messages.queryOk);
								break;
							}

							String elapsedTimeStr = nf.format(elapsedTime);
							if (elapsedTime < 0.001) {
								elapsedTimeStr = "0.000";
							}
							log.append(" [").append(elapsedTimeStr).append(" ");
							log.append(Messages.second).append("]").append(StringUtil.NEWLINE);

							logs.append(log);
							logs.append(makeSqlLogOnResult(sql));

							sqlHistoryDetail.setExecuteInfo(log.toString());
							sqlHistoryDetail.setElapseTime(elapsedTimeStr);
						}
					} else {
						result = createQueryExecutor(queryEditor, cntResults, "", database,
								parameterList, orignSQL, tableNames);
						result.setMultiQuerySql(multiQuerySql);
						result.setQueryMsg(Messages.bind(Messages.querySeq, order));
						result.setSqlDetailHistory(sqlHistoryDetail);

						try {
							tuneModeModel = result.makeTable(1, collectExecStats);
						} catch (final SQLException ee) {
							LOGGER.error(ee.getMessage(), ee);
							logs.append(makeSqlErrorOnResult(i, sql, ee));

							if (i == sqlTotalCount - 1) {
								continue;
							}

							lineSkip = i;
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									if (dontTipNext) {
										return;
									}
									value = queryErrSkipOrNot(ee, queries.get(lineSkip).toString());
									dontTipNext = value[0];
									isSkip = value[1];
								}
							});
							if (isSkip) {
								continue;
							} else {
								throw ee;
							}

						}
						curResult.addElement(result);
						cntResults++;
					}

					QueryUtil.freeQuery(pStmt, rs);
					pStmt = null;
					rs = null;

					// To save the recently used SQL
					// The user don't have to use multiple executed sql for the auto completion.
					if (sqlTotalCount == 1) {
						RecentlyUsedSQLContentPersistUtils.addRecentlyUsedSQLContentsById(
								getSelectedDatabase(), sql);
					}

					// SQL execution log
					sqlHistoryDetail.setSql(sql);
					RecentlyUsedSQLDetailPersistUtils.addLog(database, sqlHistoryDetail);
				}

				if (isAutocommit) {
					// TODO : Should it need to use?
					// Because it will commit automatically without running auto commit method
					// when it has the auto commit mode
					queryAction(QUERY_ACTION.COMMIT);
				}
			} catch (final SQLException event) {
				try {
					// Keep it for safe rollback transactions
					if (isAutocommit) {
						queryAction(QUERY_ACTION.ROLLBACK);
					}
				} catch (SQLException e1) {
					LOGGER.error("", e1);
				}

				if (multiQuerySql == null || result == null) {
					final String errorSql = (String) queries.get(i);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							SQLEditorComposite sqlEditorComp = combinedQueryComposite.getSqlEditorComp();
							StyledText txaEdit = sqlEditorComp.getText();
							if (txaEdit != null && !txaEdit.isDisposed()) {
								sqlEditorComp.txtFind(errorSql, 0, false, false, true, false);
								line = txaEdit.getLineAtOffset(txaEdit.getSelection().x) + 1;
							}
						}
					});
					noSelectSql += errorSql;
				} else {
					noSelectSql += result.getQuerySql();
					logs.append(result.getQueryMsg());
				}
			} finally {
				final String logsBak = logs.toString();
				final String noSelectSqlBak = noSelectSql;
				final int cntResultsBak = i;
				final boolean hasModifyQueryBak = hasModifyQuery;
				final boolean isIsolationHigherBak = isIsolationHigher;
				final boolean resolvedTransactionBak = resolvedTransaction;
				final TuneModeModel tuneModeModelBak = tuneModeModel;

				RecentlyUsedSQLDetailPersistUtils.save(database);

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						CTabFolder queryResultTabFolder = combinedQueryComposite.getQueryResultComp().getQueryResultTabFolder();
						QueryResultComposite queryResultComposite = null;
						if (multi) {
							queryResultComposite = combinedQueryComposite.getQueryResultComp();
						} else if (queryResultTabFolder != null
								&& !queryResultTabFolder.isDisposed()) {
							QueryUtil.freeQuery(pStmt, rs);
							pStmt = null;
							rs = null;

							queryResultComposite = combinedQueryComposite.getQueryResultComp();
							queryResultComposite.disposeAllResult();
							if (cntResultsBak < 1 && logsBak.trim().length() <= 0) {
								queryResultComposite.makeEmptyResult();
							} else {
								if (logsBak.trim().length() > 0) {
									queryResultComposite.makeLogResult(noSelectSqlBak, logsBak);
								}
								for (int j = 0; j < curResult.size(); j++) {
									queryResultComposite.makeSingleQueryResult((QueryExecuter) curResult.get(j));
								}
							}

							if (!isAutocommit && resolvedTransactionBak) {
								setHaveActiveTransaction(false);
							} else if (hasModifyQueryBak || isIsolationHigherBak) {
								setHaveActiveTransaction(true);
							} else {
								try {
									if (connection.hasConnection()) {
										queryAction(QUERY_ACTION.COMMIT);
									}
								} catch (SQLException event) {
									LOGGER.error("", event);
								}
								setHaveActiveTransaction(false);
							}
							if (queryResultTabFolder.getItemCount() > 0) {
								queryResultTabFolder.setSelection(queryResultTabFolder.getItemCount() - 1);
							}

							autoCommitItem.setEnabled(true);
							queryPlanItem.setEnabled(true);
							setPstmtParaItem.setEnabled(true);
						}
						if (runningCount.decrementAndGet() == 0) {
							queryResultComposite.setCanDispose(true);
							isRunning = false;
						}
						if (collectExecStats && tuneModeModelBak != null) {
							displayTuneModeResult(tuneModeModelBak);
						}
					}
				});
			}
		}
	}

	/**
	 * Return whether the query is TCL or not.
	 *
	 * @param sql String
	 * @return boolean
	 */
	private boolean hasResolvedTransactionQuery(String sql) {
		if (sql == null) {
			return false;
		}
		String tcl = sql.toLowerCase();
		return tcl.startsWith("commit") || tcl.startsWith("rollback");
	}

	public void displayTuneModeResult(final TuneModeModel tuneModeModel) {
		if (tuneModeView == null || tuneModeView.isDisposed()) {
			createTuneModeView();
		}
		tuneModeView.setParentEditorName(getPartName());
		tuneModeView.showResult(tuneModeModel);
	}

	public void showTuneModeResult() {
		tuneModeView.show();
	}

	public void hideTuneModeResult() {
	}

	/**
	 * Decide transaction close using by connection's isolation level for Select
	 * query.
	 *
	 * @param conn isolation level validation connection
	 * @param isActive Transaction is exist? (if transaction is exist, return
	 *        value is always true.)
	 * @return boolean
	 *         <ul>
	 *         <li>true: keep transaction</li>
	 *         <li>false: close transaction</li>
	 *         </ul>
	 */
	public boolean isIsolationHigherThanRepeatableRead(Connection conn, boolean isActive) {
		try {
			if (isActive) {
				return true;
			}
			if (conn == null) {
				return false;
			}
			switch (conn.getTransactionIsolation()) {
			case Connection.TRANSACTION_NONE:
			case Connection.TRANSACTION_READ_COMMITTED:
			case Connection.TRANSACTION_READ_UNCOMMITTED:
			case CUBRIDConnectionProxy.TRAN_REP_CLASS_COMMIT_INSTANCE:
			case CUBRIDConnectionProxy.TRAN_REP_CLASS_UNCOMMIT_INSTANCE:
				return false;
			case Connection.TRANSACTION_REPEATABLE_READ:
			case Connection.TRANSACTION_SERIALIZABLE:
				return true;
			default:
				break;
			}

			return true;
		} catch (final SQLException event) {
			LOGGER.error("", event);
			return false;
		}
	}

	/**
	 * Get the SQL string on editor
	 *
	 * @return SQL String
	 */
	public String getSelectText() {
		return combinedQueryComposite.getSqlEditorComp().getText().getSelectionText();
	}

	/**
	 * Set SQL string to editor
	 *
	 * @param contents String
	 */
	public void setSelectText(String contents) {
		StyledText text = combinedQueryComposite.getSqlEditorComp().getText();
		Point range = text.getSelectionRange();
		text.replaceTextRange(range.x, range.y, contents);
		text.setSelection(range.x + contents.length());
	}

	/**
	 * Delete the selected text
	 */
	public void deleteSelectedText() {
		setSelectText("");
	}

	/**
	 * Get the run item on toolbar
	 *
	 * @return runItem
	 */
	public ToolItem getRunItem() {
		return runItem;
	}

	public ToolItem getRunPlanItem() {
		return this.queryPlanItem;
	}

	public void setQueryPlanItemStatus(boolean status) {
		queryPlanItem.setEnabled(status);
	}

	public void setPstmtParaItemStatus(boolean status) {
		setPstmtParaItem.setEnabled(status);
	}

	public void setAutoCommitItemStatus(boolean status) {
		autoCommitItem.setEnabled(status);
	}

	public ToolItem getShowResultItem() {
		return showResultItem;
	}

	//	/**
	//	 * Set query connection
	//	 *
	//	 * @param queryConn Connection
	//	 */
	//	public void setQueryConn(Connection queryConn) {
	//		this.queryConn = queryConn;
	//	}

	/**
	 * When query options change or database and server refresh, refresh this
	 * query style
	 */
	public void refreshQueryOptions() {
		CubridDatabase database = getSelectedDatabase();
		boolean enabled = (database != DatabaseNavigatorMenu.NULL_DATABASE);
		if (enabled && !connection.hasConnection()) {
			this.initConnection(database);
		}
		enabled = enabled && connection.hasConnection();
		if (enabled) {
			for (CombinedQueryEditorComposite combinedQueryEditorComposite : getAllCombinedQueryEditorComposite()) {
				combinedQueryEditorComposite.getMultiDBQueryComp().setMainDatabase(database);
			}
		}
		queryPlanItem.setEnabled(enabled);
		setPstmtParaItem.setEnabled(enabled);

		ServerInfo serverInfo = getSelectedServer() == null ? null
				: getSelectedServer().getServerInfo();

		String fontString = QueryOptions.getFontString(serverInfo);
		Font font = ResourceManager.getFont(fontString);
		if (font == null) {
			String[] fontData = QueryOptions.getDefaultFont();
			font = ResourceManager.getFont(fontData[0], Integer.valueOf(fontData[1]),
					Integer.valueOf(fontData[2]));
		}
		int[] fontColor = QueryOptions.getFontColor(serverInfo);
		final Color color = ResourceManager.getColor(fontColor[0], fontColor[1], fontColor[2]);
		// Change SQL editor font
		if (font != null) {
			getSqlTextEditor().setFont(font);
		}
		getSqlTextEditor().setForeground(color);

		for (CombinedQueryEditorComposite combinedQueryEditorComposite : getAllCombinedQueryEditorComposite()) {
			combinedQueryEditorComposite.getSqlEditorComp().getTextViewer().refresh();
			// Change the table result font
			QueryResultComposite queryResultComp = combinedQueryComposite.getQueryResultComp();
			if (queryResultComp == null || queryResultComp.isDisposed()) {
				return;
			}
			Table resultTable = queryResultComp.getResultTable();
			if (resultTable != null && !resultTable.isDisposed()) {
				resultTable.setFont(font);
				resultTable.setForeground(color);
			}
			// Update editor config
			if (database != null) {
				DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, isCMMode);
				if (editorConfig != null && editorConfig.getBackGround() != null) {
					setEditorBackground(ResourceManager.getColor(editorConfig.getBackGround()));
				} else {
					setEditorBackground(ResourceManager.getColor(EditorConstance.getDefaultBackground()));
				}
			}

			//query plan
			combinedQueryComposite.getQueryPlanResultComp().fillPlanHistory();
			//sql history
			combinedQueryComposite.getRecentlyUsedSQLComposite().fillRecentlyUsedSQLList();
			/*Fire database changed*/
			combinedQueryComposite.fireDatabaseChanged(database);
		}
	}

	/**
	 * When navigator node change ,refresh the database list on query editor
	 *
	 * @param event CubridNodeChangedEvent
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}
		String type = cubridNode.getType();
		if (!NodeType.SERVER.equals(type) && !NodeType.DATABASE_FOLDER.equals(type)
				&& !NodeType.DATABASE.equals(type)) {
			return;
		}

		/* Judge the event type */
		if (!CubridNodeChangedEventType.SERVER_CONNECTED.equals(eventType)
				&& !CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(eventType)
				&& !CubridNodeChangedEventType.DATABASE_LOGIN.equals(eventType)
				&& !CubridNodeChangedEventType.DATABASE_LOGOUT.equals(eventType)
				&& !CubridNodeChangedEventType.DATABASE_STOP.equals(eventType)
				&& !CubridNodeChangedEventType.DATABASE_START.equals(eventType)) {
			return;
		}

		synchronized (this) {
			qeToolBar.refresh();
		}
	}

	/**
	 * Format the SQL script
	 */
	public void format() {
		if (combinedQueryComposite != null && !combinedQueryComposite.isDisposed()) {
			combinedQueryComposite.getSqlEditorComp().format();
		}
	}

	/**
	 * JDBC connection commit transaction
	 */
	public void commit() {
		try {
			queryAction(QUERY_ACTION.COMMIT);

		} catch (SQLException ex) {
			CommonUITool.openErrorBox(Messages.bind(
					com.cubrid.common.ui.common.Messages.errCommonTip, ex.getErrorCode(),
					ex.getMessage()));
			commitItem.setEnabled(false);
			rollbackItem.setEnabled(false);
			LOGGER.error("", ex);
		} finally {
			setHaveActiveTransaction(false);
		}
	}

	/**
	 * JDBC connection roll back transaction
	 */
	public void rollback() {
		try {
			queryAction(QUERY_ACTION.ROLLBACK);
		} catch (SQLException ex) {
			CommonUITool.openErrorBox(Messages.bind(
					com.cubrid.common.ui.common.Messages.errCommonTip, ex.getErrorCode(),
					ex.getMessage()));
			commitItem.setEnabled(false);
			rollbackItem.setEnabled(false);
			LOGGER.error("", ex);
		} finally {
			setHaveActiveTransaction(false);
		}
	}

	private StyledText getSqlTextEditor() {
		return combinedQueryComposite.getSqlEditorComp().getText();
	}

	/**
	 * Get sql editor widget, can't use to set/append query
	 *
	 * @return
	 */
	public StyledText getSqlEditorWidget() {
		return combinedQueryComposite.getSqlEditorComp().getText();
	}

	public QueryEditorDNDController getDragController() {
		return dragController;
	}

	public boolean isIncludeOidInfo() {
		return isIncludeOidInfo;
	}

	/**
	 * Open a message dialog to confirm skip query error or not.
	 *
	 * @param ee SQLException
	 * @param errorSql String
	 * @return value boolean[]
	 *
	 */
	public boolean[] queryErrSkipOrNot(final SQLException ee, String errorSql) {
		final boolean[] value = new boolean[2];
		SQLEditorComposite sqlEditorComp = combinedQueryComposite.getSqlEditorComp();
		StyledText txaEdit = sqlEditorComp.getText();
		if (txaEdit != null && !txaEdit.isDisposed()) {
			sqlEditorComp.txtFind(errorSql, 0, false, false, true, false);
			line = txaEdit.getLineAtOffset(txaEdit.getSelection().x) + 1;
		}
		String errorLineMsg = Messages.bind(Messages.errWhere, line);
		String errMsg = Messages.skipOrNot + StringUtil.NEWLINE + StringUtil.NEWLINE
				+ Messages.runError + ee.getErrorCode() + StringUtil.NEWLINE + errorLineMsg
				+ StringUtil.NEWLINE + Messages.errorHead + ee.getMessage();

		MessageDialog dialog = new MessageDialog(QueryEditorPart.this.getEditorSite().getShell(),
				Messages.warning, null, errMsg, MessageDialog.QUESTION, new String[] {
						Messages.btnYes, Messages.btnNo }, 1) {
			Button btn = null;

			protected Control createCustomArea(Composite parent) {
				btn = new Button(parent, SWT.CHECK);
				btn.setText(Messages.showOneTimeTip);
				return btn;
			}

			protected void buttonPressed(int buttonId) {
				value[0] = btn.getSelection();
				if (buttonId == IDialogConstants.CANCEL_ID) {
					value[1] = false;
				} else {
					value[1] = true;
				}
				close();
			}

		};
		dialog.open();

		return value;
	}

	/**
	 * get current select combinedQueryComposite
	 *
	 * @return
	 */
	public CombinedQueryEditorComposite getCombinedQueryComposite() {
		return combinedQueryComposite;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setIsRunning(boolean status) {
		isRunning = status;
	}

	public enum QUERY_ACTION {
		COMMIT, ROLLBACK, CLOSE, AUTOCOMMIT
	}

	public void queryAction(QUERY_ACTION action, Object... args) throws SQLException {
		if (connection == null) {
			return;
		}
		switch (action) {
		case COMMIT:
			if (!connection.isClosed()) {
				connection.commit();
			}
			break;
		case ROLLBACK:
			if (!connection.isClosed()) {
				connection.rollback();
			}
			break;
		case CLOSE:
			if (!connection.isClosed()) {
				connection.close();
			}
			break;
		case AUTOCOMMIT:
			if (args == null || args.length == 0) {
				return;
			}
			if (!connection.isClosed()) {
				connection.setAutoCommit(Boolean.parseBoolean(args[0].toString()));
			}
			break;
		}
	}

	public void setRunItemStatus(boolean b) {
		runItem.setEnabled(b);
		queryPlanItem.setEnabled(b);
		setMultiQueryRunItemStatus(b);
	}

	public void setMultiQueryRunItemStatus(boolean b) {
		multiRunItem.setEnabled(b);
	}

	/**
	 * Set editor background
	 *
	 * @param color
	 */
	public void setEditorBackground(Color color) {
		for (CombinedQueryEditorComposite combinedQueryEditorComposite : getAllCombinedQueryEditorComposite()) {
			combinedQueryEditorComposite.getSqlEditorComp().setBackground(color);
		}
	}

	/**
	 * Assign the part name
	 *
	 * @param name
	 * @param isAlwaysUse
	 */
	public void assignName(String name, boolean isAlwaysUse) {
		editorTabNameOriginal = null;
		changeQueryEditorPartName(name);
		changeQueryEditorPartNameWithShard();
		isUpdatePartName = !isAlwaysUse;
	}

	public boolean isTuningModeButton() {
		return tuneModeItem.getSelection();
	}

	public void setTuningModeButton(boolean isTune) {
		tuneModeItem.setSelection(isTune);
	}

	private void changeCollectExecStats(boolean tuningMode) {
		Connection conn = null;
		try {
			conn = connection.getConnection(false);
		} catch (SQLException ex) {
			CommonUITool.openErrorBox(ex.getLocalizedMessage());
			return;
		}
		CubridUtil.changeCollectExecStats(conn, tuningMode);
		collectExecStats = tuningMode;
	}

	public void beginCollectExecStats() {
		Connection conn = null;
		try {
			conn = connection.getConnection(false);
		} catch (SQLException ex) {
			CommonUITool.openErrorBox(ex.getLocalizedMessage());
			return;
		}
		CubridUtil.beginCollectExecStats(conn);
	}

	public void setCombinedQueryEditortabFolderSelecton(int index) {
		combinedQueryEditortabFolder.setSelection(index);
	}

	public int getShardId() {
		return shardId;
	}

	public void setShardId(int shardId) {
		this.shardId = shardId;
	}

	public int getShardVal() {
		return shardVal;
	}

	public void setShardVal(int shardVal) {
		this.shardVal = shardVal;
	}

	public int getDefaultShardQueryType() {
		return shardQueryType;
	}

	public void setShardQueryType(int defaultShardQueryType) {
		this.shardQueryType = defaultShardQueryType;
	}

	public EditorToolBar getQeToolBar() {
		return qeToolBar;
	}

	public void copySelectedItems() {
		result.copySelectedItems();

	}

	public void copyAllItems() {
		result.copyAllItems();
	}

	public static String makeSqlErrorOnResult(int index, String sql, Exception ee) {
		StringBuilder logs = new StringBuilder();
		logs.append(Messages.bind(Messages.querySeq, StringUtil.getOrdinalFromCardinalNumber(index+1))).append(" ");
		logs.append(Messages.queryFail);
		logs.append(StringUtil.NEWLINE);
		logs.append(makeSqlLogOnResult(sql));

		if (ee instanceof SQLException) {
			logs.append(Messages.runError).append(" ").append(((SQLException) ee).getErrorCode()).append(
					StringUtil.NEWLINE);
		}

		if (ee.getMessage() != null) {
			logs.append(ee.getMessage()).append(StringUtil.NEWLINE);
		}

		logs.append(StringUtil.NEWLINE).append(StringUtil.NEWLINE);

		return logs.toString();
	}

	public List<CombinedQueryEditorComposite> getAllCombinedQueryEditorComposite() {
		List<CombinedQueryEditorComposite> combinedQueryEditorCompositeList = new ArrayList<CombinedQueryEditorComposite>();

		CTabItem[] items = combinedQueryEditortabFolder.getItems();
		for (CTabItem item : items) {
			if (item instanceof SubQueryEditorTabItem) {
				CombinedQueryEditorComposite combinedQueryEditorComposite = ((SubQueryEditorTabItem) item).getControl();
				combinedQueryEditorCompositeList.add(combinedQueryEditorComposite);
			}
		}

		return combinedQueryEditorCompositeList;
	}

	public static String makeSqlLogOnResult(String sql) {
		StringBuilder logs = new StringBuilder();
		logs.append(QueryUtil.SPLIT_LINE_FOR_QUERY_RESULT);
		logs.append(StringUtil.NEWLINE);
		logs.append(sql).append(StringUtil.NEWLINE);
		logs.append(StringUtil.NEWLINE).append(StringUtil.NEWLINE);
		return logs.toString();
	}

	/**
	 * properm text changed
	 */
	public void textChanged(TextEvent event) {
		String query = null;
		if (combinedQueryComposite != null && !combinedQueryComposite.isDisposed()
				&& combinedQueryComposite.getSqlEditorComp() != null
				&& !combinedQueryComposite.getSqlEditorComp().isDisposed()) {
			query = combinedQueryComposite.getSqlEditorComp().getText().getText();
		}
		setRunItemStatus(StringUtil.isNotEmpty(query));
	}

	public CubridDatabase getDatabase() {
		return getSelectedDatabase();
	}

	public DatabaseInfo getDatabaseInfo() {
		return getSelectedDatabase() == null ? null : getSelectedDatabase().getDatabaseInfo();
	}

	public ServerInfo getServerInfo() {
		return getDatabaseInfo() == null ? null : getDatabaseInfo().getServerInfo();
	}

	public String getCharset() {
		if (combinedQueryComposite == null || combinedQueryComposite.getSqlEditorComp() == null) {
			return StringUtil.getDefaultCharset();
		}
		return combinedQueryComposite.getSqlEditorComp().getCharset();
	}

	public void disconnectAndDisposeResults() {
		try {
			System.err.println(">> disconnectAndDisposeResults.StackTrace");
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				System.err.println(">>\tat " + trace[i]);
			}
		} catch (Exception ignored) {
			noOp();
		}
		try {
			combinedQueryComposite.getQueryResultComp().disposeAllResult();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		connection.closeForDebug();
	}

	public String getMessages() {
		CTabItem tabItem = combinedQueryComposite.getQueryResultComp().getQueryResultTabFolder().getSelection();
		if (tabItem != null && tabItem instanceof QueryTableTabItem) {
			QueryTableTabItem queryTableTabItem = (QueryTableTabItem) tabItem;
			QueryResultTableCalcInfo queryResultTableCalcInfo = queryTableTabItem.getQueryResultTableCalcInfo();
			if (queryResultTableCalcInfo != null) {
				StringBuffer sb = new StringBuffer();
				sb.append(Messages.msgCalcInfoCount).append(" ").append(
						queryResultTableCalcInfo.getCount());
				if (queryResultTableCalcInfo.isHasSum()) {
					sb.append(", ").append(Messages.msgCalcInfoSUM).append(" ").append(
							queryResultTableCalcInfo.getSummary());
					sb.append(", ").append(Messages.msgCalcInfoAVG).append(" ").append(
							queryResultTableCalcInfo.getAverage());
				}
				return sb.toString();
			}
		}
		return null;
	}

	public Set<String> getDecoratorWords() {
		Set<String> keyWords = new HashSet<String>();
		keyWords.add(Messages.msgCalcInfoCount);
		keyWords.add(Messages.msgCalcInfoSUM);
		keyWords.add(Messages.msgCalcInfoAVG);
		return keyWords;
	}

	public String getSelectedText() {
		return combinedQueryComposite.getSqlEditorComp().getText().getSelectionText();
	}
}
