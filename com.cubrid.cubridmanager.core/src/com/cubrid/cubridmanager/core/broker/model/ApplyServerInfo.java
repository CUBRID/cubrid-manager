/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.core.broker.model;

/**
 * A java bean which include all the information of the applied server
 * 
 * @author lizhiqiang
 * @version 1.0 - 2009-5-19 created by lizhiqiang
 */
public class ApplyServerInfo implements
		Cloneable {
	private String asId;
	private String asPid;
	private String asC; //reqest
	private String asPsize;
	private String asPort;
	private String asStatus;
	private String asCpu;
	private String asCtime;
	private String asLat;
	private String asCur; //execute sql statement
	private String asNumQuery;
	private String asNumTran;
	private String asLongQuery;
	private String asLongTran;
	private String asErrorQuery;
	private String asDbName;
	private String asDbHost;
	private String asLct;
	private String asClientIp;

	public String getAsId() {
		return asId;
	}

	public void setAsId(String asId) {
		this.asId = asId;
	}

	public String getAsPid() {
		return asPid;
	}

	public void setAsPid(String asPid) {
		this.asPid = asPid;
	}

	public String getAsC() {
		return asC;
	}

	public void setAsC(String asc) {
		this.asC = asc;
	}

	public String getAsPort() {
		return asPort;
	}

	public void setAsPort(String asPort) {
		this.asPort = asPort;
	}

	public String getAsPsize() {
		return asPsize;
	}

	public void setAsPsize(String asPsize) {
		this.asPsize = asPsize;
	}

	public String getAsStatus() {
		return asStatus;
	}

	public void setAsStatus(String asStatus) {
		this.asStatus = asStatus;
	}

	public String getAsCpu() {
		return asCpu;
	}

	public void setAsCpu(String asCpu) {
		this.asCpu = asCpu;
	}

	public String getAsCtime() {
		return asCtime;
	}

	public void setAsCtime(String asCtime) {
		this.asCtime = asCtime;
	}

	public String getAsLat() {
		return asLat;
	}

	public void setAsLat(String asLat) {
		this.asLat = asLat;
	}

	public String getAsCur() {
		return asCur;
	}

	public void setAsCur(String asCur) {
		this.asCur = asCur;
	}

	public String getAsNumQuery() {
		return asNumQuery;
	}

	public void setAsNumQuery(String asNumQuery) {
		this.asNumQuery = asNumQuery;
	}

	public String getAsNumTran() {
		return asNumTran;
	}

	public void setAsNumTran(String asNumTran) {
		this.asNumTran = asNumTran;
	}

	public String getAsLongQuery() {
		return asLongQuery;
	}

	public void setAsLongQuery(String asLongQuery) {
		this.asLongQuery = asLongQuery;
	}

	public String getAsLongTran() {
		return asLongTran;
	}

	public void setAsLongTran(String asLongTran) {
		this.asLongTran = asLongTran;
	}

	public String getAsErrorQuery() {
		return asErrorQuery;
	}

	public void setAsErrorQuery(String asErrorQuery) {
		this.asErrorQuery = asErrorQuery;
	}

	public String getAsDbName() {
		return asDbName;
	}

	public void setAsDbName(String asDbName) {
		this.asDbName = asDbName;
	}

	public String getAsDbHost() {
		return asDbHost;
	}

	public void setAsDbHost(String asDbHost) {
		this.asDbHost = asDbHost;
	}

	public String getAsLct() {
		return asLct;
	}

	public void setAsLct(String asLct) {
		this.asLct = asLct;
	}

	public String getAsClientIp() {
		return asClientIp;
	}

	public void setAsClientIp(String asClientIp) {
		this.asClientIp = asClientIp;
	}

	/**
	 * @return BrokerInfo
	 */
	public ApplyServerInfo clone() {
		try {
			return (ApplyServerInfo) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	/**
	 * Clear all the value in this instance.
	 * 
	 */
	public void clear() {
		asId = "";
		asPid = "";
		asC = "";
		asPsize = "";
		asPort = "";
		asStatus = "";
		asCpu = "";
		asCtime = "";
		asLat = "";
		asCur = "";
		asNumQuery = "";
		asNumTran = "";
		asLongQuery = "";
		asLongTran = "";
		asErrorQuery = "";
		asDbName = "";
		asDbHost = "";
		asLct = "";
		asClientIp = "";
	}
}
