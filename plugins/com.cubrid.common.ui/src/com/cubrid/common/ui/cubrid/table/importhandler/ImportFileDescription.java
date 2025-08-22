/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.importhandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Import File Description Container.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-22 created by Kevin Cao
 */
public final class ImportFileDescription {

    private int totalCount;
    private int sheetNum;
    private final List<String> firstRowCols = new ArrayList<String>();
    private List<Integer> itemsNumberOfSheets;

    public ImportFileDescription() {
        // do nothing.
    }

    public ImportFileDescription(int totalCount, int sheetNum, List<String> firstRowCols) {
        this.totalCount = totalCount;
        this.sheetNum = sheetNum;
        setFirstRowCols(firstRowCols);
    }

    /**
     * Retrieves the total row count of file.
     *
     * @return integer
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Set total count
     *
     * @param totalCount of integer
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Get the sheet number.
     *
     * @return integer
     */
    public int getSheetNum() {
        return sheetNum;
    }

    /**
     * Set sheet number.
     *
     * @param sheetNum of integer.
     */
    public void setSheetNum(int sheetNum) {
        this.sheetNum = sheetNum;
    }

    /**
     * Retrieves the first row.
     *
     * @return values of first row
     */
    public List<String> getFirstRowCols() {
        return firstRowCols;
    }

    /**
     * Set first row columns
     *
     * @param firstRowCols of string list.
     */
    public void setFirstRowCols(List<String> firstRowCols) {
        this.firstRowCols.clear();
        if (firstRowCols != null) {
            this.firstRowCols.addAll(firstRowCols);
        }
    }

    /**
     * Get the itemsNumberOfSheets
     *
     * @return the itemsNumberOfSheets
     */
    public List<Integer> getItemsNumberOfSheets() {
        return itemsNumberOfSheets;
    }

    /** @param itemsNumberOfSheets the itemsNumberOfSheets to set */
    public void setItemsNumberOfSheets(List<Integer> itemsNumberOfSheets) {
        this.itemsNumberOfSheets = itemsNumberOfSheets;
    }
}
