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
package com.cubrid.tool.editor.action;

import com.cubrid.tool.editor.Messages;
import com.cubrid.tool.editor.xml.XMLEditor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Format XML Action.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-2-17 created by Kevin Cao
 */
public class FormatXMLAction extends Action {

    private final XMLEditor editor;

    public FormatXMLAction(String title, XMLEditor editor) {
        Assert.isNotNull(editor);
        this.setText(title);
        this.editor = editor;
    }

    /** Format XML strings. */
    public void run() {
        // format editor.
        try {
            ByteArrayOutputStream sw = new ByteArrayOutputStream();
            writeTo(sw, editor.getDocument().get(), "utf-8");
            String string = new String(sw.toByteArray(), "utf-8");
            editor.getDocument().set(string);
            sw.close();
        } catch (UnsupportedEncodingException e) {
            showErrorMessage();
        } catch (IOException e) {
            showErrorMessage();
        } catch (DocumentException e) {
            showErrorMessage();
        }
    }

    /** Show error message in a dialog. */
    private void showErrorMessage() {
        Shell shell = editor.getSite().getShell();
        MessageDialog dialog =
                new MessageDialog(
                        shell,
                        Messages.titleError,
                        null,
                        Messages.invalidateXML,
                        MessageDialog.WARNING,
                        new String[] {Messages.btnClose},
                        0);
        dialog.open();
    }

    /**
     * Format XML strings to a output stream.
     *
     * @param out OutputStream
     * @param content the content to be formated.
     * @param encoding String
     * @throws DocumentException when document error raised.
     * @throws IOException when IO errors.
     */
    public static void writeTo(OutputStream out, String content, String encoding)
            throws DocumentException, IOException {
        StringReader reader = new StringReader(content);
        SAXReader xmlReader = new SAXReader();
        Document doc = xmlReader.read(reader);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(encoding);
        format.setIndent(true);
        format.setIndent(" ");
        format.setIndentSize(4);
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(doc);
        writer.flush();
        writer.close();
        reader.close();
    }
}
