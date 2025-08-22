package com.cubrid.common.ui.compare.schema.control;

import com.cubrid.common.ui.compare.schema.model.TableSchema;
import com.cubrid.common.ui.compare.schema.model.TableSchemaCompareModel;
import org.eclipse.compare.ITypedElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Table Schema Compare Viewer Column Sorter
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.16 created by Ray Yin
 */
public class TableSchemaCompareTableViewerSorter extends ViewerSorter {
    private static final int STATUS = 1;
    private static final int SOURCE_DB = 2;
    private static final int TARGET_DB = 3;
    private static final int SOURCE_RECORDS = 4;
    private static final int TARGET_RECORDS = 5;
    private static final int SOURCE_ATTRS = 6;
    private static final int TARGET_ATTRS = 7;
    private static final int SOURCE_INDEX = 8;
    private static final int TARGET_INDEX = 9;
    private static final int SOURCE_PK = 10;
    private static final int TARGET_PK = 11;

    public static final TableSchemaCompareTableViewerSorter STATUS_ASC =
            new TableSchemaCompareTableViewerSorter(STATUS);
    public static final TableSchemaCompareTableViewerSorter STATUS_DESC =
            new TableSchemaCompareTableViewerSorter(-STATUS);
    public static final TableSchemaCompareTableViewerSorter SOURCE_DB_ASC =
            new TableSchemaCompareTableViewerSorter(SOURCE_DB);
    public static final TableSchemaCompareTableViewerSorter SOURCE_DB_DESC =
            new TableSchemaCompareTableViewerSorter(-SOURCE_DB);
    public static final TableSchemaCompareTableViewerSorter TARGET_DB_ASC =
            new TableSchemaCompareTableViewerSorter(TARGET_DB);
    public static final TableSchemaCompareTableViewerSorter TARGET_DB_DESC =
            new TableSchemaCompareTableViewerSorter(-TARGET_DB);
    public static final TableSchemaCompareTableViewerSorter SOURCE_RECORDS_ASC =
            new TableSchemaCompareTableViewerSorter(SOURCE_RECORDS);
    public static final TableSchemaCompareTableViewerSorter SOURCE_RECORDS_DESC =
            new TableSchemaCompareTableViewerSorter(-SOURCE_RECORDS);
    public static final TableSchemaCompareTableViewerSorter TARGET_RECORDS_ASC =
            new TableSchemaCompareTableViewerSorter(TARGET_RECORDS);
    public static final TableSchemaCompareTableViewerSorter TARGET_RECORDS_DESC =
            new TableSchemaCompareTableViewerSorter(-TARGET_RECORDS);
    public static final TableSchemaCompareTableViewerSorter SOURCE_ATTRS_ASC =
            new TableSchemaCompareTableViewerSorter(SOURCE_ATTRS);
    public static final TableSchemaCompareTableViewerSorter SOURCE_ATTRS_DESC =
            new TableSchemaCompareTableViewerSorter(-SOURCE_ATTRS);
    public static final TableSchemaCompareTableViewerSorter TARGET_ATTRS_ASC =
            new TableSchemaCompareTableViewerSorter(TARGET_ATTRS);
    public static final TableSchemaCompareTableViewerSorter TARGET_ATTRS_DESC =
            new TableSchemaCompareTableViewerSorter(-TARGET_ATTRS);
    public static final TableSchemaCompareTableViewerSorter SOURCE_INDEX_ASC =
            new TableSchemaCompareTableViewerSorter(SOURCE_INDEX);
    public static final TableSchemaCompareTableViewerSorter SOURCE_INDEX_DESC =
            new TableSchemaCompareTableViewerSorter(-SOURCE_INDEX);
    public static final TableSchemaCompareTableViewerSorter TARGET_INDEX_ASC =
            new TableSchemaCompareTableViewerSorter(TARGET_INDEX);
    public static final TableSchemaCompareTableViewerSorter TARGET_INDEX_DESC =
            new TableSchemaCompareTableViewerSorter(-TARGET_INDEX);
    public static final TableSchemaCompareTableViewerSorter SOURCE_PK_ASC =
            new TableSchemaCompareTableViewerSorter(SOURCE_PK);
    public static final TableSchemaCompareTableViewerSorter SOURCE_PK_DESC =
            new TableSchemaCompareTableViewerSorter(-SOURCE_PK);
    public static final TableSchemaCompareTableViewerSorter TARGET_PK_ASC =
            new TableSchemaCompareTableViewerSorter(TARGET_PK);
    public static final TableSchemaCompareTableViewerSorter TARGET_PK_DESC =
            new TableSchemaCompareTableViewerSorter(-TARGET_PK);

    private int sortType;

    /**
     * The constructor
     *
     * @param sortType
     */
    private TableSchemaCompareTableViewerSorter(int sortType) {
        this.sortType = sortType;
    }

    public int compare(Viewer viewer, Object e1, Object e2) {
        TableSchemaCompareModel cm1 = (TableSchemaCompareModel) e1;
        TableSchemaCompareModel cm2 = (TableSchemaCompareModel) e2;

        switch (sortType) {
            case STATUS:
                {
                    Integer status_1 = cm1.getCompareStatus();
                    Integer status_2 = cm2.getCompareStatus();
                    return status_1.compareTo(status_2);
                }
            case -STATUS:
                {
                    Integer status_1 = cm1.getCompareStatus();
                    Integer status_2 = cm2.getCompareStatus();
                    return status_2.compareTo(status_1);
                }
            case SOURCE_DB:
                {
                    String source_db_1 = getTableName(cm1.getLeft());
                    String source_db_2 = getTableName(cm2.getLeft());
                    return source_db_1.compareTo(source_db_2);
                }
            case -SOURCE_DB:
                {
                    String source_db_1 = getTableName(cm1.getLeft());
                    String source_db_2 = getTableName(cm2.getLeft());
                    return source_db_2.compareTo(source_db_1);
                }
            case TARGET_DB:
                {
                    String target_db_1 = getTableName(cm1.getRight());
                    String target_db_2 = getTableName(cm2.getRight());
                    return target_db_1.compareTo(target_db_2);
                }
            case -TARGET_DB:
                {
                    String target_db_1 = getTableName(cm1.getRight());
                    String target_db_2 = getTableName(cm2.getRight());
                    return target_db_2.compareTo(target_db_1);
                }
            case SOURCE_RECORDS:
                {
                    Long records_1 = cm1.getSourceRecords();
                    Long records_2 = cm2.getSourceRecords();
                    return records_1.compareTo(records_2);
                }
            case -SOURCE_RECORDS:
                {
                    Long records_1 = cm1.getSourceRecords();
                    Long records_2 = cm2.getSourceRecords();
                    return records_2.compareTo(records_1);
                }
            case TARGET_RECORDS:
                {
                    Long records_1 = cm1.getTargetRecords();
                    Long records_2 = cm2.getTargetRecords();
                    return records_1.compareTo(records_2);
                }
            case -TARGET_RECORDS:
                {
                    Long records_1 = cm1.getTargetRecords();
                    Long records_2 = cm2.getTargetRecords();
                    return records_2.compareTo(records_1);
                }
            case SOURCE_ATTRS:
                {
                    Integer attrs_1 = 0;
                    if (cm1.getSourceTableDetailInfo() != null)
                        attrs_1 = cm1.getSourceTableDetailInfo().getColumnsCount();

                    Integer attrs_2 = 0;
                    if (cm2.getSourceTableDetailInfo() != null)
                        attrs_2 = cm2.getSourceTableDetailInfo().getColumnsCount();

                    return attrs_1.compareTo(attrs_2);
                }
            case -SOURCE_ATTRS:
                {
                    Integer attrs_1 = 0;
                    if (cm1.getSourceTableDetailInfo() != null)
                        attrs_1 = cm1.getSourceTableDetailInfo().getColumnsCount();

                    Integer attrs_2 = 0;
                    if (cm2.getSourceTableDetailInfo() != null)
                        attrs_2 = cm2.getSourceTableDetailInfo().getColumnsCount();

                    return attrs_2.compareTo(attrs_1);
                }
            case TARGET_ATTRS:
                {
                    Integer attrs_1 = 0;
                    if (cm1.getTargetTableDetailInfo() != null)
                        attrs_1 = cm1.getTargetTableDetailInfo().getColumnsCount();

                    Integer attrs_2 = 0;
                    if (cm2.getTargetTableDetailInfo() != null)
                        attrs_2 = cm2.getTargetTableDetailInfo().getColumnsCount();

                    return attrs_1.compareTo(attrs_2);
                }
            case -TARGET_ATTRS:
                {
                    Integer attrs_1 = 0;
                    if (cm1.getTargetTableDetailInfo() != null)
                        attrs_1 = cm1.getTargetTableDetailInfo().getColumnsCount();

                    Integer attrs_2 = 0;
                    if (cm2.getTargetTableDetailInfo() != null)
                        attrs_2 = cm2.getTargetTableDetailInfo().getColumnsCount();

                    return attrs_2.compareTo(attrs_1);
                }
            case SOURCE_INDEX:
                {
                    Integer index_1 = 0;
                    if (cm1.getSourceTableDetailInfo() != null)
                        index_1 = cm1.getSourceTableDetailInfo().getIndexCount();

                    Integer index_2 = 0;
                    if (cm2.getSourceTableDetailInfo() != null)
                        index_2 = cm2.getSourceTableDetailInfo().getIndexCount();

                    return index_1.compareTo(index_2);
                }
            case -SOURCE_INDEX:
                {
                    Integer index_1 = 0;
                    if (cm1.getSourceTableDetailInfo() != null)
                        index_1 = cm1.getSourceTableDetailInfo().getIndexCount();

                    Integer index_2 = 0;
                    if (cm2.getSourceTableDetailInfo() != null)
                        index_2 = cm2.getSourceTableDetailInfo().getIndexCount();

                    return index_2.compareTo(index_1);
                }
            case TARGET_INDEX:
                {
                    Integer index_1 = 0;
                    if (cm1.getTargetTableDetailInfo() != null)
                        index_1 = cm1.getTargetTableDetailInfo().getIndexCount();

                    Integer index_2 = 0;
                    if (cm2.getTargetTableDetailInfo() != null)
                        index_2 = cm2.getTargetTableDetailInfo().getIndexCount();

                    return index_1.compareTo(index_2);
                }
            case -TARGET_INDEX:
                {
                    Integer index_1 = 0;
                    if (cm1.getTargetTableDetailInfo() != null)
                        index_1 = cm1.getTargetTableDetailInfo().getIndexCount();

                    Integer index_2 = 0;
                    if (cm2.getTargetTableDetailInfo() != null)
                        index_2 = cm2.getTargetTableDetailInfo().getIndexCount();

                    return index_2.compareTo(index_1);
                }
            case SOURCE_PK:
                {
                    String pk_1 = "";
                    if (cm1.getSourceTableDetailInfo() != null) {
                        if (cm1.getSourceTableDetailInfo().getPkCount() > 0) pk_1 = "Y";
                    }

                    String pk_2 = "";
                    if (cm2.getSourceTableDetailInfo() != null) {
                        if (cm2.getSourceTableDetailInfo().getPkCount() > 0) pk_2 = "Y";
                    }

                    return pk_1.compareTo(pk_2);
                }
            case -SOURCE_PK:
                {
                    String pk_1 = "";
                    if (cm1.getSourceTableDetailInfo() != null) {
                        if (cm1.getSourceTableDetailInfo().getPkCount() > 0) pk_1 = "Y";
                    }

                    String pk_2 = "";
                    if (cm2.getSourceTableDetailInfo() != null) {
                        if (cm2.getSourceTableDetailInfo().getPkCount() > 0) pk_2 = "Y";
                    }

                    return pk_2.compareTo(pk_1);
                }
            case TARGET_PK:
                {
                    String pk_1 = "";
                    if (cm1.getTargetTableDetailInfo() != null) {
                        if (cm1.getTargetTableDetailInfo().getPkCount() > 0) pk_1 = "Y";
                    }

                    String pk_2 = "";
                    if (cm2.getTargetTableDetailInfo() != null) {
                        if (cm2.getTargetTableDetailInfo().getPkCount() > 0) pk_2 = "Y";
                    }

                    return pk_1.compareTo(pk_2);
                }
            case -TARGET_PK:
                {
                    String pk_1 = "";
                    if (cm1.getTargetTableDetailInfo() != null) {
                        if (cm1.getTargetTableDetailInfo().getPkCount() > 0) pk_1 = "Y";
                    }

                    String pk_2 = "";
                    if (cm2.getTargetTableDetailInfo() != null) {
                        if (cm2.getTargetTableDetailInfo().getPkCount() > 0) pk_2 = "Y";
                    }

                    return pk_2.compareTo(pk_1);
                }
        }

        return 0;
    }

    private String getTableName(ITypedElement element) {
        if (element instanceof TableSchema) {
            String name = ((TableSchema) element).getName();
            return name == null ? "" : name;
        }

        return ((TableSchema) element).getName();
    }
}
