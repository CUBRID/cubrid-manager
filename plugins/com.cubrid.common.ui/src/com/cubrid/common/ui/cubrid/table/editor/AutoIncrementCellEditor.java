package com.cubrid.common.ui.cubrid.table.editor;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.TableEditorAdaptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AutoIncrementCellEditor extends CellEditor {
    private Text startVal;
    private Text increVal;
    private String value;
    private TableEditorAdaptor editorAdaptor;
    private Composite composite;

    public AutoIncrementCellEditor(Composite parent, TableEditorAdaptor editor) {
        super(parent, SWT.NONE);
        this.editorAdaptor = editor;
    }

    protected Control createControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        {
            GridLayout gl = new GridLayout(3, false);
            gl.marginHeight = 0;
            gl.marginBottom = 0;
            gl.marginTop = 0;
            gl.marginLeft = 0;
            gl.marginRight = 0;
            gl.marginWidth = 0;
            composite.setLayout(gl);
        }

        startVal = new Text(composite, SWT.BORDER);
        startVal.setLayoutData(new GridData(50, -1));
        startVal.setToolTipText(Messages.lblAutoIncrStart);

        new Label(composite, SWT.NONE).setText(",");
        increVal = new Text(composite, SWT.BORDER);
        increVal.setLayoutData(new GridData(15, -1));
        increVal.setToolTipText(Messages.lblAutoIncrIncr);

        KeyAdapter keyAdapter =
                new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        if (e.character == '\r') {
                            deactivateWidget();
                        }
                    }
                };
        startVal.addKeyListener(keyAdapter);
        increVal.addKeyListener(keyAdapter);

        ModifyListener modifyListener =
                new ModifyListener() {
                    public void modifyText(ModifyEvent e) {
                        value = getAutoIncrementValue();
                        markDirty();
                        valueChanged(true, true);
                    }
                };
        startVal.addModifyListener(modifyListener);
        increVal.addModifyListener(modifyListener);

        FocusAdapter focusAdapter =
                new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        deactivateWidget();
                        AutoIncrementCellEditor.this.focusLost();
                    }
                };
        parent.addFocusListener(focusAdapter);

        return composite;
    }

    private String getAutoIncrementValue() { // FIXME move this logic to core module
        if (StringUtil.isEmpty(startVal.getText()) || StringUtil.isEmpty(increVal.getText())) {
            return null;
        }

        return startVal.getText().trim() + "," + increVal.getText().trim();
    }

    protected Object doGetValue() {
        return value;
    }

    protected void doSetFocus() {
        if (startVal != null && !startVal.isDisposed()) {
            startVal.setFocus();

            Rectangle rect = composite.getBounds();
            editorAdaptor.showToolTip(rect, null, Messages.errInvalidAutoIncrForm);
        }
    }

    protected void doSetValue(Object value) { // FIXME move this logic to core module
        this.value = (String) value;
        String[] values = null;
        if (value == null) {
            values = new String[] {"", ""};
        } else {
            String text = (String) value;
            values = text.split(",");
            if (values.length < 2) {
                values = new String[] {"", ""};
            }
        }

        String startValue = values[0];
        String increValue = values[1];

        if (StringUtil.isEmpty(startValue) && StringUtil.isEmpty(increValue)) {
            startValue = "";
            increValue = "1";
        }

        startVal.setText(startValue);
        startVal.setSelection(startVal.getText().length());

        increVal.setText(increValue);
        increVal.setSelection(increVal.getText().length());
    }

    private void deactivateWidget() {
        if (isActivated()) {
            fireApplyEditorValue();
            deactivate();
        }
    }

    protected void focusLost() {
        deactivateWidget();
        editorAdaptor.hideToolTip();
    }
}
