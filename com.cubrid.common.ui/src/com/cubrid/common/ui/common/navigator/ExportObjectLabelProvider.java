package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.PendingUpdateAdapter;

public class ExportObjectLabelProvider extends CellLabelProvider {
    public static final String CONDITION = "Condition";

    /**
     * update
     *
     * @param cell ViewerCell
     */
    public void update(ViewerCell cell) {
        if (cell.getColumnIndex() == 0) {
            if (cell.getElement() instanceof ICubridNode) {
                ICubridNode node = (ICubridNode) cell.getElement();
                cell.setImage(CommonUIPlugin.getImage(node.getIconPath()));
                cell.setText(node.getName());
            } else if (cell.getElement() instanceof PendingUpdateAdapter) {
                cell.setText(Messages.msgLoading);
            }
        } else if (cell.getColumnIndex() == 1 && cell.getElement() instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) cell.getElement();
            String condition = (String) node.getData(CONDITION);
            if (condition != null) {
                cell.setText(condition);
            }
            // cell can't edit ,set it to gray color
            if (node.getType().equals(NodeType.TABLE_COLUMN)) {
                cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
            }
        }
    }
}
