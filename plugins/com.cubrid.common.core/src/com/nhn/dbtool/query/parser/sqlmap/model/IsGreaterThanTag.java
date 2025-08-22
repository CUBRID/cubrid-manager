/*
 * Copyright (C) 2015 Search Solution Corporation. All rights reserved by Search
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
package com.nhn.dbtool.query.parser.sqlmap.model;

import java.util.List;

/**
 * A definition of isGreaterThan tag. <isGreaterThan property="" prepend="" open="" close=""
 * removeFirstPrepend="true" compareProperty="" compareValue=""></isGreaterThan>
 *
 * @author Bumsik, Jang
 */
public class IsGreaterThanTag extends SqlMapCondition {
    private static final long serialVersionUID = 6693608168146150612L;

    public IsGreaterThanTag() {
        this.setType("isGreaterThan");
    }

    @Override
    public String getExpectedCompareValue() {
        return compareValue != null
                ? "> " + compareValue
                : (compareProperty != null ? "> #" + compareProperty + "#" : null);
    }

    @Override
    public boolean isMatchCondition(List<String> parameterList) {
        for (String parameter : parameterList) {
            if (!parameter.startsWith(getProperty())) {
                continue;
            }

            String[] value = parameter.split(":");
            try {
                if (value.length == 0) {
                    return false;
                } else if (value.length == 1) {
                    return true;
                } else if (value[1].equals(this.getExpectedCompareValue())
                        || greaterThan(value[1], compareValue)) {
                    return true;
                }
            } catch (Exception ignored) {
                // ignore
            }
        }

        return false;
    }

    private boolean greaterThan(String value, String compareValue) {
        return Double.parseDouble(value) > Double.parseDouble(compareValue);
    }
}
