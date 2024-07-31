/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.cubrid.table.progress;

import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

/** @author CHOE JUNGYEON */
public abstract class ExportTableDefinitionLayoutType { // FIXME move this logic to core module
    private ExportTableDefinitionProgress progress;

    public ExportTableDefinitionLayoutType(ExportTableDefinitionProgress progress) {
        this.progress = progress;
    }

    private static final Logger LOGGER = LogUtil.getLogger(ExportTableDefinitionLayoutType.class);

    protected WritableCellFormat normalCellStyle = getNormalCenterAlignCellStyle();
    protected WritableCellFormat normalLeftAlignCellStyle = getNormalLeftAlignCellStyle();
    protected WritableCellFormat normalRightAlignCellStyle = getNormalRightAlignCellStyle();
    protected WritableCellFormat boldCellStyle = getBoldCenterAlignCellStyle();
    protected String dateString = new SimpleDateFormat("yyyy.MM.dd").format(new Date());

    protected ExportTableDefinitionProgress getProgressObject() {
        return progress;
    }

    /**
     * Generate table name sheet
     *
     * @param wwb
     * @param conn
     * @param exportSchemaInfoList
     * @param monitor
     * @throws Exception
     */
    public abstract void generateTableDetailSheets(
            WritableWorkbook wwb,
            Connection conn,
            List<SchemaInfo> exportSchemaInfoList,
            IProgressMonitor monitor)
            throws Exception;

    /**
     * Generate table name sheet
     *
     * @param wwb
     * @param exportTableNames
     * @throws Exception
     */
    public abstract void generateTableNamesSheet(
            WritableWorkbook wwb, List<String> exportTableNames) throws Exception;

    /**
     * GetNormalCell
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getNormalCenterAlignCellStyle() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error("", e);
        }
        return format;
    }

    /**
     * GetNormalLeftAlignCellStyle
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getNormalLeftAlignCellStyle() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.LEFT);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error("", e);
        }
        return format;
    }

    /**
     * GetNormalRightAlignCellStyle
     *
     * @return
     */
    public static WritableCellFormat getNormalRightAlignCellStyle() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.RIGHT);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error("", e);
        }
        return format;
    }

    /**
     * GetNormalCell
     *
     * @return WritableCellFormat
     */
    public static WritableCellFormat getBoldCenterAlignCellStyle() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            font.setBoldStyle(WritableFont.BOLD);
            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            format.setBackground(jxl.format.Colour.GREY_25_PERCENT);
            format.setWrap(true);
        } catch (WriteException e) {
            LOGGER.error("", e);
        }
        return format;
    }
}
