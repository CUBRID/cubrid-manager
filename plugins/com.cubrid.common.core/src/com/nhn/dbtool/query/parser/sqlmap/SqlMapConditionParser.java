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
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapQuery;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * tag content에서 condition 및 parameter를 파싱
 *
 * @author Bumsik, Jang
 */
public class SqlMapConditionParser {
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(SqlMapConditionParser.class);

    /** MyBatis mapper xml의 "test" attribute만 파싱하는 MyBatis 전용 파서 */
    private final MyBatisTestConditionParser myBatisTestConditionParser =
            new MyBatisTestConditionParser();

    /**
     * Condition 단위의 Element를 통해 SqlMapCondition를 생성한다.
     *
     * @param conditionElement Condition 단위의 Element
     * @param query
     * @throws Exception
     */
    public SqlMapCondition parse(Element conditionElement, SqlMapQuery query) throws Exception {
        SqlMapConditionParser conditionParser = new SqlMapConditionParser();
        SqlMapParameterParser parameterParser = new SqlMapParameterParser();
        StringBuffer modifiedStatement = new StringBuffer();

        // 최상위 Element의 attribute로 Condition 생성
        SqlMapCondition condition = createSqlMapCondition(conditionElement);
        if (condition != null) {
            parameterParser.parse(conditionElement, query);

            // rootElement 하위의 모든 Node를 읽어온다
            // Node 타입이 TEXT, ELEMENT인 경우에 따라 정보 수집
            Iterator<?> nodeIterator = conditionElement.nodeIterator();
            while (nodeIterator.hasNext()) {
                Node node = (Node) nodeIterator.next();
                switch (node.getNodeType()) {
                    case Node.TEXT_NODE:
                    case Node.CDATA_SECTION_NODE:
                        // 변환된 statement 취합
                        modifiedStatement.append(node.getText());
                        // Parameter 파싱
                        parameterParser.parse(node, query);
                        break;

                    case Node.ELEMENT_NODE:
                        // recursive
                        // 생성된 condition을 childConditionList에서 추가
                        SqlMapCondition childCondition =
                                conditionParser.parse((Element) node, query);
                        if (childCondition != null) {
                            // MyBatis
                            initSqlMapCondition(node, childCondition);
                            condition.getChildConditionList().add(childCondition);
                            // 변환된 statement 취합
                            modifiedStatement.append(childCondition.getKey());
                        }
                        // Parameter 파싱
                        parameterParser.parse(node, query);
                        break;

                    default:
                        break;
                }
            }

            // 변경된 statement 저장
            condition.setModifiedStatement(modifiedStatement.toString());

            // include 처리용 statement 저장
            Element copiedElement = conditionElement.createCopy();
            copiedElement.clearContent();
            copiedElement.setText(modifiedStatement.toString());
            condition.setIncludedStatement(copiedElement.asXML());
        }

        return condition;
    }

    /**
     * Element 타입의 node에서 condition tag 정보를 읽어 SqlMapCondition를 생성한다.
     *
     * @param node Element 타입의 node
     * @return condition tag 정보가 포함된 SqlMapCondition
     * @throws Exception
     */
    private SqlMapCondition createSqlMapCondition(Node node) throws Exception {
        // 현재의 element name에 해당하는 Class를 동적으로 생성
        SqlMapCondition condition = SqlMapConditionLoader.getSqlMapConditionTag(node.getName());
        if (condition != null) {
            condition.setProperty(SqlMapParserUtil.getAttribute(node, "property"));
            condition.setPrepend(SqlMapParserUtil.getAttribute(node, "prepend"));
            condition.setOpen(SqlMapParserUtil.getAttribute(node, "open"));
            condition.setClose(SqlMapParserUtil.getAttribute(node, "close"));
            // condition.setRemoveFirstPrepend(SqlMapParserUtil.getAttribute(node,
            // "removeFirstPrepend"));
            condition.setCompareProperty(SqlMapParserUtil.getAttribute(node, "compareProperty"));
            condition.setCompareValue(SqlMapParserUtil.getAttribute(node, "compareValue"));
            condition.setRefid(SqlMapParserUtil.getAttribute(node, "refid"));
            condition.setConjunction(SqlMapParserUtil.getAttribute(node, "conjunction"));
            condition.setKeyProperty(SqlMapParserUtil.getAttribute(node, "keyProperty"));
            condition.setResultClass(SqlMapParserUtil.getAttribute(node, "resultClass"));
            condition.setStatement(node.asXML());

            // MyBatis 전용 속성 할당
            initSqlMapCondition(node, condition);
        }

        return condition;
    }

    /**
     * MyBatis 속성으로 초기화
     *
     * @param node
     * @param condition
     */
    private void initSqlMapCondition(Node node, SqlMapCondition condition) {
        if (SqlMapParserUtil.getAttribute(node, "test") != null) {
            parseMyBatisTestCondition(node, condition);
        }
        condition.setItem(SqlMapParserUtil.getAttribute(node, "item"));
        condition.setIndex(SqlMapParserUtil.getAttribute(node, "index"));
        condition.setCollection(SqlMapParserUtil.getAttribute(node, "collection"));
        condition.setSeparator(SqlMapParserUtil.getAttribute(node, "separator"));
        condition.setPrefix(SqlMapParserUtil.getAttribute(node, "prefix"));
        condition.setSuffixOverrides(SqlMapParserUtil.getAttribute(node, "suffixOverrides"));
        condition.setPrefixOverrides(SqlMapParserUtil.getAttribute(node, "prefixOverrides"));
        condition.setName(SqlMapParserUtil.getAttribute(node, "name"));
        condition.setValue(SqlMapParserUtil.getAttribute(node, "value"));
    }

    /**
     * MyBatis mapper xml의 "test" 속성을 파싱해서 SqlMapCondition object의 MyBatisTestConditions에 입력
     *
     * @param node
     * @param sqlMapCondition
     */
    private void parseMyBatisTestCondition(Node node, SqlMapCondition sqlMapCondition) {
        String test = SqlMapParserUtil.getAttribute(node, "test");
        if (StringUtils.isBlank(test) || sqlMapCondition == null) {
            return;
        }

        List<MyBatisTestCondition> conditions = myBatisTestConditionParser.parse(test);
        sqlMapCondition.getMyBatisTestConditions().addAll(conditions);
    }
}
