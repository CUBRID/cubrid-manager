/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.util.paramSetter;

import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.jdbc.proxy.driver.CUBRIDClobProxy;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Clob setter
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-17 created by Kevin.Wang
 */
public class ClobSetter extends DefaultParamSetter {
    /**
     * @param stmt PreparedStatement
     * @param idx parameter index
     * @param columnValue ColumnValue
     * @throws SQLException when SQL error.
     */
    public void handle(PreparedStatement stmt, PstmtParameter parameter)
            throws SQLException, ParamSetException {
        Object value = parameter.getParamValue();
        if (value == null || "".equals(value)) {
            stmt.setNull(parameter.getParamIndex(), Types.NULL);
            return;
        }
        Reader reader = null;
        Writer writer = null;
        Clob clob = null;
        String charset = parameter.getCharSet();
        if (StringUtil.isEmpty(charset)) {
            charset = "UTF-8";
        }
        try {
            if (value instanceof Clob) {
                Clob srcClob = (Clob) value;
                reader = srcClob.getCharacterStream();
            } else if (value instanceof InputStream) {
                reader = new InputStreamReader((InputStream) value, charset);
            } else if (parameter.isFileValue()) {
                File file = null;
                if (value instanceof String) {
                    String path = String.valueOf(value);
                    file = new File(path);
                } else if (value instanceof File) {
                    file = (File) value;
                }
                if (file.exists()) {
                    reader = new InputStreamReader(new FileInputStream(file), charset);
                } else {
                    setNull(stmt, parameter.getParamIndex());
                    return;
                }
            } else {
                reader = new StringReader(String.valueOf(value));
            }
            clob = stmt.getConnection().createClob();
            writer = clob.setCharacterStream(1);
            char[] charArr = new char[512];
            int count = reader.read(charArr);
            while (count > 0) {
                writer.write(charArr, 0, count);
                count = reader.read(charArr);
            }
        } catch (Exception e) {
            throw new ParamSetException(e, parameter);
        } finally {
            Closer.close(reader);
            Closer.close(writer);
        }
        stmt.setClob(parameter.getParamIndex(), ((CUBRIDClobProxy) clob).getProxyObj());
    }
}
