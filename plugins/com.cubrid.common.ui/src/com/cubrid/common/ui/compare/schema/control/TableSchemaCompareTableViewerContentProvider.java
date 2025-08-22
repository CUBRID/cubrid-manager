package com.cubrid.common.ui.compare.schema.control;

import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Table Schema Compare Table Viewer Content Provider
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.12 created by Ray Yin
 */
public class TableSchemaCompareTableViewerContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof TableSchemaCompareModel) {
            TableSchemaCompareModel model = (TableSchemaCompareModel) inputElement;
            List<TableSchemaCompareModel> list = model.getTableCompareList();

            if (list == null) {
                return new TableSchemaCompareModel[0];
            }

            return list.toArray(new TableSchemaCompareModel[list.size()]);
        }

        return new Object[] {};
    }

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}
