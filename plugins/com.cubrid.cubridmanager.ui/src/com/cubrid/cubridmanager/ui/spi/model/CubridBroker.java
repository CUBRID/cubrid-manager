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
import com.cubrid.cubridmanager.core.broker.model.BrokerInfo;
import com.cubrid.cubridmanager.core.common.model.OnOffType;

/**
 * CUBRID broker node object
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridBroker extends DefaultCubridNode {

    private String startedIconPath;

    /**
     * The constructor
     *
     * @param id
     * @param label
     * @param stopedIconPath
     */
    public CubridBroker(String id, String label, String stopedIconPath) {
        super(id, label, stopedIconPath);
        setType(CubridNodeType.BROKER);
        setContainer(true);
    }

    /**
     * Get whether the broker is running
     *
     * @return <code>true</code> if it is running;<code>false</code> otherwise
     */
    public boolean isRunning() {
        return getBrokerInfo() == null
                ? false
                : getBrokerInfo()
                        .getState()
                        .toLowerCase()
                        .equals(OnOffType.ON.getText().toLowerCase());
    }

    /**
     * Set the broker running status
     *
     * @param isRunning whether it is running
     */
    public void setRunning(boolean isRunning) {
        if (getBrokerInfo() != null) {
            getBrokerInfo().setState(isRunning ? OnOffType.ON.getText() : OnOffType.OFF.getText());
        }
    }

    /**
     * Get broker information
     *
     * @return the BrokerInfo object
     */
    public BrokerInfo getBrokerInfo() {
        Object obj = this.getAdapter(BrokerInfo.class);
        if (obj instanceof BrokerInfo) {
            return (BrokerInfo) obj;
        }
        return null;
    }

    /**
     * Get icon path of broker started status
     *
     * @return the started icon path
     */
    public String getStartedIconPath() {
        return startedIconPath;
    }

    /**
     * Set icon path of broker started status
     *
     * @param startedIconPath the started icon path
     */
    public void setStartedIconPath(String startedIconPath) {
        this.startedIconPath = startedIconPath;
    }

    /**
     * Get icon path of broker stoped status
     *
     * @return the stoped icon path
     */
    public String getStopedIconPath() {
        return this.getIconPath();
    }

    /**
     * Set icon path of broker stoped status
     *
     * @param stopedIconPath the stoped icon path
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
        if (!(obj instanceof CubridBroker)) {
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
}
