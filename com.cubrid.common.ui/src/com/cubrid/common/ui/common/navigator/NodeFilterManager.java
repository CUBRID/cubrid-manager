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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ViewerFilter;
import org.slf4j.Logger;

/**
 * Node filter manager is to manage and persist all filter pattern
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 */
public final class NodeFilterManager { // FIXME move logic class to core module

    private static final Logger LOGGER = LogUtil.getLogger(NodeFilterManager.class);
    private static final String FILTER_XML_CONTENT = "CUBRID_NODE_FILTER";
    private static NodeFilterManager instance;
    private final List<String> idFilterList = new ArrayList<String>();
    private final List<String> idGrayFilterList = new ArrayList<String>();
    private final List<String> nameFilterList = new ArrayList<String>();
    private String matchFilter = null;
    private final ViewerFilter[] viewerFilter = new ViewerFilter[] {new NavigatorViewFilter()};

    private NodeFilterManager() {}

    /**
     * Get the only NodeFilterManager instance
     *
     * @return NodeFilterManager
     */
    public static NodeFilterManager getInstance() {
        synchronized (NodeFilterManager.class) {
            if (instance == null) {
                instance = new NodeFilterManager();
                instance.loadFilterSetting();
            }
        }
        return instance;
    }

    public String getMatchFilter() {
        return matchFilter;
    }

    public void setMatchFilter(String matchFilter) {
        this.matchFilter = matchFilter;
    }

    @SuppressWarnings("deprecation")
    private void loadFilterSetting() {
        synchronized (this) {
            IEclipsePreferences preference = new InstanceScope().getNode(CommonUIPlugin.PLUGIN_ID);
            String xmlString = preference.get(FILTER_XML_CONTENT, "");
            if (StringUtil.isEmpty(xmlString)) {
                LOGGER.warn("The preference.get(FILTER_XML_CONTENT) has a empty string.");
                return;
            }

            try {
                ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
                IXMLMemento memento = XMLMemento.loadMemento(in);
                if (memento == null) {
                    LOGGER.error("XMLMemento.loadMemento(in) is a null.");
                    return;
                }

                IXMLMemento[] children = memento.getChildren("idFilter");
                for (int i = 0; i < children.length; i++) {
                    String id = children[i].getString("id");
                    idFilterList.add(id);
                }
                children = memento.getChildren("idGrayFilter");
                for (int i = 0; i < children.length; i++) {
                    String id = children[i].getString("id");
                    idGrayFilterList.add(id);
                }
                children = memento.getChildren("nameFilter");
                for (int i = 0; i < children.length; i++) {
                    String pattern = children[i].getString("pattern");
                    nameFilterList.add(pattern);
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("loadFilterSetting()", e);
            }
        }
    }

    /** Save filter setting to plug-in preference */
    @SuppressWarnings("deprecation")
    public void saveFilterSetting() {
        synchronized (this) {
            try {
                XMLMemento memento = XMLMemento.createWriteRoot("filters");
                if (memento == null) {
                    LOGGER.error("XMLMemento.createWriteRoot('filters') is a null.");
                    return;
                }

                Iterator<String> iterator = idFilterList.iterator();
                while (iterator.hasNext()) {
                    String pattern = (String) iterator.next();
                    IXMLMemento child = memento.createChild("idFilter");
                    child.putString("id", pattern);
                }
                iterator = idGrayFilterList.iterator();
                while (iterator.hasNext()) {
                    String pattern = (String) iterator.next();
                    IXMLMemento child = memento.createChild("idGrayFilter");
                    child.putString("id", pattern);
                }
                iterator = nameFilterList.iterator();
                while (iterator.hasNext()) {
                    String pattern = (String) iterator.next();
                    IXMLMemento child = memento.createChild("nameFilter");
                    child.putString("pattern", pattern);
                }

                String xmlString = memento.saveToString();
                IEclipsePreferences preference =
                        new InstanceScope().getNode(CommonUIPlugin.PLUGIN_ID);
                preference.put(FILTER_XML_CONTENT, xmlString);
                preference.flush();
            } catch (Exception e) {
                LOGGER.error("saveFilterSetting", e);
            }
        }
    }

    /**
     * Add the id filter
     *
     * @param id String
     */
    public void addIdFilter(String id) {
        synchronized (this) {
            if (!idFilterList.contains(id)) {
                idFilterList.add(id);
                saveFilterSetting();
            }
        }
    }

    /**
     * Remove the filter which match this id
     *
     * @param id String
     */
    public void removeIdFilter(String id) {
        synchronized (this) {
            if (idFilterList.contains(id)) {
                idFilterList.remove(id);
                saveFilterSetting();
            }
        }
    }

    /**
     * Remove the filter which match this prefix
     *
     * @param prefix String
     * @param isSaved boolean
     */
    public void removeIdFilterByPrefix(String prefix, boolean isSaved) {
        synchronized (this) {
            List<String> deletedList = new ArrayList<String>();
            for (String id : idFilterList) {
                if (id.startsWith(prefix)) {
                    deletedList.add(id);
                }
            }
            idFilterList.removeAll(deletedList);
            if (isSaved) {
                saveFilterSetting();
            }
        }
    }

    /**
     * Remove the filter which match this prefix
     *
     * @param prefix String
     * @param isSaved boolean
     */
    public void removeIdGrayFilterByPrefix(String prefix, boolean isSaved) {
        synchronized (this) {
            List<String> deletedList = new ArrayList<String>();
            for (String id : idGrayFilterList) {
                if (id.startsWith(prefix)) {
                    deletedList.add(id);
                }
            }
            idGrayFilterList.removeAll(deletedList);
            if (isSaved) {
                saveFilterSetting();
            }
        }
    }

    /**
     * Return whether has hidden element start with prefix
     *
     * @param prefix String
     * @return boolean
     */
    public boolean isHasHiddenByPrefix(String prefix) {
        synchronized (this) {
            for (String id : idFilterList) {
                if (id.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set the filter id list
     *
     * @param idList List<String>
     */
    public void setIdFilterList(List<String> idList) {
        synchronized (this) {
            idFilterList.clear();
            idFilterList.addAll(idList);
            saveFilterSetting();
        }
    }

    /**
     * Add the filter id list
     *
     * @param idList List<String>
     */
    public void addIdFilterList(List<String> idList) {
        synchronized (this) {
            idFilterList.addAll(idList);
            saveFilterSetting();
        }
    }

    /**
     * Set the filter name list
     *
     * @param nameList List<String>
     */
    public void setNameFilterList(List<String> nameList) {
        synchronized (this) {
            nameFilterList.clear();
            nameFilterList.addAll(nameList);
            saveFilterSetting();
        }
    }

    /**
     * Return whether this pattern exist
     *
     * @param id String
     * @return boolean
     */
    public boolean isExistIdFilter(String id) {
        for (int i = 0; i < idFilterList.size(); i++) {
            String filterPattern = idFilterList.get(i);
            if (filterPattern.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether match this string by name filter
     *
     * @param str String
     * @return boolean
     */
    public boolean isMatch(String str) {
        if (matchFilter != null) {
            String filterPattern = null;
            filterPattern = matchFilter.replaceAll("\\*", ".*").replaceAll("\\?", ".?");
            if (str.matches(filterPattern)) {
                return false;
            }
            return true;
        }

        for (int i = 0; i < nameFilterList.size(); i++) {
            String filterPattern = nameFilterList.get(i);
            filterPattern = filterPattern.replaceAll("\\*", ".*").replaceAll("\\?", ".?");
            if (str.matches(filterPattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return this node whether hidden
     *
     * @param node ICubridNode
     * @return boolean
     */
    public boolean isHidden(ICubridNode node) {
        String id = node.getId();
        if (isExistIdFilter(id)) {
            return true;
        }
        String str = node.getLabel();
        if (isMatch(str)) {
            return true;
        }
        ICubridNode parent = node.getParent();
        while (parent != null) {
            if (isHidden(parent)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Retrieves a copy of idFilterList
     *
     * @return a copy of idFilterList
     */
    public List<String> getIdFilterList() {
        List<String> result = new ArrayList<String>();
        result.addAll(idFilterList);
        return result;
    }

    /**
     * Retrieves a copy of nameFilterList
     *
     * @return a copy of nameFilterList
     */
    public List<String> getNameFilterList() {
        List<String> result = new ArrayList<String>();
        result.addAll(nameFilterList);
        return result;
    }

    public List<String> getIdGrayFilterList() {
        return idGrayFilterList;
    }

    /**
     * Set the filter id gray list
     *
     * @param idGrayList List<String>
     */
    public void setIdGrayFilterList(List<String> idGrayList) {
        synchronized (this) {
            idGrayFilterList.clear();
            idGrayFilterList.addAll(idGrayList);
            saveFilterSetting();
        }
    }

    /**
     * Add the filter id gray list
     *
     * @param idGrayList List<String>
     */
    public void addIdGrayFilterList(List<String> idGrayList) {
        synchronized (this) {
            idGrayFilterList.addAll(idGrayList);
            saveFilterSetting();
        }
    }

    /**
     * Retrieves a copy of viewerFilter
     *
     * @return a copy of viewerFilter
     */
    public ViewerFilter[] getViewerFilter() {
        ViewerFilter[] filters = new ViewerFilter[viewerFilter.length];
        System.arraycopy(viewerFilter, 0, filters, 0, viewerFilter.length);
        return filters;
    }
}
