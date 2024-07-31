package com.cubrid.common.ui.compare.schema.model;

import org.eclipse.compare.ITypedElement;
import org.eclipse.swt.graphics.Image;

/**
 * Table Schema Class
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.08 created by Ray Yin
 */
public class TableSchema implements ITypedElement {
    private String tableName;
    private String tableType;
    private String tableSchema;

    public TableSchema() {
        this.tableName = "";
        this.tableSchema = "";
    }

    /**
     * The constructor
     *
     * @param tableName
     * @param tableSchema
     */
    public TableSchema(String tableName, String tableSchema) {
        this.tableName = tableName;
        this.tableSchema = tableSchema;
    }

    public void setName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return this.tableName;
    }

    public void setSchemaInfo(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getSchemaInfo() {
        return tableSchema;
    }

    /** @return the tableType */
    public String getTableType() {
        return tableType;
    }

    /** @param tableType the tableType to set */
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public Image getImage() {
        return null;
    }

    public String getType() {
        return null;
    }
}
