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
package com.cubrid.common.ui.query.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;

/**
 * a composite to show query property page
 * 
 * @author wangsl 2009-6-4
 */
public class QueryPropertyComposite extends
		Composite {

	private Button searchUnitBtn;
	private Spinner pageUnitCountSpinner;
	private Spinner unitCountSpinner;
	private Spinner loadSizeSpinner;
//	private Button oidBtn;
	private final CubridServer server;
	protected int fontColorBlue;
	protected int fontColorGreen;
	protected int fontColorRed;
	protected String fontString = "";
	private Text fontExampleTxt;
	private Button keywordLowerBtn;
	private Button withoutPromptSaveBtn;
	private Button autoNoUppercaseKeywordBtn;
	private Button multiPageConfirmBtn;
	private Button useScientificNotationBtn;

	public QueryPropertyComposite(Composite parent, CubridServer server) {
		super(parent, SWT.NONE);
		this.server = server;
		createContent();
	}

	/**
	 * load query option from preference store
	 */
	public void loadPreference() {
		ServerInfo serverInfo = server == null ? null : server.getServerInfo();
		boolean unitInstances = QueryOptions.getEnableSearchUnit(serverInfo);
		int recordCount = QueryOptions.getSearchUnitCount(serverInfo);
		int pageCount = QueryOptions.getPageLimit(serverInfo);
		int loadSize = QueryOptions.getLobLoadSize(serverInfo);

		boolean isKeywordLowerCase = QueryOptions.getKeywordLowercase(serverInfo);
		boolean isNoAutoUppercaseKeyword = QueryOptions.getNoAutoUppercaseKeyword(serverInfo);
		boolean isWithoutPromptSave = QueryOptions.getWithoutPromptSave(serverInfo);
		boolean isShowMultiPageConfirm = QueryOptions.getMultiPageConfirm();
		boolean isUseScientificNotation = QueryOptions.getUseScientificNotation(serverInfo);

		fontColorRed = QueryOptions.getFontColorRed(serverInfo);
		fontColorBlue = QueryOptions.getFontColorBlue(serverInfo);
		fontColorGreen = QueryOptions.getFontColorGreen(serverInfo);
		fontString = QueryOptions.getFontString(serverInfo);

		searchUnitBtn.setSelection(unitInstances);
		unitCountSpinner.setEnabled(unitInstances);
		unitCountSpinner.setSelection(recordCount);
		loadSizeSpinner.setSelection(loadSize);
		pageUnitCountSpinner.setSelection(pageCount);

		keywordLowerBtn.setSelection(isKeywordLowerCase);
		keywordLowerBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				autoNoUppercaseKeywordBtn.setEnabled(!keywordLowerBtn.getSelection());
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		autoNoUppercaseKeywordBtn.setSelection(isNoAutoUppercaseKeyword);
		withoutPromptSaveBtn.setSelection(isWithoutPromptSave);
		multiPageConfirmBtn.setSelection(isShowMultiPageConfirm);
		changeExampleFont();

		autoNoUppercaseKeywordBtn.setEnabled(!isKeywordLowerCase);
		useScientificNotationBtn.setSelection(isUseScientificNotation);
	}

	/**
	 * Change example font
	 */
	private void changeExampleFont() {
		Font font = ResourceManager.getFont(fontString);
		if (font == null) {
			String[] fontData = QueryOptions.getDefaultFont();
			font = ResourceManager.getFont(fontData[0],
					Integer.valueOf(fontData[1]), Integer.valueOf(fontData[2]));
		}
		fontExampleTxt.setFont(font);
		fontExampleTxt.setForeground(ResourceManager.getColor(fontColorRed,
				fontColorGreen, fontColorBlue));
		fontExampleTxt.redraw();
	}

	/**
	 * 
	 * Save query options
	 */
	public void save() {
		ServerInfo serverInfo = server == null ? null : server.getServerInfo();
		boolean isEnableSearchUnit = searchUnitBtn.getSelection();
		int unitCount = unitCountSpinner.getSelection();
		int pageUnitCount = pageUnitCountSpinner.getSelection();
		int loadSize = loadSizeSpinner.getSelection();

		boolean isKeywordLowercase = keywordLowerBtn.getSelection();
		boolean isNoAutoUppercaseKeyword = autoNoUppercaseKeywordBtn.getSelection();
		boolean isWithoutPromptSave = withoutPromptSaveBtn.getSelection();
		boolean isShowMultiPageConfirm = multiPageConfirmBtn.getSelection();
		boolean isUseScientificNotation = useScientificNotationBtn.getSelection();

		QueryOptions.setEnableSearchUnit(serverInfo, isEnableSearchUnit);
		QueryOptions.setSearchUnitCount(serverInfo, unitCount);
		QueryOptions.setPageLimit(serverInfo, pageUnitCount);
		QueryOptions.setKeywordLowercase(serverInfo, isKeywordLowercase);
		QueryOptions.setNoAutoUppercaseKeyword(serverInfo, isNoAutoUppercaseKeyword);
		QueryOptions.setWithoutPromptSave(serverInfo, isWithoutPromptSave);
		QueryOptions.setFontColorRed(serverInfo, fontColorRed);
		QueryOptions.setFontColorGreen(serverInfo, fontColorGreen);
		QueryOptions.setFontColorBlue(serverInfo, fontColorBlue);
		QueryOptions.setFontString(serverInfo, fontString);
		QueryOptions.setShowStyle(serverInfo, true);
		QueryOptions.setUseScientificNotation(serverInfo, isUseScientificNotation);
		QueryOptions.setLobLoadSize(serverInfo, loadSize);
		QueryOptions.setMultiPageConfirm(isShowMultiPageConfirm);
		QueryOptions.savePref();

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IEditorReference[] editorRefs = page.getEditorReferences();
			for (IEditorReference ref : editorRefs) {
				IEditorPart editor = ref.getEditor(true);
				if (editor instanceof QueryEditorPart) {
					QueryEditorPart part = (QueryEditorPart) editor;
					part.refreshQueryOptions();
				}

			}
		}
	}

	/**
	 * create the content.
	 * 
	 */
	private void createContent() {
		setLayout(new GridLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Group groupFirst = new Group(this, SWT.NONE);
		groupFirst.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		groupFirst.setLayout(gridLayout);

		searchUnitBtn = new Button(groupFirst, SWT.CHECK);
		final GridData gdSearchUnitBtn = new GridData(SWT.LEFT, SWT.CENTER,
				true, false);
		searchUnitBtn.setLayoutData(gdSearchUnitBtn);
		searchUnitBtn.setText(Messages.searchUnitInstances);
		searchUnitBtn.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 * @param event an event containing information about the selection
			 */
			public void widgetSelected(SelectionEvent event) {
				unitCountSpinner.setEnabled(searchUnitBtn.getSelection());
			}

		});
		unitCountSpinner = new Spinner(groupFirst, SWT.BORDER);
		unitCountSpinner.setMaximum(2147483647);
		final GridData gdUnitCountSpinner = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false);
		gdUnitCountSpinner.widthHint = 129;
		unitCountSpinner.setLayoutData(gdUnitCountSpinner);
		unitCountSpinner.setIncrement(100);

		final Label label = new Label(groupFirst, SWT.NONE);
		label.setText(Messages.pageUnitInstances);

		pageUnitCountSpinner = new Spinner(groupFirst, SWT.BORDER);
		pageUnitCountSpinner.setMaximum(2147483647);
		final GridData gdPageUnitSpinner = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false);
		gdPageUnitSpinner.widthHint = 129;
		pageUnitCountSpinner.setLayoutData(gdPageUnitSpinner);
		pageUnitCountSpinner.setIncrement(10);

		final Label lobSizeLabel = new Label(groupFirst, SWT.NONE);
		lobSizeLabel.setText(Messages.lblLobLoadSize);

		loadSizeSpinner = new Spinner(groupFirst, SWT.BORDER);
		loadSizeSpinner.setMaximum(1024 * 1024);
		final GridData gdlobSizeSpinner = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gdlobSizeSpinner.widthHint = 129;
		loadSizeSpinner.setLayoutData(gdPageUnitSpinner);
		loadSizeSpinner.setIncrement(8);

		multiPageConfirmBtn = new Button(groupFirst, SWT.CHECK);
		multiPageConfirmBtn.setText(Messages.showMultiPageConfirm);
		{
			final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			multiPageConfirmBtn.setLayoutData(gd);
		}

		keywordLowerBtn = new Button(groupFirst, SWT.CHECK);
		keywordLowerBtn.setText(Messages.btnKeywordLowercase);
		final GridData gdKeywordBtn = new GridData(GridData.FILL_HORIZONTAL);
		gdKeywordBtn.horizontalSpan = 2;
		keywordLowerBtn.setLayoutData(gdKeywordBtn);

		autoNoUppercaseKeywordBtn = new Button(groupFirst, SWT.CHECK);
		autoNoUppercaseKeywordBtn.setText(Messages.btnNoAutoUppercase);
		final GridData gdAutoUppercaseKeywordBtn = new GridData(GridData.FILL_HORIZONTAL);
		gdAutoUppercaseKeywordBtn.horizontalSpan = 2;
		autoNoUppercaseKeywordBtn.setLayoutData(gdAutoUppercaseKeywordBtn);

		withoutPromptSaveBtn = new Button(groupFirst, SWT.CHECK);
		withoutPromptSaveBtn.setText(Messages.btnWithoutPromptSave);
		final GridData gdPromptSaveBtn = new GridData(GridData.FILL_HORIZONTAL);
		gdPromptSaveBtn.horizontalSpan = 2;
		withoutPromptSaveBtn.setLayoutData(gdPromptSaveBtn);

		useScientificNotationBtn = new Button(groupFirst, SWT.CHECK);
		useScientificNotationBtn.setText(Messages.btnUseScientificNotation);
		useScientificNotationBtn.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));

		final Group changeFontGroup = new Group(this, SWT.NONE);
		changeFontGroup.setText(Messages.changeFont);
		final GridData gdChangeFontGroup = new GridData(SWT.FILL, SWT.FILL,
				true, true);
		changeFontGroup.setLayoutData(gdChangeFontGroup);
		changeFontGroup.setLayout(new GridLayout());

		final Group exampleGroup = new Group(changeFontGroup, SWT.NONE);
		exampleGroup.setText(Messages.fontExample);
		final GridData gdExmplaeGroup = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		exampleGroup.setLayoutData(gdExmplaeGroup);
		exampleGroup.setLayout(new GridLayout());

		fontExampleTxt = new Text(exampleGroup, SWT.CENTER | SWT.BORDER);
		fontExampleTxt.setEditable(false);
		fontExampleTxt.setLayoutData(CommonUITool.createGridData(
				GridData.FILL_BOTH, 1, 1, -1, 30));
		fontExampleTxt.setText("CUBRID");
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		fontExampleTxt.setMenu(menu);

		final Composite btnComposite = new Composite(changeFontGroup, SWT.NONE);
		btnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false));
		final GridLayout gridLayoutBtnComposite = new GridLayout();
		gridLayoutBtnComposite.numColumns = 2;
		btnComposite.setLayout(gridLayoutBtnComposite);

		Button changeBtn = new Button(btnComposite, SWT.PUSH);
		final GridData gdChangeBtn = new GridData(SWT.RIGHT, SWT.CENTER, false,
				false);
		changeBtn.setLayoutData(gdChangeBtn);
		changeBtn.setText(Messages.change);
		changeBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FontDialog dlg = new FontDialog(getShell());
				FontData fontdata = null;
				if (fontString == null || fontString.trim().length() == 0) {
					fontdata = new FontData();
					String[] fontStr = QueryOptions.getDefaultFont();
					fontdata.setName(fontStr[0]);
					fontdata.setHeight(Integer.parseInt(fontStr[1]));
					fontdata.setStyle(Integer.parseInt(fontStr[2]));
				} else {
					fontdata = new FontData(fontString);
				}
				FontData fontList[] = new FontData[1];
				fontList[0] = fontdata;
				dlg.setRGB(new RGB(fontColorRed, fontColorGreen, fontColorBlue));
				dlg.setFontList(fontList);

				fontdata = dlg.open();
				if (fontdata != null) {
					fontString = fontdata.toString();
					RGB rgb = dlg.getRGB();
					if (rgb != null) {
						fontColorRed = rgb.red;
						fontColorBlue = rgb.blue;
						fontColorGreen = rgb.green;
					}
					changeExampleFont();
				}
			}
		});

		Button restoreBtn = new Button(btnComposite, SWT.PUSH);
		restoreBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		restoreBtn.setText(Messages.restoreDefault);
		restoreBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				fontString = "";
				FontData[] fData = Display.getDefault().getSystemFont().getFontData();
				if (fData != null && fData.length > 0) {
					fontString = fData[0].toString();
				}
				fontColorRed = 0;
				fontColorGreen = 0;
				fontColorBlue = 0;
				changeExampleFont();
			}
		});
	}

	/**
	 * validate data.
	 * 
	 * @return boolean
	 */
	public boolean checkValid() {
		return true;
	}
}
