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
package com.cubrid.common.ui.er.control;

import com.cubrid.common.core.task.AbstractTask;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.slf4j.Logger;

/**
 * Export ERD all picture as a picture
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-12-5 created by Yu Guojia
 */
public class ExportPictureTask extends AbstractTask {
    private static final Logger LOGGER = LogUtil.getLogger(ExportPictureTask.class);

    private final ERSchemaEditor erSchemaEditor;
    private final String filename;
    private boolean isCancel = false;
    private boolean isSuccess = false;

    public ExportPictureTask(ERSchemaEditor erSchemaEditor, String filename) {
        this.erSchemaEditor = erSchemaEditor;
        this.filename = filename;
    }

    public void cancel() {
        isCancel = true;
    }

    public void finish() {
        isSuccess = true;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    private int getImageFormat(String suffix) {
        if ("jpg".equalsIgnoreCase(suffix)) {
            return SWT.IMAGE_JPEG;
        } else if ("bmp".equalsIgnoreCase(suffix)) {
            return SWT.IMAGE_BMP;
        } else if ("png".equalsIgnoreCase(suffix)) {
            return SWT.IMAGE_PNG;
        } else if ("tif".equalsIgnoreCase(suffix)) {
            return SWT.IMAGE_TIFF;
        }

        return SWT.IMAGE_JPEG;
    }

    public void execute() {
        ScalableFreeformRootEditPart rootPart = erSchemaEditor.getRootEditPart();
        // To ensure every graphical element is included
        IFigure figure = rootPart.getLayer(ScalableFreeformRootEditPart.PRINTABLE_LAYERS);
        String suffix = filename.substring(filename.indexOf("."));
        byte[] data = createImage(figure, getImageFormat(suffix));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            fos.write(data);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private byte[] createImage(IFigure figure, int format) {
        Rectangle r = figure.getBounds();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        Image image = null;
        GC gc = null;
        Graphics g = null;
        try {
            image = new Image(null, r.width, r.height);
            gc = new GC(image);
            g = new SWTGraphics(gc);
            g.translate(r.x * -1, r.y * -1);
            figure.paint(g);
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.data = new ImageData[] {image.getImageData()};
            imageLoader.save(result, format);
        } finally {
            if (g != null) {
                g.dispose();
            }
            if (gc != null) {
                gc.dispose();
            }
            if (image != null) {
                image.dispose();
            }
        }
        return result.toByteArray();
    }
}
