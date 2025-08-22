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
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * A metadata of the sqlmap file.
 *
 * @author Bumsik, Jang
 */
public class SqlMapFile implements Serializable {
    private static final long serialVersionUID = 6203438370527960105L;

    private String fileName;
    private String fileContent;
    private String fileEncoding;
    private int revisionNo;
    private String namespace;
    private String errorMessage;
    private String parsedXmlFileName;

    // Mapper가 iBatis인지 MyBatis인지 확인을 위해 사용
    public static final int FILE_TYPE_IBATIS = 0;
    public static final int FILE_TYPE_MYBATIS = 1;
    private int fileType = FILE_TYPE_IBATIS;

    // 쿼리 목록
    private List<SqlMapQuery> sqlMapQueryList = new ArrayList<SqlMapQuery>();

    public boolean isSuccess() {
        return !StringUtils.isNotEmpty(errorMessage);
    }

    /**
     * 수행될 query를 리턴한다.
     *
     * @param queryId Query Id
     * @param parameterList 쿼리 수행시 전달될 파라미터 목록. ex) 'param:value'
     * @return 파라미터가 적용된 쿼리
     */
    public String createQuery(String queryId, List<String> parameterList) {
        SqlMapQuery query = getQuery(queryId);
        if (query != null) {
            return query.createQuery(parameterList);
        }

        return null;
    }

    /**
     * 쿼리ID에 해당하는 include 만 처리된 쿼리를 반환한다.
     *
     * @param queryId 쿼리ID
     * @return include 만 처리된 쿼리
     */
    public String createQuery(String queryId) {
        SqlMapQuery query = getQuery(queryId);
        if (query != null) {
            return query.getIncludedQuery();
        }

        return null;
    }

    public SqlMapQuery getQuery(String queryId) {
        for (SqlMapQuery query : sqlMapQueryList) {
            if (queryId.equals(query.getId())) {
                return query;
            }
        }

        return null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileEncoding() {
        if (StringUtils.isEmpty(fileEncoding)) {
            return "UTF-8";
        }

        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public int getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(int revisionNo) {
        this.revisionNo = revisionNo;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getParsedXmlFileName() {
        return parsedXmlFileName;
    }

    public void setParsedXmlFileName(String parsedXmlFileName) {
        this.parsedXmlFileName = parsedXmlFileName;
    }

    public List<SqlMapQuery> getSqlMapQueryList() {
        return sqlMapQueryList;
    }

    public void setSqlMapQueryList(List<SqlMapQuery> sqlMapQueryList) {
        this.sqlMapQueryList = sqlMapQueryList;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
