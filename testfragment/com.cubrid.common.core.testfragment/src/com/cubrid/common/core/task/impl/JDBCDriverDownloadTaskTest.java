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
package com.cubrid.common.core.task.impl;

import com.cubrid.cubridmanager.core.SetupJDBCTestCase;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fulei
 * @version 1.0 - 2012-12-19 created by fulei
 */
public class JDBCDriverDownloadTaskTest extends SetupJDBCTestCase {
    String fileName = "CUBRID-8.4.1_jdbc.jar";
    String dirPath = this.getFilePathInPlugin("/com/cubrid/common/core/task/impl");
    //	String dirPath = "d:" + File.separator;
    public void testGetJDBCFileList() {
        JDBCDriverDownloadTask task = new JDBCDriverDownloadTask();
        task.getDriverList();
        assertNull(task.getErrorMsg());
        try {
            assertFalse(task.getJDBCFileList().isEmpty());
        } catch (Exception ignore) {

        }
    }

    public void testDownloadDriver() {
        List<String> driverList = new ArrayList<String>();
        driverList.add(fileName);

        JDBCDriverDownloadTask task = new JDBCDriverDownloadTask(driverList, dirPath, "", "");
        task.execute();
        assertTrue(task.isSuccess());
    }

    protected void tearDown() throws Exception {
        File file = new File(dirPath + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
