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
package com.cubrid.tool.editor.dialog;

/**
 * Find and replace option
 *
 * @author pangqiren
 * @version 1.0 - 2011-12-19 created by pangqiren
 */
public class FindReplaceOption {

    private String searchedStr;
    private String replacedStr;

    private boolean isSearchAll = true;
    private boolean isForward = true;
    private boolean isWholeWord;
    private boolean isCaseSensitive;
    private boolean isRegularExpressions;
    private boolean isWrapSearch = true;

    public String getSearchedStr() {
        return searchedStr;
    }

    public void setSearchedStr(String searchedStr) {
        this.searchedStr = searchedStr;
    }

    public String getReplacedStr() {
        return replacedStr;
    }

    public void setReplacedStr(String replacedStr) {
        this.replacedStr = replacedStr;
    }

    public boolean isSearchAll() {
        return isSearchAll;
    }

    public void setSearchAll(boolean isSearchAll) {
        this.isSearchAll = isSearchAll;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean isForward) {
        this.isForward = isForward;
    }

    public boolean isWholeWord() {
        return isWholeWord;
    }

    public void setWholeWord(boolean isWholeWord) {
        this.isWholeWord = isWholeWord;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public boolean isRegularExpressions() {
        return isRegularExpressions;
    }

    public void setRegularExpressions(boolean isRegularExpressions) {
        this.isRegularExpressions = isRegularExpressions;
    }

    public boolean isWrapSearch() {
        return isWrapSearch;
    }

    public void setWrapSearch(boolean isWrapSearch) {
        this.isWrapSearch = isWrapSearch;
    }
}
