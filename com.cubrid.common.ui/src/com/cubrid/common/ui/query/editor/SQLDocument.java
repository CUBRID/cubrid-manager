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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.slf4j.Logger;

/**
 * This class adds persistence to the Document class
 *
 * @author wangsl
 * @version 1.0 - 2009-06-24 created by wangsl
 */
public class SQLDocument extends Document implements IDocumentListener {
    private static final Logger LOGGER = LogUtil.getLogger(SQLDocument.class);
    private String fileName;
    private String encoding = StringUtil.getDefaultCharset();

    private boolean dirty;

    /** The constructor */
    public SQLDocument() {
        addDocumentListener(this);
    }

    /**
     * Gets whether this document is dirty
     *
     * @return boolean
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Gets the file name
     *
     * @return String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name
     *
     * @param fileName String
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Saves the file
     *
     * @throws IOException if any problems
     */
    public void save() throws IOException {
        if (fileName == null) {
            throw new IllegalStateException("");
        }
        BufferedWriter out = null;
        try {
            out =
                    new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(fileName), encoding));
            out.write(get());
            dirty = false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Opens the file
     *
     * @throws IOException if any problems
     */
    public void open() throws IOException { // FIXME move this logic to core module
        if (fileName == null) {
            throw new IllegalStateException(Messages.notSaveNull);
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
            StringBuffer buf = new StringBuffer();
            String line = in.readLine();
            while (line != null) {
                buf.append(line + StringUtil.NEWLINE);
                line = in.readLine();
            }
            set(buf.toString());
            dirty = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    /** Clears the file's contents */
    public void clear() {
        set("");
        fileName = "";
        dirty = false;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Called when the document is about to be changed
     *
     * @param event the event
     */
    public void documentAboutToBeChanged(DocumentEvent event) {
        // empty
    }

    /**
     * Called when the document changes
     *
     * @param event the event
     */
    public void documentChanged(DocumentEvent event) {
        // Document has changed; make it dirty
        dirty = true;
    }
}
