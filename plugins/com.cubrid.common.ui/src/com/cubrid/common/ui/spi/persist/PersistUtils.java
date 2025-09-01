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
package com.cubrid.common.ui.spi.persist;

import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Persist utility class
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-1 created by pangqiren
 */
public final class PersistUtils {

    private PersistUtils() {
        // empty
    }

    /**
     * get the preference object from configuration scope.
     *
     * @param pluginId String
     * @return IEclipsePreferences
     */
    public static IEclipsePreferences getGlobalPreference(String pluginId) {
        return ConfigurationScope.INSTANCE.getNode(pluginId);
    }

    /**
     * Get value by id from global preference of configuration scope
     *
     * @param pluginId String
     * @param key String
     * @return the value
     */
    public static String getGlobalPreferenceValue(String pluginId, String key) {
        return getGlobalPreference(pluginId).get(key, "");
    }

    /**
     * Save value to global preference of configuration scope
     *
     * @param pluginId String
     * @param key the key
     * @param value the value
     */
    public static void setGlobalPreferenceValue(String pluginId, String key, String value) {
        IEclipsePreferences prefs = getGlobalPreference(pluginId);
        prefs.put(key, value);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            prefs = null;
        }
    }

    /**
     * get the preference object from instance scope.
     *
     * @param pluginId String
     * @return IEclipsePreferences
     */
    public static IEclipsePreferences getPreference(String pluginId) {
        return InstanceScope.INSTANCE.getNode(pluginId);
    }

    /**
     * Get value by id from plugin preference of instance scope
     *
     * @param pluginId String
     * @param key String
     * @return the value
     */
    public static String getPreferenceValue(String pluginId, String key) {
        return getPreference(pluginId).get(key, "");
    }

    /**
     * Get value by id from plugin preference of instance scope
     *
     * @param pluginId String
     * @param key String
     * @param defaultValue String
     * @return the value
     */
    public static String getPreferenceValue(String pluginId, String key, String defaultValue) {
        return getPreference(pluginId).get(key, defaultValue);
    }

    /**
     * Save value to plugin preference of instance scope
     *
     * @param pluginId String
     * @param key the key
     * @param value the value
     */
    public static void setPreferenceValue(String pluginId, String key, String value) {
        IEclipsePreferences prefs = getPreference(pluginId);
        prefs.put(key, value);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            prefs = null;
        }
    }

    /**
     * Get the node XML memento from instance scope preference
     *
     * @param pluginId String
     * @param key String
     * @return IXMLMemento
     */
    public static IXMLMemento getXMLMemento(String pluginId, String key) {

        IEclipsePreferences preference = getPreference(pluginId);
        String xmlString = preference.get(key, "");
        if (xmlString == null || xmlString.length() == 0) {
            return null;
        }
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            return XMLMemento.loadMemento(in);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Save the xml content to instance scope preference
     *
     * @param pluginId String
     * @param key String
     * @param memento XMLMemento
     */
    public static void saveXMLMemento(String pluginId, String key, XMLMemento memento) {
        String xmlString = null;
        try {
            xmlString = memento.saveToString();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        IEclipsePreferences preference = PersistUtils.getPreference(pluginId);
        try {
            preference.put(key, xmlString);
            preference.flush();
        } catch (BackingStoreException e) {
            preference = null;
        }
    }
}
