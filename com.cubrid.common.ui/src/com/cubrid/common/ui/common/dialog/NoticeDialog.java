package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class NoticeDialog extends Dialog {
    private boolean unseenUntilNextVersion = false;
    private String version = null;
    private String url = null;

    public boolean isUnseenUntilNextVersion() {
        return unseenUntilNextVersion;
    }

    public void setUnseenUntilNextVersion(boolean unseenUntilNextVersion) {
        this.unseenUntilNextVersion = unseenUntilNextVersion;
    }

    public NoticeDialog(
            String url, String version, Shell parentShell, ImageDescriptor aboutImageDescriptor) {
        super(parentShell);
        this.version = version;
        this.url = url;
    }

    protected int getShellStyle() {
        return super.getShellStyle();
    }

    protected void buttonPressed(int buttonId) {
        if (isUnseenUntilNextVersion()) {
            CommonUIPlugin.setLastCheckedNoticeVersion(version);
        } else {
            CommonUIPlugin.setLastCheckedNoticeVersion(null);
        }
        super.buttonPressed(buttonId);
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        this.getShell().setMinimumSize(500, 400);
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.titleNoticeDialog);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        final Button btn = new Button(parent, SWT.CHECK);
        if (version != null && version.equals(CommonUIPlugin.getLastCheckedNoticeVersion())) {
            btn.setSelection(true);
            unseenUntilNextVersion = true;
        } else {
            btn.setSelection(false);
            unseenUntilNextVersion = false;
        }
        btn.setText(Messages.chkNoticeDialogSeen);
        btn.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        NoticeDialog.this.setUnseenUntilNextVersion(btn.getSelection());
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
        createButton(parent, IDialogConstants.OK_ID, Messages.btnNoticeDialogClose, true);
    }

    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        {
            GridLayout tLayout = new GridLayout();
            tLayout.marginHeight = 0;
            tLayout.marginWidth = 0;
            tLayout.verticalSpacing = 0;
            tLayout.horizontalSpacing = 0;
            parentComp.setLayout(tLayout);
            parentComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        try {
            Browser browser = new Browser(parentComp, SWT.NONE);
            browser.setSize(500, 400);
            {
                GridLayout tLayout = new GridLayout();
                tLayout.marginHeight = 0;
                tLayout.marginWidth = 0;
                tLayout.verticalSpacing = 0;
                tLayout.horizontalSpacing = 0;
                browser.setLayout(tLayout);
                browser.setLayoutData(new GridData(GridData.FILL_BOTH));
            }
            browser.setUrl(url);
        } catch (Exception e) {
        }

        return parent;
    }
}
