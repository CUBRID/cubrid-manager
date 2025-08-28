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

import com.nhn.dbtool.query.parser.sqlmap.model.MyBatisTestCondition;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapCondition;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapParameter;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapQuery;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * A sqlmap query parser.
 *
 * @author Bumsik, Jang
 */
public class SqlMapQueryParser {
    private Logger logger = LoggerFactory.getLogger(SqlMapQueryParser.class);

    /**
     * Query 단위의 Element를 통해 SqlMapQuery를 생성한다.
     *
     * @param queryElement Query 단위의 Element
     * @throws Exception
     */
    public SqlMapQuery parse(Element queryElement) throws Exception {

        StringBuffer modifiedQuery = new StringBuffer();
        SqlMapConditionParser conditionParser = new SqlMapConditionParser();
        SqlMapParameterParser parameterParser = new SqlMapParameterParser();

        // Element 타입의 node에서 Query 정보를 읽어 SqlMapQuery를 생성한다.
        SqlMapQuery query = createSqlMapQuery(queryElement);

        // queryElement 하위의 모든 Node를 읽어온다
        // Node 타입이 TEXT, ELEMENT인 경우에 따라 정보 수집
        Iterator<?> nodeIterator = queryElement.nodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = (Node) nodeIterator.next();
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                case Node.CDATA_SECTION_NODE:
                    modifiedQuery.append(node.getText());

                    // Parameter 파싱
                    parameterParser.parse(node, query);
                    break;
                case Node.ELEMENT_NODE:
                    SqlMapCondition condition = conditionParser.parse((Element) node, query);
                    if (condition != null) {
                        query.getConditionList().add(condition);
                        modifiedQuery.append(condition.getKey());

                        if (!condition.getChildConditionList().isEmpty()) {
                            for (SqlMapCondition childCondition :
                                    condition.getChildConditionList()) {
                                if (childCondition != null) {
                                    query.getConditionList().add(childCondition);
                                    //									modifiedQuery.append(childCondition.getKey());
                                }
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        }

        // MyBatis test 속성 내의 조건식을 쿼리 조합시 사용하도록 parameter list에 입력
        extractMyBatisTestConditions(query, query.getConditionList());

        query.setModifiedQuery(modifiedQuery.toString());

        logger.debug(query.getModifiedQuery());

        if (query.isPrimitiveTypeParameter()
                && query.isDynamicQuery() == false
                && query.getConditionList().size() > 0) {
            query.addDefaultDynamicParameter();
        }

        return query;
    }

    /**
     * MyBatis test 속성 내의 조건식을 쿼리 조합시 사용하도록 parameter list에 입력
     *
     * @param query
     * @param conditionList
     */
    private void extractMyBatisTestConditions(
            SqlMapQuery query, List<SqlMapCondition> conditionList) {
        if (conditionList == null) {
            return;
        }

        for (SqlMapCondition condition : conditionList) {
            if (condition == null || condition.getMyBatisTestConditions() == null) {
                continue;
            }

            for (MyBatisTestCondition mCondition : condition.getMyBatisTestConditions()) {
                String property = mCondition.getProperty();
                SqlMapParameter sqlmapParameter = new SqlMapParameter();
                sqlmapParameter.setName(property);
                sqlmapParameter.setDynamic(true);
                query.addParameter(sqlmapParameter);
            }

            if ("foreach".equals(condition.getType())) {
                SqlMapParameter sqlmapParameter = new SqlMapParameter();
                sqlmapParameter.setName(condition.getCollection());
                sqlmapParameter.setDynamic(true);
                query.addParameter(sqlmapParameter);
            }

            if (condition.getChildConditionList() != null) {
                extractMyBatisTestConditions(query, condition.getChildConditionList());
            }
        }
    }

    /**
     * Element 타입의 node에서 Query 정보를 읽어 SqlMapQuery를 생성한다.
     *
     * @param node Element 타입의 node
     * @return Query 정보가 포함된 SqlMapQuery
     * @throws Exception
     */
    private SqlMapQuery createSqlMapQuery(Node node) throws Exception {
        SqlMapQuery query = new SqlMapQuery();
        query.setType(node.getName());
        query.setId(SqlMapParserUtil.getAttribute(node, "id"));
        query.setParameterClass(SqlMapParserUtil.getAttribute(node, "parameterClass"));
        logger.debug("[[node.asXML]]" + node.asXML());

        // Element에서 하위 노드의 tag가 포함된 text를 읽기위해서는 node.asXML()를 사용하여야 하는데,
        // 이때, Element 자체의 tag가 포함되어 있어 순수한 text값을 읽어들이기 위해서 해당 tag를 제거한다.
        String text = SqlMapParserUtil.removeElementTag(node.asXML(), node.getName());
        query.setQuery(text);

        return query;
    }
}
