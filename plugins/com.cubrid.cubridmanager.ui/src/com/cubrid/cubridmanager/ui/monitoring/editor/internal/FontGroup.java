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
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.cubridmanager.ui.monitoring.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This type is responsible for create group for font FontGroup Description
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-19 created by lizhiqiang
 */
public class FontGroup {

    /** Internal font style constant for regular fonts. */
    private static final String REGULAR = "regular"; // $NON-NLS-1$

    /** Internal font style constant for bold fonts. */
    private static final String BOLD = "bold"; // $NON-NLS-1$

    /** Internal font style constant for italic fonts. */
    private static final String ITALIC = "italic"; // $NON-NLS-1$

    /** Internal font style constant for bold italic fonts. */
    private static final String BOLD_ITALIC = "bold italic"; // $NON-NLS-1$

    private static final int SHOWNAME = 1;

    private static final int SHOWSTYLE = 2;

    private static final int SHOWSIZE = 4;

    private Button changeFontButton;
    private String changeButtonText = Messages.fontChangeBtn;
    private RGB chosenRgb;
    private String fontName;
    private int fontSize;
    private String fontStyle;
    private Text fontNameText;
    private Text fontSizeText;
    private Text fontStyleText;
    private Label fontColorLbl;
    private int mode = 5;
    private final Group fontGroup;

    public FontGroup(Composite parent) {
        fontGroup = new Group(parent, SWT.NONE);
    }

    /**
     * Load the content to the given contain.
     *
     * @param fontGroup
     * @param layout
     */
    public void loadContent() {
        GridLayout layout = new GridLayout(2, false);
        fontGroup.setLayout(layout);
        fontGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fontGroup.setText(Messages.fontGroupTxt);
        Group txtGroup = new Group(fontGroup, SWT.NONE);
        txtGroup.setLayout(layout);
        txtGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        if ((mode & SHOWNAME) != 0) {
            Label nameLbl = new Label(txtGroup, SWT.NONE);
            nameLbl.setText(Messages.fontNameLbl);
            fontNameText = new Text(txtGroup, SWT.BORDER | SWT.READ_ONLY);
            final GridData gdFontNameText = new GridData(SWT.FILL, SWT.CENTER, true, false);
            fontNameText.setLayoutData(gdFontNameText);
            fontNameText.setText(fontName);
        }
        if ((mode & SHOWSTYLE) != 0) {
            Label styleLbl = new Label(txtGroup, SWT.NONE);
            styleLbl.setText(Messages.fontStyleLbl);
            fontStyleText = new Text(txtGroup, SWT.BORDER | SWT.READ_ONLY);
            final GridData gdFontStyleText = new GridData(SWT.FILL, SWT.CENTER, true, false);
            fontStyleText.setLayoutData(gdFontStyleText);
            fontStyleText.setText(String.valueOf(fontSize));
        }
        if ((mode & SHOWSIZE) != 0) {
            Label sizeLbl = new Label(txtGroup, SWT.NONE);
            sizeLbl.setText(Messages.fontSizeLbl);
            fontSizeText = new Text(txtGroup, SWT.BORDER | SWT.READ_ONLY);
            final GridData gdFontSizeText = new GridData(SWT.FILL, SWT.CENTER, true, false);
            fontSizeText.setLayoutData(gdFontSizeText);
            fontSizeText.setText(String.valueOf(fontSize));
        }
        Label colorLbl = new Label(txtGroup, SWT.NONE);
        colorLbl.setText(Messages.fontColorLbl);

        fontColorLbl = new Label(txtGroup, SWT.BORDER | SWT.CENTER | SWT.READ_ONLY);
        final GridData gdFontColorText = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdFontColorText.widthHint = 35;
        fontColorLbl.setLayoutData(gdFontColorText);
        fontColorLbl.setData(chosenRgb);
        Display display = fontColorLbl.getDisplay();
        Color fColor = new Color(display, chosenRgb);
        fontColorLbl.setBackground(fColor);

        changeFontButton = createChangeControl(fontGroup);

        final GridData gdChangeBtn = new GridData(SWT.FILL, SWT.CENTER, false, false);
        changeFontButton.setLayoutData(gdChangeBtn);

        updateColorImage();
    }

    /**
     * create change color button
     *
     * @param parent the parent container
     * @return button the instance of Button
     */
    private Button createChangeControl(Composite parent) {
        if (changeFontButton == null) {
            changeFontButton = new Button(parent, SWT.PUSH);
        }
        if (changeButtonText != null) {
            changeFontButton.setText(changeButtonText);
        }
        changeFontButton.addSelectionListener(new ChangeSelectListen());
        changeFontButton.addDisposeListener(
                new DisposeListener() {
                    public void widgetDisposed(DisposeEvent event) {
                        changeFontButton = null;
                    }
                });
        changeFontButton.setFont(parent.getFont());
        return changeFontButton;
    }

    /** Update the image being displayed on the button using the current color setting. */
    private void updateColorImage() {
        Display display = fontColorLbl.getDisplay();
        Color fColor = new Color(display, chosenRgb);
        fontColorLbl.setBackground(fColor);
    }

    /**
     * Convert a style from int to string
     *
     * @param style the style represent with int
     * @return style which represent with string
     */
    private String styleAsString(int style) {
        boolean bold = (style & SWT.BOLD) == SWT.BOLD;
        boolean italic = (style & SWT.ITALIC) == SWT.ITALIC;
        if (bold && italic) {
            return BOLD_ITALIC;
        } else if (bold) {
            return BOLD;
        } else if (italic) {
            return ITALIC;
        } else {
            return REGULAR;
        }
    }

    /**
     * Convert a style from string to int
     *
     * @param style represent with string
     * @return the style represent with int
     */
    private int styleAsInt(String style) {
        if (style.equals(BOLD)) {
            return 1;
        } else if (style.equals(ITALIC)) {
            return 2;
        } else if (style.equals(BOLD_ITALIC)) {
            return 3;
        } else {
            return 0;
        }
    }

    /**
     * Set whether show name text control.
     *
     * @param showName whether show name text control
     */
    public void setShowName(boolean showName) {
        if (showName) {
            mode |= SHOWNAME;
        }
    }

    /**
     * Get whether show name text control
     *
     * @return boolean
     */
    public boolean isShowName() {
        return (mode & SHOWNAME) != 0;
    }

    /**
     * Set whether show style text control.
     *
     * @param showStyle whether show style text control
     */
    public void setShowStyle(boolean showStyle) {
        if (showStyle) {
            mode |= SHOWSTYLE;
        }
    }

    /**
     * Get whether show style text control
     *
     * @return boolean
     */
    public boolean isShowStyle() {
        return (mode & SHOWSTYLE) != 0;
    }

    /**
     * Set whether show size text control.
     *
     * @param showSize whether show size text control
     */
    public void setShowSize(boolean showSize) {
        if (showSize) {
            mode |= SHOWSIZE;
        }
    }

    public boolean isShowSize() {
        return (mode & SHOWSIZE) != 0;
    }

    /**
     * Get the control of fontNameText
     *
     * @return the fontNameText
     */
    public Text getFontNameText() {
        return fontNameText;
    }

    /**
     * Get the control of fontSizeText
     *
     * @return the fontSizeText
     */
    public Text getFontSizeText() {
        return fontSizeText;
    }

    /**
     * Get the control of fontStyleText
     *
     * @return the fontStyleText
     */
    public Text getFontStyleText() {
        return fontStyleText;
    }

    /**
     * Get the control of FontColorLbl
     *
     * @return the fontColorLbl
     */
    public Label getFontColorLbl() {
        return fontColorLbl;
    }

    /** @param fontNameText the fontNameText to set */
    public void setFontNameText(Text fontNameText) {
        this.fontNameText = fontNameText;
    }

    /** @param fontSizeText the fontSizeText to set */
    public void setFontSizeText(Text fontSizeText) {
        this.fontSizeText = fontSizeText;
    }

    /** @param fontStyleText the fontStyleText to set */
    public void setFontStyleText(Text fontStyleText) {
        this.fontStyleText = fontStyleText;
    }

    /** @param fontColorLbl the fontColorLbl to set */
    public void setFontColorLbl(Label fontColorLbl) {
        this.fontColorLbl = fontColorLbl;
    }

    /** @param changeButtonText the changeButtonText to set */
    public void setChangeButtonText(String changeButtonText) {
        this.changeButtonText = changeButtonText;
    }

    /**
     * This type that extends the type of SelectionAdapter and is responsible for response to the
     * color button selection event
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-3-19 created by lizhiqiang
     */
    private final class ChangeSelectListen extends SelectionAdapter {
        /**
         * Response to the select event
         *
         * @param event the instance of SelectionEvent
         */
        public void widgetSelected(SelectionEvent event) {
            FontDialog fontDialog = new FontDialog(changeFontButton.getShell());
            FontData[] chosenFont = null;

            FontData fontdata = new FontData();
            if ((mode & SHOWNAME) != 0) {
                fontName = fontNameText.getText();
                fontdata.setName(fontName);
            }
            if ((mode & SHOWSTYLE) != 0) {
                fontStyle = fontStyleText.getText();
                fontdata.setStyle(styleAsInt(fontStyle));
            }
            if ((mode & SHOWSIZE) != 0) {
                fontSize = Integer.parseInt(fontSizeText.getText());
                fontdata.setHeight(fontSize);
            }

            chosenFont = new FontData[1];
            chosenFont[0] = fontdata;
            fontDialog.setFontList(chosenFont);

            if (chosenFont != null) {
                fontDialog.setFontList(chosenFont);
            }
            if (chosenRgb != null) {
                fontDialog.setRGB(chosenRgb);
            }
            FontData font = fontDialog.open();
            if (font != null) {

                if ((mode & SHOWNAME) != 0) {
                    fontNameText.setText(font.getName());
                }
                if ((mode & SHOWSTYLE) != 0) {
                    fontStyleText.setText(styleAsString(font.getStyle()));
                }
                if ((mode & SHOWSIZE) != 0) {
                    fontSizeText.setText(String.valueOf(font.getHeight()));
                }
                chosenRgb = fontDialog.getRGB();
                updateColorImage();
            }
        }
    }

    /** @param chosenRgb the chosenRgb to set */
    public void setChosenRgb(RGB chosenRgb) {
        this.chosenRgb = chosenRgb;
    }

    /** @param fontName the fontName to set */
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    /** @param fontSize the fontSize to set */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /** @param fontStyle the fontStyle to set */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * Get chosenRgb
     *
     * @return the chosenRgb
     */
    public RGB getChosenRgb() {
        return chosenRgb;
    }
}
