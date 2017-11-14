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
package com.cubrid.common.ui.common.control;

import static com.cubrid.common.ui.CommonUIPlugin.PLUGIN_ID;
import static com.cubrid.common.ui.CommonUIPlugin.getImage;
import static com.cubrid.common.ui.spi.util.CommonUITool.createGridData;
import static com.cubrid.common.ui.spi.util.CommonUITool.openConfirmBox;
import static com.cubrid.common.ui.spi.util.CommonUITool.openErrorBox;
import static com.cubrid.common.ui.spi.util.CommonUITool.openInformationBox;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.common.dialog.BrokerConfOpenFileDialog;
import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil;
import com.cubrid.common.ui.spi.persist.PersistUtils;

/**
 * CUBRID broker editor.
 *
 * @author fulei
 * @version 1.0 - 2012-10-29 created by fulei
 */
public class BrokerConfigEditorPart extends
		AbstractBrokerConfigEditorPart {
	public static final String ID = BrokerConfigEditorPart.class.getName();
	private final Logger LOGGER = LogUtil.getLogger(getClass());
	private final BrokerConfPersistUtil persistUtil = new BrokerConfPersistUtil();
	private ToolItem saveConfItem;
	private ToolItem saveAsConfItem;
	private File confFile;
	private String charset;
	private boolean firstView = true;

	/**
	 * Create part controls
	 *
	 * @param parent of the controls
	 */
	public void createPartControl(Composite parent) {
		final Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(1, false));

		createToolBarComp(comp);
		editorComp = new BrokerConfigEditComposite(comp, SWT.NONE, this, confFile.getName());

		loadData();
	}

	/**
	 * Create the toolbar composition.
	 *
	 * @param parent
	 */
	public void createToolBarComp(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gridData);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = -1;
		comp.setLayout(layout);

		final ToolBar toolbar = new ToolBar(comp, SWT.LEFT_TO_RIGHT | SWT.FLAT);
		toolbar.setLayoutData(createGridData(HORIZONTAL_ALIGN_END, 1, 1, -1, -1));

		// Show/hide of the history pane
		saveConfItem = new ToolItem(toolbar, SWT.PUSH);
		saveConfItem.setImage(getImage("icons/queryeditor/file_save.png"));
		saveConfItem.setToolTipText(Messages.cubridBrokerConfEditorSaveItemLabel);
		saveConfItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// check whether contains duplicate property name
				String errMsg = editorComp.validate();
				if (isNotBlank(errMsg)) {
					openErrorBox(errMsg);
					return;
				}
				if (!openConfirmBox(Messages.cubridBrokerConfEditorSaveConfirm)) {
					return;
				}
				saveData();
			}
		});

		// Show/hide of the history pane
		saveAsConfItem = new ToolItem(toolbar, SWT.PUSH);
		saveAsConfItem.setImage(getImage("icons/queryeditor/file_saveas.png"));
		saveAsConfItem.setToolTipText(Messages.cubridBrokerConfEditorSaveAsItemLabel);
		saveAsConfItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Check whether contains duplicate property or broker name
				String errMsg = editorComp.validate();
				if (isNotBlank(errMsg)) {
					openErrorBox(errMsg);
					return;
				}
				if (!openConfirmBox(Messages.cubridBrokerConfEditorSaveAsConfirm)) {
					return;
				}

				final FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN | SWT.MULTI);
				dialog.setFilterPath(PersistUtils.getPreferenceValue(PLUGIN_ID,
						BrokerConfOpenFileDialog.CUBRIDBROKERCONFPATH));
				dialog.setText(Messages.cubridBrokerConfEditorSaveAsDialogTitle);
				dialog.setFilterExtensions(new String[] { "*.conf" });
				dialog.setOverwrite(false);
				String path = dialog.open();
				File saveAsFile = null;
				if (path == null) {
					return;
				}
				if (!path.endsWith(".conf")) {
					path += ".conf";
				}
				saveAsFile = new File(path);

				BrokerConfig brokerConf = editorComp.getBrokerConfig();
				String contents = persistUtil.readBrokerConfig(brokerConf);
				try {
					persistUtil.writeBrokerConfig(saveAsFile, charset, contents);
					openInformationBox(Messages.titleSuccess,
							Messages.cubridBrokerConfEditorSaveSuccessMsg);
					setDirty(false);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					openErrorBox(e.getMessage());
				}
			}
		});

		new ToolItem(toolbar, SWT.SEPARATOR);

		addPropItem = new ToolItem(toolbar, SWT.PUSH);
		addPropItem.setImage(getImage("icons/queryeditor/table_record_insert.png"));
		addPropItem.setToolTipText(Messages.cubridBrokerConfEditorAddPropItemLabel);
		addPropItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editorComp.addPropData();
			}
		});

		deletePropItem = new ToolItem(toolbar, SWT.PUSH);
		deletePropItem.setImage(getImage("icons/queryeditor/table_record_delete.png"));
		deletePropItem.setToolTipText(Messages.cubridBrokerConfEditorDeletePropItemLabel);
		deletePropItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editorComp.deletePropData();
			}
		});
	}

	/**
	 * Save data to the open file
	 */
	public void saveData() {
		try {
			BrokerConfig brokerConf = editorComp.getBrokerConfig();
			String contents = persistUtil.readBrokerConfig(brokerConf);
			persistUtil.writeBrokerConfig(confFile, charset, contents);
			openInformationBox(Messages.titleSuccess, Messages.cubridBrokerConfEditorSaveSuccessMsg);
			setDirty(false);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			openErrorBox(e.getMessage());
		}
	}

	public void doSave(IProgressMonitor monitor) {
		saveData();
	}

	/**
	 * Parse common table value to CubridBrokerConfig
	 *
	 * @param dataList
	 * @param tableColumnCount
	 * @return
	 */
	public BrokerConfig parseCommonTableValueToBrokerConfig(List<Map<String, String>> dataList,
			int tableColumnCount) {
		return persistUtil.parseCommonTableValueToBrokerConfig(dataList, tableColumnCount);
	}

	/**
	 * Parse string line to CubridBrokerConfig
	 *
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public BrokerConfig parseStringLineToBrokerConfig(String content) {
		return persistUtil.parseStringLineToBrokerConfig(content);
	}

	/**
	 * Parse a CubridBrokerConfig model to a document string
	 *
	 * @param config CubridBrokerConfig
	 * @return String
	 */
	public String parseBrokerConfigToDocumnetString(BrokerConfig config) {
		return persistUtil.readBrokerConfig(config);
	}

	/**
	 * Parse CubridBrokerConfig model to common table value
	 *
	 * @param config
	 * @return
	 */
	public List<Map<String, String>> parseBrokerConfigToCommonTableValue(BrokerConfig config) {
		return persistUtil.parseBrokerConfigToCommonTableValue(config);
	}

	/**
	 * Initialize the editor part
	 *
	 * @param site IEditorSite
	 * @param input MigrationCfgEditorInput
	 * @throws PartInitException when error
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.confFile = (File) input.getAdapter(File.class);
		this.charset = (String) input.getAdapter(String.class);

		setSite(site);
		setInput(input);
		setTitleToolTip(confFile.getAbsolutePath());
		setTitleImage(getImage("icons/queryeditor/query_editor.png"));
		setPartName(confFile.getName());
	}

	/**
	 * Load cubrid broker config info
	 */
	public void loadData() {
		try {
			if (firstView) {
				String contents = persistUtil.loadCubridBrokerConfigString(
						confFile.getAbsoluteFile(), charset);
				BrokerConfig brokerConf = persistUtil.parseStringLineToBrokerConfig(contents);
				firstView = false;
				editorComp.setBrokerConfig(brokerConf);
				editorComp.createBrokerConfTableData();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Set edit table item enable
	 *
	 * @param enabled
	 */
	public void setEditTableItemEnabled(boolean enabled) {
		addPropItem.setEnabled(enabled);
		deletePropItem.setEnabled(enabled);
	}
}
