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
package com.cubrid.cubridmanager.ui.spi.util;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.model.CubridBrokerProperty;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil;
import com.cubrid.cubridmanager.core.broker.task.SetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.editor.UnifyHostConfigEditor;
import com.cubrid.cubridmanager.ui.host.model.CubridCMConfConfig;
import com.cubrid.cubridmanager.ui.host.model.CubridCMConfProperty;
import com.cubrid.cubridmanager.ui.host.model.CubridConfConfig;
import com.cubrid.cubridmanager.ui.host.model.CubridConfProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author fulei this class is used to convert config file to model
 * @version 1.0 - 2013-2-19 created by fulei
 */
public class UnifyHostConfigUtil {

    /**
     * parseStringLineToCubridConfConfig
     *
     * @param content
     * @return
     * @throws Exception
     */
    public CubridConfConfig parseStringLineToCubridConfConfig(String content) {
        CubridConfConfig config = new CubridConfConfig();

        String cubridConfRegex = "\\[.*";
        Pattern cubridConfPattern = Pattern.compile(cubridConfRegex);

        String annotationRegex = "#(\\s*).+";
        Pattern annotationPattern = Pattern.compile(annotationRegex);

        String propertyRegex = ".+=.+";
        Pattern propertyPattern = Pattern.compile(propertyRegex);

        CubridConfProperty confProp = null;
        StringBuilder annotationBuilder = new StringBuilder();

        String[] contentArray = content.split(StringUtil.NEWLINE);
        for (String lineString : contentArray) {

            Matcher cubridConfMatcher = cubridConfPattern.matcher(lineString);
            Matcher annotationMatcher = annotationPattern.matcher(lineString);
            Matcher propertyMatcher = propertyPattern.matcher(lineString);
            if (cubridConfMatcher.find()) { // find broker like [service] [common] [@database]
                confProp = new CubridConfProperty();
                confProp.setCubridConfPropKey(lineString);
                confProp.setCubridConf(true);
                if (annotationBuilder != null) {
                    confProp.setCubridConfPropAnnotation(annotationBuilder.toString());
                }
                config.addCubridConfProperty(confProp);
                annotationBuilder = null;
            } else if (annotationMatcher.find() || lineString.equals("")) { // find annotation
                if (annotationBuilder == null) {
                    annotationBuilder = new StringBuilder();
                }
                annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
            } else if (propertyMatcher.find()) { // find key and value
                if (confProp == null) { // if there is no cubrid conf,ignore the property
                    continue;
                }
                String[] keyValueString = lineString.split("=");
                if (keyValueString.length == 2) {
                    String key = keyValueString[0].trim();
                    String value = keyValueString[1].trim();
                    CubridConfProperty property =
                            new CubridConfProperty(); // new property then set parameter
                    property.setCubridConfPropKey(key);
                    property.setCubridConfPropValue(value);
                    if (annotationBuilder != null) {
                        property.setCubridConfPropAnnotation(annotationBuilder.toString());
                    }
                    confProp.addCubridConfProperty(property);
                    annotationBuilder = null;
                }
            } else { // opthers see it to bottom annotation
                annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
            }
        }
        if (annotationBuilder != null) {
            config.setConfAnnotation(annotationBuilder.toString());
        }

        return config;
    }

    /**
     * parseStringLineToCubridCMConfConfig
     *
     * @param content
     * @return
     * @throws Exception
     */
    public CubridCMConfConfig parseStringLineToCubridCMConfConfig(String content) {
        CubridCMConfConfig config = new CubridCMConfConfig();

        String annotationRegex = "#((\\s*).+)|.+";
        Pattern annotationPattern = Pattern.compile(annotationRegex);

        String propertyRegex = ".+=.+";
        Pattern propertyPattern = Pattern.compile(propertyRegex);

        StringBuilder annotationBuilder = new StringBuilder();

        String[] contentArray = content.split(StringUtil.NEWLINE);
        for (String lineString : contentArray) {
            Matcher propertyMatcher = propertyPattern.matcher(lineString);
            Matcher annotationMatcher = annotationPattern.matcher(lineString);
            if (propertyMatcher.find()) { // find key and value
                if (lineString.startsWith("#")) {
                    if (annotationBuilder == null) {
                        annotationBuilder = new StringBuilder();
                    }
                    annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
                    continue;
                }
                String[] keyValueString = lineString.split("=");
                if (keyValueString.length == 2) {
                    String key = keyValueString[0].trim();
                    String value = keyValueString[1].trim();
                    CubridCMConfProperty property =
                            new CubridCMConfProperty(); // new property then set parameter
                    property.setCubridCMConfPropKey(key);
                    property.setCubridCMConfPropValue(value);
                    if (annotationBuilder != null) {
                        property.setCubridCMConfPropAnnotation((annotationBuilder.toString()));
                    }
                    config.addCubridCMConfProperty(property);
                    annotationBuilder = null;
                }
            } else if (annotationMatcher.find() || lineString.equals("")) { // find annotation
                if (annotationBuilder == null) {
                    annotationBuilder = new StringBuilder();
                }
                annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
            }
        }

        return config;
    }

    /**
     * parse broker data to table value
     *
     * @param Map<String, CubridConfConfig> cubridBrokerConfigDataMap
     */
    public List<Map<String, String>> parseCubridBrokerConfigToCommonTableValue(
            Map<String, BrokerConfig> cubridBrokerConfigDataMap) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<String> propList = new ArrayList<String>(); // mark property in the list
        Map<String, String> dataMap = null;

        Map<String, String> serverMap = new HashMap<String, String>();
        Map<String, String> brokerMap = new HashMap<String, String>();

        // first row as server name
        result.add(serverMap);
        serverMap.put("0", UnifyHostConfigEditor.SERVERNAMECOLUMNTITLE);
        int index = 1;
        for (Entry<String, BrokerConfig> entry : cubridBrokerConfigDataMap.entrySet()) {
            BrokerConfig cubridBrokerConfig = entry.getValue();
            for (CubridBrokerProperty cubridBroker : cubridBrokerConfig.getPropertyList()) {
                serverMap.put("" + index, entry.getKey()); // server name
                if (cubridBroker.getCubridBrokerPropAnnotation() != null) { // set annotation
                    serverMap.put(
                            Integer.toString(index) + UnifyHostConfigEditor.ANNOTATION,
                            cubridBroker.getCubridBrokerPropAnnotation());
                }
                index++;
            }
        }

        // second row as broker name
        result.add(brokerMap);
        brokerMap.put("0", UnifyHostConfigEditor.BROKERNAMECOLUMNTITLE);
        index = 1;
        for (Entry<String, BrokerConfig> entry : cubridBrokerConfigDataMap.entrySet()) {
            BrokerConfig cubridBrokerConfig = entry.getValue();
            for (CubridBrokerProperty cubridBroker : cubridBrokerConfig.getPropertyList()) {
                brokerMap.put("" + index, cubridBroker.getCubridBrokerPropKey()); // broker name
                if (cubridBroker.getCubridBrokerPropAnnotation() != null) { // set annotation
                    brokerMap.put(
                            Integer.toString(index) + UnifyHostConfigEditor.ANNOTATION,
                            cubridBroker.getCubridBrokerPropAnnotation());
                }
                index++;
            }
        }

        for (Entry<String, BrokerConfig> entry : cubridBrokerConfigDataMap.entrySet()) {
            String serviceName = entry.getKey();
            BrokerConfig cubridBrokerConfig = entry.getValue();

            for (CubridBrokerProperty cubridBroker : cubridBrokerConfig.getPropertyList()) {
                String brokerName = cubridBroker.getCubridBrokerPropKey();
                for (CubridBrokerProperty prop : cubridBroker.getPropertyList()) {
                    String properKey = prop.getCubridBrokerPropKey();
                    if (!propList.contains(properKey)) { // a new property
                        propList.add(properKey);
                        dataMap = new HashMap<String, String>();
                        result.add(dataMap);

                        dataMap.put("0", prop.getCubridBrokerPropKey());
                        if (prop.getCubridBrokerPropValue() != null) {
                            dataMap.put("0", prop.getCubridBrokerPropKey());
                        }
                        String indexString =
                                getMapKeyByValue(serverMap, brokerMap, serviceName, brokerName);
                        if (indexString != null) {
                            dataMap.put(indexString, prop.getCubridBrokerPropValue());
                            if (prop.getCubridBrokerPropAnnotation() != null) {
                                dataMap.put(
                                        indexString + UnifyHostConfigEditor.ANNOTATION,
                                        prop.getCubridBrokerPropAnnotation());
                            }
                        }
                    } else {
                        Map<String, String> oneRowData = getRowData(result, properKey);
                        String indexString =
                                getMapKeyByValue(serverMap, brokerMap, serviceName, brokerName);
                        String value = prop.getCubridBrokerPropValue();
                        String annotation = prop.getCubridBrokerPropAnnotation();
                        if (oneRowData != null && indexString != null && value != null) {
                            oneRowData.put(indexString, value);
                            if (annotation != null) {
                                oneRowData.put(
                                        indexString + UnifyHostConfigEditor.ANNOTATION, annotation);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * parse cubrid data to table value
     *
     * @param Map<String, CubridConfConfig> cubridConfConfigDataMap
     */
    public List<Map<String, String>> parseCubridConfConfigToCommonTableValue(
            Map<String, CubridConfConfig> cubridConfConfigDataMap) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<String> propList = new ArrayList<String>(); // mark property in the list
        Map<String, String> dataMap = null;

        Map<String, String> serverMap = new HashMap<String, String>();
        Map<String, String> cubridMap = new HashMap<String, String>();

        // first row as server name
        result.add(serverMap);
        serverMap.put("0", UnifyHostConfigEditor.SERVERNAMECOLUMNTITLE);
        int index = 1;
        for (Entry<String, CubridConfConfig> entry : cubridConfConfigDataMap.entrySet()) {
            CubridConfConfig cubridConfConfig = entry.getValue();
            for (CubridConfProperty cubridConfProperty : cubridConfConfig.getPropertyList()) {
                serverMap.put("" + index, entry.getKey()); // server name
                if (cubridConfProperty.getCubridConfPropAnnotation() != null) { // set annotation
                    serverMap.put(
                            Integer.toString(index) + UnifyHostConfigEditor.ANNOTATION,
                            cubridConfProperty.getCubridConfPropAnnotation());
                }
                index++;
            }
        }

        // second row as broker name
        result.add(cubridMap);
        cubridMap.put("0", UnifyHostConfigEditor.CUBRIDNAMECOLUMNTITLE);
        index = 1;
        for (Entry<String, CubridConfConfig> entry : cubridConfConfigDataMap.entrySet()) {
            CubridConfConfig cubridConfConfig = entry.getValue();
            for (CubridConfProperty cubridConfProperty : cubridConfConfig.getPropertyList()) {
                cubridMap.put("" + index, cubridConfProperty.getCubridConfPropKey()); // cubrid name
                if (cubridConfProperty.getCubridConfPropAnnotation() != null) { // set annotation
                    cubridMap.put(
                            Integer.toString(index) + UnifyHostConfigEditor.ANNOTATION,
                            cubridConfProperty.getCubridConfPropAnnotation());
                }
                index++;
            }
        }

        for (Entry<String, CubridConfConfig> entry : cubridConfConfigDataMap.entrySet()) {
            String serviceName = entry.getKey();
            CubridConfConfig cubridConfConfig = entry.getValue();

            for (CubridConfProperty cubridConfProperty : cubridConfConfig.getPropertyList()) {
                String cubridName = cubridConfProperty.getCubridConfPropKey();
                for (CubridConfProperty prop : cubridConfProperty.getPropertyList()) {
                    String properKey = prop.getCubridConfPropKey();
                    if (!propList.contains(properKey)) { // a new property
                        propList.add(properKey);
                        dataMap = new HashMap<String, String>();
                        result.add(dataMap);

                        dataMap.put("0", prop.getCubridConfPropKey());
                        if (prop.getCubridConfPropValue() != null) {
                            dataMap.put("0", prop.getCubridConfPropKey());
                        }
                        String indexString =
                                getMapKeyByValue(serverMap, cubridMap, serviceName, cubridName);
                        if (indexString != null) {
                            dataMap.put(indexString, prop.getCubridConfPropValue());
                            if (prop.getCubridConfPropAnnotation() != null) {
                                dataMap.put(
                                        indexString + UnifyHostConfigEditor.ANNOTATION,
                                        prop.getCubridConfPropAnnotation());
                            }
                        }
                    } else {
                        Map<String, String> oneRowData = getRowData(result, properKey);
                        String indexString =
                                getMapKeyByValue(serverMap, cubridMap, serviceName, cubridName);
                        String value = prop.getCubridConfPropValue();
                        String annotation = prop.getCubridConfPropAnnotation();
                        if (oneRowData != null && indexString != null && value != null) {
                            oneRowData.put(indexString, value);
                            if (annotation != null) {
                                oneRowData.put(
                                        indexString + UnifyHostConfigEditor.ANNOTATION, annotation);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * parse cubrid cm conf data to table value
     *
     * @param Map<String, CubridConfConfig> cubridConfConfigDataMap
     */
    public List<Map<String, String>> parseCubridCMConfConfigToCommonTableValue(
            Map<String, CubridCMConfConfig> cubridCMConfConfigDataMap) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<String> propList = new ArrayList<String>(); // mark property in the list
        Map<String, String> dataMap = null;

        Map<String, String> serverMap = new HashMap<String, String>();

        // first row as server name
        result.add(serverMap);
        serverMap.put("0", UnifyHostConfigEditor.SERVERNAMECOLUMNTITLE);
        int index = 1;
        for (String serverName : cubridCMConfConfigDataMap.keySet()) {
            serverMap.put("" + (index++), serverName); // server name
        }

        for (Entry<String, CubridCMConfConfig> entry : cubridCMConfConfigDataMap.entrySet()) {
            String serverName = entry.getKey();
            CubridCMConfConfig cubridCMConfConfig = entry.getValue();
            for (CubridCMConfProperty cubridCMConfProperty : cubridCMConfConfig.getPropertyList()) {
                String properKey = cubridCMConfProperty.getCubridCMConfPropKey();
                if (!propList.contains(properKey)) { // a new property
                    propList.add(properKey);
                    dataMap = new HashMap<String, String>();
                    result.add(dataMap);
                    if (cubridCMConfProperty.getCubridCMConfPropValue() != null) {
                        dataMap.put("0", cubridCMConfProperty.getCubridCMConfPropKey());
                    }
                    String indexString = getMapKeyByValue(serverMap, serverName);
                    if (indexString != null) {
                        dataMap.put(indexString, cubridCMConfProperty.getCubridCMConfPropValue());
                        if (cubridCMConfProperty.getCubridCMConfPropAnnotation() != null) {
                            dataMap.put(
                                    indexString + UnifyHostConfigEditor.ANNOTATION,
                                    cubridCMConfProperty.getCubridCMConfPropAnnotation());
                        }
                    }
                } else {
                    Map<String, String> oneRowData = getRowData(result, properKey);
                    String indexString = getMapKeyByValue(serverMap, serverName);
                    String value = cubridCMConfProperty.getCubridCMConfPropValue();
                    String annotation = cubridCMConfProperty.getCubridCMConfPropAnnotation();
                    if (oneRowData != null && indexString != null && value != null) {
                        oneRowData.put(indexString, value);
                        if (annotation != null) {
                            oneRowData.put(
                                    indexString + UnifyHostConfigEditor.ANNOTATION, annotation);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * parse common table value to broker config model list
     *
     * @param dataList
     * @return
     */
    public LinkedHashMap<String, BrokerConfig> parseCommonTableValueToCubridBrokerConfig(
            List<Map<String, String>> dataList, int tableColumnCount) {
        LinkedHashMap<String, BrokerConfig> cubridBrokerConfigMap =
                new LinkedHashMap<String, BrokerConfig>();
        Map<String, String> serverNameMap = null;
        Map<String, String> brokerNameMap = null;
        for (int i = 0; i < dataList.size(); i++) {
            if (i == 0) { // first data is server name
                serverNameMap = dataList.get(i);
                for (int j = 1; j < serverNameMap.size(); j++) {
                    String serverName = serverNameMap.get(j + "");
                    if (serverName == null || "".equals(serverName)) {
                        continue;
                    }
                    if (cubridBrokerConfigMap.get(serverName) == null) {
                        BrokerConfig config = new BrokerConfig();
                        cubridBrokerConfigMap.put(serverName, config);
                    }
                }
            } else if (i == 1) { // second data is server name
                brokerNameMap = dataList.get(i);
                for (int j = 1; j < brokerNameMap.size(); j++) {
                    String brokerName = brokerNameMap.get(j + "");
                    if (brokerName == null || "".equals(brokerName)) {
                        continue;
                    }
                    String annotation = brokerNameMap.get(j + UnifyHostConfigEditor.ANNOTATION);
                    CubridBrokerProperty brokerConf = new CubridBrokerProperty();
                    brokerConf.setCubridBrokerPropKey(brokerName);
                    brokerConf.setCubridBrokerPropAnnotation(annotation);
                    brokerConf.setCubridBroker(true);
                    String serverName = serverNameMap.get(j + "");
                    BrokerConfig config = cubridBrokerConfigMap.get(serverName);
                    config.addCubridBrokerProperty(brokerConf);
                }
            } else {
                Map<String, String> valueMap = dataList.get(i);
                String propName = "";
                String propValue = "";
                for (int j = 0; j < tableColumnCount; j++) {
                    String value = valueMap.get(Integer.toString(j));
                    if (j == 0) {
                        propName = value;
                        continue;
                    }
                    propValue = value;
                    if (propValue != null && !"".equals(propValue)) {
                        String brokerName = brokerNameMap.get(j + "");
                        String serverName = serverNameMap.get(j + "");
                        BrokerConfig config = cubridBrokerConfigMap.get(serverName);

                        CubridBrokerProperty brokerConf =
                                getCubridBrokerPropertyByBrokerName(config, brokerName);
                        if (brokerConf != null) {
                            CubridBrokerProperty brokerProp = new CubridBrokerProperty();
                            brokerConf.addCubridBrokerProperty(brokerProp);
                            brokerProp.setCubridBrokerPropKey(propName);
                            brokerProp.setCubridBrokerPropValue(propValue);
                            String annotation =
                                    valueMap.get(
                                            Integer.toString(j) + UnifyHostConfigEditor.ANNOTATION);
                            brokerProp.setCubridBrokerPropAnnotation(annotation);
                        }
                    }
                }
            }
        }
        return cubridBrokerConfigMap;
    }

    /**
     * parse common table value to cubrid config model list
     *
     * @param dataList
     * @return
     */
    public LinkedHashMap<String, CubridConfConfig> parseCommonTableValueToCubridConfConfig(
            List<Map<String, String>> dataList, int tableColumnCount) {
        LinkedHashMap<String, CubridConfConfig> cubridConfConfigMap =
                new LinkedHashMap<String, CubridConfConfig>();
        Map<String, String> serverNameMap = null;
        Map<String, String> cubridConfNameMap = null;
        for (int i = 0; i < dataList.size(); i++) {
            if (i == 0) { // first data is server name
                serverNameMap = dataList.get(i);
                for (int j = 1; j < serverNameMap.size(); j++) {
                    String serverName = serverNameMap.get(j + "");
                    if (serverName == null || "".equals(serverName)) {
                        continue;
                    }
                    if (cubridConfConfigMap.get(serverName) == null) {
                        CubridConfConfig config = new CubridConfConfig();
                        cubridConfConfigMap.put(serverName, config);
                    }
                }
            } else if (i == 1) { // second data is server name
                cubridConfNameMap = dataList.get(i);
                for (int j = 1; j < cubridConfNameMap.size(); j++) {
                    String cubridConfName = cubridConfNameMap.get(j + "");
                    if (cubridConfName == null || "".equals(cubridConfName)) {
                        continue;
                    }
                    String annotation = cubridConfNameMap.get(j + UnifyHostConfigEditor.ANNOTATION);
                    CubridConfProperty cubridConfProperty = new CubridConfProperty();
                    cubridConfProperty.setCubridConfPropKey(cubridConfName);
                    cubridConfProperty.setCubridConfPropAnnotation(annotation);
                    cubridConfProperty.setCubridConf(true);
                    String serverName = serverNameMap.get(j + "");
                    CubridConfConfig config = cubridConfConfigMap.get(serverName);
                    config.addCubridConfProperty(cubridConfProperty);
                }
            } else {
                Map<String, String> valueMap = dataList.get(i);
                String propName = "";
                String propValue = "";
                for (int j = 0; j < tableColumnCount; j++) {
                    String value = valueMap.get(Integer.toString(j));
                    if (j == 0) {
                        propName = value;
                        continue;
                    }
                    propValue = value;
                    if (propValue != null && !"".equals(propValue)) {
                        String cubridConfName = cubridConfNameMap.get(j + "");
                        String serverName = serverNameMap.get(j + "");
                        CubridConfConfig config = cubridConfConfigMap.get(serverName);

                        CubridConfProperty cubridConf =
                                getCubridConfPropertyByCubridConfName(config, cubridConfName);
                        if (cubridConf != null) {
                            CubridConfProperty cubridConfProp = new CubridConfProperty();
                            cubridConf.addCubridConfProperty(cubridConfProp);
                            cubridConfProp.setCubridConfPropKey(propName);
                            cubridConfProp.setCubridConfPropValue(propValue);
                            String annotation =
                                    valueMap.get(
                                            Integer.toString(j) + UnifyHostConfigEditor.ANNOTATION);
                            cubridConfProp.setCubridConfPropAnnotation(annotation);
                        }
                    }
                }
            }
        }
        return cubridConfConfigMap;
    }

    /**
     * parse common table value to cubrid config model list
     *
     * @param dataList
     * @return
     */
    public LinkedHashMap<String, CubridCMConfConfig> parseCommonTableValueToCubridCMConfConfig(
            List<Map<String, String>> dataList, int tableColumnCount) {
        LinkedHashMap<String, CubridCMConfConfig> cubridCMConfConfigMap =
                new LinkedHashMap<String, CubridCMConfConfig>();
        Map<String, String> serverNameMap = null;
        for (int i = 0; i < dataList.size(); i++) {
            if (i == 0) { // first data is server name
                serverNameMap = dataList.get(i);
                for (int j = 1; j < serverNameMap.size(); j++) {
                    String serverName = serverNameMap.get(j + "");
                    if (serverName == null || "".equals(serverName)) {
                        continue;
                    }
                    if (cubridCMConfConfigMap.get(serverName) == null) {
                        CubridCMConfConfig config = new CubridCMConfConfig();
                        cubridCMConfConfigMap.put(serverName, config);
                    }
                }
            } else {
                Map<String, String> valueMap = dataList.get(i);
                String propName = "";
                String propValue = "";
                for (int j = 0; j < tableColumnCount; j++) {
                    String value = valueMap.get(Integer.toString(j));
                    if (j == 0) {
                        propName = value;
                        continue;
                    }
                    propValue = value;
                    if (propValue != null && !"".equals(propValue)) {
                        String serverName = serverNameMap.get(j + "");
                        CubridCMConfConfig cubridCMConfConfig =
                                cubridCMConfConfigMap.get(serverName);

                        CubridCMConfProperty cubridCMConfProperty = new CubridCMConfProperty();
                        cubridCMConfConfig.addCubridCMConfProperty(cubridCMConfProperty);
                        cubridCMConfProperty.setCubridCMConfPropKey(propName);
                        cubridCMConfProperty.setCubridCMConfPropValue(propValue);
                        String annotation =
                                valueMap.get(
                                        Integer.toString(j) + UnifyHostConfigEditor.ANNOTATION);
                        cubridCMConfProperty.setCubridCMConfPropAnnotation(annotation);
                    }
                }
            }
        }
        return cubridCMConfConfigMap;
    }

    /**
     * get broker property by broker name
     *
     * @param config
     * @param brokerName
     * @return
     */
    private CubridBrokerProperty getCubridBrokerPropertyByBrokerName(
            BrokerConfig config, String brokerName) {
        for (CubridBrokerProperty prop : config.getPropertyList()) {
            if (brokerName.equals(prop.getCubridBrokerPropKey())) {
                return prop;
            }
        }
        return null;
    }

    /**
     * get cubrid property by cubrid conf name
     *
     * @param config
     * @param cubridConfName
     * @return
     */
    private CubridConfProperty getCubridConfPropertyByCubridConfName(
            CubridConfConfig config, String cubridConfName) {
        for (CubridConfProperty prop : config.getPropertyList()) {
            if (cubridConfName.equals(prop.getCubridConfPropKey())) {
                return prop;
            }
        }
        return null;
    }

    /**
     * get title key(column index) by value(brokerName)
     *
     * @param value
     * @return
     */
    public String getMapKeyByValue(
            Map<String, String> serviceMap,
            Map<String, String> brokerMap,
            String serviceName,
            String brokerName) {
        for (String key : brokerMap.keySet()) {
            // first column is name column
            if (key.equals("0")) {
                continue;
            }
            if (brokerName.equals(brokerMap.get(key)) && serviceName.equals(serviceMap.get(key))) {
                return key;
            }
        }
        return null;
    }

    /**
     * get title key(column index) by value(server name)
     *
     * @param value
     * @return
     */
    public String getMapKeyByValue(Map<String, String> serviceMap, String serviceName) {
        for (String key : serviceMap.keySet()) {
            // first column is name column
            if (key.equals("0")) {
                continue;
            }
            if (serviceName.equals(serviceMap.get(key))) {
                return key;
            }
        }
        return null;
    }

    public Map<String, String> getRowData(List<Map<String, String>> dataList, String propertyKey) {
        for (Map<String, String> oneRowData : dataList) {
            for (String key : oneRowData.keySet()) {
                if (oneRowData.get(key).equals(propertyKey)) {
                    return oneRowData;
                }
            }
        }
        return null;
    }

    /**
     * save cubrid broker conf
     *
     * @param monitor
     * @param brokerConfMap
     * @param editorInput
     * @return failed server
     */
    public List<String> saveBrokerConf(
            IProgressMonitor monitor,
            LinkedHashMap<String, BrokerConfig> brokerConfMap,
            CubridServer[] cubridServers) {
        List<String> failedServer = new ArrayList<String>();
        BrokerConfPersistUtil cubridBrokerConfUtil = new BrokerConfPersistUtil();
        for (Entry<String, BrokerConfig> entry : brokerConfMap.entrySet()) {

            BrokerConfig cubridBrokerConfig = entry.getValue();
            String serverName = entry.getKey();
            String contents = cubridBrokerConfUtil.readBrokerConfig(cubridBrokerConfig);

            CubridServer cubridServer = getCubridServer(serverName, cubridServers);
            if (cubridServer != null) {
                monitor.subTask(
                        Messages.bind(
                                Messages.unifyHostConfigEditorSavingDataMsg2,
                                "broker.conf",
                                serverName));
                String[] lines = contents.split(System.getProperty("line.separator"));
                SetBrokerConfParameterTask task =
                        new SetBrokerConfParameterTask(cubridServer.getServerInfo());
                task.setConfContents(Arrays.asList(lines));
                task.execute();
                if (!task.isSuccess()) {
                    failedServer.add(cubridServer.getName());
                }
                task.finish();
                monitor.worked(1);
            }
        }
        return failedServer;
    }

    /**
     * save cubrid broker conf
     *
     * @param monitor
     * @param brokerConfMap
     * @param editorInput
     * @return failed server
     */
    public List<String> saveCubridConf(
            IProgressMonitor monitor,
            LinkedHashMap<String, CubridConfConfig> cubridConfMap,
            CubridServer[] cubridServers) {
        List<String> failedServer = new ArrayList<String>();
        for (Entry<String, CubridConfConfig> entry : cubridConfMap.entrySet()) {

            CubridConfConfig cubridConfConfig = entry.getValue();
            String serverName = entry.getKey();
            String contents = parseCubridConfConfigToDocumnetString(cubridConfConfig);
            CubridServer cubridServer = getCubridServer(serverName, cubridServers);
            if (cubridServer != null) {
                monitor.subTask(
                        Messages.bind(
                                Messages.unifyHostConfigEditorSavingDataMsg2,
                                "cubrid.conf",
                                serverName));
                String[] lines = contents.split(System.getProperty("line.separator"));

                SetCubridConfParameterTask task =
                        new SetCubridConfParameterTask(cubridServer.getServerInfo());
                task.setConfContents(Arrays.asList(lines));
                task.execute();
                if (!task.isSuccess()) {
                    failedServer.add(cubridServer.getName());
                }
                task.finish();
                monitor.worked(1);
            }
        }
        return failedServer;
    }

    /**
     * save cubrid broker conf
     *
     * @param monitor
     * @param brokerConfMap
     * @param editorInput
     * @return failed server
     */
    public List<String> saveCubridCMConf(
            IProgressMonitor monitor,
            LinkedHashMap<String, CubridCMConfConfig> cubridCMConfMap,
            CubridServer[] cubridServers) {
        List<String> failedServer = new ArrayList<String>();
        for (Entry<String, CubridCMConfConfig> entry : cubridCMConfMap.entrySet()) {

            CubridCMConfConfig cubridCMConfConfig = entry.getValue();
            String serverName = entry.getKey();
            String contents = parseCubridCMConfConfigToDocumnetString(cubridCMConfConfig);
            CubridServer cubridServer = getCubridServer(serverName, cubridServers);
            if (cubridServer != null) {
                monitor.subTask(
                        Messages.bind(
                                Messages.unifyHostConfigEditorSavingDataMsg2,
                                "cm.conf",
                                serverName));
                String[] lines = contents.split(System.getProperty("line.separator"));

                SetCMConfParameterTask task =
                        new SetCMConfParameterTask(cubridServer.getServerInfo());
                task.setConfContents(Arrays.asList(lines));
                task.execute();
                if (!task.isSuccess()) {
                    failedServer.add(cubridServer.getName());
                }
                task.finish();
                monitor.worked(1);
            }
        }
        return failedServer;
    }

    /**
     * parse a CubridConfConfig model to a document string
     *
     * @param config CubridBrokerConfig
     * @return String
     */
    public String parseCubridConfConfigToDocumnetString(CubridConfConfig config) {
        StringBuilder contents = new StringBuilder();
        for (CubridConfProperty cubridConfConfig : config.getPropertyList()) {
            String annotation = cubridConfConfig.getCubridConfPropAnnotation();
            if (annotation != null) {
                contents.append(annotation);
            }
            // if is cubrid conf set broker name
            if (cubridConfConfig.isCubridConf()) {
                contents.append(cubridConfConfig.getCubridConfPropKey()).append(StringUtil.NEWLINE);
            }
            // loop properies
            for (CubridConfProperty property : cubridConfConfig.getPropertyList()) {
                annotation = property.getCubridConfPropAnnotation();
                if (annotation != null) {
                    contents.append(annotation);
                }
                contents.append(property.getCubridConfPropKey())
                        .append("=")
                        .append(property.getCubridConfPropValue())
                        .append(StringUtil.NEWLINE);
            }
        }
        // add bottom annotation
        if (config.getConfAnnotation() != null) {
            contents.append(config.getConfAnnotation());
        }

        return contents.toString();
    }

    /**
     * parse a CubridConfConfig model to a document string
     *
     * @param config CubridBrokerConfig
     * @return String
     */
    public String parseCubridCMConfConfigToDocumnetString(CubridCMConfConfig config) {
        StringBuilder contents = new StringBuilder();
        for (CubridCMConfProperty cubridCMConfProperty : config.getPropertyList()) {
            String annotation = cubridCMConfProperty.getCubridCMConfPropAnnotation();
            if (annotation != null) {
                contents.append(annotation);
            }
            contents.append(cubridCMConfProperty.getCubridCMConfPropKey())
                    .append("=")
                    .append(cubridCMConfProperty.getCubridCMConfPropValue())
                    .append(StringUtil.NEWLINE);
        }

        return contents.toString();
    }

    /**
     * delete cubrid property by section name
     *
     * @param config
     * @param sectionName
     */
    public void deleteCubridConfPropertyBySectionName(CubridConfConfig config, String sectionName) {
        CubridConfProperty deleteProp = getCubridConfPropertyBySectionName(config, sectionName);
        if (deleteProp != null) {
            config.getPropertyList().remove(deleteProp);
        }
    }

    /**
     * get cubrid conf property by section name
     *
     * @param config
     * @param sectionName
     * @return
     */
    private CubridConfProperty getCubridConfPropertyBySectionName(
            CubridConfConfig config, String sectionName) {
        for (CubridConfProperty prop : config.getPropertyList()) {
            if (sectionName.equals(prop.getCubridConfPropKey())) {
                return prop;
            }
        }
        return null;
    }

    /**
     * get CubridServer by server name from editor input
     *
     * @param serverName
     * @param cubridServers
     * @return
     */
    public CubridServer getCubridServer(String serverName, CubridServer[] cubridServers) {
        for (CubridServer cubridServer : cubridServers) {
            if (serverName.equals(cubridServer.getName())) {
                return cubridServer;
            }
        }
        return null;
    }
}
