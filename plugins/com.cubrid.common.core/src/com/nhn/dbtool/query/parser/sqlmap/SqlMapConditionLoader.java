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
package com.nhn.dbtool.query.parser.sqlmap;

import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapCondition;
import org.apache.commons.lang.StringUtils;

/**
 * Dynamic 쿼리의 Tag에 해당하는 Class를 동적으로 생성하여 전달하는 처리
 *
 * @author Bumsik, Jang
 */
public class SqlMapConditionLoader {
    private static final String SQLMAP_MODEL_PACKAGE_NAME =
            "com.nhn.dbtool.query.parser.sqlmap.model."; // TODO SQLMAP

    /**
     * Dynamic 쿼리의 Tag에 해당하는 Class를 동적으로 생성
     *
     * @param tagName Dynamic 쿼리의 Tag 이름 (isNull, isNotNull, isEmpty, ...)
     * @return 해당 Tag 이름에 맞는 SqlMapCondition 객체
     * @throws Exception 오류
     */
    public static SqlMapCondition getSqlMapConditionTag(String tagName) throws Exception {
        if (StringUtils.isEmpty(tagName)) {
            throw new Exception("It has no value on the tagName parameter.");
        }

        String classFullName = getClassFullName(tagName);
        try {
            ClassLoader classLoader = SqlMapConditionLoader.class.getClassLoader();
            Class<?> conditionClass = classLoader.loadClass(classFullName);
            SqlMapCondition conditionTag = (SqlMapCondition) conditionClass.newInstance();
            return conditionTag;
        } catch (ClassNotFoundException e) {
            throw new Exception("The SqlMapConditionTag was not defined. [" + classFullName + "]");
        } catch (InstantiationException e) {
            throw new Exception("The SqlMapConditionTag was not defined. [" + classFullName + "]");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getClassFullName(String tagName) {
        StringBuffer classFullName = new StringBuffer();
        classFullName.append(SQLMAP_MODEL_PACKAGE_NAME);
        classFullName.append(tagName.substring(0, 1).toUpperCase());
        classFullName.append(tagName.substring(1));
        classFullName.append("Tag");
        return classFullName.toString();
    }
}
