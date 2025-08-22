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
package com.cubrid.cubridmanager.ui.mondashboard.preference;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.Color;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;

/**
 * The manager of monitor dashboard preference.
 *
 * @author SC13425
 * @version 1.0 - 2010-8-17 created by SC13425
 */
public class MonitorDashboardPreference {

    private static final Logger LOGGER = LogUtil.getLogger(MonitorDashboardPreference.class);

    private static Preferences pre =
            new InstanceScope().getNode(CubridManagerUIPlugin.PLUGIN_ID).node("monitor");

    private static final Color COLOR_UNKNOWN = ResourceManager.getColor(250, 225, 0);

    private static final Color COLOR_STOPPED = ResourceManager.getColor(255, 10, 10);

    private static final Color COLOR_CSMODE = ResourceManager.getColor(225, 230, 250);

    private static final Color COLOR_ACTIVE = ResourceManager.getColor(0, 255, 60);
    private static final Color COLOR_STANDBY = ResourceManager.getColor(200, 200, 200);

    private static final Color COLOR_TO_BE = ResourceManager.getColor(255, 255, 0);

    private static final String HA_HEARTBEAT_TIMEOUT = "ha_heartbeat_timeout";

    public static final int HA_HEARTBEAT_TIMEOUT_DEFAULT = 1000;

    /**
     * Parse string that read from preferences to color type.
     *
     * @param key String preference key.
     * @param def Color default color.
     * @return the Color parsed.If any errors occurred,default color will be returned.
     */
    private Color getColorFromPreference(String key, Color def) {
        try {
            String strColor = pre.get(key, "");
            strColor = strColor.replaceAll("Color", "").replaceAll("\\{", "").replaceAll("\\}", "");
            String[] rgb = strColor.split(",");
            int red = Integer.parseInt(rgb[0].trim());
            int green = Integer.parseInt(rgb[1].trim());
            int blue = Integer.parseInt(rgb[2].trim());
            return ResourceManager.getColor(red, green, blue);
        } catch (Exception ex) {
            return def;
        }
    }

    /** Save figure's color to preference. */
    public void save() {
        try {
            pre.flush();
        } catch (BackingStoreException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Get the color in preference by DBStatusType showtext.
     *
     * @param key DBStatusType showtext
     * @return color in preference
     */
    public Color getColor(String key) {
        return getColorFromPreference(key, getDefaultColor(key));
    }

    /**
     * Get the default color in preference by DBStatusType showtext.
     *
     * @param key DBStatusType showtext
     * @return color in preference
     */
    public Color getDefaultColor(String key) {
        if (key.startsWith("active")) {
            return COLOR_ACTIVE;
        } else if (key.startsWith("standby")) {
            return COLOR_STANDBY;
        } else if (key.startsWith("to-be-")) {
            return COLOR_TO_BE;
        } else if (key.startsWith("unknown")) {
            return COLOR_UNKNOWN;
        } else if (key.startsWith("CS")) {
            return COLOR_CSMODE;
        } else if (key.startsWith("stopped")) {
            return COLOR_STOPPED;
        }
        return getColorFromPreference(key, COLOR_STOPPED);
    }

    /**
     * Get the color to preference
     *
     * @param key DBStatusType showtext
     * @param color to be saved
     */
    public void setColor(String key, Color color) {
        pre.put(key, color.toString());
    }

    /**
     * Return HA Heartbeat timeout value
     *
     * @return miliseconds
     */
    public int getHAHeartBeatTimeout() {
        try {
            return pre.getInt(
                    MonitorDashboardPreference.HA_HEARTBEAT_TIMEOUT, HA_HEARTBEAT_TIMEOUT_DEFAULT);
        } catch (Exception ignored) {
            return HA_HEARTBEAT_TIMEOUT_DEFAULT;
        }
    }

    /**
     * Set HA Heartbeat timeout value
     *
     * @param timeout int
     */
    public void setHAHeartBeatTimeout(int timeout) {
        try {
            pre.put(MonitorDashboardPreference.HA_HEARTBEAT_TIMEOUT, String.valueOf(timeout));
        } catch (Exception ignored) {
            LOGGER.error(ignored.getMessage());
        }
    }
}
