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
package com.cubrid.cubridmanager.core.cubrid.database.model.lock;

/**
 * A plain java bean that includes the info of blocked holders
 *
 * @author robin
 * @version 1.0 - 2009-4-13 created by robin
 */
public class BlockedHolders {
    private int tran_index;
    private String granted_mode;
    private int count;
    private int nsubgranules;
    private String blocked_mode;
    private String start_at;
    private String wait_for_sec;

    public int getTran_index() {
        return tran_index;
    }

    public void setTran_index(int tranIndex) {
        this.tran_index = tranIndex;
    }

    public String getGranted_mode() {
        return granted_mode;
    }

    public void setGranted_mode(String grantedMode) {
        this.granted_mode = grantedMode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNsubgranules() {
        return nsubgranules;
    }

    public void setNsubgranules(int nsubgranules) {
        this.nsubgranules = nsubgranules;
    }

    public String getBlocked_mode() {
        return blocked_mode;
    }

    public void setBlocked_mode(String blockedMode) {
        this.blocked_mode = blockedMode;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String startAt) {
        this.start_at = startAt;
    }

    public String getWait_for_sec() {
        return wait_for_sec;
    }

    public void setWait_for_sec(String waitForSec) {
        this.wait_for_sec = waitForSec;
    }
}
