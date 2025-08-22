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
package com.cubrid.cubridmanager.ui.mondashboard.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.BasicCounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterType;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.RangeType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;

/**
 * This class is responsible for generating history file, recording data into history file and
 * distilling data from history file.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-7-27 created by lizhiqiang
 */
public class HistoryFileHelp {
    private static final Logger LOGGER = LogUtil.getLogger(HistoryFileHelp.class);
    private String historyPath;
    private CounterFile countFile;
    private boolean isChangedHistoryPath;
    private int interval = 4; // default 4s
    private int maxCount = 36000;

    /**
     * Create or Open a history file when the history data should be restored.
     *
     * @param typeNames the array of type names
     * @return the instance of CounterFile
     */
    private CounterFile createOrOpenHistoryFile(String[] typeNames) {
        File counter = new File(historyPath);
        if (counter.exists() && counter.isFile()) {
            try {
                countFile = new BasicCounterFile(counter, null);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        } else {
            List<CounterType> counterLst = new ArrayList<CounterType>();
            for (String type : typeNames) {
                CounterType counterType = new CounterType(type, true, false, RangeType.INT);
                counterLst.add(counterType);
            }
            CounterType[] types = counterLst.toArray(new CounterType[counterLst.size()]);
            Properties props = new Properties();
            try {
                countFile = new BasicCounterFile(counter, types, maxCount, interval, 0, props);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        return countFile;
    }

    /**
     * Storage the data into the local file.
     *
     * @param <T> the generic type which is the sub type of IDiagPara
     * @param updateMap a instance of TreeMap which include all the data that will be storage.
     * @param ts a generic array, for instance BrokerDiagEnum.values(),DbStatDumpEnum.values()
     */
    public <T extends IDiagPara> void storageData(Map<String, String> updateMap, T[] ts) {
        long time = System.currentTimeMillis();
        for (T diagName : ts) {
            try {
                String type = diagName.getName();
                long value = Long.valueOf(updateMap.get(type));
                countFile.updateData(time, type, value);
            } catch (NumberFormatException e) {
                LOGGER.error(e.getMessage());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * Open a history file while reading the history data
     *
     * @return the instance of CounterFile
     */
    public CounterFile openHistoryFile() {
        File counter = new File(historyPath);
        if (counter.exists() && counter.isFile()) {
            try {
                countFile = new BasicCounterFile(counter, null);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        } else {
            // confirm
            CommonUITool.openErrorBox(Messages.wainNoHistoryFile);
        }
        return countFile;
    }

    /**
     * Get the instance of CounterFile when the history data should be restored.
     *
     * @param typeNames the array of types
     */
    public void buildCountFile(String[] typeNames) {
        if (countFile == null || isChangedHistoryPath) {
            closeHistroyFile();
            countFile = createOrOpenHistoryFile(typeNames);
            isChangedHistoryPath = false;
        }
    }

    /** @return the historyPath */
    public String getHistoryPath() {
        return historyPath;
    }

    /** @param historyPath the historyPath to set */
    public void setHistoryPath(String historyPath) {
        this.historyPath = historyPath;
    }

    /** Close the opened file */
    public void closeHistroyFile() {
        if (countFile == null) {
            return;
        }
        try {
            countFile.close();
            countFile = null;
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /** @param interval the interval to set */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /** @param maxCount the maxCount to set */
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * Set isChangedHistoryPath
     *
     * @return the isChangedHistoryPath
     */
    public boolean isChangedHistoryPath() {
        return isChangedHistoryPath;
    }

    /** @param isChangedHistoryPath the isChangedHistoryPath to set */
    public void setChangedHistoryPath(boolean isChangedHistoryPath) {
        this.isChangedHistoryPath = isChangedHistoryPath;
    }
}
