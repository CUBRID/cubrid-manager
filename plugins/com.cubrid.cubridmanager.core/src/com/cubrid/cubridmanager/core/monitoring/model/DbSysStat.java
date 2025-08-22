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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;

/**
 * This class includes the information of a database process.Generally, these information is a part
 * of the type of DbProstata
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-4 created by lizhiqiang
 */
public class DbSysStat {
    private static final Logger LOGGER = LogUtil.getLogger(DbSysStat.class);
    private String dbname;
    private String cpu_kernel;
    private String cpu_user;
    private String mem_physical;
    private String mem_virtual;

    // Constructor
    public DbSysStat() {
        dbname = "0";
        cpu_kernel = "0";
        cpu_user = "0";
        mem_physical = "0";
        mem_virtual = "0";
    }

    /**
     * clone the data from another instance of the DbServerProc
     *
     * @param clone a instance of DbServerProc
     */
    public void copyFrom(DbSysStat clone) {
        dbname = clone.dbname;
        cpu_kernel = clone.cpu_kernel;
        cpu_user = clone.cpu_user;
        mem_physical = clone.mem_physical;
        mem_virtual = clone.mem_virtual;
    }

    /**
     * Get the CPU user using percentage
     *
     * @param dspA a instance of DbServerProc
     * @param dspB a instance of DbServerProc
     * @return the percentage value
     */
    public int getCpuUserPercent(DbSysStat dspA, DbSysStat dspB) {
        double cpuUserDlt = getCpuUserDelta(dspA, dspB);
        double cpuTotalDlt = getCpuTotalDelta(dspA, dspB);
        return (int) ((cpuUserDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU user using percentage
     *
     * @param dspA a instance of DbServerProc
     * @param dspB a instance of DbServerProc
     * @param dspC a instance of DbServerProc
     * @return the percentage value
     */
    public int getCpuUserPercent(DbSysStat dspA, DbSysStat dspB, DbSysStat dspC) {
        double cpuUserDlt = getCpuUserDelta(dspA, dspB, dspC);
        double cpuTotalDlt = getCpuTotalDelta(dspA, dspB, dspC);
        return (int) ((cpuUserDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU kernel using percentage
     *
     * @param dspA a instance of DbServerProc
     * @param dspB a instance of DbServerProc
     * @return the percentage value
     */
    public int getCpuKernelPercent(DbSysStat dspA, DbSysStat dspB) {
        double cpuKernelDlt = getCpuKernelDelta(dspA, dspB);
        double cpuTotalDlt = getCpuTotalDelta(dspA, dspB);
        return (int) ((cpuKernelDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU kernel using percentage
     *
     * @param dspA a instance of DbServerProc
     * @param dspB a instance of DbServerProc
     * @param dspC a instance of DbServerProc
     * @return the percentage value
     */
    public int getCpuKernelPercent(DbSysStat dspA, DbSysStat dspB, DbSysStat dspC) {
        double cpuKernelDlt = getCpuKernelDelta(dspA, dspB, dspC);
        double cpuTotalDlt = getCpuTotalDelta(dspA, dspB, dspC);
        return (int) ((cpuKernelDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU total delta
     *
     * @param dspA a instance of DbServerProc
     * @param dspB a instance of DbServerProc
     * @return the delta of CPU total value
     */
    long getCpuTotalDelta(DbSysStat dspA, DbSysStat dspB) {
        long cpuUserA = Long.parseLong(dspA.cpu_user);
        long cpuKernelA = Long.parseLong(dspA.cpu_kernel);

        long cpuUserB = Long.parseLong(dspB.cpu_user);
        long cpuKernelB = Long.parseLong(dspB.cpu_kernel);

        long cpuUserDlt = cpuUserA - cpuUserB;
        long cpuKernelDlt = cpuKernelA - cpuKernelB;

        return cpuUserDlt + cpuKernelDlt;
    }

    /**
     * Get the CPU total delta
     *
     * @param dspA a instance of DbServerProc
     * @param dspB a instance of DbServerProc
     * @param dspC a instance of DbServerProc
     * @return the delta of CPU total value
     */
    long getCpuTotalDelta(DbSysStat dspA, DbSysStat dspB, DbSysStat dspC) {
        long cpuUserDlt =
                getDeltaLong(dspA, "Cpu_user", dspA.cpu_user, dspB.cpu_user, dspC.cpu_user);
        long cpuKernelDlt =
                getDeltaLong(dspA, "Cpu_kernel", dspA.cpu_kernel, dspB.cpu_kernel, dspC.cpu_kernel);
        return cpuUserDlt + cpuKernelDlt;
    }

    /**
     * Get the CPU user delta
     *
     * @param dssA a instance of DbServerProc
     * @param dssB a instance of DbServerProc
     * @return the delta of cpu user value
     */
    long getCpuUserDelta(DbSysStat dssA, DbSysStat dssB) {
        long cpuUserA = Long.parseLong(dssA.cpu_user);
        long cpuUserB = Long.parseLong(dssB.cpu_user);
        return cpuUserA - cpuUserB;
    }

    /**
     * Get the CPU user delta
     *
     * @param dssA a instance of DbServerProc
     * @param dssB a instance of DbServerProc
     * @param dssC a instance of DbServerProc
     * @return the delta of cpu user value
     */
    long getCpuUserDelta(DbSysStat dssA, DbSysStat dssB, DbSysStat dssC) {
        return getDeltaLong(dssA, "Cpu_user", dssA.cpu_user, dssB.cpu_user, dssC.cpu_user);
    }

    /**
     * Get the CPU kernel delta
     *
     * @param dssA a instance of DbServerProc
     * @param dssB a instance of DbServerProc
     * @return the delta of CPU user value
     */
    long getCpuKernelDelta(DbSysStat dssA, DbSysStat dssB) {
        long cpuKernelA = Long.parseLong(dssA.cpu_kernel);
        long cpuKernelB = Long.parseLong(dssB.cpu_kernel);
        return cpuKernelA - cpuKernelB;
    }

    /**
     * Get the CPU kernel delta
     *
     * @param dssA a instance of DbServerProc
     * @param dssB a instance of DbServerProc
     * @param dssC a instance of DbServerProc
     * @return the delta of CPU user value
     */
    long getCpuKernelDelta(DbSysStat dssA, DbSysStat dssB, DbSysStat dssC) {
        return getDeltaLong(dssA, "Cpu_kernel", dssA.cpu_kernel, dssB.cpu_kernel, dssC.cpu_kernel);
    }

    /**
     * Get the dbname.
     *
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /** @param dbname the dbname to set */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * Get cpu_kernel.
     *
     * @return the cpu_kernel
     */
    public String getCpu_kernel() {
        return cpu_kernel;
    }

    /** @param cpuKernel the cpu_kernel to set */
    public void setCpu_kernel(String cpuKernel) {
        cpu_kernel = cpuKernel;
    }

    /**
     * Get the cpu_virtual.
     *
     * @return the cpu_virtual
     */
    public String getCpu_user() {
        return cpu_user;
    }

    /** @param cpuUser the cpuUser to set */
    public void setCpu_user(String cpuUser) {
        cpu_user = cpuUser;
    }

    /**
     * Get the mem_physical
     *
     * @return the mem_physical
     */
    public String getMem_physical() {
        return mem_physical;
    }

    /** @param memPhysical the mem_physical to set */
    public void setMem_physical(String memPhysical) {
        mem_physical = memPhysical;
    }

    /**
     * Get the mem_virtual
     *
     * @return the mem_virtual
     */
    public String getMem_virtual() {
        return mem_virtual;
    }

    /** @param memVirtual the mem_virtual to set */
    public void setMem_virtual(String memVirtual) {
        mem_virtual = memVirtual;
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
    private long getDeltaLong(
            DbSysStat object, String fieldName, String fieldA, String fieldB, String fieldC) {
        long result = 0;
        try {
            if (Long.parseLong(fieldA) < 0 && Long.parseLong(fieldB) > 0) {
                long partA = Long.MAX_VALUE - Long.parseLong(fieldB);
                long partB = Long.parseLong(fieldA) - Long.MIN_VALUE;
                result = partA + partB;
            } else {
                result = Long.parseLong(fieldA) - Long.parseLong(fieldB);
                if (result < 0) {
                    result = Long.parseLong(fieldB) - Long.parseLong(fieldC);
                    long aValue = Long.parseLong(fieldB) + result;
                    Class<?> cc = DbSysStat.class;
                    Method mm = cc.getMethod("set" + fieldName, new Class[] {String.class});
                    mm.invoke(object, Long.toString(aValue));
                }
            }

        } catch (NumberFormatException ee) {
            result = 0;
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
     * @see java.lang.Object#hashCode()
     * @return int
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbname == null) ? 0 : dbname.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj the instance of Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DbSysStat other = (DbSysStat) obj;
        if (dbname == null) {
            if (other.dbname != null) {
                return false;
            }
        } else if (!dbname.equals(other.dbname)) {
            return false;
        }
        return true;
    }
}
