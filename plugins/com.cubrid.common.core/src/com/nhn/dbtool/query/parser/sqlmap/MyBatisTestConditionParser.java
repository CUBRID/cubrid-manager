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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A parser of MyBatis test condition.
 *
 * @author CHOE JUNGYEON
 */
public class MyBatisTestConditionParser {
    private static final int TYPE_PROPERTY = 0;
    private static final int TYPE_VALUE = 1;

    public List<MyBatisTestCondition> parse(String testString) {
        int type = TYPE_PROPERTY;
        boolean hasPreWhitespace = true;

        List<MyBatisTestCondition> list = new ArrayList<MyBatisTestCondition>();
        MyBatisTestCondition tc = new MyBatisTestCondition();
        list.add(tc);

        // There should be added additional white-spaces due to guarantee a safety at the end of
        // string.
        String test = StringEscapeUtils.unescapeHtml(testString) + "          ";
        StringBuilder buf = new StringBuilder();
        char ch = 0;

        for (int i = 0, len = test.length(); i < len; i++) {
            ch = test.charAt(i);

            boolean hasConcatenation =
                    (ch == '|' && test.charAt(i + 1) == '|') // ||
                            // &&
                            || (ch == '&' && test.charAt(i + 1) == '&')
                            // AaNnDd
                            || (hasPreWhitespace
                                    && (ch == 'a' || ch == 'A')
                                    && (test.charAt(i + 1) == 'n' || test.charAt(i + 1) == 'N')
                                    && (test.charAt(i + 2) == 'd' || test.charAt(i + 2) == 'D')
                                    && (test.charAt(i + 3) == ' ' || test.charAt(i + 3) == '\t'))
                            // OoRr
                            || (hasPreWhitespace
                                    && (ch == 'o' || ch == 'O')
                                    && (test.charAt(i + 1) == 'r' || test.charAt(i + 1) == 'R')
                                    && (test.charAt(i + 2) == ' ' || test.charAt(i + 2) == '\t'));

            if (hasConcatenation) {
                String concatenationName;
                switch (ch) {
                    case 'a':
                    case 'A':
                        i += 3;
                        concatenationName = "and";
                        break;
                    case '&':
                        i++;
                        concatenationName = "and";
                        break;
                    case 'o':
                    case 'O':
                        i += 2;
                        concatenationName = "or";
                        break;
                    case '|':
                        i++;
                        concatenationName = "or";
                        break;
                    default:
                        throw new RuntimeException("It was failed to parse a MyBatis XML file.");
                }

                if (type == TYPE_VALUE) {
                    // 이항연산에서 비교할 값
                    tc.setValue(buf.toString().trim());
                    tc = new MyBatisTestCondition();
                    tc.setConcatenation(concatenationName);
                    list.add(tc);
                } else if (type == TYPE_PROPERTY) {
                    // 단항연산
                    tc.setProperty(buf.toString().trim());
                    tc = new MyBatisTestCondition();
                    tc.setConcatenation(concatenationName);
                    list.add(tc);
                } else {
                    tc.setConcatenation(concatenationName);
                    tc.setProperty(buf.toString().trim());
                    tc.setOperator(null);
                    tc.setValue(null);
                }
                buf.delete(0, buf.length());
                type = TYPE_PROPERTY;

            } else if (ch == '=' && test.charAt(i + 1) == '='
                    || ch == '!' && test.charAt(i + 1) == '='
                    || ch == '>' && test.charAt(i + 1) == '='
                    || ch == '<' && test.charAt(i + 1) == '='
                    || ch == '<' && test.charAt(i + 1) == '>'
                    || ch == '>'
                    || ch == '<') {
                String operatorName = ch + "";
                if (test.charAt(i + 1) == '=' || test.charAt(i + 1) == '>') {
                    operatorName += test.charAt(i + 1) + "";
                    i += 1;
                }

                tc.setOperator(operatorName);
                tc.setProperty(buf.toString().trim());
                buf.delete(0, buf.length());
                type = TYPE_VALUE;

            } else {
                // Prepare the property or the value string
                hasPreWhitespace = ch == ' ' || ch == '\t';
                buf.append(ch);
                if (i >= len - 1) {
                    if (type == TYPE_PROPERTY) {
                        tc.setProperty(buf.toString().trim());
                    } else if (type == TYPE_VALUE) {
                        tc.setValue(buf.toString().trim());
                    }
                }
            }
        }

        // property로 단항 연산자(사용자 정의 함수)가 사용된 경우 value에 true/false 대입
        for (MyBatisTestCondition condition : list) {
            String property = condition.getProperty();
            if (StringUtils.isBlank(property)) {
                continue; // TODO 에러처리 필요?
            }

            if (condition.getOperator() != null && condition.getValue() != null) {
                condition.setValue(condition.getOperator() + condition.getValue());
            }

            if (condition.getValue() != null || condition.getOperator() != null) {
                continue;
            }

            if (property.charAt(0) == '!') {
                condition.setProperty(property.substring(1));
                condition.setValue("false");
            } else {
                condition.setValue("true");
            }
        }

        return list;
    }
}
