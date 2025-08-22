/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.slf4j.Logger;

/**
 * The CUBRID Manager information dialog
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-24 created by pangqiren
 */
public class ProductInfoDialog extends TrayDialog {

    private static final Logger LOGGER = LogUtil.getLogger(ProductInfoDialog.class);
    private AboutItem item;
    private Cursor handCursor;
    private Cursor busyCursor;
    private boolean mouseDown = false;
    private boolean dragEvent = false;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public ProductInfoDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Adds listeners to the given styled text
     *
     * @param styledText the StyledText object
     */
    protected void addListeners(StyledText styledText) {
        styledText.addMouseListener(
                new MouseAdapter() {
                    public void mouseDown(MouseEvent event) {
                        if (event.button != 1) {
                            return;
                        }
                        mouseDown = true;
                    }

                    public void mouseUp(MouseEvent event) {
                        mouseDown = false;
                        StyledText text = (StyledText) event.widget;
                        int offset = text.getCaretOffset();
                        if (dragEvent) {
                            // don't activate a link during a drag/mouse up operation
                            dragEvent = false;
                            if (item != null && item.isLinkAt(offset)) {
                                text.setCursor(handCursor);
                            }
                        } else if (item != null && item.isLinkAt(offset)) {
                            text.setCursor(busyCursor);
                            String url = item.getLinkAt(offset);
                            if (StringUtil.isEmpty(url)) {
                                LOGGER.warn("The url is a null or empty string : {}.", url);
                            } else {
                                openLink(url);
                            }

                            StyleRange selectionRange = getCurrentRange(text);
                            if (selectionRange == null) {
                                LOGGER.warn("The selectionRange is a null.");
                            } else {
                                text.setSelectionRange(selectionRange.start, selectionRange.length);
                                text.setCursor(null);
                            }
                        }
                    }
                });

        styledText.addMouseMoveListener(
                new MouseMoveListener() {
                    public void mouseMove(MouseEvent event) {
                        if (mouseDown) {
                            if (!dragEvent) {
                                StyledText text = (StyledText) event.widget;
                                text.setCursor(null);
                            }
                            dragEvent = true;
                            return;
                        }
                        StyledText text = (StyledText) event.widget;
                        int offset = -1;
                        try {
                            offset = text.getOffsetAtLocation(new Point(event.x, event.y));
                        } catch (IllegalArgumentException ex) {
                            offset = -1;
                        }
                        if (offset == -1) {
                            text.setCursor(null);
                        } else if (item != null && item.isLinkAt(offset)) {
                            text.setCursor(handCursor);
                        } else {
                            text.setCursor(null);
                        }
                    }
                });

        styledText.addTraverseListener(
                new TraverseListener() {
                    public void keyTraversed(TraverseEvent event) {
                        StyledText text = (StyledText) event.widget;
                        switch (event.detail) {
                            case SWT.TRAVERSE_ESCAPE:
                                event.doit = true;
                                break;
                            case SWT.TRAVERSE_TAB_NEXT:
                                // Previously traverse out in the backward direction?
                                Point nextSelection = text.getSelection();
                                int charCount = text.getCharCount();
                                if ((nextSelection.x == charCount)
                                        && (nextSelection.y == charCount)) {
                                    text.setSelection(0);
                                }
                                StyleRange nextRange = findNextRange(text);
                                if (nextRange == null) {
                                    // Next time in start at beginning, also used by
                                    // TRAVERSE_TAB_PREVIOUS to indicate we traversed out
                                    // in the forward direction
                                    text.setSelection(0);
                                    event.doit = true;
                                } else {
                                    text.setSelectionRange(nextRange.start, nextRange.length);
                                    event.doit = true;
                                    event.detail = SWT.TRAVERSE_NONE;
                                }
                                break;
                            case SWT.TRAVERSE_TAB_PREVIOUS:
                                // Previously traverse out in the forward direction?
                                Point previousSelection = text.getSelection();
                                if ((previousSelection.x == 0) && (previousSelection.y == 0)) {
                                    text.setSelection(text.getCharCount());
                                }
                                StyleRange previousRange = findPreviousRange(text);
                                if (previousRange == null) {
                                    // Next time in start at the end, also used by
                                    // TRAVERSE_TAB_NEXT to indicate we traversed out
                                    // in the backward direction
                                    text.setSelection(text.getCharCount());
                                    event.doit = true;
                                } else {
                                    text.setSelectionRange(
                                            previousRange.start, previousRange.length);
                                    event.doit = true;
                                    event.detail = SWT.TRAVERSE_NONE;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });

        // Listen for Tab and Space to allow keyboard navigation
        styledText.addKeyListener(
                new KeyAdapter() {
                    public void keyPressed(KeyEvent event) {
                        StyledText text = (StyledText) event.widget;
                        if (event.character == ' '
                                || event.keyCode == SWT.CR
                                || event.keyCode == SWT.KEYPAD_CR) {
                            if (item == null) {
                                LOGGER.warn("AboutItem is a null.");
                                return;
                            }

                            // Be sure we are in the selection
                            int offset = text.getSelection().x + 1;
                            if (item.isLinkAt(offset)) {
                                text.setCursor(busyCursor);
                                String url = item.getLinkAt(offset);
                                if (StringUtil.isEmpty(url)) {
                                    LOGGER.warn("The url is a null or empty string : {}.", url);
                                } else {
                                    openLink(url);
                                }

                                StyleRange selectionRange = getCurrentRange(text);
                                if (selectionRange == null) {
                                    LOGGER.warn("The selectionRange is a null.");
                                } else {
                                    text.setSelectionRange(
                                            selectionRange.start, selectionRange.length);
                                    text.setCursor(null);
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Gets the busy cursor.
     *
     * @return the busy cursor
     */
    protected Cursor getBusyCursor() {
        return busyCursor;
    }

    /**
     * Sets the busy cursor.
     *
     * @param busyCursor the busy cursor
     */
    protected void setBusyCursor(Cursor busyCursor) {
        this.busyCursor = busyCursor;
    }

    /**
     * Gets the hand cursor.
     *
     * @return Returns a hand cursor
     */
    protected Cursor getHandCursor() {
        return handCursor;
    }

    /**
     * Sets the hand cursor.
     *
     * @param handCursor The hand cursor to set
     */
    protected void setHandCursor(Cursor handCursor) {
        this.handCursor = handCursor;
    }

    /**
     * Gets the about item.
     *
     * @return the about item
     */
    protected AboutItem getItem() {
        return item;
    }

    /**
     * Sets the about item.
     *
     * @param item about item
     */
    protected void setItem(AboutItem item) {
        this.item = item;
    }

    /**
     * Find the range of the current selection.
     *
     * @param text the StyledText
     * @return the StyleRange
     */
    protected StyleRange getCurrentRange(StyledText text) {
        StyleRange[] ranges = text.getStyleRanges();
        int currentSelectionEnd = text.getSelection().y;
        int currentSelectionStart = text.getSelection().x;

        for (int i = 0; i < ranges.length; i++) {
            if ((currentSelectionStart >= ranges[i].start)
                    && (currentSelectionEnd <= (ranges[i].start + ranges[i].length))) {
                return ranges[i];
            }
        }
        return null;
    }

    /**
     * Find the next range after the current selection.
     *
     * @param text the StyledText
     * @return the StyleRange
     */
    protected StyleRange findNextRange(StyledText text) {
        StyleRange[] ranges = text.getStyleRanges();
        int currentSelectionEnd = text.getSelection().y;

        for (int i = 0; i < ranges.length; i++) {
            if (ranges[i].start >= currentSelectionEnd) {
                return ranges[i];
            }
        }
        return null;
    }

    /**
     * Find the previous range before the current selection.
     *
     * @param text the StyledText
     * @return the StyleRange
     */
    protected StyleRange findPreviousRange(StyledText text) {
        StyleRange[] ranges = text.getStyleRanges();
        int currentSelectionStart = text.getSelection().x;

        for (int i = ranges.length - 1; i > -1; i--) {
            if ((ranges[i].start + ranges[i].length - 1) < currentSelectionStart) {
                return ranges[i];
            }
        }
        return null;
    }

    /**
     * Open a link
     *
     * @param href the href string
     */
    protected void openLink(String href) {
        // format the href for an html file (file:///<filename.html>
        // required for Mac only.
        String hrefUrl = href;
        if (href.startsWith("file:")) { // $NON-NLS-1$
            hrefUrl = href.substring(5);
            while (hrefUrl.startsWith("/")) { // $NON-NLS-1$
                hrefUrl = hrefUrl.substring(1);
            }
            hrefUrl = "file:///" + hrefUrl; // $NON-NLS-1$
        }
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        try {
            IWebBrowser browser = support.getExternalBrowser();
            browser.openURL(new URL(urlEncodeForSpaces(hrefUrl.toCharArray())));
        } catch (MalformedURLException e) {
            LOGGER.info(e.getMessage(), e);
        } catch (PartInitException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    /**
     * This method encodes the url, removes the spaces from the url and replaces the same with
     * <code>"%20"</code>.
     *
     * @param input the char array
     * @return the string
     */
    private String urlEncodeForSpaces(char[] input) {
        StringBuffer retu = new StringBuffer(input.length);
        for (int i = 0; i < input.length; i++) {
            if (input[i] == ' ') {
                retu.append("%20"); // $NON-NLS-1$
            } else {
                retu.append(input[i]);
            }
        }
        return retu.toString();
    }

    /**
     * Sets the styled text's bold ranges
     *
     * @param styledText the styledText
     * @param boldRanges the range array
     */
    protected void setBoldRanges(StyledText styledText, int[][] boldRanges) {
        for (int i = 0; i < boldRanges.length; i++) {
            StyleRange r = new StyleRange(boldRanges[i][0], boldRanges[i][1], null, null, SWT.BOLD);
            styledText.setStyleRange(r);
        }
    }

    /**
     * Sets the styled text's link (blue) ranges
     *
     * @param styledText the styledText
     * @param linkRanges the range array
     */
    protected void setLinkRanges(StyledText styledText, int[][] linkRanges) {
        Color fg = JFaceColors.getHyperlinkText(styledText.getShell().getDisplay());
        for (int i = 0; i < linkRanges.length; i++) {
            StyleRange r = new StyleRange(linkRanges[i][0], linkRanges[i][1], fg, null);
            styledText.setStyleRange(r);
        }
    }

    /**
     * Scan the contents of the about text
     *
     * @param str the string
     * @return the AboutItem object
     */
    protected AboutItem scan(String str) {
        List<int[]> linkRanges = new ArrayList<int[]>();
        List<String> links = new ArrayList<String>();

        // slightly modified version of jface url detection
        // see org.eclipse.jface.text.hyperlink.URLHyperlinkDetector

        int urlSeparatorOffset = str.indexOf("://"); // $NON-NLS-1$
        while (urlSeparatorOffset >= 0) {

            boolean startDoubleQuote = false;

            // URL protocol (left to "://")
            int urlOffset = urlSeparatorOffset;
            char ch;
            do {
                urlOffset--;
                ch = ' ';
                if (urlOffset > -1) {
                    ch = str.charAt(urlOffset);
                }
                startDoubleQuote = ch == '"';
            } while (Character.isUnicodeIdentifierStart(ch));
            urlOffset++;

            // Right to "://"
            StringTokenizer tokenizer =
                    new StringTokenizer(
                            str.substring(urlSeparatorOffset + 3),
                            " \t\n\r\f<>",
                            false); //$NON-NLS-1$
            if (!tokenizer.hasMoreTokens()) {
                return null;
            }

            int urlLength = tokenizer.nextToken().length() + 3 + urlSeparatorOffset - urlOffset;

            if (startDoubleQuote) {
                int endOffset = -1;
                int nextDoubleQuote = str.indexOf('"', urlOffset);
                int nextWhitespace = str.indexOf(' ', urlOffset);
                if (nextDoubleQuote >= 0 && nextWhitespace >= 0) {
                    endOffset = Math.min(nextDoubleQuote, nextWhitespace);
                } else if (nextDoubleQuote >= 0) {
                    endOffset = nextDoubleQuote;
                } else if (nextWhitespace >= 0) {
                    endOffset = nextWhitespace;
                }
                if (endOffset != -1) {
                    urlLength = endOffset - urlOffset;
                }
            }

            linkRanges.add(new int[] {urlOffset, urlLength});
            links.add(str.substring(urlOffset, urlOffset + urlLength));

            urlSeparatorOffset = str.indexOf("://", urlOffset + urlLength + 1); // $NON-NLS-1$
        }
        return new AboutItem(
                str,
                (int[][]) linkRanges.toArray(new int[linkRanges.size()][2]),
                (String[]) links.toArray(new String[links.size()]));
    }
}
