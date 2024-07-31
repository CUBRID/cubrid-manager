package com.cubrid.common.ui.compare.data.control;

import com.cubrid.common.ui.compare.data.model.DataCompare;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class DataCompareSchemaListViewSorter extends ViewerSorter {
    public static final int SORT_CHECKED = 0;
    public static final int SORT_TABLE = 1;
    public static final int SORT_SOURCE_RECORDS = 2;
    public static final int SORT_TARGET_RECORDS = 3;
    public static final int SORT_PROGRESS = 4;
    public static final int SORT_MATCHES = 5;
    public static final int SORT_NOT_MATCHES = 6;
    public static final int SORT_NOT_EXISTS = 7;
    public static final int SORT_ERROR = 8;

    private int sortType = SORT_TABLE;
    private boolean sortAsc = true;

    public DataCompareSchemaListViewSorter() {}

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public void setSortAsc(boolean asc) {
        this.sortAsc = asc;
    }

    public int compare(Viewer viewer, Object src, Object dst) {
        DataCompare srcComp = (DataCompare) src;
        DataCompare dstComp = (DataCompare) dst;

        switch (sortType) {
            case SORT_CHECKED:
                {
                    Boolean sVal = srcComp.isUse();
                    Boolean dVal = dstComp.isUse();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_TABLE:
                {
                    String sVal = srcComp.getTableName();
                    String dVal = dstComp.getTableName();
                    return sVal.compareToIgnoreCase(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_SOURCE_RECORDS:
                {
                    Long sVal = srcComp.getRecordsSource();
                    Long dVal = dstComp.getRecordsSource();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_TARGET_RECORDS:
                {
                    Long sVal = srcComp.getRecordsTarget();
                    Long dVal = dstComp.getRecordsTarget();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_PROGRESS:
                {
                    Long sVal = srcComp.getProgressPosition();
                    Long dVal = dstComp.getProgressPosition();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_MATCHES:
                {
                    Long sVal = srcComp.getMatches();
                    Long dVal = dstComp.getMatches();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_NOT_MATCHES:
                {
                    Long sVal = srcComp.getNotMatches();
                    Long dVal = dstComp.getNotMatches();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_NOT_EXISTS:
                {
                    Long sVal = srcComp.getNotExists();
                    Long dVal = dstComp.getNotExists();
                    return sVal.compareTo(dVal) * (sortAsc ? 1 : -1);
                }
            case SORT_ERROR:
                {
                    int sVal = 0;
                    if (srcComp.getRecordsSource() == 0) {
                        sVal += 1;
                    }
                    if (srcComp.getRecordsTarget() == -1) {
                        sVal += 3;
                    }

                    int dVal = 0;
                    if (dstComp.getRecordsSource() == 0) {
                        dVal += 1;
                    }
                    if (dstComp.getRecordsTarget() == -1) {
                        dVal += 3;
                    }

                    Integer sValInt = sVal;
                    Integer dValInt = dVal;
                    return sValInt.compareTo(dValInt) * (sortAsc ? 1 : -1);
                }
            default:
                return 0;
        }
    }
}
