package com.cubrid.common.ui.cubrid.table.dashboard.control;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.ui.cubrid.table.Messages;

public class TableDashboardLabelProvider extends LabelProvider implements
ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof TableDetailInfo) {
			TableDetailInfo tableDetail = (TableDetailInfo)element;
			if (tableDetail != null) {
				switch (columnIndex) {
					case 0 : return tableDetail.getTableName();
					case 1 : return tableDetail.getTableDesc();
					case 2 : return String.valueOf(tableDetail.getRecordsCount() < 0? Messages.tablesDetailInfoPartNotEstimated : tableDetail.getRecordsCount());
					case 3 : return String.valueOf(tableDetail.getColumnsCount() == 0 ? Messages.tablesDetailInfoPartNotRunColumn : tableDetail.getColumnsCount());
					case 4 : return String.valueOf(tableDetail.getPkCount() == -1 ? Messages.tablesDetailInfoPartNotRunKey : "Y");
					case 5 : return String.valueOf(tableDetail.getUkCount() == -1 ? Messages.tablesDetailInfoPartNotRunKey : tableDetail.getUkCount());
					case 6 : return String.valueOf(tableDetail.getFkCount() == -1 ? Messages.tablesDetailInfoPartNotRunKey : tableDetail.getFkCount());
					case 7 : return String.valueOf(tableDetail.getIndexCount() == -1 ? Messages.tablesDetailInfoPartNotRunKey : tableDetail.getIndexCount());
					case 8 : return convertSize(tableDetail.getRecordsSize(), tableDetail.isHasUnCountColumnSize());
				}
			}
		}
		return null;
	}

	private String convertSize (BigDecimal size, boolean hasUncountedColumnSize) { // FIXME move this logic to core module
		if (size == null) {
			return Messages.tablesDetailInfoPartNotRunSize;
		}

		String result = "";

		BigDecimal kbytes = new BigDecimal(1024);
		BigDecimal mbytes = new BigDecimal(1024 * 1024);
		BigDecimal gbytes = new BigDecimal(1024 * 1024 * 1024);

		if (size.compareTo(gbytes) > 0) {
			result = size.divide(gbytes).toBigInteger().toString();
			result += " GB";
		} else if (size.compareTo(mbytes) > 0) {
			result = size.divide(mbytes).toBigInteger().toString();
			result += " MB";
		} else if (size.compareTo(kbytes) > 0) {
			result = size.divide(kbytes).toBigInteger().toString();
			result += " KB";
		} else if (size.compareTo(kbytes) < 1) {
			result = size.toBigInteger().toString();
			result += " B";
		} else if (size.longValue() < 0) {
			return "-";
		} else if (size.longValue() == 0) {
			return "0";
		} else {
			result = size.toBigInteger().toString() + " b";
		}

		if (hasUncountedColumnSize) {
			result += "+";
		}

		return result;
	}
}
