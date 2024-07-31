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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A metadata of the query.
 *
 * @author Bumsik, Jang
 */
public class SqlMapQuery implements Serializable {
    private static final long serialVersionUID = -7964227736391866162L;
    private static final String QUERY_DEFAULT_DYNAMIC_PARAMETER = "dynamic_param";

    public static final String[] QUERY_TYPE = {
        "select", "insert", "update", "delete", "sql", "procedure", "statement"
    };
    public static final String QUERY_TYPE_SELECT = "select";
    public static final String QUERY_TYPE_INSERT = "insert";
    public static final String QUERY_TYPE_UPDATE = "update";
    public static final String QUERY_TYPE_DELETE = "delete";
    public static final String QUERY_TYPE_SQL = "sql";

    private String type;
    private String id;
    private String parameterClass;
    // 원본 쿼리
    private String query;
    // 가공된 쿼리
    private String modifiedQuery;
    private String comment;

    /** 쿼리 비교 결과 태그(비교시 설정됨) : A:Added, M:Modified, R:Removed */
    private String diffTag;

    /** 쿼리 변경전 쿼리(비교시 설정됨) : diffTag 가 M(Modified) 인 경우 설정됨 */
    private String beforeQuery;

    /** include 만 처리된 쿼리(비교시 설정됨) */
    private String includedQuery;

    private List<SqlMapParameter> parameterList = new ArrayList<SqlMapParameter>();
    private List<SqlMapCondition> conditionList = new ArrayList<SqlMapCondition>();

    /** 현재 쿼리에서 비교값으로 사용될 parameter:value's 매핑 정보 */
    private Map<String, List<String>> dynamicCompareValueMap = null;

    /**
     * 수행될 query를 생성하여 리턴한다.
     *
     * @param parameterList 쿼리 수행시 전달될 파라미터 목록. ex) 'param:value'
     * @return 파라미터가 적용된 쿼리
     */
    public String createQuery(List<String> parameterList) {
        String query = modifiedQuery;
        for (SqlMapCondition condition : this.conditionList) {
            query = query.replace(condition.getKey(), condition.createStatement(parameterList));
        }

        return query;
    }

    /**
     * include 만 처리된 쿼리를 반환한다.
     *
     * @return include 만 처리된 쿼리
     */
    private String createQuery() {
        if (this.includedQuery != null) {
            return this.includedQuery;
        }

        String query = modifiedQuery;
        for (SqlMapCondition condition : this.conditionList) {
            query = query.replace(condition.getKey(), condition.createStatement());
        }

        this.includedQuery = query;

        return this.includedQuery;
    }

    /**
     * dynamic query 여부
     *
     * @return dynamic query 여부
     */
    public boolean isDynamicQuery() {
        for (SqlMapParameter parameter : getParameterList()) {
            if (parameter.isDynamic()) {
                return true;
            }
        }

        return false;
    }

    public List<SqlMapCondition> getIncludeConditionList() {
        return getConditionList("include");
    }

    public List<SqlMapCondition> getConditionList(String type) {
        List<SqlMapCondition> sqlMapConditionList = new ArrayList<SqlMapCondition>();
        for (SqlMapCondition condition : this.conditionList) {
            if (type.equalsIgnoreCase(condition.getType())) {
                sqlMapConditionList.add(condition);
            }
        }

        if (sqlMapConditionList.size() == 0) {
            return null;
        } else {
            return sqlMapConditionList;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(String parameterClass) {
        this.parameterClass = parameterClass;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getModifiedQuery() {
        return modifiedQuery;
    }

    public void setModifiedQuery(String modifiedQuery) {
        this.modifiedQuery = modifiedQuery;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDiffTag() {
        return diffTag;
    }

    public void setDiffTag(String diffTag) {
        this.diffTag = diffTag;
    }

    public String getBeforeQuery() {
        return beforeQuery;
    }

    public void setBeforeQuery(String beforeQuery) {
        this.beforeQuery = beforeQuery;
    }

    public String getIncludedQuery() {
        return includedQuery == null ? this.createQuery() : includedQuery;
    }

    public void setIncludedQuery(String includedQuery) {
        this.includedQuery = includedQuery;
    }

    public List<SqlMapParameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<SqlMapParameter> parameterList) {
        this.parameterList = parameterList;
    }

    public List<SqlMapCondition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<SqlMapCondition> conditionList) {
        this.conditionList = conditionList;
    }

    public void addParameter(SqlMapParameter newParameter) {
        // 기존에 등록된 파라미터인지 확인하여 존재하는 경우 isDynamic 정보만 업데이트
        for (SqlMapParameter parameter : getParameterList()) {
            if (parameter.getName().equals(newParameter.getName())) {
                boolean isDynamic = (parameter.isDynamic() || newParameter.isDynamic());
                parameter.setDynamic(isDynamic);

                return;
            }
        }

        // 기존에 등록된 파라미터가 아닌 경우 신규 추가
        getParameterList().add(newParameter);
    }

    public boolean isPrimitiveTypeParameter() {
        if (StringUtils.isNotEmpty(parameterClass)
                && (parameterClass.indexOf("java.lang.") >= 0
                        || "string".equalsIgnoreCase(parameterClass)
                        || "int".equalsIgnoreCase(parameterClass)
                        || "integer".equalsIgnoreCase(parameterClass))) {
            return true;
        }

        return false;
    }

    public void addDefaultDynamicParameter() {
        SqlMapParameter parameter = new SqlMapParameter();
        parameter.setDynamic(true);
        parameter.setName(QUERY_DEFAULT_DYNAMIC_PARAMETER);
        getParameterList().add(parameter);
    }

    /**
     * 현재 쿼리에서 비교값으로 사용될 parameter:value's 매핑 정보를 반환한다.
     *
     * @return <parameter, List<value>> 매핑정보
     */
    public Map<String, List<String>> getDynamicCompareValueMap() {
        if (this.dynamicCompareValueMap != null) {
            return this.dynamicCompareValueMap;
        }

        for (SqlMapParameter sqlMapParameter : this.getParameterList()) {
            if (sqlMapParameter.isDynamic()) {
                this.initExpectedCompareValueMap(
                        sqlMapParameter.getName(), this.getConditionList());
            }
        }

        return this.dynamicCompareValueMap;
    }

    /**
     * [Recursive] 주어진 파라미터를 property로 가지는 조건의 예상되는 비교값를 찾아 파라미터에 매핑한다.
     *
     * @param parameter 파라미터
     * @param sqlMapConditions SqlMapCondition 리스트
     */
    private void initExpectedCompareValueMap(
            String parameter, List<SqlMapCondition> sqlMapConditions) {
        if (this.dynamicCompareValueMap == null) {
            this.dynamicCompareValueMap = new HashMap<String, List<String>>();
        }

        List<String> valueList = null;
        for (SqlMapCondition sqlMapCondition : sqlMapConditions) {
            if (parameter.equals(sqlMapCondition.getProperty())) {
                valueList = this.dynamicCompareValueMap.get(parameter);
                if (valueList == null) {
                    valueList = new ArrayList<String>();
                }

                if (sqlMapCondition.getExpectedCompareValue() != null
                        && !valueList.contains(sqlMapCondition.getExpectedCompareValue())) {
                    valueList.add(sqlMapCondition.getExpectedCompareValue());
                    this.dynamicCompareValueMap.put(parameter, valueList);
                }
            }

            // MyBatis의 test 속성에 지정된 조건을 쿼리 선택 화면 변수 조합에 사용하도록 추가
            List<MyBatisTestCondition> myBatisConditionList =
                    sqlMapCondition.getMyBatisTestConditions();
            if (myBatisConditionList != null) {
                for (MyBatisTestCondition cond : myBatisConditionList) {
                    if (!parameter.startsWith(cond.getProperty())) {
                        continue;
                    }
                    valueList = this.dynamicCompareValueMap.get(parameter);
                    if (valueList == null) {
                        valueList = new ArrayList<String>();
                    }
                    if (cond.getOperator() != null && !valueList.contains(cond.getValue())) {
                        valueList.add(cond.getValue());
                        this.dynamicCompareValueMap.put(parameter, valueList);
                    }
                }
            }

            // recursion
            this.initExpectedCompareValueMap(parameter, sqlMapCondition.getChildConditionList());
        }
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
