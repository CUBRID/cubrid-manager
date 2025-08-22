package com.cubrid.common.ui.query.control;

import com.cubrid.common.ui.cubrid.trigger.Messages;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DateTimeComponent extends Composite {

    private DateTime date;
    private DateTime time;
    private Button buttonOk;
    private Button buttonNULL;
    private Composite parent;
    private SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    private String returnDateValue;
    private String returnDateTimeValue;
    private String returnTimestampValue;
    private String returnTimeValue;

    public DateTimeComponent(Composite parent, int style) {
        super(parent, style);
        this.parent = parent;
        this.setBackground(new Color(Display.getCurrent(), new RGB(255, 255, 255)));
        date = new DateTime(this, SWT.CALENDAR);
        date.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        date.setLayout(new GridLayout(2, true));
        time = new DateTime(this, SWT.TIME);
        time.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
        date.addListener(
                SWT.MouseDoubleClick,
                new Listener() {
                    public void handleEvent(Event event) {
                        okPressed();
                    }
                });
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, false));
        buttonComposite.setLayout(new GridLayout(2, true));

        buttonOk = new Button(buttonComposite, SWT.PUSH);
        buttonOk.setText(Messages.okBTN);
        buttonOk.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
        buttonOk.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent e) {
                        okPressed();
                    }
                });
        buttonOk.setFocus();
        setButtonLayoutData(buttonOk);
        buttonNULL = new Button(buttonComposite, SWT.PUSH);
        buttonNULL.setText("&NULL");
        buttonNULL.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
        buttonNULL.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent e) {
                        nullPressed();
                    }
                });
        setButtonLayoutData(buttonNULL);
    }

    public Point componentSize() {
        Point datePoint = date.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point timePoint = time.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point buttonOkPoint = buttonOk.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point dateTimeComponentPoint =
                new Point(datePoint.x + 40, timePoint.y + datePoint.y + buttonOkPoint.y + 90);
        return dateTimeComponentPoint;
    }

    private void okPressed() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                date.getYear(),
                date.getMonth(),
                date.getDay(),
                time.getHours(),
                time.getMinutes(),
                time.getSeconds());

        returnDateTimeValue = sdfDateTime.format(calendar.getTime());
        returnDateValue = sdfDate.format(calendar.getTime());
        returnTimestampValue = sdfTimestamp.format(calendar.getTime());
        returnTimeValue = sdfTime.format(calendar.getTime());
        parent.dispose();
    }

    private void nullPressed() {
        returnDateTimeValue = "(NULL)";
        returnDateValue = "(NULL)";
        returnTimestampValue = "(NULL)";
        parent.dispose();
    }

    public String getReturnTimestampValue() {
        return returnTimestampValue;
    }

    public String getReturnDateValue() {
        return returnDateValue;
    }

    public String getReturnDateTimeValue() {
        return returnDateTimeValue;
    }

    public String getReturnTimeValue() {
        return returnTimeValue;
    }

    /**
     * Set the layout data of the button to a GridData with appropriate heights and widths.
     *
     * @param button
     */
    protected void setButtonLayoutData(Button button) {
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = IDialogConstants.BUTTON_WIDTH;
        Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        button.setLayoutData(data);
    }
}
