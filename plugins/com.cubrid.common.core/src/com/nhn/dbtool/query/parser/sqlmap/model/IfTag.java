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
 * A definition of MyBatis if tag.
 *
 * @author CHOE JUNGYEON
 */
public class IfTag extends SqlMapCondition {
    private static final long serialVersionUID = 5071864812217053951L;

    public IfTag() {
        this.setType("if");
    }

    /**
     * 비교 조건이 일치하는지 체크한다. 일치하는경우 statement 를 생성한다.
     *
     * @param parameterList 파라미터리스트
     * @return 일치여부
     */
    @Override
    public boolean isMatchCondition(List<String> parameterList) {
        List<MyBatisTestCondition> condList = getMyBatisTestConditions();
        if (condList == null) {
            return false;
        }

        boolean testAllLoops = false;
        for (MyBatisTestCondition cond : condList) {
            String property = cond.getProperty();
            String value = cond.getValue();
            String operator = cond.getOperator();
            String concatenation = cond.getConcatenation();
            boolean testThisLoop = false;
            String param = findSelectedParameter(parameterList, property);

            // 단항 연산자(사용자 정의 함수)
            if (operator == null) {
                if (param != null) {
                    testThisLoop = "true".equals(value); // TODO T/F값을 어떻게 할것인가?
                } else {
                    testThisLoop = "false".equals(value); // TODO T/F값을 어떻게 할것인가?
                }
            } else {
                if (param != null && operator != null && canEvaluate(value)) {
                    String[] keyValueParam = param.split("\\:", 2);
                    try {
                        if (evaluate(operator, keyValueParam[1], value)) {
                            testThisLoop = true;
                        }
                    } catch (Exception ignored) {
                        testThisLoop = false;
                    }
                }
            }

            if ("and".equalsIgnoreCase(concatenation)) {
                testAllLoops &= testThisLoop;
            } else {
                testAllLoops |= testThisLoop;
            }
        }

        return testAllLoops;
    }

    private boolean evaluate(String operator, String userValue, String conditionValue) {
        conditionValue = conditionValue.replaceAll(operator, "");
        if ((operator.equals("!=") || operator.equals("<>")) && conditionValue.equals("null")) {
            return true;
        } else if (operator.equals("==") && conditionValue.equals("null")) {
            return false;
        } else if (operator.equals(">=")) {
            return Double.parseDouble(userValue) >= Double.parseDouble(conditionValue);
        } else if (operator.equals(">")) {
            return Double.parseDouble(userValue) > Double.parseDouble(conditionValue);
        } else if (operator.equals("<=")) {
            return Double.parseDouble(userValue) <= Double.parseDouble(conditionValue);
        } else if (operator.equals("<")) {
            return Double.parseDouble(userValue) < Double.parseDouble(conditionValue);
        }
        return false;
    }

    private String findSelectedParameter(List<String> parameterList, String propertyName) {
        for (String parameter : parameterList) {
            if (parameter.startsWith(propertyName + ":")) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * @param conditionOfDefinition it might be as same as "!=null".
     * @return
     */
    private boolean canEvaluate(String conditionOfDefinition) {
        if (conditionOfDefinition == null) {
            return false;
        }
        if (conditionOfDefinition.startsWith("!=")
                || conditionOfDefinition.startsWith("==")
                || conditionOfDefinition.startsWith(">=")
                || conditionOfDefinition.startsWith("<=")
                || conditionOfDefinition.startsWith(">")
                || conditionOfDefinition.startsWith("<")) {
            return true;
        }
        return false;
    }
}
