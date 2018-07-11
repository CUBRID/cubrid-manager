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
package com.cubrid.common.ui.cubrid.table;

import org.eclipse.osgi.util.NLS;

import com.cubrid.common.ui.CommonUIPlugin;

/**
 * To access and store messages for international requirement
 * 
 * @author moulinwang
 * @version 1.0 - 2009-5-5 created by moulinwang
 */
public class Messages extends
		NLS {

	static {
		NLS.initializeMessages(CommonUIPlugin.PLUGIN_ID
				+ ".cubrid.table.Messages", Messages.class);
	}
	public static String btnAdd;
	public static String btnAddColumn;
	public static String btnAutoIncr;
	public static String btnCancel;
	public static String btnDel;
	public static String btnDelColumn;
	public static String btnEditAutoIncr;
	public static String btnDelete;
	public static String btnEdit;
	public static String btnEditColumn;
	public static String btnPK;
	public static String btnShowInherit;
	public static String btnChangeName;

	public static String colColumnName;
	public static String colDataType;
	public static String colName;
	public static String colOrder;
	public static String colRefColumn;
	public static String colSchemaType;
	public static String colUseColumn;
	public static String colPrefixLength;
	public static String colFunctionExpression;

	public static String dataNewKey;

	public static String errColumnExist;
	public static String errColumnName;
	public static String errColumnNotEdit;
	public static String errDataType;
	public static String errDataTypeImcompatible;
	public static String errDataTypeInCompatible;
	public static String errDataTypeNotFound;
	public static String errExistIndex;
	public static String errExistReverseIndex;
	public static String errExistReverseUniqueIndex;
	public static String errExistUniqueIndex;
	public static String errExistUniqueSameRule;
	public static String errExistIndexName;
	public static String errInvalidDataType;
	public static String errInvalidFile;
	public static String errMultColumnsNotSet;
	public static String errMultiBytes;
	public static String errNoColumnInTable;
	public static String errNoNameForCacheColumn;
	public static String errNoSelectedColumn;
	public static String errNoSelectedSuperClass;
	public static String errOneColumnNotSet;
	public static String errPrecisionGreaterSize;
	public static String errSelectDataType;
	public static String errSelectMoreColumn;
	public static String errSelectMoreColumns;
	public static String errSelectTableWithPK;
	public static String errDoNotDropMultipleType;
	public static String errIndexPrefixLength;
	public static String errMultColIndexPrefixLength;
	public static String errPKNameEmpty;

	public static String grpColumnType;
	public static String grpConstaint;
	public static String grpFieldDesc;
	public static String grpOnUpdate;

	public static String infoGeneralTab;
	public static String infoIndexesTab;
	public static String infoInheritTab;
	public static String infoOwner;
	public static String infoSQLScriptTab;
	public static String infoSuccess;
	public static String infoSuperClasses;
	public static String infoSystemSchema;
	public static String infoType;
	public static String infoUserSchema;

	public static String lblAlias;
	public static String lblCacheColumnName;
	public static String lblColumn;
	public static String lblColumnName;
	public static String lblColumnDesc;
	public static String lblGeneral;
	public static String lblColumns;
	public static String lblConflicts;
	public static String lblDataType;
	public static String msgUserInput;
	public static String msgUserInputSubType;
	public static String lblDefault;
	public static String lblDefaultValue;
	public static String lblFK;
	public static String lblFKName;
	public static String lblFTableName;
	public static String lblFTablePK;
	public static String lblIncr;
	public static String lblIncrement;
	public static String lblCurVal;
	public static String lblIndexes;
	public static String lblIndexName;
	public static String lblIndexDescription;
	public static String lblIndexType;
	public static String lblOwner;
	public static String lblCollation;
	public static String lblPKName;
	public static String lblPrecision;
	public static String lblQuerySpec;
	public static String lblResolution;
	public static String lblResolutionType;
	public static String lblSchemaType;
	public static String lblSeed;
	public static String lblMinVal;
	public static String lblSelectColumns;
	public static String lblSelectTables;
	public static String lblShared;
	public static String lblSharedValue;
	public static String lblSize;
	public static String lblSuperClass;
	public static String lblSuperInfo;
	public static String lblSuperList;
	public static String lblTableName;
	public static String lblTableDesc;
	public static String lblTipSuperClass;
	public static String lblType;
	public static String lblTableInfo;

	public static String msg_warning;
	public static String msg_information;
	public static String msgAddColumn;
	public static String msgSelectSupers;
	public static String msgSetPK;
	public static String msgSetResolution;
	public static String msgTitleAddColumn;
	public static String msgTitleAddFK;
	public static String msgTitleEditFK;
	public static String msgTitleAddIndex;
	public static String msgAddIndex;
	public static String msgTitleEditIndex;
	public static String msgEditIndex;
	public static String msgTitleEditColumn;
	public static String msgTitleSetPK;
	public static String msgTitleSetResolution;
	public static String msgTitleSetSupers;

	public static String schemaTypeClass;
	public static String sqlConnectionError;

	public static String btnReuseOid;
	public static String lblScale;
	public static String btnAddFk;
	public static String btnEditFk;
	public static String btnDelFk;
	public static String btnAddIndex;
	public static String btnEditIndex;
	public static String btnDelIndex;
	public static String btnAddPartition;
	public static String btnEditPartition;
	public static String btnDelPartition;

	//create table for null2Default
	public static String confirmSelectSetDef;
	public static String confirmSetDef;
	public static String confirmResetDef;
	public static String confirmKeepNull;
	public static String msgNull2DefRearName;
	public static String msgNull2DefComplete;
	public static String noAttributes;
	public static String msgAlterTableConfirm;
	public static String msgCreateTableConfirm;
	public static String msgCancelEditTableConfirm;
	public static String msgDeleteColumnConfirm;
	public static String updateDescriptionTask;
	//empty table message
	public static String confirmTableDeleteWarn;
	public static String resultTableDeleteInformantion;
	public static String msgDeleteTableDataJobName;
	public static String msgTruncateTableJobName;
	//select count(*) message
	public static String selectCountTitle;
	public static String selectCountResult1;
	public static String selectCountResult2;
	public static String columnSelectCountResult1;
	public static String columnSelectCountResult2;

	//insert instance dialog
	public static String insertButtonName;
	public static String btnCommit;
	public static String clearButtonName;
	public static String closeButtonName;
	//column names
	public static String metaAttribute;
	public static String metaDomain;
	public static String metaConstaints;
	public static String metaValue;
	//Title
	public static String insertInstanceWindowTitle;
	public static String insertInstanceMsgTitle;
	public static String insertInstanceMsg;
	public static String systemSchema;
	public static String tblColColumnName;
	public static String tblColDataType;
	public static String tblColTableName;
	public static String tblColumnAlias;
	public static String tblColumnAutoIncr;
	public static String tblColumnAutoIncrHint;
	public static String tblColumnCacheColumn;
	public static String tblColumnClass;
	public static String tblColumnClassHint;
	public static String tblColumnColumnName;
	public static String tblColumnColumnCollation;
	public static String tblColumnColumnDesc;
	public static String tblColumnColumns;
	public static String tblColumnDataType;
	public static String tblColumnDataLength;
	public static String tblColumnDefault;
	public static String tblColumnDefaultHint;
	public static String tblColumnDeleteRule;
	public static String tblColumnFK;
	public static String tblColumnForeignColumnName;
	public static String tblColumnForeignTable;
	public static String tblColumnIndexName;
	public static String tblColumnIndexRule;
	public static String tblColumnIndexMemo;
	public static String tblColumnIndexType;
	public static String tblColumnInherit;
	public static String tblColumnInheritHint;
	public static String tblColumnName;
	public static String tblColumnNotNull;
	public static String tblColumnNotNullHint;
	public static String tblColumnOnColumns;
	public static String tblColumnPK;
	public static String tblColumnShared;
	public static String tblColumnSharedHint;
	public static String tblColumnSuperTable;
	public static String tblColumnTableName;
	public static String tblColumnType;
	public static String tblColumnUnique;
	public static String tblColumnUniqueHint;
	public static String tblColumnUpdateRule;
	public static String tblColUseColumn;
	public static String tblColUseTable;
	public static String tipChooseDataType;
	public static String tipInput;
	public static String tipResolutionTable;
	public static String tipSuperClassTable;
	public static String titleAddColumn;
	public static String titleAddFK;
	public static String msgAddFK;
	public static String msgEditFK;
	public static String titleAddIndex;
	public static String titleEditColumn;
	public static String titleSetPK;
	public static String titleSetSuperTables;
	public static String titleTitleSetResolution;
	public static String itemDeleteColumn;;


	public static String totalInsertedCountMsg;
	public static String insertFailed;
	public static String insertedCountMsg;
	public static String insertDataTypeErrorMsg;
	public static String insertNotNullErrorMsg;

	//export data function
	public static String exportShellTitle;
	public static String exportDataMsgTitle;
	public static String exportDataMsg;
	public static String exportTargetTitle;
	public static String exportButtonName;
	public static String exportSelectTargetTableERRORMSG;
	public static String exportSelectFileERRORMSG;
	public static String exportFirstLineFLAG;
	public static String exportResultTitle;
	public static String errExportTableData;
	public static String errExportTableData2;
	public static String msgExportTableData;
	public static String msgCancelExportTableData;
	public static String msgExportSpendTime;
	public static String msgExportFinished;
	public static String lblFilePath;
	public static String lblFileType;
	public static String errNoMemory;

	public static String exportWizardTypeDescription;
	public static String exportWizardComfimrDescription;
	public static String exportWizardSourceTableLable;
	public static String exportWizardWhatExport;
	public static String exportWizardWhereExport;
	public static String exportWizardViews;
	public static String exportWizardFileType;
	public static String exportWizardFilepath;
	public static String exportWizardParsingOption;
	public static String exportWizardDataOption;
	public static String exportWizardDelimiterOptions;
	public static String exportWizardNullOptions;;
	public static String exportWizardRowSeperator;
	public static String exportWizardColumnSeperator;
	public static String exportWizardType3;
	public static String exportWizardType2;
	public static String exportWizardType1;
	public static String exportWizardTypeDescription1;
	public static String exportWizardTypeDescription2;
	public static String exportWizardTypeDescription3;
	public static String exportColumnCountOverWarnInfo;
	public static String exportFileOverwriteQuestionMSG;
	public static String exportFileOverwriteQuestionTitle;
	public static String exportMonitorMsg;
	public static String msgExportDataRearJobName;
	public static String msgExportDataRow;
	public static String msgTipExportBigValue;
	public static String msgExportResultData;
	public static String exportWizardTypePageErrMsg1;
	
	public static String exportWizardBackConfirmMsg;
	public static String exportWizardLoadDBPageErrMsg1;
	public static String exportWizardLoadDBPageErrMsg2;
	public static String exportWizardLoadDBPageErrMsg3;
	public static String exportWizardLoadDBPageErrMsg4;
	public static String exportWizardLoadDBPageErrMsg5;
	public static String exportWizardLoadDBPageErrMsg6;
	public static String exportWizardLoadDBPageErrMsg7;
	public static String exportWizardLoadDBPageErrMsg8;
	public static String exportWizardSettngPageFilepathErrMsg;
	public static String exportWizardLoadDBPageFilepathErrMsg1;
	public static String exportWizardLoadDBPageFilepathErrMsg2;
	public static String exportWizardLoadDBPageFilepathErrMsg3;
	public static String xlsxFileType;
	public static String xlsFileType;
	public static String csvFileType;
	public static String sqlFileType;
	public static String obsFileType;
	public static String txtFileType;
	public static String allFileType;
	public static String lblSplitBy;
	public static String btnParse;
	public static String errorSeparatorInvalid;
	public static String errorSeparatorInvalid2;
	public static String btnSelectAll;
	public static String exportDataCheckErrorMsg;
	public static String errorExportHistoryColumn;
	public static String exportMonitorPartColumnFileName;
	public static String exportMonitorPartColumnTotalcount;
	public static String exportMonitorPartColumnParsecount;
	public static String exportMonitorPartSaveLabel;
	public static String exportMonitorPartSaveButton;
	public static String exportMonitorPartSaveErrMsg1;
	public static String exportMonitorPartSaveErrMsg2;
	//import data function
	public static String importErrorHead;
	public static String importShellTitle;
	public static String importButtonName;
	public static String cancleImportButtonName;
	public static String importDataMsgTitle;
	public static String importDataMsg;
	public static String importTargetTable;
	public static String importFileNameLBL;
	public static String importTableColumns;
	public static String importDeleteExcelColumnBTN;
	public static String importUpTableColumnBTN;
	public static String importDownTableColumnBTN;
	public static String importDeleteTableColumnBTN;
	public static String importSelectTargetTableERRORMSG;
	public static String importSelectFileERRORMSG;
	public static String importColumnCountMatchERRORMSG;
	public static String importNoExcelColumnERRORMSG;
	public static String importNoTableColumnERRORMSG;
	public static String lblFileCharset;
	public static String grpJDBCCharset;
	public static String lblJDBCCharset;
	public static String lblImportMapping;
	public static String errNoAvailableMemory;

	//rename table name
	public static String renameTable;
	public static String renameView;
	public static String renameInvalidTableNameMSG;
	public static String renameMSGTitle;
	public static String renameDialogMSG;
	public static String renameNewTableName;
	public static String renameShellTitle;
	public static String renameOKBTN;
	public static String renameCancelBTN;
	public static String renameTableTaskName;

	//drop table
	public static String dropTable;
	public static String dropView;
	public static String newTableMsgTitle;
	public static String newViewMsgTitle;
	public static String newTableMsg;
	public static String newViewMsg;
	public static String newTableButtonName;
	public static String cancleButtonName;
	public static String newTableShellTitle;
	public static String newViewShellTitle;
	public static String editTableMsgTitle;
	public static String editViewMsgTitle;
	public static String editTableMsg;
	public static String editViewMsg;
	public static String editTableShellTitle;
	public static String editViewShellTitle;
	public static String typeTable;
	public static String typeView;
	public static String userSchema;
	public static String errExistColumn;
	public static String errParseValue2DataType;
	public static String btnOK;
	public static String editAttrShellTitle;
	public static String dataTypeInSet;
	public static String addAttrShellTitle;
	public static String invalidTimestampOld;
	public static String invalidTimestamp;
	public static String invalidDate;
	public static String invalidTime;
	public static String invalidBit;
	public static String invalidBitVarying;
	public static String btnUp;
	public static String btnDown;
	public static String dropTableTaskName;

	//Create view
	public static String errInput;
	public static String errInputViewName;
	public static String errInputNameLength;
	public static String errInputValidViewName;
	public static String errAddSpecification;
	public static String errClickValidate;
	public static String titleSuccess;
	public static String msgSuccessCreateView;
	public static String msgSuccessEditView;
	public static String tabItemGeneral;
	public static String tabItemSQLScript;
	public static String lblViewName;
	public static String lblViewOwnerName;
	public static String lblViewDescription;
	public static String lblQueryList;
	public static String lblSelectQueryList;
	public static String tblColViewName;
	public static String tblColViewDataType;
	public static String tblColViewDefaultType;
	public static String tblColViewDefaultValue;
	public static String tblColViewMemo;
	public static String msgPropertyInfo;
	public static String msgEditInfo;
	public static String btnAddParameter;
	public static String btnDeleteParameter;
	public static String btnEditParameter;
	public static String btnValidateColumn;
	public static String lblTableNameColumns;
	//add query
	public static String titleAddQueryDialog;
	public static String titleEditQueryDialog;
	public static String grpQuerySpecification;
	public static String msgAddQueryDialog;
	public static String errFileCannotDelete;
	public static String errFileCannotRename;
	public static String grpOnDelete;
	public static String errNoTableName;
	public static String btnOnCacheObject;

	public static String typeClass;
	public static String typeInstance;
	public static String typeShared;
	public static String typeUnique;
	public static String typeNotNull;
	public static String titleSchemEditPart;
	public static String errColumnNotDrop;
	public static String errColumnNotDropForPk;
	public static String errDropForPartitonColumn;
	public static String errNoDefaultOnClassColumnNotNull;
	public static String errCanNotChangeNotNull;
	public static String errEnumerationEmpty;
	public static String errEnumerationValueFormat;
	public static String errFKNotDrop;
	public static String errIndexNotDrop;
	public static String invalidDatetime;
	public static String invalidDatetimeOld;
	public static String errInheritItself;
	public static String errExistTable;
	public static String errExistView;
	public static String errExistLocColumn;
	public static String errExistResolution;
	public static String msgEditColumn;
	public static String errColumnExistInFK;
	public static String errNumber;
	public static String errRange;
	public static String errIncrement;
	public static String errNotEnoughtColumns;
	public static String exportCharacterCountExceedWarnInfo;

	//Partition
	public static String tabItemPartition;
	public static String tblColPartitionName;
	public static String tblColPartitionDescription;
	public static String tblColType;
	public static String tblColExpr;
	public static String tblColExprValue;
	public static String tblColRows;
	public static String msgNoTableName;
	public static String msgDelPartition;
	public static String msgDelHashPartition;

	public static String titleAddPartition;
	public static String titleEditPartition;

	public static String titleTypePage;
	public static String msgTypePage;
	public static String lblPartitionType;
	public static String grpPartitionExpr;
	public static String btnUseColumn;
	public static String tblColName;
	public static String tblColMiniValue;
	public static String tblColMaxValue;
	public static String tblColValueCount;
	public static String btnUseExpr;
	public static String lblPartitionExpr;
	public static String lblExprDataType;
	public static String errNoExpression;
	public static String errNoSelectColumn;
	public static String errInvalidExpr;

	public static String titleHashPage;
	public static String msgHashPage;
	public static String grpPartitionInfo;
	public static String lblPartitionNumber;
	public static String errPartitionNumber;

	public static String titleListPage;
	public static String msgListPage;
	public static String lblPartitionName;
	public static String lblPartitionDescription;
	public static String grpExpressionValue;
	public static String lblPartitionValue;
	public static String errValueExist;
	public static String lblExprValueInfo;
	public static String errValueTooMany;
	public static String errNoPartitionName;
	public static String errPartitionNameExist;
	public static String errNoExprValue;

	public static String titleRangePage;
	public static String msgRangePage;
	public static String lblRange;
	public static String btnMaxValue;
	public static String errMaxValueExist;
	public static String errNoRangeValue;
	public static String errInvalidRangeValue;
	public static String errRangeValueExist;
	public static String errRangeInvalidate;
	
	public static String msgConfirmUpdateStatis;
	public static String msgSuccessUpdateStatis;

	//create like table dialog
	public static String titleCreateLikeTableDialog;
	public static String msgCreateLikeTableDialog;
	public static String lblLikeTableName;
	public static String lblNewTableName;
	public static String createLikeTableTaskName;

	//PstmtOneDataDialog
	public static String titlePstmtDataDialog;
	public static String msgPstmtOneDataDialog;
	public static String grpSql;
	public static String btnClear;
	public static String btnAnalyze;
	public static String grpParameters;
	public static String colParaName;
	public static String colParaType;
	public static String colParaValue;
	public static String btnClearValue;
	public static String btnExecute;
	public static String errInvalidType;
	public static String errInvalidSql;
	public static String msgParaType;
	public static String errParaTypeValueMapping;
	public static String msgParaValue;
	public static String executeSqlJobName;
	public static String titleExecuteResult;
	public static String msgExecuteResult;
	public static String msgPstmtType;

	//PstmtMultiDataDialog
	public static String msgPstmtMultiDataDialog;
	public static String grpSelectFile;
	public static String lblTotalLines;
	public static String lblThreadCount;
	public static String lblCommitLines;
	public static String grpMapping;
	public static String lblMapping;
	public static String btnFirstAsColumn;
	public static String colFileColumn;
	public static String btnClearColumn;
	public static String btnBrowse;
	public static String errUnsupportedCharset;
	public static String errMappingParaColumn;
	public static String msgSelectFile;
	public static String errNoDataFile;
	public static String errThreadCount;
	public static String errNoFitThreadCount;
	public static String errCommitLines;
	public static String errNoMappingParaColumn;
	public static String msgCancelExecSql;
	public static String msgSuccessExecSql;
	public static String errExecSql;
	public static String msgDetailCause;

	public static String msgExeSqlTaskName;
	public static String errDataTypeNoMatch;
	public static String errOccur;
	public static String msgRowsFinish;

	//SetPstmtValueDialog
	public static String titleSetPstmtValueDialog;
	public static String msgSetPstmtValueDialog;
	public static String btnSetParaValue;
	public static String btnSelectFile;
	public static String errTextTypeNotMatch;
	public static String btnSetNull;

	public static String updateStatisTaskName;
	public static String msgCopySQLToFile;
	public static String btnCreateSQL;
	public static String btnGrantSQL;
	public static String btnSelectSQL;
	public static String btnInsertSQL;
	public static String btnUpdateSQL;
	public static String btnDeleteSQL;
	public static String lblFile;
	public static String msgCopySQLToFileDes;
	public static String importColumnNO;
	public static String importColumnMessage;
	public static String importColumnNOTotal;
	public static String lableImportErrorControl;
	public static String btnIgnore;
	public static String btnBreak;
	public static String toolTipIgnore;
	public static String toolTipBreak;
	public static String contextCopy;

	public static String txtImportGroup;
	public static String importReport;
	public static String importSetupTitle;
	public static String importSetupLabel;
	public static String importSetupLabel1;
	public static String lblOtherValue;
	
	public static String txtExportGroup;
	public static String exportSetupLabel1;
	public static String exportOtherValue;	
	public static String msgErrorOtherValueEmpty;
	public static String msgErrorContainsComma;
	public static String msgErrorSeparatorEmpty;
	
	public static String tableLabel;
	public static String conditionLabel;
	public static String conditionError;
	public static String conditionErrorTitle;
	
	public static String btnSaveReport;
	public static String errDeleteMsg;
	public static String confirmExitExportWizard;
	public static String confirmStartExportWizard;
	
	public static String errRunPstmtNoJob;

	public static String titleCopyToPojo;
	public static String msgCopyToPojo;
	public static String titleResultTableToCode;
	public static String msgResultTableToCode;
	public static String msgResultErrorTableToCode;
	public static String msgConfirmTableToCode;
	
	public static String pstmtSQLUnsupportPasteType;
	public static String pstmtSQLMuchItem;
	
	public static String tablesDetailInfoPartTitle;
	public static String tablesDetailInfoPartColTableName;
	public static String tablesDetailInfoPartColTableMemo;
	public static String tablesDetailInfoPartColRecordsCount;
	public static String tablesDetailInfoPartColColumnsCount;
	public static String tablesDetailInfoPartColPK;
	public static String tablesDetailInfoPartColUK;
	public static String tablesDetailInfoPartColFK;
	public static String tablesDetailInfoPartColIndex;
	public static String tablesDetailInfoPartColTableRecordsSize;
	public static String tablesDetailInfoPartMenuCopy;
	public static String tablesDetailInfoPartBtnViewData;
	public static String tablesDetailInfoPartBtnViewDataTip;
	public static String tablesDetailInfoPartBtnViewDataSelectOne;
	public static String tablesDetailInfoPartBtnEsitmateRecord;
	public static String tablesDetailInfoPartBtnEsitmateColumn;
	public static String tablesDetailInfoPartBtnEsitmateKey;
	public static String tablesDetailInfoPartBtnEsitmateRecordSize;
	public static String tablesDetailInfoPartBtnEsitmateRecordAlert;
	public static String tablesDetailInfoPartBtnEsitmateAlert;
	public static String tablesDetailInfoPartBtnCopyTableNames;
	public static String tablesDetailInfoPartBtnCopyColumnNames;
	public static String tablesDetailInfoPartBtnRefreshTip;
	public static String tablesDetailInfoPartBtnEsitmateRecordTip;
	public static String tablesDetailInfoPartBtnEsitmateColumnTip;
	public static String tablesDetailInfoPartBtnEsitmateKeyTip;
	public static String tablesDetailInfoPartBtnEsitmateRecordSizeTip;
	public static String tablesDetailInfoPartBtnCopyTableNamesTip;
	public static String tablesDetailInfoPartBtnCopyColumnNamesTip;
	public static String tablesDetailInfoPartBtnCopySuccessTitle;
	public static String tablesDetailInfoPartBtnCopySuccessMsg;
	public static String tablesDetailInfoPartBtnCopySuccessFailed;
	public static String tablesDetailInfoPartNotEstimated;
	public static String tablesDetailInfoPartNotRunColumn;
	public static String tablesDetailInfoPartNotRunKey;
	public static String tablesDetailInfoPartNotRunSize;

	public static String tablesDetailInfoPartRefreshConfirm;
	public static String tablesDetailInfoPartRefreshBtn;
	public static String tablesDetailInfoPartCloseMenu;
	public static String tablesDetailInfoPartCloseAllMenu;
	public static String tablesDetailInfoPartCloseOthersMenu;
	public static String tablesDetailInfoPartAlertNotSelected;
	public static String importResultDialogWriteExcelSucessInfo;
	public static String tablesDetailInfoPartRefreshMenu;
	public static String tablesDetailInfoLoadingDataTitle;
	public static String tablesDetailInfoLoadingData;

	public static String loadTableRecordCountsProgressTaskName;
	public static String loadTableColumnsProgressTaskName;
	public static String loadTableKeysProgressTaskName;
	public static String loadTableRecordSizeProgressTaskName;
	public static String loadTableRecordCountsProgressSubTaskName;
	public static String loadTableColumnsProgressSubTaskName;
	public static String loadTableKeysProgressSubTaskName;
	public static String loadTableRecordSizeProgressSubTaskName;

	
	public static String errGetSchemaInfo;
	
	/*Import table data wizard*/
	public static String titleImportDataWizardDialog;
	public static String titleImportDataWizard;
	public static String confirmStartImportWizard;
	
	public static String titleChooseImportType;
	public static String msgChooseImportType;
	public static String btnImportSQL;
	public static String lblImportSQL;
	public static String btnImportExcel;
	public static String lblImportExcel;
	public static String btnImportTxt;
	public static String lblImportTxt;
	public static String btnImportLoadDB;
	public static String lblImportLoadDB;
	public static String btnImportHistory;
	public static String lblImportHistory;
	public static String btnRenameHistory;
	public static String btnDeleteHistory;
	public static String titleRenameDialog;
	public static String descRenameDialog;
	public static String msgRenamePleaseInputNewName;
	public static String msgRenameAlreadyExists;
	
	/*ImportSettingPage*/
	public static String titleImportSettingPage;
	public static String msgImportSettingPage;
	public static String btnAddFiles;
	public static String btnAddSchemaFile;
	public static String btnAddDataFile;
	public static String btnAddIndexFile;
	public static String btnDelFiles;
	public static String grpAddRemove;
	public static String lblImportFileType;
	public static String columnImportFileName;
	public static String columnImportFileType;
	public static String grpDataOptions;
	public static String grpNulls;
	public static String btnOther;
	public static String btnBreakImport;
	public static String lblErrorHandle;
	public static String btnIgnoreSetToNull;
	public static String grpEncodingOption;
	public static String lblDBCharset;
	public static String grpDelimiter;
	public static String errTableSetting;
	public static String errNoSelectedTable;
	public static String errFileNotExist;
	public static String errThreadCountTooBig;
	public static String lblCol;
	public static String lblRow;

	public static String grpImportOptions;
	public static String lblTotalLine;
	public static String lblThreadNum;
	public static String lblCommitCount;
	public static String lblNameComma;
	public static String lblNameEnter;
	public static String lblNameTab;
	public static String lblNameQuote;
	
	public static String grpLobOptions;
	public static String btnImportClob;
	public static String btnImportBlob;
	
	/*FileToTableMappingComposite*/
	public static String columnTableName;
	public static String columnFileName;
	public static String columnRowCount;
	public static String columnMapped;
	public static String columnDiscardFirstLine;
	public static String btnCreateTable;
	public static String errorOpenFile;
	public static String errorOpenFileDetail;
	public static String errRowDelimiterEmpty;
	public static String errColumnDelimiterEmpty;
	public static String taskLoading;
	public static String taskLoadingTable;
	public static String taskLoadingColumn;
	public static String taskParsingData;
	public static String taskParsingFile;
	
	/*AddTableFileDialog*/
	public static String titleAddTableFileDialog;
	public static String msgAddTableFileDialog;
	
	/*ImportDataViewPart*/
	public static String columnName;
	public static String columnCount;
	public static String columnFinished;
	public static String columnFailed;
	public static String columnStatus;
	public static String columnTime;
	public static String lblHistory;
	public static String btnSaveAndClose;
	public static String btnClose;
	public static String btnStop;
	public static String runSQLOpenBtn;
	public static String runSQLStatusFinished;
	public static String runSQLStatusRunning;
	public static String runSQLStatusStopped;
	public static String runSQLStatusWaiting;
	public static String runSQLStatusFailed;
	public static String errHistoryEmpty;
	public static String errHistoryExist;
	public static String btnViewErrorLog;
	
	public static String lblExportTargetSchema;
	public static String lblExportTargetIndex;
	public static String lblExportTargetData;
	public static String lblExportTargetSerial;
	public static String lblExportTargetView;
	public static String lblExportTargetTrigger;
	public static String lblExportTargetStartValue;
	public static String tipExportTargetStartValue;
	public static String lblExportLobData;
	public static String tipExportLobData;
	public static String confirmExitImportWizard;
	
	public static String titleImportSelectSqlFile;
	public static String errFileRepeat;
	public static String titleExportSetting;
	public static String exportFileAlreadyExistsAreYouSureContinue;
	public static String exportFileAlreadyExistsAreYouSureContinue2;
	public static String exportFileAlreadyExistsCreateNewDir;
	public static String exportFileAlreadyExistsOverideTip;
	public static String canceledBecauseOfExists;
	public static String canceledBecauseOfExistsWithFolderName;
	public static String errorExportExistsFilesInFolder;
	public static String errorExportExistsFilesInFolderWithRename;

	public static String warnImportNewTableDetectedCreate;
	public static String warnImportNewTableDetectedSkip;
	
	public static String grpImportSqlHelper;
	public static String warnImportSqlHelper;
	public static String btnInHaMode;

	public static String lblExportConfirmExportType;
	public static String lblExportConfirmExportObjects;
	public static String lblExportConfirmFileType;
	public static String lblExportConfirmFilePath;
	public static String lblExportConfirmThreads;
	public static String lblExportConfirmCharset;
	public static String lblExportConfirmTables;
	public static String lblExportConfirmSchema;
	public static String lblExportConfirmIndex;
	public static String lblExportConfirmData;
	public static String lblExportConfirmSerial;
	public static String lblExportConfirmView;
	public static String lblExportConfirmTrigger;
	public static String lblImportConfirmImportType;
	public static String lblImportConfirmCommitCount;
	public static String lblImportConfirmThreads;
	public static String lblImportConfirmFileList;
	public static String lblImportConfirmSqlType;
	public static String lblImportConfirmTxtType;
	public static String lblImportConfirmCsvExcelType;

	public static String importWizardBackConfirmMsg;
	public static String importWizardComfimrDescription;
	public static String titleImportStep1;
	public static String titleImportStep2;
	public static String titleImportStep3;
	public static String titleExportStep1;
	public static String titleExportStep2;
	public static String titleExportStep3;

	public static String defaultExportHistoryName;
	public static String defaultImportHistoryName;
	
	public static String confirmDeleteExportHistory;
	public static String confirmDeleteImportHistory;
	
	// erwin function
	public static String errFileCannotRead;
	public static String errMissingEntityGroups;
	public static String errMissingEntity;
	public static String msgDuplicateTableName;
	
	public static String errNotSupportTableCommentNotice;
	public static String errNotSupportTableCommentNoticeShort;
	
	public static String renameTableDialogConfirmMsg;

	public static String titleExportRenameDialog;
	public static String descExportRenameDialog;
	public static String msgExportRenamePleaseInputNewName;
	public static String msgExportRenameAlreadyExists;

	//view dashboard
	public static String viewsDetailInfoPartTitle;
	public static String viewChangeRefreshConfirmMsg;
	public static String viewDetailInfoPartColViewName;
	public static String viewDetailInfoPartTableDefColumn;
	public static String viewDetailInfoPartTableOwnerColumn;
	public static String viewDetailInfoPartTableNoSelectionMsg;
	public static String viewDetailInfoPartTableCreateViewBtn;
	public static String viewDetailInfoPartTableEditViewBtn;
	public static String viewDetailInfoPartTableDropViewBtn;
	
	public static String runSQLExportBtn;
	public static String viewFailedSQLDialogTitle;
	public static String failedSQLlineNumber;
	public static String failedSQL;
	public static String failedErrorMessage;
	
	public static String errCanNotAddAutoincrementAlreadyExists;
	public static String errInvalidAutoIncrForm;
	public static String errCanNotSetDefaultOnAI;
	public static String errCanNotSetAIOnDefault;
	public static String errCanNotUseUkAndSharedOnce;
	public static String msgInputSharedValue;
	public static String lblAutoIncrStart;
	public static String lblAutoIncrIncr;

	public static String tipsDataType1;
	public static String tipsDataType2;
	public static String tipsDataType3;
	public static String tipsDataType4;
	public static String tipsDataType5;
	public static String tipsDataType6;
	public static String tipsDataType7;
	public static String tipsDataType8;
	public static String tipsDataType9;
	public static String tipsDataType10;
	public static String tipsDataType11;
	public static String tipsDataType12;
	public static String tipsDataType13;
	public static String tipsDataType14;
	public static String tipsDataType15;
	public static String tipsDataType16;
	public static String tipsDataType17;
	public static String tipsDataType18;
	public static String tipsDataType19;
	public static String tipsDataType20;
	public static String tipsDataType21;
	public static String tipsDataType22;
	public static String tipsDataType23;
	public static String tipsDataType24;
	public static String tipsDataType25;
	public static String tipsDataTypeTitle;
	public static String errSameNameOnEditTableColumn;
	public static String errEmptyNameOnEditTableColumn;
	public static String errSameNameOnEditTableAddColumn;
	public static String errCanNotFindIndex;
	public static String titleSelectFolderToBeExported;
	public static String msgSelectFolderToBeExported;
	public static String titleColumnDescEditor;
	public static String msgColumnDescEditor;
	public static String labelColumnDescEditor;
	public static String titleTableDescEditor;
	public static String msgTableDescEditor;
	public static String labelTableDescEditor;
	
	public static String msgFailedByRollback;
	public static String titleCloneTable;
	public static String msgCloneTable;
	public static String lblTargetName;
	public static String errInvalidName;
}
