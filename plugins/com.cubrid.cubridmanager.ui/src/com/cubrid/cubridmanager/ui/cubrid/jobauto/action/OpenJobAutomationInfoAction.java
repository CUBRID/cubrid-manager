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
package com.cubrid.cubridmanager.ui.cubrid.jobauto.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.ThreadUtil;
import com.cubrid.common.ui.common.navigator.CubridNavigatorView;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.util.NodeUtil;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.editor.JobAutoDashboardEditorPart;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.editor.JobAutoDashboardInput;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.progress.OpenJobAutomationInfoPartProgress;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * @author fulei
 * @version 1.0 - 2013-1-14 created by fulei
 */
public class OpenJobAutomationInfoAction extends SelectionAction {

    public static final String ID = OpenJobAutomationInfoAction.class.getName();
    private static final Logger LOGGER = LogUtil.getLogger(OpenJobAutomationInfoAction.class);

    public OpenJobAutomationInfoAction() {
        this(null, null, null, null, null);
    }

    public OpenJobAutomationInfoAction(
            Shell shell, String text, ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
        this(shell, null, text, enabledIcon, disabledIcon);
    }

    public OpenJobAutomationInfoAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon) {
        super(shell, provider, text, enabledIcon);
        this.setId(ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj) || obj.length == 0) {
            setEnabled(false);
            return;
        }

        ICubridNode node = (ICubridNode) obj[0];
        if (node.getType().equals(CubridNodeType.JOB_FOLDER)) {
            CubridNavigatorView view =
                    CubridNavigatorView.getNavigatorView("com.cubrid.cubridmanager.host.navigator");
            if (view == null) {
                return;
            }
            // if not expand ,expand the node and wait until all children be added
            TreeViewer treeViewer = view.getViewer();
            if (!treeViewer.getExpandedState(node)) {
                treeViewer.expandToLevel(node, 1);
                while (node.getChildren().size() == 0) {
                    ThreadUtil.sleep(500);
                }
            }
            openJobsDetailInfoEditor(NodeUtil.getCubridDatabase(node));
        }
    }

    /**
     * open job detail info part
     *
     * @param database
     */
    public void openJobsDetailInfoEditor(CubridDatabase database) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (null == window) {
            return;
        }

        if (database == null) {
            return;
        }

        /*Check it open same editor*/
        IEditorPart editorPart = getOpenedEditorPart(database, JobAutoDashboardEditorPart.ID);
        if (editorPart == null) {
            OpenJobAutomationInfoPartProgress progress =
                    new OpenJobAutomationInfoPartProgress(database);
            progress.loadJobAutomationInfoList();
            if (progress.isSuccess()) {
                JobAutoDashboardInput input =
                        new JobAutoDashboardInput(
                                database,
                                progress.getBackupPlanInfoList(),
                                progress.getQueryPlanInfoList());
                try {
                    window.getActivePage().openEditor(input, JobAutoDashboardEditorPart.ID);
                } catch (PartInitException e) {
                    LOGGER.error("Can not initialize the trigger view list UI.", e);
                }
            }
        } else {
            JobAutoDashboardEditorPart jobAutoDetailInfoPart =
                    (JobAutoDashboardEditorPart) editorPart;
            window.getActivePage().activate(jobAutoDetailInfoPart);
            jobAutoDetailInfoPart.refreshAll();
        }
    }

    /**
     * Get opened IEditorPart
     *
     * @param database CubridDatabase
     * @param editorId String
     * @return
     */
    private IEditorPart getOpenedEditorPart(CubridDatabase database, String editorId) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }
        IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
        for (IEditorReference reference : editorReferences) {
            if (reference.getId().equals(editorId)) {
                IEditorPart editor = reference.getEditor(false);

                if (editor != null) {
                    if (editor instanceof JobAutoDashboardEditorPart) {
                        JobAutoDashboardEditorPart jobAutoDetailInfoPart =
                                (JobAutoDashboardEditorPart) editor;
                        if (jobAutoDetailInfoPart.getDatabase().equals(database)) {
                            return editor;
                        }
                    }
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
     */
    public boolean isSupported(Object obj) {
        return false;
    }
}
