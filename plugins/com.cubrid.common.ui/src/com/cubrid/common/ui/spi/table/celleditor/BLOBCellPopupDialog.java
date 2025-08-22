/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.table.celleditor;

import com.cubrid.common.core.task.AbstractUITask;
import com.cubrid.common.core.util.Closer;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.table.FileDialogUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.FileTypeUtils;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;
import com.cubrid.cubridmanager.core.cubrid.table.model.FormatDataResult;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * The table cell content edited dialog for BLOB and bit varying
 *
 * <p>BLOBCellPopupDialog
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-12 created by Kevin.Wang
 */
public class BLOBCellPopupDialog extends CMTitleAreaDialog implements ICellPopupEditor {

    private StyledText columnValueText;
    private CellValue newValue;
    private Composite dataComposite;
    private Button textBtn;
    private Button imageBtn;
    private Composite imageCanvas;
    private ImageViewer imageViewer;
    private Combo fileCharsetCombo;
    private ModifyListener listener;

    private Label sizeLabel;
    private Button binaryBtn;
    private Button hexBtn;
    private ToolItem openByExternalBtn;
    private Menu programMenu;
    private MenuItem programMenuItem;

    private Object currValue;
    private ColumnInfo columnInfo;
    protected CellValue value;
    protected Button setNullBtn;
    protected Button importBtn;
    protected Button exportBtn;
    protected boolean isEditable = true;
    protected String defaultCharset;

    /**
     * The Constructor
     *
     * @param parent Shell
     * @param cellType CellType
     */
    public BLOBCellPopupDialog(
            Shell parent,
            ColumnInfo columnInfo,
            String defaultCharset,
            CellValue value,
            boolean isEditable) {
        super(parent);

        this.columnInfo = columnInfo;
        this.defaultCharset = defaultCharset;
        this.value = value;
        this.isEditable = isEditable;
    }

    /**
     * Create dialog area
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        dataComposite = new Composite(parentComp, SWT.NONE);
        dataComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        dataComposite.setLayout(layout);

        Composite leftBtnComposite = new Composite(dataComposite, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 3;
        leftBtnComposite.setLayout(rowLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.BEGINNING;
        leftBtnComposite.setLayoutData(gridData);

        textBtn = new Button(leftBtnComposite, SWT.RADIO);
        textBtn.setText(Messages.btnText);
        textBtn.setSelection(true);
        textBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        switchShowStyle();
                    }
                });

        binaryBtn = new Button(leftBtnComposite, SWT.RADIO);
        binaryBtn.setText(Messages.btnBinary);
        binaryBtn.setSelection(false);
        binaryBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        switchShowStyle();
                    }
                });

        hexBtn = new Button(leftBtnComposite, SWT.RADIO);
        hexBtn.setText(Messages.btnHex);
        hexBtn.setSelection(false);
        hexBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        switchShowStyle();
                    }
                });

        imageBtn = new Button(leftBtnComposite, SWT.RADIO);
        imageBtn.setText(Messages.btnImage);
        imageBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        switchShowStyle();
                    }
                });

        Composite rightBtnComposite = new Composite(dataComposite, SWT.NONE);
        rowLayout = new RowLayout();
        rowLayout.spacing = 5;
        rightBtnComposite.setLayout(rowLayout);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.END;
        gridData.horizontalSpan = 2;
        rightBtnComposite.setLayoutData(gridData);

        setNullBtn = new Button(rightBtnComposite, SWT.CHECK);
        setNullBtn.setText(Messages.btnSetNull);
        setNullBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        changeBtnStatus();
                        updateSizeLabel();
                    }
                });
        importBtn = new Button(rightBtnComposite, SWT.PUSH);
        importBtn.setText(Messages.btnImport);
        importBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        File file = FileDialogUtils.getImportedFile(getShell(), null);
                        if (null != file && file.getName().length() > 0) {
                            importData(file.getPath());
                            try {
                                showContentByText();
                            } catch (Exception ex) {
                                CommonUITool.openErrorBox(ex.getMessage());
                            }
                        }
                    }
                });

        exportBtn = new Button(rightBtnComposite, SWT.PUSH);
        exportBtn.setText(Messages.btnExport);
        exportBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (currValue == null) {
                            CommonUITool.openWarningBox(Messages.noDataExport);
                            return;
                        }
                        if (currValue instanceof String) {
                            String str = (String) currValue;
                            if (str.trim().length() == 0) {
                                CommonUITool.openWarningBox(Messages.noDataExport);
                                return;
                            }
                        }
                        File file =
                                FileDialogUtils.getDataExportedFile(
                                        getShell(),
                                        new String[] {"*.*"},
                                        new String[] {"*.*"},
                                        null);
                        if (null != file && file.getName().length() > 0) {
                            exportData(file.getPath(), false);
                        }
                    }
                });

        fileCharsetCombo = new Combo(rightBtnComposite, SWT.NONE);
        String charset = value == null ? null : value.getFileCharset();
        fileCharsetCombo.setItems(QueryOptions.getAllCharset(charset));

        if (charset == null || charset.trim().length() == 0) {
            charset = defaultCharset;
        }
        if (charset == null || charset.trim().length() == 0) {
            charset = System.getProperty("file.encoding");
        }
        fileCharsetCombo.setText(charset);

        fileCharsetCombo.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        if (imageBtn.getSelection() || setNullBtn.getSelection()) {
                            return;
                        }
                        String charset = fileCharsetCombo.getText();
                        if (charset == null || charset.trim().length() == 0) {
                            return;
                        }
                        try {
                            " ".getBytes(charset);
                        } catch (UnsupportedEncodingException ex) {
                            return;
                        }
                        if (currValue instanceof File) {
                            File file = (File) currValue;
                            importData(file.getAbsolutePath());
                        } else if (currValue instanceof byte[]) {
                            columnValueText.removeModifyListener(listener);
                            String content =
                                    StringUtil.converByteToString(
                                            (byte[]) currValue, fileCharsetCombo.getText());
                            columnValueText.setText(content);
                            columnValueText.addModifyListener(listener);
                        }

                        try {
                            showContentByText();
                        } catch (Exception ex) {
                            CommonUITool.openErrorBox(ex.getMessage());
                        }
                    }
                });

        createTextComposite();
        return dataComposite;
    }
    /** Create the text composite */
    private void createTextComposite() {
        columnValueText =
                new StyledText(
                        dataComposite,
                        SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        // columnValueText.setEditable(false);
        if (value.hasLoadAll()) {
            CommonUITool.registerContextMenu(columnValueText, isEditable);
        }

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 3;
        gd.heightHint = 280;
        gd.widthHint = 500;
        columnValueText.setLayoutData(gd);

        dataComposite.layout();
    }

    /** Create the image canvas */
    private void createImageCanvas() {
        imageCanvas = new Composite(dataComposite, SWT.NONE);
        imageCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
        imageCanvas.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, -1));
        imageCanvas.setLayout(new FillLayout());

        InputStream in = null;
        try {
            if (currValue instanceof File) {
                in = new FileInputStream((File) currValue);
            } else if (currValue instanceof byte[]) {
                in = new ByteArrayInputStream((byte[]) currValue);
            }
            if (in == null) {
                imageCanvas.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                return;
            }
            ImageLoader loader = new ImageLoader();
            ImageData[] imageDatas = loader.load(in);
            if (imageDatas == null || imageDatas.length == 0) {
                imageCanvas.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                return;
            }
            imageViewer = new ImageViewer(imageCanvas);

            if (imageDatas.length == 1) {
                imageViewer.setImage(imageDatas[0]);
            } else {
                imageViewer.setImages(imageDatas, loader.repeatCount);
            }
            imageViewer.pack();
        } catch (Exception ex) {
            imageCanvas.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        } finally {
            Closer.close(in);
        }

        dataComposite.layout();
    }

    /** Initial the value */
    private void initValue() {
        if (value == null || value.getValue() == null || NULL_VALUE.equals(value.getValue())) {
            setNullBtn.setSelection(true);
            changeBtnStatus();
        } else {
            Object obj = value.getValue();
            currValue = obj;
            String fileType = null;
            if (obj instanceof String) {
                columnValueText.setText((String) obj);
                String str = (String) obj;
                if (DBAttrTypeFormatter.isBinaryString(str)) {
                    textBtn.setSelection(false);
                    binaryBtn.setSelection(true);
                    hexBtn.setSelection(false);
                } else if (DBAttrTypeFormatter.isHexString(str)) {
                    textBtn.setSelection(false);
                    binaryBtn.setSelection(false);
                    hexBtn.setSelection(true);
                }
            } else if (obj instanceof byte[]) {
                String content =
                        StringUtil.converByteToString((byte[]) obj, fileCharsetCombo.getText());
                columnValueText.setText(content);
                fileType = FileTypeUtils.getFileType((byte[]) obj);
            }
            if (fileType != null && FileTypeUtils.isImage(fileType)) {
                switchImageStyle();
            }
        }

        updateSizeLabel();

        listener =
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        if (validate()) {
                            if (isEditable && getButton(IDialogConstants.OK_ID) != null) {
                                getButton(IDialogConstants.OK_ID).setEnabled(true);
                            }
                        } else {
                            if (isEditable && getButton(IDialogConstants.OK_ID) != null) {
                                getButton(IDialogConstants.OK_ID).setEnabled(false);
                            }
                        }

                        String content = columnValueText.getText();
                        if (textBtn.getSelection()) {
                            byte[] value = StringUtil.stringToByte(content);
                            currValue = value;
                        } else if (binaryBtn.getSelection()) {
                            int beginIndex = content.indexOf("'");
                            int endIndex = content.lastIndexOf("'");
                            if (beginIndex > 0 && endIndex > beginIndex) {
                                content = content.substring(beginIndex + 1, endIndex);
                            }
                            byte[] value = null;
                            try {
                                value = DBAttrTypeFormatter.getBitBytes(content, 2);
                            } catch (Exception ex) {
                                setErrorMessage(Messages.errDataInvalid);
                                if (isEditable && getButton(IDialogConstants.OK_ID) != null) {
                                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                                }
                            }
                            currValue = value;
                        } else if (hexBtn.getSelection()) {
                            int beginIndex = content.indexOf("'");
                            int endIndex = content.lastIndexOf("'");
                            if (beginIndex > 0 && endIndex > beginIndex) {
                                content = content.substring(beginIndex + 1, endIndex);
                            }
                            byte[] value = null;
                            try {
                                value = DBAttrTypeFormatter.getBitBytes(content, 16);
                            } catch (Exception ex) {
                                setErrorMessage(Messages.errDataInvalid);
                                if (isEditable && getButton(IDialogConstants.OK_ID) != null) {
                                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                                }
                            }
                            currValue = value;
                        }
                        updateSizeLabel();
                    }
                };
        if (columnValueText != null) {
            columnValueText.addModifyListener(listener);
        }
    }

    private boolean validate() {
        setErrorMessage(null);
        String content = "";
        if (isEditable && columnValueText != null && !columnValueText.isDisposed()) {
            content = columnValueText.getText();
            if (DataType.isBitDataType(columnInfo.getType())
                    || DataType.isBitVaryingDataType(columnInfo.getType())) {
                String dataType =
                        DataType.makeType(
                                columnInfo.getType(),
                                columnInfo.getChildElementType(),
                                columnInfo.getPrecision(),
                                columnInfo.getScale());
                FormatDataResult result =
                        DBAttrTypeFormatter.format(
                                dataType, content, null, false, fileCharsetCombo.getText(), true);
                if (!result.isSuccess()) {
                    String msg =
                            Messages.bind(
                                    com.cubrid.common.ui.query.Messages.errTextTypeNotMatch,
                                    dataType);
                    setErrorMessage(msg);
                    return false;
                }
            }
        }
        return true;
    }

    /** Update the size label */
    private void updateSizeLabel() {
        long size = 0;
        if (setNullBtn.getSelection()) {
            size = 0;

        } else if (currValue instanceof String) {
            String str = (String) currValue;
            size = DBAttrTypeFormatter.getStringByteSize(str, fileCharsetCombo.getText());
        } else if (currValue instanceof byte[]) {
            byte[] bytes = (byte[]) currValue;
            size = bytes.length;
        }
        sizeLabel.setText("Size: " + StringUtil.getSizeString(size));
    }

    /** Dispose the image canvas */
    private void disposeImageCanvas() {
        if (imageViewer != null && !imageViewer.isDisposed()) {
            imageViewer.dispose();
            imageViewer = null;
        }
        if (imageCanvas != null && !imageCanvas.isDisposed()) {
            imageCanvas.dispose();
            imageCanvas = null;
        }
    }

    /** Switch the show style */
    private void switchShowStyle() {
        if (textBtn.getSelection() || binaryBtn.getSelection() || hexBtn.getSelection()) {
            imageBtn.setSelection(false);
            disposeImageCanvas();

            if (columnValueText == null || columnValueText.isDisposed()) {
                createTextComposite();
            }

            try {
                showContentByText();
            } catch (Exception ex) {
                CommonUITool.openErrorBox(ex.getMessage());
            }
        } else {
            switchImageStyle();
        }
        dataComposite.layout(true);
    }

    /** Switch the image style */
    private void switchImageStyle() {
        textBtn.setSelection(false);
        binaryBtn.setSelection(false);
        hexBtn.setSelection(false);

        imageBtn.setSelection(true);
        if (columnValueText != null && !columnValueText.isDisposed()) {
            columnValueText.dispose();
            columnValueText = null;
        }
        if (imageCanvas == null || imageCanvas.isDisposed()) {
            createImageCanvas();
        }
    }

    /**
     * Switch the text style
     *
     * @throws Exception The exception
     */
    private void showContentByText() throws Exception {
        if (imageBtn.getSelection()) {
            return;
        }

        if (columnValueText == null || columnValueText.isDisposed()) {
            return;
        }

        // String content = columnValueText.getText();
        String charset = fileCharsetCombo.getText();
        if (charset == null || charset.trim().length() == 0) {
            charset = System.getProperty("file.encoding");
        }
        String content = null;

        if (textBtn.getSelection()) {
            if (currValue != null) {
                if (currValue instanceof String) {
                    content = (String) currValue;
                } else if (currValue instanceof byte[]) {
                    content = StringUtil.converByteToString((byte[]) currValue, charset);
                }
            } else {
                content = "";
            }
        } else if (binaryBtn.getSelection()) {
            if (currValue != null) {
                if (currValue instanceof String) {
                    content = (String) currValue;
                    if (DBAttrTypeFormatter.isBinaryString(content)) {
                        // do not handle
                    } else if (DBAttrTypeFormatter.isHexString(content)) {
                        content = DBAttrTypeFormatter.getInnerString(content);
                        byte[] bytes = DBAttrTypeFormatter.getBytes(content, 16);
                        content = DBAttrTypeFormatter.getBinaryString(bytes);
                        content = "B'" + content + "'";
                    } else {
                        byte[] bytes = content.getBytes(charset);
                        if (bytes != null) {
                            content = DBAttrTypeFormatter.getBinaryString(bytes);
                            content = "B'" + content + "'";
                        }
                    }
                } else if (currValue instanceof byte[]) {
                    content = DBAttrTypeFormatter.getBinaryString((byte[]) currValue);
                    content = "B'" + content + "'";
                }
            } else {
                content = "B''";
            }
        } else if (hexBtn.getSelection()) {
            if (currValue != null) {
                if (currValue instanceof String) {
                    content = (String) currValue;
                    if (DBAttrTypeFormatter.isBinaryString(content)) {
                        content = DBAttrTypeFormatter.getInnerString(content);
                        byte[] bytes = DBAttrTypeFormatter.getBytes(content, 2);
                        content = DBAttrTypeFormatter.getHexString(bytes);
                        content = "X'" + content + "'";
                    } else if (DBAttrTypeFormatter.isHexString(content)) {
                        // do not handle
                    } else {
                        byte[] bytes = content.getBytes(charset);
                        if (bytes != null) {
                            content = DBAttrTypeFormatter.getHexString(bytes);
                            content = "X'" + content + "'";
                        }
                    }
                } else if (currValue instanceof byte[]) {
                    content = DBAttrTypeFormatter.getHexString((byte[]) currValue);
                    content = "X'" + content + "'";
                }
            } else {
                content = "X''";
            }
        }

        columnValueText.removeModifyListener(listener);
        columnValueText.setText(content);
        columnValueText.addModifyListener(listener);
    }

    /** Change button status */
    private void changeBtnStatus() {
        if (setNullBtn.getSelection()) {
            importBtn.setEnabled(false);
            textBtn.setEnabled(false);
            binaryBtn.setEnabled(false);
            hexBtn.setEnabled(false);
            imageBtn.setEnabled(false);
            if (columnValueText != null) {
                columnValueText.setEnabled(false);
            }
        } else {
            importBtn.setEnabled(true);
            textBtn.setEnabled(true);
            binaryBtn.setEnabled(true);
            hexBtn.setEnabled(true);
            imageBtn.setEnabled(true);
            if (columnValueText != null) {
                columnValueText.setEnabled(true);
            }
        }
    }

    /**
     * Import the data from file
     *
     * @param filePath String
     */
    private void importData(final String filePath) {
        byte[] bytes = FileUtil.readBinaryData(new File(filePath));
        currValue = bytes;
        if (columnValueText != null) {
            String content = StringUtil.converByteToString(bytes, fileCharsetCombo.getText());
            if (listener != null) {
                columnValueText.removeModifyListener(listener);
            }
            columnValueText.setText(content);
            if (listener != null) {
                columnValueText.addModifyListener(listener);
            }
        }
        if (imageCanvas != null) {
            disposeImageCanvas();
            createImageCanvas();
            dataComposite.layout(true);
        }
        updateSizeLabel();
    }

    /**
     * Export data to file
     *
     * @param isOpenProgram boolean
     * @param filePath String
     */
    private boolean exportData(final String filePath, final boolean isOpenProgram) {
        final String charsetName = fileCharsetCombo.getText();
        if (currValue instanceof String) {
            try {
                "".getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                CommonUITool.openErrorBox(Messages.errCharset);
                return false;
            }
        }

        AbstractUITask task =
                new AbstractUITask() {
                    boolean isSuccess = false;

                    public void execute(final IProgressMonitor monitor) {
                        BufferedWriter writer = null;
                        FileOutputStream out = null;
                        InputStream in = null;
                        try {
                            if (currValue instanceof String) {
                                String content = (String) currValue;
                                if (DBAttrTypeFormatter.isBinaryString(content)) {
                                    content = DBAttrTypeFormatter.getInnerString(content);
                                    byte[] bytes = DBAttrTypeFormatter.getBytes(content, 2);
                                    content = new String(bytes, charsetName);

                                } else if (DBAttrTypeFormatter.isHexString(content)) {
                                    content = DBAttrTypeFormatter.getInnerString(content);
                                    byte[] bytes = DBAttrTypeFormatter.getBytes(content, 16);
                                    content = new String(bytes, charsetName);
                                }

                                writer =
                                        new BufferedWriter(
                                                new OutputStreamWriter(
                                                        new FileOutputStream(filePath),
                                                        charsetName));
                                writer.write(content);
                                writer.flush();

                            } else if (currValue instanceof File) {
                                out = new FileOutputStream(filePath);
                                in = new FileInputStream((File) currValue);
                                byte[] bytes = new byte[1024];
                                int len = 0;
                                while ((len = in.read(bytes)) != -1) {
                                    out.write(bytes, 0, len);
                                }
                                out.flush();
                            } else if (currValue instanceof byte[]) {
                                out = new FileOutputStream(filePath);
                                byte[] bytes = (byte[]) currValue;
                                out.write(bytes, 0, bytes.length);
                                out.flush();
                            }
                            isSuccess = true;
                        } catch (Exception e) {
                            errorMsg = e.getMessage();
                        } finally {
                            if (writer != null) {
                                try {
                                    writer.close();
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                        }
                    }

                    public void cancel() {
                        // empty
                    }

                    public void finish() {
                        // empty
                    }

                    public boolean isCancel() {
                        return false;
                    }

                    public boolean isSuccess() {
                        return isSuccess;
                    }
                };

        TaskExecutor taskExecutor = new CommonTaskExec(Messages.msgExportFieldData);
        taskExecutor.addTask(task);
        new ExecTaskWithProgress(taskExecutor).exec();
        if (taskExecutor.isSuccess()) {
            if (isOpenProgram) {
                return true;
            }
            CommonUITool.openInformationBox(
                    getShell(), Messages.titleSuccess, Messages.msgExportSuccess);
            return true;
        }
        return false;
    }

    /** Load the program menus */
    private void loadProgramMenu() {
        if (programMenu != null) {
            programMenu.dispose();
        }
        programMenu = new Menu(getShell(), SWT.POP_UP);
        String fileType = getFileType();

        // Load the default application
        if (fileType != null && fileType.trim().length() > 0) {
            final Program program = Program.findProgram(fileType);
            if (program != null
                    && program.getName() != null
                    && program.getName().trim().length() > 0) {
                programMenuItem = new MenuItem(programMenu, SWT.NONE);
                programMenuItem.setText(program.getName());
                ImageData imageData = program.getImageData();
                if (imageData != null) {
                    final Image image = new Image(getShell().getDisplay(), imageData);
                    programMenuItem.setImage(image);
                    programMenu.addDisposeListener(
                            new DisposeListener() {

                                public void widgetDisposed(DisposeEvent e) {
                                    image.dispose();
                                }
                            });
                }
                programMenuItem.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                openByExternalProgram(program);
                            }
                        });
                new MenuItem(programMenu, SWT.SEPARATOR);
            }
        }

        // Load the other applications
        Program[] programs = Program.getPrograms();
        if (programs != null) {
            for (final Program program : programs) {
                String name = program.getName();
                if (name == null || name.trim().length() == 0) {
                    continue;
                }
                if (programMenuItem != null && programMenuItem.getText().equals(name.trim())) {
                    continue;
                }
                final MenuItem selectProgramMenuItem = new MenuItem(programMenu, SWT.NONE);

                selectProgramMenuItem.setText(name);
                ImageData imageData = program.getImageData();
                if (imageData != null) {
                    final Image image = new Image(getShell().getDisplay(), imageData);
                    selectProgramMenuItem.setImage(image);
                    selectProgramMenuItem.addDisposeListener(
                            new DisposeListener() {

                                public void widgetDisposed(DisposeEvent e) {
                                    image.dispose();
                                }
                            });
                }
                selectProgramMenuItem.addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                openByExternalProgram(program);
                            }
                        });
            }
        }

        Rectangle rect = openByExternalBtn.getBounds();
        Point pt = new Point(rect.x, rect.y + rect.height);
        pt = openByExternalBtn.getParent().toDisplay(pt);
        programMenu.setLocation(pt);
        programMenu.setVisible(true);
    }

    /**
     * Get the file type
     *
     * @return String
     */
    private String getFileType() {
        String fileType = ".txt";
        if (currValue instanceof byte[]) {
            byte[] bytes = (byte[]) currValue;
            fileType = FileTypeUtils.getFileType(bytes);
        }
        return fileType;
    }

    /** Open the external program */
    private void openByExternalProgram() {
        if (setNullBtn.getSelection() || currValue == null) {
            return;
        }

        String fileType = getFileType();
        if (fileType != null && fileType.trim().length() > 0) {
            final Program program = Program.findProgram(fileType);
            if (program != null) {
                openByExternalProgram(program);
                return;
            }
        }
        loadProgramMenu();
    }

    /**
     * Open the file by the external program
     *
     * @param program Program
     */
    private void openByExternalProgram(Program program) {
        if (setNullBtn.getSelection()) {
            return;
        }
        if (currValue instanceof File) {
            File file = (File) currValue;
            program.execute(file.getAbsolutePath());
        } else {
            String fileType = getFileType();
            String filePath = FileUtil.getDefaultTempDataFilePath();
            String tempFilePath = FileUtil.getOnlyTemporaryFile(filePath);
            if (fileType != null && fileType.trim().length() > 0) {
                tempFilePath += "." + fileType;
            }
            if (exportData(tempFilePath, true)) {
                program.execute(tempFilePath);
            }
        }
    }

    /**
     * Get the sell style
     *
     * @return int
     */
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.SHELL_TRIM;
    }

    /**
     * Create the button bar
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createButtonBar(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        Composite leftComposite = new Composite(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        leftComposite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.BEGINNING;
        leftComposite.setLayoutData(gridData);

        sizeLabel = new Label(leftComposite, SWT.NONE);
        sizeLabel.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 200, -1));

        initValue();

        Composite rightComposite = new Composite(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        rightComposite.setLayout(layout);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.END;
        rightComposite.setLayoutData(gridData);

        ((GridLayout) rightComposite.getLayout()).numColumns++;
        ToolBar toolBar = new ToolBar(rightComposite, SWT.HORIZONTAL | SWT.FLAT);
        openByExternalBtn = new ToolItem(toolBar, SWT.DROP_DOWN);
        openByExternalBtn.setText(Messages.btnOpenByExternalProgram);
        openByExternalBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (event.detail == SWT.ARROW) {
                            loadProgramMenu();
                        } else {
                            openByExternalProgram();
                        }
                    }
                });

        // Add the buttons to the button bar.
        createButtonsForButtonBar(rightComposite);
        return composite;
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
        updateButtonStatus();
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setMinimumSize(720, 540);
        getShell()
                .addDisposeListener(
                        new DisposeListener() {
                            public void widgetDisposed(DisposeEvent e) {
                                disposeImageCanvas();
                            }
                        });
        if (isEditable) {
            getShell().setText(Messages.titleEditData);
            this.setMessage(Messages.msgEditData);
        } else {
            getShell().setText(Messages.titleViewData);
            this.setMessage(Messages.msgViewData);
        }
    }

    protected void updateButtonStatus() {
        if (!isEditable || !value.hasLoadAll()) {
            if (null != setNullBtn) {
                setNullBtn.setEnabled(false);
            }
            if (null != importBtn) {
                importBtn.setEnabled(false);
            }
            if (getButton(IDialogConstants.OK_ID) != null) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }
        }
        if (!value.hasLoadAll()) {
            if (null != exportBtn) {
                exportBtn.setEnabled(false);
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            Object obj = null;
            String shownValue = "";

            if (setNullBtn.getSelection()) {
                shownValue = NULL_VALUE;
            } else if (value instanceof CellValue) {
                obj = currValue;
                if (DataType.isBlobDataType(columnInfo.getType())) {
                    shownValue = BLOB_VALUE;
                } else if (DataType.isBitDataType(columnInfo.getType())
                        || DataType.isBitVaryingDataType(columnInfo.getType())) {
                    shownValue = BIT_VALUE;
                }
            } else {
                return;
            }

            if (CellViewer.isCellValueEqual(value, obj)) {
                super.buttonPressed(IDialogConstants.CANCEL_ID);
                return;
            } else {
                boolean isContinue =
                        CommonUITool.openConfirmBox(getShell(), Messages.confirmDataChanged);
                if (!isContinue) {
                    return;
                }
            }

            newValue = new CellValue(obj);
            if (obj instanceof File || obj instanceof byte[]) {
                newValue.setFileCharset(fileCharsetCombo.getText());
            }
            newValue.setShowValue(shownValue);
        }
        super.buttonPressed(buttonId);
    }

    public CellValue getValue() {
        return newValue;
    }

    public int show() {
        return this.open();
    }
}
