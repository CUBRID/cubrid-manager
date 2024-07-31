package com.cubrid.common.ui.cubrid.table.dashboard.control;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TableDashboardInput implements IEditorInput {

    private final CubridDatabase database;
    private List<TableDetailInfo> tableList;

    public TableDashboardInput(CubridDatabase database) {
        this.database = database;
    }

    public void setTableList(List<TableDetailInfo> tableList) {
        this.tableList = tableList;
    }
    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @param adapter the adapter class to look up
     * @return a object castable to the given class, or <code>null</code> if this object does not
     *     have an adapter for the given class
     */
    @SuppressWarnings("all")
    public Object getAdapter(Class adapter) {
        if (adapter.equals(CubridDatabase.class)) {
            return database;
        } else if (adapter.equals(List.class)) {
            return tableList;
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return Messages.tablesDetailInfoPartTitle;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return this.getName();
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    public int hashCode() {
        if (database != null) {
            return database.hashCode();
        }
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof TableDashboardInput) {
            CubridDatabase objDatabase = ((TableDashboardInput) obj).getDatabase();
            if (objDatabase != null && this.database != null) {
                return this.database.equals(objDatabase);
            }
        }
        return super.equals(obj);
    }
}
