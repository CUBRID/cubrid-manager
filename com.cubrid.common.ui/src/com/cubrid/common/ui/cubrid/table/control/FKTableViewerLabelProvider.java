/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.cubrid.table.control;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SuperClassUtil;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * FK Table Viewer Label Provider
 *
 * @author robin 2009-6-4
 */
public class FKTableViewerLabelProvider implements ITableLabelProvider {
    DatabaseInfo database = null;
    private ERSchema erSchema = null;

    public FKTableViewerLabelProvider(DatabaseInfo database) {
        this.database = database;
    }

    public FKTableViewerLabelProvider(DatabaseInfo database, ERSchema erSchema) {
        this.database = database;
        this.erSchema = erSchema;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     * @param element the object representing the entire row, or <code>null</code> indicating that
     *     no input object is set in the viewer
     * @param columnIndex the zero-based index of the column in which the label appears
     * @return Image or <code>null</code> if there is no image for the given object at columnIndex
     */
    public Image getColumnImage(Object element, int columnIndex) {

        switch (columnIndex) {
            case 0:
                return null;
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                return null;
            case 4:
                return null;
            case 5:
                return null;
            case 6:
                return null;
            default:
                break;
        }
        return null;
    }

    /**
     * Get SchemaInfo from ER or real database
     *
     * @param refedTable
     * @return
     */
    private SchemaInfo getRefedTable(String refedTable) {
        if (this.erSchema != null) {
            return erSchema.getSchemaInfo(refedTable);
        }
        return this.database.getSchemaInfo(refedTable);
    }

    /**
     * Get supper table. If the table come from ER, return empty.
     *
     * @param refSchema
     * @return
     */
    private List<SchemaInfo> getRefedSupper(SchemaInfo refSchema) {
        if (this.erSchema != null) {
            return new ArrayList<SchemaInfo>(0);
        }
        return SuperClassUtil.getSuperClasses(database, refSchema);
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     * @param element the object representing the entire row, or <code>null</code> indicating that
     *     no input object is set in the viewer
     * @param columnIndex the zero-based index of the column in which the label appears
     * @return String or or <code>null</code> if there is no text for the given object at
     *     columnIndex
     */
    public String getColumnText(Object element, int columnIndex) {
        Constraint fk = (Constraint) element;

        String refTable = null;
        String delRule = null;
        String updateRule = null;
        String cacheRule = null;
        List<String> rules = fk.getRules();
        for (String rule : rules) {
            String refStr = "REFERENCES ";
            String delStr = "ON DELETE ";
            String updStr = "ON UPDATE ";
            String cacheStr = "ON CACHE OBJECT ";

            if (rule.startsWith(refStr)) {
                refTable = rule.replace(refStr, "");
            } else if (rule.startsWith(delStr)) {
                delRule = rule.replace(delStr, "");
            } else if (rule.startsWith(updStr)) {
                updateRule = rule.replace(updStr, "");
            } else if (rule.startsWith(cacheStr)) {
                cacheRule = rule.replace(cacheStr, "");
            }
        }

        switch (columnIndex) {
            case 0:
                return fk.getName() == null ? "" : fk.getName();
            case 1:
                List<String> columns = fk.getAttributes();
                StringBuffer bf = new StringBuffer();
                int count = 0;

                for (String column : columns) {
                    if (count != 0) {
                        bf.append(",");
                    }
                    bf.append(column);
                    count++;
                }

                return bf.toString();
            case 2:
                return refTable;
            case 3:
                // get reference table's PK
                if (null == refTable) {
                    return null;
                }
                SchemaInfo refSchema = getRefedTable(refTable);
                List<SchemaInfo> refSupers = getRefedSupper(refSchema);
                Constraint refPK = refSchema.getPK(refSupers);
                if (refPK == null) {
                    return null;
                } else {
                    List<String> refPKAttrs = refPK.getAttributes();
                    StringBuffer bf2 = new StringBuffer();
                    int count2 = 0;

                    for (String column : refPKAttrs) {
                        if (count2 != 0) {
                            bf2.append(",");
                        }
                        bf2.append(column);
                        count2++;
                    }

                    return bf2.toString();
                }

            case 4:
                return updateRule;
            case 5:
                return delRule;
            case 6:
                return cacheRule;

            default:
                break;
        }
        return null;
    }

    /**
     * @see
     *     org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @param listener a label provider listener
     */
    public void addListener(ILabelProviderListener listener) {
        // empty
    }

    /** @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose() */
    public void dispose() {
        // empty
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *     java.lang.String)
     * @param element the element
     * @param property the property
     * @return <code>true</code> if the label would be affected, and <code>false</code> if it would
     *     be unaffected
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /**
     * @see
     *     org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @param listener a label provider listener
     */
    public void removeListener(ILabelProviderListener listener) {
        // empty
    }
}
