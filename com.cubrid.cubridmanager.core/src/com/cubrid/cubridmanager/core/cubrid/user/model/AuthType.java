/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.core.cubrid.user.model;

import com.cubrid.common.core.util.StringUtil;

/**
 * AuthType Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-2 created by Kevin.Wang
 */
public enum AuthType {
    SELECT(0x0001),
    INSERT(0x0002),
    UPDATE(0x0004),
    DELETE(0x0008),
    ALTER(0x0010),
    INDEX(0X0020),
    EXECUTE(0X0040),
    SELECTGRANT(0X0080),
    INSERTGRANT(0X0100),
    UPDATEGRANT(0X0200),
    DELETEGRANT(0X0400),
    ALTERGTANT(0X0800),
    INDEXGRANT(0X1000),
    EXECUTEGRANT(0X2000),
    ALL(0X3FFF);
    private int value;

    private AuthType(int type) {
        this.value = type;
    }

    /** @return the authType */
    public int getValue() {
        return value;
    }

    public static AuthType getAuthType(String auth, boolean isGrantAble) {
        if (StringUtil.isEqualIgnoreCase(auth, "SELECT")) {
            if (isGrantAble) {
                return SELECTGRANT;
            } else {
                return SELECT;
            }
        }

        if (StringUtil.isEqualIgnoreCase(auth, "INSERT")) {
            if (isGrantAble) {
                return INSERTGRANT;
            } else {
                return INSERT;
            }
        }

        if (StringUtil.isEqualIgnoreCase(auth, "UPDATE")) {
            if (isGrantAble) {
                return UPDATEGRANT;
            } else {
                return UPDATE;
            }
        }

        if (StringUtil.isEqualIgnoreCase(auth, "DELETE")) {
            if (isGrantAble) {
                return UPDATEGRANT;
            } else {
                return DELETE;
            }
        }

        if (StringUtil.isEqualIgnoreCase(auth, "ALTER")) {
            if (isGrantAble) {
                return ALTERGTANT;
            } else {
                return ALTER;
            }
        }

        if (StringUtil.isEqualIgnoreCase(auth, "INDEX")) {
            if (isGrantAble) {
                return INDEX;
            } else {
                return INDEXGRANT;
            }
        }

        if (StringUtil.isEqualIgnoreCase(auth, "EXECUTE")) {
            if (isGrantAble) {
                return EXECUTEGRANT;
            } else {
                return EXECUTE;
            }
        }
        return null;
    }

    public static AuthType getAuthType(int value) {
        if (SELECT.getValue() == value) return SELECT;

        if (INSERT.getValue() == value) return INSERT;

        if (UPDATE.getValue() == value) return UPDATE;

        if (DELETE.getValue() == value) return DELETE;

        if (ALTER.getValue() == value) return ALTER;

        if (INDEX.getValue() == value) return INDEX;

        if (EXECUTE.getValue() == value) return EXECUTE;

        if (SELECTGRANT.getValue() == value) return SELECTGRANT;

        if (INSERTGRANT.getValue() == value) return INSERTGRANT;

        if (UPDATEGRANT.getValue() == value) return UPDATEGRANT;

        if (DELETEGRANT.getValue() == value) return DELETEGRANT;

        if (ALTERGTANT.getValue() == value) return ALTERGTANT;

        if (INDEXGRANT.getValue() == value) return INDEXGRANT;

        if (EXECUTEGRANT.getValue() == value) return EXECUTEGRANT;

        if (ALL.getValue() == value) return ALL;

        return null;
    }

    public static boolean isHasAuth(AuthType authType, AuthType targetType) {
        if (authType == null || targetType == null) {
            return false;
        }

        return (authType.getValue() & targetType.getValue()) > 0;
    }

    public static AuthType mergeAuth(AuthType t1, AuthType t2) {
        if (t1 == null) {
            return t2;
        }

        if (t2 == null) {
            return t1;
        }

        return getAuthType(t1.getValue() | t2.getValue());
    }
}
