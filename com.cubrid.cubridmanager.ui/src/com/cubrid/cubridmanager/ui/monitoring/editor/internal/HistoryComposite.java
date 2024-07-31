/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.cubridmanager.ui.monitoring.Messages;
import java.util.Calendar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

/**
 * This type provides for some composite for monitor history
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-4-27 created by lizhiqinag
 */
public class HistoryComposite {
    // history file name is composed of prefix+hostAddress+"_"+hostport+".dat"
    public static final String DB_HISTORY_FILE_PREFIX = "db_history_on_";
    public static final String BROKER_HISTORY_FILE_PREFIX = "broker_history_on_";
    public static final String HOSTDASHBOARD_HISTORY_FILE_PREFIX = "host_dashboard_history_on_";
    public static final String DBDASHBOARD_HISTORY_FILE_PREFIX = "db_dashboard_history_on_";
    public static final String HOST_SYSMON_HISTORY_FILE_PREFIX = "host_sysmon_history_on_";
    public static final String DB_SYSMON_HISTORY_FILE_PREFIX = "db_sysmon_history_on_";
    public static final String HISTORY_SUFFIX = ".dat";
    private DateTime dateChooser;
    private Button queryBtn;
    private DateTime fromTimeTxt;
    private DateTime toTimeTxt;

    /**
     * Load time selection composite on the parent composite
     *
     * @param parent the parent composite
     * @return the instance of Composite
     */
    public Composite loadTimeSelection(Composite parent) {
        final Composite composite = new Composite(parent, SWT.RESIZE);
        composite.setLayout(new GridLayout(7, false));
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label dateLbl = new Label(composite, SWT.NONE);
        dateLbl.setText(Messages.historySelectDate);

        dateChooser = new DateTime(composite, SWT.DATE | SWT.BORDER);
        dateChooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label fromTimeLbl = new Label(composite, SWT.NONE);
        fromTimeLbl.setText(Messages.historySelectStartTime);

        fromTimeTxt = new DateTime(composite, SWT.TIME | SWT.BORDER);
        fromTimeTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label toTimeLbl = new Label(composite, SWT.NONE);
        toTimeLbl.setText(Messages.historySelectEndTime);

        toTimeTxt = new DateTime(composite, SWT.TIME | SWT.BORDER);
        toTimeTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        dateChooser.setDate(year, month, day);
        toTimeTxt.setTime(hours, minutes, seconds);
        if (hours > 0) {
            hours = hours - 1;
        }
        fromTimeTxt.setTime(hours, minutes, seconds);

        queryBtn = new Button(composite, SWT.PUSH);
        queryBtn.setText(Messages.btnHistoryQuery);

        return parent;
    }

    /** @return the queryBtn */
    public Button getQueryBtn() {
        return queryBtn;
    }

    /** @return the String */
    public String getDate() {
        int year = dateChooser.getYear();
        int month = dateChooser.getMonth();
        int day = dateChooser.getDay();
        return year + "-" + month + "-" + day;
    }

    /** @return the String */
    public String getFromTime() {
        int hours = fromTimeTxt.getHours();
        int minutes = fromTimeTxt.getMinutes();
        int seconds = fromTimeTxt.getSeconds();
        return hours + ":" + minutes + ":" + seconds;
    }

    /** @return the String */
    public String getToTime() {
        int hours = toTimeTxt.getHours();
        int minutes = toTimeTxt.getMinutes();
        int seconds = toTimeTxt.getSeconds();
        return hours + ":" + minutes + ":" + seconds;
    }

    /**
     * Check if the time of toTime represented is after the time of fromTime represented.
     *
     * @param date the date
     * @param fromTime the start time
     * @param toTime the end time
     * @return true if the time is right,false or else
     */
    public boolean checkTime(String date, String fromTime, String toTime) { // FIXME extract
        String[] historyYmd = date.split("-");
        String match = "^\\d{1,2}[:]\\d{1,2}[:]\\d{1,2}$";
        boolean isMatch = fromTime.matches(match);
        if (!isMatch) {
            return false;
        }
        isMatch = toTime.matches(match);
        if (!isMatch) {
            return false;
        }
        String[] fromHms = fromTime.split(":");
        String[] toHms = toTime.split(":");
        Calendar fromCal = Calendar.getInstance();
        fromCal.set(
                Integer.parseInt(historyYmd[0]),
                Integer.parseInt(historyYmd[1]),
                Integer.parseInt(historyYmd[2]),
                Integer.parseInt(fromHms[0]),
                Integer.parseInt(fromHms[1]),
                Integer.parseInt(fromHms[2]));
        Calendar toCal = Calendar.getInstance();
        toCal.set(
                Integer.parseInt(historyYmd[0]),
                Integer.parseInt(historyYmd[1]),
                Integer.parseInt(historyYmd[2]),
                Integer.parseInt(toHms[0]),
                Integer.parseInt(toHms[1]),
                Integer.parseInt(toHms[2]));
        return fromCal.before(toCal);
    }
}
