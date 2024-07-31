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
package com.cubrid.tool.editor.xml.scanner;

import com.cubrid.tool.editor.ColorManager;
import com.cubrid.tool.editor.WhitespaceDetector;
import com.cubrid.tool.editor.xml.IXMLColorConstants;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

/**
 * XML Tag Scanner
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-1-21 created by Kevin Cao
 */
public class XMLTagDamageRepairScanner extends RuleBasedScanner {

    public XMLTagDamageRepairScanner(ColorManager manager) {
        IToken valueToken =
                new Token(new TextAttribute(manager.getColor(IXMLColorConstants.STRING)));
        IRule[] rules = new IRule[5];
        IToken tagToken = new Token(new TextAttribute(manager.getColor(IXMLColorConstants.TAG)));
        // Add rule for double quotes
        rules[0] = new SingleLineRule("\"", "\"", valueToken, '\\');
        // Add rule for single quotes
        rules[1] = new SingleLineRule("\'", "\'", valueToken, '\\');
        // Add rule for braces
        rules[2] = new SingleLineRule("[", "]", valueToken, '\\');
        rules[3] = new XMLWord(tagToken);
        // Add generic whitespace rule.
        rules[4] = new WhitespaceRule(new WhitespaceDetector());

        setRules(rules);
    }

    /**
     * XML Word Rule.
     *
     * @author Kevin Cao
     * @version 1.0 - 2011-2-24 created by Kevin Cao
     */
    static class XMLWord implements IPredicateRule {

        private final IToken tagToken;

        public XMLWord(IToken tagToken) {
            this.tagToken = tagToken;
        }

        /**
         * Evaluate the xml tag.
         *
         * @param scanner ICharacterScanner
         * @param resume boolean
         * @return the evaluate token.
         */
        public IToken evaluate(ICharacterScanner scanner, boolean resume) {
            int c = scanner.read();
            if ((char) c == '<') {
                c = scanner.read();
                while (c >= 0) {
                    if ((char) c == ' '
                            || (char) c == '	'
                            || (char) c == '\r'
                            || (char) c == '\n'
                            || (char) c == '>') {
                        break;
                    } else if ((char) c == '/') {
                        c = scanner.read();
                        if ((char) c == '>') {
                            break;
                        }
                    }
                    c = scanner.read();
                }
                return tagToken;
            } else if ((char) c == '>') {
                return tagToken;
            } else if ((char) c == '/') {
                c = scanner.read();
                if ((char) c == '>') {
                    return tagToken;
                }
            }
            scanner.unread();
            return Token.UNDEFINED;
        }

        /**
         * Get success token.
         *
         * @return the xml tag token.
         */
        public IToken getSuccessToken() {
            return tagToken;
        }

        /**
         * Evaluate the xml tag.
         *
         * @param scanner ICharacterScanner
         * @return the evaluate token.
         */
        public IToken evaluate(ICharacterScanner scanner) {
            return evaluate(scanner, false);
        }
    }
}
