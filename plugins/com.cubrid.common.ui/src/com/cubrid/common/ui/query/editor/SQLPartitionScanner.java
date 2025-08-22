/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

package com.cubrid.common.ui.query.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * SQL partition scanner
 *
 * @author pangqiren 2009-3-2
 */
public final class SQLPartitionScanner extends RuleBasedPartitionScanner
        implements ISQLPartitions { // NOPMD
    // Partition content type
    private static final String[] TYPES =
            new String[] {
                SQL_SINGLE_LINE_COMMENT,
                SQL_MULTI_LINE_COMMENT,
                SQL_STRING,
                IDocument.DEFAULT_CONTENT_TYPE
            };

    public SQLPartitionScanner() {
        // Create the token for comment partitions
        IToken singleLineComment = new Token(SQL_SINGLE_LINE_COMMENT);
        IToken multiLineComment = new Token(SQL_MULTI_LINE_COMMENT);
        IToken string = new Token(SQL_STRING);

        List<IRule> rules = new ArrayList<IRule>();
        // Add rule for strings.
        // rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        rules.add(new MultiLineRule("'", "'", string, '\\'));
        rules.add(new MultiLineRule("\"", "\"", string, '\\'));
        rules.add(new MultiLineRule("[", "]", string, '\\'));
        rules.add(new MultiLineRule("/*", "*/", multiLineComment));
        rules.add(new EndOfLineRule("//", singleLineComment));
        rules.add(new EndOfLineRule("--", singleLineComment));

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }

    public static String[] getAllTypes() {
        return (String[]) TYPES.clone();
    }
}
