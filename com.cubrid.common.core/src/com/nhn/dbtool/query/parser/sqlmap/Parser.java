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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.cubrid.common.core.Messages;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapCondition;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapFile;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapParameter;
import com.nhn.dbtool.query.parser.sqlmap.model.SqlMapQuery;

/**
 * sqlmap 파일 내용을 통해 query 단위로 Parsing
 * (SqlMapFile.getSqlMapQueryList()가 생성된다)
 *
 * @author Bumsik, Jang
 */
public class Parser {
	private Logger logger = Logger.getLogger(Parser.class);

	/**
	 * 멀티파일 include 처리위해 sql 엘리먼트 타입만 저장하는 참조용 SqlMapQuery 해쉬맵 인스턴스
	 */
	private HashMap<String, SqlMapQuery> refSqlHashMap = null;

	/**
	 * 복수개의 SqlMapFile 을 파싱한다.(멀티파일 include 처리)
	 *
	 * @param sqlMapFileList SqlMapFile 목록
	 * @throws Exception 예외
	 */
	public void parse(List<SqlMapFile> sqlMapFileList) throws Exception {
		refSqlHashMap = new HashMap<String, SqlMapQuery>();

		// 멀티파일 include 처리위해 sql 엘리먼트 타입만 우선 파싱한다.
		for (SqlMapFile sqlMapFile : sqlMapFileList) {
			parseSqlNode((SqlMapFile)BeanUtils.cloneBean(sqlMapFile));
		}

		// SqlMapFile 파싱
		for (SqlMapFile sqlMapFile : sqlMapFileList) {
			parse(sqlMapFile);
		}
	}

	/**
	 * 멀티파일 include 처리위해 sql 엘리먼트 타입만 우선 파싱한다.
	 *
	 * @param sqlMapFile SqlMapFile
	 * @throws Exception 예외
	 */
	private void parseSqlNode(SqlMapFile sqlMapFile) throws Exception {
		Document document;

		if (StringUtils.isEmpty(sqlMapFile.getFileContent())) {
			String message = "The sqlmap file object has empty content.";
			sqlMapFile.setErrorMessage(message);
			throw new Exception(message);
		}

		// sqlmap 파일의 내용으로 XML Document를 생성한다.
		try {
			document = createDocument(sqlMapFile.getFileContent());
		} catch (Exception e) {
			String message = "The sqlmap file has a content of an invalid format.";
			sqlMapFile.setErrorMessage(e.getMessage());
			throw new Exception(message, e);
		}

		// iBatis(sqlMap) 또는 MyBatis(mapper) 파일인지 확인
		if (!isMapperXML(document)) {
			String message = "The sqlmap file doesn't supported. It can be parsed particular format as ibatis, mybatis.";
			sqlMapFile.setErrorMessage(message);
			throw new Exception(message);
		}

		// namespace 생성
		String namespace = SqlMapParserUtil.getAttribute(document.getRootElement(), "namespace");
		sqlMapFile.setNamespace(namespace);
		logger.debug("namespace:" + namespace);

		// 개별 쿼리를 파싱
		try {
			loopSqlNode(document, sqlMapFile);
		} catch (Exception e) {
			String message = "Failed to parse a sqlmap file.";
			sqlMapFile.setErrorMessage(message);
			throw new Exception(message, e);
		}
	}

	/**
	 * 멀티파일 include 처리위해 sql 엘리먼트 타입만 우선 파싱하여 SqlMapQuery 해쉬맵에 저장한다.
	 *
	 * @param document XML Document
	 * @param sqlMapFile SqlMapFile
	 * @throws Exception 예외
	 */
	private void loopSqlNode(Document document, SqlMapFile sqlMapFile) throws Exception {
		String currentComment = "";
		SqlMapQueryParser queryParser = new SqlMapQueryParser();

		// rootElement 하위의 모든 Node를 읽어온다
		// Node 타입이 COMMENT, ELEMENT인 경우에 따라 정보 수집
		Iterator<?> nodeIterator = document.getRootElement().nodeIterator();
		while (nodeIterator.hasNext()) {
			Node node = (Node)nodeIterator.next();
			switch (node.getNodeType()) {
				case Node.COMMENT_NODE:
					currentComment = node.getText();
					break;
				case Node.ELEMENT_NODE:
					// 멀티파일 include 처리위해 sql 엘리먼트이면 SqlMapQuery 해쉬맵에 추가
					if ("sql".equals(node.getName())) {
						//Element 타입의 node에서 Query 정보를 읽어 SqlMapQuery를 생성한다.
						SqlMapQuery query = queryParser.parse((Element)node);

						// ELEMENT_NODE 직전에 읽어들인 Comment를 본 쿼리의 comment로 간주하여 설정한다.
						query.setComment(currentComment.replaceAll("\t", ""));

						// SqlMapQuery 해쉬맵에 추가
						if (refSqlHashMap != null && "sql".equals(query.getType())) {
							refSqlHashMap.put(sqlMapFile.getNamespace() + "."
									+ query.getId().replace(sqlMapFile.getNamespace() + ".", ""), query);
						}
					}

					currentComment = "";
					break;
				default:
					currentComment = "";
					break;
			}
		}

	}

	public void parse(SqlMapFile sqlMapFile) throws Exception {
		Document document;

		if (StringUtils.isEmpty(sqlMapFile.getFileContent())) {
			sqlMapFile.setErrorMessage(Messages.sqlmapEmptyContent);
			throw new Exception(Messages.sqlmapEmptyContent);
		}

		// create a XML document with SQLMap document.
		try {
			document = createDocument(sqlMapFile.getFileContent());
		} catch (Exception e) {
			sqlMapFile.setErrorMessage(Messages.sqlmapInvalidFormat);
			throw new Exception(Messages.sqlmapInvalidFormat, e);
		}

		// determine whether iBatis(sqlMap) or MyBatis(mapper) from the document header.
		if (!isMapperXML(document)) {
			sqlMapFile.setErrorMessage(Messages.sqlmapNoMybatisFormat);
			throw new Exception(Messages.sqlmapNoMybatisFormat);
		}

		// generate a namespace
		String namespace = SqlMapParserUtil.getAttribute(document.getRootElement(), "namespace");
		sqlMapFile.setNamespace(namespace);
		logger.debug("namespace:" + namespace);

		// parse sqlmap document
		try {
			loopNode(document, sqlMapFile);
		} catch (Exception e) {
			sqlMapFile.setErrorMessage(Messages.sqlmapNoMybatisFormat);
			throw new Exception(Messages.sqlmapNoMybatisFormat, e);
		}

		// replace <include refid=""></include> by referred sql through the refid
		for (SqlMapQuery query : sqlMapFile.getSqlMapQueryList()) {
			// find Include condition in queries
			for (SqlMapCondition condition : query.getConditionList()) {
				mergeInclude(sqlMapFile, query, condition);
			}
		}
	}

	/**
	 * XML Document에서 Node 단위로 반복하여 Query를 추출하여 SqlMapFile에 담는다.
	 *
	 * @param document XML Document
	 * @param sqlMapFile
	 * @throws Exception
	 */
	private void loopNode(Document document, SqlMapFile sqlMapFile) throws Exception {
		String currentComment = "";
		SqlMapQueryParser queryParser = new SqlMapQueryParser();

		// rootElement 하위의 모든 Node를 읽어온다
		// Node 타입이 COMMENT, ELEMENT인 경우에 따라 정보 수집
		Iterator<?> nodeIterator = document.getRootElement().nodeIterator();
		while (nodeIterator.hasNext()) {
			Node node = (Node)nodeIterator.next();
			switch (node.getNodeType()) {
				case Node.COMMENT_NODE:
					currentComment = node.getText();
					break;

				case Node.ELEMENT_NODE:
					// parsing 해야 할 element인지 확인
					if (SqlMapParserUtil.isParsingTarget(node.getName())) {
						//Element 타입의 node에서 Query 정보를 읽어 SqlMapQuery를 생성한다.
						SqlMapQuery query = queryParser.parse((Element)node);
						// ELEMENT_NODE 직전에 읽어들인 Comment를 본 쿼리의 comment로 간주하여 설정한다.
						query.setComment(currentComment.replaceAll("\t", ""));
						sqlMapFile.getSqlMapQueryList().add(query);
					}
					currentComment = "";
					break;

				case Node.TEXT_NODE:
					break;

				default:
					currentComment = "";
					break;
			}
		}

	}

	/**
	 * 하위의 모든 SqlMapCondition 를 대상으로 Include Id에 해당 하는 Query의 파싱된 정보를 원본 Query 및 Condition 에 추가한다.
	 *
	 * @param sqlMapFile SqlMapFile
	 * @param query SqlMapQuery
	 * @param condition SqlMapCondition 리스트
	 */
	private void mergeInclude(SqlMapFile sqlMapFile, SqlMapQuery query, SqlMapCondition condition) {
		// child condition 을 우선 처리
		for (SqlMapCondition childCondition : condition.getChildConditionList()) {
			// 재귀 호출
			this.mergeInclude(sqlMapFile, query, childCondition);
		}

		// include 타입인 경우만 처리
		if ("include".equals(condition.getType())) {
			// refId attribute에서 namespace 부분 제거
			String referenceId = condition.getRefid();
			referenceId = referenceId.replace(sqlMapFile.getNamespace() + ".", "");
			logger.debug("include id:" + referenceId);

			// Include Id에 해당 하는 Query 가져오기
			SqlMapQuery includeQuery = sqlMapFile.getQuery(referenceId);
			if (includeQuery != null) {
				// Include Id에 해당 하는 Query의 파싱된 정보를 원본 Query 및 Condition 에 추가
				this.mergeInclude(query, condition, includeQuery);
			} else if (refSqlHashMap != null) {
				// Include Id 에 해당하는 Query 가 없는 경우 SqlMapQuery 해쉬맵에서 찾음
				if (referenceId.contains(".")) {
					includeQuery = refSqlHashMap.get(referenceId);
				} else {
					includeQuery = refSqlHashMap.get(sqlMapFile.getNamespace() + "." + referenceId);
				}

				if (includeQuery != null) {
					mergeInclude(query, condition, includeQuery);
				}
			}
		}
	}

	/**
	 * Include Id에 해당 하는 Query의 파싱된 정보를 원본 Query 및 Condition 에 추가
	 *
	 * @param query 원본 쿼리
	 * @param condition 원본 Include Condition
	 * @param includeQuery Include condition에 해당하는  Query
	 */
	private void mergeInclude(SqlMapQuery query, SqlMapCondition condition, SqlMapQuery includeQuery) {
		// Query 정보 추가
		condition.setStatement(includeQuery.getQuery());
		condition.setModifiedStatement(includeQuery.getModifiedQuery());

		// includeQuery의 condition 목록을 원본 Condition의 childCondition으로 추가
		// condition를 그대로 추가하지 않고 Object의 hascode를 포함한 key값을 다시 생성하여야 한다.
		List<SqlMapCondition> includeConditionList = new ArrayList<SqlMapCondition>();
		for (SqlMapCondition includeCondition : includeQuery.getConditionList()) {
			SqlMapCondition copiedCondition = includeCondition;
			copiedCondition.setKey(null);

			// 기존 쿼리에서 key값을 통한 해당 condition의 statement가 위치 자리의 key값을 새롭게 생성된 key값으로 변경한다.
			condition.setModifiedStatement(condition.getModifiedStatement().replaceAll(includeCondition.getKey(), copiedCondition.getKey()));
			includeConditionList.add(copiedCondition);
		}
		condition.setChildConditionList(includeQuery.getConditionList());

		// Parameter 추가
		for (SqlMapParameter includeParameter : includeQuery.getParameterList()) {
			query.addParameter(includeParameter);
		}
	}

	/**
	 * sqlmap 파일의 내용으로 XML Document를 생성한다.
	 *
	 * @param fileContent sqlmap 파일의 내용
	 * @return XML Document
	 */
	private Document createDocument(String fileContent) throws Exception {
		SAXReader reader = new SAXReader(false);
		Document document = null;

		try {
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			fileContent = SqlMapParserUtil.replaceInvalidDtdUrl(fileContent);
			InputSource source = new InputSource(new StringReader(fileContent));
			document = reader.read(source);
		} catch (DocumentException e) {
			ExceptionUtils.printRootCauseStackTrace(e);
			throw new IllegalArgumentException("fail to parse source sqlMap file", e);
		}

		return document;
	}

	/**
	 * iBatis(sqlMap) 또는 MyBatis(mapper) 파일인지 확인
	 *
	 * @param document
	 * @return
	 */
	private boolean isMapperXML(Document document) {
		boolean isSqlMap  = document.getRootElement().getName().equals("sqlMap");
		boolean isMyBatis = document.getRootElement().getName().equals("mapper");
		return isSqlMap || isMyBatis;
	}
}
