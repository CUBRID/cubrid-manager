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
package com.cubrid.common.core.util;

import java.util.Locale;

/**
 * Partition utility
 *
 * @author pangqiren
 * @version 1.0 - 2010-4-15 created by pangqiren
 */
public final class PartitionUtil {
    private static final String[] SUPPORTED_DATATYPES =
            new String[] {
                "CHAR",
                "VARCHAR",
                "VARYING",
                "INTEGER",
                "SMALLINT",
                "DATE",
                "TIMESTAMP",
                "TIME",
                "STRING"
            };

    private PartitionUtil() {}

    /**
     * Get supported data type
     *
     * @return The string array
     */
    public static String[] getSupportedDateTypes() {
        return SUPPORTED_DATATYPES.clone();
    }

    /**
     * Check whether match the type
     *
     * @param dataType String
     * @return boolean
     */
    public static boolean isMatchType(String dataType) {
        if (dataType == null || dataType.length() == 0) {
            return false;
        }

        String type = dataType.toUpperCase(Locale.getDefault());
        for (int i = 0; i < SUPPORTED_DATATYPES.length; i++) {
            if (type.startsWith(SUPPORTED_DATATYPES[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get match data type
     *
     * @param dataType The string
     * @return the String
     */
    public static String getMatchType(String dataType) {
        if (dataType == null || dataType.length() == 0) {
            return null;
        }

        String type = dataType.toUpperCase(Locale.getDefault());
        if (type.startsWith("CHARACTER VARYING")
                || type.startsWith("CHAR VARYING")
                || type.startsWith("VARYING")) {
            return "VARCHAR";
        }

        for (int i = 0; i < SUPPORTED_DATATYPES.length; i++) {
            if (type.startsWith(SUPPORTED_DATATYPES[i])) {
                return SUPPORTED_DATATYPES[i];
            }
        }

        return null;
    }

    /**
     * Check expression data type whether need add quote for expression value
     *
     * @param dataType The string
     * @return boolean
     */
    public static boolean isUsingQuoteForExprValue(String dataType) {
        if (dataType == null || dataType.length() == 0) {
            return true;
        }

        String[] usingQuoteDateType = {
            "CHAR", "VARCHAR", "VARYING", "DATE", "TIME", "TIMESTAMP", "STRING"
        };
        String type = dataType.toUpperCase(Locale.getDefault());
        for (int i = 0; i < usingQuoteDateType.length; i++) {
            if (type.startsWith(usingQuoteDateType[i])) {
                return true;
            }
        }

        return false;
    }
}
