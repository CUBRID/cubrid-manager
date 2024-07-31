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
package com.cubrid.common.ui.query.editor;

import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.graphics.RGB;

/**
 * The EditorConstance class
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-3-20 created by Kevin.Wang
 */
public final class EditorConstance {
    private static List<RGB> BACKGROUND_COLORS = new ArrayList<RGB>();
    private static List<RGB> BACKGROUND_COLORS_DEEP = new ArrayList<RGB>();

    static {
        BACKGROUND_COLORS.add(new RGB(255, 255, 255));
        BACKGROUND_COLORS.add(new RGB(255, 255, 215));
        BACKGROUND_COLORS.add(new RGB(255, 245, 245));
        BACKGROUND_COLORS.add(new RGB(240, 255, 240));
        BACKGROUND_COLORS.add(new RGB(235, 235, 255));
        BACKGROUND_COLORS.add(new RGB(240, 240, 240));

        BACKGROUND_COLORS_DEEP.add(new RGB(255, 255, 255));
        BACKGROUND_COLORS_DEEP.add(new RGB(255, 255, 0));
        BACKGROUND_COLORS_DEEP.add(new RGB(255, 150, 150));
        BACKGROUND_COLORS_DEEP.add(new RGB(0, 255, 0));
        BACKGROUND_COLORS_DEEP.add(new RGB(128, 128, 255));
        BACKGROUND_COLORS_DEEP.add(new RGB(150, 150, 150));
    }

    public static RGB convertDeepBackground(RGB rgb) {
        for (int i = 0, len = BACKGROUND_COLORS.size(); i < len; i++) {
            RGB item = BACKGROUND_COLORS.get(i);
            if (rgb.red == item.red && rgb.green == item.green && rgb.blue == item.blue) {
                return BACKGROUND_COLORS_DEEP.get(i);
            }
        }

        return BACKGROUND_COLORS_DEEP.get(0);
    }

    public static List<RGB> getAvaliableBackground() {
        return BACKGROUND_COLORS;
    }

    public static RGB getDefaultBackground() {
        return BACKGROUND_COLORS.get(0);
    }

    /**
     * Get the rgb's position which is in BACKGROUND_COLORS.If rgb is not in the BACKGROUND_COLORS,
     * return -1
     *
     * @param rgb
     * @return the rgb's position which is in BACKGROUND_COLORS. e.g. RGB(255,255,255),return 1.
     */
    public static int getBGPos(RGB rgb) {
        if (rgb != null) {
            for (int i = 0; i < BACKGROUND_COLORS.size(); i++) {
                if (rgb.equals(BACKGROUND_COLORS.get(i))) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    /**
     * Get the available background color.If the pos is err,return null;
     *
     * @param pos
     * @return Get the available background color .If the pos is err,return null;
     */
    public static RGB getRGBByPos(int pos) {
        int index = pos - 1;
        if (index >= 0 && index < BACKGROUND_COLORS.size()) {
            return BACKGROUND_COLORS.get(index);
        }
        return null;
    }

    /**
     * Define is need to set the editor background
     *
     * @param editorConfig
     * @return true,if need to set editor background. otherwise false.
     */
    public static boolean isNeedSetBackground(DatabaseEditorConfig editorConfig) {
        if (editorConfig == null) {
            return true;
        }

        return isNeedSetBackground(editorConfig.getBackGround());
    }

    /**
     * Define is need to set the editor background
     *
     * @param background
     * @return true,if need to set editor background. otherwise false.
     */
    public static boolean isNeedSetBackground(RGB background) {
        if (background == null) {
            return true;
        }

        return false;
    }
}
