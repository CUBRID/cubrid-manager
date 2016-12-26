/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.common.ui.query.control;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.slf4j.Logger;

import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.action.HelpDocumentAction;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.action.AddQueryToFavoriteAction;
import com.cubrid.common.ui.query.action.CopyAction;
import com.cubrid.common.ui.query.action.CreateSqlJavaCodeAction;
import com.cubrid.common.ui.query.action.CreateSqlPhpCodeAction;
import com.cubrid.common.ui.query.action.CutAction;
import com.cubrid.common.ui.query.action.FindReplaceAction;
import com.cubrid.common.ui.query.action.GotoLineAction;
import com.cubrid.common.ui.query.action.PasteAction;
import com.cubrid.common.ui.query.action.QueryOpenAction;
import com.cubrid.common.ui.query.action.RedoAction;
import com.cubrid.common.ui.query.action.ReformatColumnsAliasAction;
import com.cubrid.common.ui.query.action.RunQueryAction;
import com.cubrid.common.ui.query.action.RunQueryPlanAction;
import com.cubrid.common.ui.query.action.ParseSqlmapQueryAction;
import com.cubrid.common.ui.query.action.ShowSchemaAction;
import com.cubrid.common.ui.query.action.UndoAction;
import com.cubrid.common.ui.query.builder.quickbuilder.QuickBuilderDialog;
import com.cubrid.common.ui.query.dialog.SetFileEncodingDialog;
import com.cubrid.common.ui.query.editor.ISQLPartitions;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.SQLContentAssistProcessor;
import com.cubrid.common.ui.query.editor.SQLDocument;
import com.cubrid.common.ui.query.editor.SQLPartitionScanner;
import com.cubrid.common.ui.query.editor.SQLTextViewer;
import com.cubrid.common.ui.query.editor.SQLViewerConfiguration;
import com.cubrid.common.ui.query.editor.SubQueryEditorTabItem;
import com.cubrid.common.ui.query.tuner.action.QueryTunerRunAction;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.FocusAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.tool.editor.TextEditorFindReplaceMediator;
import com.cubrid.tool.editor.dialog.FindReplaceOption;

/**
 * A SQL editor composite
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-3 created by pangqiren
 * @version 8.4.1 - 2012-04 modify by fulei
 */
public class SQLEditorComposite extends Composite {
	private static final Logger LOGGER = LogUtil.getLogger(SQLEditorComposite.class);
	public static final String SQL_EDITOR_FLAG = TextEditorFindReplaceMediator.SQL_EDITOR_FLAG;
	private SQLTextViewer sqlTextViewer;
	private StyledText text;
	private SQLDocument document;
	private TextViewerUndoManager undoManager;
	private FindReplaceDocumentAdapter findReplaceDocAdapter;
	private IContentAssistant contentAssistant;
	private IContentAssistant recentlyUsedSQLcontentAssistant;
	private final QueryEditorPart queryEditor;
	protected FindReplaceOption findOption;
	private boolean isWholeWord;
	protected boolean useCompletions = true;
	private boolean pendingCompletionsListener = false;
	private SQLViewerConfiguration viewerConfig;
	private boolean dirty = false;
	private SubQueryEditorTabItem editorTabItem;

	private String filepath;
	private TextViewerOperationHandler formatHandler;
	private TextViewerOperationHandler contentAssistHandler;

	public SQLEditorComposite(Composite parent, int style, QueryEditorPart editorPart, SubQueryEditorTabItem editorTabItem) {
		super(parent, style);
		this.queryEditor = editorPart;
		this.editorTabItem = editorTabItem;
		setLayout(new GridLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createSQLEditor();
	}

	public void updateRuler() {
		if (sqlTextViewer != null) {
			sqlTextViewer.updateRuler();
		}
	}

	public QueryEditorPart getQueryEditorPart() {
		return queryEditor;
	}

	/**
	 * Create the SQL editor
	 */
	private void createSQLEditor() {
		final Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		CompositeRuler ruler = new CompositeRuler();
		LineNumberRulerColumn lineCol = new LineNumberRulerColumn();
		lineCol.setBackground(ResourceManager.getColor(new RGB(236, 233, 216)));
		ruler.addDecorator(0, lineCol);

		sqlTextViewer = new SQLTextViewer(composite, ruler, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER, queryEditor);
		viewerConfig = new SQLViewerConfiguration(queryEditor);

		sqlTextViewer.configure(viewerConfig);

		document = new SQLDocument();
		IDocumentPartitioner partitioner = new FastPartitioner(
				new SQLPartitionScanner(),
				SQLPartitionScanner.getAllTypes());
		document.setDocumentPartitioner(ISQLPartitions.SQL_PARTITIONING, partitioner);
		partitioner.connect(document);
		sqlTextViewer.setDocument(document);

		findReplaceDocAdapter = new FindReplaceDocumentAdapter(document);
		undoManager = new TextViewerUndoManager(50);
		undoManager.connect(sqlTextViewer);

		contentAssistant = viewerConfig.getContentAssistant(sqlTextViewer);
		contentAssistant.install(sqlTextViewer);

		recentlyUsedSQLcontentAssistant = viewerConfig.getRecentlyUsedContentAssistant(sqlTextViewer);
		recentlyUsedSQLcontentAssistant.install(sqlTextViewer);

		formatHandler = new TextViewerOperationHandler(sqlTextViewer,
				ISourceViewer.FORMAT);
		contentAssistHandler = new TextViewerOperationHandler(sqlTextViewer, ISourceViewer.CONTENTASSIST_PROPOSALS);

		text = (StyledText) sqlTextViewer.getTextWidget();
		text.setIndent(1);
		text.setData(SQL_EDITOR_FLAG, sqlTextViewer);

		createContextMenu();
		addListener();
	}

	protected void createContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IAction copyAction = ActionManager.getInstance().getAction(CopyAction.ID);
				if (copyAction != null) {
					manager.add(copyAction);
				}

				IAction cutAction = ActionManager.getInstance().getAction(CutAction.ID);
				if (cutAction != null) {
					manager.add(cutAction);
				}

				IAction pasteAction = ActionManager.getInstance().getAction(PasteAction.ID);
				if (pasteAction != null) {
					manager.add(pasteAction);
				}

				manager.add(new Separator());

				IAction findAction = ActionManager.getInstance().getAction(FindReplaceAction.ID);
				if (findAction != null) {
					manager.add(findAction);
				}

				manager.add(new Separator());

				IAction runQueryAction = ActionManager.getInstance().getAction(RunQueryAction.ID);
				if (runQueryAction != null) {
					manager.add(runQueryAction);
				}
				
				IAction runSqlmapQueryAction = ActionManager.getInstance().getAction(ParseSqlmapQueryAction.ID);
				if (runSqlmapQueryAction != null) {
					manager.add(runSqlmapQueryAction);
				}

				IAction runQueryPlanAction = ActionManager.getInstance().getAction(RunQueryPlanAction.ID);
				if (runQueryPlanAction != null) {
					manager.add(runQueryPlanAction);
				}

				IAction favoriteQueryAction = ActionManager.getInstance().getAction(AddQueryToFavoriteAction.ID);
				if (favoriteQueryAction != null) {
					manager.add(favoriteQueryAction);
				}

				manager.add(new Separator());

				IAction showSchemaViewAction = ActionManager.getInstance().getAction(ShowSchemaAction.ID);
				if (showSchemaViewAction != null) {
					manager.add(showSchemaViewAction);
				}
				manager.add(new Separator());

				IAction queryTunerRunAction = ActionManager.getInstance().getAction(QueryTunerRunAction.ID);
				manager.add(queryTunerRunAction);
				manager.add(new Separator());

				IAction createPhpCodeAction = ActionManager.getInstance().getAction(CreateSqlPhpCodeAction.ID);
				if (createPhpCodeAction != null) {
					manager.add(createPhpCodeAction);
				}

				IAction createJavaCodeAction = ActionManager.getInstance().getAction(CreateSqlJavaCodeAction.ID);
				if (createJavaCodeAction != null) {
					manager.add(createJavaCodeAction);
				}

				manager.add(new Separator());

				IAction reformatColumnsAliasAction = ActionManager.getInstance().getAction(
						ReformatColumnsAliasAction.ID);
				if (reformatColumnsAliasAction != null) {
					manager.add(reformatColumnsAliasAction);
				}
			}
		});

		Menu contextMenu = menuManager.createContextMenu(text);
		text.setMenu(contextMenu);
	}

	/**
	 * Add listener for text
	 */
	private void addListener() {
		text.addModifyListener(new ModifyListener() {
			IAction undoAction = ActionManager.getInstance().getAction(UndoAction.ID);
			IAction redoAction = ActionManager.getInstance().getAction(RedoAction.ID);
			public void modifyText(ModifyEvent event) {
				setDirty(true);

				if (!undoAction.isEnabled()) {
					FocusAction.changeActionStatus(undoAction, text);
				}

				if (!redoAction.isEnabled()) {
					FocusAction.changeActionStatus(redoAction, text);
				}

				ServerInfo serverInfo = queryEditor == null
						|| queryEditor.getSelectedServer() == null ? null
						: queryEditor.getSelectedServer().getServerInfo();
				boolean isLowerCase = QueryOptions.getKeywordLowercase(serverInfo);
				boolean isNoAutoUpperCase = QueryOptions.getNoAutoUppercaseKeyword(serverInfo);
				if (!isLowerCase && !isNoAutoUpperCase) {
					autoReplaceKeyword();
				}
			}

			// FIXME extract method?
			// replace keyword to upper case automatically
			public void autoReplaceKeyword() {
				if (pendingCompletionsListener) {
					return;
				}

				int pos = text.getCaretOffset() - 1;
				if (pos <= 0) {
					return;
				}

				String currentKey = text.getText(pos, pos);
				if (currentKey == null || currentKey.length() <= 0) {
					return;
				}

				char cur = currentKey.charAt(0);
				if (cur != ' ' && cur != '(' && cur != '\t' && cur != '\n'
						&& cur != '\r' && cur != ',') {
					return;
				}

				pos--;
				if (pos < 0) {
					return;
				}

				int spos = pos - 20;
				if (spos < 0) {
					spos = 0;
				}

				String txt = text.getText(spos, pos);
				spos = pos + 1;
				for (int i = txt.length() - 1; i >= 0; i--) {
					char c = txt.charAt(i);
					if (c == ' ' || c == '\t' || c == '\n' || c == '(') {
						break;
					}

					spos--;
				}

				int epos = pos;
				if (spos < 0 || epos < 0 || spos > epos) {
					return;
				}

				String currentKeyword = text.getText(spos, epos);
				if (currentKeyword == null) {
					return;
				}

				int len = currentKeyword.length();
				for (int i = 0; i < QuerySyntax.KEYWORDS_AUTO_UPPER.length; i++) {
					String keyword = QuerySyntax.KEYWORDS_AUTO_UPPER[i];
					if (keyword.equalsIgnoreCase(currentKeyword)) {
						pendingCompletionsListener = true;
						text.replaceTextRange(spos, len, keyword.toUpperCase());
						pendingCompletionsListener = false;
						break;
					}
				}
			}

		});
		text.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				if ((event.stateMask & SWT.SHIFT) != 0 && event.keyCode == SWT.TAB) {
					event.doit = false;
					return;
				}
				if (((event.stateMask & SWT.CTRL) != 0 || (event.stateMask & SWT.COMMAND) != 0)
						&& (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR)) {
					event.doit = false;
					return;
				}
				if (((event.stateMask & SWT.CTRL) != 0 || (event.stateMask & SWT.COMMAND) != 0)
						&& (event.stateMask & SWT.ALT) != 0) {
					event.doit = false;
					return;
				}
				if (event.keyCode == SWT.TAB) {
					event.doit = false;
				} else {
					event.doit = true;
				}
			}
		});
		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if ((event.stateMask & SWT.COMMAND) != 0) {//for Mac
					if ((event.stateMask & SWT.SHIFT) != 0) {
						if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
							queryEditor.runQuery(false);
							return;
						}
					}
				}

				if (event.keyCode == SWT.F5 || (event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'e') {
					queryEditor.runQuery(false);
				} else if (event.keyCode == SWT.F6 || (event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'l') {
					queryEditor.runQuery(true);
				} else if (event.keyCode == SWT.F7) {
					queryEditor.getCombinedQueryComposite().showQueryHistory();
				} else if(event.keyCode == SWT.F8) {
					queryEditor.runMultiQuery();
				} else if (event.keyCode == SWT.F1) {
					ActionManager.getInstance().getAction(HelpDocumentAction.ID).run();
				} else if (event.keyCode == SWT.F3) {
					if ((event.stateMask & SWT.SHIFT) == 0) {
						TextEditorFindReplaceMediator.findNext();
					} else {
						TextEditorFindReplaceMediator.findPrevious();
					}
				} else if (event.keyCode == SWT.F9) {
					queryEditor.setTuningModeButton(!queryEditor.isTuningModeButton());
				} else if (event.keyCode == SWT.F11) {
					queryEditor.getCombinedQueryComposite().rotateQueryPlanDisplayMode();
				} else if ((event.stateMask & SWT.CTRL) == 0
						&& (event.stateMask & SWT.SHIFT) == 0
						&& (event.stateMask & SWT.ALT) == 0
						&& event.keyCode == SWT.ESC) {
					int cursorOffset = text.getCaretOffset();
					text.setSelectionRange(cursorOffset, 0);
				} else if ((event.stateMask & SWT.CTRL) != 0
						&& (event.stateMask & SWT.SHIFT) == 0
						&& (event.stateMask & SWT.ALT) == 0
						&& event.keyCode == ',') {
					new QuickBuilderDialog(getShell(), SWT.NONE).open();
				} else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == ' ') {
					contentAssistant.showPossibleCompletions();
				} else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'r') {
					recentlyUsedSQLcontentAssistant.showPossibleCompletions();
				} else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'd') {
					deleteRow();
				} else if ((event.stateMask & SWT.CTRL) != 0 && (event.stateMask & SWT.ALT) != 0 && event.keyCode == SWT.ARROW_DOWN) {
					duplicateRow();
				} else if ((event.stateMask & SWT.CTRL) != 0 && (event.stateMask & SWT.SHIFT) != 0 && event.keyCode == 't') {
					//add sql tab
					queryEditor.addEditorTab();
				}else if (((event.stateMask & SWT.CTRL) != 0 && (event.stateMask & SWT.SHIFT) != 0) && (event.keyCode == 'f' || event.keyCode == 'F')) {
					format();
				} else if((event.stateMask & SWT.ALT) != 0 && event.keyCode == '/') {
					contentAssist();
				}
				else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'o') {
					// Open a SQL file...
					ActionManager.getInstance().getAction(QueryOpenAction.ID).run();
				} else if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 's') {
					// Save SQL to a file...
					try {
						save();
					} catch (IOException e) {
						MessageDialog.openError(getShell(), Messages.error, Messages.errCanNotSaveASQLFile);
					}
				} else if ((event.stateMask & SWT.CTRL) != 0 && (event.stateMask & SWT.SHIFT) == 0) {
					if (event.keyCode == '/') {
						inputComment(false, false);
					} else if (event.keyCode == 'z') {
						event.doit = false;
						undo();
					} else if (event.keyCode == 'y') {
						redo();
					} else if (event.keyCode == 'f' || event.keyCode == 'h') {
						find();
					} else if ((event.stateMask & SWT.ALT) != 0
							&& (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR)) { // NOPMD
						queryEditor.runQueryPlanInCursorLine();
					} else if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
						queryEditor.runQueryInCursorLine();
					} else if (event.keyCode == 'g') {
						gotoLine();
					}
				} else if ((event.stateMask & SWT.CTRL) != 0 && (event.stateMask & SWT.SHIFT) != 0) {
					if ((event.stateMask & SWT.ALT) != 0
							&& (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR)) { // NOPMD
						queryEditor.runQuery(true);
					} else if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
						queryEditor.runQuery(false);
					}else if (event.keyCode == 'x') {
						toUpperCase();
					}else if(event.keyCode == 'y') {
						toLowerCase();
					}
				} else if ((event.stateMask & SWT.SHIFT) == 0 && event.keyCode == SWT.TAB) {
					indent();
				} else if ((event.stateMask & SWT.SHIFT) != 0 && event.keyCode == SWT.TAB) {
					unindent();
				}

				if (SQLContentAssistProcessor.isShowProposal(event.character)) {
					contentAssistant.showPossibleCompletions();
					useCompletions = true;
				} else if ((event.character >= 'A' && event.character <= 'Z')
						|| (event.character >= 'a' && event.character <= 'z')) {
					if (useCompletions) {
						contentAssistant.showPossibleCompletions();
					}
					useCompletions = false;
				} else if (event.character == ' ' || event.character == '\t'
						|| event.keyCode == SWT.KEYPAD_CR
						|| event.keyCode == SWT.CR || event.keyCode == SWT.BS
						|| (text.getText().trim().length() < 1)) {
					useCompletions = true;
				}

				// ctrl + alt + 1 ~ 9 : change editor tab
				if ((event.stateMask & SWT.CTRL) != 0
						&& (event.stateMask & SWT.ALT) != 0
						&& (event.keyCode >= '1' && event.keyCode <= '9')) {
					IWorkbenchPage page = LayoutUtil.getActivePage();
					if (page != null) {
						int index = event.keyCode - '1';
						IEditorReference[] refs = page.getEditorReferences();
						for (int i = 0, selected = 0; i < refs.length; i++) {
							IEditorReference ref = refs[i];
							if (QueryEditorPart.ID.equals(ref.getId())) {
								if (index == selected) {
									IEditorPart part = ref.getEditor(true);
									page.activate(part);
								}
								selected++;
							}
						}
					}
				} else if ((event.stateMask & SWT.CTRL) != 0 && (event.keyCode >= '1' && event.keyCode <= '9')) {
					// ctrl + 1 ~ 9 : change middle tab
					ITabSelection selector = queryEditor.getCombinedQueryComposite();
					selector.select(event.keyCode - '1', -1);
				} else if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode >= '1' && event.keyCode <= '9')) {
					// alt + 1 ~ 9 : change bottom tab
					ITabSelection selector = queryEditor.getCombinedQueryComposite();
					selector.select(-1, event.keyCode - '1');
				}
			}

		});
		text.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 * @param event an event containing information about the selection
			 */
			public void widgetSelected(SelectionEvent event) {
				IAction copyAction = ActionManager.getInstance().getAction(CopyAction.ID);
				copyAction.setEnabled(false);
				IAction cutAction = ActionManager.getInstance().getAction(CutAction.ID);
				cutAction.setEnabled(false);
				IAction reformatColumnsAliasAction = ActionManager.getInstance().getAction(ReformatColumnsAliasAction.ID);
				reformatColumnsAliasAction.setEnabled(false);

				// show schema info view with a selected text
				IAction showSchemaAction = ActionManager.getInstance().getAction(ShowSchemaAction.ID);
				showSchemaAction.setEnabled(false);
				if (event.getSource() instanceof StyledText) {
					StyledText stext = (StyledText) event.getSource();
					if (stext != null && stext.getSelectionText() != null && stext.getSelectionText().length() > 0) {
						copyAction.setEnabled(true);
						cutAction.setEnabled(true);
						reformatColumnsAliasAction.setEnabled(true);
						CubridDatabase db = queryEditor.getSelectedDatabase();
						if (DatabaseNavigatorMenu.SELF_DATABASE_ID.equals(db.getId())
								&& ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
							showSchemaAction.setEnabled(false);
						} else {
							showSchemaAction.setEnabled(true);
						}
					}
				}
			}

		});

		text.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				ActionManager.getInstance().changeFocusProvider(text);
			}
		});

		TextEditorFindReplaceMediator editorDialogMediator = new TextEditorFindReplaceMediator();
		text.addFocusListener(editorDialogMediator);
	}

	private void duplicateRow() {
		int cursorOffset = text.getCaretOffset();
		int lineNumber = text.getLineAtOffset(cursorOffset);
		int firstCharOffset = text.getOffsetAtLine(lineNumber);

		String allTextOnEditor = text.getText();
		int lastCharOffset = allTextOnEditor.indexOf(text.getLineDelimiter(), firstCharOffset);
		if (lastCharOffset == -1) {
			if (allTextOnEditor.length() == 0) {
				return;
			}

			lastCharOffset = allTextOnEditor.length();
		}

		String row = allTextOnEditor.substring(firstCharOffset, lastCharOffset) + text.getLineDelimiter();
		text.getContent().replaceTextRange(firstCharOffset, 0, row);

		cursorOffset = text.getCaretOffset();
		lineNumber = text.getLineAtOffset(cursorOffset);
		firstCharOffset = text.getOffsetAtLine(lineNumber);
		text.setCaretOffset(firstCharOffset);
		text.setSelectionRange(firstCharOffset, row.length() - text.getLineDelimiter().length());

		text.setTopIndex(firstCharOffset);
		this.updateRuler();
	}

	private void deleteRow() {
		int cursorOffset = text.getCaretOffset();
		int lineNumber = text.getLineAtOffset(cursorOffset);
		int firstCharOffset = text.getOffsetAtLine(lineNumber);

		String allTextOnEditor = text.getText();
		int replaceLength = 0;
		int lastCharOffset = allTextOnEditor.indexOf(text.getLineDelimiter(), firstCharOffset);
		if (lastCharOffset == -1) {
			if (firstCharOffset > 0) {
				replaceLength = allTextOnEditor.length() - firstCharOffset + 1;
				firstCharOffset--;
			} else {
				replaceLength = allTextOnEditor.length() - firstCharOffset;
			}
		} else {
			replaceLength = lastCharOffset - firstCharOffset + text.getLineDelimiter().length();
		}
		text.getContent().replaceTextRange(firstCharOffset, replaceLength, "");

		cursorOffset = text.getCaretOffset();
		lineNumber = text.getLineAtOffset(cursorOffset);
		firstCharOffset = text.getOffsetAtLine(lineNumber);
		text.setCaretOffset(firstCharOffset);
	}

	public void undo() {
		undoManager.undo();
	}

	public void redo() {
		undoManager.redo();
	}

	public StyledText getText() {
		return text;
	}

	public SourceViewer getTextViewer() {
		return sqlTextViewer;
	}

	public boolean hasQueryString() {
		return StringUtil.isNotEmpty(sqlTextViewer.getTextWidget().getText());
	}

	private void gotoLine() {
		IAction action = ActionManager.getInstance().getAction(GotoLineAction.ID);
		if (action != null) {
			action.run();
		}
	}

	/**
	 * Move a scroll to bottom of the query editor pane.
	 */
	public void gotoButtom() {
		int lineCount = getText().getContent().getLineCount() - 1;
		if (lineCount <= 0) {
			return;
		}
		int offset = getText().getContent().getOffsetAtLine(lineCount);
		getText().setCaretOffset(offset);
		getText().setTopIndex(lineCount);
		getText().setFocus();
		updateRuler();
	}

	/**
	 * Find the text
	 *
	 * @param strFind Find String
	 * @param curOffsetStart Start offset
	 * @param isWrap Return at end of stream
	 * @param isUp reverse search
	 * @param isCaseSensitive Case sensitive
	 * @param isWholeWord Whole word
	 * @return boolean Success : true, Nothing or faild : false
	 */
	public boolean txtFind(String strFind, int curOffsetStart, boolean isWrap,
			boolean isUp, boolean isCaseSensitive, boolean isWholeWord) {
		IRegion region = null;
		int offsetStart = curOffsetStart;
		try {
			if (offsetStart < 0) {
				Point pt = sqlTextViewer.getSelectedRange();

				// Get the current offset
				offsetStart = pt.x + pt.y;

				// If something is currently selected, and they're searching
				// backwards,
				// move offset to beginning of selection. Otherwise, repeated
				// backwards
				// finds will only find the same text
				if (isUp && pt.x != pt.y) {
					offsetStart = pt.x - 1;
				}
			}

			// Perform the find
			region = findReplaceDocAdapter.find(offsetStart, strFind, !isUp,
					isCaseSensitive, isWholeWord, false /* regex */);
		} catch (BadLocationException event) {
			LOGGER.error("", event);
		}

		// Update the viewer with found selection
		if (region == null) {
			if (isWrap) {
				return txtFind(strFind,
						isUp ? findReplaceDocAdapter.length() - 1 : 0, false,
						isUp, isCaseSensitive, isWholeWord);
			} else {
				return false;
			}
		} else {
			sqlTextViewer.setSelectedRange(region.getOffset(),
					region.getLength());
			sqlTextViewer.revealRange(region.getOffset(), region.getLength());
			return true;
		}
	}

	public void toUpperCase() {
		int curOffsetStart = sqlTextViewer.getSelectedRange().x;
		int curOffsetLength = sqlTextViewer.getSelectedRange().y;

		if (curOffsetLength > 0) {
			String content = text.getSelectionText();
			text.replaceTextRange(curOffsetStart, curOffsetLength, content.toUpperCase());
		}
	}

	public void toLowerCase() {
		int curOffsetStart = sqlTextViewer.getSelectedRange().x;
		int curOffsetLength = sqlTextViewer.getSelectedRange().y;

		if (curOffsetLength > 0) {
			String content = text.getSelectionText();
			text.replaceTextRange(curOffsetStart, curOffsetLength, content.toLowerCase());
		}
	}

	/**
	 * replace all of text
	 *
	 * @param strFind String
	 * @param strReplace String
	 * @param isCaseSensitive boolean
	 * @return cnt
	 */
	public int txtReplaceAll(String strFind, String strReplace, boolean isCaseSensitive) {
		int cnt = 0;
		int curOffsetStart = 0;

		while (txtFind(strFind, curOffsetStart, false, false, isCaseSensitive, isWholeWord)) {
			curOffsetStart = sqlTextViewer.getSelectedRange().x;
			text.replaceTextRange(curOffsetStart, sqlTextViewer.getSelectedRange().y, strReplace);
			curOffsetStart += strReplace.length();
			cnt++;
			// There's no sense why it doesn't warp search
			// if ((curOffsetStart + strReplace.length()) > frda.length())
			// break;
		}

		return cnt;
	}

	/**
	 * get the selected query
	 *
	 * @return query
	 */
	public String getSelectedQueries() {
		if (text.getSelectionCount() > 0) {
			return text.getSelectionText();
		}

		return text.getText();
	}

	/**
	 * Get all queries
	 *
	 * @return
	 */
	public String getAllQueries() {
		return text.getText();
	}

	/**
	 * set the query
	 *
	 * @param query String
	 */
	public void setQueries(String query) {
		text.setText(query);
		setDirty(false);
	}

	/**
	 * release the object
	 */
	public void release() {
		if (document != null) {
			document.clear();
			document = null;
		}
	}

	/**
	 * find key in source editor
	 */
	public void find() {
		TextEditorFindReplaceMediator.openFindReplaceDialog();
	}

	/**
	 * change current line sql script to comment
	 */
	public void comment() {
		inputComment(true, true);
	}

	/**
	 * change comment to current line sql script
	 */
	public void uncomment() {
		inputComment(true, false);
	}

	/**
	 * remove tab in script
	 */
	public void unindent() {
		removeTab();
	}

	/**
	 * insert tab in script
	 */
	public void indent() {
		inputTab();
	}

	void inputComment(boolean isForce, boolean isComment) {
		boolean isComments = isComment;
		int startOffset = text.getSelection().x;
		int endOffset = text.getSelection().y;

		int startLine = text.getLineAtOffset(startOffset);
		int endLine = text.getLineAtOffset(endOffset);

		if (text.getSelectionText().endsWith(StringUtil.NEWLINE)) {
			endLine--;
		}

		int currLineOffset;

		if (!isForce) {
			isComments = false; // if isComment == true, adding comment

			for (int i = startLine; i <= endLine; i++) {
				currLineOffset = text.getOffsetAtLine(i);
				if (!text.getText().substring(currLineOffset).trim().startsWith("--")) {
					isComments |= true;
				}
			}
		}

		if (startOffset == endOffset) {
			currLineOffset = text.getOffsetAtLine(startLine);
			if (isComments) {
				text.replaceTextRange(currLineOffset, 0, "--");
			} else {
				int lineStartOffset = text.getOffsetAtLine(startLine);
				if ((lineStartOffset + 2 <= text.getText().length())
						&& (text.getText().substring(lineStartOffset, lineStartOffset + 2).equals("--"))) {
					currLineOffset = text.getText().indexOf("--", currLineOffset);
					text.replaceTextRange(currLineOffset, 2, "");
				}
			}
		} else {
			if (isComments) {
				for (int i = startLine; i <= endLine; i++) {
					currLineOffset = text.getOffsetAtLine(i);
					text.replaceTextRange(currLineOffset, 0, "--");
				}
				startOffset += 2;
				endOffset += (endLine - startLine + 1) * 2;
			} else {
				for (int i = startLine; i <= endLine; i++) {
					int lineStartOffset = text.getOffsetAtLine(i);

					if ((lineStartOffset + 2 <= text.getText().length())
							&& (text.getText().substring(lineStartOffset, lineStartOffset + 2).equals("--"))) {
						currLineOffset = text.getText().indexOf("--", text.getOffsetAtLine(i));
						text.replaceTextRange(currLineOffset, 2, "");
						if (i == startLine) {
							startOffset -= 2;
						}
						endOffset -= 2;
					}
				}
			}
			text.setSelection(startOffset, endOffset);
		}
	}

	public void inputTab() {
		int startOffset = text.getSelection().x;
		int endOffset = text.getSelection().y;

		int startLine = text.getLineAtOffset(startOffset);
		int endLine = text.getLineAtOffset(endOffset);

		if (endLine > startLine) {
			if (text.getSelectionText().endsWith(StringUtil.NEWLINE)) {
				endLine--;
			}
			for (int i = startLine; i <= endLine; i++) {
				text.replaceTextRange(text.getOffsetAtLine(i), 0, "\t");
			}
			startOffset++;
			endOffset += (endLine - startLine + 1);
		} else if (text.getSelectionCount() > 0) {
			text.replaceTextRange(startOffset, text.getSelectionCount(), "\t");
			startOffset++;
			endOffset = startOffset;
		} else {
			text.insert("\t");
			startOffset++;
			endOffset++;
		}
		text.setSelection(startOffset, endOffset);
	}

	public void removeTab() {
		int startOffset = text.getSelection().x;
		int endOffset = text.getSelection().y;

		int startLine = text.getLineAtOffset(startOffset);
		int endLine = text.getLineAtOffset(endOffset);

		if (endLine > startLine) {
			if (text.getSelectionText().endsWith(StringUtil.NEWLINE)) {
				endLine--;
			}
			if (text.getText().substring(text.getOffsetAtLine(startLine)).startsWith("\t")) {
				startOffset--;
			}
			int offset = 0;
			for (int i = startLine; i <= endLine; i++) {
				if (!text.getText().substring(text.getOffsetAtLine(i)).startsWith("\t")) {
					continue;
				}
				text.replaceTextRange(text.getText().indexOf("\t", text.getOffsetAtLine(i)), 1, "");
				offset++;
			}
			endOffset -= offset;
		} else if (text.getSelectionCount() > 0) {
			int startVal = startOffset;
			if (startOffset > 0) {
				String str = text.getText().substring(startOffset - 1, startOffset);
				if (str != null && str.equals("\t")) {
					text.replaceTextRange(startOffset - 1, 1, "");
					startOffset--;
					endOffset--;
				}
			}
			if (startVal == startOffset && text.getText().length() > startOffset) {
				String str = text.getText().substring(startOffset, startOffset + 1);
				if (str != null && str.equals("\t")) {
					text.replaceTextRange(startOffset, 1, "");
					endOffset--;
				}
			}
		} else {
			if (endOffset >= 1) {
				String str = text.getText().substring(endOffset - 1, endOffset);
				if ("\t".equals(str)) {
					text.replaceTextRange(endOffset - 1, 1, "");
					startOffset--;
					endOffset--;
				}
			}
		}
		text.setSelection(startOffset, endOffset);
	}

	/**
	 * format script
	 */
	public void format() {
		try {
			formatHandler.execute(null);
		} catch (Exception ex) {
			CommonUITool.openErrorBox(ex.getMessage());
		}
	}

	public void contentAssist() {
		try {
			contentAssistHandler.execute(null);
		} catch (Exception ex) {
			CommonUITool.openErrorBox(ex.getMessage());
		}
	}

	/**
	 * save the content of querySourceView
	 *
	 * @return boolean
	 * @throws IOException if failed
	 */
	public boolean save() throws IOException {
		if (filepath == null) {
			doSaveAs();
			return false;
		}

		File file = new File(filepath);
		if (file == null || !file.exists()) {
			SetFileEncodingDialog dialog = new SetFileEncodingDialog(getShell(), document.getEncoding(), false);
			if (IDialogConstants.OK_ID == dialog.open()) {
				document.setEncoding(dialog.getEncoding());
				filepath = dialog.getFilePath();
				document.setFileName(filepath);
			} else {
				return false;
			}
		} else {
			document.setEncoding(document.getEncoding());
			document.setFileName(filepath);
		}
		document.save();
		dirty = false;
		updateTabName(filepath);
		return true;
	}

	/**
	 * Save as the file content
	 *
	 * @throws IOException if failed
	 */
	public void doSaveAs() throws IOException {
		SetFileEncodingDialog dialog = new SetFileEncodingDialog(getShell(), document.getEncoding(), false, filepath);
		if (IDialogConstants.OK_ID == dialog.open()) {
			if (StringUtil.isEqual(filepath, dialog.getFilePath())) {
				if (!CommonUITool.openConfirmBox(Messages.msgConfirmEditorExistFile)) {
					return;
				}
			}

			document.setEncoding(dialog.getEncoding());
			filepath = dialog.getFilePath();
			document.setFileName(filepath);
			document.save();
			dirty = false;

			updateTabName(filepath);
		}
	}

	/**
	 * Open the file
	 *
	 * @param filePath String
	 * @param encoding String
	 * @throws IOException if failed
	 */
	public void open(String filepath, String encoding) throws IOException {
		document.setFileName(filepath);
		document.setEncoding(encoding);
		document.open();
		dirty = false;
		this.filepath = filepath;
		updateTabName(filepath);
	}

	/**
	 * Open a file which
	 */
	public void doOpen() throws IOException {
		if (dirty && !CommonUITool.openConfirmBox(Messages.msgConfirmEditorNotSaved)) {
			return;
		}
		String charset = document.getEncoding();
		SetFileEncodingDialog dialog = new SetFileEncodingDialog(getShell(), charset, true);
		if (IDialogConstants.OK_ID == dialog.open()) {
			open(dialog.getFilePath(), dialog.getEncoding());
		}
	}

	public SQLDocument getDocument() {
		return document;
	}

	public void setBackground(Color color) {
		text.setBackground(color);
	}

	public void setSelected(int offset, int length) {
		sqlTextViewer.setSelectedRange(offset, length);
		sqlTextViewer.setTopIndex(offset + length);
		setFocus();
		text.setFocus();
	}

	public void forcusCursor(int offset, int length) {
		sqlTextViewer.setSelectedRange(offset, length - 3); // it should be positioned on ';' behind
		int pos = offset + length;
		sqlTextViewer.setTopIndex(pos);
		setFocus();
		text.setFocus();
	}

	public SQLTextViewer getSqlTextViewer() {
		return sqlTextViewer;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		if (document != null) {
			updateTabName(document.getFileName());
		} else {
			queryEditor.updateTabName(editorTabItem, dirty);
		}
	}

	public String getCharset() {
		return document.getEncoding();
	}

	public void setCharset(String charset) {
		if (document != null) {
			document.setEncoding(charset);
		}
	}

	public boolean isFileOpened() {
		return StringUtil.isNotEmpty(document.getFileName());
	}


	/**
	 * Change the editor tab name using the file name.
	 */
	private void updateTabName(String filepath) {
		String filename = null;
		if (filepath != null) {
			int sp = filepath.lastIndexOf(File.separator);
			if (sp != -1) {
				filename = filepath.substring(sp + 1).trim();
			}
		}
		if (filename != null) {
			queryEditor.updateTabName(editorTabItem, filename, dirty);
		} else {
			queryEditor.updateTabName(editorTabItem, dirty);
		}
	}
}
