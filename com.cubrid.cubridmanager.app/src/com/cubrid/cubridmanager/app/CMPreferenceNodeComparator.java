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
package com.cubrid.cubridmanager.app;

import com.cubrid.common.ui.common.preference.GeneralPreferencePage;
import com.cubrid.common.ui.common.preference.JdbcManagePreferencePage;
import com.cubrid.common.ui.common.preference.NavigatorPreferencePage;
import com.cubrid.common.ui.cubrid.table.preference.ImportPreferencePage;
import com.cubrid.common.ui.query.preference.QueryOptionPreferencePage;
import com.cubrid.common.ui.spi.PreferenceNodeComparator;
import com.cubrid.cubridmanager.ui.mondashboard.preference.DashboardPreferencePage;

/**
 * 
 * CUBRID Manager preference node comparator
 * 
 * @author pangqiren
 * @version 1.0 - 2010-12-17 created by pangqiren
 */
public class CMPreferenceNodeComparator extends
		PreferenceNodeComparator {

	private static final long serialVersionUID = 3601999025389973303L;

	/**
	 * Compare the preference node id
	 * 
	 * @param id1 String
	 * @param id2 String
	 * 
	 * @return int
	 */
	protected int compareId(String id1, String id2) {
		if (GeneralPreferencePage.ID.equals(id1)) {
			return -1;
		} else if (GeneralPreferencePage.ID.equals(id2)) {
			return 1;
		}
		if (QueryOptionPreferencePage.ID.equals(id1)) {
			return -1;
		} else if (QueryOptionPreferencePage.ID.equals(id2)) {
			return 1;
		}
		if (ImportPreferencePage.ID.equals(id1)) {
			return -1;
		} else if (ImportPreferencePage.ID.equals(id2)) {
			return 1;
		}
		if (JdbcManagePreferencePage.ID.equals(id1)) {
			return -1;
		} else if (JdbcManagePreferencePage.ID.equals(id2)) {
			return 1;
		}
		if (DashboardPreferencePage.ID.equals(id1)) {
			return -1;
		} else if (DashboardPreferencePage.ID.equals(id2)) {
			return 1;
		}
		if (NavigatorPreferencePage.ID.equals(id1)) {
			return -1;
		} else if (NavigatorPreferencePage.ID.equals(id2)) {
			return 1;
		}
		return 0;
	}
}
