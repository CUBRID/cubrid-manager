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
package com.cubrid.cubridmanager.core.broker.model;

import junit.framework.TestCase;

/**
 * Test JobInfo
 *
 * @author sq
 * @version 1.0 - 2010-1-7 created by sq
 */
public class JobInfoTest extends TestCase {
    private JobInfo bean;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        bean = new JobInfo();
    }

    /** Test method for {@link com.cubrid.cubridmanager.core.broker.model.JobInfo#getJob_id()}. */
    public void testGetJob_id() {
        bean.setJob_id("job_id");
        assertEquals(bean.getJob_id(), "job_id");
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.broker.model.JobInfo#getJob_priority()}.
     */
    public void testGetJob_priority() {
        bean.setJob_priority("job_priority");
        assertEquals(bean.getJob_priority(), "job_priority");
    }

    /** Test method for {@link com.cubrid.cubridmanager.core.broker.model.JobInfo#getJob_ip()}. */
    public void testGetJob_ip() {
        bean.setJob_ip("job_ip");
        assertEquals(bean.getJob_ip(), "job_ip");
    }

    /** Test method for {@link com.cubrid.cubridmanager.core.broker.model.JobInfo#getJob_time()}. */
    public void testGetJob_time() {
        bean.setJob_time("job_time");
        assertEquals(bean.getJob_time(), "job_time");
    }

    /**
     * Test method for {@link com.cubrid.cubridmanager.core.broker.model.JobInfo#getJob_request()}.
     */
    public void testGetJob_request() {
        bean.setJob_request("job_request");
        assertEquals(bean.getJob_request(), "job_request");
    }

    public void testClear() {
        bean.setJob_id("jobId");
        bean.clear();
        assertEquals("", bean.getJob_id());
    }
}
