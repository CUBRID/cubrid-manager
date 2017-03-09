package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;

public class DbSpaceInfoListOld extends DbSpaceInfoList{
	private static final Logger LOGGER = LogUtil.getLogger(DbSpaceInfoListNew.class);
	
	public int getTotalSize(){
		int totalSize = 0;
		for (DbSpaceInfo bean : spaceinfo) {
			if (!bean.getType().equals("GENERIC")
					&& !bean.getType().equals("DATA")
					&& !bean.getType().equals("TEMP")
					&& !bean.getType().equals("INDEX")) {
				continue;
			}
			totalSize += bean.getTotalpage();
		}
		
		return totalSize;
	}

	public int getFreeSize(){
		int freeSize = 0;
		for (DbSpaceInfo bean : spaceinfo) {
			if (!bean.getType().equals("GENERIC")
					&& !bean.getType().equals("DATA")
					&& !bean.getType().equals("TEMP")
					&& !bean.getType().equals("INDEX")) {
				continue;
			}
			freeSize += bean.getFreepage();
		}
		
		return freeSize;
	}

}
