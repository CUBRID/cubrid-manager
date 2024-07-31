package com.cubrid.cubridmanager.core.logs.modal;

import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import com.cubrid.cubridmanager.core.logs.model.AdminLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultInfo;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogResultList;
import com.cubrid.cubridmanager.core.logs.model.AnalyzeCasLogTopResultInfo;
import com.cubrid.cubridmanager.core.logs.model.BrokerLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.BrokerLogInfos;
import com.cubrid.cubridmanager.core.logs.model.CasLogTopResultInfo;
import com.cubrid.cubridmanager.core.logs.model.DbLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.DbLogInfos;
import com.cubrid.cubridmanager.core.logs.model.GetExecuteCasRunnerResultInfo;
import com.cubrid.cubridmanager.core.logs.model.LogContentInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfo;
import com.cubrid.cubridmanager.core.logs.model.LogInfoManager;
import com.cubrid.cubridmanager.core.logs.model.LogType;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfo;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfoList;
import com.cubrid.cubridmanager.core.logs.model.ManagerLogInfos;

/** @author code create 2009-6-24 */
public class LogsModelTest extends SetupEnvTestCase {
    public void testModelAdminLogInfoList() {
        AdminLogInfoList bean = new AdminLogInfoList();
        // bean.getTaskName();
        // bean.addAdminLogInfo();
        // bean.getAdminLogInfoList();
        assertEquals(bean.getTaskName(), "getadminloginfo");
        LogInfo logInfo = new LogInfo();
        logInfo.setPath("path");
        logInfo.setType("type");
        logInfo.setOwner("owner");
        logInfo.setSize("size");
        logInfo.setLastupdate("lastupdate");
        logInfo.setFilename("filename");
        bean.addAdminLogInfo(null);
        bean.addAdminLogInfo(logInfo);
        bean.getAdminLogInfoList();
        bean.addAdminLogInfo(logInfo);
    }

    public void testModelAnalyzeCasLogResultInfo() {
        AnalyzeCasLogResultInfo bean = new AnalyzeCasLogResultInfo();
        bean.setQindex("qindex");
        assertEquals(bean.getQindex(), "qindex");
        bean.setMax("max");
        assertEquals(bean.getMax(), "max");
        bean.setMin("min");
        assertEquals(bean.getMin(), "min");
        bean.setAvg("avg");
        assertEquals(bean.getAvg(), "avg");
        bean.setCnt("cnt");
        assertEquals(bean.getCnt(), "cnt");
        bean.setErr("err");
        assertEquals(bean.getErr(), "err");
        bean.setExecTime("execTime");
        assertEquals(bean.getExecTime(), "execTime");
        bean.setQueryString("queryString");
        assertEquals(bean.getQueryString(), "queryString");
        bean.setSavedFileName("savedFileName");
        assertEquals(bean.getSavedFileName(), "savedFileName");
    }

    public void testModelAnalyzeCasLogResultList() {
        AnalyzeCasLogResultList bean = new AnalyzeCasLogResultList();
        bean.addResultFile(new AnalyzeCasLogResultInfo());
        // assertEquals(bean.getResultFile() instanceof List, true);
        bean.setResultfile("resultfile");
        assertEquals(bean.getResultfile(), "resultfile");
        bean.getTaskName();
        bean.getLogFileInfoList();
        AnalyzeCasLogResultInfo analyzeCasLogResultInfo = new AnalyzeCasLogResultInfo();
        analyzeCasLogResultInfo.setQindex("qindex");
        bean.addResultFile(analyzeCasLogResultInfo);
    }

    public void testModelAnalyzeCasLogTopResultInfo() {
        AnalyzeCasLogTopResultInfo bean = new AnalyzeCasLogTopResultInfo();

        assertEquals(bean.getTaskName(), "getcaslogtopresult");
        bean.addLogString("a");
        bean.addLogString("b");
        assertEquals(bean.getLogString().get(0), "a");
    }

    public void testModelBrokerLogInfoList() {
        BrokerLogInfoList bean = new BrokerLogInfoList();
        // bean.getLogFileInfoList();
        // bean.addLogFile();
        bean.setBrokerLogInfoList(null);
        LogInfo logInfo = new LogInfo();
        bean.addLogFile(logInfo);
        logInfo.setFilename("filename");
        bean.addLogFile(logInfo);
    }

    public void testModelBrokerLogInfos() {
        BrokerLogInfos bean = new BrokerLogInfos();
        bean.setBroker("broker");
        assertEquals(bean.getBroker(), "broker");

        assertEquals(bean.getTaskName(), "getlogfileinfo");
        bean.addLogFileInfo(null);
        bean.getBrokerLogInfoList();
    }

    public void testModelCasLogTopResultInfo() {
        CasLogTopResultInfo bean = new CasLogTopResultInfo();
        bean.addResult("getResult");
        assertEquals(bean.getResult().size(), 1);
        bean.addResult("getResult2");
    }

    public void testModelDbLogInfoList() {
        DbLogInfoList bean = new DbLogInfoList();
        bean.removeAllLog();
        bean.addLog(new LogInfo());
        bean.removeLog(new LogInfo());
        bean.removeAllLog();
        bean.getDbLogInfoList();
        bean.getDbLogInfo("aa");
        bean.addLog(new LogInfo());
        bean.removeAllLog();
        LogInfo aInfo = new LogInfo();
        aInfo.setPath("path");
        bean.addLog(aInfo);
        LogInfo bInfo = new LogInfo();
        bInfo.setPath("aaa");
        bean.addLog(bInfo);
        bean.getDbLogInfo("path");
    }

    public void testModelDbLogInfos() {
        DbLogInfos bean = new DbLogInfos();
        bean.setDbname("dbname");
        assertEquals(bean.getDbname(), "dbname");
        bean.getTaskName();
        bean.getDbLogInfoList();
        assertEquals(LogType.SCRIPT.getText(), "SCRIPT");
        bean.setDbLogInfoList(null);
        bean.getDbLogInfoList();
        bean.getDbLogInfo("path");
        bean.addLogInfo(null);
        bean.getDbLogInfo("path");
    }

    public void testModelGetExecuteCasRunnerResultInfo() {
        GetExecuteCasRunnerResultInfo bean = new GetExecuteCasRunnerResultInfo();
        bean.setQueryResultFile("queryResultFile");
        assertEquals(bean.getQueryResultFile(), "queryResultFile");
        bean.setQueryResultFileNum("queryResultFileNum");
        assertEquals(bean.getQueryResultFileNum(), "queryResultFileNum");
        bean.getTaskName();
        bean.getResult();
        bean.addResult("");
    }

    public void testModelLogContentInfo() {
        LogContentInfo bean = new LogContentInfo();
        bean.setPath("path");
        assertEquals(bean.getPath(), "path");
        bean.setStart("start");
        assertEquals(bean.getStart(), "start");
        bean.setEnd("end");
        assertEquals(bean.getEnd(), "end");
        bean.setTotal("total");
        assertEquals(bean.getTotal(), "total");
        bean.getTaskName();
        bean.getLine();
        bean.addLine("");
    }

    public void testModelLogInfo() {
        LogInfo bean = new LogInfo();
        bean.setPath("path");
        assertEquals(bean.getPath(), "path");
        bean.setType("type");
        assertEquals(bean.getType(), "type");
        bean.setOwner("owner");
        assertEquals(bean.getOwner(), "owner");
        bean.setSize("size");
        assertEquals(bean.getSize(), "size");
        bean.setLastupdate("lastupdate");
        assertEquals(bean.getLastupdate(), "lastupdate");
        bean.setFilename("filename");
        assertEquals(bean.getFilename(), "filename");
        bean.getName();
        bean.setPath("c:/c");
        bean.getName();
    }

    public void testModelLogInfoManager() {
        LogInfoManager bean = new LogInfoManager();
        DbLogInfos dbLogInfos = new DbLogInfos();
        dbLogInfos.setDbname("dbname");
        bean.addDbLogInfos(dbLogInfos);
        bean.getDbLogInfo("dbname", "path");
    }

    public void testModelManagerLogInfo() {
        ManagerLogInfo bean = new ManagerLogInfo();
        bean.setUser("user");
        assertEquals(bean.getUser(), "user");
        bean.setTaskName("taskName");
        assertEquals(bean.getTaskName(), "taskName");
        bean.setTime("time");
        assertEquals(bean.getTime(), "time");
        bean.setErrorNote("errorNote");
        assertEquals(bean.getErrorNote(), "errorNote");
    }

    public void testModelManagerLogInfoList() {
        ManagerLogInfoList bean = new ManagerLogInfoList();
        bean.removeAllLog();
        bean.addLog(new ManagerLogInfo());
        bean.removeAllLog();
        bean.getManagerLogInfoList();
        bean.addLog(new ManagerLogInfo());
    }

    public void testModelManagerLogInfos() {
        ManagerLogInfos bean = new ManagerLogInfos();
        bean.setAccessLog(new ManagerLogInfoList());
        assertEquals(bean.getAccessLog().getClass(), ManagerLogInfoList.class);
        bean.setErrorLog(new ManagerLogInfoList());
        assertEquals(bean.getErrorLog().getClass(), ManagerLogInfoList.class);
    }
}
