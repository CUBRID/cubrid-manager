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
package com.cubrid.cubridmanager.core.cubrid.synonym;

import com.cubrid.common.core.common.model.Synonym;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;

/**
 * This class indicates the trigger ddl
 *
 * @author sq
 * @version 1.0 - 2009-12-29 created by sq
 */
public final class SynonymDDL {
    // Constructor
    private SynonymDDL() {
        // empty
    }

    static String newLine = StringUtil.NEWLINE;
    static String endLineChar = ";";

    /**
     * Get the ddl
     *
     * @param trigger Trigger
     * @return String
     */
    public static String getDDL(Synonym synonym) {
        StringBuffer bf = new StringBuffer();
        appendHead(synonym, bf);
        appendComment(synonym, bf);
        return bf.toString();
    }

    /**
     * COMMENT 'user defined comment' [ ; ]
     *
     * @param trigger Trigger
     * @param bf StringBuffer
     */
    public static void appendComment(Synonym synonym, StringBuffer bf) {
        String commnet = synonym.getComment();
        if (StringUtil.isNotEmpty(commnet)) {
            commnet = String.format("'%s'", commnet);
            bf.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(commnet)));
        }
        bf.append(endLineChar);
    }

    /**
     * Generate head of trigger clause.
     *
     * @param trigger Trigger
     * @param bf StringBuffer
     */
    private static void appendHead(Synonym synonym, StringBuffer bf) {
        // CREATE TRIGGER trigger_name
        bf.append("CREATE SYNONYM ");
        String synonymName = synonym.getName();
        String ownerName = synonym.getOwner();
        String targetName = synonym.getTargetName();
        String targetOwner = synonym.getTargetOwner();
        bf.append(
                QuerySyntax.escapeKeyword(ownerName)
                        + "."
                        + QuerySyntax.escapeKeyword(synonymName));
        bf.append(" FOR ");
        bf.append(
                QuerySyntax.escapeKeyword(targetOwner)
                        + "."
                        + QuerySyntax.escapeKeyword(targetName));
        bf.append(newLine);
    }

    /**
     * Get after ddl
     *
     * @param oldTrigger Trigger
     * @param newTrigger Trigger
     * @return String
     */
    public static String getAlterDDL(Synonym oldSynonym, Synonym newSynonym) {
        String oldName = newSynonym.getName();
        String oldOwnerName = newSynonym.getOwner();
        String targetName = newSynonym.getTargetName();
        String targetOwner = newSynonym.getTargetOwner();
        StringBuffer bf = new StringBuffer();
        bf.append("ALTER SYNONYM ");
        bf.append(
                QuerySyntax.escapeKeyword(oldOwnerName) + "." + QuerySyntax.escapeKeyword(oldName));
        bf.append(" FOR ");
        bf.append(
                QuerySyntax.escapeKeyword(targetOwner)
                        + "."
                        + QuerySyntax.escapeKeyword(targetName));

        String oldComment = oldSynonym.getComment();
        String newCommnet = newSynonym.getComment();
        boolean commentChanged = false;
        if (newCommnet != null && !newCommnet.equals(oldComment)) {
            commentChanged = true;
        }

        if (commentChanged) {
            newCommnet = String.format("'%s'", newCommnet);
            bf.append(String.format(" COMMENT %s", StringUtil.escapeQuotes(newCommnet)));
            bf.append(endLineChar);
            bf.append(newLine);
        }
        bf.append(endLineChar);

        return bf.toString();
    }
}
