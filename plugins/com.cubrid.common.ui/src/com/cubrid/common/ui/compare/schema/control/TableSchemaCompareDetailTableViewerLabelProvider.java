package com.cubrid.common.ui.compare.schema.control;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.compare.Messages;
import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import org.eclipse.compare.ITypedElement;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Table Schema Compare Table Viewer Label Provider
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.12 created by Ray Yin
 */
public class TableSchemaCompareDetailTableViewerLabelProvider extends LabelProvider
        implements ITableLabelProvider, IColorProvider {
    private final Image tableIcon =
            CommonUIPlugin.getImage("icons/navigator/schema_table_item.png");
    private final Image greenballIcon = CommonUIPlugin.getImage("icons/compare/green_ball.png");
    private final Image redballIcon = CommonUIPlugin.getImage("icons/compare/red_ball.png");
    private final Image yellowIcon = CommonUIPlugin.getImage("icons/compare/yellow_ball.png");

    private final Device device = Display.getCurrent();
    private final Color diffschemaColor = new Color(device, 249, 222, 223);
    private final Color targetMissColor = new Color(device, 255, 241, 198);
    private final Color sourceMissColor = new Color(device, 229, 230, 249);

    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof TableSchemaCompareModel)) {
            return "";
        }

        TableSchemaCompareModel cm = (TableSchemaCompareModel) element;
        String sourceTable = getTableName(cm.getLeft());
        String targetTable = getTableName(cm.getRight());

        switch (columnIndex) {
            case 0:
                return "";
            case 1:
                if (cm.getCompareStatus() == TableSchemaCompareModel.SCHEMA_SMISS)
                    sourceTable = Messages.statusMissing;
                return sourceTable;
            case 2:
                if (cm.getCompareStatus() == TableSchemaCompareModel.SCHEMA_TMISS)
                    targetTable = Messages.statusMissing;
                return targetTable;
            default:
                return "";
        }
    }

    private String getTableName(ITypedElement element) {
        if (element == null) return "";

        if (element instanceof TableSchema) {
            String tableName = ((TableSchema) element).getName();
            return tableName == null ? "" : tableName;
        }

        return ((TableSchema) element).getName();
    }

    public Color getBackground(Object element) {
        TableSchemaCompareModel cm = (TableSchemaCompareModel) element;
        if (cm.getCompareStatus() == TableSchemaCompareModel.SCHEMA_DIFF) {
            return diffschemaColor;
        } else if (cm.getCompareStatus() == TableSchemaCompareModel.SCHEMA_TMISS) {
            return targetMissColor;
        } else if (cm.getCompareStatus() == TableSchemaCompareModel.SCHEMA_SMISS) {
            return sourceMissColor;
        }

        return null;
    }

    public Image getColumnImage(Object element, int columnIndex) {
        TableSchemaCompareModel cm = (TableSchemaCompareModel) element;

        if (columnIndex == 0) {
            if (cm.getCompareStatus() != TableSchemaCompareModel.SCHEMA_EQUAL
                    && cm.getCompareStatus() != TableSchemaCompareModel.RECORDS_DIFF) {
                return redballIcon;
            } else if (cm.getSourceRecords() != cm.getTargetRecords()) {
                cm.setCompareStatus(TableSchemaCompareModel.RECORDS_DIFF);
                return yellowIcon;
            } else {
                return greenballIcon;
            }
        } else if (columnIndex == 1 || columnIndex == 2) {
            return tableIcon;
        }

        return null;
    }

    public Color getForeground(Object element) {
        return null;
    }
}
