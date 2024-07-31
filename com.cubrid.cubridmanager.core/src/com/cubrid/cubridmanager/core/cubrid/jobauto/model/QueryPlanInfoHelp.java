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
package com.cubrid.cubridmanager.core.cubrid.jobauto.model;

/**
 * A help class that implements convert from a instance of QueryPlanInfo to the value in the
 * relevant dialog
 *
 * @author lizhiqiang 2009-4-10
 */
public class QueryPlanInfoHelp {
    private QueryPlanInfo queryPlanInfo;
    private String hour;
    private String minute;
    private String time;
    private String interval;
    private boolean isPeriodic = false;

    /**
     * Gets the instance of QueryPlanInfo
     *
     * @return the queryPlanInfo
     */
    public QueryPlanInfo getQueryPlanInfo() {
        return queryPlanInfo;
    }

    /** @param queryPlanInfo the queryPlanInfo to set */
    public void setQueryPlanInfo(QueryPlanInfo queryPlanInfo) {
        this.queryPlanInfo = queryPlanInfo;
    }

    /**
     * Builds the message to a query plan
     *
     * @param withUser whether using user name and user password
     * @return String A string includes the info of query plan
     */
    public String buildMsg(boolean withUser) {
        if (withUser) {
            return buildMsgWithUser();
        } else {
            return buildMsgNoUser();
        }
    }

    /**
     * Builds the message to a query plan without info of user
     *
     * @return String A string includes the info of query plan
     */
    private String buildMsgNoUser() {
        StringBuffer msg = new StringBuffer();
        msg.append("query_id:");
        msg.append(queryPlanInfo.getQuery_id());
        msg.append("\n");
        msg.append("period:");
        msg.append(queryPlanInfo.getPeriod());
        msg.append("\n");
        msg.append("detail:");
        msg.append(queryPlanInfo.getDetail());
        msg.append("\n");
        msg.append("query_string:");
        msg.append(queryPlanInfo.getQuery_string());
        msg.append("\n");
        return msg.toString();
    }

    /**
     * Builds the message to a query plan with info of user
     *
     * @return String A string includes the info of query plan
     */
    private String buildMsgWithUser() {
        StringBuffer msg = new StringBuffer();
        msg.append("query_id:");
        msg.append(queryPlanInfo.getQuery_id());
        msg.append("\n");
        msg.append("username:");
        msg.append(queryPlanInfo.getUsername());
        msg.append("\n");
        msg.append("userpass:");
        msg.append(queryPlanInfo.getUserpass());
        msg.append("\n");
        msg.append("period:");
        msg.append(queryPlanInfo.getPeriod());
        msg.append("\n");
        msg.append("detail:");
        msg.append(queryPlanInfo.getDetail());
        msg.append("\n");
        msg.append("query_string:");
        msg.append(queryPlanInfo.getQuery_string());
        msg.append("\n");
        return msg.toString();
    }

    /**
     * Get whether the instance of QueryPlanInfo is periodic or not
     *
     * @return
     */
    public boolean isPeriodic() {
        String detailTime = getDetailTimeFromInstance();
        if (detailTime.startsWith("i")) {
            isPeriodic = true;
        } else {
            isPeriodic = false;
        }
        return isPeriodic;
    }

    /**
     * Gets the time from the instance of QueryPlanInfo;
     *
     * @return String a string indicates the time
     */
    public String getDetailTimeFromInstance() {
        String detailVal = queryPlanInfo.getDetail();
        String detailTime = detailVal.substring(detailVal.indexOf(" ") + 1); // the first
        // blank
        return detailTime;
    }

    /**
     * Gets the time from the instance of QueryPlanInfo
     *
     * @return String the time in format HH:MM
     */
    public String getTime() {
        String detailTime = getDetailTimeFromInstance();
        if (detailTime.startsWith("i")) {
            time = "-1:-1";
        } else {
            time = detailTime;
        }

        return time;
    }

    /**
     * Gets the hour from the instance of QueryPlanInfo;
     *
     * @return int the hour
     * @deprecated
     */
    public int getHour() {
        String time = getDetailTimeFromInstance();
        if (time.startsWith("i")) {
            hour = "-1";
            return Integer.valueOf(hour);
        }
        hour = time.substring(0, time.indexOf(":"));
        if ("null".equals(hour)) {
            hour = "0";
        } else if (hour.startsWith("0")) {
            hour = hour.substring(1);
        }
        return Integer.valueOf(hour);
    }

    /**
     * Gets the minute from the instance of QueryPlanInfo;
     *
     * @return int The minute
     * @deprecated
     */
    public int getMinute() {
        String time = getDetailTimeFromInstance();
        if (time.startsWith("i")) {
            minute = "-1";
            return Integer.valueOf(minute);
        }
        minute = time.substring(time.indexOf(":") + 1);
        if ("null".equals(minute)) {
            minute = "0";
        } else if (minute.startsWith("0")) {
            minute = minute.substring(1);
        }
        return Integer.valueOf(minute);
    }

    /**
     * Get interval value stored in instance of QueryPlanInfo
     *
     * @return
     */
    public int getInterval() {
        String time = getDetailTimeFromInstance();
        if (time.startsWith("i")) {
            interval = time.substring(1);
        } else {
            interval = "-1";
        }

        return Integer.valueOf(interval);
    }

    /**
     * @see com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#getDbname()
     * @return String The database name
     */
    public String getDbname() {
        return queryPlanInfo.getDbname();
    }

    /**
     * @see com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#getDetail()
     * @return String the detail
     */
    public String getDetail() {
        String string = queryPlanInfo.getDetail();
        String detail = string.substring(0, string.indexOf(" ")); // the first
        // blank
        String newDetail = detail;
        if (queryPlanInfo.getPeriod().equalsIgnoreCase("WEEK")) {
            String[] tempAr = detail.split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : tempAr) {
                sb.append(getMatchedWeekDayForUI(s)).append(",");
            }
            if (sb.length() > 0) {
                newDetail = sb.deleteCharAt(sb.length() - 1).toString();
            }
        } else if (queryPlanInfo.getPeriod().equalsIgnoreCase("DAY")) {
            newDetail = "";
        } else if (queryPlanInfo.getPeriod().equalsIgnoreCase("ONE")) {
            newDetail = detail.replace("/", "-");
        }
        return newDetail;
    }

    private String getMatchedWeekDayForUI(String value) {
        String result = "";
        if (value.equalsIgnoreCase("SUN")) {
            result = "sunday";
        } else if (value.equalsIgnoreCase("MON")) {
            result = "monday";
        } else if (value.equalsIgnoreCase("TUE")) {
            result = "tuesday";
        } else if (value.equalsIgnoreCase("WED")) {
            result = "wednesday";
        } else if (value.equalsIgnoreCase("THU")) {
            result = "thursday";
        } else if (value.equalsIgnoreCase("FRI")) {
            result = "friday";
        } else if (value.equalsIgnoreCase("SAT")) {
            result = "saturday";
        }
        return result;
    }

    /**
     * @see com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#getPeriod()
     * @return String The period that may be MONTH,WEEK,DAY or a special day
     */
    public String getPeriod() {
        String period = queryPlanInfo.getPeriod();
        String newPeriod = "";
        if (period.equalsIgnoreCase("MONTH")) {
            newPeriod = "Monthly";
        } else if (period.equalsIgnoreCase("WEEK")) {
            newPeriod = "Weekly";
        } else if (period.equalsIgnoreCase("DAY")) {
            newPeriod = "Daily";
        } else {
            newPeriod = "Special";
        }
        return newPeriod;
    }

    /**
     * @see com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#getQuery_id()
     * @return String A string indicates query id
     */
    public String getQuery_id() {
        return queryPlanInfo.getQuery_id();
    }

    /**
     * @see com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#getQuery_string()
     * @return String A string indicates query string
     */
    public String getQuery_string() {
        return queryPlanInfo.getQuery_string();
    }

    /**
     * @see com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#
     *     setDbname(java.lang.String)
     * @param dbname String database name
     */
    public void setDbname(String dbname) {
        queryPlanInfo.setDbname(dbname);
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#setDetail(java.lang.String)
     * @param detail String the details time
     */
    public void setDetail(String detail) {
        String newDetail = "";
        if (queryPlanInfo.getPeriod().equalsIgnoreCase("WEEK")) {
            String[] tempAr = detail.split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : tempAr) {
                sb.append(getMatchedWeekDayForReq(s)).append(",");
            }
            if (sb.length() > 0) {
                newDetail = sb.deleteCharAt(sb.length() - 1).toString();
            }
        } else if (queryPlanInfo.getPeriod().equalsIgnoreCase("DAY")) {
            newDetail = "EVERYDAY";
        } else if (queryPlanInfo.getPeriod().equalsIgnoreCase("ONE")) {
            newDetail = detail.replace("-", "/");
        } else {
            newDetail = detail;
        }
        newDetail = newDetail + " " + getDetailTimeForInstance();
        queryPlanInfo.setDetail(newDetail);
    }

    private String getMatchedWeekDayForReq(String origVal) {
        String result = "";
        if (origVal.equalsIgnoreCase("sunday")) {
            result = "SUN";
        } else if (origVal.equalsIgnoreCase("monday")) {
            result = "MON";
        } else if (origVal.equalsIgnoreCase("tuesday")) {
            result = "TUE";
        } else if (origVal.equalsIgnoreCase("wednesday")) {
            result = "WED";
        } else if (origVal.equalsIgnoreCase("thursday")) {
            result = "THU";
        } else if (origVal.equalsIgnoreCase("friday")) {
            result = "FRI";
        } else if (origVal.equalsIgnoreCase("saturday")) {
            result = "SAT";
        }

        return result;
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#setPeriod(java.lang.String)
     * @param period String The period
     */
    public void setPeriod(String period) {
        String newPeriod = "";
        if (period.equalsIgnoreCase("Monthly")) {
            newPeriod = "MONTH";
        } else if (period.equalsIgnoreCase("Weekly")) {
            newPeriod = "WEEK";
        } else if (period.equalsIgnoreCase("Daily")) {
            newPeriod = "DAY";
        } else {
            newPeriod = "ONE";
        }
        queryPlanInfo.setPeriod(newPeriod);
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#setQuery_id(java.lang.String)
     * @param queryId String A string that indicates query_id
     */
    public void setQuery_id(String queryId) {
        queryPlanInfo.setQuery_id(queryId);
    }

    /**
     * @see
     *     com.cubrid.cubridmanager.core.cubrid.jobauto.model.QueryPlanInfo#setQuery_string(java.lang.String)
     * @param queryString String A string that indicates query string
     */
    public void setQuery_string(String queryString) {
        queryPlanInfo.setQuery_string(queryString);
    }

    public void setPeriodic(boolean isPeriodic) {
        this.isPeriodic = isPeriodic;
    }

    /**
     * Get the format time for update the instance of queryPlanInfo
     *
     * @return String a string indicates the time
     */
    public String getDetailTimeForInstance() {
        if (!isPeriodic) {
            return time;
        } else {
            return "i" + interval;
        }
    }

    /**
     * @param hour the hour to set
     * @deprecated
     */
    public void setHour(String hour) {
        this.hour = hour;
    }

    /**
     * @param minute the minute to set
     * @deprecated
     */
    public void setMinute(String minute) {
        this.minute = minute;
    }

    /**
     * Set the time in format HH:MM
     *
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Set the interval value
     *
     * @param interval
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * Get the user name;
     *
     * @return the user name
     */
    public String getUserName() {
        return queryPlanInfo.getUsername();
    }

    /**
     * Set the user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        queryPlanInfo.setUserName(userName);
    }

    /**
     * Get the password of user.
     *
     * @return the user password
     */
    public String getUserPwd() {
        return queryPlanInfo.getUserpass();
    }

    /**
     * Set the password of user
     *
     * @param password the user password
     */
    public void setUserPwd(String password) {
        queryPlanInfo.setUserpass(password);
    }
}
