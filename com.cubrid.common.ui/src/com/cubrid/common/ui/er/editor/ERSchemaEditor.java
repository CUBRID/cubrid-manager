/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution. 
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GEFPlugin;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.StackAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.compare.schema.TableSchemaComparator;
import com.cubrid.common.ui.compare.schema.TableSchemaCompareUtil;
import com.cubrid.common.ui.compare.schema.control.TableSchemaCompareInfoPart;
import com.cubrid.common.ui.compare.schema.control.TableSchemaCompareModelInputLazy;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.compare.schema.model.TableSchemaModel;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlDatabaseInfoMapper;
import com.cubrid.common.ui.cubrid.database.erwin.WrappedDatabaseInfo;
import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.SchemaContextMenuProvider;
import com.cubrid.common.ui.er.SchemaEditorInput;
import com.cubrid.common.ui.er.SchemaPaletteViewerProvider;
import com.cubrid.common.ui.er.ValidationGraphicalViewer;
import com.cubrid.common.ui.er.action.AddColumnAction;
import com.cubrid.common.ui.er.action.DeleteAction;
import com.cubrid.common.ui.er.action.EditTableAction;
import com.cubrid.common.ui.er.action.ImportERwinDataAction;
import com.cubrid.common.ui.er.action.ModifyTableNameAction;
import com.cubrid.common.ui.er.control.ExportDataController;
import com.cubrid.common.ui.er.control.ExportERwinDataController;
import com.cubrid.common.ui.er.control.ExportImportGsonDataController;
import com.cubrid.common.ui.er.control.ExportPictureController;
import com.cubrid.common.ui.er.control.ExportSQLDataController;
import com.cubrid.common.ui.er.control.ImportERwinDataController;
import com.cubrid.common.ui.er.dialog.ExportERDataDialog;
import com.cubrid.common.ui.er.dialog.ImportERDataDialog;
import com.cubrid.common.ui.er.dialog.SetPhysicalLogicaMapDialog;
import com.cubrid.common.ui.er.directedit.StatusLineValidationMessageHandler;
import com.cubrid.common.ui.er.dnd.ERDNDController;
import com.cubrid.common.ui.er.figures.TableFigure;
import com.cubrid.common.ui.er.loader.ERSchemaTableNodesLoader;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.ERVirtualDatabase;
import com.cubrid.common.ui.er.model.ERVirtualDatabaseInfo;
import com.cubrid.common.ui.er.part.PartFactory;
import com.cubrid.common.ui.er.part.SchemaDiagramPart;
import com.cubrid.common.ui.er.part.TablePart;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.Collation;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllSchemaTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetCollations;
import com.cubrid.cubridmanager.core.cubrid.table.task.UpdateDescriptionTask;

/**
 * ERD editor. Implemented {@code GraphicalEditor} and composed a palette in it.
 * 
 * @author Yu Guojia
 * @version 1.0 - 2013-5-8 created by Yu Guojia
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ERSchemaEditor extends
		GraphicalEditor implements
		CommandStackListener,
		ISelectionListener,
		ICubridNodeChangedListener {
	public static final String ID = "com.cubrid.common.ui.er.editor.SchemaEditor";
	private final Logger LOGGER = LogUtil.getLogger(getClass());
	private CubridDatabase database;
	private ERSchema erSchema;
	private ERSchemaToolBar erToolBar;
	protected ICubridNode cubridNode;
	private ERSchemaTableNodesLoader tableNodesLoader;
	private PropertySheetPage undoablePropertySheetPage;
	private GraphicalViewer graphicalViewer;
	private List editPartActionIDs = new ArrayList();
	private List stackActionIDs = new ArrayList();
	private List editorActionIDs = new ArrayList();
	private ActionRegistry actionRegistry;
	private ERSchemaEditDomain editDomain;
	private boolean isDirty;
	private Composite topPane;
	private Composite canvasViewComp;
	private ERDNDController dndController;
	private ExportDataController dataExporter;
	private ZoomManager zoomManager;
	private static final double[] ZOOM_LEVELS = { 0.1, 0.25, 0.5, 0.75, 0.9, 1, 1.1, 1.25, 1.5, 2,
			4 };

	private String oldSearchKey = "";
	public static final String DBAUSER_NAME = "dba";

	public Control getGraphicalControl() {
		return getGraphicalViewer().getControl();
	}

	public ERSchemaEditor() {
		editDomain = new ERSchemaEditDomain(this);
		setEditDomain(editDomain);
	}

	public ERSchemaEditDomain getEditDomain() {
		return editDomain;
	}

	public void setEditDomain(ERSchemaEditDomain editDomain) {
		super.setEditDomain(editDomain);
		this.editDomain = editDomain;
	}

	protected void initializeGraphicalViewer() {
		IEditorInput input = this.getEditorInput();
		if (input instanceof ICubridNode) {
			ICubridNode node = (ICubridNode) input;
			ERSchema erSchema = (ERSchema) node.getAdapter(ERSchema.class);
			getGraphicalViewer().setContents(erSchema);
		}
	}

	public void createPartControl(Composite parent) {
		createToolBar(parent);
		canvasViewComp = new Composite(topPane, SWT.NONE);
		FillLayout flayout = new FillLayout();
		canvasViewComp.setLayout(flayout);
		canvasViewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		super.createPartControl(canvasViewComp);
		setDirty(false);
	}

	public void createToolBar(Composite parent) {
		ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		FillLayout flayout = new FillLayout();
		scrolledComp.setLayout(flayout);

		topPane = new Composite(scrolledComp, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		topPane.setLayout(gridLayout);
		topPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		scrolledComp.setContent(topPane);
		scrolledComp.setExpandHorizontal(true);
		scrolledComp.setExpandVertical(true);

		final Composite toolBarComposite = new Composite(topPane, SWT.NONE);
		toolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.marginHeight = 0;
		gridLayout2.horizontalSpacing = 0;
		gridLayout2.marginWidth = 0;
		toolBarComposite.setLayout(gridLayout2);
		erToolBar = new ERSchemaToolBar(toolBarComposite, SWT.WRAP | SWT.FLAT, this);
		erToolBar.init();

		topPane.pack();
		erToolBar.pack();
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		String name = input.getName();
		if (input instanceof SchemaEditorInput) {
			name += "@" + ((SchemaEditorInput) input).getDatabase().getName();
		}
		this.setPartName(name);
		// store site and input
		setSite(site);
		setInput(input);
		getEditDomain().setPaletteRoot(getPaletteRoot());
		setTitleImage(input.getImageDescriptor().createImage());

		getCommandStack().addCommandStackListener(this);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		createActions();

		this.getSite().getPage().addPartListener(new IPartListener() {

			public void partOpened(IWorkbenchPart part) {
				if (part == ERSchemaEditor.this) {
					CommonUITool.activeView(ERDThumbnailViewPart.ID);
				}
			}

			public void partDeactivated(IWorkbenchPart part) {
			}

			public void partClosed(IWorkbenchPart part) {
				if (part == ERSchemaEditor.this) {
					ERDThumbnailViewPart view = (ERDThumbnailViewPart) CommonUITool.findView(ERDThumbnailViewPart.ID);
					if (view != null) {
						view.redraw(null);
					}
				}
			}

			public void partBroughtToTop(IWorkbenchPart part) {
			}

			public void partActivated(IWorkbenchPart part) {
				if (part == ERSchemaEditor.this) {
					ERDThumbnailViewPart view = (ERDThumbnailViewPart) CommonUITool.findView(ERDThumbnailViewPart.ID);
					if (view != null) {
						view.redraw(getRootEditPart());
					}
				}
			}
		});

		if (database != null && !database.isVirtual()) {
			CubridNodeManager.getInstance().addCubridNodeChangeListener(this);
		}
	}
	
	/**
	 * Open dialog that sets global view model.
	 */
	public void openPhysicalLogicalMapDialog() {
		SetPhysicalLogicaMapDialog dialog = new SetPhysicalLogicaMapDialog(getGraphicalControl().getShell(), erSchema);
		int returnvalue = dialog.open();
		if (returnvalue != IDialogConstants.OK_ID) {
			return;
		}
		erSchema.FireModelRelationChanged();
		
	}

	/**
	 * Open a ERwin file
	 */
	public void doOpen() {
		if (!CommonUITool.openConfirmBox(Messages.msgConfirmLoadERFile)) {
			return;
		}

		ImportERDataDialog dialog = new ImportERDataDialog(getGraphicalControl().getShell(),
				erSchema);
		int returnvalue = dialog.open();
		if (returnvalue != IDialogConstants.OK_ID) {
			return;
		}

		boolean success = false;
		if (dialog.isERWinFile()) {
			ImportERwinDataController importControl = new ImportERwinDataController(erSchema);
			success = importControl.importERwinData(getGraphicalControl().getShell(),
					dialog.getERWinContainer());
		} else if (dialog.isGsonFile()) {
			ExportImportGsonDataController gsonDataController = new ExportImportGsonDataController(
					erSchema);
			success = gsonDataController.importGsonData(getGraphicalControl().getShell(),
					dialog.getGsonData());
		}

		if (success) {
			// clear the exporter, the exported data as a new ERD editor
			dataExporter = null;
			setLocatePoint(0, 0);
		}
	}

	/**
	 * sync table comments to db tables.
	* 
	* @return void
	 */
	public void syncComments() {
		DatabaseInfo info = database.getDatabaseInfo();
		if (info == null) {
			CommonUITool.openErrorBox(Messages.errNoDatabase);
			return;
		}
		if (!info.isLogined()) {
			CommonUITool.openErrorBox(Messages.msgDBNotLogin);
			return;
		}
		ServerInfo serverInfo = info.getServerInfo();
		if (serverInfo != null && !serverInfo.isConnected()) {
			CommonUITool.openErrorBox(Messages.msgDBNotLogin);
			return;
		}
		boolean isSupportTableComment = false;
		Connection conn = null;
		try {
			conn = JDBCConnectionManager.getConnection(info, false);
			isSupportTableComment = SchemaCommentHandler.isInstalledMetaTable(info, conn);
			if(!isSupportTableComment){
				CommonUITool.openErrorBox(Messages.cannotSyncComments);
				return;
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn);
		}
		
		if(!CommonUITool.openConfirmBox(getSite().getShell(), Messages.bind(Messages.msgConfirmSyncComments, database.getLabel()))){
			return;
		}
		CommonTaskExec taskJobExec = new CommonTaskExec(null);
		List<UpdateDescriptionTask> updateDescTasks = getUpdateDescriptionTaskList(info);
		for (UpdateDescriptionTask task : updateDescTasks) {
			taskJobExec.addTask(task);
		}
		if(updateDescTasks.size() == 0){
			CommonUITool.openInformationBox(Messages.msgNoComments);
			return;
		}
		
		new ExecTaskWithProgress(taskJobExec).exec();
		Set<String> failTables = new HashSet<String>();
		for (UpdateDescriptionTask task : updateDescTasks) {
			if(!task.isSuccess()){
				failTables.add(task.getTableName());
			}
		}
		if (failTables.size() == 0) {
			CommonUITool.openInformationBox(Messages.msgSuccessSyncComments);
		}else{
			CommonUITool.openErrorBox(Messages.bind(Messages.errSyncComments, failTables.toString()));
		}
	}
	
	/**
	 * Get UpdateDescriptionTaskList
	 *
	 * @param dbInfo
	 * @return
	 */
	private List<UpdateDescriptionTask> getUpdateDescriptionTaskList(DatabaseInfo dbInfo){
		List<UpdateDescriptionTask> updateDescriptionTaskList = new ArrayList<UpdateDescriptionTask>();
		Map<String, SchemaInfo> schemaInfos = erSchema.getAllSchemaInfo();
		Collection<SchemaInfo> erdSchemaInfos = schemaInfos.values();
		GetAllSchemaTask task = new GetAllSchemaTask(dbInfo);
		task.execute();
		
		Map<String, SchemaInfo> dbSchemaInfoMap = task.getSchemas();
		String taskName = com.cubrid.common.ui.cubrid.table.Messages.updateDescriptionTask;
		for (SchemaInfo newSchemaInfo : erdSchemaInfos) {
			String tableName = newSchemaInfo.getClassname();
			SchemaInfo dbSchemaInfo = dbSchemaInfoMap.get(tableName);
			if (dbSchemaInfo == null) {
				continue;
			}
			for (DBAttribute newAttr : newSchemaInfo.getAttributes()) {
				DBAttribute oldAttr = dbSchemaInfo.getDBAttributeByName(newAttr.getName(), newAttr.isClassAttribute());
				if (oldAttr == null || StringUtil.isEqual(oldAttr.getDescription(), newAttr.getDescription())) {
					continue;
				}
				updateDescriptionTaskList.add(new UpdateDescriptionTask(taskName, dbInfo, tableName, newAttr.getName(),newAttr.getDescription()));
			}
			if (!StringUtil.isEqual(dbSchemaInfo.getDescription(), newSchemaInfo.getDescription())) {
				updateDescriptionTaskList.add(new UpdateDescriptionTask(taskName, dbInfo, tableName, "",newSchemaInfo.getDescription()));
			}
		}

		return updateDescriptionTaskList;
	}
	
	public void compareDDL2DB() {
		DatabaseInfo info = database.getDatabaseInfo();
		if (info == null) {
			CommonUITool.openErrorBox(Messages.errNoDatabase);
			return;
		}
		if (!info.isLogined()) {
			CommonUITool.openErrorBox(Messages.msgDBNotLogin);
			return;
		}
		ServerInfo serverInfo = info.getServerInfo();
		if (serverInfo != null && !serverInfo.isConnected()) {
			CommonUITool.openErrorBox(Messages.msgDBNotLogin);
			return;
		}

		Map<String, SchemaInfo> schemaInfos = erSchema.getAllSchemaInfo();
		Map<String, TableSchema> tableSchemas = new HashMap<String, TableSchema>();

		WrappedDatabaseInfo wrappedDatabaseInfo = new WrappedDatabaseInfo(info.getDbName(),
				info.getServerInfo());

		ERXmlDatabaseInfoMapper.addWrappedDatabaseInfo(info, wrappedDatabaseInfo);
		wrappedDatabaseInfo.addSchemaInfos(schemaInfos);

		SchemaDDL ddl = new SchemaDDL(null, wrappedDatabaseInfo);
		for (String tableName : schemaInfos.keySet()) {

			SchemaInfo schemaInfo = schemaInfos.get(tableName);
			if (schemaInfo == null || !tableName.equals(schemaInfo.getClassname())) {
				continue;
			}

			// now do not support view table
			String strDDL = ddl.getSchemaDDL(schemaInfo, true, true);

			TableSchema tableSchema = new TableSchema(tableName, "");
			tableSchema.setSchemaInfo(strDDL);
			tableSchemas.put(tableSchema.getName(), tableSchema);
		}

		wrappedDatabaseInfo.addTableSchemas(tableSchemas);

		compareTableSchemas(getEditorInput().getName(), tableSchemas, schemaInfos);
	}

	public void compareTableSchemas(final String modelName,
			final Map<String, TableSchema> tableSchema, final Map<String, SchemaInfo> schemaInfos) {
		final List<TableSchemaCompareModelInputLazy> input = new ArrayList<TableSchemaCompareModelInputLazy>();
		ITask reportBugTask = new AbstractUITask() {
			private boolean success = false;

			public void cancel() {
			}

			public void finish() {
			}

			public boolean isCancel() {
				return false;
			}

			public boolean isSuccess() {
				return success;
			}

			public void execute(IProgressMonitor monitor) {

				CubridDatabase database = erSchema.getCubridDatabase();
				List<TableDetailInfo> leftDbTableInfoList = TableSchemaCompareUtil.getTableInfoList(database);
				final CubridDatabase virtualDb = new CubridDatabase(modelName, modelName);
				virtualDb.setVirtual(true);

				DatabaseInfo info = database.getDatabaseInfo();
				virtualDb.setDatabaseInfo(info);
				WrappedDatabaseInfo wrappedDatabaseInfo = new WrappedDatabaseInfo(info);
				wrappedDatabaseInfo.addSchemaInfos(schemaInfos);
				wrappedDatabaseInfo.addTableSchemas(tableSchema);
				ERXmlDatabaseInfoMapper.addWrappedDatabaseInfo(info, wrappedDatabaseInfo);

				TableSchemaModel leftModel = TableSchemaCompareUtil.createTableSchemaModel(leftDbTableInfoList);
				TableSchemaModel rightModel = new TableSchemaModel();

				rightModel.getTableSchemaMap().putAll(tableSchema);

				TableSchemaComparator comparator = new TableSchemaComparator(database, virtualDb);

				TableSchemaCompareModel model = comparator.compare(leftModel, rightModel);
				model.setSourceDB(database);
				model.setTargetDB(virtualDb);
				input.add(new TableSchemaCompareModelInputLazy(model));
				success = true;
			}
		};

		TaskExecutor taskExecutor = new CommonTaskExec(
				com.cubrid.common.ui.common.Messages.titleSchemaComparison);
		taskExecutor.addTask(reportBugTask);
		new ExecTaskWithProgress(taskExecutor).exec();
		if (taskExecutor.isSuccess()) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
						input.get(0), TableSchemaCompareInfoPart.ID);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Generate "insert" and "update" SQLs to editing query tabs.
	* 
	* @return void
	 * @throws PartInitException 
	 */
	public void generateSyncCommentSQL() throws PartInitException, SQLException {
		Connection conn = null;
		StringBuffer allInsertSqls = null;
		StringBuffer allUpdateSqls = null;
		StringBuffer alterSqls = null;	// for version of 10.x
		Map<String, SchemaInfo> schemaInfos = erSchema.getAllSchemaInfo();
		Collection<SchemaInfo> erdSchemaInfos = schemaInfos.values();
		SqlFormattingStrategy formater = new SqlFormattingStrategy();
		DatabaseInfo info = database.getDatabaseInfo();
		final boolean isSupportOnServer = CompatibleUtil.isCommentSupports(info);

		if (isSupportOnServer) {
			conn = JDBCConnectionManager.getConnection(info, true);
			alterSqls = new StringBuffer();
		} else {
			allInsertSqls = new StringBuffer();
			allUpdateSqls = new StringBuffer();
		}

		for (SchemaInfo newSchemaInfo : erdSchemaInfos) {
			boolean isAddLine = false;
			String tableName = newSchemaInfo.getClassname();

			if(StringUtil.isNotEmpty(newSchemaInfo.getDescription())){
				if (isSupportOnServer) {
					String description = String.format("'%s'", newSchemaInfo.getDescription());
					String sql = SchemaCommentHandler.generateDescriptionSql(conn,
							tableName, "*", StringUtil.escapeQuotes(description));
					appendFormattingSQL(alterSqls, sql, formater);
				} else {
					String insertSql = SchemaCommentHandler.buildInsertSQL(tableName, "", newSchemaInfo.getDescription());
					appendFormattingSQL(allInsertSqls, insertSql, formater);
					String updateSql = SchemaCommentHandler.buildUpdateSQL(tableName, "", newSchemaInfo.getDescription());
					appendFormattingSQL(allUpdateSqls, updateSql, formater);
				}
				isAddLine = true;
			}

			for (DBAttribute newAttr : newSchemaInfo.getAttributes()) {
				if(StringUtil.isNotEmpty(newAttr.getDescription())){
					if (isSupportOnServer) {
						String description = String.format("'%s'", newAttr.getDescription());
						String sql = SchemaCommentHandler.generateDescriptionSql(conn,
								tableName, newAttr.getName(), StringUtil.escapeQuotes(description));
						appendFormattingSQL(alterSqls, sql, formater);
					} else {
						String insertSql = SchemaCommentHandler.buildInsertSQL(tableName, newAttr.getName(), newAttr.getDescription());
						appendFormattingSQL(allInsertSqls, insertSql, formater);
						String updateSql = SchemaCommentHandler.buildUpdateSQL(tableName, newAttr.getName(), newAttr.getDescription());
						appendFormattingSQL(allUpdateSqls, updateSql, formater);
					}
					isAddLine = true;
				}
			}
			if(isAddLine){
				if(isSupportOnServer) {
					alterSqls.append("\n\r");
				} else {
					allInsertSqls.append("\n\r");
				}
			}
		}

		if((isSupportOnServer && alterSqls.length() < 1)	// since version of 10.x
				|| (!isSupportOnServer) && allInsertSqls.length() < 1) {	// before version of 10.x
			CommonUITool.openInformationBox(Messages.msgNoComments);
			return;
		}

		final QueryEditorPart editPart = CommonUITool.openQueryEditor(database,false);
		if(isSupportOnServer) {
			editPart.setQuery(alterSqls.toString(), true, false, false);
		} else {
			editPart.setQuery(allInsertSqls.toString(), true, false, false);
			editPart.newQueryTab(allUpdateSqls.toString(), false);
		}
		UIJob job = new UIJob("") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				String tabMessage = isSupportOnServer ? Messages.edittabNameAlterSQL : Messages.edittabNameInsertSQL;
				List<CombinedQueryEditorComposite>  allTabItems = editPart.getAllCombinedQueryEditorComposite();
				editPart.updateTabName(allTabItems.get(0).getSubQueryEditorTabItem(), tabMessage, true);
				if (allTabItems.size() > 1) {
					editPart.updateTabName(allTabItems.get(1).getSubQueryEditorTabItem(), Messages.edittabNameUpdateSQL, true);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(UIJob.BUILD);
		job.schedule();
	}
	
	private void appendFormattingSQL(StringBuffer allSqls, String sql, SqlFormattingStrategy formater){
		sql = formater.format(sql).trim();
		allSqls.append(sql);
		allSqls.append("\n\r");
	}
	
	public void registDropTarget() {
		dndController = new ERDNDController(this);
		dndController.registerDropTarget();
	}

	/**
	 * set database node
	 * 
	 * @return CubridDatabase
	 */
	public void setDatabase(CubridDatabase db) {
		database = db;
		SchemaEditorInput input = (SchemaEditorInput) getEditorInput();
		input.setDatabase(database);
		setPartName(input.getName() + "@" + database.getName());
	}
	
	/**
	 * Get database node
	 * 
	 * @return CubridDatabase
	 */
	public CubridDatabase getDatabase() {
		return database;
	}

	/**
	 * Change part name
	 * 
	 * @param String partName
	 */
	public void changePartName(String partName) {
		setPartName(partName);
	}

	/**
	 * The selection listener implementation
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		updateActions(editPartActionIDs);
	}

	/**
	 * Set all tables to be selected.
	 */
	public void setAllTableSelected() {
		graphicalViewer.setFocus(null);

		SchemaDiagramPart schemaRootPart = getERSchemaRootPart();
		List allParts = schemaRootPart.getChildren();
		StructuredSelection allTables = new StructuredSelection(allParts);
		graphicalViewer.setSelection(allTables);
	}

	/**
	 * When user click the empty space on the ERD canvas, then all the table
	 * should be unselected and background color should be reset to default.
	 */
	public void setAllFiguresOrigin() {
		graphicalViewer.setFocus(null);
		graphicalViewer.deselectAll();
		SchemaDiagramPart schemaRootPart = getERSchemaRootPart();
		List allParts = schemaRootPart.getChildren();
		Iterator it = allParts.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!(obj instanceof TablePart)) {
				continue;
			}
			TablePart tablePart = (TablePart) obj;
			tablePart.setSelected(TablePart.SELECTED_NONE);
			tablePart.getFigure().setBackgroundColor(TableFigure.defaultBackgroundColor);
		}
	}

	/**
	 * Search tables by key word
	 * 
	 * @param keyWord
	 */
	public void doSearchTables(String keyWord) {
		boolean isChanged = !oldSearchKey.equalsIgnoreCase(keyWord);
		oldSearchKey = keyWord;

		if (StringUtil.isEmpty(keyWord) && !isChanged) {
			return;
		}

		if (isChanged) {
			setAllFiguresOrigin();
		}
		searchAndLocate(keyWord, isChanged);
	}

	/**
	 * The <code>CommandStackListener</code> that listens for
	 * <code>CommandStack </code> changes.
	 */
	public void commandStackChanged(EventObject event) {
		updateActions(stackActionIDs);
		setDirty(getCommandStack().isDirty());
	}

	/**
	 * Returns the <code>GraphicalViewer</code> of this editor.
	 * 
	 * @return the <code>GraphicalViewer</code>
	 */
	public GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}

	public ScalableFreeformRootEditPart getRootEditPart() {
		return (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();
	}

	public SchemaDiagramPart getERSchemaRootPart() {
		return (SchemaDiagramPart) graphicalViewer.getRootEditPart().getContents();
	}

	public Viewport getViewPort() {
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();
		return (Viewport) rootEditPart.getFigure();
	}

	public FigureCanvas getCanvas() {
		FigureCanvas canvas = (FigureCanvas) getGraphicalViewer().getControl();
		return canvas;
	}

	public int getHorizontalScrollWidth() {
		FigureCanvas canvas = getCanvas();
		return canvas.getViewport().getHorizontalRangeModel().getValue();
	}

	public int getVerticalScrollHeight() {
		FigureCanvas canvas = getCanvas();
		return canvas.getViewport().getVerticalRangeModel().getValue();
	}

	/**
	 * Set matched tables to be selected, and next(or first) finding table to be
	 * focused
	 * 
	 * @param keyWord
	 * @param isChangedKey
	 * @return
	 */
	public boolean searchAndLocate(String keyWord, boolean isChangedKey) {
		SchemaDiagramPart schemaRootPart = getERSchemaRootPart();
		List allParts = schemaRootPart.getChildren();

		// set next focus table(focus circularly )
		if (!isChangedKey && graphicalViewer.getFocusEditPart() instanceof TablePart) {
			TablePart focusedTablePart = (TablePart) graphicalViewer.getFocusEditPart();
			int preFocusIndex = allParts.indexOf(focusedTablePart);
			int start = preFocusIndex + 1;
			for (; start < allParts.size(); start++) {
				Object table = allParts.get(start);
				if (table instanceof TablePart) {
					TablePart nexFocusTable = (TablePart) table;
					if (nexFocusTable.getTable().getShownName().contains(keyWord)) {
						getViewPort().setViewLocation(nexFocusTable.getFigure().getBounds().x,
								nexFocusTable.getFigure().getBounds().y);
						graphicalViewer.setFocus(nexFocusTable);
						return true;
					}
				}
				if (start == allParts.size()) {
					start = 0;
				}
			}
		}

		// set first focus table and gray tables
		Iterator it = allParts.iterator();
		List matchedList = new ArrayList();
		boolean isFocus = false;
		while (it.hasNext()) {
			Object obj = it.next();
			if (!(obj instanceof TablePart)) {
				continue;
			}
			TablePart tablePart = (TablePart) obj;
			ERTable erTable = tablePart.getTable();
			if (erTable.getShownName().contains(keyWord)) {
				matchedList.add(tablePart);
				if (!isFocus) {
					getViewPort().setViewLocation(tablePart.getFigure().getBounds().x,
							tablePart.getFigure().getBounds().y);
					graphicalViewer.setFocus(tablePart);
					isFocus = true;
				}
			} else {
				TableFigure figure = (TableFigure) tablePart.getFigure();
				figure.setDisabled(true);
			}

		}
		StructuredSelection matchedTables = new StructuredSelection(matchedList);
		graphicalViewer.setSelection(matchedTables);

		return true;
	}

	/**
	 * Set the ERD focus location onto the gave table
	 * 
	 * @param tableName
	 */
	public void setLocateTable(String tableName) {
		SchemaDiagramPart schemaRootPart = getERSchemaRootPart();
		List allParts = schemaRootPart.getChildren();
		Iterator it = allParts.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!(obj instanceof TablePart)) {
				continue;
			}
			TablePart tablePart = (TablePart) obj;
			ERTable erTable = tablePart.getTable();
			if (erTable.getName().equals(tableName)) {
				setLocatePoint(erTable.getBounds().x, erTable.getBounds().y);
			}
		}
	}

	/**
	 * Set the ERD focus location onto the gave point
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocatePoint(int x, int y) {

		int setMax = Math.max(x, y);
		RangeModel recModel = getViewPort().getVerticalRangeModel();
		int max = recModel.getMaximum();
		int extent = recModel.getExtent();
		if (setMax > max - extent) {
			getViewPort().getVerticalRangeModel().setMaximum(setMax + extent);
		}
		getViewPort().setViewLocation(x, y);
	}

	public void dispose() {
		getCommandStack().removeCommandStackListener(this);
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		CubridNodeManager.getInstance().removeCubridNodeChangeListener(this);
		// dispos3 the ActionRegistry (will dispose all actions)
		getActionRegistry().dispose();
		// important: always call super implementation of dispose
		super.dispose();
	}

	/**
	 * Call this method when this editor is focus
	 */
	public void setFocus() {
		if (cubridNode != null) {
			LayoutManager.getInstance().getTitleLineContrItem().changeTitleForViewOrEditPart(
					cubridNode, this);
			LayoutManager.getInstance().getStatusLineContrItem().changeStuatusLineForViewOrEditPart(
					cubridNode, this);
		}
	}

	/**
	 * Close the editors which are the same database
	 * 
	 * @param event CubridNodeChangedEvent
	 * @param database CubridDatabase
	 */
	public void close(CubridNodeChangedEvent event, CubridDatabase database) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}
		if (event.getSource() instanceof CubridDatabase) {
			CubridDatabase eventCubridDatabase = (CubridDatabase) event.getSource();
			if (eventCubridDatabase.equals(database)) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null) {
					return;
				}
				window.getActivePage().closeEditor(this, true);
			}
		}
	}

	/**
	 * Close the editors which are the same server
	 * 
	 * @param event CubridNodeChangedEvent
	 * @param database CubridServer
	 */
	public void close(CubridNodeChangedEvent event, CubridServer server) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}
		if (event.getSource() instanceof CubridServer) {
			CubridServer eventCubridServer = (CubridServer) event.getSource();
			if (eventCubridServer.equals(server)) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null) {
					return;
				}
				window.getActivePage().closeEditor(this, true);
			}
		}
	}

	/**
	 * Adaptable implementation for Editor
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == GraphicalViewer.class || adapter == EditPartViewer.class)
			return getGraphicalViewer();
		else if (adapter == CommandStack.class)
			return getCommandStack();
		else if (adapter == EditDomain.class)
			return getEditDomain();
		else if (adapter == ActionRegistry.class)
			return getActionRegistry();
		else if (adapter == IPropertySheetPage.class)
			return getPropertySheetPage();

		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();

			Boolean val = (Boolean) getGraphicalViewer().getProperty(
					RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGuides((GraphicalEditPart) this));
			}
			val = (Boolean) getGraphicalViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGeometry((GraphicalEditPart) this));
			}
			val = (Boolean) getGraphicalViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGrid((GraphicalEditPart) this));
			}
			if (snapStrategies.size() == 0) {
				return null;
			}
			if (snapStrategies.size() == 1) {
				return (SnapToHelper) snapStrategies.get(0);
			}
			SnapToHelper[] sth = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++) {
				sth[i] = (SnapToHelper) snapStrategies.get(i);
			}
			return new CompoundSnapToHelper(sth);
		}

		return super.getAdapter(adapter);
	}

	/**
	 * Returns the <code>CommandStack</code> of this editor's
	 * <code>EditDomain</code>.
	 * 
	 * @return the <code>CommandStack</code>
	 */
	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}

	/**
	 * Returns the schema model associated with the editor
	 * 
	 * @return an instance of <code>Schema</code>
	 */
	public ERSchema getSchema() {
		return erSchema;
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof SchemaEditorInput) {
			database = ((SchemaEditorInput) input).getDatabase();
		}

		tableNodesLoader = new ERSchemaTableNodesLoader(database);
		erSchema = new ERSchema(database.getName(), (SchemaEditorInput) input);
		erSchema.setLayoutManualDesiredAndFire(true);
		initCollections(database.getDatabaseInfo());
	}

	/**
	 * Get collections from database, and add a empty collection
	 * 
	 * @param databaseInfo
	 */
	private void initCollections(final DatabaseInfo databaseInfo) {

		if (databaseInfo instanceof ERVirtualDatabaseInfo) {
			ERVirtualDatabaseInfo db = (ERVirtualDatabaseInfo) databaseInfo;
			erSchema.setCollections(db.getCollections());
			return;
		}

		new Thread(new Runnable() {
			public void run() {
				GetCollations collationTask = new GetCollations(databaseInfo);
				collationTask.execute();

				List<Collation> collationList = collationTask.getCollations();
				Collation emptyCollation = new Collation();
				emptyCollation.setCharset("");
				emptyCollation.setName("");
				collationList.add(0, emptyCollation);
				erSchema.setCollections(collationList);
			}
		}).start();
	}

	/**
	 * Creates a PaletteViewerProvider that will be used to create palettes for
	 * the view and the flyout.
	 * 
	 * @return the palette provider
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new SchemaPaletteViewerProvider(editDomain);
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
	}

	/**
	 * Creates a new <code>GraphicalViewer</code>, configures, registers and
	 * initializes it.
	 * 
	 * @param parent the parent composite
	 * @return a new <code>GraphicalViewer</code>
	 */
	protected void createGraphicalViewer(Composite parent) {
		IEditorSite editorSite = getEditorSite();
		ScrollingGraphicalViewer viewer = createGraphicalViewer(editorSite, parent);
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer.getRootEditPart();

		zoomManager = rootEditPart.getZoomManager();
		zoomManager.setZoomLevels(ZOOM_LEVELS);

		// hook the viewer into the EditDomain
		getEditDomain().addViewer(viewer);
		// acticate the viewer as selection provider for Eclipse
		getSite().setSelectionProvider(viewer);
		viewer.setContents(erSchema);

		ContextMenuProvider provider = new SchemaContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(provider);
		getSite().registerContextMenu(provider.getId(), provider, viewer);
		graphicalViewer = viewer;

		// key handler
		GraphicalViewerKeyHandler graphicalViewerKeyHandler = new GraphicalViewerKeyHandler(viewer);
		KeyHandler parentKeyHandler = graphicalViewerKeyHandler.setParent(getCommonKeyHandler());
		viewer.setKeyHandler(parentKeyHandler);

		registDropTarget();
	}

	private ScrollingGraphicalViewer createGraphicalViewer(IEditorSite editorSite, Composite parent) {
		StatusLineValidationMessageHandler messageHandler = new StatusLineValidationMessageHandler(
				editorSite);
		ScrollingGraphicalViewer viewer = new ValidationGraphicalViewer(messageHandler);
		viewer.createControl(parent);
		viewer.getControl().setBackground(ColorConstants.white);
		viewer.setRootEditPart(new ERScalableFreeformRootEditPart());
		viewer.setEditPartFactory(new PartFactory());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, true);
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, true);
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_SPACING, new Dimension(10, 10));
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_ORIGIN, new Point(5, 5));
		return viewer;
	}

	/**
	 * Do zoom in action
	 */
	public void doZoomIn() {
		zoomManager.zoomIn();
	}

	/**
	 * Do zoom out action
	 */
	public void doZoomOut() {
		zoomManager.zoomOut();
	}

	protected KeyHandler getCommonKeyHandler() {
		KeyHandler sharedKeyHandler = new KeyHandler();

		Action action = new ZoomInAction(zoomManager);
		getActionRegistry().registerAction(action);
		action = new ZoomOutAction(zoomManager);
		getActionRegistry().registerAction(action);

		sharedKeyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		sharedKeyHandler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

		sharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
				getActionRegistry().getAction(DeleteAction.ID));
		sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0),
				getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
		sharedKeyHandler.put(KeyStroke.getReleased('', 97, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.SELECT_ALL.getId()));

		getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL),
				MouseWheelZoomHandler.SINGLETON);
		graphicalViewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
				MouseWheelZoomHandler.SINGLETON);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, new Boolean(true));
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, true);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING, new Dimension(10, 10));

		return sharedKeyHandler;
	}

	/**
	 * Sets the dirty state of this editor.
	 * 
	 * <p>
	 * An event will be fired immediately if the new state is different than the
	 * current one.
	 * 
	 * @param dirty the new dirty state to set
	 */
	public void setDirty(boolean dirty) {
		if (isDirty != dirty) {
			isDirty = dirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	/**
	 * Creates actions and registers them to the ActionRegistry.
	 */
	protected void createActions() {
		addStackAction(new UndoAction(this));
		addStackAction(new RedoAction(this));

		addEditPartAction(new DeleteAction(this));// er own deleting action
		addEditPartAction(new ImportERwinDataAction(this));
		addEditPartAction(new ModifyTableNameAction(this));
		addEditPartAction(new AddColumnAction(this));
		addEditPartAction(new EditTableAction(this));
	}

	/**
	 * Adds an <code>EditPart</code> action to this editor.
	 * <code>EditPart</code> actions are actions that depend and work on the
	 * selected <code>EditPart</code>s.
	 * 
	 * @param action the <code>EditPart</code> action
	 */
	protected void addEditPartAction(SelectionAction action) {
		getActionRegistry().registerAction(action);
		editPartActionIDs.add(action.getId());
	}

	/**
	 * Adds an <code>CommandStack</code> action to this editor.
	 * <p>
	 * <code>CommandStack</code> actions are actions that depend and work on the
	 * <code>CommandStack</code>.
	 * 
	 * @param action the <code>CommandStack</code> action
	 */
	protected void addStackAction(StackAction action) {
		getActionRegistry().registerAction(action);
		stackActionIDs.add(action.getId());
	}

	/**
	 * Adds an editor action to this editor.
	 * <p>
	 * Editor actions are actions that depend and work on the editor.
	 * 
	 * @param action the editor action
	 */
	protected void addEditorAction(WorkbenchPartAction action) {
		getActionRegistry().registerAction(action);
		editorActionIDs.add(action.getId());
	}

	/**
	 * Adds an action to this editor's <code>ActionRegistry</code>. (This is a
	 * helper method.)
	 * 
	 * @param action the action to add.
	 */
	protected void addAction(IAction action) {
		getActionRegistry().registerAction(action);
	}

	/**
	 * Updates the specified actions.
	 * 
	 * @param actionIds the list of ids of actions to update
	 */
	protected void updateActions(List actionIds) {
		for (Iterator ids = actionIds.iterator(); ids.hasNext();) {
			IAction action = getActionRegistry().getAction(ids.next());
			if (null != action && action instanceof UpdateAction)
				((UpdateAction) action).update();

		}
	}

	/**
	 * Returns the action registry of this editor.
	 * 
	 * @return the action registry
	 */
	public ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();

		return actionRegistry;
	}

	/**
	 * Get the zoom manager
	 * 
	 * @return
	 */
	public ZoomManager getZoomManager() {
		return zoomManager;
	}

	/**
	 * Returns the undoable <code>PropertySheetPage</code> for this editor.
	 * 
	 * @return the undoable <code>PropertySheetPage</code>
	 */
	protected PropertySheetPage getPropertySheetPage() {
		if (undoablePropertySheetPage == null) {
			undoablePropertySheetPage = new PropertySheetPage();
			undoablePropertySheetPage.setRootEntry(GEFPlugin.createUndoablePropertySheetEntry(getCommandStack()));
		}

		return undoablePropertySheetPage;
	}

	protected void firePropertyChange(int propertyId) {
		super.firePropertyChange(propertyId);
		updateActions(editorActionIDs);
	}

	/**
	 * @return the PaletteRoot to be used with the PaletteViewer
	 */
	protected PaletteRoot getPaletteRoot() {
		return new PaletteViewerCreator().createPaletteRoot(erSchema);
	}

	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}
		String type = cubridNode.getType();
		if (!NodeType.DATABASE.equals(type) && !NodeType.SERVER.equals(type)) {
			return;
		}

		if (!CubridNodeChangedEventType.DATABASE_LOGIN.equals(eventType)
				&& !CubridNodeChangedEventType.DATABASE_LOGOUT.equals(eventType)
				&& !CubridNodeChangedEventType.NODE_REMOVE.equals(eventType)
				&& !CubridNodeChangedEventType.NODE_REFRESH.equals(eventType)
				&& !CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(eventType)) {
			return;
		}
		if (NodeType.SERVER.equals(type) && cubridNode instanceof CubridServer) {
			if (CubridNodeChangedEventType.NODE_REMOVE.equals(eventType)
					|| CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(eventType)) {
				Object obj = database.getAdapter(ServerInfo.class);
				CubridServer server = (CubridServer) cubridNode;
				ServerInfo severInfo = server.getServerInfo();
				if (severInfo != null && severInfo.equals(obj)) {
					setDatabase(ERVirtualDatabase.getInstance());
					CubridNodeManager.getInstance().removeCubridNodeChangeListener(this);
				}
			}
		}
		if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(eventType)
				|| CubridNodeChangedEventType.NODE_REMOVE.equals(eventType)) {
			if (cubridNode.equals(database)) {
				setDatabase(ERVirtualDatabase.getInstance());
				CubridNodeManager.getInstance().removeCubridNodeChangeListener(this);
			}
		}

		synchronized (this) {
			erToolBar.refresh();
		}
	}

	public ERSchema getERSchema() {
		return erSchema;
	}

	public void setERSchema(ERSchema erSchema) {
		this.erSchema = erSchema;
	}

	public void changeDataBase(CubridDatabase db) {
		erSchema.getInput().setDatabase(db);
	}

	public ERSchemaTableNodesLoader getTableNodesLoader() {
		return tableNodesLoader;
	}

	public void setTableNodesLoader(ERSchemaTableNodesLoader tableNodesLoader) {
		this.tableNodesLoader = tableNodesLoader;
	}

	public ERSchemaToolBar getToolBar() {
		return erToolBar;
	}

	/**
	 * Save ERwin XML file
	 * 
	 * @see EditorPart#doSave
	 */
	public void doSave(IProgressMonitor monitor) {
		if (dataExporter == null) {
			doSaveAs();
			return;
		}

		if (erSchema.getAllSchemaInfo().size() == 0) {
			CommonUITool.openErrorBox(Messages.errExportNoTable);
			return;
		}

		boolean isSuccess = dataExporter.exportData(getGraphicalControl().getShell(), true);
		setDirty(!isSuccess);
	}

	/**
	 * Save as ERwin XML, erd or picture file
	 */
	public void doSaveAs() {
		if (erSchema.getAllSchemaInfo().size() == 0) {
			CommonUITool.openErrorBox(Messages.errExportNoTable);
			return;
		}
		ExportERDataDialog typeDialog = new ExportERDataDialog(getGraphicalControl().getShell());
		if (typeDialog.open() != IDialogConstants.OK_ID) {
			return;

		}
		int index = typeDialog.getSelectedType();
		ExportDataController oldExportor = dataExporter;
		if (index == ExportERDataDialog.ERDTYPE) {// erd
			dataExporter = ExportDataController.getNewAdaptor(ExportImportGsonDataController.class,
					this);
		} else if (index == ExportERDataDialog.ERWINTYPE) {// xml
			dataExporter = ExportDataController.getNewAdaptor(ExportERwinDataController.class, this);
		} else if (index == ExportERDataDialog.IMAGETYPE) {// picture
			dataExporter = ExportDataController.getNewAdaptor(ExportPictureController.class, this);
		} else if (index == ExportERDataDialog.SQLTYPE) {// schema sql
			dataExporter = ExportDataController.getNewAdaptor(ExportSQLDataController.class, this);
		} else {
			return;
		}

		if (dataExporter == null) {
			dataExporter = oldExportor;
			CommonUITool.openErrorBox(Messages.errExportData);
			return;
		}
		boolean isSuccess = dataExporter.exportData(getGraphicalControl().getShell(), false);
		setDirty(!isSuccess);
	}

	/**
	 * Save as not allowed
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setCanvasViewComp(Composite canvasViewComp) {
		this.canvasViewComp = canvasViewComp;
	}

	/**
	 * Indicates if the editor has unsaved changes.
	 * 
	 * @see EditorPart#isDirty
	 */
	public boolean isDirty() {
		return isDirty;
	}
}