package com.cubrid.cubridmanager.ui.service.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Service Dashboard Editor Sorter
 *
 * @author Ray Yin
 * @version 1.0 - 2013.03.28 created by Ray Yin
 */
public class ServiceDashboardEditorSorter extends ViewerSorter {
    private static final int Name = 1;
    private static final int Address = 2;
    private static final int Port = 3;
    private static final int User = 4;
    private static final int Data = 5;
    private static final int Index = 6;
    private static final int Temp = 7;
    private static final int Generic = 8;
    private static final int Tps = 9;
    private static final int Qps = 10;
    private static final int ErrorQ = 11;
    private static final int Memory = 12;
    private static final int Disk = 13;
    private static final int Cpu = 14;
    private static final int DbStatus = 15;
    private static final int Version = 16;
    private static final int BrokerPort = 17;

    public static final ServiceDashboardEditorSorter Name_ASC =
            new ServiceDashboardEditorSorter(Name);
    public static final ServiceDashboardEditorSorter Name_DESC =
            new ServiceDashboardEditorSorter(-Name);
    public static final ServiceDashboardEditorSorter Address_ASC =
            new ServiceDashboardEditorSorter(Address);
    public static final ServiceDashboardEditorSorter Address_DESC =
            new ServiceDashboardEditorSorter(-Address);
    public static final ServiceDashboardEditorSorter Port_ASC =
            new ServiceDashboardEditorSorter(Port);
    public static final ServiceDashboardEditorSorter Port_DESC =
            new ServiceDashboardEditorSorter(-Port);
    public static final ServiceDashboardEditorSorter User_ASC =
            new ServiceDashboardEditorSorter(User);
    public static final ServiceDashboardEditorSorter User_DESC =
            new ServiceDashboardEditorSorter(-User);
    public static final ServiceDashboardEditorSorter Data_ASC =
            new ServiceDashboardEditorSorter(Data);
    public static final ServiceDashboardEditorSorter Data_DESC =
            new ServiceDashboardEditorSorter(-Data);
    public static final ServiceDashboardEditorSorter Index_ASC =
            new ServiceDashboardEditorSorter(Index);
    public static final ServiceDashboardEditorSorter Index_DESC =
            new ServiceDashboardEditorSorter(-Index);
    public static final ServiceDashboardEditorSorter Temp_ASC =
            new ServiceDashboardEditorSorter(Temp);
    public static final ServiceDashboardEditorSorter Temp_DESC =
            new ServiceDashboardEditorSorter(-Temp);
    public static final ServiceDashboardEditorSorter Generic_ASC =
            new ServiceDashboardEditorSorter(Generic);
    public static final ServiceDashboardEditorSorter Generic_DESC =
            new ServiceDashboardEditorSorter(-Generic);
    public static final ServiceDashboardEditorSorter Tps_ASC =
            new ServiceDashboardEditorSorter(Tps);
    public static final ServiceDashboardEditorSorter Tps_DESC =
            new ServiceDashboardEditorSorter(-Tps);
    public static final ServiceDashboardEditorSorter Qps_ASC =
            new ServiceDashboardEditorSorter(Qps);
    public static final ServiceDashboardEditorSorter Qps_DESC =
            new ServiceDashboardEditorSorter(-Qps);
    public static final ServiceDashboardEditorSorter ErrorQ_ASC =
            new ServiceDashboardEditorSorter(ErrorQ);
    public static final ServiceDashboardEditorSorter ErrorQ_DESC =
            new ServiceDashboardEditorSorter(-ErrorQ);
    public static final ServiceDashboardEditorSorter Memory_ASC =
            new ServiceDashboardEditorSorter(Memory);
    public static final ServiceDashboardEditorSorter Memory_DESC =
            new ServiceDashboardEditorSorter(-Memory);
    public static final ServiceDashboardEditorSorter Disk_ASC =
            new ServiceDashboardEditorSorter(Disk);
    public static final ServiceDashboardEditorSorter Disk_DESC =
            new ServiceDashboardEditorSorter(-Disk);
    public static final ServiceDashboardEditorSorter Cpu_ASC =
            new ServiceDashboardEditorSorter(Cpu);
    public static final ServiceDashboardEditorSorter Cpu_DESC =
            new ServiceDashboardEditorSorter(-Cpu);
    public static final ServiceDashboardEditorSorter DbStatus_ASC =
            new ServiceDashboardEditorSorter(DbStatus);
    public static final ServiceDashboardEditorSorter DbStatus_DESC =
            new ServiceDashboardEditorSorter(-DbStatus);
    public static final ServiceDashboardEditorSorter Version_ASC =
            new ServiceDashboardEditorSorter(Version);
    public static final ServiceDashboardEditorSorter Version_DESC =
            new ServiceDashboardEditorSorter(-Version);
    public static final ServiceDashboardEditorSorter BrokerPort_ASC =
            new ServiceDashboardEditorSorter(BrokerPort);
    public static final ServiceDashboardEditorSorter BrokerPort_DESC =
            new ServiceDashboardEditorSorter(-BrokerPort);

    private int sortType;

    /** The default constructor */
    private ServiceDashboardEditorSorter(int sortType) {
        this.sortType = sortType;
    }

    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof ServiceDashboardInfo && e2 instanceof ServiceDashboardInfo) {
            ServiceDashboardInfo cm1 = (ServiceDashboardInfo) e1;
            ServiceDashboardInfo cm2 = (ServiceDashboardInfo) e2;

            switch (sortType) {
                case Name:
                    {
                        String name1 = cm1.getServer().getName();
                        String name2 = cm2.getServer().getName();
                        return name1.compareTo(name2);
                    }
                case -Name:
                    {
                        String name1 = cm1.getServer().getName();
                        String name2 = cm2.getServer().getName();
                        return name2.compareTo(name1);
                    }
                case Address:
                    {
                        String address1 = cm1.getServer().getHostAddress();
                        String address2 = cm2.getServer().getHostAddress();
                        return address1.compareTo(address2);
                    }
                case -Address:
                    {
                        String address1 = cm1.getServer().getHostAddress();
                        String address2 = cm2.getServer().getHostAddress();
                        return address2.compareTo(address1);
                    }
                case Port:
                    {
                        String port1 = cm1.getServer().getMonPort();
                        String port2 = cm2.getServer().getMonPort();
                        return port1.compareTo(port2);
                    }
                case -Port:
                    {
                        String port1 = cm1.getServer().getMonPort();
                        String port2 = cm2.getServer().getMonPort();
                        return port2.compareTo(port1);
                    }
                case User:
                    {
                        String user1 = cm1.getServer().getUserName();
                        String user2 = cm2.getServer().getUserName();
                        return user1.compareTo(user2);
                    }
                case -User:
                    {
                        String user1 = cm1.getServer().getUserName();
                        String user2 = cm2.getServer().getUserName();
                        return user2.compareTo(user1);
                    }
                case Data:
                    {
                        Integer data1 = cm1.getFreeDataPerc();
                        Integer data2 = cm2.getFreeDataPerc();
                        return data1.compareTo(data2);
                    }
                case -Data:
                    {
                        Integer data1 = cm1.getFreeDataPerc();
                        Integer data2 = cm2.getFreeDataPerc();
                        return data2.compareTo(data1);
                    }
                case Index:
                    {
                        Integer index1 = cm1.getFreeIndexPerc();
                        Integer index2 = cm2.getFreeIndexPerc();
                        return index1.compareTo(index2);
                    }
                case -Index:
                    {
                        Integer index1 = cm1.getFreeIndexPerc();
                        Integer index2 = cm2.getFreeIndexPerc();
                        return index2.compareTo(index1);
                    }
                case Temp:
                    {
                        Integer temp1 = cm1.getFreeTempPerc();
                        Integer temp2 = cm2.getFreeTempPerc();
                        return temp1.compareTo(temp2);
                    }
                case -Temp:
                    {
                        Integer temp1 = cm1.getFreeTempPerc();
                        Integer temp2 = cm2.getFreeTempPerc();
                        return temp2.compareTo(temp1);
                    }
                case Generic:
                    {
                        Integer generic1 = cm1.getFreeGenericPerc();
                        Integer generic2 = cm2.getFreeGenericPerc();
                        return generic1.compareTo(generic2);
                    }
                case -Generic:
                    {
                        Integer generic1 = cm1.getFreeGenericPerc();
                        Integer generic2 = cm2.getFreeGenericPerc();
                        return generic2.compareTo(generic1);
                    }
                case Tps:
                    {
                        Integer tps1 = cm1.getServerTps();
                        Integer tps2 = cm2.getServerTps();
                        return tps1.compareTo(tps2);
                    }
                case -Tps:
                    {
                        Integer tps1 = cm1.getServerTps();
                        Integer tps2 = cm2.getServerTps();
                        return tps2.compareTo(tps1);
                    }
                case Qps:
                    {
                        Integer qps1 = cm1.getServerQps();
                        Integer qps2 = cm2.getServerQps();
                        return qps1.compareTo(qps2);
                    }
                case -Qps:
                    {
                        Integer qps1 = cm1.getServerQps();
                        Integer qps2 = cm2.getServerQps();
                        return qps2.compareTo(qps1);
                    }
                case ErrorQ:
                    {
                        Integer error1 = cm1.getServerErrorQ();
                        Integer error2 = cm2.getServerErrorQ();
                        return error1.compareTo(error2);
                    }
                case -ErrorQ:
                    {
                        Integer error1 = cm1.getServerErrorQ();
                        Integer error2 = cm2.getServerErrorQ();
                        return error2.compareTo(error1);
                    }
                case Memory:
                    {
                        Double memory1 = cm1.getMemTotal();
                        Double memory2 = cm2.getMemTotal();
                        return memory1.compareTo(memory2);
                    }
                case -Memory:
                    {
                        Double memory1 = cm1.getMemTotal();
                        Double memory2 = cm2.getMemTotal();
                        return memory2.compareTo(memory1);
                    }
                case Disk:
                    {
                        Long disk1 = cm1.getFreespaceOnStorage();
                        Long disk2 = cm2.getFreespaceOnStorage();
                        return disk1.compareTo(disk2);
                    }
                case -Disk:
                    {
                        Long disk1 = cm1.getFreespaceOnStorage();
                        Long disk2 = cm2.getFreespaceOnStorage();
                        return disk2.compareTo(disk1);
                    }
                case Cpu:
                    {
                        Double cpu1 = cm1.getCpuUsed();
                        Double cpu2 = cm2.getCpuUsed();
                        return cpu1.compareTo(cpu2);
                    }
                case -Cpu:
                    {
                        Double cpu1 = cm1.getCpuUsed();
                        Double cpu2 = cm2.getCpuUsed();
                        return cpu2.compareTo(cpu1);
                    }
                case DbStatus:
                    {
                        Integer db1 = cm1.getDatabaseOn();
                        Integer db2 = cm2.getDatabaseOn();
                        return db1.compareTo(db2);
                    }
                case -DbStatus:
                    {
                        Integer db1 = cm1.getDatabaseOn();
                        Integer db2 = cm2.getDatabaseOn();
                        return db2.compareTo(db1);
                    }
                case Version:
                    {
                        String version1 = cm1.getServerVersion();
                        String version2 = cm2.getServerVersion();
                        return version1.compareTo(version2);
                    }
                case -Version:
                    {
                        String version1 = cm1.getServerVersion();
                        String version2 = cm2.getServerVersion();
                        return version2.compareTo(version1);
                    }
                case BrokerPort:
                    {
                        String brokerPort1 = cm1.getBrokerPort();
                        String brokerPort2 = cm2.getBrokerPort();
                        return brokerPort1.compareTo(brokerPort2);
                    }
                case -BrokerPort:
                    {
                        String brokerPort1 = cm1.getBrokerPort();
                        String brokerPort2 = cm2.getBrokerPort();
                        return brokerPort2.compareTo(brokerPort1);
                    }
            }
        }
        return 0;
    }
}
