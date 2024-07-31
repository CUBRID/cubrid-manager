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
package com.cubrid.cubridmanager.core.monitoring.model;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.model.IModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;

/**
 * A class that extends IModel and responsible for the task of "getbrokerdiagdata"
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-20 created by lizhiqiang
 */
public class BrokerDiagData implements IModel {
    private static final Logger LOGGER = LogUtil.getLogger(DiagStatusResult.class);
    private String status;
    private String note;
    private String bname;
    private String cas_mon_req;
    private String cas_mon_act_session;
    private String cas_mon_session;
    private String cas_mon_active;
    private String cas_mon_tran;
    private String cas_mon_query;
    private String cas_mon_long_query;
    private String cas_mon_long_tran;
    private String cas_mon_error_query;
    private final TreeMap<IDiagPara, String> diagStatusResultMap;
    private List<BrokerDiagData> subBrokerDiagLst;

    /* (non-Javadoc)
     * @see com.cubrid.cubridmanager.core.common.model.IModel#getTaskName()
     */
    public String getTaskName() {
        return "getbrokerdiagdata";
    }

    public BrokerDiagData() {
        cas_mon_req = "0";
        cas_mon_tran = "0";
        cas_mon_act_session = "0";
        cas_mon_session = "0";
        cas_mon_active = "0";
        cas_mon_query = "0";
        cas_mon_long_query = "0";
        cas_mon_long_tran = "0";
        cas_mon_error_query = "0";
        diagStatusResultMap = new TreeMap<IDiagPara, String>();
        subBrokerDiagLst = new ArrayList<BrokerDiagData>();
        putVauleInMap();
    }

    public BrokerDiagData(BrokerDiagData clone) {
        cas_mon_req = clone.cas_mon_req;
        cas_mon_tran = clone.cas_mon_tran;
        cas_mon_act_session = clone.cas_mon_act_session;
        cas_mon_session = clone.cas_mon_session;
        cas_mon_active = clone.cas_mon_active;
        cas_mon_query = clone.cas_mon_query;
        cas_mon_long_query = clone.cas_mon_long_query;
        cas_mon_long_tran = clone.cas_mon_long_tran;
        cas_mon_error_query = clone.cas_mon_error_query;
        subBrokerDiagLst = clone.getBrokerList();
        diagStatusResultMap = new TreeMap<IDiagPara, String>();
        putVauleInMap();
    }

    /**
     * Get the clone value from the given object
     *
     * @param clone BrokerDiagData
     */
    public void copyFrom(BrokerDiagData clone) {
        cas_mon_req = clone.cas_mon_req;
        cas_mon_tran = clone.cas_mon_tran;
        cas_mon_act_session = clone.cas_mon_act_session;
        cas_mon_session = clone.cas_mon_session;
        cas_mon_active = clone.cas_mon_active;
        cas_mon_query = clone.cas_mon_query;
        cas_mon_long_query = clone.cas_mon_long_query;
        cas_mon_long_tran = clone.cas_mon_long_tran;
        cas_mon_error_query = clone.cas_mon_error_query;
        subBrokerDiagLst = clone.getBrokerList();
        putVauleInMap();
    }

    /**
     * Gets the delta by two bean of BrokerDiagData
     *
     * @param dsrA BrokerDiagData
     * @param dsrB BrokerDiagData
     */
    public void getDelta(BrokerDiagData dsrA, BrokerDiagData dsrB) {
        cas_mon_req = getDeltaLong(dsrA.cas_mon_req, dsrB.cas_mon_req);
        cas_mon_query = getDeltaLong(dsrA.cas_mon_query, dsrB.cas_mon_query);
        cas_mon_tran = getDeltaLong(dsrA.cas_mon_tran, dsrB.cas_mon_tran);
        cas_mon_act_session = dsrA.cas_mon_act_session;
        cas_mon_session = dsrA.cas_mon_session;
        cas_mon_active = dsrA.cas_mon_active;
        cas_mon_long_query = getDeltaLong(dsrA.cas_mon_long_query, dsrB.cas_mon_long_query);
        cas_mon_long_tran = getDeltaLong(dsrA.cas_mon_long_tran, dsrB.cas_mon_long_tran);
        cas_mon_error_query = getDeltaLong(dsrA.cas_mon_error_query, dsrB.cas_mon_error_query);
        putVauleInMap();
    }

    /**
     * Gets the delta by three bean of BrokerDiagData
     *
     * @param dsrA BrokerDiagData
     * @param dsrB BrokerDiagData
     * @param dsrC BrokerDiagData
     */
    public void getDelta(BrokerDiagData dsrA, BrokerDiagData dsrB, BrokerDiagData dsrC) {
        cas_mon_req =
                getDeltaLong(
                        dsrA, "Cas_mon_req", dsrA.cas_mon_req, dsrB.cas_mon_req, dsrC.cas_mon_req);
        cas_mon_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_query",
                        dsrA.cas_mon_query,
                        dsrB.cas_mon_query,
                        dsrC.cas_mon_query);
        cas_mon_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_tran",
                        dsrA.cas_mon_tran,
                        dsrB.cas_mon_tran,
                        dsrC.cas_mon_tran);

        cas_mon_act_session = dsrA.cas_mon_act_session;
        cas_mon_session = dsrA.cas_mon_session;
        cas_mon_active = dsrA.cas_mon_active;

        cas_mon_long_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_query",
                        dsrA.cas_mon_long_query,
                        dsrB.cas_mon_long_query,
                        dsrC.cas_mon_long_query);
        cas_mon_long_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_tran",
                        dsrA.cas_mon_long_tran,
                        dsrB.cas_mon_long_tran,
                        dsrC.cas_mon_long_tran);
        cas_mon_error_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_error_query",
                        dsrA.cas_mon_error_query,
                        dsrB.cas_mon_error_query,
                        dsrC.cas_mon_error_query);
        putVauleInMap();
    }

    /**
     * Gets the delta by three bean of BrokerDiagData and the interval between getting the instance
     * of BrokerDiagData
     *
     * @param dsrA BrokerDiagData
     * @param dsrB BrokerDiagData
     * @param dsrC BrokerDiagData
     * @param inter float
     */
    public void getDelta(
            BrokerDiagData dsrA, BrokerDiagData dsrB, BrokerDiagData dsrC, float inter) {
        cas_mon_req =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_req",
                        dsrA.cas_mon_req,
                        dsrB.cas_mon_req,
                        dsrC.cas_mon_req,
                        inter);
        cas_mon_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_query",
                        dsrA.cas_mon_query,
                        dsrB.cas_mon_query,
                        dsrC.cas_mon_query,
                        inter);
        cas_mon_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_tran",
                        dsrA.cas_mon_tran,
                        dsrB.cas_mon_tran,
                        dsrC.cas_mon_tran,
                        inter);

        cas_mon_act_session = dsrA.cas_mon_act_session;
        cas_mon_session = dsrA.cas_mon_session;
        cas_mon_active = dsrA.cas_mon_active;

        cas_mon_long_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_query",
                        dsrA.cas_mon_long_query,
                        dsrB.cas_mon_long_query,
                        dsrC.cas_mon_long_query,
                        inter);
        cas_mon_long_tran =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_long_tran",
                        dsrA.cas_mon_long_tran,
                        dsrB.cas_mon_long_tran,
                        dsrC.cas_mon_long_tran,
                        inter);
        cas_mon_error_query =
                getDeltaLong(
                        dsrA,
                        "Cas_mon_error_query",
                        dsrA.cas_mon_error_query,
                        dsrB.cas_mon_error_query,
                        dsrC.cas_mon_error_query,
                        inter);
        putVauleInMap();
    }

    /** Put the new value of fields to map */
    private void putVauleInMap() {
        diagStatusResultMap.put(BrokerDiagEnum.RPS, cas_mon_req);
        diagStatusResultMap.put(BrokerDiagEnum.TPS, cas_mon_tran);
        diagStatusResultMap.put(BrokerDiagEnum.ACTIVE_SESSION, cas_mon_act_session);
        diagStatusResultMap.put(BrokerDiagEnum.SESSION, cas_mon_session);
        diagStatusResultMap.put(BrokerDiagEnum.ACTIVE, cas_mon_active);
        diagStatusResultMap.put(BrokerDiagEnum.QPS, cas_mon_query);
        diagStatusResultMap.put(BrokerDiagEnum.LONG_Q, cas_mon_long_query);
        diagStatusResultMap.put(BrokerDiagEnum.LONG_T, cas_mon_long_tran);
        diagStatusResultMap.put(BrokerDiagEnum.ERR_Q, cas_mon_error_query);
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @return the difference between fieldA and fieldB in value
     */
    private String getDeltaLong(String fieldA, String fieldB) {
        String result = "";
        try {
            result = String.valueOf(Long.parseLong(fieldA) - Long.parseLong(fieldB));
        } catch (NumberFormatException ee) {
            result = "0";
        }
        return result;
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param object the object of this type
     * @param fieldName the field name but the initial character should be capital
     * @return a field of this object
     */
    private String getDeltaLong(
            BrokerDiagData object, String fieldName, String fieldA, String fieldB, String fieldC) {
        String result = "";
        try {
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                result = String.valueOf(partA + partB);
            } else {
                result = String.valueOf(Long.parseLong(fieldA) - Long.parseLong(fieldB));
                if (Long.parseLong(result) < 0) {
                    result = String.valueOf(Long.parseLong(fieldB) - Long.parseLong(fieldC));
                    long aValue = Long.parseLong(fieldB) + Long.parseLong(result);
                    Class<?> cc = BrokerDiagData.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }

        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the long value from given the strings which should be a field of a instance of the same
     * type
     *
     * @param object a field of this object
     * @param fieldName the field name but the initial character should be capital
     * @param fieldA the field of a object
     * @param fieldB the field of a object
     * @param fieldC the field of a object
     * @param inter float
     * @return String
     */
    private String getDeltaLong(
            BrokerDiagData object,
            String fieldName,
            String fieldA,
            String fieldB,
            String fieldC,
            float inter) {
        String result = "";
        try {
            long temp = 0;
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                temp = partA + partB;
            } else {
                temp = Long.parseLong(fieldA) - Long.parseLong(fieldB);
                if (temp < 0) {
                    temp = Long.parseLong(fieldB) - Long.parseLong(fieldC);
                    long aValue = (Long.parseLong(fieldB) + temp);
                    Class<?> cc = BrokerDiagData.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }
            LOGGER.debug(fieldName + "(before divided by interval) = " + temp);
            temp = (long) (temp / inter);
            result = String.valueOf(temp);
            LOGGER.debug(fieldName + "(after divided by interval) = " + result);
        } catch (NumberFormatException ee) {
            result = "0";
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Get the status
     *
     * @return the status
     */
    public boolean getStatus() {
        if ("success".equals(status)) {
            return true;
        }
        return false;
    }

    /** @param status the status to set */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the note
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /** @param note the note to set */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get the broker name
     *
     * @return the broker_name
     */
    public String getBname() {
        return bname;
    }

    /** @param brokerName the broker_name to set */
    public void setBname(String brokerName) {
        bname = brokerName;
    }

    /**
     * Get the cas_mon_req
     *
     * @return the cas_mon_req
     */
    public String getCas_mon_req() {
        return cas_mon_req;
    }

    /** @param casMonReq the cas_mon_req to set */
    public void setCas_mon_req(String casMonReq) {
        cas_mon_req = casMonReq;
    }

    /**
     * Get the cas_mon_act_session
     *
     * @return the cas_mon_act_session
     */
    public String getCas_mon_act_session() {
        return cas_mon_act_session;
    }

    /** @param casMonActSession the cas_mon_act_session to set */
    public void setCas_mon_act_session(String casMonActSession) {
        cas_mon_act_session = casMonActSession;
    }

    /**
     * Get the cas_mon_tran
     *
     * @return the cas_mon_tran
     */
    public String getCas_mon_tran() {
        return cas_mon_tran;
    }

    /** @param casMonTran the cas_mon_tran to set */
    public void setCas_mon_tran(String casMonTran) {
        cas_mon_tran = casMonTran;
    }

    /**
     * Get the cas_mon_query.
     *
     * @return the cas_mon_query
     */
    public String getCas_mon_query() {
        return cas_mon_query;
    }

    /** @param casMonQuery the cas_mon_query to set */
    public void setCas_mon_query(String casMonQuery) {
        cas_mon_query = casMonQuery;
    }

    /**
     * Get the cas_mon_long_query.
     *
     * @return the cas_mon_long_query
     */
    public String getCas_mon_long_query() {
        return cas_mon_long_query;
    }

    /** @param casMonLongQuery the cas_mon_long_query to set */
    public void setCas_mon_long_query(String casMonLongQuery) {
        cas_mon_long_query = casMonLongQuery;
    }

    /**
     * Get the cas_mon_long_tran
     *
     * @return the cas_mon_long_tran
     */
    public String getCas_mon_long_tran() {
        return cas_mon_long_tran;
    }

    /** @param casMonLongTran the cas_mon_long_tran to set */
    public void setCas_mon_long_tran(String casMonLongTran) {
        cas_mon_long_tran = casMonLongTran;
    }

    /**
     * Get the cas_mon_error_query
     *
     * @return the cas_mon_error_query
     */
    public String getCas_mon_error_query() {
        return cas_mon_error_query;
    }

    /** @param casMonErrorQuery the cas_mon_error_query to set */
    public void setCas_mon_error_query(String casMonErrorQuery) {
        cas_mon_error_query = casMonErrorQuery;
    }

    /**
     * Get the cas_mon_session.
     *
     * @return the cas_mon_session
     */
    public String getCas_mon_session() {
        return cas_mon_session;
    }

    /** @param casMonSession the cas_mon_session to set */
    public void setCas_mon_session(String casMonSession) {
        cas_mon_session = casMonSession;
    }

    /**
     * Get the cas_mon_active.
     *
     * @return the cas_mon_active
     */
    public String getCas_mon_active() {
        return cas_mon_active;
    }

    /** @param casMonActive the cas_mon_active to set */
    public void setCas_mon_active(String casMonActive) {
        cas_mon_active = casMonActive;
    }

    /**
     * Get the diagStatusResultMap which include the status result
     *
     * @return the diagStatusResultMap
     */
    public TreeMap<IDiagPara, String> getDiagStatusResultMap() {
        return diagStatusResultMap;
    }

    /**
     * Add the subBroker which is the instance of BrokerDiagData
     *
     * @param brokerDiagData the instance of BrokerDiagData
     */
    public void addBroker(BrokerDiagData brokerDiagData) {
        subBrokerDiagLst.add(brokerDiagData);
    }

    /**
     * Get the list which include all the sub instance of BrokerDiagData
     *
     * @return List<BrokerDiagData>
     */
    public List<BrokerDiagData> getBrokerList() {
        return subBrokerDiagLst;
    }

    /**
     * Get the sub instance of BrokerDiagData by given broker name.
     *
     * @param bname the broker name
     * @return the instance of BrokerDiagData
     */
    public BrokerDiagData getSubBrokerByName(String bname) {
        BrokerDiagData brokerDiagData = new BrokerDiagData();
        if (subBrokerDiagLst.isEmpty() || bname == null) {
            return brokerDiagData;
        }
        for (BrokerDiagData bdd : subBrokerDiagLst) {
            if (bname.equals(bdd.getBname())) {
                return bdd;
            }
        }
        return brokerDiagData;
    }
}
