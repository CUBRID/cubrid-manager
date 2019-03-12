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
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.novocode.naf.swt.custom.Hyperlink;

/**
 * Database general information for creating database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class GeneralInfoPage extends
		WizardPage implements
		ModifyListener {

	public final static String PAGENAME = "CreateDatabaseWizard/GeneralInfoPage";

	private Text databaseNameText;
	private Combo pageSizeCombo;
	private Text genericVolumePathText;
	private Text logVolumePathText;
	private final CubridServer server;
	private String databasePath = "";
	private Text genericVolumeSizeText;
	private Text logVolumeSizeText;
	private Combo logPageSizeCombo;
	private Button autoStartButton;
	private String defaultDatVolumeSize = null;
	private Combo charsetCombo;

	private Button enIsoRadio;
	private Button enUtfRadio;
	private Button koEuckrRadio;
	private Button koUtfRadio;
	private Button userDefinedRadio;
	private Text userDefinedCharsetText;
	private final String CHARSET_EN_US_ISO88591 = "en_US.iso88591";
	private final String CHARSET_EN_US_UTF8 = "en_US.utf8";
	private final String CHARSET_KO_KR_EUCKR = "ko_KR.euckr";
	private final String CHARSET_KO_KR_UTF8 = "ko_KR.utf8";

	private final String DEFAULT = "Default";
	private String[] charsetItem = new String[]{DEFAULT, "en_US", "ko_KR", "tr_TR" , "zh_CN" , "de_DE" ,
			"es_ES" , "fr_FR" , "it_IT" , "km_KH" , "ja_JP" , "vi_VN"};


	public GeneralInfoPage(CubridServer server) {
		super(PAGENAME);
		this.server = server;
		setPageComplete(false);
	}

	/**
	 * Create the control for this page
	 *
	 * @param parent the parent composite
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		createDatabseNameGroup(composite);
		//9.2 support create DB with specified charset
		if (CompatibleUtil.isSupportCreateDBByCharset(server.getServerInfo())) {
			createChartsetGroup(composite);
		}
		createGenericVolumeGroup(composite);
		createLogVolumeGroup(composite);
		createAutoStartGroup(composite);

		initial();
		setTitle(Messages.titleWizardPageGeneral);
		setMessage(Messages.msgWizardPageGeneral);

		setControl(composite);
	}

	/**
	 * Create database name group
	 *
	 * @param parent the parent composite
	 */
	private void createDatabseNameGroup(Composite parent) {
		Group generalGroup = new Group(parent, SWT.NONE);
		generalGroup.setText(Messages.grpGeneralInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		generalGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		generalGroup.setLayout(layout);

		Label databaseNameLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
		databaseNameLabel.setText(Messages.lblDbName);
		gridData = new GridData();
		gridData.widthHint = 150;
		databaseNameLabel.setLayoutData(gridData);

		databaseNameText = new Text(generalGroup, SWT.BORDER);
		databaseNameText.setTextLimit(ValidateUtil.MAX_DB_NAME_LENGTH);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		databaseNameText.setLayoutData(gridData);

		Label pageSizeLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
		pageSizeLabel.setText(Messages.lblPageSize);
		gridData = new GridData();
		gridData.widthHint = 150;
		pageSizeLabel.setLayoutData(gridData);

		pageSizeCombo = new Combo(generalGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		pageSizeCombo.setLayoutData(gridData);
		pageSizeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				valid();
			}
		});

		//now 9.1 not support different charset database
		/*if (CompatibleUtil.isSupportCreateDBByCharset(server.getServerInfo())) {
			Label charsetLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
			charsetLabel.setText(Messages.lblLocale);
			gridData = new GridData();
			gridData.widthHint = 150;
			pageSizeLabel.setLayoutData(gridData);

			charsetCombo = new Combo(generalGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			gridData = new GridData();
			gridData.horizontalSpan = 1;
			gridData.widthHint = 50;
			charsetCombo.setLayoutData(gridData);
			charsetCombo.setEnabled(false);

			Label charsetMemoLabel = new Label(generalGroup, SWT.LEFT | SWT.WRAP);
			charsetMemoLabel.setText(Messages.msgLocaleNotice);
			gridData = new GridData();
			gridData.horizontalSpan = 2;
			gridData.widthHint = 295;
			charsetMemoLabel.setLayoutData(gridData);

			new Label(generalGroup, SWT.LEFT | SWT.WRAP);
			new Label(generalGroup, SWT.LEFT | SWT.WRAP);

			Hyperlink link = new Hyperlink(generalGroup, SWT.None);
			link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			link.setText(Messages.msgLocaleManual);
			gridData = new GridData();
			gridData.horizontalSpan = 2;
			link.setLayoutData(gridData);
			link.addMouseListener(new MouseAdapter() {
				public void mouseUp(MouseEvent e) {
					IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					try {
					    IWebBrowser br = browserSupport.createBrowser(null);
					    br.openURL(new URL(Messages.msgLocaleManualUrl));
					} catch (Exception ignored) {
					}
				}
			});
		}*/
	}

	/**
	 * Create chartset information group
	 *
	 * @param parent the parent composite
	 */
	private void createChartsetGroup(Composite parent){
		Group charsetGroup = new Group(parent, SWT.NONE);
		charsetGroup.setText(Messages.grpCharsetInfo);
		GridData gdCharsetGroup = new GridData(GridData.FILL_HORIZONTAL);
		charsetGroup.setLayoutData(gdCharsetGroup);
		GridLayout layoutCharsetGroup = new GridLayout();
		layoutCharsetGroup.numColumns = 3;
		charsetGroup.setLayout(layoutCharsetGroup);


		enIsoRadio = new Button(charsetGroup, SWT.RADIO);
		final GridData gdEnIsoRadio = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		enIsoRadio.setLayoutData(gdEnIsoRadio);
		enIsoRadio.setText(CHARSET_EN_US_ISO88591);

		enUtfRadio = new Button(charsetGroup, SWT.RADIO);
		final GridData gdEnUtfRadio = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		enUtfRadio.setLayoutData(gdEnUtfRadio);
		enUtfRadio.setText(CHARSET_EN_US_UTF8);

		koEuckrRadio = new Button(charsetGroup, SWT.RADIO);
		final GridData gdKoEuckrRadio = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		koEuckrRadio.setLayoutData(gdKoEuckrRadio);
		koEuckrRadio.setText(CHARSET_KO_KR_EUCKR);

		koUtfRadio = new Button(charsetGroup, SWT.RADIO);
		final GridData gdKoUtfRadio = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		koUtfRadio.setLayoutData(gdKoUtfRadio);
		koUtfRadio.setText(CHARSET_KO_KR_UTF8);
		
		userDefinedRadio = new Button(charsetGroup, SWT.RADIO);
		final GridData gdUserDefinedRadio = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		koUtfRadio.setLayoutData(gdUserDefinedRadio);
		userDefinedRadio.setText(Messages.lblUserDefinedCharset);
		userDefinedRadio.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.widget;
				userDefinedCharsetText.setEnabled(btn.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		userDefinedCharsetText = new Text(charsetGroup, SWT.BORDER);
		final GridData gdDetailCombo = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		userDefinedCharsetText.setLayoutData(gdDetailCombo);
		userDefinedCharsetText.setEnabled(false);
		userDefinedCharsetText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				//TODO whether need check the input style?
				valid();
			}
		});

		Hyperlink link = new Hyperlink(charsetGroup, SWT.None);
		GridData gdLink = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdLink.horizontalSpan = 3;
		link.setLayoutData(gdLink);
		link.setText(Messages.msgLocaleManual);
		link.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				try {
					IWebBrowser br = browserSupport.createBrowser(null);
					String url = Messages.msgLocaleManualUrl91;
					if (CompatibleUtil.isAfter930(server.getServerInfo())) {
						url = Messages.msgLocaleManualUrl93;
					} else if (CompatibleUtil.isAfter920(server.getServerInfo())) {
						url = Messages.msgLocaleManualUrl92;
					}
					br.openURL(new URL(url));
				} catch (Exception ignored) {
				}
			}
		});

	}

	/**
	 * Create generic volume information group
	 *
	 * @param parent the parent composite
	 */
	private void createGenericVolumeGroup(Composite parent) {
		Group genericVolumeGroup = new Group(parent, SWT.NONE);
		genericVolumeGroup.setText(Messages.grpGenericVolInfo);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		genericVolumeGroup.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		genericVolumeGroup.setLayoutData(gridData);

		Label genericSizeLabel = new Label(genericVolumeGroup, SWT.LEFT
				| SWT.WRAP);
		genericSizeLabel.setText(Messages.lblVolSize);
		gridData = new GridData();
		gridData.widthHint = 150;
		genericSizeLabel.setLayoutData(gridData);

		genericVolumeSizeText = new Text(genericVolumeGroup, SWT.BORDER);
		genericVolumeSizeText.setTextLimit(20);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		genericVolumeSizeText.setLayoutData(gridData);
		genericVolumeSizeText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				genericVolumeSizeText.addModifyListener(GeneralInfoPage.this);
			}

			public void focusLost(FocusEvent event) {
				genericVolumeSizeText.removeModifyListener(GeneralInfoPage.this);
			}
		});

		Label genericVolumePathLabel = new Label(genericVolumeGroup, SWT.LEFT | SWT.WRAP);
		genericVolumePathLabel.setText(Messages.lblGenericVolPath);
		gridData = new GridData();
		gridData.widthHint = 150;
		genericVolumePathLabel.setLayoutData(gridData);
		genericVolumePathText = new Text(genericVolumeGroup, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		genericVolumePathText.setLayoutData(gridData);

		Button selectDirectoryButton = new Button(genericVolumeGroup, SWT.NONE);
		selectDirectoryButton.setText(Messages.btnBrowse);
		selectDirectoryButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
		selectDirectoryButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				if (databasePath != null && databasePath.trim().length() > 0) {
					dlg.setFilterPath(databasePath);
				}
				dlg.setText(Messages.msgSelectDir);
				dlg.setMessage(Messages.msgSelectDir);
				String dir = dlg.open();
				if (dir != null) {
					genericVolumePathText.setText(dir);
				}
			}
		});

		ServerInfo serverInfo = server.getServerInfo();
		if (serverInfo != null && !serverInfo.isLocalServer()) {
			selectDirectoryButton.setEnabled(false);
		}
	}

	/**
	 * Create log volume information group
	 *
	 * @param parent the parent composite
	 */
	private void createLogVolumeGroup(Composite parent) {
		Group logVolumeGroup = new Group(parent, SWT.NONE);
		logVolumeGroup.setText(Messages.grpLogVolInfo);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		logVolumeGroup.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		logVolumeGroup.setLayoutData(gridData);
		if (CompatibleUtil.isSupportLogPageSize(server.getServerInfo())) {
			Label logPageSizeLabel = new Label(logVolumeGroup, SWT.LEFT
					| SWT.WRAP);
			logPageSizeLabel.setText(Messages.lblLogPageSize);
			gridData = new GridData();
			gridData.widthHint = 150;
			logPageSizeLabel.setLayoutData(gridData);

			logPageSizeCombo = new Combo(logVolumeGroup, SWT.DROP_DOWN
					| SWT.READ_ONLY);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			logPageSizeCombo.setLayoutData(gridData);
			logPageSizeCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					valid();
				}
			});
		}

		Label logSizeLabel = new Label(logVolumeGroup, SWT.LEFT | SWT.WRAP);
		logSizeLabel.setText(Messages.lblVolSize);
		gridData = new GridData();
		gridData.widthHint = 150;
		logSizeLabel.setLayoutData(gridData);

		logVolumeSizeText = new Text(logVolumeGroup, SWT.BORDER);
		logVolumeSizeText.setTextLimit(20);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		logVolumeSizeText.setLayoutData(gridData);
		logVolumeSizeText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent event) {
				logVolumeSizeText.addModifyListener(GeneralInfoPage.this);
			}

			public void focusLost(FocusEvent event) {
				logVolumeSizeText.removeModifyListener(GeneralInfoPage.this);
			}
		});

		Label logVolumePathLabel = new Label(logVolumeGroup, SWT.LEFT | SWT.WRAP);
		logVolumePathLabel.setText(Messages.lblLogVolPath);
		gridData = new GridData();
		gridData.widthHint = 150;
		logVolumePathLabel.setLayoutData(gridData);

		logVolumePathText = new Text(logVolumeGroup, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		logVolumePathText.setLayoutData(gridData);

		Button selectDirectoryButton = new Button(logVolumeGroup, SWT.NONE);
		selectDirectoryButton.setText(Messages.btnBrowse);
		selectDirectoryButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
		selectDirectoryButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				if (databasePath != null && databasePath.trim().length() > 0) {
					dlg.setFilterPath(databasePath);
				}
				dlg.setText(Messages.msgSelectDir);
				dlg.setMessage(Messages.msgSelectDir);
				String dir = dlg.open();
				if (dir != null) {
					logVolumePathText.setText(dir);
				}
			}
		});

		ServerInfo serverInfo = server.getServerInfo();
		if (serverInfo != null && !serverInfo.isLocalServer()) {
			selectDirectoryButton.setEnabled(false);
		}
	}

	/**
	 * Create auto start group
	 *
	 * @param parent the parent composite
	 */
	private void createAutoStartGroup(Composite parent) {
		Group buttonGroup = new Group(parent, SWT.NONE);
		buttonGroup.setText(Messages.grpAutoStartInfo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		buttonGroup.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		buttonGroup.setLayout(layout);

		/*create auto start button*/
		autoStartButton = new Button(buttonGroup,SWT.CHECK);
		autoStartButton.setText(Messages.btnAutoStart);
		autoStartButton.setSelection(true);

		Label autoStartInfoLable = new Label(buttonGroup,SWT.None);
		autoStartInfoLable.setText(Messages.lblAutoStartInfo);
	}

	/**
	 * Call this method when modify text
	 *
	 * @param event the modify event
	 */
	public void modifyText(ModifyEvent event) {
		String databaseName = databaseNameText.getText();
		boolean isValidDatabaseName = ValidateUtil.isValidDBName(databaseName);
		if (!isValidDatabaseName) {
			setErrorMessage(Messages.errDbName);
			setPageComplete(false);
			return;
		}

		boolean isValidDatabaseNameLength = ValidateUtil.isValidDbNameLength(databaseName);
		if (!isValidDatabaseNameLength) {
			setErrorMessage(Messages.bind(
					Messages.errDbNameLength,
					new String[]{String.valueOf(ValidateUtil.MAX_DB_NAME_LENGTH - 1) }));
			setPageComplete(false);
			return;
		}

		List<String> dbList = server.getServerInfo().getAllDatabaseList();
		if (dbList != null) {
			for (String dbName : dbList) {
				if (databaseName.equalsIgnoreCase(dbName)) {
					setErrorMessage(Messages.errDbExist);
					setPageComplete(false);
					return;
				}
			}
		}

		if (event.widget == databaseNameText && isValidDatabaseName
				&& isValidDatabaseNameLength) {
			genericVolumePathText.setText(databasePath
					+ server.getServerInfo().getPathSeparator()
					+ databaseNameText.getText());
			logVolumePathText.setText(databasePath
					+ server.getServerInfo().getPathSeparator()
					+ databaseNameText.getText());
			VolumeInfoPage volumeInfoPage = (VolumeInfoPage) getWizard().getPage(
					VolumeInfoPage.PAGENAME);
			if (volumeInfoPage != null) {
				volumeInfoPage.changeVolumePath();
				volumeInfoPage.changeVolumeTable();
			}
		} else if (event.widget == databaseNameText
				&& (!isValidDatabaseName || !isValidDatabaseNameLength)) {
			genericVolumePathText.setText(databasePath);
			logVolumePathText.setText(databasePath);
		}

		valid();
	}

	private void valid() {
		String databaseName = databaseNameText.getText();
		boolean isValidDatabaseName = ValidateUtil.isValidDBName(databaseName);
		if (!isValidDatabaseName) {
			setErrorMessage(Messages.errDbName);
			setPageComplete(false);
			return;
		}

		boolean isValidDatabaseNameLength = ValidateUtil.isValidDbNameLength(databaseName);
		if (!isValidDatabaseNameLength) {
			setErrorMessage(Messages.bind(
					Messages.errDbNameLength,
					new String[]{String.valueOf(ValidateUtil.MAX_DB_NAME_LENGTH - 1) }));
			setPageComplete(false);
			return;
		}

		List<String> dbList = server.getServerInfo().getAllDatabaseList();
		if (dbList != null) {
			for (String dbName : dbList) {
				if (databaseName.equalsIgnoreCase(dbName)) {
					setErrorMessage(Messages.errDbExist);
					setPageComplete(false);
					return;
				}
			}
		}

		if (userDefinedCharsetText != null
				&& userDefinedCharsetText.getEnabled()) {
			String charset = userDefinedCharsetText.getText();
			if (StringUtil.isEmpty(charset)) {
				setErrorMessage(Messages.errUserDefinedCharset);
				setPageComplete(false);
				return;
			}
		}

		String genericVolumeSize = genericVolumeSizeText.getText();
		boolean isValidGenericiVolumeSize = (ValidateUtil.isNumber(genericVolumeSize) || ValidateUtil.isPositiveDouble(genericVolumeSize))
				&& Double.parseDouble(genericVolumeSize) >= 20
				&& Double.parseDouble(genericVolumeSize) <= 20480;
		if (!isValidGenericiVolumeSize) {
			setErrorMessage(Messages.errGenericVolSize);
			setPageComplete(false);
			return;
		}

		String genericVolumePath = genericVolumePathText.getText();
		boolean isValidGenericPathName = ValidateUtil.isValidPathName(genericVolumePath);
		if (!isValidGenericPathName) {
			setErrorMessage(Messages.errGenericVolPath);
			setPageComplete(false);
			return;
		}

		String logSize = logVolumeSizeText.getText();
		boolean isValidLogSize = (ValidateUtil.isNumber(logSize) || ValidateUtil.isPositiveDouble(logSize))
				&& Double.parseDouble(logSize) >= 20
				&& Double.parseDouble(logSize) <= 20480;
		if (!isValidLogSize) {
			setErrorMessage(Messages.errLogSize);
			setPageComplete(false);
			return;
		}

		String logVolumePath = logVolumePathText.getText();
		boolean isValidLogPathName = ValidateUtil.isValidPathName(logVolumePath);
		if (!isValidLogPathName) {
			setErrorMessage(Messages.errLogVolPath);
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	/**
	 * Return page number,for example:(1)pageNum=100.01,return 101 (2)
	 * pageNum=100.00,return 100
	 *
	 * @param pageNum the page number
	 * @return the page number
	 */
	public static long getPageNum(double pageNum) {
		long lPageNum = (long) pageNum;
		if (lPageNum < pageNum) {
			lPageNum = lPageNum + 1;
		}

		return lPageNum;
	}

	/**
	 * Calculate volume size
	 *
	 * @return pageNumber - If the pageNum is valid,return the page
	 *         number,otherwise return -1;
	 */
	private long calcLogPageNum() {
		String logVolumeSize = logVolumeSizeText.getText();
		String pageSizeStr = pageSizeCombo.getText();
		if (logPageSizeCombo != null) {
			pageSizeStr = logPageSizeCombo.getText();
		}

		return CreateDatabaseWizard.calcVolumePageNum(logVolumeSize, pageSizeStr);
	}

	private void initial() {
		String[] pageSizeItem = CompatibleUtil.getSupportedPageSize(server.getServerInfo());
		pageSizeCombo.setItems(pageSizeItem);
		pageSizeCombo.setText(CompatibleUtil.getDefaultPageSize(server.getServerInfo()));
		if (logPageSizeCombo != null) {
			logPageSizeCombo.setItems(pageSizeItem);
			logPageSizeCombo.setText(CompatibleUtil.getDefaultPageSize(server.getServerInfo()));
		}
		/*if (charsetCombo != null) {
			charsetCombo.setItems(charsetItem);
			charsetCombo.setText(DEFAULT);
		}*/

		EnvInfo envInfo = server.getServerInfo().getEnvInfo();
		if (envInfo != null) {
			databasePath = envInfo.getDatabaseDir();
			ServerInfo serverInfo = server.getServerInfo();
			if (serverInfo != null) {
				databasePath = FileUtil.changeSeparatorByOS(databasePath,
						serverInfo.getServerOsInfo());
			}
			genericVolumePathText.setText(databasePath);
			logVolumePathText.setText(databasePath);
		}

		genericVolumeSizeText.setText(ConfConstants.DEFAULT_DATA_VOLUME_SIZE);
		defaultDatVolumeSize = ConfConstants.DEFAULT_DATA_VOLUME_SIZE;
		/*If Configed Generic Volume Size is not null*/
		String genericVolumeSize = CompatibleUtil.getConfigGenericVolumeSize(
				server.getServerInfo(), null);
		if (!StringUtil.isEmpty(genericVolumeSize)) {
			Long bytes = StringUtil.getByteNumber(genericVolumeSize);
			if (bytes > -1) {
				double value = StringUtil.convertToM(bytes);
				String strValue = getIntSizeString(value, 20);
				genericVolumeSizeText.setText(strValue);
				defaultDatVolumeSize = strValue;
			}
		}

		logVolumeSizeText.setText(ConfConstants.DEFAULT_DATA_VOLUME_SIZE);
		/*If Config Log Volume Size is not null*/
		String logValueSize = CompatibleUtil.getConfigLogVolumeSize(
				server.getServerInfo(), null);
		if (!StringUtil.isEmpty(logValueSize)) {
			Long bytes = StringUtil.getByteNumber(logValueSize);
			if (bytes > -1) {
				double value = StringUtil.convertToM(bytes);
				logVolumeSizeText.setText(getIntSizeString(value, 20));
			}
		}

		databaseNameText.addModifyListener(this);
		genericVolumePathText.addModifyListener(this);
		logVolumePathText.addModifyListener(this);
		
		selectInitialDatabaseCollationAndCharset();
	}

	/**
	 * select a default collation
	 */
	private void selectInitialDatabaseCollationAndCharset() {
		Locale locale = Locale.getDefault();

		if (locale.equals(Locale.KOREA) || locale.equals(Locale.KOREAN)) {
			koUtfRadio.setSelection(true);
		} else {
			enIsoRadio.setSelection(true);
		}
	}

	/**
	 * Get database name
	 *
	 * @return the database name
	 */
	public String getDatabaseName() {
		return databaseNameText.getText();
	}

	/**
	 * Get generic volume size
	 *
	 * @return the generic volume size
	 */
	public String getGenericVolumeSize() {
		return genericVolumeSizeText.getText();
	}

	/**
	 * Get generic page number
	 *
	 * @return the generic page number
	 */
	public String getGenericPageNum() {
		return String.valueOf(CreateDatabaseWizard.calcVolumePageNum(genericVolumeSizeText.getText(), pageSizeCombo.getText()));
	}

	/**
	 * Get generic page size
	 *
	 * @return the page size
	 */
	public String getPageSize() {
		return pageSizeCombo.getText();
	}

	/**
	 * Get log page size
	 *
	 * @return the page size
	 */
	public String getLogPageSize() {
		if (logPageSizeCombo != null) {
			return logPageSizeCombo.getText();
		}
		return null;
	}

	/**
	 * Get charset
	 *
	 * @return the chart
	 */
	public String getCharset () {
		if (enIsoRadio != null && enIsoRadio.getSelection()) {
			return CHARSET_EN_US_ISO88591;
		} else if (enUtfRadio != null && enUtfRadio.getSelection()) {
			return CHARSET_EN_US_UTF8;
		} else if (koEuckrRadio != null && koEuckrRadio.getSelection()) {
			return CHARSET_KO_KR_EUCKR;
		} else if (koUtfRadio != null && koUtfRadio.getSelection()) {
			return CHARSET_KO_KR_UTF8;
		} else if (userDefinedRadio != null && userDefinedRadio.getSelection()
				&& userDefinedCharsetText != null) {
			return userDefinedCharsetText.getText().trim();
		}
		//		if (charsetCombo != null) {
		//			return DEFAULT.equals(charsetCombo.getText())? null : charsetCombo.getText();
		//		}
		return null;
	}

	/**
	 * Get generic volume path
	 *
	 * @return the generic volume path
	 */
	public String getGenericVolumePath() {
		return genericVolumePathText.getText();
	}

	/**
	 * Get log page number
	 *
	 * @return the log page number
	 */
	public String getLogPageNum() {
		return String.valueOf(calcLogPageNum());
	}

	/**
	 * Get log volume path
	 *
	 * @return the log volume path
	 */
	public String getLogVolumePath() {
		return logVolumePathText.getText();
	}

	/**
	 * Get default data volume size
	 *
	 * @return the data volume size
	 */
	public String getDefaultDatVolumeSize() {
		return defaultDatVolumeSize;
	}

	public boolean isAutoStart() {
		return autoStartButton.getSelection();
	}

	/**
	 * Get the volume size string
	 *
	 * @param value
	 * @param minValue
	 * @return
	 */
	static String getIntSizeString(double value, int minValue) {
		Integer intValue = new Double(value).intValue();
		if (intValue < minValue) {
			intValue = minValue;
		}
		return intValue.toString();
	}
}
