/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.cubrid.jobauto.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.ArrayUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.cubridmanager.ui.cubrid.jobauto.Messages;

/**
 * A group that depict some widget in some class with composite such as
 * EditBackupPlanDialog,EditQueryPlanDialog and so on
 *
 * @author lizhiqiang 2009-3-24
 */
public class PeriodGroup extends
		Observable {

	private String msgPeriodGroup;
	private String msgPeriodTypeLbl;
	private String msgPeriodDetailLbl;
	private String msgPeriodTimeLbl;
	private String tipPeriodDetailCombo;
	private String tipPeriodDetailText;

	private String specialdayPeriodType;

	private final CMTitleAreaDialog dialog;
	private Combo typeCombo;
	private Label detailLabel;
	private Combo detailCombo;
	private Text detailText;
	private Combo timeCombo;
	private Combo intervalCombo;

	//	private Button btnMutliTimeCheck;
	private Button btnSpecificRadio;
	private Button btnPeriodicRadio;
	private Button[] btnDateChecks;
	private Group detailGroup;

	private final String[] itemsOfTypeCombo;
	private String[] itemsOfDetailsCombo;
	private final String[] itemsOfDetailsComboForMon;
	private final String[] itemsOfDetailsComboForWeek;
	private final String[] itemsOfTimeCombo;
	private final String[] itemsOfIntervalCombo;
	private String typeValue;
	// for support multi date
	private List<String> detailList = new ArrayList<String>();
	private String timeValue;
	private String intervalValue;

	// isAllow[0] for detail(date) value;
	// isAllow[1] for time value;
	// isAllow[2] for special date check;
	// isAllow[3] for check whether detail value equal hint ;
	// isAllow[4] for interval value;
	// isAllow[5] for date radio group check;
	private boolean isAllow[];

	private boolean isEditAble;

	private boolean isSupportPeriodic = false;
	private boolean isPeriodicEnabled = false;
	// For time style in request/response, if style is 'HH:MM', then true, if 'HHMM', then false.
	private boolean isTimeSplitByColon = false;
	private int colCountPerLine = 4;
	private int typeComboIndex = 0;
	private final int specialDateLimit = 20;

	/**
	 * Constructor
	 *
	 * @param dialog
	 */
	public PeriodGroup(CMTitleAreaDialog dialog, boolean isEditAble) {
		this.dialog = dialog;
		this.isEditAble = isEditAble;

		specialdayPeriodType = isSupportPeriodic ? Messages.specialdaysPeriodType
				: Messages.specialdayPeriodType;

		msgPeriodGroup = Messages.msgPeriodGroup;
		msgPeriodTypeLbl = Messages.msgPeriodTypeLbl;
		msgPeriodDetailLbl = Messages.msgPeriodDetailLbl;
		msgPeriodTimeLbl = Messages.msgPeriodTimeLbl;
		tipPeriodDetailCombo = Messages.tipPeriodDetailCombo;
		tipPeriodDetailText = Messages.tipPeriodDetailText;
		itemsOfTypeCombo = new String[] {Messages.monthlyPeriodType,
				Messages.weeklyPeriodType, Messages.dailyPeriodType,
				specialdayPeriodType };
		itemsOfDetailsComboForMon = new String[31];
		for (int i = 0; i < 31; i++) {
			itemsOfDetailsComboForMon[i] = Integer.toString(i + 1);
		}
		// Initials the value of typeValue and detailValue
		itemsOfDetailsComboForWeek = new String[] {Messages.sundayOfWeek,
				Messages.mondayOfWeek, Messages.tuesdayOfWeek,
				Messages.wednesdayOfWeek, Messages.thursdayOfWeek,
				Messages.fridayOfWeek, Messages.saturdayOfWeek };
		typeValue = itemsOfTypeCombo[0];
		// Sets itemsOfDetailsComboForMon as itemsOfDetailsCombo
		itemsOfDetailsCombo = itemsOfDetailsComboForMon;
		detailList.add(itemsOfDetailsCombo[0]);

		itemsOfTimeCombo = new String[48];
		for (int i = 0; i < 48; i++) {
			String hour = StringUtil.leftPad(Integer.toString(i / 2), '0', 2);
			String min = StringUtil.leftPad(Integer.toString(i % 2 * 30), '0', 2);
			itemsOfTimeCombo[i] = hour + ":" + min;
		}
		timeValue = itemsOfTimeCombo[25];

		itemsOfIntervalCombo = new String[18];
		for (int i = 0; i < itemsOfIntervalCombo.length; i++) {
			if (i < 6) {
				itemsOfIntervalCombo[i] = Integer.toString((i + 1) * 10);
			} else if (i < 8) {
				itemsOfIntervalCombo[i] = Integer.toString((i - 3) * 30);
			} else {
				itemsOfIntervalCombo[i] = Integer.toString((i - 5) * 60);
			}
		}
		intervalValue = itemsOfIntervalCombo[5];

		isAllow = new boolean[6];
		for (int i = 0; i < isAllow.length; i++) {
			isAllow[i] = true;
		}
	}

	/**
	 * Create a period group
	 *
	 * @param composite Composite
	 */
	public void createPeriodGroup(Composite composite) {
		if (isSupportPeriodic) {
			colCountPerLine = 2;
		}

		final Group periodGroup = new Group(composite, SWT.RESIZE);
		GridLayout periodGpLayout = new GridLayout();
		periodGpLayout.verticalSpacing = 0;
		periodGroup.setLayout(periodGpLayout);
		final GridData gdPeriodGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
		periodGroup.setLayoutData(gdPeriodGroup);
		periodGroup.setText(msgPeriodGroup);

		Composite periodComp = new Composite(periodGroup, SWT.RESIZE);
		final GridLayout periodGridLayout = new GridLayout(colCountPerLine, false);
		periodComp.setLayout(periodGridLayout);
		periodComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label typelabel = new Label(periodComp, SWT.RESIZE);
		final GridData gdTypeLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdTypeLabel.widthHint = 80;
		typelabel.setLayoutData(gdTypeLabel);
		typelabel.setText(msgPeriodTypeLbl);

		typeCombo = new Combo(periodComp, SWT.READ_ONLY);
		final GridData gdTypeCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdTypeCombo.widthHint = 135;
		typeCombo.setLayoutData(gdTypeCombo);
		typeCombo.setItems(itemsOfTypeCombo);
		typeCombo.setText(typeValue);
		typeCombo.addModifyListener(new TypeComboModifyListener());

		if (!isSupportPeriodic) {
			// initials detailCombo
			detailLabel = new Label(periodComp, SWT.RESIZE);
			final GridData gdDetailLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
			gdDetailLabel.widthHint = 80;
			detailLabel.setLayoutData(gdDetailLabel);
			detailLabel.setText(msgPeriodDetailLbl);

			detailCombo = new Combo(periodComp, SWT.RESIZE);
			final GridData gdDetailCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdDetailCombo.widthHint = 135;
			detailCombo.setLayoutData(gdDetailCombo);

			if (typeValue.equalsIgnoreCase(itemsOfTypeCombo[0])) {
				itemsOfDetailsCombo = itemsOfDetailsComboForMon;
			} else if (typeValue.equalsIgnoreCase(itemsOfTypeCombo[1])) {
				itemsOfDetailsCombo = itemsOfDetailsComboForWeek;
			} else if (typeValue.equalsIgnoreCase(itemsOfTypeCombo[2])) {
				detailLabel.setVisible(false);
				detailCombo.setVisible(false);
			} else if (typeValue.equalsIgnoreCase(itemsOfTypeCombo[3])) {
				itemsOfDetailsCombo = new String[]{tipPeriodDetailCombo };
				detailCombo.setText(tipPeriodDetailCombo);
				detailCombo.setToolTipText(tipPeriodDetailCombo);
			}

			detailCombo.setItems(itemsOfDetailsCombo);
			detailCombo.setText(detailList.get(0));
			detailCombo.addModifyListener(new DetailComboModifyListener());

			addTimeItemToComp(periodComp);

			if (!isEditAble) {
				typeCombo.setEnabled(false);
				detailCombo.setEnabled(false);
				timeCombo.setEnabled(false);
				detailCombo.setEnabled(false);
			}
		} else {
			addNewPeriodGroupItem(periodGroup);
			if (!isEditAble) {
				typeCombo.setEnabled(false);
				detailText.setEnabled(false);
				timeCombo.setEnabled(false);
			}
		}
	}

	private void addTimeItemToComp(Composite composite) {
		Label backupHourLabel = null;
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData.widthHint = 80;

		if (isBackupPlanDialog()) {
			Composite backupTimeComp = new Composite(composite, SWT.RESIZE);
			final GridLayout layout = new GridLayout(colCountPerLine, false);
			backupTimeComp.setLayout(layout);
			backupTimeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			backupHourLabel = new Label(backupTimeComp, SWT.RESIZE);
			timeCombo = new Combo(backupTimeComp, SWT.BORDER);
		} else {
			backupHourLabel = new Label(composite, SWT.RESIZE);
			timeCombo = new Combo(composite, SWT.BORDER);
		}

		backupHourLabel.setLayoutData(gridData);
		backupHourLabel.setText(msgPeriodTimeLbl);

		timeCombo.setItems(itemsOfTimeCombo);
		final GridData gdTimeCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdTimeCombo.widthHint = 80;
		timeCombo.setLayoutData(gdTimeCombo);
		timeCombo.setText(timeValue);
		timeCombo.setToolTipText(Messages.timeToolTip);
		timeCombo.addVerifyListener(new TimeComboVerifyListener());
		timeCombo.addModifyListener(new TimeModifyListener());
		timeCombo.setEnabled(!isPeriodicEnabled);
	}

	private boolean isBackupPlanDialog() {
		return msgPeriodTimeLbl.equals(Messages.msgPeriodTimeLbl);
	}

	private void addNewPeriodGroupItem(Composite composite) {
		addDetailGroupToComp(composite);
		if (isBackupPlanDialog()) {
			addTimeItemToComp(composite);
		} else {
			addTimeTypeGroupForEditQuery(composite);
		}
	}

	private void addTimeTypeGroupForEditQuery(Composite composite) {
		final Group timeTypeGroup = new Group(composite, SWT.RESIZE);
		GridLayout timeTypeGpLayout = new GridLayout();
		timeTypeGpLayout.verticalSpacing = 0;
		timeTypeGroup.setLayout(timeTypeGpLayout);
		final GridData gdTimeTypeGroup = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		timeTypeGroup.setLayoutData(gdTimeTypeGroup);
		timeTypeGroup.setText(Messages.msgTimeTypeGp);

		Composite timeTypeComp = new Composite(timeTypeGroup, SWT.RESIZE);
		final GridLayout timeTypeGridLayout = new GridLayout(4, false);
		timeTypeComp.setLayout(timeTypeGridLayout);
		timeTypeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		btnSpecificRadio = new Button(timeTypeComp, SWT.RADIO);
		btnSpecificRadio.setText(Messages.msgSpecificRadioBtn);
		btnSpecificRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
		btnSpecificRadio.setSelection(true);
		btnSpecificRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (btnSpecificRadio.getSelection()) {
					timeCombo.setEnabled(true);
					validTimeCombo();
					intervalCombo.setEnabled(false);
					isPeriodicEnabled = false;
					isAllow[4] = true;
					notifyDialog();
				}
			}
		});

		addTimeItemToComp(timeTypeComp);

		btnPeriodicRadio = new Button(timeTypeComp, SWT.RADIO);
		btnPeriodicRadio.setText(Messages.msgPeriodicRadioBtn);
		btnPeriodicRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
		btnPeriodicRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (btnPeriodicRadio.getSelection()) {
					intervalCombo.setEnabled(true);
					validIntervalCombo();
					timeCombo.setEnabled(false);
					isPeriodicEnabled = true;
					isAllow[1] = true;
					notifyDialog();
				}
			}
		});

		final Label intervalLabel = new Label(timeTypeComp, SWT.RESIZE);
		final GridData gdIntervalLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdIntervalLabel.widthHint = 80;
		intervalLabel.setLayoutData(gdIntervalLabel);
		intervalLabel.setText(Messages.msgIntervalLbl);

		intervalCombo = new Combo(timeTypeComp, SWT.BORDER);
		intervalCombo.setItems(itemsOfIntervalCombo);
		final GridData gdIntervalCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdIntervalCombo.widthHint = 80;
		intervalCombo.setLayoutData(gdIntervalCombo);
		intervalCombo.setText(intervalValue);
		intervalCombo.setToolTipText(Messages.tipIntervalCombo);

		intervalCombo.addVerifyListener(new IntervalComboVerifyListener());
		intervalCombo.addModifyListener(new IntervalModifyListener());

		//set default status
		btnSpecificRadio.setSelection(!isPeriodicEnabled);
		btnPeriodicRadio.setSelection(isPeriodicEnabled);
		intervalCombo.setEnabled(isPeriodicEnabled);
	}

	private void addDetailGroupToComp(Composite composite) {
		detailGroup = new Group(composite, SWT.RESIZE);
		GridLayout detailGpLayout = new GridLayout();
		detailGpLayout.verticalSpacing = 0;
		detailGroup.setLayout(detailGpLayout);
		final GridData gdDetailGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
		detailGroup.setLayoutData(gdDetailGroup);
		detailGroup.setText(msgPeriodDetailLbl);

		//find index according to typeValue
		int index = 0;
		for (int i = 0; i < itemsOfTypeCombo.length; i++) {
			if (itemsOfTypeCombo[i].equals(typeValue)) {
				index = i;
				break;
			}
		}
		redrawDetailGroup(index, true);
	}

	/**
	 * A class that response the change of typeCombo
	 *
	 * @author lizhiqing
	 * @version 1.0 - 2009-12-28 created by lizhiqing
	 */
	private class TypeComboModifyListener implements ModifyListener {
		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 * @param event an event containing information about the modify
		 */
		public void modifyText(ModifyEvent event) {
			detailsForType(typeCombo);
		}

	}

	private class DateCheckBoxSelectionListener implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			int index = typeCombo.getSelectionIndex();
			boolean isChecked = false;
			if (isSupportPeriodic && (index == 0 || index == 1)) {
				for (Button btn : btnDateChecks) {
					if (btn.getSelection()) {
						isChecked = true;
					}
				}
				isAllow[5] = isChecked;
				notifyDialog();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	/**
	 * A class that response the change of detailCombo
	 *
	 * @author lizhiqing
	 * @version 1.0 - 2009-12-28 created by lizhiqing
	 */
	public class DetailComboModifyListener implements ModifyListener {
		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 * @param event an event containing information about the modify
		 */
		public void modifyText(ModifyEvent event) {
			String text = detailCombo.getText().trim();
			if (validateDetailCombo(detailCombo)) {
				if (text.equals(tipPeriodDetailCombo)) {
					isAllow[3] = false;
				} else {
					isAllow[0] = true;
					isAllow[3] = true;
				}
			} else {
				isAllow[0] = false;
			}
			notifyDialog();
		}
	}

	/**
	 * A class that response the change of detailText
	 *
	 * @author Santiago Wang
	 * @version 1.0 - 2013-07-12 created by Wang Xi
	 */
	public class DetailTextModifyListener implements ModifyListener {
		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 * @param event an event containing information about the modify
		 */
		public void modifyText(ModifyEvent event) {
			if (validateDetailText(detailText)) {
				isAllow[2] = true;
			} else {
				isAllow[2] = false;
			}
			notifyDialog();
		}

	}

	/**
	 * A class that response the change of hourSpinner
	 *
	 * @author lizhiqing
	 * @version 1.0 - 2009-12-28 created by lizhiqing
	 */
	private class TimeModifyListener implements ModifyListener {
		/**
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 * @param event an event containing information about the modify
		 */
		public void modifyText(ModifyEvent event) {
			validTimeCombo();
		}
	}

	/**
	 * Check whether value in time combo is valid or not. If not, set reference
	 * element in isAllow array to false, and notify dialog.
	 */
	private void validTimeCombo() {
		String rawTime = timeCombo.getText().trim();
		if (rawTime.length() == 0) {
			isAllow[1] = false;
		} else {
			char splitter = ':';
			int index = rawTime.indexOf(splitter);
			if (index < 1
					|| rawTime.length() == index + 1
					|| index != rawTime.lastIndexOf(splitter)) {
				isAllow[1] = false;
			} else {
				int hour = Integer.valueOf(rawTime.substring(0, index));
				int min = Integer.valueOf(rawTime.substring(index + 1));
				if (hour > 23 || hour < 0 || min < 0 || min > 59) {
					isAllow[1] = false;
				} else {
					isAllow[1] = true;
				}
			}
		}
		notifyDialog();
	}

	private class IntervalModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			validIntervalCombo();
		}
	}

	/**
	 * Check whether value in interval combo is valid or not. If not, set
	 * reference element in isAllow array to false, and notify dialog.
	 */
	private void validIntervalCombo() {
		String value = intervalCombo.getText();
		if (value.trim().length() == 0) {
			isAllow[4] = false;
		} else {
			int interval = Integer.valueOf(value);
			if (interval < 1 || interval >= 1440) {
				isAllow[4] = false;
			} else {
				isAllow[4] = true;
			}
		}
		notifyDialog();
	}

	/**
	 *
	 * Sets the value of details when changing the item of typeCombo
	 *
	 * @param combo Combo
	 */
	private void detailsForType(Combo combo) {
		int selectionIndex = combo.getSelectionIndex();
		if (!isSupportPeriodic) {
			detailLabel.setVisible(true);
			detailCombo.setVisible(true);
			detailCombo.setToolTipText("");
			switch (selectionIndex) {
			case 0:
				itemsOfDetailsCombo = itemsOfDetailsComboForMon;
				detailCombo.setItems(itemsOfDetailsCombo);
				detailCombo.setText(itemsOfDetailsCombo[0]);
				break;
			case 1:
				itemsOfDetailsCombo = itemsOfDetailsComboForWeek;
				detailCombo.setItems(itemsOfDetailsCombo);
				detailCombo.setText(itemsOfDetailsCombo[0]);
				break;
			case 2:
				detailLabel.setVisible(false);
				detailCombo.setVisible(false);
				isAllow[0] = true;
				isAllow[3] = true;
				notifyDialog();
				detailCombo.setText("nothing");
				break;
			case 3:
				itemsOfDetailsCombo = new String[]{tipPeriodDetailCombo };
				detailCombo.setToolTipText(tipPeriodDetailCombo);
				detailCombo.setItems(itemsOfDetailsCombo);
				detailCombo.setText(itemsOfDetailsCombo[0]);
				break;
			default:
			}

		} else {
			// avoid to redraw same item
			if (selectionIndex == typeComboIndex) {
				return;
			}
			typeComboIndex = selectionIndex;
			redrawDetailGroup(selectionIndex, false);
			detailGroup.getParent().getParent().layout();
		}
	}

	private void redrawDetailGroup(final int index, final boolean isInitial) {
		int btnCnt = 0;
		// Dispose children of detail group
		for (Control comp : detailGroup.getChildren()) {
			comp.dispose();
		}

		int colItemCnt = 4;
		switch (index) {
		case 0:
			colItemCnt = 8;
			break;
		case 1:
		case 2:
			break;
		case 3:
			colItemCnt = 2;
			break;
		default:
		}

		Composite detailComp = new Composite(detailGroup, SWT.RESIZE);
		final GridLayout detailCompGridLayout = new GridLayout(colItemCnt, false);
		detailComp.setLayout(detailCompGridLayout);
		detailComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		switch (index) {
		case 0:
			detailGroup.setVisible(true);

			btnCnt = itemsOfDetailsComboForMon.length;
			btnDateChecks = new Button[btnCnt];
			for (int i = 0; i < btnCnt; i++) {
				btnDateChecks[i] = new Button(detailComp, SWT.CHECK);
				btnDateChecks[i].setText(itemsOfDetailsComboForMon[i]);
				btnDateChecks[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				btnDateChecks[i].addSelectionListener(new DateCheckBoxSelectionListener());
				if (isInitial && detailList.contains(itemsOfDetailsComboForMon[i])) {
					btnDateChecks[i].setSelection(true);
					if (!isEditAble) {
						btnDateChecks[i].setEnabled(false);
					}
				} else if (!isInitial && i == 0) {
					btnDateChecks[i].setSelection(true);
				}
			}
			break;
		case 1:
			detailGroup.setVisible(true);

			btnCnt = itemsOfDetailsComboForWeek.length;
			btnDateChecks = new Button[btnCnt];

			for (int i = 0; i < btnCnt; i++) {
				btnDateChecks[i] = new Button(detailComp, SWT.CHECK);
				btnDateChecks[i].setText(itemsOfDetailsComboForWeek[i]);
				btnDateChecks[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				btnDateChecks[i].addSelectionListener(new DateCheckBoxSelectionListener());
				if (isInitial && detailList.contains(itemsOfDetailsComboForWeek[i])) {
					btnDateChecks[i].setSelection(true);
					if (!isEditAble) {
						btnDateChecks[i].setEnabled(false);
					}
				} else if (!isInitial && i == 0) {
					btnDateChecks[i].setSelection(true);
				}
			}
			break;
		case 2:
			detailGroup.setVisible(false);
			isAllow[0] = true;
			isAllow[3] = true;
			break;
		case 3:
			detailGroup.setVisible(true);

			detailLabel = new Label(detailComp, SWT.RESIZE);
			final GridData gdDetailLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
			gdDetailLabel.widthHint = 80;
			detailLabel.setLayoutData(gdDetailLabel);
			detailLabel.setText(Messages.msgPeriodYmdLbl);

			detailText = new Text(detailComp, SWT.BORDER);
			final GridData gdDetailCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
			detailText.setLayoutData(gdDetailCombo);
			detailText.setToolTipText(tipPeriodDetailText);

			if (isInitial) {
				detailText.setText(ArrayUtil.collectionToCSString(detailList));
				if (!isEditAble) {
					detailText.setEnabled(false);
				}
			} else {
				detailText.setText(tipPeriodDetailCombo);
				isAllow[2] = false;
				notifyDialog();
			}

			detailText.addModifyListener(new DetailTextModifyListener());
			break;
		default:
		}
		if (!isInitial && index != 3) {
			isAllow[0] = true;
			isAllow[2] = true;
			isAllow[5] = true;
			notifyDialog();
		} else {
			typeComboIndex = index;
		}
	}

	/**
	 * Validates the value of detail combo
	 *
	 * @param combo Combo
	 * @return boolean
	 */
	private boolean validateDetailCombo(Combo combo) {
		boolean returnvalue = true;
		String text = combo.getText().trim();
		switch (typeCombo.getSelectionIndex()) {
		case 2:
			returnvalue = true;
			break;
		case 3:
			if (!text.matches(verifyTime())) {
				returnvalue = false;
			}
			break;
		default:
			boolean exist = Arrays.asList(itemsOfDetailsCombo).contains(text);
			if (!exist) {
				returnvalue = false;
			}

		}
		return returnvalue;
	}

	private boolean validateDetailText(Text text) {
		boolean isValid = true;
		String detail = text.getText().trim();
		if (detail.endsWith(",")) {
			isValid = false;
			return isValid;
		}
		String[] tempAr = detail.split(",");
		if (tempAr.length > specialDateLimit) {
			isValid = false;
			return isValid;
		}
		for (String s : tempAr) {
			if (!s.matches(verifyTime())) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}

	/**
	 * A class that response the change of Time Combo
	 *
	 * @author Santiago Wang
	 * @version 1.0 - 2013-07-18 created by Santiago Wang
	 */
	private static class TimeComboVerifyListener implements VerifyListener {
		/**
		 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
		 * @param event an event containing information about the verify
		 */
		public void verifyText(VerifyEvent event) {
			String text = event.text;
			if ("".equals(text)) {
				return;
			}
			if (text.matches("^[\\d\\:]*$")) {
				event.doit = true;
			} else {
				event.doit = false;
			}
		}
	}

	/**
	 * A class that response the change of Interval Combo
	 *
	 * @author lizhiqing
	 * @version 1.0 - 2009-12-28 created by lizhiqing
	 */
	private static class IntervalComboVerifyListener implements VerifyListener {
		/**
		 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
		 * @param event an event containing information about the verify
		 */
		public void verifyText(VerifyEvent event) {
			String text = event.text;
			if (("").equals(text)) {
				return;
			}
			if (text.matches("^\\d+$")) {
				event.doit = true;
			} else {
				event.doit = false;
			}
		}
	}

	public void setMsgPeriodGroup(String msgPeriodGroup) {
		this.msgPeriodGroup = msgPeriodGroup;
	}

	public void setMsgPeriodTypeLbl(String msgPeriodTypeLbl) {
		this.msgPeriodTypeLbl = msgPeriodTypeLbl;
	}

	public void setMsgPeriodDetailLbl(String msgPeriodDetailLbl) {
		this.msgPeriodDetailLbl = msgPeriodDetailLbl;
	}

	public void setMsgPeriodTimeLbl(String msgPeriodTimeLbl) {
		this.msgPeriodTimeLbl = msgPeriodTimeLbl;
	}

	public void setTipPeriodDetailCombo(String tipPeriodDetailCombo) {
		this.tipPeriodDetailCombo = tipPeriodDetailCombo;
	}

	/**
	 * Gets time value by hourSpinner and minuteSpinner
	 *
	 * @return string
	 */
	public String getTime() {
		StringBuilder time = new StringBuilder();
		if (!isPeriodicEnabled) {
			String rawTime = timeCombo.getText().trim();
			int index = rawTime.indexOf(':');
			String hour = StringUtil.leftPad(rawTime.substring(0, index), '0', 2);
			String min = StringUtil.leftPad(rawTime.substring(index + 1), '0', 2);
			time.append(hour);
			if (isTimeSplitByColon) {
				time.append(':');
			}
			time.append(min);
		} else {
			time.append("i").append(getInterval());
		}
		return time.toString();
	}

	public String getInterval() {
		return intervalCombo.getText().trim();
	}

	/**
	 * @param type the typeValue to set
	 */
	public void setTypeValue(String type) {
		if (type.equalsIgnoreCase("Monthly")) {
			this.typeValue = Messages.monthlyPeriodType;
		} else if (type.equalsIgnoreCase("Weekly")) {
			this.typeValue = Messages.weeklyPeriodType;
		} else if (type.equalsIgnoreCase("Daily")) {
			this.typeValue = Messages.dailyPeriodType;
		} else if (type.equalsIgnoreCase("Special")) {
			this.typeValue = specialdayPeriodType;
		} else {
			this.typeValue = type;
		}
	}

	/**
	 * @param detail the detailValue to set
	 */
	public void setDetailValue(String detail) {
		this.detailList.clear();
		if (detail == null) {
			return;
		}
		if (detail.indexOf(",") == -1) {
			this.detailList.add(getMatchedDateValue(detail));
		} else {
			String[] dates = detail.split(",");
			for (String date : dates) {
				this.detailList.add(getMatchedDateValue(date));
			}
		}
	}

	private String getMatchedDateValue(String date) {
		String result;
		if (date.equalsIgnoreCase("sunday")) {
			result = Messages.sundayOfWeek;
		} else if (date.equalsIgnoreCase("Monday")) {
			result = Messages.mondayOfWeek;
		} else if (date.equalsIgnoreCase("Tuesday")) {
			result = Messages.tuesdayOfWeek;
		} else if (date.equalsIgnoreCase("Wednesday")) {
			result = Messages.wednesdayOfWeek;
		} else if (date.equalsIgnoreCase("Thursday")) {
			result = Messages.thursdayOfWeek;
		} else if (date.equalsIgnoreCase("Friday")) {
			result = Messages.fridayOfWeek;
		} else if (date.equalsIgnoreCase("Saturday")) {
			result = Messages.saturdayOfWeek;
		} else {
			result = date;
		}
		return result;
	}

	public boolean isPeriodicEnabled() {
		return isPeriodicEnabled;
	}

	/**
	 * @param hourValue the hourValue to set
	 */
	public void setTimeValue(String timeValue) {
		if (isTimeSplitByColon) {
			this.timeValue = timeValue;
		} else {
			this.timeValue = timeValue.substring(0, 2) + ':'
					+ timeValue.substring(2);
		}

		this.isPeriodicEnabled = false;
	}

	/**
	 * Set interval value
	 *
	 * @param intervalValue
	 */
	public void setIntervalValue(int intervalValue) {
		this.intervalValue = Integer.toString(intervalValue);
		this.isPeriodicEnabled = true;
	}

	/**
	 * Gest the tip of tipPeriodDetailCombo
	 *
	 * @return the tipPeriodDetailCombo
	 */
	public String getTipPeriodDetailCombo() {
		return tipPeriodDetailCombo;
	}

	/**
	 * Gets the text of typeCombo
	 *
	 * @return the typeCombo
	 */
	public String getTextOfTypeCombo() {
		String returnType = "";
		String type = typeCombo.getText().trim();
		if (type.equalsIgnoreCase(Messages.monthlyPeriodType)) {
			returnType = "Monthly";
		} else if (type.equalsIgnoreCase(Messages.weeklyPeriodType)) {
			returnType = "Weekly";
		} else if (type.equalsIgnoreCase(Messages.dailyPeriodType)) {
			returnType = "Daily";
		} else if (type.equalsIgnoreCase(specialdayPeriodType)) {
			returnType = "Special";
		} else {
			returnType = type;
		}
		return returnType;
	}

	/**
	 * Gets the value of detail item
	 *
	 * @return the detail value
	 */
	public String getDetailValue() {
		String returnDetail = "";
		if (!isSupportPeriodic) {
			String detail = detailCombo.getText().trim();
			returnDetail = getMatchedReturnValue(detail);
		} else {
			List<String> list = new ArrayList<String>();
			switch (typeCombo.getSelectionIndex()) {
			case 0:
				for (Button btn : btnDateChecks) {
					if (btn.getSelection()) {
						list.add(btn.getText());
					}
				}
				returnDetail = ArrayUtil.collectionToCSString(list);
				break;
			case 1:
				for (Button btn : btnDateChecks) {
					if (btn.getSelection()) {
						list.add(getMatchedReturnValue(btn.getText()));
					}
				}
				returnDetail = ArrayUtil.collectionToCSString(list);
				break;
			case 2:
				returnDetail = "nothing";
				break;
			case 3:
				returnDetail = detailText.getText().trim();
				break;
			default:
			}
		}

		return returnDetail;
	}

	private String getMatchedReturnValue(String detailVal) {
		String result = "";
		if (detailVal.equalsIgnoreCase(Messages.sundayOfWeek)) {
			result = "Sunday";
		} else if (detailVal.equalsIgnoreCase(Messages.mondayOfWeek)) {
			result = "Monday";
		} else if (detailVal.equalsIgnoreCase(Messages.tuesdayOfWeek)) {
			result = "Tuesday";
		} else if (detailVal.equalsIgnoreCase(Messages.wednesdayOfWeek)) {
			result = "Wednesday";
		} else if (detailVal.equalsIgnoreCase(Messages.thursdayOfWeek)) {
			result = "Thursday";
		} else if (detailVal.equalsIgnoreCase(Messages.fridayOfWeek)) {
			result = "Friday";
		} else if (detailVal.equalsIgnoreCase(Messages.saturdayOfWeek)) {
			result = "Saturday";
		} else {
			result = detailVal;
		}
		return result;
	}

	/**
	 * If some control has changed, notify the relevant Observer(Dialog)
	 *
	 * @param string
	 */
	public void notifyDialog() {
		setChanged();
		boolean allow = true;
		for (int k = 0; k < isAllow.length; k++) {
			allow = allow && isAllow[k];
		}
		notifyObservers(allow);
	}

	/**
	 * Gets the regular expressions of "yyyy-mm-dd"
	 *
	 * @return string
	 */
	private String verifyTime() {
		return "^((([2-9]\\d{3})-(0[13578]|1[02])-"
				+ "(0[1-9]|[12]\\d|3[01]))|(([2-9]\\d{3})-"
				+ "(0[469]|11)-(0[1-9]|[12]\\d|30))|(([2-9]\\d{3})-"
				+ "0?2-(0[1-9]|1\\d|2[0-8]))|((([2-9]\\d)(0[48]|[2468]"
				+ "[048]|[13579][26])|((16|[2468][048]|[3579][26])00))-02-29))$";
	}

	/**
	 * Get is allow
	 *
	 * @return boolean[]
	 */
	public boolean[] getAllow() {
		return this.isAllow == null ? null : (boolean[]) this.isAllow.clone();
	}

	/**
	 * Enable the "OK" button,called by caller
	 */
	public void enableOk() {
		if (!isAllow[0]) {
			dialog.setErrorMessage(Messages.errDetailTextMsg);
			return;
		}
		if (!isAllow[1]) {
			dialog.setErrorMessage(Messages.errTimeTextMsg);
			return;
		}
		if (!isAllow[2]) {
			dialog.setErrorMessage(Messages.bind(
					Messages.errSpecialDatesTextMsg,
					Integer.toString(specialDateLimit + 1)));
			return;
		}
		if (!isAllow[4]) {
			dialog.setErrorMessage(Messages.tipIntervalCombo);
			return;
		}
		if (!isAllow[5]) {
			dialog.setErrorMessage(Messages.tipDateCheckGroup);
			return;
		}
		dialog.setErrorMessage(null);
	}

	/**
	 * Set isSupportPeriodic. And update the text of period type specific day.
	 *
	 * @param isSupportPeriodic
	 */
	public void setSupportPeriodic(boolean isSupportPeriodic) {
		this.isSupportPeriodic = isSupportPeriodic;
		this.specialdayPeriodType = isSupportPeriodic ? Messages.specialdaysPeriodType
				: Messages.specialdayPeriodType;
		this.itemsOfTypeCombo[3] = this.specialdayPeriodType;
	}

	/**
	 * Set whether time value for request/response is 'HH:MM' or 'HHMM'. If
	 * front one, value should be true.
	 *
	 * @param isTimeSplitByColon
	 */
	public void setTimeSplitByColon(boolean isTimeSplitByColon) {
		this.isTimeSplitByColon = isTimeSplitByColon;
	}
}
