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

import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * A SqlMapParser utility.
 *
 * @author Bumsik, Jang
 */
public class SqlMapParserUtil {

    /**
     * 주어진 Element에서 attribute를 읽는다.
     *
     * @param element XML Element
     * @param attributeName
     * @return attribute 값
     */
    public static String getAttribute(Element element, String attributeName) {
        return element.attributeValue(attributeName);
    }

    /**
     * 주어진 Element 타입 Node에서 attribute를 읽는다.
     *
     * @param node XML Node
     * @param attributeName
     * @return attribute 값
     */
    public static String getAttribute(Node node, String attributeName) {
        return getAttribute((Element) node, attributeName);
    }

    public static boolean hasChildNode(Node node) {
        int nodeCount = ((Element) node).nodeCount();
        return (nodeCount > 0);
    }

    /**
     * parsing 해야 할 element인지 확인
     *
     * @param elementName Element name
     * @return parsing 진행 여부
     */
    public static boolean isParsingTarget(String elementName) {
        for (String queryType : SqlMapQuery.QUERY_TYPE) {
            if (queryType.equalsIgnoreCase(elementName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * XML DTD 파일 URL이 과거 정보(http://www.ibatis.com/dtd/sql-map-2.dtd)를 사용한 경우의 올바르게 변환
     *
     * @param fileContent sqlmap 파일의 내용
     * @return DTD 파일 URL이 올바르게 변환된 sqlmap 파일의 내용
     */
    public static String replaceInvalidDtdUrl(String fileContent) {
        return fileContent.replaceAll("www.ibatis.com", "ibatis.apache.org");
    }

    /**
     * Element에서 하위 노드의 tag가 포함된 text를 읽기위해서는 node.asXML()를 사용하여야 하는데, 이때, Element 자체의 tag가 포함되어 있어
     * 순수한 text값을 읽어들이기 위해서 해당 tag를 제거한다.
     *
     * @param asXml node.asXML()를 사용하여 읽은 값
     * @param elementName Element name
     * @return
     */
    public static String removeElementTag(String asXml, String elementName) {

        // 시작 tag 제거
        String startTagExpression = "<[ ]*" + elementName + "([^>]*)(>)";
        Pattern pattern = Pattern.compile(startTagExpression);
        Matcher matcher = pattern.matcher(asXml);
        if (matcher.find()) {
            asXml = asXml.substring(matcher.end());
        }

        // 종료 tag 제거
        String endTagExpression = "</[ ]*" + elementName + "[^>]*(>)";
        asXml = asXml.replaceAll(endTagExpression, "");

        // CDATA tag 제거
        asXml = removeCDataTag(asXml);
        return asXml;
    }

    /**
     * CDATA tag 제거
     *
     * @param content
     * @return
     */
    public static String removeCDataTag(String content) {
        content = content.replaceAll("<!\\[CDATA\\[", "");
        content = content.replaceAll("\\]\\]>", "");
        return content;
    }

    /**
     * Element내의 text 내용에서 파라미터를 추출
     *
     * @param nodeText Element내의 text 내용
     * @return 추출된 파라미터 목록
     */
    public static List<String> parseParameter(String nodeText) {

        List<String> parameterList = new ArrayList<String>();
        // iBatis
        parameterList.addAll(parseParameter(nodeText, "#", "#"));
        parameterList.addAll(parseParameter(nodeText, "\\$", "\\$"));
        // MyBatis
        parameterList.addAll(parseParameter(nodeText, "#\\{", "\\}"));
        parameterList.addAll(parseParameter(nodeText, "\\$\\{", "\\}"));

        return parameterList;
    }

    /**
     * Prefix, suffix 문자를 이용해서 바인드 변수 파싱
     *
     * @param nodeText
     * @param prefix
     * @param suffix
     * @return
     */
    private static List<String> parseParameter(String nodeText, String prefix, String suffix) {

        List<String> parameterList = new ArrayList<String>();

        // #\\{([^#\\{\\}]*)(\\}) or #([^#\\{\\}]*)(#)
        String tagExpression = prefix + "([^" + prefix + "\\{\\}]*)(" + suffix + ")";
        Pattern pattern = Pattern.compile(tagExpression);
        Matcher matcher = pattern.matcher(nodeText);
        while (matcher.find()) {
            String parameter = matcher.group();
            parameter = parameter.replaceAll(prefix, "");
            parameter = parameter.replaceAll(suffix, "");
            parameter = parameter.replace("[]", "");
            parameterList.add(parameter);
        }

        return parameterList;
    }
}
