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
package com.cubrid.common.ui.query.editor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QuerySyntax;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.preference.GeneralPreference;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.jface.text.contentassist.CompletionProposal;
import com.cubrid.common.ui.query.control.jface.text.contentassist.ContextInformationValidator;
import com.cubrid.common.ui.query.control.jface.text.contentassist.ICompletionProposal;
import com.cubrid.common.ui.query.control.jface.text.contentassist.IContentAssistProcessor;
import com.cubrid.common.ui.query.control.jface.text.contentassist.IContextInformation;
import com.cubrid.common.ui.query.control.jface.text.contentassist.IContextInformationValidator;
import com.cubrid.common.ui.spi.persist.RecentlyUsedSQLContentPersistUtils;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * This class manages Cubrid KeyWord Content Assist Processor
 *
 * @author wangsl
 * @version 1.0 - 2009-06-24 created by wangsl
 * @version 8.4.1 - 2012-04 modify by fulei
 */
public class SQLContentAssistProcessor implements IContentAssistProcessor {
	private static final Logger LOGGER = LogUtil.getLogger(SQLContentAssistProcessor.class);
	private String lastError = null;
	private final IContextInformationValidator contextInfoValidator;
	private final WordTracker wordTracker;
	private final IDatabaseProvider databaseProvider;
	private boolean isTableLowercase = false;
	private boolean running = false;
	private List<String> tableNames = null;
	private Map<String, List<ColumnProposalDetailInfo>> columns = null;
	private String currentIpAndDb = null;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
		CubridManagerCorePlugin.getDefault().setSQLCodeAutocompletionMode(running);
	}

	public boolean isTableLowercase() {
		return isTableLowercase;
	}

	public void setTableLowercase(boolean isTableLowercase) {
		this.isTableLowercase = isTableLowercase;
	}

	public SQLContentAssistProcessor(WordTracker tracker, IDatabaseProvider databaseProvider) {
		super();
		contextInfoValidator = new ContextInformationValidator(this);
		wordTracker = tracker;
		this.databaseProvider = databaseProvider;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		int currOffset = offset - 1;
		try {
			String currWord = "";
			char currChar;
			while (currOffset >= 0
					&& !isKeywordSeparator(currChar = document.getChar(currOffset))) {
				currWord = currChar + currWord;
				currOffset--;
			}

			currWord = trimQualifier(currWord);

			if (currWord.trim().length() == 0
					&& isSQLEndPos(document, currOffset)) {
				return null;
			}

			// if currWord is "" or AssistProcessor is displaying return null;
			if (currWord.trim().equals("") && isRunning()) {
				return null;
			}
			setRunning(true);
			List<ICompletionProposal> finalProposals = new ArrayList<ICompletionProposal>();

			if (GeneralPreference.isAutoCompleteTablesOrColumns()) {
				// columns
				List<ColumnProposalDetailInfo> tableColumns = getColumnNames(
						currWord.toUpperCase(Locale.getDefault()), document,
						offset - 1);
				List<ICompletionProposal> columnProposals = buildColumnProposals(
						tableColumns,
						currWord,
						offset - currWord.length(),
						CommonUIPlugin.getImage("icons/navigator/table_column_item.png"),
						false);
				finalProposals.addAll(columnProposals);

				// tables
				List<String> tableNames = getTableNames(
						currWord.toUpperCase(Locale.getDefault()), document,
						offset - 1);
				List<ICompletionProposal> tableProposals = buildProposals(
						tableNames,
						currWord,
						offset - currWord.length(),
						CommonUIPlugin.getImage("icons/navigator/schema_table_item.png"),
						false);
				finalProposals.addAll(tableProposals);
			}

			// keywords
			if (GeneralPreference.isAutoCompleteKeyword()) {
				List<String> suggestions = wordTracker.suggest(
						currWord.toUpperCase(Locale.getDefault()), databaseProvider.getDatabaseInfo());
				List<ICompletionProposal> keywordProposals = buildProposals(
						suggestions, currWord, offset - currWord.length(),
						CommonUIPlugin.getImage("icons/navigator/sql.png"),
						true);
				finalProposals.addAll(keywordProposals);
			}

			return finalProposals.toArray(new ICompletionProposal[finalProposals.size()]);
		} catch (BadLocationException e) {
			LOGGER.error("", e);
			lastError = e.getMessage();
			return null;
		} finally {
			setRunning(false);
		}
	}

	/**
	 * Return whether the previous character is keyword separator
	 *
	 * @param ch char
	 * @return boolean
	 */
	private boolean isKeywordSeparator(char ch) {
		boolean isSeparator = (Character.isWhitespace(ch)
				|| ch == ICharacterScanner.EOF || ch == ' ' || ch == '('
				|| ch == '\r' || ch == '\n' || ch == '\t' || ch == ';'
				|| ch == ',' || ch == ')');
		return isSeparator;
	}

	/**
	 * build the column proposals with image
	 *
	 * @param tableColumns List<TableColumn>
	 * @param replacedWord String
	 * @param offset int
	 * @param Image image
	 * @return proposals
	 */
	private List<ICompletionProposal> buildColumnProposals(
			List<ColumnProposalDetailInfo> tableColumns, String replacedWord,
			int offset, Image image, boolean showSecondProposals) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (ColumnProposalDetailInfo column : tableColumns) {
			String columnName = column.getColumnName();
			String currSuggestion = columnName;
			String displayString = columnName;
			if (replacedWord.indexOf(".") > 0) {
				String[] words = replacedWord.split("\\.");
				currSuggestion = words[0] + "." + column.getColumnName();
			}

			if (checkShowSecondProposals(showSecondProposals, column.getColumnName())) {
				proposals.add(new CompletionProposal(
						currSuggestion,
						offset,
						replacedWord.length(),
						currSuggestion.length(),
						CommonUIPlugin.getImage("icons/queryeditor/arrow_right.png"),
						displayString, null, column.getColumnAdditionalInfo()));
			} else {
				proposals.add(new CompletionProposal(currSuggestion, offset,
						replacedWord.length(), currSuggestion.length(), image,
						displayString, null, "<pre>" + column.getColumnAdditionalInfo() + "</pre>"));
			}
		}

		return proposals;
	}

	/**
	 * Build the proposals with image
	 *
	 * @param names List
	 * @param replacedWord String
	 * @param offset int
	 * @param Image image
	 * @return proposals
	 */
	private List<ICompletionProposal> buildProposals(List<String> names,
			String replacedWord, int offset, Image image,
			boolean showSecondProposals) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (String name : names) {
			String currSuggestion = name;
			String displayString = name;
			if (checkShowSecondProposals(showSecondProposals, name)) {
				// content += SECONDPROPOSALSUFFIX;
				proposals.add(new CompletionProposal(
						currSuggestion,
						offset,
						replacedWord.length(),
						currSuggestion.length(),
						CommonUIPlugin.getImage("icons/queryeditor/arrow_right.png"),
						displayString, null, null));
			} else {
				if (QuerySyntax.getKeywordContent(name) == null) {
					proposals.add(new CompletionProposal(currSuggestion,
							offset, replacedWord.length(),
							currSuggestion.length(), image, displayString,
							null, null));
				} else {
					// show help
					proposals.add(new CompletionProposal(currSuggestion,
							offset, replacedWord.length(),
							currSuggestion.length(), image, null, null, "<pre>"
									+ QuerySyntax.getKeywordContent(name)
									+ "</pre>"));
				}
			}
		}

		return proposals;
	}

	/**
	 * check whether display showSecondProposal symbol
	 *
	 * @param showSecondProposals
	 * @param name
	 * @return
	 */
	private boolean checkShowSecondProposals(boolean showSecondProposals, String name) {
		if (!showSecondProposals) {
			return false;
		}
		name = name.trim();
		boolean flag = false;
		for (String statement : RecentlyUsedSQLContentPersistUtils.SUPPORTSQL) {
			String upper = statement.toUpperCase();
			String lower = statement.toLowerCase();
			if (name.equals(upper) || name.equals(lower)) {
				flag = true;
				break;
			}
		}
		// if name is not a SUPPORTSQL return false;
		if (!flag) {
			return false;
		}
		// if no recently user sql return false;
		LinkedList<String> list = (RecentlyUsedSQLContentPersistUtils.getRecentlyUsedSQLContentsById(databaseProvider.getDatabase()));
		if (list.size() == 0) {
			return false;
		}
		for (String sql : list) {
			if (StringUtil.startsWithIgnoreCase(name, sql)) {
				return true;
			}
		}
		return false;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		lastError = Messages.noContext;
		return new IContextInformation[]{};
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[]{};
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[]{};
	}

	public String getErrorMessage() {
		return lastError;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return contextInfoValidator;
	}

	/**
	 * if after "FROM" return all table names
	 *
	 * @param document IDocument
	 * @param currOffset an offset within the document for which completions
	 *        should be computed
	 * @param currWord String
	 * @return table names
	 */
	@SuppressWarnings("unused")
	private List<String> getTableNames(String currWord, IDocument document,
			int currOffset) { // FIXME move this logic to core module
		String beforWord = "";
		String afterWord = "";
		List<String> result = new ArrayList<String>();
		char beforeCurrChar;
		char afterCurrChar;
		int beforeOffset = currOffset;
		int afterOffset = currOffset + 1;

		try {
			// get before word
			while (isKeywordSeparator(beforeCurrChar = document.getChar(beforeOffset))) {
				beforeOffset--;
			}
			while (beforeOffset >= 0) {
				beforeCurrChar = document.getChar(beforeOffset);
				beforWord = Character.toUpperCase(beforeCurrChar) + beforWord;
				beforeOffset--;
			}
			// get after word
			while (afterOffset < document.getLength()
					&& isKeywordSeparator(afterCurrChar = document.getChar(afterOffset))) {
				afterOffset++;
			}
			while (afterOffset < document.getLength()) {
				afterCurrChar = document.getChar(afterOffset);
				afterWord += Character.toUpperCase(afterCurrChar);
				afterOffset++;
			}

			if (beforWord.indexOf("FROM") > -1
					|| beforWord.indexOf("UPDATE") > -1
					|| beforWord.indexOf("DELETE") > -1
					|| beforWord.indexOf("INSERT INTO") > -1) {
				if ("".equals(currWord)) {
					result.addAll(getAllTableNames());
				} else {
					for (String tableName : getAllTableNames()) {
						if (tableName.startsWith(isTableLowercase() ? currWord.toLowerCase(Locale.getDefault())
								: currWord)) {
							result.add(tableName);
						}
					}
				}

			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}

		return result;
	}

	private void loadAllTableColumnsMap() {
		if (databaseProvider.getDatabaseInfo() == null){
			return;
		}

		final DatabaseInfo dbInfo = databaseProvider.getDatabaseInfo();
		ColumnProposal proposal = ColumnProposalAdvisor.getInstance().findProposal(dbInfo);

		/*The proposal haven't loaded*/
		if(proposal == null) {
			return;
		}
		tableNames = proposal.getTableNames();
		columns = proposal.getColumns();
	}

	/**
	 * return a list of table name
	 *
	 * @return List<String>
	 * @throws SQLException e
	 */
	protected List<String> getAllTableNames() throws SQLException { // FIXME move this logic to core module
		try {
			String dbname = databaseProvider.getDatabaseInfo().getDbName();
			String ip = databaseProvider.getDatabaseInfo().getBrokerIP();
			String curDbKey = dbname + "@" + ip;
			if (!curDbKey.equals(currentIpAndDb)) {
				currentIpAndDb = curDbKey;
				cleanSchemaInfosForAutocompletion();
			}
		} catch (Exception e) {
		}

		loadAllTableColumnsMap();

		List<String> list = new ArrayList<String>();
		if(tableNames != null) {
			for (String tname : tableNames) {
				if (Character.isLowerCase(tname.charAt(0))) {
					this.setTableLowercase(true);
				}
				list.add(tname);
			}
		}

		return list;
	}

	/**
	 * get columns which table behind "from"
	 *
	 * @param currWord String
	 * @param document IDocument
	 * @param currOffset an offset within the document for which completions
	 *        should be computed
	 * @return tablenames List<ColumnProposalDetailInfo>
	 */
	private List<ColumnProposalDetailInfo> getColumnNames(String currWord,
			IDocument document, int currOffset) { // FIXME move this logic to core module
		String beforWord = "";
		String afterWord = "";
		List<ColumnProposalDetailInfo> result = new ArrayList<ColumnProposalDetailInfo>();
		char beforeCurrChar;
		char afterCurrChar;
		int beforeOffset = currOffset;
		int afterOffset = currOffset + 1;

		try {

			// get before word
			while (isKeywordSeparator(beforeCurrChar = document.getChar(beforeOffset))) {
				beforeOffset--;
			}
			while (beforeOffset >= 0) {
				beforeCurrChar = document.getChar(beforeOffset);
				// get current sql ;if there has ";" it's the previous sql
				if (beforeCurrChar == ';') {
					break;
				}
				beforWord = Character.toUpperCase(beforeCurrChar) + beforWord;
				beforeOffset--;
			}
			// get after word
			while (afterOffset < document.getLength()
					&& isKeywordSeparator(afterCurrChar = document.getChar(afterOffset))) {
				afterOffset++;
			}
			while (afterOffset < document.getLength()) {
				afterCurrChar = document.getChar(afterOffset);
				// get current sql ;if there has ";" it's the next sql
				if (afterCurrChar == ';') {
					break;
				}
				afterWord += Character.toUpperCase(afterCurrChar);
				afterOffset++;
			}

			String currTableName = null;
			String currColumnName = currWord;
			String[] objArr = currWord.split("\\.");
			if (currWord.endsWith(".")) {
				String[] newObjArr = new String[objArr.length + 1];
				System.arraycopy(objArr, 0, newObjArr, 0, objArr.length);
				newObjArr[objArr.length] = "";
				objArr = newObjArr;
			}
			if (objArr.length == 1) {
				currColumnName = trimQualifier(objArr[0]);
			} else if (objArr.length == 2) {
				currTableName = trimQualifier(objArr[0]);
				currColumnName = trimQualifier(objArr[1]);
			} else {
				return result;
			}

			if (beforWord.indexOf("SELECT") > -1
					&& beforWord.indexOf("FROM") > -1
					&& beforWord.indexOf("WHERE") > -1) { // SELECT WHERE
															// STATMENT
				// get table name
				String tableName = beforWord.substring(
						beforWord.indexOf("FROM") + 4,
						beforWord.indexOf("WHERE")).trim();

				return getColumnNames(currTableName, currColumnName, tableName);

			} else if (beforWord.indexOf("SELECT") > -1
					&& afterWord.indexOf("FROM") > -1) { // SELECT STATMENT
				char tableCurrChar;
				String tableName = "";
				// "from" tableOffset
				int tableOffset = afterWord.indexOf("FROM") + 4;

				if (afterWord.indexOf("WHERE") > tableOffset) {
					tableName = afterWord.substring(tableOffset,
							afterWord.indexOf("WHERE")).trim();
				} else if (afterWord.indexOf("GROUP") > tableOffset) {
					tableName = afterWord.substring(tableOffset,
							afterWord.indexOf("GROUP")).trim();
				} else if (afterWord.indexOf("ORDER") > tableOffset) {
					tableName = afterWord.substring(tableOffset,
							afterWord.indexOf("ORDER")).trim();
				} else {
					// move offset to the first word after "FROM"
					while (tableOffset < afterWord.length()
							&& isKeywordSeparator(tableCurrChar = afterWord.charAt(tableOffset))) {
						tableOffset++;
					}
					// get table name
					while (tableOffset < afterWord.length()
							&& !isKeywordSeparator(afterWord.charAt(tableOffset))) {
						tableCurrChar = afterWord.charAt(tableOffset);
						tableName += Character.toUpperCase(tableCurrChar);
						tableOffset++;
					}
				}

				return getColumnNames(currTableName, currColumnName, tableName);

			} else if (beforWord.indexOf("INSERT INTO") > -1
					&& afterWord.indexOf("VALUES") > -1) { // INSERT STATMENT
				char tableCurrChar;
				String tableName = "";
				int tableOffset = beforWord.indexOf("INTO") + 4;
				// move offset to the first word after "INSERT INTO"
				while (tableOffset < beforWord.length()
						&& isKeywordSeparator(tableCurrChar = beforWord.charAt(tableOffset))) {
					tableOffset++;
				}
				// get table name
				while (tableOffset < beforWord.length()
						&& !isKeywordSeparator(beforWord.charAt(tableOffset))) {
					tableCurrChar = beforWord.charAt(tableOffset);
					tableName += Character.toUpperCase(tableCurrChar);
					tableOffset++;
				}
				tableName = trimQualifier(tableName);
				if (tableName.trim().length() > 0) {
					for (ColumnProposalDetailInfo column : getTableColumns(tableName.trim())) {
						String columnName = column.getColumnName();
						if (columnName.startsWith(isTableLowercase() ? currWord.toLowerCase(Locale.getDefault())
								: currWord)) {
							result.add(column);
						}
					}
				}
			} else if (beforWord.indexOf("UPDATE") > -1
					&& beforWord.indexOf("SET") > -1
					&& beforWord.indexOf("WHERE") > -1) { // UPDATE WHERE
															// STATMENT
				String tableName = "";
				// get table name
				tableName = beforWord.substring(
						beforWord.indexOf("UPDATE") + 6,
						beforWord.indexOf("SET")).trim();
				tableName = trimQualifier(tableName);
				if (tableName.trim().length() > 0) {
					for (ColumnProposalDetailInfo column : getTableColumns(tableName.trim())) {
						String columnName = column.getColumnName();
						if (columnName.startsWith(isTableLowercase() ? currWord.toLowerCase(Locale.getDefault())
								: currWord)) {
							result.add(column);
						}
					}
				}
			} else if (beforWord.indexOf("UPDATE") > -1
					&& beforWord.indexOf("SET") > -1) { // UPDATE STATMENT
				String tableName = beforWord.substring(
						beforWord.indexOf("UPDATE") + 6,
						beforWord.indexOf("SET"));
				tableName = trimQualifier(tableName);
				if (tableName.trim().length() > 0) {
					for (ColumnProposalDetailInfo column : getTableColumns(tableName.trim())) {
						String columnName = column.getColumnName();
						if (columnName.startsWith(isTableLowercase() ? currWord.toLowerCase(Locale.getDefault())
								: currWord)) {
							result.add(column);
						}
					}
				}
			} else if (beforWord.indexOf("DELETE FROM") > -1
					&& beforWord.indexOf("WHERE") > -1) { // DELETE STATMENT
				String tableName = beforWord.substring(
						beforWord.indexOf("DELETE FROM") + 11,
						beforWord.indexOf("WHERE"));
				tableName = trimQualifier(tableName);
				if (tableName.trim().length() > 0) {
					for (ColumnProposalDetailInfo column : getTableColumns(tableName.trim())) {
						String columnName = column.getColumnName();
						if (columnName.startsWith(isTableLowercase() ? currWord.toLowerCase(Locale.getDefault())
								: currWord)) {
							result.add(column);
						}
					}
				}
			} else if (currTableName != null
					&& currTableName.trim().length() > 0) {
				String realTableName = trimQualifier(currTableName);
				for (ColumnProposalDetailInfo column : getTableColumns(realTableName.trim())) {
					String columnName = column.getColumnName();
					if (currColumnName == null || currColumnName.length() == 0) {
						result.add(column);
					} else if (columnName.startsWith(isTableLowercase() ? currColumnName.toLowerCase(Locale.getDefault())
							: currColumnName)) {
						result.add(column);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return result;
	}

	/**
	 * Return whether this is SQL end position
	 *
	 * @param document IDocument
	 * @param currOffset int
	 * @return boolean
	 */
	private boolean isSQLEndPos(IDocument document, int currOffset) { // FIXME move this logic to core module
		int beforeOffset = currOffset;
		char beforeCurrChar;
		// get before word
		try {
			while (beforeOffset >= 0) {
				beforeCurrChar = document.getChar(beforeOffset);
				if (String.valueOf(beforeCurrChar).matches("\\s")) {
					beforeOffset--;
					continue;
				} else if (beforeCurrChar == ';') {
					return true;
				} else {
					return false;
				}
			}
		} catch (BadLocationException e) {
			LOGGER.error("", e);
		}

		return false;
	}

	/**
	 * Get the column names
	 *
	 * @param currTableName String
	 * @param currColumnName String
	 * @param fromTableNames String
	 * @return List<ColumnProposalDetailInfo>
	 * @throws SQLException The exception
	 */
	private List<ColumnProposalDetailInfo> getColumnNames(String currTableName,
			String currColumnName, String fromTableNames) throws SQLException { // FIXME move this logic to core module
		List<ColumnProposalDetailInfo> result = new ArrayList<ColumnProposalDetailInfo>();
		if (fromTableNames == null || fromTableNames.trim().length() == 0) {
			if (currTableName == null || currTableName.trim().length() == 0) {
				return result;
			}
			fromTableNames = currTableName;
		}
		boolean isHaveCurrTableName = false;
		String[] tableNames = fromTableNames.split(",");
		for (String name : tableNames) {
			String[] aliasNames = name.trim().split("\\s+");
			String realTableName = aliasNames[0];
			String aliasTableName = "";
			if (aliasNames.length > 1) {
				aliasTableName = aliasNames[1];
			}
			if (currTableName != null && currTableName.trim().length() > 0
					&& !currTableName.trim().equalsIgnoreCase(realTableName)
					&& !currTableName.trim().equalsIgnoreCase(aliasTableName)) {
				continue;
			}
			if (currTableName != null && currTableName.trim().length() > 0) {
				isHaveCurrTableName = true;
			}
			realTableName = trimQualifier(realTableName);

			for (ColumnProposalDetailInfo column : getTableColumns(realTableName.trim())) {
				String columnName = column.getColumnName();
				if (currColumnName == null || currColumnName.length() == 0) {
					result.add(column);
				} else if (columnName.startsWith(isTableLowercase() ? currColumnName.toLowerCase(Locale.getDefault())
						: currColumnName)) {
					result.add(column);
				}
			}
		}

		if (!isHaveCurrTableName && currTableName != null && currTableName.trim().length() > 0) {
			String realTableName = trimQualifier(currTableName);
			for (ColumnProposalDetailInfo column : getTableColumns(realTableName.trim())) {
				String columnName = column.getColumnName();
				if (currColumnName == null || currColumnName.length() == 0) {
					result.add(column);
				} else if (columnName.startsWith(isTableLowercase() ? currColumnName.toLowerCase(Locale.getDefault())
						: currColumnName)) {
					result.add(column);
				}
			}
		}
		return result;
	}

	/**
	 * get Table's Columns
	 *
	 * @param tableName String
	 * @return columnNames List<ColumnProposalDetailInfo>
	 * @throws SQLException e
	 */
	protected List<ColumnProposalDetailInfo> getTableColumns(
			final String tableName) throws SQLException { // FIXME move this logic to core module
		if (tableName == null || tableName.length() == 0) {
			return new ArrayList<ColumnProposalDetailInfo>();
		}

		loadAllTableColumnsMap();

		String lowerTbl = tableName.toLowerCase();
		if (columns != null && columns.containsKey(lowerTbl)) {
			return columns.get(lowerTbl);
		}

		return new ArrayList<ColumnProposalDetailInfo>();
	}

	public ICompletionProposal[] computeSecondProposals(
			ITextViewer viewer,
			int offset,
			org.eclipse.jface.text.contentassist.ICompletionProposal selectedProposal) {

		return computeSecondProposals(viewer, offset, (ICompletionProposal) selectedProposal);
	}

	/**
	 * compute Second Proposals
	 *
	 * @param viewer
	 * @param offset
	 * @param firstProposalsSelectedProposal
	 * @return ICompletionProposal[]
	 */
	public ICompletionProposal[] computeSecondProposals(ITextViewer viewer,
			int offset, ICompletionProposal firstProposalsSelectedProposal) {
		IDocument document = viewer.getDocument();
		int currOffset = offset - 1;
		try {
			String currWord = "";
			char currChar;
			while (currOffset >= 0 && !isKeywordSeparator(currChar = document.getChar(currOffset))) {
				currWord = Character.toUpperCase(currChar) + currWord;
				currOffset--;
			}

			List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
			String firstProposalsDisplayString = firstProposalsSelectedProposal.getDisplayString();

			for (String sql : RecentlyUsedSQLContentPersistUtils.getRecentlyUsedSQLContentsById(databaseProvider.getDatabase())) {
				if (StringUtil.startsWithIgnoreCase(firstProposalsDisplayString, sql)) {
					String content = "";
					if (sql.length() > 60) {
						content = sql.substring(0, 57);
						content += "...";
					} else {
						content = sql;
					}

					proposals.add(new CompletionProposal(
							sql,
							offset - currWord.length(),
							currWord.length(),
							sql.length(),
							CommonUIPlugin.getImage("icons/queryeditor/arrow_left.png"),
							content, null, sql));
				}
			}

			return proposals.toArray(new ICompletionProposal[proposals.size()]);
		} catch (Exception e) {
			LOGGER.error("", e);
			return null;
		}
	}

	/**
	 * clean cache table names & column names cache
	 */
	private void cleanSchemaInfosForAutocompletion() {
		columns = null;
		tableNames = null;
	}

	/**
	 * Trim the qualifier
	 *
	 * @param objectName String
	 * @return String
	 */
	private String trimQualifier(String objectName) { // FIXME move this logic to core module
		if (objectName == null || objectName.trim().length() == 0) {
			return objectName;
		}

		return objectName.replaceAll("['\"]", "");
	}

	/**
	 * When input the char, whether to be able to show proposal
	 *
	 * @param ch char
	 * @return boolean
	 */
	public static boolean isShowProposal(char ch) { // FIXME move this logic to core module
		return ch == '.' /*|| ch == ' ' || ch == '\r' || ch == '\n'*/
				|| ch == '('/* || ch == ','*/;
	}
}
