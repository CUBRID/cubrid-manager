/*
o * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.part.CubridViewPart;

/**
 * 
 * A layout utility is responsible for managing workbench part
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class LayoutUtil {
	private static final Logger LOGGER = LogUtil.getLogger(LayoutUtil.class);
	public static final int MAX_OPEN_QUERY_EDITOR_NUM = 10;
	
	/**
	 * The constructor
	 */
	private LayoutUtil() {
	}

	/**
	 * 
	 * Get active workbench page
	 * 
	 * @return IWorkbenchPage
	 */
	public static IWorkbenchPage getActivePage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return null;
		}
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		return window.getActivePage();
	}

	/**
	 * 
	 * Close all opened editor and view part
	 * 
	 */
	public static void closeAllEditorAndView() {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return;
		}

		page.closeAllEditors(true);
		IViewReference[] viewRef = page.getViewReferences();
		for (int i = 0, n = viewRef.length; i < n; i++) {
			page.hideView(viewRef[i]);
		}
	}

	/**
	 * 
	 * Get the editor part of this CUBRID node and this editorId
	 * 
	 * @param cubridNode the ICubridNode object
	 * @param editorId the editor id
	 * @return the IEditorPart object
	 */
	public static IEditorPart getEditorPart(ICubridNode cubridNode,
			String editorId) {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return null;
		}

		IEditorReference[] editorRefArr = page.getEditorReferences();
		if (editorRefArr == null || editorRefArr.length == 0) {
			return null;
		}

		for (IEditorReference editorRef : editorRefArr) {
			try {
				IEditorInput editorInput = editorRef.getEditorInput();
				String id = editorRef.getId();
				if (editorInput instanceof ICubridNode) {
					ICubridNode node = (ICubridNode) editorInput;
					if (node.getId().equals(cubridNode.getId())
							&& editorId.equals(id)) {
						return editorRef.getEditor(false);
					}
				}
			} catch (PartInitException e) {
				LOGGER.error(e.getMessage());
			}
		}

		return null;
	}

	/**
	 * 
	 * Get the editor parts of this CUBRID node
	 * 
	 * @param cubridNode the ICubridNode object
	 * @return List<IEditorPart>
	 */
	public static List<IEditorPart> getEditorParts(ICubridNode cubridNode) {
		List<IEditorPart> partList = new ArrayList<IEditorPart>();

		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return partList;
		}

		IEditorReference[] editorRefArr = page.getEditorReferences();
		if (editorRefArr == null || editorRefArr.length == 0) {
			return partList;
		}

		for (IEditorReference editorRef : editorRefArr) {
			try {
				IEditorInput editorInput = editorRef.getEditorInput();
				if (editorInput instanceof ICubridNode) {
					ICubridNode node = (ICubridNode) editorInput;
					if (node.getId().equals(cubridNode.getId())) {
						partList.add(editorRef.getEditor(false));
					}
				}
			} catch (PartInitException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return partList;
	}

	/**
	 * 
	 * Get the view part of this cubrid node and viewId
	 * 
	 * @param cubridNode the ICubridNode object
	 * @param viewId the view id
	 * @return the IViewPart object
	 */
	public static IViewPart getViewPart(ICubridNode cubridNode, String viewId) {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return null;
		}

		IViewReference[] viewRefArr = page.getViewReferences();
		if (viewRefArr == null || viewRefArr.length == 0) {
			return null;
		}

		for (IViewReference viewRef : viewRefArr) {
			IViewPart viewPart = viewRef.getView(false);
			String id = viewRef.getId();
			if (viewPart instanceof CubridViewPart) {
				CubridViewPart cubridViewPart = (CubridViewPart) viewPart;
				ICubridNode node = cubridViewPart.getCubridNode();
				if (node != null && node.getId().equals(cubridNode.getId())
						&& viewId.equals(id)) {
					return viewPart;
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * Get the view parts of this CUBRID node
	 * 
	 * @param cubridNode the ICubridNode object
	 * @return List<IViewPart>
	 */
	public static List<IViewPart> getViewParts(ICubridNode cubridNode) {
		List<IViewPart> viewPartList = new ArrayList<IViewPart>();

		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return viewPartList;
		}

		IViewReference[] viewRefArr = page.getViewReferences();
		if (viewRefArr == null || viewRefArr.length == 0) {
			return viewPartList;
		}
		for (IViewReference viewRef : viewRefArr) {
			IViewPart viewPart = viewRef.getView(false);
			if (viewPart instanceof CubridViewPart) {
				CubridViewPart cubridViewPart = (CubridViewPart) viewPart;
				ICubridNode node = cubridViewPart.getCubridNode();
				if (node != null && node.getId().equals(cubridNode.getId())) {
					viewPartList.add(viewPart);
				}
			}
		}
		return viewPartList;
	}

	/**
	 * 
	 * Close the editor or view part relating to this given CUBIRD node, but the
	 * query editor is not included.
	 * 
	 * @param cubridNode the ICubridNode object
	 */
	public static void closeEditorAndView(ICubridNode cubridNode) {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return;
		}

		String editorId = cubridNode.getEditorId();
		if (editorId != null) {
			IEditorPart editorPart = getEditorPart(cubridNode, editorId);
			if (editorPart != null) {
				page.closeEditor(editorPart, false);
			}
		}

		String viewId = cubridNode.getViewId();
		if (viewId != null) {
			IViewPart viewPart = getViewPart(cubridNode, viewId);
			if (null != viewPart) {
				page.hideView(viewPart);
			}
		}
	}

	/**
	 * Get the view second id
	 * 
	 * @param cubridNode the ICubridNode object
	 * @return the string that represent for view second id
	 */
	public static String getViewSecondId(ICubridNode cubridNode) {
		return cubridNode.getId().replaceAll(":", "_");
	}

	/**
	 * 
	 * Close all opened editor and view part related with CUBRID Manager,not
	 * include query editor
	 * 
	 */
	public static void closeAllCubridEditorAndView() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return;
		}
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		IEditorReference[] editorRefArr = page.getEditorReferences();
		if (editorRefArr != null && editorRefArr.length > 0) {
			for (IEditorReference editorRef : editorRefArr) {
				try {
					IEditorInput editorInput = editorRef.getEditorInput();
					if (editorInput instanceof ICubridNode) {
						window.getActivePage().closeEditor(
								editorRef.getEditor(false), true);
					}
				} catch (PartInitException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
		IViewReference[] viewRefArr = page.getViewReferences();
		if (viewRefArr != null && viewRefArr.length > 0) {
			for (IViewReference viewRef : viewRefArr) {
				IViewPart viewPart = viewRef.getView(false);
				if (viewPart instanceof CubridViewPart) {
					page.hideView(viewPart);
				}
			}
		}
	}

	/**
	 * 
	 * When database logout or stop,check query editor whether some transaction
	 * are not commit
	 * 
	 * @param databaseNode the CubridDatabase object
	 * @return <code>true</code> if transaction is commited;<code>false</code>
	 *         otherwise
	 */
	public static boolean checkAllQueryEditor(CubridDatabase databaseNode) {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return true;
		}

		boolean isContinue = true;
		IEditorReference[] editorRefArr = page.getEditorReferences();
		if (editorRefArr == null || editorRefArr.length == 0) {
			return true;
		}

		String brokerName = databaseNode.getDatabaseInfo().getBrokerName();
		for (IEditorReference editorRef : editorRefArr) {
			String editorId = editorRef.getId();
			if (editorId != null && editorId.equals(QueryEditorPart.ID)) {
				QueryEditorPart queryEditor = (QueryEditorPart) editorRef.getEditor(false);
				CubridDatabase db = queryEditor.getSelectedDatabase();
				if (db != null && db.getId().equals(databaseNode.getId())) {
					isContinue = queryEditor.resetJDBCConnection();
				}
				String logoutKey = databaseNode.getName() + ":"
						+ databaseNode.getServer().getServerName();
				if (queryEditor.getPartName().contains(logoutKey)) {
					databaseNode.getServer().getServerInfo().releaseCasCount(brokerName);
				}
			}
		}

		return isContinue;
	}

	/**
	 * 
	 * When server disconnect or delete,check query editor whether some
	 * transaction are not commit
	 * 
	 * @param cubridServer the CubridServer object
	 * @return <code>true</code> if transaction is commited;<code>false</code>
	 *         otherwise
	 */
	public static boolean checkAllQueryEditor(CubridServer cubridServer) {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return true;
		}

		IEditorReference[] editorRefArr = page.getEditorReferences();
		if (editorRefArr == null || editorRefArr.length == 0) {
			return true;
		}

		boolean isContinue = true;
		for (IEditorReference editorRef : editorRefArr) {
			String editorId = editorRef.getId();
			if (editorId != null && editorId.equals(QueryEditorPart.ID)) {
				QueryEditorPart queryEditor = (QueryEditorPart) editorRef.getEditor(false);
				CubridDatabase db = queryEditor.getSelectedDatabase();
				if (db != null && db.getServer() != null
						&& db.getServer().getId().equals(cubridServer.getId())) {
					isContinue = queryEditor.resetJDBCConnection();
				}
			}
		}

		return isContinue;
	}
}
