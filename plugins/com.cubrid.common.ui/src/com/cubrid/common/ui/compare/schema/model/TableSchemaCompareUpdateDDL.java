/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.compare.schema.model;

import com.cubrid.common.core.common.model.Constraint;
import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeManager;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaDDL;
import java.util.List;

/**
 * Table Schema Update(Alter) DDL
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.18 created by Ray Yin
 */
public class TableSchemaCompareUpdateDDL {
    private final SchemaInfo sourceTableSchema;
    private final SchemaInfo targetTableSchema;
    private final SchemaChangeManager changeManager;
    private final SchemaDDL sourceSchemaDDL;
    private final SchemaDDL targetSchemaDDL;
    private String alterDDL;

    /**
     * The constructor
     *
     * @param changeManager
     * @param schemaDDL
     * @param sourceTableSchema
     * @param targetTableSchema
     */
    public TableSchemaCompareUpdateDDL(
            SchemaChangeManager changeManager,
            SchemaDDL sourceSchemaDDL,
            SchemaDDL targetSchemaDDL,
            SchemaInfo sourceTableSchema,
            SchemaInfo targetTableSchema) {
        super();
        this.sourceTableSchema = sourceTableSchema;
        this.targetTableSchema = targetTableSchema;
        this.changeManager = changeManager;
        this.sourceSchemaDDL = sourceSchemaDDL;
        this.targetSchemaDDL = targetSchemaDDL;
        this.alterDDL = null;
    }

    public String getTableSchemaAlterDDL() { // FIXME logic code move to core module
        if (sourceTableSchema == null) {
            alterDDL = targetSchemaDDL.getSchemaDDL(targetTableSchema);
        } else if (targetTableSchema == null) {
            String escapedTableName = QuerySyntax.escapeKeyword(sourceTableSchema.getUniqueName());
            alterDDL = "DROP TABLE " + escapedTableName + ";" + StringUtil.NEWLINE;
        } else {
            setClassNameAlterDDL();
            setAttributesAlterDDL();
            setIndexAlterDDL();
            setFKAlterDDL();
            setPositionAlterDDL();
            alterDDL = sourceSchemaDDL.getAlterDDL(sourceTableSchema, targetTableSchema);
        }

        return alterDDL;
    }

    /** Compare table class name */
    public void setClassNameAlterDDL() { // FIXME logic code move to core module
        String sourceClassName = sourceTableSchema.getUniqueName();
        String targetClassName = targetTableSchema.getUniqueName();

        if (!sourceClassName.toLowerCase().equals(targetClassName.toLowerCase())) {
            changeManager.addSchemeChangeLog(
                    new SchemaChangeLog(
                            sourceClassName, targetClassName, SchemeInnerType.TYPE_SCHEMA));
        }
    }

    /** Compare table attributes */
    public void setAttributesAlterDDL() { // FIXME logic code move to core module
        List<DBAttribute> sourceDBAttributes = sourceTableSchema.getAttributes();
        List<DBAttribute> targetDBAttributes = targetTableSchema.getAttributes();

        for (DBAttribute targetAttr : targetDBAttributes) {
            String targetAttrName = targetAttr.getName().toLowerCase();
            DBAttribute sourceAttr = sourceTableSchema.getDBAttributeByName(targetAttrName, false);
            if (sourceAttr != null) {
                if (!targetAttr.equals(sourceAttr)) {
                    changeManager.addSchemeChangeLog(
                            new SchemaChangeLog(
                                    sourceAttr.getName(),
                                    targetAttrName,
                                    SchemeInnerType.TYPE_ATTRIBUTE));
                    changeManager.addSchemeChangeLog(
                            new SchemaChangeLog(
                                    sourceAttr.getName(),
                                    targetAttrName,
                                    SchemeInnerType.TYPE_POSITION));
                }
            } else {
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(null, targetAttrName, SchemeInnerType.TYPE_ATTRIBUTE));
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(
                                targetAttrName, targetAttrName, SchemeInnerType.TYPE_POSITION));
            }
        }

        for (DBAttribute sourceAttr : sourceDBAttributes) {
            String sourceAttrName = sourceAttr.getName();
            DBAttribute targetAttr = targetTableSchema.getDBAttributeByName(sourceAttrName, false);
            if (targetAttr == null) {
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(sourceAttrName, null, SchemeInnerType.TYPE_ATTRIBUTE));
            }
        }
    }

    /** Compare table index constraints */
    public void setIndexAlterDDL() { // FIXME logic code move to core module
        List<Constraint> sourceDBConstraints = sourceTableSchema.getConstraints();
        List<Constraint> targetDBConstraints = targetTableSchema.getConstraints();

        for (Constraint targetCons : targetDBConstraints) {
            String targetConsName = targetCons.getName().toLowerCase();
            Constraint sourceCons = findConstraint(sourceTableSchema, targetConsName);

            if (targetCons.getType().equals(Constraint.ConstraintType.INDEX.getText())
                    || targetCons.getType().equals(Constraint.ConstraintType.UNIQUE.getText())) {
                if (sourceCons != null) {
                    if (!targetCons.equals(sourceCons)) {
                        changeManager.addSchemeChangeLog(
                                new SchemaChangeLog(
                                        sourceCons.getDefaultName(sourceTableSchema.getUniqueName())
                                                + "$"
                                                + sourceCons.getName(),
                                        targetCons.getDefaultName(targetTableSchema.getUniqueName())
                                                + "$"
                                                + targetCons.getName(),
                                        SchemeInnerType.TYPE_INDEX));
                    }
                } else {
                    changeManager.addSchemeChangeLog(
                            new SchemaChangeLog(
                                    null,
                                    targetCons.getDefaultName(targetTableSchema.getUniqueName())
                                            + "$"
                                            + targetCons.getName(),
                                    SchemeInnerType.TYPE_INDEX));
                }
            }
        }

        for (Constraint sourceCons : sourceDBConstraints) {
            String sourceConsName = sourceCons.getName();
            Constraint t_cons = findConstraint(targetTableSchema, sourceConsName);

            if (t_cons == null
                    && (sourceCons.getType().equals(Constraint.ConstraintType.INDEX.getText())
                            || sourceCons
                                    .getType()
                                    .equals(Constraint.ConstraintType.UNIQUE.getText()))) {
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(
                                sourceCons.getDefaultName(sourceTableSchema.getUniqueName())
                                        + "$"
                                        + sourceCons.getName(),
                                null,
                                SchemeInnerType.TYPE_INDEX));
            }
        }
    }

    /** Compare table FK constraints */
    public void setFKAlterDDL() { // FIXME logic code move to core module
        List<Constraint> sourceFKConstraints = sourceTableSchema.getFKConstraints();
        List<Constraint> targetFKConstraints = targetTableSchema.getFKConstraints();

        for (Constraint targetFk : targetFKConstraints) {
            String targetFkName = targetFk.getName();
            Constraint sourceFk = findConstraint(sourceTableSchema, targetFkName);

            if (sourceFk != null) {
                if (!targetFk.equals(sourceFk)) {
                    changeManager.addSchemeChangeLog(
                            new SchemaChangeLog(
                                    sourceFk.getName(),
                                    targetFk.getName(),
                                    SchemeInnerType.TYPE_FK));
                }
            } else {
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(null, targetFk.getName(), SchemeInnerType.TYPE_FK));
            }
        }

        for (Constraint sourceFk : sourceFKConstraints) {
            String sourceFkName = sourceFk.getName();
            Constraint targetFk = findConstraint(targetTableSchema, sourceFkName);

            if (targetFk == null) {
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(sourceFk.getName(), null, SchemeInnerType.TYPE_FK));
            }
        }
    }

    /** Compare table Attributes Positions */
    public void setPositionAlterDDL() { // FIXME logic code move to core module
        List<DBAttribute> targetDBAttributes = targetTableSchema.getAttributes();
        List<DBAttribute> sourceDBAttributes = sourceTableSchema.getAttributes();

        for (DBAttribute targetAttr : targetDBAttributes) {
            String targetAttrName = targetAttr.getName();
            int targetPosition = targetDBAttributes.indexOf(targetAttr);

            DBAttribute sourceAttr = sourceTableSchema.getDBAttributeByName(targetAttrName, false);
            int sourcePosition = sourceDBAttributes.indexOf(sourceAttr);

            if (sourceAttr != null && sourcePosition != targetPosition) {
                changeManager.addSchemeChangeLog(
                        new SchemaChangeLog(
                                sourceAttr.getName(),
                                targetAttr.getName(),
                                SchemeInnerType.TYPE_POSITION));
            }
        }
    }

    /**
     * @param schemaInfo
     * @param sourceFkName
     * @return
     */
    private Constraint findConstraint(
            SchemaInfo schemaInfo, String sourceFkName) { // FIXME logic code move to core module
        List<Constraint> cons = schemaInfo.getConstraints();
        for (Constraint c : cons) {
            if (c.getName().toLowerCase().equals(sourceFkName.toLowerCase())) {
                return c;
            }
        }
        return null;
    }
}
