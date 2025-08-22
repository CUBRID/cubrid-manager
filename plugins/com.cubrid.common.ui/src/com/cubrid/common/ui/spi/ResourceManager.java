package com.cubrid.common.ui.spi;

import com.cubrid.common.core.util.LogUtil;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * Utility class for managing OS resources associated with SWT controls such as colors, fonts,
 * images, etc.
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public final class ResourceManager {
    private static final Logger LOGGER = LogUtil.getLogger(ResourceManager.class);
    private static HashMap<String, Font> fontMap = new HashMap<String, Font>();
    private static HashMap<Font, Font> fontToBoldFontMap = new HashMap<Font, Font>();
    private static HashMap<Integer, Cursor> cursorMap = new HashMap<Integer, Cursor>();
    private static HashMap<RGB, Color> colorMap = new HashMap<RGB, Color>();

    private ResourceManager() {}

    public static void dispose() {
        disposeColors();
        disposeFonts();
        disposeCursors();
    }

    public static Color getColor(int systemColorID) {
        Display display = Display.getDefault();
        return display.getSystemColor(systemColorID);
    }

    public static Color getColor(int red, int green, int blue) {
        return getColor(new RGB(red, green, blue));
    }

    public static Color getColor(RGB rgb) {
        Color color = colorMap.get(rgb);
        if (color == null) {
            Display display = Display.getCurrent();
            color = new Color(display, rgb);
            colorMap.put(rgb, color);
        }
        return color;
    }

    public static void disposeColors() {
        for (Iterator<Color> iter = colorMap.values().iterator(); iter.hasNext(); ) {
            iter.next().dispose();
        }
        colorMap.clear();
    }

    public static Font getFont(String name, int height, int style) {
        return getFont(name, height, style, false, false);
    }

    public static Font getFont(
            String name, int size, int style, boolean strikeout, boolean underline) {
        String fontName = name + '|' + size + '|' + style + '|' + strikeout + '|' + underline;
        Font font = fontMap.get(fontName);
        if (font == null) {
            FontData fontData = new FontData(name, size, style);
            if (strikeout || underline) {
                try {
                    Class<?> logFontClass =
                            Class.forName("org.eclipse.swt.internal.win32.LOGFONT"); // $NON-NLS-1$
                    Object logFont = FontData.class.getField("data").get(fontData); // $NON-NLS-1$
                    if (logFont != null && logFontClass != null) {
                        if (strikeout) {
                            logFontClass
                                    .getField("lfStrikeOut")
                                    .set(logFont, Byte.valueOf((byte) 1)); // $NON-NLS-1$
                        }
                        if (underline) {
                            logFontClass
                                    .getField("lfUnderline")
                                    .set(logFont, Byte.valueOf((byte) 1)); // $NON-NLS-1$
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            font = new Font(Display.getCurrent(), fontData);
            fontMap.put(fontName, font);
        }
        return font;
    }

    public static Font getFont(String fontString) {
        if (fontString == null || fontString.trim().length() == 0) {
            return null;
        }
        Font font = fontMap.get(fontString);
        if (font == null) {
            FontData fontData = new FontData(fontString);
            font = new Font(Display.getDefault(), fontData);
            fontMap.put(fontString, font);
        }
        return font;
    }

    public static void disposeFonts() {
        for (Iterator<Font> iter = fontMap.values().iterator(); iter.hasNext(); ) {
            iter.next().dispose();
        }
        fontMap.clear();

        for (Iterator<Font> iter = fontToBoldFontMap.values().iterator(); iter.hasNext(); ) {
            iter.next().dispose();
        }
        fontToBoldFontMap.clear();
    }

    public static void disposeCursors() {
        for (Iterator<Cursor> iter = cursorMap.values().iterator(); iter.hasNext(); ) {
            iter.next().dispose();
        }
        cursorMap.clear();
    }
}
