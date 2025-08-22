package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BrokerLogParserDialog extends CMTitleAreaDialog {
    public Text logText;
    private String resultSql;

    public BrokerLogParserDialog(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText(Messages.brokerLogParserDialogTitle);
        setTitle(Messages.brokerLogParserDialogTitle);
        setMessage(Messages.brokerLogParserDialogMessages);

        Composite logTextComp = new Composite(parent, SWT.BORDER);
        logTextComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        logTextComp.setLayout(new GridLayout());
        new Label(logTextComp, SWT.NONE).setText(Messages.brokerLogParserDialogLabelBrokerLog);

        logText = new Text(logTextComp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        logText.setLayoutData(new GridData(GridData.FILL_BOTH));

        return parent;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            resultSql = CommonUITool.parseBrokerLogToSQL(logText.getText());
            if (StringUtil.isEmpty(resultSql)) {
                CommonUITool.openErrorBox(Messages.brokerLogParserDialogErrMsg);
                return;
            }
        }
        setReturnCode(buttonId);
        close();
    }

    protected Point getInitialSize() {
        Point p = super.getInitialSize();
        p.x = 600;
        p.y = 400;
        return p;
    }

    public String getResultSql() {
        return resultSql;
    }
}
