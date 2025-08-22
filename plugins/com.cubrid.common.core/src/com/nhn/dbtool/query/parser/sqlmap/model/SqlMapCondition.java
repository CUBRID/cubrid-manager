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

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

/**
 * A metadata of dynamic queries.
 *
 * @author Bumsik, Jang
 * @author CHOE JUNGYEON
 */
public abstract class SqlMapCondition implements Serializable {
    private static final long serialVersionUID = 6169190921522543080L;
    private static final String QUERY_DEFAULT_DYNAMIC_PARAMETER = "dynamic_param";

    protected String key;
    protected String type;
    protected String statement;
    protected String modifiedStatement;

    /** include 만 처리한 statement */
    protected String includedStatement;

    protected List<SqlMapCondition> childConditionList = new ArrayList<SqlMapCondition>();

    protected String property;
    protected String prepend;
    protected String open;
    protected String close;
    protected String compareProperty;
    protected String compareValue;

    protected String refid;
    protected String conjunction;
    protected String keyProperty;
    protected String resultClass;

    // for MyBatis
    protected List<MyBatisTestCondition> myBatisTestConditions =
            new ArrayList<MyBatisTestCondition>();
    protected String item;
    protected String index;
    protected String collection;
    protected String separator;
    protected String prefix;
    protected String suffixOverrides;
    protected String prefixOverrides;
    protected String name;
    protected String value;

    /**
     * 수행될 statement를 리턴한다.
     *
     * @param parameterList 쿼리 수행시 전달될 파라미터 목록. ex) 'param:value'
     * @return 파라미터가 적용된 쿼리
     */
    public String createStatement(List<String> parameterList) {
        if (parameterList == null) {
            parameterList = new ArrayList<String>();
        }

        boolean useNotEqualCondition = false;
        String findParam = getProperty() + ":" + getCompareValue();
        if ("isNotEqual".equals(getType())) {
            useNotEqualCondition = true;
            for (String param : parameterList) {
                if (param.startsWith(findParam)) {
                    useNotEqualCondition = false;
                    break;
                }
            }
        }

        if (useNotEqualCondition || this.isMatchCondition(parameterList)) {
            StringBuilder query = new StringBuilder();

            if (StringUtils.isNotEmpty(this.prepend)) {
                query.append(this.prepend).append(" ");
            }
            query.append(getStringForStatement(this.open));
            query.append(getStringForStatement(this.modifiedStatement));
            query.append(getStringForStatement(this.close));

            String createdQuery = query.toString();

            boolean isChooseTag = false;
            int matchCounts = 0;
            if ("choose".equals(getType())) {
                isChooseTag = true;
            }

            // 하위의 condition이 존재하는 경우
            if (childConditionList != null && childConditionList.size() > 0) {
                for (SqlMapCondition childCondition : childConditionList) {
                    String content = childCondition.createStatement(parameterList);
                    if (isChooseTag) {
                        if (content != null && content.trim().length() > 0) {
                            isChooseTag = true;
                            matchCounts++;
                        }
                        if (matchCounts > 1) {
                            content = "";
                        }
                    }
                    createdQuery = createdQuery.replace(childCondition.getKey(), content);
                }
            }

            if (createdQuery == null) {
                createdQuery = "";
            }

            // foreach 반복
            //			if ("foreach".equals(getType())) {
            //				int matchesInParameters = 0;
            //				for (String parameter : parameterList) {
            //					if (parameter.startsWith(getCollection() + ":")) {
            //						matchesInParameters++;
            //					}
            //				}
            //				if (matchesInParameters  == 0) {
            //					createdQuery = "";
            //				}
            //			}

            // set이나 where 하부에 선택된 조건 또는 statement가 없으면 사용하지 않음
            if (("set".equals(getType()) || "where".equals(getType()))
                    && (createdQuery.trim().length() > 0)) {
                if ("set".equals(getType())) {
                    createdQuery = trimLast(createdQuery, ",");
                    createdQuery = "SET " + createdQuery;
                } else if ("where".equals(getType())) {
                    createdQuery = trimFirst(createdQuery, "AND|OR");
                    createdQuery = "WHERE " + createdQuery;
                }
            }

            if ("trim".equals(getType())) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(getSuffixOverrides())) {
                    createdQuery = trimLast(createdQuery, getSuffixOverrides());
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(getPrefixOverrides())) {
                    createdQuery = trimFirst(createdQuery, getPrefixOverrides());
                }
                if (org.apache.commons.lang.StringUtils.isNotBlank(getPrefix())
                        && org.apache.commons.lang.StringUtils.trim(createdQuery).length() > 0) {
                    createdQuery = getPrefix() + " " + createdQuery;
                }
            }

            return createdQuery;
        } else {
            return "";
        }
    }

    /**
     * 문장 마지막에 trimStringWithPipe로 지정된 문자열 제거
     *
     * @param string
     * @param trimStringWithPipe |로 구분된 문자열
     * @return
     */
    private String trimLast(String string, String trimStringWithPipe) {
        if (string == null) {
            return "";
        }
        String replace =
                trimStringWithPipe.replaceAll(
                        "\\.", "\\\\."); // TODO 정규표현식 안전한 문자열로 변환 필요, 현재는 .만 처리
        Pattern pattern =
                Pattern.compile(
                        ".*(" + replace + ")[ \\t\\r\\n]*",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            return string;
        }
        int sp = matcher.start(1);
        int ep = matcher.end(1);
        if (sp != -1 && ep != -1 && sp <= ep) {
            return string.substring(0, sp) + string.substring(ep + 1);
        }
        return string;
    }

    /**
     * 문장 처음에 trimStringWithPipe로 지정된 문자열 제거
     *
     * @param string
     * @param trimStringWithPipe |로 구분된 문자열
     * @return
     */
    private String trimFirst(String string, String trimStringWithPipe) {
        if (string == null) {
            return "";
        }
        String replace =
                trimStringWithPipe.replaceAll(
                        "\\.", "\\\\."); // TODO 정규표현식 안전한 문자열로 변환 필요, 현재는 .만 처리
        Pattern pattern =
                Pattern.compile(
                        "[ \\t\\r\\n]*(" + replace + ").*",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            return string;
        }
        int sp = matcher.start(1);
        int ep = matcher.end(1);
        if (sp != -1 && ep != -1 && sp <= ep) {
            return string.substring(0, sp) + string.substring(ep + 1);
        }
        return string;
    }

    /**
     * include 만 처리한 statement 를 반환한다.
     *
     * @return include 만 처리된 statement
     */
    public String createStatement() {
        StringBuilder query = new StringBuilder();

        query.append(getStringForStatement(this.open));
        if (type.equals("include")) {
            query.append(getStringForStatement(this.statement.trim()));
        } else {
            query.append(getStringForStatement(this.getIncludedStatement().trim()));
        }
        query.append(getStringForStatement(this.close));

        String createdQuery = query.toString();

        // 하위의 condition이 존재하는 경우
        if (childConditionList != null && childConditionList.size() > 0) {
            for (SqlMapCondition childCondition : childConditionList) {
                createdQuery =
                        createdQuery.replace(
                                childCondition.getKey(), childCondition.createStatement());
            }
        }

        return createdQuery;
    }

    private String getStringForStatement(String attribute) {
        if (StringUtils.isNotEmpty(attribute)) {
            return attribute;
        } else {
            return "";
        }
    }

    /**
     * 해당 condition 고유의 Key를 생성
     *
     * @return
     */
    private void createKey() {
        StringBuilder keyString = new StringBuilder();
        keyString.append("!!!!!");
        keyString.append(this.type).append(":");
        keyString.append(this.property).append(":");
        keyString.append(this.hashCode()).append(":");
        keyString.append("!!!!!");

        key = keyString.toString();
    }

    public String getKey() {
        if (key == null) {
            createKey();
        }
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getModifiedStatement() {
        return modifiedStatement;
    }

    public void setModifiedStatement(String modifiedStatement) {
        this.modifiedStatement = modifiedStatement;
    }

    public String getIncludedStatement() {
        if (this.includedStatement == null) {
            if (this.statement == null) {
                throw new NullPointerException("The statement field couldn't be a null.");
            }

            SAXReader reader = new SAXReader(false);
            Document document = null;

            try {
                InputSource source = new InputSource(new StringReader(this.statement));
                document = reader.read(source);
            } catch (DocumentException e) {
                ExceptionUtils.printRootCauseStackTrace(e);
                throw new IllegalArgumentException("fail to parse condition", e);
            }

            // include 처리용 statement 저장
            Element copiedElement = document.getRootElement().createCopy();
            copiedElement.clearContent();
            copiedElement.setText(this.modifiedStatement);
            this.includedStatement = copiedElement.asXML();
        }

        return includedStatement;
    }

    public void setIncludedStatement(String includedStatement) {
        this.includedStatement = includedStatement;
    }

    public List<SqlMapCondition> getChildConditionList() {
        return childConditionList;
    }

    public void setChildConditionList(List<SqlMapCondition> childConditionList) {
        this.childConditionList = childConditionList;
    }

    public String getProperty() {
        if (property == null) {
            return QUERY_DEFAULT_DYNAMIC_PARAMETER;
        }

        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPrepend() {
        return prepend;
    }

    public void setPrepend(String prepend) {
        this.prepend = prepend;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getCompareProperty() {
        return compareProperty;
    }

    public void setCompareProperty(String compareProperty) {
        this.compareProperty = compareProperty;
    }

    public String getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(String compareValue) {
        this.compareValue = compareValue;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getConjunction() {
        return conjunction;
    }

    public void setConjunction(String conjunction) {
        this.conjunction = conjunction;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public String getResultClass() {
        return resultClass;
    }

    public void setResultClass(String resultClass) {
        this.resultClass = resultClass;
    }

    /**
     * 현재 조건을 적용하여 예상되는 비교값을 반환한다.
     *
     * @return 비교값
     */
    public String getExpectedCompareValue() {
        return null;
    }

    /**
     * 비교 조건이 일치하는지 체크한다. 일치하는경우 statement 를 생성한다.
     *
     * @param parameterList 파라미터리스트
     * @return 일치여부
     */
    public boolean isMatchCondition(List<String> parameterList) {
        for (String parameter : parameterList) {
            if (parameter.startsWith(getProperty())) {
                return true;
            }
        }
        return false;
    }

    public List<MyBatisTestCondition> getMyBatisTestConditions() {
        return myBatisTestConditions;
    }

    public void setMyBatisTestConditions(List<MyBatisTestCondition> myBatisTestConditions) {
        this.myBatisTestConditions = myBatisTestConditions;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffixOverrides() {
        return suffixOverrides;
    }

    public void setSuffixOverrides(String suffixOverrides) {
        this.suffixOverrides = suffixOverrides;
    }

    public String getPrefixOverrides() {
        return prefixOverrides;
    }

    public void setPrefixOverrides(String prefixOverrides) {
        this.prefixOverrides = prefixOverrides;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static boolean isSetTag(Node node) {
        return "set".equals(node.getName());
    }
}
