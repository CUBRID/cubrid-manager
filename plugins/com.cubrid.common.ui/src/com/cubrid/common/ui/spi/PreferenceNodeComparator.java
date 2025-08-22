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
package com.cubrid.common.ui.spi;

import java.io.Serializable;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.ui.model.ContributionComparator;

/**
 * Preference node and Property node comparator
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-16 created by pangqiren
 */
public abstract class PreferenceNodeComparator extends ContributionComparator
        implements Serializable {

    private static final long serialVersionUID = -3345728687720482366L;

    /**
     * Compare object
     *
     * @param o1 Object
     * @param o2 Object
     * @return int
     */
    public int compare(Object o1, Object o2) {
        PreferenceNode node1 = null;
        PreferenceNode node2 = null;
        if (o1 instanceof PreferenceNode) {
            node1 = (PreferenceNode) o1;
        }
        if (o2 instanceof PreferenceNode) {
            node2 = (PreferenceNode) o2;
        }
        int retValue = compareNullObj(node1, node2);
        if (retValue != 2) {
            return retValue;
        }
        String id1 = node1.getId();
        String id2 = node2.getId();
        retValue = compareNullObj(id1, id2);
        if (retValue != 2) {
            return retValue;
        }
        return compareId(id1, id2);
    }

    /**
     * Compare the preference node id
     *
     * @param id1 String
     * @param id2 String
     * @return int
     */
    protected abstract int compareId(String id1, String id2);

    /**
     * Compare null object
     *
     * @param o1 Object
     * @param o2 Object
     * @return int
     */
    private int compareNullObj(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        return 2;
    }
}
