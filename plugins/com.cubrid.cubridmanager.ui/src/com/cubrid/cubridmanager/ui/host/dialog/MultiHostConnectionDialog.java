package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.host.Messages;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class MultiHostConnectionDialog extends CMTitleAreaDialog {

    public static final String KEY_BAR = "bar";
    public static final String KEY_CONNECTION = "con";
    private final int CONNECT_ID = 100;

    private List<ConnectionTaskContainer> container;

    Map<FutureTask<Integer>, ConnectionTaskContainer> runner =
            new HashMap<FutureTask<Integer>, ConnectionTaskContainer>();

    private Table table;
    private TableViewer viewer;

    public MultiHostConnectionDialog(Shell parentShell, List<ConnectionTaskContainer> container) {
        super(parentShell);
        this.container = container;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == CONNECT_ID) {
            handleConnection();

            Button btn = getButton(CONNECT_ID);
            btn.setData(IDialogConstants.OK_ID);
            btn.setText(IDialogConstants.OK_LABEL);
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void cancelPressed() {
        for (ConnectionTaskContainer con : container) {
            con.setCanceled(true);
        }
        super.cancelPressed();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CONNECT_ID, Messages.btnConnectHost, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite newComp = new Composite(parentComp, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        setTitle(Messages.titleConnectionProgress);
        setMessage(Messages.msgClickToConnect, IMessageProvider.INFORMATION);
        GridLayout layout = new GridLayout();

        layout.numColumns = 1;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

        newComp.setLayout(layout);
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        newComp.setLayoutData(data);

        viewer = new TableViewer(newComp, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        final GridData gdColumnsTable = new GridData(SWT.FILL, SWT.FILL, true, true);

        table = viewer.getTable();
        table.setLayoutData(gdColumnsTable);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        {
            TableColumn icons = new TableColumn(table, SWT.CENTER, 0);
            icons.setText("");
            icons.setWidth(20);

            TableColumn taskName = new TableColumn(table, SWT.CENTER, 1);
            taskName.setText(Messages.colTaskName);
            taskName.setWidth(150);

            TableColumn taskProgress = new TableColumn(table, SWT.CENTER, 2);
            taskProgress.setText(Messages.colTaskProgress);
            taskProgress.setWidth(300);

            TableColumn details = new TableColumn(table, SWT.CENTER, 3);
            details.setText(Messages.colTaskDetails);
            details.setWidth(300);
        }

        initTask();
        Control control = super.createDialogArea(parent);
        return control;
    }

    @Override
    protected Point getInitialSize() {
        return super.getInitialSize();
    }

    private void handleConnection() {
        for (TableItem item : table.getItems()) {
            Object obj = item.getData(KEY_CONNECTION);
            if (obj instanceof ConnectionTaskContainer) {
                ConnectionTaskContainer container = (ConnectionTaskContainer) obj;
                FutureTask<Integer> task = new FutureTask<Integer>(container);
                runner.put(task, container);
                Display.getDefault().syncExec(task);
            }
        }
        //		new Thread(new Runnable() {
        //
        //			@Override
        //			public void run() {
        //				while (runner.size() > 0) {
        //					FutureTask<Integer> tmp = null;
        //					for (Entry<FutureTask<Integer>, ConnectionTaskContainer> entry : runner.entrySet()) {
        //						try {
        //							FutureTask<Integer> task = entry.getKey();
        //							int retVal = task.get(1, TimeUnit.SECONDS);
        //							ConnectionTaskContainer con = entry.getValue();
        //							tmp = task;
        //							break;
        //						} catch (Exception e) {
        //							e.printStackTrace();
        //							continue;
        //						}
        //					}
        //					runner.remove(tmp);
        //					try {
        //						Thread.sleep(500);
        //					} catch (InterruptedException e) {
        //						e.printStackTrace();
        //					}
        //				}
        //			}
        //		}).start();

    }

    public void initTask() {
        if (container == null) return;

        for (ConnectionTaskContainer con : container) {
            ServerInfo info = con.getServerInfo();
            TableItem item = new TableItem(table, SWT.NONE);

            item.setText(1, info.getServerName());

            TableEditor editor = new TableEditor(table);
            editor.grabHorizontal = editor.grabVertical = true;
            ProgressBar bar = new ProgressBar(table, SWT.NONE);
            editor.setEditor(bar, item, 2);
            con.setItem(item);
            item.setData(KEY_BAR, bar);
            item.setData(KEY_CONNECTION, con);
        }
    }
}
