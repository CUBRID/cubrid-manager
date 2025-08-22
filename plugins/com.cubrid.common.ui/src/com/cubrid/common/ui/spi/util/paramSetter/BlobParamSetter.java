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
package com.cubrid.common.ui.spi.util.paramSetter;

import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.jdbc.proxy.driver.CUBRIDBlobProxy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * BlobHandler
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-17 created by Kevin.Wang
 */
public class BlobParamSetter extends DefaultParamSetter {

    public void handle(PreparedStatement stmt, PstmtParameter parameter)
            throws SQLException, ParamSetException {
        Object value = parameter.getParamValue();
        if (value == null || "".equals(value)) {
            stmt.setNull(parameter.getParamIndex(), Types.NULL);
            return;
        }
        String charset = parameter.getCharSet();
        if (StringUtil.isEmpty(charset)) {
            charset = StringUtil.getDefaultCharset();
        }
        InputStream in = null;
        OutputStream out = null;
        Blob blob;
        try {
            if (value instanceof Blob) {
                Blob srcBlob = (Blob) value;
                in = srcBlob.getBinaryStream();
            } else if (value instanceof InputStream) {
                in = (InputStream) value;
            } else if (value instanceof byte[]) {
                in = new ByteArrayInputStream((byte[]) value);
            } else if (parameter.isFileValue()) {
                File file = null;
                if (value instanceof String) {
                    String path = String.valueOf(value);
                    file = new File(path);
                } else if (value instanceof File) {
                    file = (File) value;
                }
                if (file.exists()) {
                    in = new FileInputStream(file);
                } else {
                    setNull(stmt, parameter.getParamIndex());
                    return;
                }

            } else {
                String str = String.valueOf(value);
                in = new ByteArrayInputStream(str.getBytes(charset));
            }
            blob = stmt.getConnection().createBlob();
            out = blob.setBinaryStream(1);
            byte[] data = new byte[512];
            int count;
            while ((count = in.read(data)) != -1) {
                out.write(data, 0, count);
            }
        } catch (Exception e) {
            throw new ParamSetException(e, parameter);
        } finally {
            Closer.close(in);
            Closer.close(out);
        }
        stmt.setBlob(parameter.getParamIndex(), ((CUBRIDBlobProxy) blob).getProxyObj());
    }
}
