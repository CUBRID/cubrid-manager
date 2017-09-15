/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.common.ui.query.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.BrokerStatusInfos;
import com.cubrid.cubridmanager.core.broker.task.GetBrokerStatusInfosTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;

/**
 * Used for notify all the query editor some event.
 * 
 * @author Kevin.Wang
 * @version 1.0 - Apr 26, 2012 created by Kevin.Wang
 */
public class QueryEditorUtil {
	private static final Logger LOGGER = LogUtil.getLogger(QueryEditorUtil.class);

	/**
	 * Fire all the query editor that schema node have changed
	 * 
	 * @param schemaNode
	 */
	public static void fireSchemaNodeChanged(ISchemaNode schemaNode) {
		List<QueryEditorPart> editorPartList = getAllQueryEditorPart();
		for (QueryEditorPart editor : editorPartList) {
			if (schemaNode != null && schemaNode instanceof DefaultSchemaNode) {
				editor.getCombinedQueryComposite().fireSchemaNodeChanged(
						(DefaultSchemaNode) schemaNode);
			}
		}
	}

	/**
	 * Get all opened query editor
	 * 
	 * @return
	 */
	public static List<QueryEditorPart> getAllQueryEditorPart() {
		List<QueryEditorPart> editorPartList = new ArrayList<QueryEditorPart>();

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return editorPartList;
		}

		IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();

		for (IEditorReference reference : editorReferences) {
			if (reference.getId().equals(QueryEditorPart.ID)) {
				QueryEditorPart editor = (QueryEditorPart) reference.getEditor(false);

				editorPartList.add(editor);
			}
		}
		return editorPartList;
	}

	/**
	 * Open query editor and run query
	 * 
	 * @param database
	 * @param query
	 * @param isAutoRun
	 * @param isOpenInExistEditor
	 */
	public static void openQueryEditorAndRunQuery(CubridDatabase database, String query,
			boolean isAutoRun, boolean isOpenInExistEditor) {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}

		QueryEditorPart queryEditor = null;
		/*Find exist query editor*/
		if (isOpenInExistEditor) {
			List<QueryEditorPart> editorPartList = getAllQueryEditorPart();
			for (QueryEditorPart editor : editorPartList) {
				if (database.equals(editor.getSelectedDatabase())) {
					queryEditor = editor;
				}
			}
		}

		boolean isNewQueryEditor = false;
		/*Open new query editor*/
		if (queryEditor == null) {
			QueryUnit input = new QueryUnit();
			input.setDatabase(database);

			try {
				queryEditor = (QueryEditorPart) window.getActivePage().openEditor(input,
						QueryEditorPart.ID);

				queryEditor.connect(database);
				isNewQueryEditor = true;
			} catch (PartInitException ex) {
				LOGGER.equals(ex.getMessage());
			}
		}
		/*Run query*/
		if (queryEditor != null) {
			if (isNewQueryEditor) {
				queryEditor.setQuery(query, false, isAutoRun, false);
			} else {
				queryEditor.newQueryTab(query, isAutoRun);
			}
			window.getActivePage().activate(queryEditor);
		}
	}

	/**
	 * Check for available connections
	 * @param database
	 * @return
	 */
	public static boolean isAvailableConnect(CubridDatabase database) {
		int openedQueryEditor = getOpenedQueryEditorCount();
		int openedAndRunningQueryEditor = getOpenedAndRunningCount();
		int maxNumApplServer = getMaxNumApplServer(database);
		int runningCas = getRunningCasCount(database);
		boolean isExistIdleCas = maxNumApplServer - runningCas > 0;
		boolean isAvailableOpenQueryEditor =
				maxNumApplServer - (runningCas + openedQueryEditor - openedAndRunningQueryEditor) > 0;

		if (isExistIdleCas && isAvailableOpenQueryEditor) {
			return true;
		}
		return false;
	}

	private static int getMaxNumApplServer(CubridDatabase database) {
		String currentBrokerName = database.getDatabaseInfo().getBrokerName();
		return Integer.parseInt(
				database.getServer().getServerInfo().getBrokerConfParaMap()
				.get(currentBrokerName).get("MAX_NUM_APPL_SERVER"));
	}

	private static int getRunningCasCount(CubridDatabase database) {
		String currentBrokerName = database.getDatabaseInfo().getBrokerName();
		int runningCasCount = 0;
		BrokerStatusInfos brokerStatusInfos = new BrokerStatusInfos();
		GetBrokerStatusInfosTask<BrokerStatusInfos> statisTask =
				new GetBrokerStatusInfosTask<BrokerStatusInfos>(
						database.getServer().getServerInfo(),
						CommonSendMsg.getGetBrokerStatusItems(),
						brokerStatusInfos);
		statisTask.setBrokerName(currentBrokerName);
		statisTask.execute();
		brokerStatusInfos = statisTask.getResultModel();
		if (brokerStatusInfos != null) {
			List<ApplyServerInfo> casInfos = brokerStatusInfos.getAsinfo();
			for (ApplyServerInfo casInfo : casInfos) {
				if (casInfo.getAs_status().equals("BUSY")
						|| casInfo.getAs_status().equals("CLIENT WAIT")) {
					runningCasCount++;
				}
			}
		}
		statisTask.finish();
		return runningCasCount;
	}

	private static int getOpenedQueryEditorCount() {
		IEditorReference[] editorRefArr = getQueryEditorRefs();
		if (editorRefArr == null) {
			return 0;
		} else {
			return editorRefArr.length;
		}
	}

	private static int getOpenedAndRunningCount() {
		int count = 0;
		IEditorReference[] editorRefArr = getQueryEditorRefs();
		if (editorRefArr == null) {
			return count;
		}

		for (IEditorReference editorRef : editorRefArr) {
			String editorId = editorRef.getId();
			if (editorId != null && editorId.equals(QueryEditorPart.ID)) {
				QueryEditorPart queryEditor = (QueryEditorPart) editorRef.getEditor(false);
				if (queryEditor.isActive()) {
					count++;
				}
			}
		}

		return count;
	}

	private static IEditorReference[] getQueryEditorRefs() {
		IWorkbenchPage page = LayoutUtil.getActivePage();
		return page == null ? null : page.getEditorReferences();
	}
}
