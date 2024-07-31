package com.cubrid.common.ui.cubrid.table.dashboard.control;

import com.cubrid.common.core.common.model.TableDetailInfo;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TableDashboardContentProvider implements IStructuredContentProvider {
    /**
     * getElements
     *
     * @param inputElement Object
     * @return Object[]
     */
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            List<TableDetailInfo> list = (List<TableDetailInfo>) inputElement;
            TableDetailInfo[] nodeArr = new TableDetailInfo[list.size()];
            return list.toArray(nodeArr);
        }

        return new Object[] {};
    }

    /** dispose */
    public void dispose() {
        // do nothing
    }

    /**
     * inputChanged
     *
     * @param viewer Viewer
     * @param oldInput Object
     * @param newInput Object
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }
}
