/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.ServerManager;
import com.cubrid.cubridmanager.ui.broker.editor.internal.BrokerIntervalSettingManager;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.dispatcher.DataGeneratorPool;
import com.cubrid.cubridmanager.ui.spi.contribution.CubridWorkbenchContrItem;
import com.cubrid.cubridmanager.ui.spi.persist.CMHostNodePersistManager;

/**
 * Host utility class
 * 
 * @author pangqiren
 * @version 1.0 - 2011-3-2 created by pangqiren
 */
public final class HostUtils {
	private static final Logger LOGGER = LogUtil.getLogger(HostUtils.class);

	private HostUtils() {
	}

	/**
	 * Process the resource when disconnect host
	 * 
	 * @param server CubridServer
	 * @return boolean
	 */
	public static boolean processHostDisconnected(CubridServer server) {
		if (server == null) {
			LOGGER.debug("The server is a null.");
			return false;
		}

		final JobFamily jobFamily = new JobFamily();
		String serverName = server.getLabel();
		String dbName = JobFamily.ALL_DB;
		jobFamily.setServerName(serverName);
		jobFamily.setDbName(dbName);
		Job[] jobs = Job.getJobManager().find(jobFamily);

		boolean isHaveDashboard = DataGeneratorPool.getInstance().isHasConnection(
				server.getHostAddress(), server.getMonPort(), server.getUserName());

		// check whether have jobs and monitoring dashboard in this server
		if ((jobs != null && jobs.length > 0) || isHaveDashboard) {
			String msg = Messages.bind(Messages.msgConfirmDisconnectHostWithJob, serverName);
			boolean isDisconnectHost = CommonUITool.openConfirmBox(msg);
			if (!isDisconnectHost) {
				return false;
			}
		}

		// check the query editor in this server
		if (!LayoutUtil.checkAllQueryEditor(server)) {
			return false;
		}

		boolean isSaved = server.getServerInfo().isConnected();
		boolean isCloseAll = CubridWorkbenchContrItem.closeAllEditorAndViewInServer(server, isSaved);
		if (!isCloseAll) {
			return false;
		}

		cancelJob(jobFamily);

		server.getLoader().setLoaded(false);
		server.removeAllChild();
		ServerManager.getInstance().setConnected(
				server.getHostAddress(),
				server.getMonPort() == null ? 0 : Integer.parseInt(server.getMonPort()),
				server.getUserName(), false);
		if (!server.isAutoSavePassword()) {
			server.getServerInfo().setUserPassword("");
		}

		return true;
	}

	/**
	 * Process resource when delete host
	 * 
	 * @param server CubridServer
	 * @return boolean
	 */
	public static boolean processHostDeleted(CubridServer server) {
		if (server == null) {
			LOGGER.debug("The server is a null.");
			return false;
		}

		final JobFamily jobFamily = new JobFamily();
		String serverName = server.getLabel();
		String dbName = JobFamily.ALL_DB;
		jobFamily.setServerName(serverName);
		jobFamily.setDbName(dbName);
		Job[] jobs = Job.getJobManager().find(jobFamily);

		boolean isHaveDashboard = DataGeneratorPool.getInstance().isHasConnection(
				server.getHostAddress(), server.getMonPort(), server.getUserName());

		// check whether have jobs and monitoring dashboard in this server
		if ((jobs != null && jobs.length > 0) || isHaveDashboard) {
			String msg = Messages.bind(Messages.msgConfirmDeleteHostWithJob, serverName);
			boolean isDisconnectHost = CommonUITool.openConfirmBox(msg);
			if (!isDisconnectHost) {
				return false;
			}
		}

		// check the query editor in this server
		if (!LayoutUtil.checkAllQueryEditor(server)) {
			return false;
		}

		boolean isSaved = server.getServerInfo().isConnected();
		boolean isCloseAll = CubridWorkbenchContrItem.closeAllEditorAndViewInServer(server, isSaved);
		if (!isCloseAll) {
			return false;
		}

		cancelJob(jobFamily);

		QueryOptions.removePref(server.getServerInfo());
		BrokerIntervalSettingManager.getInstance().removeAllBrokerIntervalSettingInServer(server.getLabel());
		CMHostNodePersistManager.getInstance().removeServer(server);

		return true;
	}

	/**
	 * Cancel all jobs that belong to the job family
	 * 
	 * @param jobFamily the JobFamily
	 */
	private static void cancelJob(final JobFamily jobFamily) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					Job.getJobManager().cancel(jobFamily);
				} catch (Exception ignored) {
					LOGGER.error(ignored.getMessage(), ignored);
				}
			}
		});
	}
}
