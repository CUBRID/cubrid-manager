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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * SQL key word rule
 *
 * @author pangqiren 2009-3-2
 */
public class SQLKeyWordRule implements IRule {

    /** The word detector used by this rule */
    protected QueryWordDetector wordDetector;
    /** The default token to be returned on success and if nothing else has been specified. */
    protected IToken fDefaultToken;

    /** The table of predefined words and token for this rule */
    public Map<String, IToken> wordMap = new HashMap<String, IToken>();

    private final StringBuffer strBuffer = new StringBuffer();

    public SQLKeyWordRule(QueryWordDetector detector, IToken defaultToken) {
        wordDetector = detector;
        fDefaultToken = defaultToken;
    }

    /**
     * Add the word
     *
     * @param word String
     * @param token IToken
     */
    public void addWord(String word, IToken token) {
        if (word != null) {
            wordMap.put(word.toLowerCase(Locale.getDefault()), token);
        }
    }

    /**
     * @see
     *     org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     * @param scanner the character scanner to be used by this rule
     * @return the token computed by the rule
     */
    public IToken evaluate(ICharacterScanner scanner) {
        int c = scanner.read();
        if (c != ICharacterScanner.EOF
                && wordDetector.isWordStart((char) c)
                && isPreCharSeparator(scanner)) {
            strBuffer.setLength(0);
            char startChar = (char) c;
            do {
                strBuffer.append((char) c);
                c = scanner.read();
            } while (c != ICharacterScanner.EOF && wordDetector.isWordPart(startChar, (char) c));

            if (c != ICharacterScanner.EOF
                    && c != ' '
                    && c != '\r'
                    && c != '\n'
                    && c != '\t'
                    && c != '('
                    && c != ')'
                    && c != ','
                    && c != ';') {
                strBuffer.append((char) c);
            }
            scanner.unread();

            String word = strBuffer.substring(0, strBuffer.length()).toLowerCase();

            IToken token = (IToken) wordMap.get(word);
            if (token != null) {
                return token;
            }

            if (fDefaultToken.isUndefined()) {
                unreadBuffer(scanner);
            }
            return fDefaultToken;
        }
        scanner.unread();
        return Token.UNDEFINED;
    }

    /**
     * Return whether the previous character is word separator
     *
     * @param scanner ICharacterScanner
     * @return boolean
     */
    private boolean isPreCharSeparator(ICharacterScanner scanner) {
        scanner.unread();
        scanner.unread();
        int ch = scanner.read();
        boolean isSeparator =
                (ch == ICharacterScanner.EOF
                        || ch == ' '
                        || ch == '('
                        || ch == '\r'
                        || ch == '\n'
                        || ch == '\t'
                        || ch == ';'
                        || ch == ','
                        || ch == ')');
        scanner.read();
        return isSeparator;
    }

    /**
     * unread the buffer content.
     *
     * @param scanner ICharacterScanner
     */
    public void unreadBuffer(ICharacterScanner scanner) {
        for (int i = strBuffer.length() - 1; i >= 0; i--) {
            scanner.unread();
        }
    }

    public int getMapSize() {
        return this.wordMap.size();
    }
}
