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

import java.util.List;
import java.util.TreeMap;

/**
 * This type is responsible for transforming the data in the instance of DbProcStat to the data in
 * the instance of this type
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-4 created by lizhiqiang
 */
public class DbProcStatProxy {
    private String deltaUser;
    private String deltaKernel;
    private String memPhysical;
    private String memVirtual;

    private final TreeMap<IDiagPara, String> diagStatusResultMap;

    private static final int CONVERT_RATE = 1024;

    public DbProcStatProxy() {
        deltaUser = "0";
        deltaKernel = "0";
        memPhysical = "0";
        memVirtual = "0";
        diagStatusResultMap = new TreeMap<IDiagPara, String>();
    }

    /** Put the new value of fields to map */
    private void putVauleInMap() {
        diagStatusResultMap.put(DbProcStatEnum.DELTA_USER, deltaUser);
        diagStatusResultMap.put(DbProcStatEnum.DELTA_KERNEL, deltaKernel);
        diagStatusResultMap.put(DbProcStatEnum.MEM_PHYSICAL, memPhysical);
        diagStatusResultMap.put(DbProcStatEnum.MEM_VIRTUAL, memVirtual);
    }

    /**
     * @param dbname the given database name
     * @param dpsA an instance of DbProcStat
     * @param dpsB an instance of DbProcStat
     */
    public void compute(String dbname, DbProcStat dpsA, DbProcStat dpsB) {
        long userDeltaL = getDeltaUserCpu(dbname, dpsA, dpsB);
        deltaUser = Long.toString(userDeltaL);

        long kernelDeltaL = getDeltaKernelCpu(dbname, dpsA, dpsB);
        deltaKernel = Long.toString(kernelDeltaL);

        long memPhyLong = getMemPhysical(dbname, dpsA) / CONVERT_RATE;
        long memVirLong = getMemVirtual(dbname, dpsA) / CONVERT_RATE;

        memPhysical = Long.toString(memPhyLong);
        memVirtual = Long.toString(memVirLong);

        putVauleInMap();
    }

    /**
     * @param dbname the given database name
     * @param dpsA an instance of DbProcStat
     * @param dpsB an instance of DbProcStat
     * @param dpsC an instance of DbProcStat
     */
    public void compute(String dbname, DbProcStat dpsA, DbProcStat dpsB, DbProcStat dpsC) {
        long userDeltaL = getDeltaUserCpu(dbname, dpsA, dpsB, dpsC);
        deltaUser = Long.toString(userDeltaL);

        long kernelDeltaL = getDeltaKernelCpu(dbname, dpsA, dpsB, dpsC);
        deltaKernel = Long.toString(kernelDeltaL);

        long memPhyLong = getMemPhysical(dbname, dpsA) / CONVERT_RATE;
        long memVirLong = getMemVirtual(dbname, dpsA) / CONVERT_RATE;

        memPhysical = Long.toString(memPhyLong);
        memVirtual = Long.toString(memVirLong);

        putVauleInMap();
    }

    /**
     * Get the delta value on CPU user between two different instance
     *
     * @param dbname the given database name
     * @param dpsA an instance of DbProcStat
     * @param dpsB an instance of DbProcStat
     * @return the delta value of CPU user
     */
    public long getDeltaUserCpu(String dbname, DbProcStat dpsA, DbProcStat dpsB) {
        if ("".equalsIgnoreCase(dbname)) {
            long cpuUserDlt = 0;
            //	double cpuTotalDlt = 0d;
            List<DbSysStat> dpsALst = dpsA.getDbProcLst();
            if (dpsALst.isEmpty()) {
                return 0;
            }
            for (DbSysStat dssA : dpsA.getDbProcLst()) {
                DbSysStat dssB = getSubType(dssA.getDbname(), dpsB);
                if (null == dssB) {
                    continue;
                }
                cpuUserDlt += dssA.getCpuUserDelta(dssA, dssB);
            }
            return cpuUserDlt;
        } else {
            DbSysStat dssA = getSubType(dbname, dpsA);
            if (null == dssA) {
                return 0;
            }
            DbSysStat dssB = getSubType(dbname, dpsB);
            if (null == dssB) {
                return 0;
            }
            return dssA.getCpuUserDelta(dssA, dssB);
        }
    }

    /**
     * Get the delta value on CPU kernel between two different instance
     *
     * @param dbname the given database name
     * @param dpsA an instance of DbProcStat
     * @param dpsB an instance of DbProcStat
     * @return the delta value of CPU kernel
     */
    public long getDeltaKernelCpu(String dbname, DbProcStat dpsA, DbProcStat dpsB) {
        if ("".equalsIgnoreCase(dbname)) {
            long cpuKernelDlt = 0;

            List<DbSysStat> dpsALst = dpsA.getDbProcLst();
            if (dpsALst.isEmpty()) {
                return 0;
            }
            for (DbSysStat dssA : dpsA.getDbProcLst()) {
                DbSysStat dssB = getSubType(dssA.getDbname(), dpsB);
                if (null == dssB) {
                    continue;
                }
                cpuKernelDlt += dssA.getCpuKernelDelta(dssA, dssB);
            }
            return cpuKernelDlt;
        } else {
            DbSysStat dssA = getSubType(dbname, dpsA);
            if (null == dssA) {
                return 0;
            }
            DbSysStat dssB = getSubType(dbname, dpsB);
            if (null == dssB) {
                return 0;
            }
            return dssA.getCpuKernelDelta(dssA, dssB);
        }
    }

    /**
     * Get the delta value on CPU user between two different instance
     *
     * @param dbname the database name
     * @param dpsA an instance of DbProcStat
     * @param dpsB an instance of DbProcStat
     * @param dpsC an instance of DbProcStat
     * @return the delta value of CPU user
     */
    public long getDeltaUserCpu(String dbname, DbProcStat dpsA, DbProcStat dpsB, DbProcStat dpsC) {

        if ("".equalsIgnoreCase(dbname)) {
            long cpuUserDlt = 0;
            List<DbSysStat> dpsALst = dpsA.getDbProcLst();
            if (dpsALst.isEmpty()) {
                return 0;
            }
            for (DbSysStat dssA : dpsA.getDbProcLst()) {
                DbSysStat dssB = getSubType(dssA.getDbname(), dpsB);
                if (null == dssB) {
                    continue;
                }
                DbSysStat dssC = getSubType(dssA.getDbname(), dpsC);
                if (null == dssC) {
                    return 0;
                }
                cpuUserDlt += dssA.getCpuUserDelta(dssA, dssB, dssC);
            }
            return cpuUserDlt;
        } else {
            DbSysStat dssA = getSubType(dbname, dpsA);
            if (null == dssA) {
                return 0;
            }
            DbSysStat dssB = getSubType(dbname, dpsB);
            if (null == dssB) {
                return 0;
            }
            DbSysStat dssC = getSubType(dbname, dpsC);
            if (null == dssC) {
                return 0;
            }
            return dssA.getCpuKernelDelta(dssA, dssB, dssC);
        }
    }

    /**
     * Get the delta value on CPU kernel between three different instance
     *
     * @param dbname the database name
     * @param dpsA an instance of DbProcStat
     * @param dpsB an instance of DbProcStat
     * @param dpsC an instance of DbProcStat
     * @return the delta value of CPU kernel
     */
    public long getDeltaKernelCpu(
            String dbname, DbProcStat dpsA, DbProcStat dpsB, DbProcStat dpsC) {
        if ("".equalsIgnoreCase(dbname)) {
            long cpuKernelDlt = 0;
            List<DbSysStat> dpsALst = dpsA.getDbProcLst();
            if (dpsALst.isEmpty()) {
                return 0;
            }
            for (DbSysStat dssA : dpsA.getDbProcLst()) {
                DbSysStat dssB = getSubType(dssA.getDbname(), dpsB);
                if (null == dssB) {
                    continue;
                }
                DbSysStat dssC = getSubType(dssA.getDbname(), dpsC);
                if (null == dssC) {
                    return 0;
                }
                cpuKernelDlt += dssA.getCpuKernelDelta(dssA, dssB, dssC);
            }
            return cpuKernelDlt;
        } else {
            DbSysStat dssA = getSubType(dbname, dpsA);
            if (null == dssA) {
                return 0;
            }
            DbSysStat dssB = getSubType(dbname, dpsB);
            if (null == dssB) {
                return 0;
            }
            DbSysStat dssC = getSubType(dbname, dpsC);
            if (null == dssC) {
                return 0;
            }
            return dssA.getCpuKernelDelta(dssA, dssB, dssC);
        }
    }

    /**
     * Get the physical memory based on the given dbname and the instance of DbProcStat.if the
     * dbname is "", the return value is the sum of all the physical memory value int the instance
     * of DbProcStat.
     *
     * @param dbname the database name
     * @param dps the instance of DbProcStat
     * @return the physical memory value
     */
    private long getMemPhysical(String dbname, DbProcStat dps) {
        if ("".equals(dbname)) {
            long memPhyLong = 0L;
            List<DbSysStat> dpsALst = dps.getDbProcLst();
            if (dpsALst.isEmpty()) {
                return 0;
            }
            for (DbSysStat dss : dps.getDbProcLst()) {
                memPhyLong += Long.parseLong(dss.getMem_physical());
            }
            return memPhyLong;
        } else {
            DbSysStat dss = getSubType(dbname, dps);
            if (null == dss) {
                return 0;
            }
            return Long.parseLong(dss.getMem_physical());
        }
    }

    /**
     * Get the virtual memory based on the given dbname and the instance of DbProcStat.if the dbname
     * is "", the return value is the sum of all the virtual memory value int the instance of
     * DbProcStat.
     *
     * @param dbname the database name
     * @param dps the instance of DbProcStat
     * @return the virtual memory value
     */
    private long getMemVirtual(String dbname, DbProcStat dps) {
        if ("".equals(dbname)) {
            long memVirtualLong = 0L;
            List<DbSysStat> dpsALst = dps.getDbProcLst();
            if (dpsALst.isEmpty()) {
                return 0;
            }
            for (DbSysStat dss : dps.getDbProcLst()) {
                memVirtualLong += Long.parseLong(dss.getMem_virtual());
            }
            return memVirtualLong;
        } else {
            DbSysStat dss = getSubType(dbname, dps);
            if (null == dss) {
                return 0;
            }
            return Long.parseLong(dss.getMem_virtual());
        }
    }

    /**
     * Get the sub type from the given instance of DbProcStat based on the given string.
     *
     * @param dbname the database name
     * @param dps an instance of DbProcStat
     * @return an instance of DbSysStat
     */
    private DbSysStat getSubType(String dbname, DbProcStat dps) {
        for (DbSysStat dss : dps.getDbProcLst()) {
            if (dbname.equals(dss.getDbname())) {
                return dss;
            }
        }
        return null;
    }

    /**
     * Get the memPhysical
     *
     * @return the memPhysical
     */
    public String getMemPhysical() {
        return memPhysical;
    }

    /**
     * Get the memVirtual
     *
     * @return the memVirtual
     */
    public String getMemVirtual() {
        return memVirtual;
    }

    /**
     * Get the diagStatusResultMap
     *
     * @return the diagStatusResultMap
     */
    public TreeMap<IDiagPara, String> getDiagStatusResultMap() {
        return diagStatusResultMap;
    }
}
