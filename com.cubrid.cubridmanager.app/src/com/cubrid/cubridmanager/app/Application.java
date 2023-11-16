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
package com.cubrid.cubridmanager.app;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.dialog.SelectWorkspaceDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.spi.Version;

/**
 * This class controls all aspects of the application's execution
 *
 * @author pangqiren
 * @version 1.0 - 2009-12-23 created by pangqiren
 */
public class Application implements IApplication {
	public Object start(IApplicationContext context) throws Exception {
		ApplicationUtil.setApplicationType(ApplicationType.CUBRID_MANAGER);
		String applicationType = ApplicationType.CUBRID_MANAGER.getShortName();
		Display display = null;

		try {
			display = PlatformUI.createDisplay();
			Shell shell = CommonUITool.getSplashShell(display);
			if (!CommonUITool.jreVersionCheck()) {
				CommonUITool.openErrorBox(shell, com.cubrid.common.ui.spi.Messages.unsupportedJRE);
				context.applicationRunning();
				return IApplication.EXIT_OK;
			}

			if (Version.buildVersionId.indexOf("NLS missing message") == 0) {
				Version.buildVersionId = Version.releaseVersion;
			}
			
			if (!SelectWorkspaceDialog.pickWorkspaceDir(shell, applicationType, Version.buildVersionId)) {
				context.applicationRunning();
				return IApplication.EXIT_OK;
			}

			String workspace = SelectWorkspaceDialog.getLastSetWorkspaceDirectory();
			LogUtil.configLogger(null, workspace);

			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}

			return IApplication.EXIT_OK;
		} finally {
			if (display != null) {
				display.dispose();
			}
			Location instanceLoc = Platform.getInstanceLocation();
			if (instanceLoc != null) {
				instanceLoc.release();
			}
		}
	}

	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return;
		}

		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed()) {
					workbench.close();
				}
			}
		});
	}
}
