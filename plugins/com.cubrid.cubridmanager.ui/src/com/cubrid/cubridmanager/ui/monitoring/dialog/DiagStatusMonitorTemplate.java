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
package com.cubrid.cubridmanager.ui.monitoring.dialog;

import org.eclipse.swt.SWT;

/**
 * A help class that get the color name or value
 *
 * @author lizhiqiang
 * @version 1.0 - 2009-4-27 created by lizhiqiang
 */
public class DiagStatusMonitorTemplate {
    public String templateName = null;
    public String desc = null;
    public String sampling_term = null;
    public static final int COLOR_SIZE = 16;

    /**
     * Gets the color name by the color value
     *
     * @param clr int
     * @return String represent specific color
     */
    public static String getColorString(int clr) {
        switch (clr) {
            case SWT.COLOR_DARK_RED:
                return "DARK_RED";
            case SWT.COLOR_DARK_GREEN:
                return "DARK_GREEN";
            case SWT.COLOR_DARK_YELLOW:
                return "DARK_YELLOW";
            case SWT.COLOR_DARK_BLUE:
                return "DARK_BLUE";
            case SWT.COLOR_DARK_MAGENTA:
                return "DARK_MAGENTA";
            case SWT.COLOR_DARK_CYAN:
                return "DARK_CYAN";
            case SWT.COLOR_DARK_GRAY:
                return "DARK_GRAY";
            case SWT.COLOR_WHITE:
                return "WHITE";
            case SWT.COLOR_RED:
                return "RED";
            case SWT.COLOR_BLACK:
                return "BLACK";
            case SWT.COLOR_YELLOW:
                return "YELLOW";
            case SWT.COLOR_BLUE:
                return "BLUE";
            case SWT.COLOR_MAGENTA:
                return "MAGENTA";
            case SWT.COLOR_CYAN:
                return "CYAN";
            case SWT.COLOR_GRAY:
                return "GRAY";
            default:
                return "GREEN";
        }
    }

    /**
     * Gets the current color value by color name
     *
     * @param clr String represent specific color
     * @return int
     */
    public static int getCurrentColorConstant(String clr) {
        if (("DARK_RED").equals(clr)) {
            return SWT.COLOR_DARK_RED;
        } else if (("DARK_GREEN").equals(clr)) {
            return SWT.COLOR_DARK_GREEN;
        } else if (("DARK_YELLOW").equals(clr)) {
            return SWT.COLOR_DARK_YELLOW;
        } else if (("DARK_BLUE").equals(clr)) {
            return SWT.COLOR_DARK_BLUE;
        } else if (("DARK_MAGENTA").equals(clr)) {
            return SWT.COLOR_DARK_MAGENTA;
        } else if (("DARK_CYAN").equals(clr)) {
            return SWT.COLOR_DARK_CYAN;
        } else if (("DARK_GRAY").equals(clr)) {
            return SWT.COLOR_DARK_GRAY;
        } else if (("WHITE").equals(clr)) {
            return SWT.COLOR_WHITE;
        } else if (("RED").equals(clr)) {
            return SWT.COLOR_RED;
        } else if (("BLACK").equals(clr)) {
            return SWT.COLOR_BLACK;
        } else if (("YELLOW").equals(clr)) {
            return SWT.COLOR_YELLOW;
        } else if (("BLUE").equals(clr)) {
            return SWT.COLOR_BLUE;
        } else if (("MAGENTA").equals(clr)) {
            return SWT.COLOR_MAGENTA;
        } else if (("CYAN").equals(clr)) {
            return SWT.COLOR_CYAN;
        } else if (("GRAY").equals(clr)) {
            return SWT.COLOR_GRAY;
        } else {
            return SWT.COLOR_GREEN;
        }
    }
}
