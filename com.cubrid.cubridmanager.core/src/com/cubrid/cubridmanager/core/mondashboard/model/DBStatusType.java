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
package com.cubrid.cubridmanager.core.mondashboard.model;

/**
 * HA database status
 *
 * @author pangqiren
 * @version 1.0 - 2010-5-31 created by pangqiren
 */
public enum DBStatusType {
    UNKNOWN("unknown"),
    STOPPED("stopped"),
    STOPPED_HA("stopped/HA"),
    CS_Mode("CS-mode"),
    ACTIVE("active"),
    STANDBY("standby"),
    TO_BE_ACTIVE("to-be-active"),
    TO_BE_STANDBY("to-be-standby"),
    MAINTENANCE("maintenance");
    String text = null;

    DBStatusType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * Get showed text in UI
     *
     * @param type DBStatusType
     * @return The String
     */
    public static String getShowText(DBStatusType type) {
        String ha = "/HA";
        if (ACTIVE == type
                || STANDBY == type
                || TO_BE_ACTIVE == type
                || TO_BE_STANDBY == type
                || MAINTENANCE == type) {
            return type.getText() + ha;
        }
        return type.getText();
    }

    /**
     * Convert the text to DBStatusType
     *
     * @param text The String
     * @param isHAMode The boolean
     * @return DBStatusType
     */
    public static DBStatusType getType(String text, boolean isHAMode) {
        if (STOPPED.getText().equals(text) && isHAMode) {
            return STOPPED_HA;
        }
        DBStatusType[] typeArr = DBStatusType.values();
        for (DBStatusType type : typeArr) {
            if (type.getText().equals(text)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * Return the database is started
     *
     * @param statusType DBStatusType
     * @return boolean
     */
    public static boolean isDbStarted(DBStatusType statusType) {
        return statusType == DBStatusType.CS_Mode
                || statusType == DBStatusType.ACTIVE
                || statusType == DBStatusType.STANDBY
                || statusType == DBStatusType.MAINTENANCE
                || statusType == DBStatusType.TO_BE_ACTIVE
                || statusType == DBStatusType.TO_BE_STANDBY;
    }
}
