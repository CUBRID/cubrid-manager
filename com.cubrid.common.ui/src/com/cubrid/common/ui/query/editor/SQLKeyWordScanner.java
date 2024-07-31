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

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.spi.ResourceManager;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;

/**
 * SQL keywords scanner
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-21 created by pangqiren
 */
public class SQLKeyWordScanner extends RuleBasedScanner {

    public SQLKeyWordScanner() {
        // Create the tokens for keywords, strings, and other (everything else)
        IToken keywordToken =
                new Token(
                        new TextAttribute(
                                ResourceManager.getColor(ISQLPartitions.COLOR_KEYWORD),
                                null, // ResourceManager.getColor(ISQLPartitions.COLOR_BACKGROUND)
                                SWT.BOLD));
        IToken defaultToken =
                new Token(
                        new TextAttribute(ResourceManager.getColor(ISQLPartitions.COLOR_DEFAULT)));

        setDefaultReturnToken(defaultToken);

        List<IRule> rules = new ArrayList<IRule>();

        // Add rule for whitespace
        rules.add(
                new WhitespaceRule(
                        new IWhitespaceDetector() {
                            public boolean isWhitespace(char whitespace) {
                                return Character.isWhitespace(whitespace);
                            }
                        }));

        // Add word rule for keywords, types, and constants.
        SQLKeyWordRule wordRule = new SQLKeyWordRule(new QueryWordDetector(), defaultToken);

        String[] keyWords = QuerySyntax.KEYWORDS;
        for (int i = 0; i < keyWords.length; i++) {
            wordRule.addWord(keyWords[i], keywordToken);
        }
        rules.add(wordRule);

        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }
}
