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

import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapParameter;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapQuery;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Node;

/**
 * A parser to extract paramters of the sqlmap.
 *
 * @author Bumsik, Jang
 */
public class SqlMapParameterParser {
    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(SqlMapParameterParser.class);

    /**
     * Parse parameters
     *
     * @param node
     * @param query SqlMapQuery
     * @throws Exception
     */
    public void parse(Node node, SqlMapQuery query) throws Exception {
        // Element node인 경우 property attribute를 통해서 파라미터를 추출
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            createDynamicParameter(node, query);
        }
        createStaticParameter(node.getText(), query);
    }

    /**
     * Element내의 text 내용에서 파라미터를 추출하여 SqlMapQuery의 ParameterList에 추가 한다.
     *
     * @param nodeText Element내의 text 내용
     * @param query 추출한 파라미터를 담을 SqlMapQuery 객체
     */
    private void createStaticParameter(String nodeText, SqlMapQuery query) {
        List<String> parameterList = SqlMapParserUtil.parseParameter(nodeText);
        for (String parameterName : parameterList) {
            SqlMapParameter newParameter = new SqlMapParameter();
            newParameter.setName(parameterName);
            newParameter.setDynamic(false);
            query.addParameter(newParameter);
        }
    }

    /**
     * Element node의 property attribute를 통해서 파라미터를 추출하여 SqlMapQuery의 ParameterList에 추가 한다.
     *
     * @param node Element node
     * @param query 추출한 파라미터를 담을 SqlMapQuery 객체
     */
    private void createDynamicParameter(Node node, SqlMapQuery query) {
        String parameterName = SqlMapParserUtil.getAttribute(node, "property");
        if (StringUtils.isNotEmpty(parameterName)) {
            SqlMapParameter newParameter = new SqlMapParameter();
            newParameter.setName(parameterName);
            newParameter.setDynamic(true);
            query.addParameter(newParameter);
        }
    }
}
