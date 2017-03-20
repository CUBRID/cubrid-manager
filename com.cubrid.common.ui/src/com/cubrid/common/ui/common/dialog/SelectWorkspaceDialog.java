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
package com.cubrid.common.ui.common.dialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.Util;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.action.SwitchWorkspaceAction;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * 
 * Select the workspace dialog
 * 
 * @author pangqiren
 * @version 1.0 - 2011-8-31 created by pangqiren
 */
public class SelectWorkspaceDialog extends
		CMTitleAreaDialog {

	// you would probably normally define these somewhere in your Preference Constants
	private static final String KEY_LAST_WORKSPACE = "LAST_WORKSPACE";
	private static final String KEY_NOT_SHOW_WORKSPACE_SELECTION_DIALOG = "NOT_SHOW_WORKSPACE_SELECTION_DIALOG";
	private static final String KEY_RECENT_WORKSPACES = "RECENT_WORKSPACES";
	// used as separator when we save the last used workspace locations
	private static final String WORKSPACE_SPLIT_CHAR = ";";
	// max number of entries in the history box
	private static final int MAX_HISTORY = 20;

	// this are our preferences we will be using as the IPreferenceStore is not available yet
	private static final IEclipsePreferences PREFERENCES = PersistUtils.getGlobalPreference(CommonUIPlugin.PLUGIN_ID);

	// our controls
	private Combo workspacePathCombo;
	private List<String> recentUsedWorkspaces;
	private Button rememberWorkspaceButton;

	private final boolean isSwitchWorkspace;
	private final String productName;
	private final String productVersion;

	/**
	 * The constructor
	 * 
	 * @param parentShell
	 * @param isSwitchWorkspace
	 */
	public SelectWorkspaceDialog(Shell parentShell, boolean isSwitchWorkspace,
			String productName, String productVersion) {
		super(parentShell);
		this.isSwitchWorkspace = isSwitchWorkspace;
		this.productName = productName;
		this.productVersion = productVersion;
		setHelpAvailable(false);
	}

	/**
	 * Create the dialog area
	 * 
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent) {

		Composite parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		CLabel label = new CLabel(composite, SWT.NONE);
		label.setText(Messages.lblWorkspace);
		label.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

		workspacePathCombo = new Combo(composite, SWT.BORDER);
		workspacePathCombo.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 1, 1, -1, -1));

		// fill in the all workspaces to combo
		String lastUsed = PREFERENCES.get(KEY_RECENT_WORKSPACES, null);
		recentUsedWorkspaces = new ArrayList<String>();
		if (lastUsed != null) {
			String[] all = lastUsed.split(WORKSPACE_SPLIT_CHAR);
			for (String str : all) {
				recentUsedWorkspaces.add(str);
			}
		}
		for (String last : recentUsedWorkspaces) {
			workspacePathCombo.add(last);
		}

		String rootDir = PREFERENCES.get(KEY_LAST_WORKSPACE, null);
		if (rootDir == null || rootDir.length() == 0) {
			rootDir = getSuggestedWorkspacePath();
		}
		workspacePathCombo.setText(rootDir == null ? "" : rootDir);

		Button btnBrowse = new Button(composite, SWT.PUSH);
		btnBrowse.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
		btnBrowse.setText(Messages.btnBrowse);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(Messages.titleSelectWorkspaceDialog);
				dialog.setMessage(Messages.msgSelectWorksapce);
				String text = workspacePathCombo.getText();
				File file = new File(text);
				while (file != null && !file.exists()) {
					file = file.getParentFile();
				}
				if (file != null) {
					text = file.getAbsolutePath();
				}
				dialog.setFilterPath(text);
				String path = dialog.open();
				if (path == null && workspacePathCombo.getText().length() == 0) {
					setErrorMessage(Messages.errNoSelectWorkspace);
				} else if (path != null) {
					setErrorMessage(null);
					workspacePathCombo.setText(path);
				}

			}
		});

		label = new CLabel(composite, SWT.NONE);
		label.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL,
				3, 1, -1, -1));
		label.setVisible(false);

		// checkbox below
		rememberWorkspaceButton = new Button(composite, SWT.CHECK);
		rememberWorkspaceButton.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
		rememberWorkspaceButton.setText(Messages.btnAsDefault);
		rememberWorkspaceButton.setSelection(PREFERENCES.getBoolean(
				KEY_NOT_SHOW_WORKSPACE_SELECTION_DIALOG, false));

		if (isSwitchWorkspace) {
			setTitle(Messages.titleSwitchWorkspaceDialog);
		} else {
			setTitle(Messages.titleSelectWorkspaceDialog);
		}
		setMessage(Messages.msgSelectWorkspaceDialog);

		return parentComp;

	}

	/**
	 * Configure the shell
	 * 
	 * @param newShell Shell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (isSwitchWorkspace) {
			newShell.setText(Messages.titleSwitchWorkspaceDialog);
		} else {
			newShell.setText(Messages.titleSelectWorkspaceDialog);
		}
	}

	/**
	 * 
	 * Get suggested workspace path
	 * 
	 * @return String
	 */
	private String getSuggestedWorkspacePath() {
		String workspacePath;
		if (Util.isMac()) {
			File file = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "CUBRIDManager");
			workspacePath = file.getAbsolutePath();
		} else {
			Location installLoc = Platform.getInstallLocation();
			URL url = installLoc.getURL();
			File file = new File(url.getFile());
			workspacePath = file.getAbsolutePath() + File.separator + "workspace";
		}
		return workspacePath;
	}

	/**
	 * Create the buttons in button bar
	 * 
	 * @param parent Composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Press ok
	 */
	protected void okPressed() {
		String workspacePath = workspacePathCombo.getText();

		if (workspacePath.length() == 0) {
			setErrorMessage(Messages.errNoSelectWorkspace);
			return;
		}
		if (isSwitchWorkspace
				&& workspacePath.equals(getLastSetWorkspaceDirectory())) {
			setErrorMessage(Messages.errWorkspaceUsed);
			return;
		}
		//now create
		String error = checkWorkspaceDirectory(workspacePath, true,
				productName, productVersion);
		if (error != null) {
			setErrorMessage(error);
			return;
		}

		if (!isSwitchWorkspace) {
			boolean isOk = false;
			try {
				Location instanceLoc = Platform.getInstanceLocation();
				isOk = instanceLoc.set(new URL("file", null, workspacePath),
						true);
			} catch (IllegalStateException e) {
				isOk = false;
			} catch (MalformedURLException e) {
				isOk = false;
			} catch (IOException e) {
				isOk = false;
			}
			if (!isOk) {
				setErrorMessage(Messages.errWorkspaceUsed);
				return;
			}
		}

		recentUsedWorkspaces.remove(workspacePath);
		if (!recentUsedWorkspaces.contains(workspacePath)) {
			recentUsedWorkspaces.add(0, workspacePath);
		}

		// deal with the max history
		if (recentUsedWorkspaces.size() > MAX_HISTORY) {
			List<String> remove = new ArrayList<String>();
			for (int i = MAX_HISTORY; i < recentUsedWorkspaces.size(); i++) {
				remove.add(recentUsedWorkspaces.get(i));
			}
			recentUsedWorkspaces.removeAll(remove);
		}

		// create a string concatenation of all our last used workspaces
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < recentUsedWorkspaces.size(); i++) {
			buf.append(recentUsedWorkspaces.get(i));
			if (i != recentUsedWorkspaces.size() - 1) {
				buf.append(WORKSPACE_SPLIT_CHAR);
			}
		}

		// save them into our preferences
		PREFERENCES.putBoolean(KEY_NOT_SHOW_WORKSPACE_SELECTION_DIALOG,
				rememberWorkspaceButton.getSelection());
		PREFERENCES.put(KEY_RECENT_WORKSPACES, buf.toString());
		PREFERENCES.put(KEY_LAST_WORKSPACE, workspacePath);
		try {
			PREFERENCES.flush();
		} catch (BackingStoreException e) { //NOPMD
			//ignore
		}
		super.okPressed();
	}

	/**
	 * 
	 * Check the workspace
	 * 
	 * @param workspaceLocation String
	 * @param isCreate boolean
	 * @param productName String
	 * @param productVersion String
	 * @return error message
	 */
	private static String checkWorkspaceDirectory(String workspaceLocation,
			boolean isCreate, String productName, String productVersion) {
		File workspaceFile = new File(workspaceLocation);
		if (!workspaceFile.exists() && isCreate) {
			boolean isOk = workspaceFile.mkdirs();
			if (!isOk) {
				return Messages.errDirNoExist;
			}
		}

		File identifierFile = new File(workspaceLocation + File.separator + "."
				+ productName);
		if (isCreate && !identifierFile.exists()) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(identifierFile));
				writer.write(productVersion);
			} catch (IOException e) {
				return Messages.errInvalidWorkspace;
			} finally {
				if (writer != null) {
					try {
						writer.close();
						writer = null;
					} catch (IOException e) {
						writer = null;
					}
				}
			}
		}

		if (!workspaceFile.isDirectory()) {
			return Messages.errNoDir;
		}

		if (!workspaceFile.canRead()) {
			return Messages.errNoReadable;
		}

		if (!workspaceFile.canWrite()) {
			return Messages.errNoWritable;
		}

		if (!identifierFile.exists()) {
			return Messages.errInvalidWorkspace;
		}
		String name = identifierFile.getName();
		if (!name.equals("." + productName)) {
			return Messages.errInvalidWorkspace;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(identifierFile));
			String version = reader.readLine();
			int ret = CompatibleUtil.compareVersion(
					productVersion, version == null ? null : version.trim());
			if (ret < 0) {
				return Messages.errNoSupported;
			}
		} catch (IOException ex) {
			return Messages.errInvalidWorkspace;
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					reader = null;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Pick up the workspace directory
	 * 
	 * @param shell Shell
	 * @param productName String
	 * @param productVersion String
	 * @return boolean
	 */
	public static boolean pickWorkspaceDir(Shell shell, String productName,
			String productVersion) {

		//check whether switch workspace and do not need to pop up dialog for select workspace
		boolean isSwitchWorkspace = "true".equals(PersistUtils.getGlobalPreferenceValue(
				CommonUIPlugin.PLUGIN_ID,
				SwitchWorkspaceAction.KEY_IS_SWITCH_WORKSPACE));

		Location instanceLoc = Platform.getInstanceLocation();
		String lastUsedWs = getLastSetWorkspaceDirectory();

		boolean isRemember = isRememberWorkspace();
		if (isRemember && (lastUsedWs == null || lastUsedWs.length() == 0)) {
			isRemember = false;
			isSwitchWorkspace = false;
		}
		if (isRemember) {
			String ret = SelectWorkspaceDialog.checkWorkspaceDirectory(
					lastUsedWs, false, productName, productVersion);
			if (ret != null) {
				CommonUITool.openErrorBox(shell, ret);
				isRemember = false;
				isSwitchWorkspace = false;
			}
		}

		String workspaceLocation = null;
		if (isRemember || isSwitchWorkspace) {
			boolean isOk = false;
			workspaceLocation = lastUsedWs;
			try {
				isOk = instanceLoc.set(
						new URL("file", null, workspaceLocation), true);
			} catch (IllegalStateException e) {
				isOk = false;
			} catch (MalformedURLException e) {
				isOk = false;
			} catch (IOException e) {
				isOk = false;
			}
			PersistUtils.setGlobalPreferenceValue(CommonUIPlugin.PLUGIN_ID,
					SwitchWorkspaceAction.KEY_IS_SWITCH_WORKSPACE, "false");
			if (isOk) {
				return true;
			} else {
				CommonUITool.openErrorBox(shell, Messages.errWorkspaceUsed);
			}
		}

		SelectWorkspaceDialog pwDialog = new SelectWorkspaceDialog(shell,
				false, productName, productVersion);
		int ret = pwDialog.open();
		if (ret != IDialogConstants.OK_ID) {
			CommonUITool.openErrorBox(shell, Messages.errNoWorkspace);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * Return whether remember workspace
	 * 
	 * @return boolean
	 */
	public static boolean isRememberWorkspace() {
		return PREFERENCES.getBoolean(KEY_NOT_SHOW_WORKSPACE_SELECTION_DIALOG,
				false);
	}

	/**
	 * 
	 * Get the last used workspace
	 * 
	 * @return String
	 */
	public static String getLastSetWorkspaceDirectory() {
		return PREFERENCES.get(KEY_LAST_WORKSPACE, null);
	}

	/**
	 * 
	 * Set the last used workspace
	 * 
	 * @param workspacePath String
	 */
	public static void setLastSetWorkspaceDirectory(String workspacePath) {
		String lastUsed = PREFERENCES.get(KEY_RECENT_WORKSPACES, null);
		List<String> lastUsedWorkspaces = new ArrayList<String>();
		if (lastUsed != null) {
			String[] all = lastUsed.split(WORKSPACE_SPLIT_CHAR);
			for (String str : all) {
				lastUsedWorkspaces.add(str);
			}
		}
		lastUsedWorkspaces.remove(workspacePath);
		if (!lastUsedWorkspaces.contains(workspacePath)) {
			lastUsedWorkspaces.add(0, workspacePath);
		}

		// deal with the max history
		if (lastUsedWorkspaces.size() > MAX_HISTORY) {
			List<String> remove = new ArrayList<String>();
			for (int i = MAX_HISTORY; i < lastUsedWorkspaces.size(); i++) {
				remove.add(lastUsedWorkspaces.get(i));
			}
			lastUsedWorkspaces.removeAll(remove);
		}

		// create a string concatenation of all our last used workspaces
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < lastUsedWorkspaces.size(); i++) {
			buf.append(lastUsedWorkspaces.get(i));
			if (i != lastUsedWorkspaces.size() - 1) {
				buf.append(WORKSPACE_SPLIT_CHAR);
			}
		}
		PREFERENCES.put(KEY_LAST_WORKSPACE, workspacePath);
		PREFERENCES.put(KEY_RECENT_WORKSPACES, buf.toString());
		try {
			PREFERENCES.flush();
		} catch (BackingStoreException e) { //NOPMD
			//ignore
		}
	}

	/**
	 * 
	 * Get workspace menu
	 * 
	 * @param productName String
	 * @param productVersion String
	 * @return MenuManager
	 */
	public static MenuManager getWorkspaceMenu(String productName,
			String productVersion) {
		MenuManager workspaceMenu = new MenuManager(
				Messages.menuSwitchWorkspace);
		String lastUsed = PREFERENCES.get(KEY_LAST_WORKSPACE, null);
		String recentUsed = PREFERENCES.get(KEY_RECENT_WORKSPACES, null);
		if (recentUsed != null) {
			String[] all = recentUsed.split(WORKSPACE_SPLIT_CHAR);
			for (String str : all) {
				if (str.equals(lastUsed)) {
					continue;
				}
				SwitchWorkspaceAction action = new SwitchWorkspaceAction(str,
						productName, productVersion, false);
				workspaceMenu.add(action);
			}
		}
		workspaceMenu.add(new Separator());
		workspaceMenu.add(new SwitchWorkspaceAction(Messages.menuOther,
				productName, productVersion, true));
		return workspaceMenu;
	}

}
