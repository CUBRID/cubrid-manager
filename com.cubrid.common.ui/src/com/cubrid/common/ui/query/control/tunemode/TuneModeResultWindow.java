package com.cubrid.common.ui.query.control.tunemode;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

public class TuneModeResultWindow {
    private QueryEditorPart editor;
    private Shell shell;
    private TuneModeResultComposite tuneModeResult;
    private boolean disposed = false;
    private String parentEditorName;

    public TuneModeResultWindow(QueryEditorPart editor) {
        this.editor = editor;
    }

    public void open() {
        shell = new Shell(SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX | SWT.MIN);
        shell.setLayout(new GridLayout());
        shell.setLayoutData(new GridData(GridData.FILL_BOTH));
        shell.setText(Messages.lblTuneModeResult);
        shell.setImage(CommonUIPlugin.getImage("icons/queryeditor/tune_mode.png"));
        shell.setSize(800, 600);
        shell.setMaximized(true);

        tuneModeResult = new TuneModeResultComposite(shell, SWT.NONE);
        tuneModeResult.setLayout(new GridLayout());
        tuneModeResult.setLayoutData(new GridData(GridData.FILL_BOTH));
        tuneModeResult.initialize();

        DatabaseInfo databaseInfo =
                (editor == null || editor.getSelectedDatabase() == null)
                        ? null
                        : editor.getSelectedDatabase().getDatabaseInfo();
        tuneModeResult.setDatabaseInfo(databaseInfo);

        shell.addShellListener(
                new ShellListener() {
                    public void shellIconified(ShellEvent e) {
                        editor.hideTuneModeResult();
                    }

                    public void shellDeiconified(ShellEvent e) {}

                    public void shellDeactivated(ShellEvent e) {}

                    public void shellClosed(ShellEvent e) {
                        disposed = true;
                        if (tuneModeResult != null && !tuneModeResult.isDisposed()) {
                            tuneModeResult.dispose();
                            tuneModeResult = null;
                        }
                        if (shell != null && !shell.isDisposed()) {
                            shell.dispose();
                            shell.close();
                        }
                    }

                    public void shellActivated(ShellEvent e) {}
                });
        shell.open();
    }

    public void setParentEditorName(String title) {
        parentEditorName = Messages.lblTuneModeResult + " >> " + title;
    }

    public void showResult(TuneModeModel tuneModeModel) {
        shell.setText(parentEditorName);
        tuneModeResult.showResult(tuneModeModel);
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void show() {
        shell.setMinimized(false);
    }
}
