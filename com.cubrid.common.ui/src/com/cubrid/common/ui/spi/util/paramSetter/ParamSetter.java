/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution. 
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met: 
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 *
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without 
 *   specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE. 
 *
 */
package com.cubrid.common.ui.spi.util.paramSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.dialog.PstmtParameter;
import com.cubrid.cubridmanager.core.cubrid.table.model.DataType;

/**
 * 
 * ParmaSetter - (Can't process NCHAR,NVHAR type)
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-17 created by Kevin.Wang
 */
public class ParamSetter implements
		IParamSetter {
	private static final Map<String, IParamSetter> handlerMap = new HashMap<String, IParamSetter>();
	private static final DefaultParamSetter defaultParamSetter = new DefaultParamSetter();

	public ParamSetter() {
		handlerMap.put(DataType.DATATYPE_CURRENCY, new NumericParamSetter());
		handlerMap.put(DataType.DATATYPE_INT, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_SHORT, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_CHAR, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_TINYINT, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_BIGINT, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_DECIMAL, new DoubleParamSetter());
		handlerMap.put(DataType.DATATYPE_NUMERIC, new NumericParamSetter());
		handlerMap.put(DataType.DATATYPE_REAL, new FloatParamSetter());
		handlerMap.put(DataType.DATATYPE_FLOAT, new FloatParamSetter());
		handlerMap.put(DataType.DATATYPE_DOUBLE, new DoubleParamSetter());
		handlerMap.put(DataType.DATATYPE_SMALLINT, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_INTEGER, defaultParamSetter);

		handlerMap.put(DataType.DATATYPE_STRING, new VarcharParamSetter());
		handlerMap.put(DataType.DATATYPE_VARCHAR, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_OID, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_MONETARY, new FloatParamSetter());
		handlerMap.put(DataType.DATATYPE_CHARACTER, new VarcharParamSetter());
		handlerMap.put(DataType.DATATYPE_CHARACTER_VARYING, new VarcharParamSetter());

		handlerMap.put(DataType.DATATYPE_DATETIME, new DateTimeSetter());
		handlerMap.put(DataType.DATATYPE_TIMESTAMP, new TimestampParamSetter());
		handlerMap.put(DataType.DATATYPE_TIME, new TimestampParamSetter());
		handlerMap.put(DataType.DATATYPE_DATE, new DateParamSetter());

		handlerMap.put(DataType.DATATYPE_SEQUENCE, new NumericParamSetter());
		handlerMap.put(DataType.DATATYPE_MULTISET, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_SET, defaultParamSetter);
		handlerMap.put(DataType.DATATYPE_ENUM, new EnumParamSetter());

		handlerMap.put(DataType.DATATYPE_CLOB, new ClobSetter());
		handlerMap.put(DataType.DATATYPE_BLOB, new BlobParamSetter());

		handlerMap.put(DataType.DATATYPE_BIT_VARYING, new BitParamSetter());
		handlerMap.put(DataType.DATATYPE_BIT, new BitParamSetter());

	}

	public void handle(PreparedStatement stmt, PstmtParameter parameter) throws SQLException,
			ParamSetException {
		IParamSetter setter = getParamSetter(parameter);
		if (setter == null) {
			throw new ParamSetException("Can't find the parameter set for the data type:"
					+ parameter.getDataType());
		}
		setter.handle(stmt, parameter);
	}

	public void handle(PreparedStatement stmt, PstmtParameter parameter, boolean isImprotCLob,
			boolean isImprotBlob) throws SQLException, ParamSetException {
		IParamSetter setter = getParamSetter(parameter);
		if (setter == null) {
			throw new ParamSetException("Can't find the parameter set for the data type:"
					+ parameter.getDataType());
		}

		if (setter instanceof ClobSetter) {
			if (isImprotCLob) {
				setter.handle(stmt, parameter);
			} else {
				setter.setNull(stmt, parameter.getParamIndex());
			}
			return;
		}

		if (setter instanceof BlobParamSetter) {
			if (isImprotBlob) {
				setter.handle(stmt, parameter);
			} else {
				setter.setNull(stmt, parameter.getParamIndex());
			}
			return;
		}

		setter.handle(stmt, parameter);
	}

	public void setNull(PreparedStatement stmt, int idx) throws SQLException {
		defaultParamSetter.setNull(stmt, idx);
	}

	private IParamSetter getParamSetter(PstmtParameter parameter) {
		if (parameter == null) {
			return null;
		}

		String dataType = parameter.getDataType();
		if (StringUtil.isEmpty(dataType)) {
			return null;
		}

		String key = DataType.getTypePart(dataType).toUpperCase();

		return handlerMap.get(key);
	}
}
