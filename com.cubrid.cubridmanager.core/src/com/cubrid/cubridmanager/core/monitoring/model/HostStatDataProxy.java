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

import java.util.TreeMap;

/**
 * This type is responsible for transforming the data in the instance of HostStatData to the data in
 * the instance of this type
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-12 created by lizhiqiang
 */
public class HostStatDataProxy {
    private String userPercent;
    private String kernelPercent;
    private String iowaitPercent;
    private String cpuTotal;
    // memory unit :KB
    private String memPhyTotal;
    private String memPhyUsed;
    private String memSwapTotal;
    private String memSwapUsed;
    private String memPhyPercent;
    private String memSwapPercent;
    private final TreeMap<IDiagPara, String> diagStatusResultMap;

    private static final int CONVERT_RATE = 1024;

    // Constructor
    public HostStatDataProxy() {
        userPercent = "0";
        kernelPercent = "0";
        iowaitPercent = "0";
        cpuTotal = "0";
        memPhyTotal = "0";
        memPhyUsed = "0";
        memSwapTotal = "0";
        memSwapUsed = "0";
        memPhyPercent = "0";
        memSwapPercent = "0";
        diagStatusResultMap = new TreeMap<IDiagPara, String>();
    }

    /** Put the new value of fields to map */
    private void putVauleInMap() {
        diagStatusResultMap.put(HostStatEnum.USER, userPercent);
        diagStatusResultMap.put(HostStatEnum.KERNEL, kernelPercent);
        diagStatusResultMap.put(HostStatEnum.IOWAIT, iowaitPercent);
        diagStatusResultMap.put(HostStatEnum.CPU_TOTAL, cpuTotal);
        diagStatusResultMap.put(HostStatEnum.MEMPHY_TOTAL, memPhyTotal);
        diagStatusResultMap.put(HostStatEnum.MEMPHY_USED, memPhyUsed);
        diagStatusResultMap.put(HostStatEnum.MEMSWAP_TOTAL, memSwapTotal);
        diagStatusResultMap.put(HostStatEnum.MEMSWAP_USED, memSwapUsed);
        diagStatusResultMap.put(HostStatEnum.MEMPHY_PERCENT, memPhyPercent);
        diagStatusResultMap.put(HostStatEnum.MEMSWAP_PERCENT, memSwapPercent);
    }

    /**
     * Compute the fields values
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     */
    public void compute(HostStatData hsdA, HostStatData hsdB) {
        int user = getCpuUserPercent(hsdA, hsdB);
        userPercent = Integer.toString(user);

        int kernel = getCpuKernelPercent(hsdA, hsdB);
        kernelPercent = Integer.toString(kernel);

        int iowait = getCpuIOwaitPercent(hsdA, hsdB);
        iowaitPercent = Integer.toString(iowait);

        long cpuTotalLong = hsdA.getCpuTotalDelta(hsdA, hsdB);
        cpuTotal = Long.toString(cpuTotalLong);

        long memPhyTotalLong = Long.parseLong(hsdA.getMem_phy_total()) / CONVERT_RATE;

        memPhyTotal = Long.toString(memPhyTotalLong);
        Long usedMemPhy =
                (Long.parseLong(hsdA.getMem_phy_total()) - Long.parseLong(hsdA.getMem_phy_free()))
                        / CONVERT_RATE;
        memPhyUsed = Long.toString(usedMemPhy);

        memPhyPercent = Integer.toString((int) ((double) usedMemPhy / memPhyTotalLong * 100 + 0.5));

        long memSwapTtlLong = Long.parseLong(hsdA.getMem_swap_total()) / CONVERT_RATE;
        memSwapTotal = Long.toString(memSwapTtlLong);

        long usedMemSwap =
                (Long.parseLong(hsdA.getMem_swap_total()) - Long.parseLong(hsdA.getMem_swap_free()))
                        / CONVERT_RATE;
        memSwapUsed = Long.toString(usedMemSwap);

        memSwapPercent =
                Integer.toString((int) ((double) usedMemSwap / memSwapTtlLong * 100 + 0.5));
        putVauleInMap();
    }

    /**
     * Compute the fields values
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     */
    public void compute(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        int user = getCpuUserPercent(hsdA, hsdB, hsdC);
        userPercent = Integer.toString(user);

        int kernel = getCpuKernelPercent(hsdA, hsdB, hsdC);
        kernelPercent = Integer.toString(kernel);

        int iowait = getCpuIOwaitPercent(hsdA, hsdB, hsdC);
        iowaitPercent = Integer.toString(iowait);

        long memPhyTotalLong = Long.parseLong(hsdA.getMem_phy_total()) / CONVERT_RATE;
        memPhyTotal = Long.toString(memPhyTotalLong);

        long usedMemPhy =
                (Long.parseLong(hsdA.getMem_phy_total()) - Long.parseLong(hsdA.getMem_phy_free()))
                        / CONVERT_RATE;
        memPhyUsed = Long.toString(usedMemPhy);
        memPhyPercent = Integer.toString((int) ((double) usedMemPhy / memPhyTotalLong * 100 + 0.5));

        long memSwapTtlLong = Long.parseLong(hsdA.getMem_swap_total()) / CONVERT_RATE;
        memSwapTotal = Long.toString(memSwapTtlLong);

        long usedMemSwap =
                (Long.parseLong(hsdA.getMem_swap_total()) - Long.parseLong(hsdA.getMem_swap_free()))
                        / CONVERT_RATE;
        memSwapUsed = Long.toString(usedMemSwap);

        memSwapPercent =
                Integer.toString((int) ((double) usedMemSwap / memSwapTtlLong * 100 + 0.5));

        putVauleInMap();
    }

    /**
     * Get the CPU user using percentage
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return a percent value
     */
    public int getCpuUserPercent(HostStatData hsdA, HostStatData hsdB) {
        double cpuUserDlt = hsdA.getCpuUserDelta(hsdA, hsdB);
        double cpuTotalDlt = hsdA.getCpuTotalDelta(hsdA, hsdB);
        return (int) ((cpuUserDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU user using percentage
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return a percent value
     */
    public int getCpuUserPercent(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        double cpuUserDlt = hsdA.getCpuUserDelta(hsdA, hsdB, hsdC);
        long cpuTotalDlt = hsdA.getCpuTotalDelta(hsdA, hsdB, hsdC);
        return (int) ((cpuUserDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU kernel using percentage
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return a percent value
     */
    public int getCpuKernelPercent(HostStatData hsdA, HostStatData hsdB) {
        double cpuKernelDlt = hsdA.getCpuKernelDelta(hsdA, hsdB);
        double cpuTotalDlt = hsdA.getCpuTotalDelta(hsdA, hsdB);
        return (int) ((cpuKernelDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU kernel using percentage
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return a percent value
     */
    public int getCpuKernelPercent(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        double cpuKernelDlt = hsdA.getCpuKernelDelta(hsdA, hsdB, hsdC);
        double cpuTotalDlt = hsdA.getCpuTotalDelta(hsdA, hsdB, hsdC);
        return (int) ((cpuKernelDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU kernel using percentage
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @return a percent value
     */
    public int getCpuIOwaitPercent(HostStatData hsdA, HostStatData hsdB) {
        double cpuIOwaitDlt = hsdA.getCpuIOwaitDelta(hsdA, hsdB);
        double cpuTotalDlt = hsdA.getCpuTotalDelta(hsdA, hsdB);
        return (int) ((cpuIOwaitDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the CPU kernel using percentage
     *
     * @param hsdA a instance of HostStatData
     * @param hsdB a instance of HostStatData
     * @param hsdC a instance of HostStatData
     * @return a percent value
     */
    public int getCpuIOwaitPercent(HostStatData hsdA, HostStatData hsdB, HostStatData hsdC) {
        double cpuIOwaitDlt = hsdA.getCpuIOwaitDelta(hsdA, hsdB, hsdC);
        long cpuTotalDlt = hsdA.getCpuTotalDelta(hsdA, hsdB, hsdC);
        return (int) ((cpuIOwaitDlt / cpuTotalDlt * 100) + 0.5);
    }

    /**
     * Get the userPercent
     *
     * @return the userPercent
     */
    public String getUserPercent() {
        return userPercent;
    }

    /**
     * Get the kernelPercent
     *
     * @return the kernelPercent
     */
    public String getKernelPercent() {
        return kernelPercent;
    }

    /**
     * Get the iowaitPercent
     *
     * @return the iowaitPercent
     */
    public String getIowaitPercent() {
        return iowaitPercent;
    }

    /**
     * Get the memPhyTotal
     *
     * @return the memPhyTotal
     */
    public String getMemPhyTotal() {
        return memPhyTotal;
    }

    /**
     * Get the memPhyUsed
     *
     * @return the memPhyUsed
     */
    public String getMemPhyUsed() {
        return memPhyUsed;
    }

    /**
     * Get the memSwapTotal
     *
     * @return the memSwapTotal
     */
    public String getMemSwapTotal() {
        return memSwapTotal;
    }

    /**
     * Get the memSwapUsed
     *
     * @return the memSwapUsed
     */
    public String getMemSwapUsed() {
        return memSwapUsed;
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
