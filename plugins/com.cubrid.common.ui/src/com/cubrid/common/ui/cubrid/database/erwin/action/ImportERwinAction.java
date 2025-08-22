package com.cubrid.common.ui.cubrid.database.erwin.action;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.compare.schema.TableSchemaComparator;
import com.cubrid.common.ui.compare.schema.TableSchemaCompareUtil;
import com.cubrid.common.ui.compare.schema.control.TableSchemaCompareInfoPart;
import com.cubrid.common.ui.compare.schema.control.TableSchemaCompareModelInputLazy;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import com.cubrid.common.ui.compare.schema.model.TableSchemaModel;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlContainer;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlDatabaseInfoMapper;
import com.cubrid.common.ui.cubrid.database.erwin.WrappedDatabaseInfo;
import com.cubrid.common.ui.cubrid.database.erwin.dialog.ERwinImportDialog;
import com.cubrid.common.ui.cubrid.database.erwin.model.ERWinSchemaInfo;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * ImportERXmlAction
 *
 * <p>parse the ER win xml file
 *
 * @author Jason You
 * @version 1.0 - 2012-11-20 created by Jason You
 */
public class ImportERwinAction extends SelectionAction {

    public static final String ID = ImportERwinAction.class.getName();
    private CubridDatabase database;

    /**
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public ImportERwinAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor icon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    /**
     * @param shell
     * @param compareSchema
     * @param imageDescriptor
     */
    public ImportERwinAction(
            Shell shell,
            String text,
            ImageDescriptor imageDescriptor,
            ImageDescriptor disabledIcon) {
        this(shell, null, text, imageDescriptor, disabledIcon);
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     */
    public boolean allowMultiSelections() {
        return true;
    }

    public boolean isSupported(Object obj) {
        return true;
    }

    public void run() { // FIXME logic code move to core module
        int selected = 0;
        int logined = 0;
        Object[] objects = getSelectedObj();
        if (objects instanceof Object[]) {
            for (Object object : objects) {
                if (object instanceof CubridDatabase) {
                    selected++;
                    CubridDatabase database = (CubridDatabase) object;
                    if (database.isLogined()) {
                        logined++;
                    }
                }
            }
        }

        if (selected > 1) {
            CommonUITool.openWarningBox(
                    com.cubrid.common.ui.cubrid.database.erwin.Messages
                            .errERwinSelectOneDbToImport);
            return;
        }

        if (selected <= 0) {
            CommonUITool.openWarningBox(
                    com.cubrid.common.ui.cubrid.database.erwin.Messages
                            .errERwinSelectImportDbToImport);
            return;
        }

        if (logined <= 0) {
            CommonUITool.openWarningBox(
                    com.cubrid.common.ui.cubrid.database.erwin.Messages
                            .errERwinSelectLoginedDbToImport);
            return;
        }

        Object[] obj = getSelectedObj();
        if (obj == null || obj.length != 1) {
            return;
        }

        if (!(obj[0] instanceof CubridDatabase)) {
            return;
        }
        database = (CubridDatabase) obj[0];

        ERwinImportDialog dialog = new ERwinImportDialog(getShell(), database);
        int returnvalue = dialog.open();
        if (returnvalue != IDialogConstants.OK_ID) {
            return;
        }

        ERXmlContainer container = dialog.getContainer();

        if (container.getErrMsg() != null && !container.getErrMsg().equals("")) {
            CommonUITool.openErrorBox(container.getErrMsg());
            return;
        }
        Map<String, TableSchema> tableSchema = container.getTableSchemas();
        Map<String, ERWinSchemaInfo> schemaInfos = container.getSchemaInfos();
        String modelName = container.getDatabaseName();
        createCompareModel(modelName, tableSchema, schemaInfos);
    }

    /**
     * @param tableSchema
     * @param schemaInfos
     */
    private void createCompareModel(
            final String modelName,
            final Map<String, TableSchema> tableSchema,
            final Map<String, ERWinSchemaInfo> schemaInfos) {

        final List<TableSchemaCompareModelInputLazy> input =
                new ArrayList<TableSchemaCompareModelInputLazy>();
        ITask reportBugTask =
                new AbstractUITask() {
                    private boolean success = false;

                    public void cancel() {}

                    public void finish() {}

                    public boolean isCancel() {
                        return false;
                    }

                    public boolean isSuccess() {
                        return success;
                    }

                    public void execute(IProgressMonitor monitor) {
                        List<TableDetailInfo> leftDbTableInfoList =
                                TableSchemaCompareUtil.getTableInfoList(database);
                        final CubridDatabase virtualDb = new CubridDatabase(modelName, modelName);
                        virtualDb.setVirtual(true);

                        DatabaseInfo info = database.getDatabaseInfo();
                        virtualDb.setDatabaseInfo(info);
                        WrappedDatabaseInfo wrappedDatabaseInfo = new WrappedDatabaseInfo(info);
                        Map<String, SchemaInfo> dbSchemaInfos = new HashMap<String, SchemaInfo>();
                        Collection<ERWinSchemaInfo> erwinSchemas = schemaInfos.values();
                        for (ERWinSchemaInfo erwinSchema : erwinSchemas) {
                            SchemaInfo schemaInfo = (SchemaInfo) erwinSchema;
                            dbSchemaInfos.put(schemaInfo.getUniqueName(), schemaInfo);
                        }
                        wrappedDatabaseInfo.addSchemaInfos(dbSchemaInfos);
                        wrappedDatabaseInfo.addTableSchemas(tableSchema);
                        ERXmlDatabaseInfoMapper.addWrappedDatabaseInfo(info, wrappedDatabaseInfo);

                        TableSchemaModel leftModel =
                                TableSchemaCompareUtil.createTableSchemaModel(leftDbTableInfoList);
                        TableSchemaModel rightModel = new TableSchemaModel();

                        rightModel.getTableSchemaMap().putAll(tableSchema);

                        TableSchemaComparator comparator =
                                new TableSchemaComparator(database, virtualDb);

                        TableSchemaCompareModel model = comparator.compare(leftModel, rightModel);
                        model.setSourceDB(database);
                        model.setTargetDB(virtualDb);
                        input.add(
                                new TableSchemaCompareModelInputLazy(
                                        model)); // TODO rename class to ErwinCompareModelInput
                        success = true;
                    }
                };

        TaskExecutor taskExecutor = new CommonTaskExec(Messages.titleSchemaComparison);
        taskExecutor.addTask(reportBugTask);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            try {
                PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .openEditor(input.get(0), TableSchemaCompareInfoPart.ID);
            } catch (Exception e) {
            }
        }
    }
}
