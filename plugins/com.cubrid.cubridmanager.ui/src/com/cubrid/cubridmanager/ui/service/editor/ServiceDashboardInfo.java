package com.cubrid.cubridmanager.ui.service.editor;

import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.DefaultCubridNode;

/**
 * ServiceDashboardInfo Description
 *
 * @author Ray Yin
 * @version 1.0 - 2013-3-27 created by Ray Yin
 */
public class ServiceDashboardInfo extends DefaultCubridNode {
    private CubridServer server;
    private int freeDataPerc, freeIndexPerc, freeTempPerc, freeGenericPerc;
    private int tps, qps, errorQ;
    private int databaseOn, databaseOff;
    private long freespaceOnStorage;
    private double memUsed, memTotal, cpuUsed;
    private String serverVersion, brokerPort;

    /** The default constructor */
    ServiceDashboardInfo(
            String id, String label, String disconnectedIconPath, CubridServer server) {
        super(id, label, disconnectedIconPath);
        this.server = server;
        this.freeDataPerc = -1;
        this.freeIndexPerc = -1;
        this.freeTempPerc = -1;
        this.freeGenericPerc = -1;
    }

    public CubridServer getServer() {
        return server;
    }

    public void setFreeDataPerc(int freeDataPerc) {
        this.freeDataPerc = freeDataPerc;
    }

    public int getFreeDataPerc() {
        return this.freeDataPerc;
    }

    public void setFreeIndexPerc(int freeIndexPerc) {
        this.freeIndexPerc = freeIndexPerc;
    }

    public int getFreeIndexPerc() {
        return this.freeIndexPerc;
    }

    public void setFreeTempPerc(int freeTempPerc) {
        this.freeTempPerc = freeTempPerc;
    }

    public int getFreeTempPerc() {
        return this.freeTempPerc;
    }

    public void setFreeGenericPerc(int freeGenericPerc) {
        this.freeGenericPerc = freeGenericPerc;
    }

    public int getFreeGenericPerc() {
        return this.freeGenericPerc;
    }

    public void setServerTps(int tps) {
        this.tps = tps;
    }

    public int getServerTps() {
        return this.tps;
    }

    public void setServerQps(int qps) {
        this.qps = qps;
    }

    public int getServerQps() {
        return this.qps;
    }

    public void setServerErrorQ(int errorQ) {
        this.errorQ = errorQ;
    }

    public int getServerErrorQ() {
        return this.errorQ;
    }

    public void setFreespaceOnStorage(long freespaceOnStorage) {
        this.freespaceOnStorage = freespaceOnStorage;
    }

    public long getFreespaceOnStorage() {
        return this.freespaceOnStorage;
    }

    public void setMemUsed(double memUsed) {
        this.memUsed = memUsed;
    }

    public double getMemUsed() {
        return this.memUsed;
    }

    public void setMemTotal(double memTotal) {
        this.memTotal = memTotal;
    }

    public double getMemTotal() {
        return this.memTotal;
    }

    public void setCpuUsed(double cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public double getCpuUsed() {
        return this.cpuUsed;
    }

    public void setDatabaseOn(int databaseOn) {
        this.databaseOn = databaseOn;
    }

    public int getDatabaseOn() {
        return this.databaseOn;
    }

    public void setDatabaseOff(int databaseOff) {
        this.databaseOff = databaseOff;
    }

    public int getDatabaseOff() {
        return this.databaseOff;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public void setBrokerPort(String brokerPort) {
        this.brokerPort = brokerPort;
    }

    public String getBrokerPort() {
        return this.brokerPort;
    }
}
