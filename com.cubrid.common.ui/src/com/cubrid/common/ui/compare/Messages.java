package com.cubrid.common.ui.compare;

import org.eclipse.osgi.util.NLS;

import com.cubrid.common.ui.CommonUIPlugin;

/**
 * Schema Compare Messages
 * 
 * @author Ray Yin
 * @version 1.0 - 2012.10.23 created by Ray Yin
 */
public class Messages extends NLS{
	
	public static String dbSchemaCompareTitle;
	public static String viewDetailInfo;
	public static String aboutViewDetailInfo;
	public static String tablesDetailCompareBtnEsitmateRecordAlert;
	public static String warnNotSelectedTables;
	public static String compareStatus;
	public static String compareStatusTip;
	public static String sourceDatabase;
	public static String sourceDatabaseTip;
	public static String targetDatabase;
	public static String targetDatabaseTip;
	public static String statusEqual;
	public static String statusDifferent;
	public static String statusMissing;
	public static String notCount;
	public static String loadDetailInfo;
	public static String recordsCountSource;
	public static String recordsCountSourceTip;
	public static String recordsCountTarget;
	public static String recordsCountTargetTip;
	public static String attrCountSource;
	public static String attrCountSourceTip;
	public static String attrCountTarget;
	public static String attrCountTargetTip;
	public static String indexCountSource;
	public static String indexCountSourceTip;
	public static String indexCountTarget;
	public static String indexCountTargetTip;
	public static String pkStatusSource;
	public static String pkStatusSourceTip;
	public static String pkStatusTarget;
	public static String pkStatusTargetTip;
	public static String viewEntireSchemaComparison;
	public static String aboutViewEntireSchemaComparison;
	public static String loadEntireSchemaComparison;
	public static String entireDbSchemaComparison;
	public static String copyWholeSchemaAlter;
	public static String copyTableSchemaAlter;
	public static String aboutCopyAlterSource;
	public static String aboutCopyAlterTarget;
	public static String emptyAlterScript;
	public static String schemaIdenticalMessage;
	public static String tableSchemaIdenticalMessage;
	public static String dbSchemaAlterCopyMessage; 
	public static String tableSchemaAlterCopyMessage; 
	public static String copySuccessfulTitle;
	public static String fromSourceToTargetLabel;
	public static String fromTargetToSourceLabel;
	public static String alterScript;
	public static String differentSchemaMsg;
	public static String diffferntDataMsg;
	public static String totallyEqualMsg;
	public static String copyTablesLeftDDL;
	public static String copyTablesRightDDL;
	public static String refreshDetailInfo;
	public static String closeAllTabs;
	public static String closeAllTabsMessage;
	public static String erwinVirtualTable;
	public static String fetchSchemaErrorFromDB;

	public static String errNeedSelectCompareDb;
	public static String errSelectSameCompareDb;
	public static String titleCompareDataWizard;
	public static String lblBtnAll;
	public static String lblBtnRange;
	public static String lblStartPos;
	public static String lblRowLimit;
	public static String btnDataCompareStart;
	public static String btnDataCompareContinue;
	public static String btnDataCompareRefresh;
	public static String errNeedSelectLogPath;
	public static String lblDataCompareLogPath;
	public static String btnDataCompareBrowse;
	public static String errNeedSelectLogPathSimple;
	public static String lblDataCompareTable;
	public static String lblDataCompareRecordSource;
	public static String lblDataCompareRecordTarget;
	public static String lblDataCompareRecordProgress;
	public static String lblDataCompareRecordMatch;
	public static String lblDataCompareRecordNoMatch;
	public static String lblDataCompareRecordNotExist;
	public static String lblDataCompareRecordError;
	public static String confirmDataCompareRefreshAlert;
	public static String errDataCompareNeedRefresh;
	public static String confirmDataCompareUseAllTables;
	public static String errDataCompareNeedRefresh2;
	public static String confirmDataCompareStart;
	public static String confirmDataCompareContinue;
	public static String confirmDataCompareStop;
	public static String lblDataCompareLogSrcLoading;
	public static String lblDataCompareLogTargetDiff;
	public static String msgDataCompareStopped;
	public static String msgDataCompareStoppedError;
	public static String msgDataCompareCompleted;
	public static String msgDataCompareRefreshingSourceRecord;
	public static String msgDataCompareRefreshingTargetRecord;
	public static String msgDataCompareRefreshingCompleted;
	public static String msgDataCompareProgressMsg;
	public static String msgLogDatePattern;
	public static String btnDataCompareStop;
	public static String titleCompareDataDetail;
	public static String btnNext;
	public static String btnPrev;
	public static String msgLoadingData;
	public static String msgStatusText;
	public static String msgStartPageAlert;
	public static String msgLastPageAlert;
	public static String msgDataCompareExport;
	public static String msgNotExistsToExportData;
	public static String msgBeginToCompareData;
	public static String msgDataCompareNoTable;
	public static String msgDataCompareNoData;
	public static String msgDataCompareResultStatus;
	public static String msgEndToCompareData;
	public static String msgBeginDataCompareExcel;
	public static String msgTableNotFound;
	public static String msgToExportExcelError;
	public static String msgEndDataCompareExcel;
	public static String msgYouCanSeeDetailDblclick;
	public static String msgClickRefreshToEsimateDiff;
	public static String msgTheSchemaDiff;
	public static String msgTargetNotFound;
	public static String msgNoDataToCompare;
	public static String msgNotYetCompared;
	public static String msgTargetNoData;
	public static String msgSameData;
	public static String btnExportReport;
	public static String lblSource;
	public static String lblTarget;
	public static String lblDataCompareDescDialog;
	public static String lblError1;
	public static String lblError2;
	public static String lblError3;
	public static String lblSchemaDifferent;
	public static String lblCheckSelectedItems;
	public static String lblUncheckSelectedItems;
	public static String lblCheckAllItems;
	public static String lblUncheckAllItems;
	public static String equalSchemaMsg;
	public static String alterAutoIncrementNotSupport;

	static {
		NLS.initializeMessages(CommonUIPlugin.PLUGIN_ID + ".compare.Messages",
				Messages.class);
	}
}
