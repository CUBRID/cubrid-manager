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
package com.cubrid.cubridmanager.ui.mondashboard.preference;

import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.mondashboard.model.DBStatusType;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page of setting the color of figures that displayed in monitor dashboard.
 *
 * @author SC13425
 * @version 1.0 - 2010-8-16 created by SC13425
 */
public class DashboardPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String COLOR_BUTTON_TEXT = "...";

    private static final String COLOR_LABEL_TEXT = "            ";

    public static final String ID = "com.cubrid.cubridmanager.ui.preference.mondashboard";

    private static final MonitorDashboardPreference PREFER = new MonitorDashboardPreference();

    private final Map<String, Label> colorSettings = new HashMap<String, Label>();

    private Spinner monitoringSpinner = null;
    private int haHeartBeatTimeout = MonitorDashboardPreference.HA_HEARTBEAT_TIMEOUT_DEFAULT;

    /** Constructor */
    public DashboardPreferencePage() {
        super();
        setTitle(Messages.dashboardPreferencePageName);
    }

    /**
     * Constructor
     *
     * @param title String
     */
    public DashboardPreferencePage(String title) {
        super(title);
        setTitle(Messages.dashboardPreferencePageName);
    }

    /**
     * Constructor
     *
     * @param title String
     * @param image ImageDescriptor
     */
    public DashboardPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
        setTitle(Messages.dashboardPreferencePageName);
    }

    /**
     * Create contents of page.
     *
     * @param parent Composite
     * @return the content.
     */
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final Group changeFontGroup = new Group(composite, SWT.NONE);
        changeFontGroup.setText(Messages.colorSettingsOfDB);
        final GridData gdChangeFontGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        changeFontGroup.setLayoutData(gdChangeFontGroup);
        changeFontGroup.setLayout(new GridLayout());

        final Composite compositeSecond = new Composite(changeFontGroup, SWT.NONE);
        compositeSecond.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayoutFirst = new GridLayout();
        gridLayoutFirst.numColumns = 3;
        compositeSecond.setLayout(gridLayoutFirst);

        for (DBStatusType dbt : DBStatusType.values()) {
            Label lbl = new Label(compositeSecond, SWT.NONE);
            lbl.setText(Messages.dbStatusType + " \"" + getDBStatusText(dbt) + "\"");
            Label lblColor = new Label(compositeSecond, SWT.NONE);
            lblColor.setText(COLOR_LABEL_TEXT);
            lblColor.setBackground(PREFER.getColor(getDBStatusText(dbt)));
            Button btn = new Button(compositeSecond, SWT.FLAT);
            btn.setText(COLOR_BUTTON_TEXT);
            btn.addSelectionListener(new ColorSelector(btn, lblColor));
            colorSettings.put(getDBStatusText(dbt), lblColor);
        }

        // HA Monitoring Option
        {
            final Group monitoringGroup = new Group(composite, SWT.NONE);
            monitoringGroup.setText(Messages.haMon);
            final GridData gdMonitoringGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
            monitoringGroup.setLayoutData(gdMonitoringGroup);
            monitoringGroup.setLayout(new GridLayout());

            final Composite compositeMonitoring = new Composite(monitoringGroup, SWT.NONE);
            compositeMonitoring.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            final GridLayout gridLayoutMonitoring = new GridLayout();
            gridLayoutMonitoring.numColumns = 2;
            compositeMonitoring.setLayout(gridLayoutMonitoring);

            Label lbl = new Label(compositeMonitoring, SWT.NONE);
            lbl.setText(Messages.haMonHertbeatTimeout);

            monitoringSpinner = new Spinner(compositeMonitoring, SWT.BORDER);
            monitoringSpinner.setMinimum(1);
            monitoringSpinner.setMaximum(3600);
            final GridData gdSpinner = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
            gdSpinner.widthHint = 40;
            monitoringSpinner.setLayoutData(gdSpinner);
            haHeartBeatTimeout = PREFER.getHAHeartBeatTimeout();
            if (haHeartBeatTimeout < 1000) {
                haHeartBeatTimeout = 1000;
            }
            monitoringSpinner.setSelection(haHeartBeatTimeout / 1000);
            monitoringSpinner.addSelectionListener(
                    new SelectionListener() {

                        public void widgetSelected(SelectionEvent event) {
                            haHeartBeatTimeout = monitoringSpinner.getSelection() * 1000;
                        }

                        public void widgetDefaultSelected(SelectionEvent event) {
                            haHeartBeatTimeout = monitoringSpinner.getSelection() * 1000;
                        }
                    });

            lbl = new Label(compositeMonitoring, SWT.NONE);
            lbl.setText(Messages.haMonHertbeatTimeoutMsg);
            lbl.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        }

        return composite;
    }

    /** Open color dialog and show the color selected. */
    private static class ColorSelector extends SelectionAdapter {

        private final Button button;
        private final Label label;

        /**
         * Constructor of ColorSelector
         *
         * @param dbt DBStatusType
         * @param button to trigger color selected event.
         * @param label to show the color which was selected.
         */
        public ColorSelector(Button button, Label label) {
            this.button = button;
            this.label = label;
        }

        /**
         * Button clicked events.Open Color Selected dialog.
         *
         * @param event SelectionEvent
         */
        public void widgetSelected(SelectionEvent event) {
            ColorDialog colorDialog = new ColorDialog(button.getShell());
            colorDialog.setRGB(label.getBackground().getRGB());
            RGB newColor = colorDialog.open();
            if (newColor != null) {
                label.setBackground(new Color(label.getDisplay(), newColor));
            }
        }
    }

    /**
     * Init page.
     *
     * @param workbench IWorkbench
     */
    public void init(IWorkbench workbench) {
        // Do nothing.
    }

    /** Perform Apply,save colors to local. */
    protected void performApply() {
        for (DBStatusType dbt : DBStatusType.values()) {
            PREFER.setColor(
                    getDBStatusText(dbt), colorSettings.get(getDBStatusText(dbt)).getBackground());
        }

        PREFER.setHAHeartBeatTimeout(haHeartBeatTimeout);

        PREFER.save();
    }

    /**
     * Perform ok.
     *
     * @return return super performOK.
     */
    public boolean performOk() {
        performApply();
        return super.performOk();
    }

    /** Restore to defaults. */
    protected void performDefaults() {
        for (DBStatusType dbt : DBStatusType.values()) {
            colorSettings
                    .get(getDBStatusText(dbt))
                    .setBackground(PREFER.getDefaultColor(getDBStatusText(dbt)));
        }

        haHeartBeatTimeout = MonitorDashboardPreference.HA_HEARTBEAT_TIMEOUT_DEFAULT;
        if (monitoringSpinner != null) {
            monitoringSpinner.setSelection(haHeartBeatTimeout / 1000);
        }
    }

    /**
     * Get show text of db status type
     *
     * @param dbt DBStatusType
     * @return show text of db status type
     */
    private String getDBStatusText(DBStatusType dbt) {
        return DBStatusType.getShowText(dbt);
    }
}
