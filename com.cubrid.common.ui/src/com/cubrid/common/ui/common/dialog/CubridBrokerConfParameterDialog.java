package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

public class CubridBrokerConfParameterDialog extends CMTitleAreaDialog {

    private static final Logger LOGGER = LogUtil.getLogger(CubridBrokerConfParameterDialog.class);

    private Text filePath;
    private String cubridBrokerConffile;

    public CubridBrokerConfParameterDialog(Shell parentShell, String cubridBrokerConffile) {
        super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL);
        this.cubridBrokerConffile = cubridBrokerConffile;
    }

    protected Control createDialogArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        comp.setLayout(new GridLayout(1, false));

        Composite filePathComp = new Composite(comp, SWT.NONE);
        filePathComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        filePathComp.setLayout(new GridLayout(2, false));

        new Label(filePathComp, SWT.NONE)
                .setText(Messages.cubridBrokerConfOpenFileDialogFilePathLabel);
        filePath = new Text(filePathComp, SWT.BORDER);
        filePath.setLayoutData(new GridData(GridData.FILL_BOTH));
        filePath.setEditable(false);

        init();
        return parent;
    }

    /** init parameter */
    public void init() {
        try {
            new ProgressMonitorDialog(this.getShell())
                    .run(
                            true,
                            false,
                            new IRunnableWithProgress() {
                                public void run(final IProgressMonitor monitor)
                                        throws InvocationTargetException, InterruptedException {
                                    monitor.beginTask(
                                            com.cubrid.common.ui.spi.Messages.msgRunning,
                                            IProgressMonitor.UNKNOWN);

                                    filePath.setText(cubridBrokerConffile);
                                }
                            });
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
