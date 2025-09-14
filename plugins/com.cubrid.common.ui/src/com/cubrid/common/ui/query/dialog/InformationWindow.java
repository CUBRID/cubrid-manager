/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.dialog;

import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * InformationWindow
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-01-27 created by Kevin.Wang
 */
public class InformationWindow extends Window {

    private StyledText infoText;

    private int width = 0;
    private int height = 20;
    private int horizonalMargin = 25;
    private int verticalMargin = 25;
    private int minAlpha = 180;
    private final Shell parentShell;

    /**
     * The constructor
     *
     * @param parent
     * @param keyWords
     */
    public InformationWindow(Shell parent) {
        super(parent);
        this.parentShell = parent;
    }

    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        infoText = new StyledText(composite, SWT.None);
        infoText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        infoText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        infoText.setEditable(false);
        infoText.setEnabled(false);
        infoText.addMouseTrackListener(
                new MouseTrackListener() {
                    public void mouseHover(MouseEvent e) {
                        increaseAlpha();
                    }

                    public void mouseExit(MouseEvent e) {
                        reduceAlpha();
                    }

                    public void mouseEnter(MouseEvent e) {
                        increaseAlpha();
                    }
                });

        this.getShell().setAlpha(minAlpha);

        return parent;
    }

    /** Increase the alpha parameter */
    private void increaseAlpha() {
        new Thread(
                        new Runnable() {
                            Shell windowShell = InformationWindow.this.getShell();
                            private int alpha = 255;
                            private int deta = 5;
                            private int currentAlpha = 0;

                            public void run() {
                                windowShell
                                        .getDisplay()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        currentAlpha = windowShell.getAlpha();
                                                        while (currentAlpha < alpha) {
                                                            currentAlpha += deta;
                                                            if (currentAlpha >= alpha) {
                                                                currentAlpha = alpha;
                                                            }
                                                            windowShell.setAlpha(currentAlpha);
                                                            Thread.yield(); // Pause
                                                        }
                                                    }
                                                });
                            }
                        })
                .start();
    }

    /** Reduce the alpha parameter */
    private void reduceAlpha() {
        new Thread(
                        new Runnable() {
                            Shell windowShell = InformationWindow.this.getShell();
                            private int alpha = minAlpha;
                            private int deta = 5;
                            private int currentAlpha = 0;

                            public void run() {
                                windowShell
                                        .getDisplay()
                                        .syncExec(
                                                new Runnable() {
                                                    public void run() {
                                                        currentAlpha = windowShell.getAlpha();
                                                        while (currentAlpha > alpha) {
                                                            currentAlpha -= deta;
                                                            if (currentAlpha <= alpha) {
                                                                currentAlpha = alpha;
                                                            }
                                                            windowShell.setAlpha(currentAlpha);
                                                        }
                                                        Thread.yield(); // Pause
                                                    }
                                                });
                            }
                        })
                .start();
    }

    /**
     * Update the information
     *
     * @param string
     */
    public void updateInfo(String string, Set<String> keyWords) {
        if (string == null) {
            infoText.setText("");
        } else {
            infoText.setText(string);
        }

        if (keyWords != null) {
            decorateText(keyWords);
        }
    }

    /** Decorate the text */
    private void decorateText(Set<String> keyWords) {
        String text = infoText.getText();
        infoText.setAlignment(SWT.LEFT_TO_RIGHT);

        for (String key : keyWords) {
            int index = text.indexOf(key);
            if (index >= 0) {
                StyleRange eachStyle = new StyleRange();
                eachStyle.start = index;
                eachStyle.length = key.length();
                eachStyle.fontStyle = SWT.BOLD;
                infoText.setStyleRange(eachStyle);
            }
        }
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
    }

    public void reLocation(Rectangle rec) {
        this.getShell().setBounds(rec);
    }

    protected int getShellStyle() {
        return SWT.NO_TRIM;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int gethMargin() {
        return horizonalMargin;
    }

    public void sethMargin(int hMargin) {
        this.horizonalMargin = hMargin;
    }

    public int getvMargin() {
        return verticalMargin;
    }

    public void setvMargin(int vMargin) {
        this.verticalMargin = vMargin;
    }

    public int getMinAlpha() {
        return minAlpha;
    }

    public void setMinAlpha(int minAlpha) {
        this.minAlpha = minAlpha;
    }
}
