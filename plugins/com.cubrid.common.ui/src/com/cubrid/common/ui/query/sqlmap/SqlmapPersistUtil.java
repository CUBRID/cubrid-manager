/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.query.sqlmap;

import static com.cubrid.common.core.util.StringUtil.nvl;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.sqlmap.BindParameter.BindParameterType;
import com.navercorp.dbtools.sqlmap.parser.MapperFile;
import com.navercorp.dbtools.sqlmap.parser.QueryCondition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

/**
 * Persistence data utility of SQLMAPs runner.
 *
 * @author CHOE JUNGYEON
 */
public class SqlmapPersistUtil {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(SqlmapPersistUtil.class);

    private static SqlmapPersistUtil instance = null;

    /** All conditions list by separated using the query id. */
    private final Map<String, List<QueryCondition>> condValues =
            new ConcurrentHashMap<String, List<QueryCondition>>();

    /** Bind parameters by separated using the query id. */
    private final Map<String, Map<String, BindParameter>> paramValues =
            new ConcurrentHashMap<String, Map<String, BindParameter>>();

    /** Used conditions set which is separated by the query id. */
    private final Map<String, Set<String>> usedConditions =
            new ConcurrentHashMap<String, Set<String>>();

    private SqlmapPersistUtil() {}

    public static SqlmapPersistUtil getInstance() {
        if (instance == null) {
            instance = new SqlmapPersistUtil();
        }

        return instance;
    }

    public void setConditions(String queryId, List<QueryCondition> queryConditions) {
        condValues.put(queryId, queryConditions);

        Set<String> oldUsedConditions = usedConditions.get(queryId);
        Set<String> newUsedConditions = new HashSet<String>();
        for (QueryCondition condition : queryConditions) {
            String conditionValue =
                    condition.getConditionKey() + ":" + condition.getConditionBody();
            if (oldUsedConditions != null && oldUsedConditions.contains(conditionValue)) {
                newUsedConditions.add(conditionValue);
            }
        }
        usedConditions.put(queryId, newUsedConditions);
    }

    public List<QueryCondition> getConditions(String queryId) {
        return condValues.get(queryId);
    }

    public void toggleUsedCondition(String queryId, String condition) {
        boolean isUsed = isUsedCondition(queryId, condition);
        changeUsedCondition(queryId, condition, !isUsed);
    }

    public boolean isUsedCondition(String queryId, String condition) {
        if (condition == null) {
            return false;
        }

        Set<String> condSet = usedConditions.get(queryId);
        if (condSet == null) {
            return false;
        }

        return condSet.contains(condition);
    }

    public void changeUsedCondition(String queryId, String condition, boolean needToUse) {
        if (condition == null) {
            return;
        }

        List<QueryCondition> queryConditions = condValues.get(queryId);
        if (queryConditions == null) {
            return;
        }

        boolean found = false;
        for (QueryCondition queryCondition : queryConditions) {
            if (condition.equals(
                    queryCondition.getConditionKey() + ":" + queryCondition.getConditionBody())) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        Set<String> condSet = usedConditions.get(queryId);
        if (needToUse) {
            condSet.add(condition);
        } else {
            condSet.remove(condition);
        }
    }

    public boolean isChanged(String queryId, List<QueryCondition> queryConditions) {
        List<QueryCondition> oldConditions = condValues.get(queryId);
        if (oldConditions == null
                || queryConditions == null
                || oldConditions.size() != queryConditions.size()) {
            return true;
        }

        Set<String> currentConditionNameSet = new HashSet<String>();
        for (QueryCondition queryCondition : oldConditions) {
            String condition =
                    queryCondition.getConditionKey() + ":" + queryCondition.getConditionBody();
            currentConditionNameSet.add(condition);
        }

        for (int i = 0; i < queryConditions.size(); i++) {
            QueryCondition newCondition = queryConditions.get(i);
            String condition =
                    newCondition.getConditionKey() + ":" + newCondition.getConditionBody();
            if (!currentConditionNameSet.contains(condition)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getUsedConditionList(String queryId) {
        List<String> params = new ArrayList<String>();

        Set<String> condSet = usedConditions.get(queryId);
        if (condSet != null) {
            Iterator<String> iter = condSet.iterator();
            while (iter.hasNext()) {
                params.add(iter.next());
            }
        }

        return params;
    }

    public void addOrModifyBindParameter(String queryId, String name, String value, String type) {
        Map<String, BindParameter> map = paramValues.get(queryId);
        if (map == null) {
            map = new HashMap<String, BindParameter>();
            paramValues.put(queryId, map);
        }

        if (!map.containsKey(name)) {
            map.put(name, new BindParameter(name, nvl(value), BindParameterType.valueOf(type)));
        } else {
            BindParameter bindValue = map.get(name);
            if (value != null) {
                bindValue.setValue(nvl(value));
                bindValue.setType(BindParameterType.valueOf(type));
                map.put(name, bindValue);
            }
        }
    }

    public void removeBindParameter(String queryId, String name) {
        Map<String, BindParameter> map = paramValues.get(queryId);
        if (map == null) {
            return;
        }
        map.remove(name);
    }

    public Map<String, BindParameter> getBindParameters(String queryId) {
        Map<String, BindParameter> values = paramValues.get(queryId);
        if (values == null) {
            return Collections.emptyMap();
        }
        return new HashMap<String, BindParameter>(values);
    }

    public BindParameter getBindParameter(String queryId, String parameterName) {
        Map<String, BindParameter> paramValue = paramValues.get(queryId);
        if (paramValue == null) {
            return null;
        }
        return paramValue.get(parameterName);
    }

    public String generateQuery(MapperFile mapperFile, String queryId) {
        List<String> params = getUsedConditionList(queryId);
        String generatedQuery = mapperFile.generateQuery(queryId, params);
        return generatedQuery;
    }
}
