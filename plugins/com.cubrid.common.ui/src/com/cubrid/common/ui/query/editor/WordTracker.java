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

package com.cubrid.common.ui.query.editor;

import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * This class manages word tracker
 *
 * @author wangsl
 * @version 1.0 - 2009-06-24 created by wangsl
 */
public class WordTracker {
    private final List<String> wordBuffer;

    public WordTracker(String[] keyWords) {
        wordBuffer = new LinkedList<String>();
        for (int i = 0; i < keyWords.length; i++) {
            wordBuffer.add(keyWords[i]);
        }
    }

    /**
     * Factory method of WordTracker
     *
     * @return WordTracker with KEYWORDS_CONTENTS_ASSIST
     */
    public static WordTracker getWordTracker() {
        return new WordTracker(QuerySyntax.KEYWORDS_CONTENTS_ASSIST);
    }

    public int getWordCount() {
        return wordBuffer.size();
    }

    /**
     * add word to suggestions
     *
     * @param word String
     * @param queryEditor QueryEditorPart
     * @return suggestions
     */
    public List<String> suggest(String word, DatabaseInfo databaseInfo) {
        List<String> suggestions = new LinkedList<String>();
        if (databaseInfo == null) {
            return suggestions;
        }
        ServerInfo serverInfo =
                databaseInfo.getServerInfo() == null ? null : databaseInfo.getServerInfo();
        boolean isLowerCase = QueryOptions.getKeywordLowercase(serverInfo);

        for (Iterator<String> i = wordBuffer.iterator(); i.hasNext(); ) {
            String currWord = i.next();
            if (currWord.toUpperCase(Locale.getDefault()).startsWith(word)) {
                suggestions.add(isLowerCase ? currWord.toLowerCase(Locale.getDefault()) : currWord);
            }
        }
        return suggestions;
    }
}
