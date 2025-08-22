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
package com.cubrid.common.ui.query.result;

import com.cubrid.common.ui.query.control.ColumnInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Query result filter setting
 *
 * @author pangqiren
 * @version 1.0 - 2013-3-19 created by pangqiren
 */
public class QueryResultFilterSetting {
    public enum MatchType {
        MATCH_FROM_START,
        MATCH_EXACTLY,
        MATCH_ANYWHERE
    }

    private List<ColumnInfo> filterColumnInfoList = new ArrayList<ColumnInfo>();
    private boolean isSearchAllColumn = true;
    private boolean isCaseSensitive = false;
    private boolean isInCaseSensitive = true;
    private boolean isUsingRegex = false;
    private boolean isUsingWildCard = false;
    private MatchType matchType = MatchType.MATCH_ANYWHERE;
    private String content;

    public List<ColumnInfo> getFilterColumnInfoList() {
        return filterColumnInfoList;
    }

    public void setFilterColumnInfoList(List<ColumnInfo> filterColumnInfoList) {
        this.filterColumnInfoList = filterColumnInfoList;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public boolean isUsingRegex() {
        return isUsingRegex;
    }

    public void setUsingRegex(boolean isUsingRegex) {
        this.isUsingRegex = isUsingRegex;
    }

    public boolean isInCaseSensitive() {
        return isInCaseSensitive;
    }

    public void setInCaseSensitive(boolean isInCaseSensitive) {
        this.isInCaseSensitive = isInCaseSensitive;
    }

    public boolean isUsingWildCard() {
        return isUsingWildCard;
    }

    public void setUsingWildCard(boolean isUsingWildCard) {
        this.isUsingWildCard = isUsingWildCard;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public boolean isSearchAllColumn() {
        return isSearchAllColumn;
    }

    public void setSearchAllColumn(boolean isSearchAllColumn) {
        this.isSearchAllColumn = isSearchAllColumn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
