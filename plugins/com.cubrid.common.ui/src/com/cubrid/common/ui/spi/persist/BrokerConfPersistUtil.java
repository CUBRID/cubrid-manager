/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.model.CubridBrokerProperty;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;

/**
 * cubrid broker config file load and persit util
 *
 * @author fulei
 * @version 1.0 - 2012-10-29 created by fulei
 */
public class BrokerConfPersistUtil {

    private final Logger LOGGER = LogUtil.getLogger(getClass());

    public static String BROKERNAMECOLUMNTITLE = "BROKER_NAME";
    public static String ANNOTATION = "_annotation";
    public static final String[] UNIFORMCONFIG = {
        "MASTER_SHM_ID", "ADMIN_LOG_FILE", "ACCESS_CONTROL", "ACCESS_CONTROL_FILE"
    };

    /**
     * load cubrid broker conf String from file
     *
     * @param cubridBrokerConfFile
     * @param charset
     * @return CubridBrokerConfig
     * @throws Exception
     */
    public String loadCubridBrokerConfigString(File cubridBrokerConfFile, String charset)
            throws Exception {
        BufferedReader reader = null;
        String lineString = null;
        StringBuilder annotationBuilder = new StringBuilder();

        try {
            reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(cubridBrokerConfFile), charset));
            while ((lineString = reader.readLine()) != null) {
                annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return annotationBuilder.toString();
    }

    /**
     * parseStringLineToCubridBrokerConfig
     *
     * @param content
     * @return
     * @throws Exception
     */
    public BrokerConfig parseStringLineToBrokerConfig(String content) {
        BrokerConfig config = new BrokerConfig();

        String cubridBrokerConfRegex = "\\[.*";
        Pattern cubridBrokerConfPattern = Pattern.compile(cubridBrokerConfRegex);

        String annotationRegex = "#(\\s*).+";
        Pattern annotationPattern = Pattern.compile(annotationRegex);

        String propertyRegex = ".+=.+";
        Pattern propertyPattern = Pattern.compile(propertyRegex);

        CubridBrokerProperty brokerConfProp = null;
        StringBuilder annotationBuilder = new StringBuilder();

        String[] contentArray = content.split(StringUtil.NEWLINE);
        for (String lineString : contentArray) {

            Matcher cubridBrokerConfMatcher = cubridBrokerConfPattern.matcher(lineString);
            Matcher annotationMatcher = annotationPattern.matcher(lineString);
            Matcher propertyMatcher = propertyPattern.matcher(lineString);
            if (cubridBrokerConfMatcher.find()) { // find broker like [broker] [%query_editor]
                brokerConfProp = new CubridBrokerProperty();
                brokerConfProp.setCubridBrokerPropKey(lineString);
                brokerConfProp.setCubridBroker(true);
                if (annotationBuilder != null) {
                    brokerConfProp.setCubridBrokerPropAnnotation(annotationBuilder.toString());
                }
                config.addCubridBrokerProperty(brokerConfProp);

                annotationBuilder = null;
            } else if (annotationMatcher.find() || lineString.equals("")) { // find annotation
                if (annotationBuilder == null) {
                    annotationBuilder = new StringBuilder();
                }
                annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
            } else if (propertyMatcher.find()) { // find key and value
                if (brokerConfProp == null) { // if there is no broker,ignore the property
                    continue;
                }
                String[] keyValueString = lineString.split("=");
                if (keyValueString.length == 2) {
                    String key = keyValueString[0].trim();
                    String value = keyValueString[1].trim();
                    CubridBrokerProperty property =
                            new CubridBrokerProperty(); // new property then set parameter
                    property.setCubridBrokerPropKey(key);
                    property.setCubridBrokerPropValue(value);
                    if (annotationBuilder != null) {
                        property.setCubridBrokerPropAnnotation(annotationBuilder.toString());
                    }
                    brokerConfProp.addCubridBrokerProperty(property);
                    annotationBuilder = null;
                }
            } else {
                // opthers see it to bottom annotation
                if (annotationBuilder != null) {
                    annotationBuilder.append(lineString).append(StringUtil.NEWLINE);
                }
            }
        }
        if (annotationBuilder != null) {
            config.setConfAnnotation(annotationBuilder.toString());
        }

        return config;
    }

    /**
     * parse a CubridBrokerConfig model to a document string
     *
     * @param config CubridBrokerConfig
     * @return String
     */
    public String readBrokerConfig(BrokerConfig config) {
        StringBuilder contents = new StringBuilder();
        for (CubridBrokerProperty cubridBrokerProperty : config.getPropertyList()) {
            String annotation = cubridBrokerProperty.getCubridBrokerPropAnnotation();
            if (annotation != null) {
                contents.append(annotation);
            }
            // if is cubrid broker set broker name
            if (cubridBrokerProperty.isCubridBroker()) {
                contents.append(cubridBrokerProperty.getCubridBrokerPropKey())
                        .append(StringUtil.NEWLINE);
            }
            // loop properies
            for (CubridBrokerProperty property : cubridBrokerProperty.getPropertyList()) {
                annotation = property.getCubridBrokerPropAnnotation();
                if (annotation != null) {
                    contents.append(annotation);
                }
                contents.append(property.getCubridBrokerPropKey())
                        .append("=")
                        .append(property.getCubridBrokerPropValue())
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
     * parse CubridBrokerConfig model to common table value
     *
     * @param config
     * @return
     */
    public List<Map<String, String>> parseBrokerConfigToCommonTableValue(BrokerConfig config) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<String> propList = new ArrayList<String>(); // mark property in the list
        Map<String, String> titleMap = new HashMap<String, String>(); // first row as title

        result.add(titleMap);
        Map<String, String> dataMap = null;
        titleMap.put("0", BROKERNAMECOLUMNTITLE);
        int index = 1;
        for (CubridBrokerProperty cubridBroker : config.getPropertyList()) {
            titleMap.put(Integer.toString(index), cubridBroker.getCubridBrokerPropKey());
            if (cubridBroker.getCubridBrokerPropAnnotation() != null) { // set annotation
                titleMap.put(
                        Integer.toString(index) + ANNOTATION,
                        cubridBroker.getCubridBrokerPropAnnotation());
            }
            index++;
        }

        for (CubridBrokerProperty cubridBroker : config.getPropertyList()) {
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

                    String indexString = getMapKeyByValue(titleMap, brokerName);
                    if (indexString != null) {
                        dataMap.put(indexString, prop.getCubridBrokerPropValue());
                        if (prop.getCubridBrokerPropAnnotation() != null) {
                            dataMap.put(
                                    indexString + ANNOTATION, prop.getCubridBrokerPropAnnotation());
                        }
                    }
                } else {
                    Map<String, String> oneRowData = getRowData(result, properKey);
                    String indexString = getMapKeyByValue(titleMap, brokerName);
                    String value = prop.getCubridBrokerPropValue();
                    String annotation = prop.getCubridBrokerPropAnnotation();
                    if (oneRowData != null && indexString != null && value != null) {
                        oneRowData.put(indexString, value);
                        if (annotation != null) {
                            oneRowData.put(indexString + ANNOTATION, annotation);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * get title key(column index) by value(brokerName)
     *
     * @param value
     * @return
     */
    private String getMapKeyByValue(Map<String, String> titleMap, String value) {
        for (String key : titleMap.keySet()) {
            // first column is name column
            if (key.equals("0")) {
                continue;
            }
            if (value.equals(titleMap.get(key))) {
                return key;
            }
        }
        return null;
    }

    private Map<String, String> getRowData(List<Map<String, String>> dataList, String propertyKey) {
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
     * parse common table value to broker config model
     *
     * @param dataList
     * @return
     */
    public BrokerConfig parseCommonTableValueToBrokerConfig(
            List<Map<String, String>> dataList, int tableColumnCount) {
        BrokerConfig config = new BrokerConfig();
        Map<String, String> brokerNameMap = null;
        for (int i = 0; i < dataList.size(); i++) {
            if (i == 0) {
                // first data is broker name
                brokerNameMap = dataList.get(i);
                for (int j = 1; j < brokerNameMap.size(); j++) {
                    String brokerName = brokerNameMap.get(Integer.toString(j));
                    if (StringUtil.isEmpty(brokerName)) {
                        continue;
                    }
                    String annotation = brokerNameMap.get(Integer.toString(j) + ANNOTATION);
                    CubridBrokerProperty brokerConf = new CubridBrokerProperty();
                    brokerConf.setCubridBrokerPropKey(brokerName);
                    brokerConf.setCubridBrokerPropAnnotation(annotation);
                    brokerConf.setCubridBroker(true);
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
                    if (StringUtil.isNotEmpty(propValue)) {
                        String brokerName = brokerNameMap.get(Integer.toString(j));
                        CubridBrokerProperty brokerConf =
                                getCubridBrokerPropertyByBrokerName(config, brokerName);
                        if (brokerConf != null) {
                            CubridBrokerProperty brokerProp = new CubridBrokerProperty();
                            brokerConf.addCubridBrokerProperty(brokerProp);
                            brokerProp.setCubridBrokerPropKey(propName);
                            brokerProp.setCubridBrokerPropValue(propValue);
                            String annotation = valueMap.get(Integer.toString(j) + ANNOTATION);
                            brokerProp.setCubridBrokerPropAnnotation(annotation);
                        }
                    }
                }
            }
        }

        return config;
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
     * delete broker property by broker name
     *
     * @param config
     * @param brokerName
     */
    public void deleteBrokerPropertyByBrokerName(BrokerConfig config, String brokerName) {
        CubridBrokerProperty deleteProp = getCubridBrokerPropertyByBrokerName(config, brokerName);
        if (deleteProp != null) {
            config.getPropertyList().remove(deleteProp);
        }
    }

    public void writeBrokerConfig(File file, String charset, String contents) throws IOException {
        BufferedWriter fs = null;
        try {
            fs = getBufferedWriter(file.getAbsolutePath(), charset);
            fs.write(contents);
        } finally {
            FileUtil.close(fs);
        }
    }

    /**
     * Get the buffered writer
     *
     * @param file String
     * @param fileCharset String
     * @return BufferedWriter
     * @throws UnsupportedEncodingException The exception
     * @throws FileNotFoundException The exception
     */
    private static BufferedWriter getBufferedWriter(String file, String fileCharset)
            throws UnsupportedEncodingException,
                    FileNotFoundException { // FIXME move this logic to core module
        BufferedWriter fs = null;
        if (fileCharset != null && fileCharset.trim().length() > 0) {
            fs =
                    new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(file), fileCharset.trim()));
        } else {
            fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        }
        return fs;
    }
}
