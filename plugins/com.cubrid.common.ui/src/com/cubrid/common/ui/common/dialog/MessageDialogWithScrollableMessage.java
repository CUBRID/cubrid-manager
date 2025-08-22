package com.cubrid.common.ui.common.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MessageDialogWithScrollableMessage extends MessageDialog {

    private String message;

    public MessageDialogWithScrollableMessage(
            Shell parentShell,
            String title,
            Image titleImage,
            String mainMessage,
            String secondaryMessage,
            int imageType,
            String[] buttonLabels) {
        super(parentShell, title, null, mainMessage, imageType, buttonLabels, 0);
        this.message = secondaryMessage;
    }

    @Override
    public Control createDialogArea(Composite parent) {
        if (this.message.length() == 0) {
            return super.createDialogArea(parent);
        }
        Composite content = (Composite) super.createDialogArea(parent);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);

        ScrolledComposite sc = new ScrolledComposite(content, SWT.H_SCROLL | SWT.V_SCROLL);

        Composite composite = new Composite(sc, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.VERTICAL));
        Label l = new Label(composite, SWT.NONE);
        l.setText(message);

        sc.setLayoutData(data);
        sc.setContent(composite);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return parent;
    }
}
