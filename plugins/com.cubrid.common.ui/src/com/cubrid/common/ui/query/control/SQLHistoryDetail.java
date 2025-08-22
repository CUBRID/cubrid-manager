package com.cubrid.common.ui.query.control;

/**
 * SQLDetailHistoryPO
 *
 * @author fulei
 */
public class SQLHistoryDetail {

    private String executeTime = " ";
    private String elapseTime = " ";
    private String sql = " ";
    private String executeInfo = " ";
    private int index = 1;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getExecuteInfo() {
        return executeInfo;
    }

    public void setExecuteInfo(String executeInfo) {
        this.executeInfo = executeInfo;
    }

    public String getElapseTime() {
        return elapseTime;
    }

    public void setElapseTime(String elapseTime) {
        this.elapseTime = elapseTime;
    }

    public SQLHistoryDetail() {}

    public SQLHistoryDetail(String time, String elapseTime, String sql, String info) {
        this.executeTime = time;
        this.elapseTime = elapseTime;
        this.sql = sql;
        this.executeInfo = info;
    }
}
