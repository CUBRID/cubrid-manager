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
package com.cubrid.cubridmanager.core.cubrid.database.model;

import com.cubrid.common.core.util.LogUtil;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

public class Collation implements Cloneable {
    private static final Logger LOGGER = LogUtil.getLogger(Collation.class);
    public static final String DEFAULT_COLLATION = "utf8_bin";

    private String name;
    private String charset;

    public Collation() {}

    public Collation(String name, String charset) {
        this.name = name;
        this.charset = charset;
    }

    public static List<Collation> getDefaultCollations() {
        List<Collation> collationList = new LinkedList<Collation>();
        Collation collation;
        collation = new Collation("iso88591_bin", "iso88591");
        collationList.add(collation);
        collation = new Collation("utf8_bin", "utf8");
        collationList.add(collation);
        collation = new Collation("iso88591_en_cs", "iso88591");
        collationList.add(collation);
        collation = new Collation("iso88591_en_ci", "iso88591");
        collationList.add(collation);
        collation = new Collation("utf8_en_cs", "utf8");
        collationList.add(collation);
        collation = new Collation("utf8_en_ci", "utf8");
        collationList.add(collation);
        collation = new Collation("utf8_tr_cs", "utf8");
        collationList.add(collation);
        collation = new Collation("utf8_ko_cs", "utf8");
        collationList.add(collation);
        collation = new Collation("euckr_bin", "euckr");
        collationList.add(collation);

        return collationList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Collation clone() {
        Collation collation = null;
        try {
            collation = (Collation) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return collation;
    }
}
