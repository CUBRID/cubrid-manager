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
import com.cubrid.common.ui.spi.model.ICubridNode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CUBRID database server log model
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-1 created by pangqiren
 */
public class CubridDbLogNode extends DefaultCubridNode {
    /**
     * The constructor
     *
     * @param id
     * @param label
     * @param iconPath
     */
    public CubridDbLogNode(String id, String label, String iconPath) {
        super(id, label, iconPath);
    }

    /**
     * Compare the object
     *
     * @param obj the ICubridNode object
     * @return <code>1<code> greater;<code>0</code>equal;<code>-1</code> less
     */
    public int compareTo(ICubridNode obj) {
        if (obj == null) {
            return 1;
        }
        if (getType() == CubridNodeType.LOGS_SERVER_DATABASE_LOG) {
            String[] dateArr1 = getLabel().split("\\.")[0].split("_");
            String[] dateArr2 = obj.getLabel().split("\\.")[0].split("_");
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd hhmm", Locale.getDefault());
            int length = dateArr1.length;
            if (length > 2 && dateArr2.length == length) {
                String str1 = dateArr1[length - 2] + " " + dateArr1[length - 1];
                String str2 = dateArr2[length - 2] + " " + dateArr2[length - 1];
                try {
                    Date date1 = dateFormat.parse(str1);
                    Date date2 = dateFormat.parse(str2);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    return 0;
                }
            }
        }
        return super.compareTo(obj);
    }
}
