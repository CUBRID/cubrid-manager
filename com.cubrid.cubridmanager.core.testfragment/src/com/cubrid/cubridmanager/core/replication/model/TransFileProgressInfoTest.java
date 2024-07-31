package com.cubrid.cubridmanager.core.replication.model;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class TransFileProgressInfoTest extends TestCase {

    public void testSlaveInfo() {
        TransFileProgressInfo progressInfo = new TransFileProgressInfo();
        String transferStatus = "success";
        String transferNote = "This is successfully";
        String sourceDir = "/home/cubrid/database/masterdb";
        String destHost = "192.168.1.221";
        String destDir = "/home/cubrid/database/sdb";
        String fileNum = "2";
        Map<String, String> fileProgressMap = new HashMap<String, String>();
        String fileName = "bak_db_001";
        String progress = "80%";
        fileProgressMap.put(fileName, progress);

        progressInfo.setTransferStatus(transferStatus);
        progressInfo.setTransferNote(transferNote);
        progressInfo.setSourceDir(sourceDir);
        progressInfo.setDestHost(destHost);
        progressInfo.setDestDir(destDir);
        progressInfo.setFileNum(fileNum);
        progressInfo.setFileProgressMap(fileProgressMap);

        assertEquals(progressInfo.getTransferStatus(), transferStatus);
        assertEquals(progressInfo.getTransferNote(), transferNote);
        assertEquals(progressInfo.getSourceDir(), sourceDir);
        assertEquals(progressInfo.getDestHost(), destHost);
        assertEquals(progressInfo.getFileNum(), fileNum);
        assertEquals(progressInfo.getFileProgressMap().get(fileName), progress);
        assertEquals(progressInfo.getDestDir(), destDir);
        progressInfo.getProgress(fileName);
        progressInfo.setFileProgressMap(null);
        progressInfo.getProgress(fileName);
    }
}
