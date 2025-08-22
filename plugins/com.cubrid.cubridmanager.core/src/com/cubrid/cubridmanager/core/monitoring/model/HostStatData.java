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
import org.slf4j.Logger;

/**
 * A class that extends IModel and is responsible for the task of "gethoststat"
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-3 created by lizhiqiang
 */
public class HostStatData implements IModel {
    private static final Logger LOGGER = LogUtil.getLogger(HostStatData.class);

    private String status;
    private String note;
    private String cpu_user;
    private String cpu_kernel;
    private String cpu_idle;
    private String cpu_iowait;
    private String mem_phy_total;
    private String mem_phy_free;
    private String mem_swap_total;
    private String mem_swap_free;

    public HostStatData() {
        cpu_user = "0";
        cpu_kernel = "0";
        cpu_idle = "0";
        cpu_iowait = "0";
        mem_phy_total = "0";
        mem_phy_free = "0";
        mem_swap_total = "0";
        mem_swap_free = "0";
    }

    /**
     * clone the data from another instance of the HostStatData
     *
     * @param clone a instance of HostStatData
     */
    public void copyFrom(HostStatData clone) {
        cpu_user = clone.cpu_user;
        cpu_kernel = clone.cpu_kernel;
        cpu_idle = clone.cpu_idle;
        cpu_iowait = clone.cpu_iowait;
        mem_phy_total = clone.mem_phy_total;
        mem_phy_free = clone.mem_phy_free;
        mem_swap_total = clone.mem_swap_total;
        mem_swap_free = clone.mem_swap_free;
    }

    /**
     * Get the CPU total delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return the delta of CPU total value
     */
    long getCpuTotalDelta(HostStatData hsdA, HostStatData hsdB) {
        long cpuUserA = Long.parseLong(hsdA.cpu_user);
        long cpuKernelA = Long.parseLong(hsdA.cpu_kernel);
        long cpuIdleA = Long.parseLong(hsdA.cpu_idle);
        long cpuIowaitA = Long.parseLong(hsdA.cpu_iowait);

        long cpuUserB = Long.parseLong(hsdB.cpu_user);
        long cpuKernelB = Long.parseLong(hsdB.cpu_kernel);
        long cpuIdleB = Long.parseLong(hsdB.cpu_idle);
        long cpuIowaitB = Long.parseLong(hsdB.cpu_iowait);

        long cpuUserDlt = cpuUserA - cpuUserB;
        long cpuKernelDlt = cpuKernelA - cpuKernelB;
        long cpuIdleDlt = cpuIdleA - cpuIdleB;
        long cpuIowaitDlt = cpuIowaitA - cpuIowaitB;
        return cpuUserDlt + cpuKernelDlt + cpuIdleDlt + cpuIowaitDlt;
    }

    /**
     * Get the CPU total delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return the delta of cpu total value
     */
    long getCpuTotalDelta(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        long cpuUserDlt =
                getDeltaLong(hsdA, "Cpu_user", hsdA.cpu_user, hsdB.cpu_user, hsdC.cpu_user);
        long cpuKernelDlt =
                getDeltaLong(hsdA, "Cpu_kernel", hsdA.cpu_kernel, hsdB.cpu_kernel, hsdC.cpu_kernel);
        long cpuIdleDlt =
                getDeltaLong(hsdA, "Cpu_idle", hsdA.cpu_idle, hsdB.cpu_idle, hsdC.cpu_idle);
        long cpuIowaitDlt =
                getDeltaLong(hsdA, "Cpu_iowait", hsdA.cpu_iowait, hsdB.cpu_iowait, hsdC.cpu_iowait);
        return cpuUserDlt + cpuKernelDlt + cpuIdleDlt + cpuIowaitDlt;
    }

    /**
     * Get the cpu user delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return the delta of cpu total value
     */
    long getCpuUserDelta(HostStatData hsdA, HostStatData hsdB) {
        long cpuUserA = Long.parseLong(hsdA.cpu_user);
        long cpuUserB = Long.parseLong(hsdB.cpu_user);
        return cpuUserA - cpuUserB;
    }

    /**
     * Get the CPU user delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return the delta of cpu user value
     */
    long getCpuUserDelta(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        return getDeltaLong(hsdA, "Cpu_user", hsdA.cpu_user, hsdB.cpu_user, hsdC.cpu_user);
    }

    /**
     * Get the CPU kernel delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return the delta of cpu total value
     */
    long getCpuKernelDelta(HostStatData hsdA, HostStatData hsdB) {
        long cpuKernelA = Long.parseLong(hsdA.cpu_kernel);
        long cpuKernelB = Long.parseLong(hsdB.cpu_kernel);
        return cpuKernelA - cpuKernelB;
    }

    /**
     * Get the CPU kernel delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return the delta of cpu user value
     */
    long getCpuKernelDelta(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        return getDeltaLong(hsdA, "Cpu_kernel", hsdA.cpu_kernel, hsdB.cpu_kernel, hsdC.cpu_kernel);
    }

    /**
     * Get the CPU IO wait delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return the delta of cpu total value
     */
    long getCpuIOwaitDelta(HostStatData hsdA, HostStatData hsdB) {
        long cpuIOwaitA = Long.parseLong(hsdA.cpu_iowait);
        long cpuIOwaitB = Long.parseLong(hsdB.cpu_iowait);
        return cpuIOwaitA - cpuIOwaitB;
    }

    /**
     * Get the CPU IO wait delta
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return the delta of cpu user value
     */
    long getCpuIOwaitDelta(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        return getDeltaLong(hsdA, "Cpu_iowait", hsdA.cpu_iowait, hsdB.cpu_iowait, hsdC.cpu_iowait);
    }

    /* (non-Javadoc)
     * @see com.cubrid.cubridmanager.core.common.model.IModel#getTaskName()
     */
    public String getTaskName() {
        return "gethoststat";
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
     * Get the cup_user.
     *
     * @return the cpu_user
     */
    public String getCpu_user() {
        return cpu_user;
    }

    /** @param cpuUser the cpu_user to set */
    public void setCpu_user(String cpuUser) {
        cpu_user = cpuUser;
    }

    /**
     * Get the cpu_kernel.
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
     * Get the cpu_idle.
     *
     * @return the cpu_idle
     */
    public String getCpu_idle() {
        return cpu_idle;
    }

    /** @param cpuIdle the cpu_idle to set */
    public void setCpu_idle(String cpuIdle) {
        cpu_idle = cpuIdle;
    }

    /**
     * Get the cpu_iowait
     *
     * @return the cpu_iowait
     */
    public String getCpu_iowait() {
        return cpu_iowait;
    }

    /** @param cpuIowait the cpu_iowait to set */
    public void setCpu_iowait(String cpuIowait) {
        cpu_iowait = cpuIowait;
    }

    /**
     * Get the mem_phy_total
     *
     * @return the mem_phy_total
     */
    public String getMem_phy_total() {
        return mem_phy_total;
    }

    /** @param memPhyTotal the mem_phy_total to set */
    public void setMem_phy_total(String memPhyTotal) {
        mem_phy_total = memPhyTotal;
    }

    /**
     * Get the mem_phy_free
     *
     * @return the mem_phy_free
     */
    public String getMem_phy_free() {
        return mem_phy_free;
    }

    /** @param memPhyFree the mem_phy_free to set */
    public void setMem_phy_free(String memPhyFree) {
        mem_phy_free = memPhyFree;
    }

    /**
     * Get the mem_swap_total
     *
     * @return the mem_swap_total
     */
    public String getMem_swap_total() {
        return mem_swap_total;
    }

    /** @param memSwapTotal the mem_swap_total to set */
    public void setMem_swap_total(String memSwapTotal) {
        mem_swap_total = memSwapTotal;
    }

    /**
     * Get the mem_swap_free
     *
     * @return the mem_swap_free
     */
    public String getMem_swap_free() {
        return mem_swap_free;
    }

    /** @param memSwapFree the mem_swap_free to set */
    public void setMem_swap_free(String memSwapFree) {
        mem_swap_free = memSwapFree;
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
            HostStatData object, String fieldName, String fieldA, String fieldB, String fieldC) {
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
                    Class<?> cc = HostStatData.class;
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
}
