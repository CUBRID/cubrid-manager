package com.cubrid.common.ui.cubrid.table.action;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.POJOUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Copy Java pojo to clipboard action
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-2-29 created by Kevin.Wang
 */
public class CopyPOJOToClipboard extends SelectionAction {

    public static final String ID = CopyPOJOToClipboard.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param icon
     */
    public CopyPOJOToClipboard(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public CopyPOJOToClipboard(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Sets this action support this object
     *
     * @see org.eclipse.jface.action.IAction.ISelectionAction
     * @param obj Object
     * @return boolean
     */
    public boolean isSupported(Object obj) {
        return ActionSupportUtil.isSupportMultiSelection(
                obj,
                new String[] {NodeType.USER_TABLE, NodeType.USER_PARTITIONED_TABLE_FOLDER},
                false);
    }

    /** @see org.eclipse.jface.action.Action#run() */
    public void run() {

        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj)) {
            setEnabled(false);
            return;
        }

        final Display display = PlatformUI.getWorkbench().getDisplay();
        BusyIndicator.showWhile(
                display,
                new Runnable() {
                    public void run() {
                        DefaultSchemaNode table = (DefaultSchemaNode) obj[0];
                        String pojoString = getPojoString(table);
                        CommonUITool.copyContentToClipboard(pojoString);
                        CommonUITool.openInformationBox(
                                Messages.titleCopyToPojo, Messages.msgCopyToPojo);
                    }
                });
    }

    private String getPojoString(DefaultSchemaNode schemaNode) {
        return POJOUtil.getJavaPOJOString(null, schemaNode);
    }
}
