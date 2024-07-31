package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.POJOUtil;
import com.cubrid.common.ui.spi.util.TableUtil;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import java.io.File;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Copy Php pojo to files action
 *
 * @author Isaiah Choe
 * @version 1.0 - 2012-7-2 created by Isaiah Choe
 */
public class TableToPhpCodeAction extends SelectionAction {
    private static final Logger LOGGER = LogUtil.getLogger(TableToPhpCodeAction.class);
    public static final String ID = TableToPhpCodeAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public TableToPhpCodeAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public TableToPhpCodeAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public boolean allowMultiSelections() {
        return true;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(
                obj,
                new String[] {NodeType.USER_TABLE, NodeType.USER_PARTITIONED_TABLE_FOLDER},
                false);
    }

    public void run() {
        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }
        doRun(obj);
    }

    /**
     * Run
     *
     * @param objects
     */
    public void run(ICubridNode[] objects) {
        doRun(objects);
    }

    /**
     * Perform all tables
     *
     * @param obj
     */
    private void doRun(final Object[] obj) {
        final File filepath = TableUtil.getSavedDirForCreateCodes(getShell(), null);
        if (filepath == null) {
            return;
        }

        if (!CommonUITool.openConfirmBox(Messages.msgConfirmTableToCode)) {
            return;
        }

        final Map<CubridDatabase, Connection> connections =
                new HashMap<CubridDatabase, Connection>();
        try {
            final Display display = PlatformUI.getWorkbench().getDisplay();
            BusyIndicator.showWhile(
                    display,
                    new Runnable() {
                        public void run() {
                            StringBuilder notExportedList =
                                    new StringBuilder(); // FIXME move this logic to core module

                            for (int i = 0; i < obj.length; i++) {
                                DefaultSchemaNode table = (DefaultSchemaNode) obj[i];
                                Connection connection = connections.get(table.getDatabase());
                                if (connection == null) {
                                    try {
                                        connection =
                                                JDBCConnectionManager.getConnection(
                                                        table.getDatabase().getDatabaseInfo(),
                                                        true);
                                        connections.put(table.getDatabase(), connection);
                                    } catch (Exception e) {
                                        LOGGER.error(e.getMessage(), e);
                                    }
                                }

                                if (connection == null) {
                                    if (notExportedList.length() > 0) {
                                        notExportedList.append(", ");
                                    }
                                    notExportedList.append(table.getName());
                                    continue;
                                }

                                String pojoClassFileName = getPojoFileName(table);
                                String pojoClassData = getPojoString(connection, table);
                                String pojoClassPath =
                                        filepath.getAbsolutePath()
                                                + File.separator
                                                + pojoClassFileName;
                                boolean result =
                                        FileUtil.writeToFile(
                                                pojoClassPath,
                                                pojoClassData,
                                                "utf-8"); // TODO: error handling
                                if (!result) {
                                    if (notExportedList.length() > 0) {
                                        notExportedList.append(", ");
                                    }
                                    notExportedList.append(table.getName());
                                }
                            }

                            finishNotice(notExportedList.toString());
                        }
                    });
        } finally {
            Collection<Connection> items = connections.values();
            for (Connection conn : items) {
                QueryUtil.freeQuery(conn);
            }
        }
    }

    private void finishNotice(String notExportedList) {
        String message = Messages.msgResultTableToCode;
        if (notExportedList != null && notExportedList.length() > 0) {
            message += "\n" + Messages.msgResultErrorTableToCode + "\n" + notExportedList;
        }
        CommonUITool.openInformationBox(Messages.titleResultTableToCode, message);
    }

    private String getPojoString(Connection connection, DefaultSchemaNode schemaNode) {
        return POJOUtil.getPhpPOJOString(connection, schemaNode);
    }

    private String getPojoFileName(DefaultSchemaNode schemaNode) {
        return POJOUtil.getPhpClassFileName(schemaNode.getName());
    }
}
