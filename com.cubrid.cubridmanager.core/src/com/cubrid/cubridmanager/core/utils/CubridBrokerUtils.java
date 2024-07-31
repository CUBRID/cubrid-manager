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
package com.cubrid.cubridmanager.core.utils;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import java.util.Map;

/**
 * Utilities for Cubrid broker manager.
 *
 * @author SC13425
 * @version 1.0 - 2010-11-23 created by SC13425
 */
public final class CubridBrokerUtils {

    // Constructor
    private CubridBrokerUtils() {
        // empty
    }

    /**
     * Retrieves Broker Port of windows server, Unix/linux cannot support the broker port setting.
     *
     * @param serverInfo ServerInfo
     * @param brokerMap Map<String, String>
     * @return BROKER PORT
     */
    public static String getBrokerPort(ServerInfo serverInfo, Map<String, String> brokerMap) {
        String defaultValue = null;
        if (CompatibleUtil.isWindows(serverInfo.getServerOsInfo())
                && brokerMap != null
                && brokerMap.get(ConfConstants.BROKER_PORT) != null) {
            int serverPortValue = Integer.parseInt(brokerMap.get(ConfConstants.BROKER_PORT)) + 1;
            defaultValue = Integer.toString(serverPortValue);
        }
        return defaultValue;
    }
}
