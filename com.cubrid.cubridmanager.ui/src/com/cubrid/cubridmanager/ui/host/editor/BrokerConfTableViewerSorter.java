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
package com.cubrid.cubridmanager.ui.host.editor;

import com.cubrid.cubridmanager.core.common.model.ConfConstants;
import java.util.HashMap;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author fulei
 * @version 1.0 - 2013-3-4 created by fulei
 */
public class BrokerConfTableViewerSorter extends ViewerSorter {
    private final int UNIMPORTANTINDEXVALUE = 10000;

    /**
     * Compares the object for sorting
     *
     * @param viewer the Viewer object
     * @param e1 the object
     * @param e2 the object
     * @return the compared value
     */
    @SuppressWarnings("unchecked")
    public int compare(Viewer viewer, Object e1, Object e2) {
        HashMap<String, String> valueMap1 = null;
        HashMap<String, String> valueMap2 = null;

        if (e1 instanceof HashMap && e2 instanceof HashMap) {
            valueMap1 = (HashMap<String, String>) e1;
            valueMap2 = (HashMap<String, String>) e2;
            int brokerNameIndex1 = getNameIndexOrder(valueMap1.get("0"));
            int brokerNameIndex2 = getNameIndexOrder(valueMap2.get("0"));
            if (brokerNameIndex1 == brokerNameIndex2) {
                return 0;
            }
            return brokerNameIndex1 < brokerNameIndex2 ? -1 : 1;
        }
        return 0;
    }

    /**
     * get broker name index
     *
     * @param name
     * @return
     */
    public int getNameIndexOrder(String name) {
        // SERVICENAMECOLUMNTITLE is the first data
        if (UnifyHostConfigEditor.SERVERNAMECOLUMNTITLE.equals(name)) {
            return -1;
        }
        // BROKERNAMECOLUMNTITLE is the second data
        if (UnifyHostConfigEditor.BROKERNAMECOLUMNTITLE.equals(name)) {
            return -1;
        }
        for (int i = 0; i < ConfConstants.brokerParameters.length; i++) {
            String brokerNameInbrokerParameters = ConfConstants.brokerParameters[i][0];
            if (brokerNameInbrokerParameters.equals(name)) {
                return i;
            }
        }
        return UNIMPORTANTINDEXVALUE;
    }
}
