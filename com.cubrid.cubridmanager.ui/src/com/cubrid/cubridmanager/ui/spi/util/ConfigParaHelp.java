/*
 * Copyright (C) 2008 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.spi.util;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.broker.task.SetBrokerConfParameterTask;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.GetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.GetHAConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCMConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetHAConfParameterTask;
import com.cubrid.cubridmanager.ui.common.Messages;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;

/**
 * The help type
 *
 * @author lizhiqiang
 * @version 1.0 - 2011-3-29 created by lizhiqiang
 */
public final class ConfigParaHelp {

    private static final Logger LOGGER = LogUtil.getLogger(ConfigParaHelp.class);

    /** private constructor */
    private ConfigParaHelp() {
        // empty
    }

    /**
     * Perform the import task for cubrid.conf
     *
     * @param serverInfo the CubridServer
     * @param confParamList the List<String>
     */
    public static void performImportCubridConf(ServerInfo serverInfo, List<String> confParamList) {
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setCubridParameterTaskName);
        SetCubridConfParameterTask task = new SetCubridConfParameterTask(serverInfo);
        task.setConfContents(confParamList);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(
                    Messages.titleSuccess, Messages.msgChangeServerParaSuccess);
        }
    }

    /**
     * Perform the import task for cubrid.conf
     *
     * @param serverInfo the CubridServer
     * @param contents the String
     */
    public static void performImportCubridConf(ServerInfo serverInfo, String contents) {
        if (contents == null) {
            return;
        }
        String[] lines = contents.split(System.getProperty("line.separator"));
        performImportCubridConf(serverInfo, Arrays.asList(lines));
    }

    /**
     * Perform the import task for cm.conf
     *
     * @param serverInfo the CubridServer
     * @param confParamList the List<String>
     */
    public static void performImportCmConf(ServerInfo serverInfo, List<String> confParamList) {
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setCMParameterTaskName);
        SetCMConfParameterTask task = new SetCMConfParameterTask(serverInfo);
        task.setConfContents(confParamList);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(Messages.titleSuccess, Messages.msgChangeCMParaSuccess);
        }
    }

    /**
     * Perform the import task for cm.conf
     *
     * @param serverInfo the CubridServer
     * @param contents the String
     */
    public static void performImportCmConf(ServerInfo serverInfo, String contents) {
        if (contents == null) {
            return;
        }
        String[] lines = contents.split(System.getProperty("line.separator"));
        performImportCmConf(serverInfo, Arrays.asList(lines));
    }

    /**
     * Perform the import task for cubrid_broker.conf
     *
     * @param serverInfo the CubridserverInfo
     * @param confParamList the List<String>
     */
    public static void performImportBrokerConf(ServerInfo serverInfo, List<String> confParamList) {
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setBrokerConfParametersTaskName);
        SetBrokerConfParameterTask task = new SetBrokerConfParameterTask(serverInfo);
        task.setConfContents(confParamList);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(Messages.titleSuccess, Messages.restartBrokerMsg);
            task.finish();
        }
    }

    /**
     * Perform the import task for cubrid_broker.conf
     *
     * @param serverInfo the CubridServer
     * @param contents the String
     */
    public static void performImportBrokerConf(ServerInfo serverInfo, String contents) {
        if (contents == null) {
            return;
        }
        String[] lines = contents.split(System.getProperty("line.separator"));
        performImportBrokerConf(serverInfo, Arrays.asList(lines));
    }

    /**
     * Perform the import task for ha.conf
     *
     * @param serverInfo the CubridServer
     * @param confParamList the List<String>
     */
    public static void performImportHAConf(ServerInfo serverInfo, List<String> confParamList) {
        CommonTaskExec taskExcutor = new CommonTaskExec(Messages.setHAConfTaskName);
        SetHAConfParameterTask task = new SetHAConfParameterTask(serverInfo);
        task.setConfContents(confParamList);
        taskExcutor.addTask(task);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            CommonUITool.openInformationBox(Messages.titleSuccess, Messages.msgSetHAConfSuccess);
        }
    }

    /**
     * Perform the import task for cubrid_broker.conf
     *
     * @param serverInfo the CubridServer
     * @param contents the String
     */
    public static void performImportHAConf(ServerInfo serverInfo, String contents) {
        if (contents == null) {
            return;
        }
        String[] lines = contents.split(System.getProperty("line.separator"));
        performImportHAConf(serverInfo, Arrays.asList(lines));
    }

    /**
     * Perform the task of get cm.conf
     *
     * @param serverInfo the CubridServer
     * @return the List<String>
     */
    public static List<String> performGetCmConf(ServerInfo serverInfo) {
        List<String> confContent = new ArrayList<String>();
        CommonTaskExec taskExcutor =
                new CommonTaskExec(com.cubrid.cubridmanager.ui.host.Messages.getCmConfTaskRunning);
        GetCMConfParameterTask getCMConfParameterTask = new GetCMConfParameterTask(serverInfo);
        taskExcutor.addTask(getCMConfParameterTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            confContent = getCMConfParameterTask.getConfContents();
        }
        return confContent;
    }

    /**
     * Perform the task of get cubrid_broker.conf
     *
     * @param serverInfo the CubridServer
     * @return the List<String>
     */
    public static List<String> performGetBrokerConf(ServerInfo serverInfo) {
        List<String> confContent = new ArrayList<String>();
        CommonTaskExec taskExcutor =
                new CommonTaskExec(
                        com.cubrid.cubridmanager.ui.host.Messages.getBrokerConfTaskRunning);
        GetBrokerConfParameterTask getBrokerConfParameterTask =
                new GetBrokerConfParameterTask(serverInfo);
        taskExcutor.addTask(getBrokerConfParameterTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            confContent = getBrokerConfParameterTask.getConfContents();
        }
        return confContent;
    }

    /**
     * Perform the task of get cubrid.conf
     *
     * @param serverInfo the CubridServer
     * @return the List<String>
     */
    public static List<String> performGetCubridConf(ServerInfo serverInfo) {
        List<String> confContent = new ArrayList<String>();
        CommonTaskExec taskExcutor =
                new CommonTaskExec(
                        com.cubrid.cubridmanager.ui.host.Messages.getCubridConfTaskRunning);
        GetCubridConfParameterTask getCubridConfParameterTask =
                new GetCubridConfParameterTask(serverInfo);
        taskExcutor.addTask(getCubridConfParameterTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            confContent = getCubridConfParameterTask.getConfContents();
        }
        return confContent;
    }

    /**
     * Perform the task of get ha.conf
     *
     * @param serverInfo the CubridServer
     * @return the List<String>
     */
    public static List<String> performGetHAConf(ServerInfo serverInfo) {
        List<String> confContent = new ArrayList<String>();
        CommonTaskExec taskExcutor =
                new CommonTaskExec(com.cubrid.cubridmanager.ui.host.Messages.getHaConfTaskRunning);
        GetHAConfParameterTask getHAConfParameterTask = new GetHAConfParameterTask(serverInfo);
        taskExcutor.addTask(getHAConfParameterTask);
        new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
        if (taskExcutor.isSuccess()) {
            confContent = getHAConfParameterTask.getConfContents();
        }
        return confContent;
    }

    /**
     * Perform the export task of cubrid_broker.conf
     *
     * @param serverInfo the CubridServer
     * @param fileFullPath the String
     * @param fileCharset the String
     */
    public static void performExportBrokerConf(
            ServerInfo serverInfo, String fileFullPath, String fileCharset) {
        List<String> brokerConf = performGetBrokerConf(serverInfo);
        exportConf(brokerConf, fileFullPath, fileCharset);
    }

    /**
     * Perform the export task of cubrid.conf
     *
     * @param serverInfo the CubridServer
     * @param fileFullPath the String
     * @param fileCharset the String
     */
    public static void performExportCubridConf(
            ServerInfo serverInfo, String fileFullPath, String fileCharset) {
        List<String> cubridConf = performGetCubridConf(serverInfo);
        exportConf(cubridConf, fileFullPath, fileCharset);
    }

    /**
     * Perform the export task of cm.conf
     *
     * @param serverInfo the CubridServer
     * @param fileFullPath the String
     * @param fileCharset the String
     */
    public static void performExportCmConf(
            ServerInfo serverInfo, String fileFullPath, String fileCharset) {
        List<String> cmConf = performGetCmConf(serverInfo);
        exportConf(cmConf, fileFullPath, fileCharset);
    }

    /**
     * Perform the export task of ha.conf
     *
     * @param serverInfo the CubridServer
     * @param fileFullPath the String
     * @param fileCharset the String
     */
    public static void performExportHAConf(
            ServerInfo serverInfo, String fileFullPath, String fileCharset) {
        List<String> haConf = performGetHAConf(serverInfo);
        exportConf(haConf, fileFullPath, fileCharset);
    }

    /**
     * Method for export the given words to the give fileFullPath
     *
     * @param contents the List<String>
     * @param fileFullPath the String
     * @param fileCharset the String
     */
    public static void exportConf(List<String> contents, String fileFullPath, String fileCharset) {
        File file = new File(fileFullPath);
        FileOutputStream fos = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            fos = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fos, fileCharset);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            for (String line : contents) {
                bufferedWriter.write(line);
                bufferedWriter.write(System.getProperty("line.separator"));
            }
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage());
            CommonUITool.openErrorBox(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage());
            CommonUITool.openErrorBox(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            CommonUITool.openErrorBox(ex.getMessage());
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        }
    }
}
