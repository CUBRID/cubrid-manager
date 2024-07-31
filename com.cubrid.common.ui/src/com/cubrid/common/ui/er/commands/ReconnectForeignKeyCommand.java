/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.er.commands;

import com.cubrid.common.ui.er.model.ERTable;
import com.cubrid.common.ui.er.model.Relationship;
import java.util.List;
import org.eclipse.gef.commands.Command;

/**
 * Command to change the foreign key we are connecting to a particular primary key
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-12 created by Yu Guojia
 */
public class ReconnectForeignKeyCommand extends Command {
    protected ERTable sourceForeignTable;
    protected ERTable targetPrimaryTable;
    protected Relationship relationship;
    protected ERTable oldSourceForeignTable;
    protected ERTable oldTargetTable;

    /**
     * Makes sure that primary key doesn't reconnect to itself or try to create a relationship which
     * already exists
     */
    @Override
    public boolean canExecute() {
        boolean returnVal = true;
        ERTable primaryKeyTable = relationship.getPrimaryKeyTable();

        // cannot connect to itself
        if (primaryKeyTable.equals(sourceForeignTable)) {
            returnVal = false;
        } else {
            List relationships = sourceForeignTable.getForeignKeyRelationships();
            for (int i = 0; i < relationships.size(); i++) {
                Relationship relationship = ((Relationship) (relationships.get(i)));
                if (relationship.getPrimaryKeyTable().equals(targetPrimaryTable)
                        && relationship.getForeignKeyTable().equals(sourceForeignTable)) {
                    returnVal = false;
                    break;
                }
            }
        }

        return returnVal;
    }

    @Override
    public void execute() {
        if (sourceForeignTable != null) {
            oldSourceForeignTable.deleteForeignKeyShipAndFire(relationship);
            relationship.setForeignKeyTable(sourceForeignTable);
            sourceForeignTable.addSourceTargetShipAndFire(relationship);
        }
    }

    public ERTable getSourceForeignKey() {
        return sourceForeignTable;
    }

    public void setSourceForeignKey(ERTable sourceForeignKey) {
        this.sourceForeignTable = sourceForeignKey;
    }

    public ERTable getTargetPrimaryKey() {
        return targetPrimaryTable;
    }

    public void setTargetPrimaryKey(ERTable targetPrimaryKey) {
        this.targetPrimaryTable = targetPrimaryKey;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    /**
     * Sets the Relationship associated with this
     *
     * @param relationship the Relationship
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
        targetPrimaryTable = relationship.getPrimaryKeyTable();
        oldSourceForeignTable = relationship.getForeignKeyTable();
    }

    @Override
    public void undo() {
        sourceForeignTable.deleteForeignKeyShipAndFire(relationship);
        relationship.setForeignKeyTable(oldSourceForeignTable);
        oldSourceForeignTable.addForeignKeyShipAndFire(relationship);
    }
}
