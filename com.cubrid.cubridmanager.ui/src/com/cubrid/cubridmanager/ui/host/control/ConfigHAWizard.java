/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.host.control;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.task.SetCubridConfParameterTask;
import com.cubrid.cubridmanager.core.common.task.SetHAConfParameterTask;
import com.cubrid.cubridmanager.ui.host.Messages;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.wizard.Wizard;

/**
 * HA Wizard
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-11-27 created by Kevin.Wang
 */
public class ConfigHAWizard extends Wizard {
    private HAModel haModel;
    private SettingHostPage settingHostPage;
    private SettingCubridConfPage settingCubridConfPage;
    private SettingCubridHAConfPage settingCubridHAConfPage;
    private SettingHAConfirmPage settingHAConfirmPage;
    private SetCubridConfParameterTask setCubridConfParameterTaskMaster;
    private SetCubridConfParameterTask setCubridConfParameterTaskSlave;
    private SetHAConfParameterTask setHAConfParameterTaskMaster;
    private SetHAConfParameterTask setHAConfParameterTaskSlave;

    public ConfigHAWizard(CubridServer cubridServer) {
        HAServer masterServer = new HAServer(cubridServer);
        haModel = new HAModel();
        haModel.setMasterServer(masterServer);
        setWindowTitle(Messages.titleHAWizard);
    }

    public void addPages() {
        settingHostPage = new SettingHostPage();
        addPage(settingHostPage);

        settingCubridConfPage = new SettingCubridConfPage();
        addPage(settingCubridConfPage);

        settingCubridHAConfPage = new SettingCubridHAConfPage();
        addPage(settingCubridHAConfPage);

        settingHAConfirmPage = new SettingHAConfirmPage();
        addPage(settingHAConfirmPage);
    }

    public boolean performCancel() {
        return CommonUITool.openConfirmBox(Messages.msgConfirmExitHAWizard);
    }

    public boolean canFinish() {
        return getContainer().getCurrentPage() instanceof SettingHAConfirmPage;
    }

    public boolean performFinish() {
        ServerInfo masterServerInfo = haModel.getMasterServer().getServer().getServerInfo();
        ServerInfo slaveServerInfo = haModel.getSlaveServer().getServer().getServerInfo();
        Map<String, Map<String, String>> masterParams =
                haModel.getMasterServer().getCubridParameters();
        Map<String, Map<String, String>> slaveParams =
                haModel.getSlaveServer().getCubridParameters();
        Map<String, Map<String, String>> masterHaParams =
                haModel.getMasterServer().getCubridHAParameters();
        Map<String, Map<String, String>> slaveHaParams =
                haModel.getSlaveServer().getCubridHAParameters();

        setCubridConfParameterTaskMaster = new SetCubridConfParameterTask(masterServerInfo);
        setCubridConfParameterTaskMaster.setConfParameters(masterParams);
        setHAConfParameterTaskMaster = new SetHAConfParameterTask(masterServerInfo);
        setHAConfParameterTaskMaster.setConfParameters(masterHaParams);

        setCubridConfParameterTaskSlave = new SetCubridConfParameterTask(slaveServerInfo);
        setCubridConfParameterTaskSlave.setConfParameters(slaveParams);
        setHAConfParameterTaskSlave = new SetHAConfParameterTask(slaveServerInfo);
        setHAConfParameterTaskSlave.setConfParameters(slaveHaParams);

        CommonTaskExec taskExec = new CommonTaskExec(Messages.msgUploading);
        taskExec.addTask(setCubridConfParameterTaskMaster);
        taskExec.addTask(setCubridConfParameterTaskSlave);
        taskExec.addTask(setHAConfParameterTaskMaster);
        taskExec.addTask(setHAConfParameterTaskSlave);

        new ExecTaskWithProgress(taskExec).exec();
        if (taskExec.isSuccess()) {
            StartHAServiceDialog dialog = new StartHAServiceDialog(getShell(), haModel);
            dialog.open();
            return true;
        } else {
            CommonUITool.openErrorBox(getErrMsg());
            return false;
        }
    }

    private String getErrMsg() {
        StringBuilder sb = new StringBuilder();

        if (!setCubridConfParameterTaskMaster.isSuccess()) {
            String errorMsg = setCubridConfParameterTaskMaster.getErrorMsg();
            String serverName = haModel.getMasterServer().getServer().getServerName();
            sb.append(errorMsg).append("@").append(serverName).append(StringUtil.NEWLINE);
        }

        if (!setCubridConfParameterTaskSlave.isSuccess()) {
            String errorMsg = setCubridConfParameterTaskSlave.getErrorMsg();
            String serverName = haModel.getSlaveServer().getServer().getServerName();
            sb.append(errorMsg).append("@").append(serverName).append(StringUtil.NEWLINE);
        }

        if (!setHAConfParameterTaskMaster.isSuccess()) {
            String errorMsg = setHAConfParameterTaskMaster.getErrorMsg();
            String serverName = haModel.getMasterServer().getServer().getServerName();
            sb.append(errorMsg).append("@").append(serverName).append(StringUtil.NEWLINE);
        }

        if (!setHAConfParameterTaskSlave.isSuccess()) {
            String errorMsg = setHAConfParameterTaskSlave.getErrorMsg();
            String serverName = haModel.getSlaveServer().getServer().getServerName();
            sb.append(errorMsg).append("@").append(serverName).append(StringUtil.NEWLINE);
        }

        return sb.toString();
    }

    /**
     * Clone a config parameters map
     *
     * @param parameters
     * @return Map<String,Map<String,String>>
     */
    public Map<String, Map<String, String>> cloneParameters(
            Map<String, Map<String, String>> parameters) {
        if (parameters == null) {
            return null;
        }

        Map<String, Map<String, String>> cloneObj = new HashMap<String, Map<String, String>>();
        for (String key : parameters.keySet()) {
            Map<String, String> data = parameters.get(key);
            if (data == null) {
                cloneObj.put(key, null);
                continue;
            }

            Map<String, String> cloneData = new HashMap<String, String>();
            for (String p : data.keySet()) {
                cloneData.put(p, data.get(p));
            }
            cloneObj.put(key, cloneData);
        }

        return cloneObj;
    }

    public HAModel getHaModel() {
        return haModel;
    }
}
