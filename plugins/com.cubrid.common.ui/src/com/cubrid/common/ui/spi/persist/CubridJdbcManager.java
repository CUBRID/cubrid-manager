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
package com.cubrid.common.ui.spi.persist;

import com.cubrid.common.configuration.jdbc.IJDBCDriverChangedObserver;
import com.cubrid.common.configuration.jdbc.IJDBCDriverChangedSubject;
import com.cubrid.common.configuration.jdbc.JDBCDriverChangingManager;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.jdbc.proxy.manage.JdbcClassLoaderFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;

/**
 * It is responsible to manager all CUBRID Jdbc driver
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public final class CubridJdbcManager
        implements IJDBCDriverChangedObserver, IJDBCDriverChangedSubject {
    private static final Logger LOGGER = LogUtil.getLogger(CubridJdbcManager.class);
    private static final String CUBRID_JDBC_SETTING = "CUBRID_JDBC_SETTING";

    private final Map<String, String> defaultVersion2FileMap = getSortedMap();
    private final Map<String, String> externalVersion2FileMap = getSortedMap();

    private final List<IJDBCDriverChangedObserver> driverChangingObservers =
            new ArrayList<IJDBCDriverChangedObserver>();
    private static CubridJdbcManager instance;

    /**
     * Return the only CubridJdbcManager
     *
     * @return CubridJdbcManager
     */
    public static CubridJdbcManager getInstance() {
        synchronized (CubridJdbcManager.class) {
            if (instance == null) {
                instance = new CubridJdbcManager();
                JDBCDriverChangingManager.getInstance().registSubject(instance);
                JDBCDriverChangingManager.getInstance().registObservor(instance);

                instance.loadCubridJdbc(false);
            }
        }
        return instance;
    }

    /** The constructor */
    private CubridJdbcManager() {}

    /**
     * Load CUBRID JDBC driver from plugin preference and register these class loaders
     *
     * @param silence if true, the chaning event will not be triggered.
     * @return all CUBRID Jdbc driver
     */
    private void loadCubridJdbc(boolean silence) {
        synchronized (CubridJdbcManager.class) {
            IXMLMemento memento =
                    PersistUtils.getXMLMemento(CommonUIPlugin.PLUGIN_ID, CUBRID_JDBC_SETTING);
            if (memento == null) {
                loadDefaultJdbcs();
            } else {
                IXMLMemento[] children = memento.getChildren("cubridJdbcSetting");
                if (children == null || children.length == 0) {
                    loadDefaultJdbcs();
                } else {
                    for (int i = 0; i < children.length; i++) {
                        String jdbcURL = children[i].getString("jdbcURL");
                        registerClassLoader(externalVersion2FileMap, jdbcURL);
                    }
                }
            }
        }
    }

    /**
     * Retrieves the driver file by version string.
     *
     * @param version JDBC version string
     * @return driver file full path.
     */
    public String getDriverFileByVersion(String version) {
        if (defaultVersion2FileMap.containsKey(version)) {
            return defaultVersion2FileMap.get(version);
        } else if (externalVersion2FileMap.containsKey(version)) {
            return externalVersion2FileMap.get(version);
        } else {
            return "";
        }
    }

    public String getVersionByDriver(String driverPath) {
        for (Entry<String, String> entry : defaultVersion2FileMap.entrySet()) {
            if (StringUtil.isEqual(driverPath, entry.getValue())) {
                return entry.getKey();
            }
        }
        for (Entry<String, String> entry : externalVersion2FileMap.entrySet()) {
            if (StringUtil.isEqual(driverPath, entry.getValue())) {
                return entry.getKey();
            }
        }

        return "";
    }

    /**
     * Get sorted map
     *
     * @return Map<String, String>
     */
    private Map<String, String> getSortedMap() {
        Map<String, String> map =
                new TreeMap<String, String>(
                        new Comparator<String>() {
                            public int compare(String o1, String o2) {
                                String version1 =
                                        o1 == null ? null : o1.replaceAll("CUBRID-JDBC-", "");
                                String version2 =
                                        o2 == null ? null : o2.replaceAll("CUBRID-JDBC-", "");
                                int ret = CompatibleUtil.compareVersion(version1, version2);
                                if (ret > 0) {
                                    return -1;
                                } else if (ret < 0) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });

        return map;
    }

    /**
     * Add or delete CUBRID JDBC driver
     *
     * @param map the CUBRID JDBC driver
     */
    public void resetCubridJdbcSetting(Map<String, String> map) {
        synchronized (CubridJdbcManager.class) {
            XMLMemento memento = XMLMemento.createWriteRoot("cubridJdbcSetting");
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                IXMLMemento child = memento.createChild("cubridJdbcSetting");
                Entry<String, String> next = iterator.next();
                child.putString("jdbcURL", next.getValue());
            }

            // register the jdbc to the jdbc factory
            Map<String, String> loaderMap = getSortedMap();
            loaderMap.putAll(defaultVersion2FileMap);
            loaderMap.putAll(externalVersion2FileMap);

            Map<String, String> noLoadedMap = new HashMap<String, String>();
            iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                // delete the loader that already loaded
                if (loaderMap.containsKey(key) && StringUtil.isEqual(loaderMap.get(key), value)) {
                    loaderMap.remove(key);
                } else {
                    noLoadedMap.put(key, value);
                }
            }
            for (String version : loaderMap.keySet()) {
                if (externalVersion2FileMap.containsKey(version)) {
                    String driverFile = externalVersion2FileMap.get(version);
                    externalVersion2FileMap.remove(version);
                    JdbcClassLoaderFactory.removeClassLoader(version);
                    fireRemoveJdbcDriver(driverFile);
                }
            }

            for (Entry<String, String> entry : noLoadedMap.entrySet()) {
                externalVersion2FileMap.put(entry.getKey(), entry.getValue());
                JdbcClassLoaderFactory.registerClassLoader(entry.getValue());
                fireAddJdbcDriver(entry.getValue());
            }

            PersistUtils.saveXMLMemento(CommonUIPlugin.PLUGIN_ID, CUBRID_JDBC_SETTING, memento);
        }
    }

    /**
     * Get loaded jdbc map
     *
     * @return Map<String, String>:key-version,value-filePath
     */
    public Map<String, String> getLoadedJdbc() {
        Map<String, String> map = getSortedMap();
        map.putAll(defaultVersion2FileMap);
        map.putAll(externalVersion2FileMap);

        return map;
    }

    /**
     * Get default jdbc map
     *
     * @return
     */
    public Map<String, String> getDefaultJdbc() {
        Map<String, String> map = getSortedMap();
        map.putAll(defaultVersion2FileMap);

        return map;
    }

    /**
     * Get JDBC driver from plugins installation directory
     *
     * @return the JDBC driver
     */
    public void loadDefaultJdbcs() {
        List<File> fileList = getDefaultJDBCPath();
        for (File filePath : fileList) {
            if (filePath != null && filePath.exists() && filePath.isDirectory()) {
                File[] listFiles =
                        filePath.listFiles(
                                new FileFilter() {
                                    public boolean accept(File file) {
                                        String fileName = file.getName().trim().toLowerCase();
                                        return file.isFile()
                                                && (fileName.matches("^JDBC\\S*.jar")
                                                        || fileName.matches("^jdbc\\S*.jar")
                                                        || fileName.matches("^CUBRID_JDBC\\S*.jar")
                                                        || fileName.matches("^cubrid-jdbc\\S*.jar")
                                                        || fileName.matches(
                                                                "^cubrid_jdbc\\S*.jar"));
                                    }
                                });
                for (File file : listFiles) {
                    String path = file.getAbsolutePath();
                    registerClassLoader(defaultVersion2FileMap, path);
                }
            }
        }
    }

    /**
     * registerClassLoader
     *
     * @param map version to file mapping
     * @param file to be registered.
     */
    private void registerClassLoader(Map<String, String> map, String file) {
        String jdbcVersion = null;
        try {
            jdbcVersion = JdbcClassLoaderFactory.getJdbcJarVersion(file);
        } catch (IOException e) {
            // Do thing
        }
        if (jdbcVersion == null || jdbcVersion.trim().length() <= 0) {
            return;
        }
        if (!map.containsKey(jdbcVersion)) {
            if (JdbcClassLoaderFactory.registerClassLoader(file)) {
                map.put(jdbcVersion, file);
                fireAddJdbcDriver(file);
            }
        }
    }

    /**
     * Get default JDBC saved path
     *
     * @return List<File>
     */
    public List<File> getDefaultJDBCPath() {
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File(Platform.getInstallLocation().getURL().getPath() + "driver"));
        try {
            File file = FileLocator.getBundleFile(CommonUIPlugin.getDefault().getBundle());
            if (file != null) {
                file = file.getParentFile();
            }
            if (file != null) {
                String jdbcParentPath =
                        file.getAbsolutePath() + File.separator + "driver" + File.separator;
                if (new File(jdbcParentPath).exists()) {
                    fileList.add(new File(jdbcParentPath));
                }
                fileList.add(file);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        fileList.add(new File(Platform.getInstallLocation().getURL().getPath() + "plugins"));
        fileList.add(new File(Platform.getInstallLocation().getURL().getPath() + "dropins"));
        return fileList;
    }

    /**
     * Check whether the path is the default JDBC path
     *
     * @param path the JDBC path
     * @return <code>true</code> if it is default JDBC path;<code>false</code> otherwise
     */
    public boolean isDefaultJdbc(String path) {
        Iterator<String> it = defaultVersion2FileMap.values().iterator();
        while (it.hasNext()) {
            String jdbcPath = it.next();
            if (jdbcPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fire add jdbc driver
     *
     * @param version
     */
    private void fireAddJdbcDriver(String driverFile) {
        for (IJDBCDriverChangedObserver ob : driverChangingObservers) {
            try {
                ob.afterAdd(this, driverFile);
            } catch (Exception ex) {
                LOGGER.error("", ex);
            }
        }
    }

    /**
     * Fire remove jdbc driver
     *
     * @param version
     */
    private void fireRemoveJdbcDriver(String driverFile) {
        for (IJDBCDriverChangedObserver ob : driverChangingObservers) {
            try {
                ob.afterDelete(this, driverFile);
            } catch (Exception ex) {
                LOGGER.error("", ex);
            }
        }
    }

    /**
     * Add a driver to management
     *
     * @param initiator who triggered the event.
     * @param df driver file full path
     */
    public void afterAdd(IJDBCDriverChangedSubject initiator, String driverFile) {
        if (this.equals(initiator)) {
            return;
        }
        String jdbcVersion = null;
        try {
            jdbcVersion = JdbcClassLoaderFactory.getJdbcJarVersion(driverFile);
        } catch (IOException e) {
            // Do thing
        }
        if (!defaultVersion2FileMap.containsKey(jdbcVersion)
                && !externalVersion2FileMap.containsKey(jdbcVersion)) {
            externalVersion2FileMap.put(jdbcVersion, driverFile);
            JdbcClassLoaderFactory.registerClassLoader(driverFile);
        }
    }

    /**
     * Delete a driver from management
     *
     * @param initiator who triggered the event.
     * @param df driver file full path
     */
    public void afterDelete(IJDBCDriverChangedSubject initiator, String driverFile) {
        if (this.equals(initiator)) {
            return;
        }
        String jdbcVersion = null;
        try {
            jdbcVersion = JdbcClassLoaderFactory.getJdbcJarVersion(driverFile);
        } catch (IOException e) {
            // Do thing
        }
        //		// Don't remove default driver
        //		if (StringUtil.isEqual(defaultVersion2FileMapping.get(jdbcVersion), delDf)) {
        //			JdbcClassLoaderFactory.removeClassLoader(jdbcVersion);
        //			defaultVersion2FileMapping.remove(jdbcVersion);
        //		}

        if (StringUtil.isEqual(externalVersion2FileMap.get(jdbcVersion), driverFile)) {
            JdbcClassLoaderFactory.removeClassLoader(jdbcVersion);
            externalVersion2FileMap.remove(jdbcVersion);
        }
    }

    public void addObservor(IJDBCDriverChangedObserver ob) {
        if (!driverChangingObservers.contains(ob)) {
            driverChangingObservers.add(ob);
        }
    }
}
