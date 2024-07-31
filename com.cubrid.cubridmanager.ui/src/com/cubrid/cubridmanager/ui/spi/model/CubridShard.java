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
package com.cubrid.cubridmanager.ui.spi.model;

import com.cubrid.common.ui.spi.model.DefaultCubridNode;
import com.cubrid.cubridmanager.core.shard.model.Shard;

/**
 * CUBRID shard node object
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-11-30
 */
public class CubridShard extends DefaultCubridNode {

    private String startedIconPath;

    private String name;

    /**
     * The constructor
     *
     * @param id
     * @param label
     * @param stopedIconPath
     */
    public CubridShard(String id, String label) {
        super(id, label, "icons/navigator/broker.png");
        setType(CubridNodeType.SHARD);
        setContainer(true);
        setStartedIconPath("icons/navigator/broker_started.png");
    }

    /**
     * Get whether the shard is running
     *
     * @return <code>true</code> if it is running;<code>false</code> otherwise
     */
    public boolean isRunning() {
        // TODO get status from shard information
        Shard shard = getShard();
        return shard == null ? false : shard.isRunning();
    }

    /**
     * Set the shard running status
     *
     * @param isRunning whether it is running
     */
    public void setRunning(boolean isRunning) {
        Shard shard = getShard();
        if (shard != null) {
            shard.setRunning(isRunning);
        }
    }

    /**
     * Get shard information
     *
     * @return the Shard object
     */
    public Shard getShard() {
        Object obj = this.getAdapter(Shard.class);
        if (obj instanceof Shard) {
            return (Shard) obj;
        }
        return null;
    }

    /**
     * Get icon path of shard started status
     *
     * @return the started icon path
     */
    public String getStartedIconPath() {
        return startedIconPath;
    }

    /**
     * Set icon path of shard started status
     *
     * @param startedIconPath the started icon path
     */
    public void setStartedIconPath(String startedIconPath) {
        this.startedIconPath = startedIconPath;
    }

    /**
     * Get icon path of shard stopped status
     *
     * @return the stopped icon path
     */
    public String getStopedIconPath() {
        return this.getIconPath();
    }

    /**
     * Set icon path of shard stopped status
     *
     * @param stopedIconPath the stopped icon path
     */
    public void setStopedIconPath(String stopedIconPath) {
        this.setIconPath(stopedIconPath);
    }

    /**
     * Return whether the current object is equal the obj
     *
     * @param obj the object
     * @return <code>true</code> if they are equal;<code>false</code> otherwise
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof CubridShard)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Return the hash code value
     *
     * @return the hash code value
     */
    public int hashCode() {
        return this.getId().hashCode();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
