/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.common.ui.query.control.QueryExecuter;
import com.cubrid.common.ui.query.result.QueryResultFilterSetting.MatchType;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.table.CellValue;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;

/**
 *
 * Result set filter contribution item
 *
 * @author pangqiren
 * @version 1.0 - 2013-3-19 created by pangqiren
 */
public class FilterResultContrItem extends ControlContribution {
	public static final String ID = FilterResultContrItem.class.getName();
	private final QueryExecuter qe;

	private Text text;
	private Button dropDownButton;
	private Combo pageLimitCombo;
	private Menu filterMenu;
	private int pageLimit;

	private MenuItem caseSensitiveItem;
	private MenuItem caseInSensitiveItem;
	private MenuItem useWildcardItem;
	private MenuItem useRegexItem;
	private MenuItem matchFromStartItem;
	private MenuItem matchExactItem;
	private MenuItem matchAnyWhereItem;
	private MenuItem allMenuItem;
	private List<MenuItem> colMenuItemList = new ArrayList<MenuItem>();
	private MenuItem moreMenuItem;

	/**
	 * The constructor
	 *
	 * @param qe
	 */
	public FilterResultContrItem(QueryExecuter qe, int pageLimit) {
		super(ID);
		this.qe = qe;
		this.pageLimit = pageLimit;
	}

	/**
	 * Create the content
	 *
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		comp.setLayoutData(gridData);

		final GridLayout gdLayout = new GridLayout(5, false);
		gdLayout.marginHeight = 0;
		gdLayout.marginWidth = 0;
		gdLayout.horizontalSpacing = 0;
		gdLayout.verticalSpacing = 0;
		gdLayout.marginRight = 10;
		comp.setLayout(gdLayout);

		Label lbl = new Label(comp, SWT.NONE);
		lbl.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		lbl.setText(" " + Messages.lblFilterSearch + " ");

		text = new Text(comp, SWT.NONE | SWT.BORDER | SWT.LEFT);
		text.setLayoutData(CommonUITool.createGridData(1, 1, 100, -1));
		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					doFilter();
				}
			} 
		});

		dropDownButton = new Button(comp, SWT.FLAT | SWT.DOWN);
		dropDownButton.setText(Messages.lblFilterSearchOption);
		dropDownButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleSelectionEvent();
			}
		});

		Label lblLimit = new Label(comp, SWT.NONE);
		lblLimit.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		lblLimit.setText(" " + Messages.lblSearchLimit + " ");

		pageLimitCombo = new Combo(comp, SWT.READ_ONLY);
		pageLimitCombo.setItems(new String[] { "100", "200", "500", "1000", "5000" });
		pageLimitCombo.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		pageLimitCombo.setText(Integer.toString(pageLimit));
		pageLimitCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				int limit = StringUtil.intValue(pageLimitCombo.getText());
				ServerInfo serverInfo = qe.getDatabase().getServer().getServerInfo();
				QueryOptions.setSearchUnitCount(serverInfo, limit);
			}
		});

		loadMenuItems();
		return comp;
	}

	/**
	 * Handle selection event.
	 */
	private void handleSelectionEvent() {
		Rectangle rect = dropDownButton.getBounds();
		Point pt = new Point(rect.x, rect.y + rect.height);
		pt = dropDownButton.toDisplay(pt);
		loadMenuItems();
		filterMenu.setLocation(pt);
		filterMenu.setVisible(true);
	}

	/**
	 * Load the menu items
	 */
	private void loadMenuItems() {
		if (filterMenu != null) {
			return;
		}

		filterMenu = new Menu(qe.getTblResult().getShell(), SWT.POP_UP);

		allMenuItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		allMenuItem.setText(Messages.menuAll);
		allMenuItem.setSelection(true);
		allMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (colMenuItemList == null || !allMenuItem.getSelection()) {
					return;
				}
				for (MenuItem item : colMenuItemList) {
					if (item == null) {
						continue;
					}
					item.setSelection(false);
				}
			}
		});

		List<ColumnInfo> colInfoList = qe.getAllColumnList();
		for (ColumnInfo colInfo : colInfoList) {
			final MenuItem colMenuItem = new MenuItem(filterMenu, SWT.CHECK);
			colMenuItem.setText(colInfo.getName());
			colMenuItem.setData(colInfo);
			colMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					boolean selected = colMenuItem.getSelection();
					if (selected) {
						allMenuItem.setSelection(false);
					}
				}
			});
			colMenuItemList.add(colMenuItem);
		}

		moreMenuItem = new MenuItem(filterMenu, SWT.LEFT);
		moreMenuItem.setText(Messages.menuMore);
		moreMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				List<ColumnInfo> selectedColInfoList = new ArrayList<ColumnInfo>();
				for (MenuItem colItem : colMenuItemList) {
					if ((allMenuItem.getSelection() || colItem.getSelection())
							&& colItem.getData() != null) {
						selectedColInfoList.add((ColumnInfo) colItem.getData());
					}
				}

				FilterChooserDialog dialog = new FilterChooserDialog(
						qe.getTblResult().getShell(), qe.getAllColumnList(),
						selectedColInfoList);
				if (IDialogConstants.OK_ID == dialog.open()) {
					List<ColumnInfo> colInfoList = dialog.getSelectedColInfoList();
					if (colInfoList.size() == qe.getAllColumnList().size()) {
						allMenuItem.setSelection(true);
					} else {
						allMenuItem.setSelection(false);
					}
					
					for (MenuItem colItem : colMenuItemList) {
						boolean isSelected = false;
						for (ColumnInfo colInfo : colInfoList) {
							if (colItem.getData() != null
									&& colInfo.equals(colItem.getData())) {
								isSelected = true;
								break;
							}
						}
						colItem.setSelection(isSelected);
					}
					doFilter();
				}
			}
		});

		new MenuItem(filterMenu, SWT.SEPARATOR);

		caseSensitiveItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		caseSensitiveItem.setText(Messages.menuCaseSensitive);

		caseInSensitiveItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		caseInSensitiveItem.setText(Messages.menuInCaseSensitive);
		caseInSensitiveItem.setSelection(true);
		new MenuItem(filterMenu, SWT.SEPARATOR);

		useWildcardItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		useWildcardItem.setText(Messages.menuUsingWildCards);

		useRegexItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		useRegexItem.setText(Messages.menuUsingRegex);
		new MenuItem(filterMenu, SWT.SEPARATOR);

		matchFromStartItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		matchFromStartItem.setText(Messages.menuMatchFromStart);

		matchExactItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		matchExactItem.setText(Messages.menuMatchExactly);

		matchAnyWhereItem = new MenuItem(filterMenu, SWT.CHECK | SWT.LEFT);
		matchAnyWhereItem.setText(Messages.menuMatchAnywhere);
		matchAnyWhereItem.setSelection(true);

		addSelectionListener();
	}

	/**
	 * Add selection listener
	 *
	 */
	private void addSelectionListener() {
		MenuItem[] items = filterMenu.getItems();
		for (final MenuItem item : items) {
			if (item.equals(moreMenuItem)) {
				continue;
			}
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (event.widget.equals(caseSensitiveItem)) {
						caseSensitiveItem.setSelection(true);
						caseInSensitiveItem.setSelection(false);
					} else if (event.widget.equals(caseInSensitiveItem)) {
						caseSensitiveItem.setSelection(false);
						caseInSensitiveItem.setSelection(true);
					}

					if (event.widget.equals(matchFromStartItem)) {
						matchFromStartItem.setSelection(true);
						matchExactItem.setSelection(false);
						matchAnyWhereItem.setSelection(false);
					} else if (event.widget.equals(matchExactItem)) {
						matchExactItem.setSelection(true);
						matchFromStartItem.setSelection(false);
						matchAnyWhereItem.setSelection(false);
					} else if (event.widget.equals(matchAnyWhereItem)) {
						matchAnyWhereItem.setSelection(true);
						matchFromStartItem.setSelection(false);
						matchExactItem.setSelection(false);
					}

					doFilter();
				}
			});
		}
	}

	/**
	 *
	 * Do filter
	 *
	 */
	private void doFilter() {
//		if (qe.getQueryModifier() != null && qe.getQueryModifier().isChanged()) {
//			return;
//		}
		QueryResultFilterSetting filterSetting = new QueryResultFilterSetting();
		filterSetting.setContent(text.getText());
		if (caseSensitiveItem.getSelection()) {
			filterSetting.setCaseSensitive(true);
			filterSetting.setInCaseSensitive(false);
		} else {
			filterSetting.setCaseSensitive(false);
			filterSetting.setInCaseSensitive(true);
		}

		if (useRegexItem.getSelection()) {
			filterSetting.setUsingRegex(true);
		} else {
			filterSetting.setUsingRegex(false);
		}

		if (useWildcardItem.getSelection()) {
			filterSetting.setUsingWildCard(true);
		} else {
			filterSetting.setUsingWildCard(false);
		}

		if (matchFromStartItem.getSelection()) {
			filterSetting.setMatchType(MatchType.MATCH_FROM_START);
		} else if (matchExactItem.getSelection()) {
			filterSetting.setMatchType(MatchType.MATCH_EXACTLY);
		} else if (matchAnyWhereItem.getSelection()) {
			filterSetting.setMatchType(MatchType.MATCH_ANYWHERE);
		}

		filterSetting.setSearchAllColumn(allMenuItem.getSelection());

		List<ColumnInfo> colInfoList = new ArrayList<ColumnInfo>();
		for (MenuItem colItem : colMenuItemList) {
			if ((allMenuItem.getSelection() || colItem.getSelection())
					&& colItem.getData() != null) {
				colInfoList.add((ColumnInfo) colItem.getData());
			}
		}
		filterSetting.setFilterColumnInfoList(colInfoList);

		qe.setFilterSetting(filterSetting);
		qe.makeItem();
	}

	/**
	 *
	 * Returns whether the given element makes it through this filter.
	 *
	 * @param dataMap Map<String, Object>
	 * @param filterSetting QueryResultFilterSetting
	 * @return boolean
	 */
	public boolean select(Map<String, CellValue> dataMap,
			QueryResultFilterSetting filterSetting) {
		if (filterSetting == null || filterSetting.getContent() == null
				|| filterSetting.getContent().trim().length() == 0) {
			return true;
		}
		List<ColumnInfo> colInfoList = filterSetting.getFilterColumnInfoList();
		if (colInfoList == null || colInfoList.isEmpty()) {
			return true;
		}

		for (ColumnInfo colInfo : colInfoList) {
			String columnIndex = colInfo.getIndex();
			Object colValue = dataMap.get(columnIndex);
			String colStringValue = null;
			if (colValue instanceof String) {
				colStringValue = (String) colValue;
			} else if (colValue instanceof CellValue) {
				colStringValue = ((CellValue) colValue).getShowValue();
			}
			if (isMatch(filterSetting, colStringValue, null)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Return whether this column value match the matched string
	 * @param filterSetting - The filter setting
	 * @param colStringValue - The value
	 * @param columnInfo - The value's columnInfo
	 * @return
	 */
	public boolean isMatch(QueryResultFilterSetting filterSetting,
			String colStringValue, ColumnInfo columnInfo) {

		if (StringUtil.isEmpty(colStringValue)) {
			return false;
		}
		
		String filterPattern = filterSetting == null ? null
				: filterSetting.getContent();

		if (StringUtil.isEmpty(filterPattern)) {
			return false;
		}

		if (columnInfo != null
				&& !filterSetting.isSearchAllColumn()
				&& (filterSetting.getFilterColumnInfoList() == null || !filterSetting.getFilterColumnInfoList().contains(
						columnInfo))) {
			return false;
		}

		if (filterSetting.isUsingWildCard() || filterSetting.isUsingRegex()) {
			if (filterSetting.isUsingWildCard()) {
				filterPattern = filterPattern.replaceAll("\\*", ".*").replaceAll(
						"\\?", ".?");
			}

			if (filterSetting.getMatchType() == MatchType.MATCH_FROM_START
					&& !filterPattern.startsWith("^")) {
				filterPattern = "^" + filterPattern;
				if (!filterPattern.endsWith(".*")) {
					filterPattern = filterPattern + ".*";
				}
			} else if (filterSetting.getMatchType() == MatchType.MATCH_EXACTLY) {
				if (!filterPattern.startsWith("^")) {
					filterPattern = "^" + filterPattern;
				}
				if (!filterPattern.endsWith("$")) {
					filterPattern = filterPattern + "$";
				}
			} else if (filterSetting.getMatchType() == MatchType.MATCH_ANYWHERE) {
				if (!filterPattern.startsWith(".*")) {
					filterPattern = ".*" + filterPattern;
				}
				if (!filterPattern.endsWith(".*")) {
					filterPattern = filterPattern + ".*";
				}
			}

			Pattern pattern = null;
			if (filterSetting.isInCaseSensitive()) {
				pattern = Pattern.compile(filterPattern,
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			} else {
				pattern = Pattern.compile(filterPattern, Pattern.MULTILINE);
			}
			boolean isMatch = pattern.matcher(colStringValue).matches();
			if (isMatch) {
				return true;
			}
		} else {
			String compareValue = colStringValue;
			if (filterSetting.isInCaseSensitive()) {
				compareValue = compareValue.toUpperCase();
				filterPattern = filterPattern.toUpperCase();
			}
			boolean isMatch = false;
			if (filterSetting.getMatchType() == MatchType.MATCH_FROM_START) {
				isMatch = compareValue.startsWith(filterPattern);
			} else if (filterSetting.getMatchType() == MatchType.MATCH_EXACTLY) {
				isMatch = compareValue.equals(filterPattern);
			} else if (filterSetting.getMatchType() == MatchType.MATCH_ANYWHERE) {
				isMatch = compareValue.indexOf(filterPattern) != -1;
			}
			if (isMatch) {
				return true;
			}
		}
		return false;
	}

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}
	
	public boolean isUseFilter() {
		return (text == null || text.isDisposed()) ? false : text.getText().length() > 0;
	}

	public int getSearchUnit() {
		return pageLimitCombo == null ? pageLimit : Integer.parseInt(pageLimitCombo.getText());
	}
}
