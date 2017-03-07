package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import java.util.ArrayList;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList.FreeTotalSizeSpacename;

public class DbSpaceInfoListOld extends DbSpaceInfoList{
	private static final Logger LOGGER = LogUtil.getLogger(DbSpaceInfoListNew.class);
	
	public int getTotalSize(){
		int totalSize = 0;
		for (DbSpaceInfoList.DbSpaceInfo bean : spaceinfo) {
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
		for (DbSpaceInfoList.DbSpaceInfo bean : spaceinfo) {
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
	
	public ArrayList<FreeTotalSizeSpacename> getVolumesInfoByType(String type){
		ArrayList<FreeTotalSizeSpacename> info = new ArrayList<FreeTotalSizeSpacename>();
		
		for (DbSpaceInfoList.DbSpaceInfo bean : spaceinfo) {
			if (bean.getType().equals(type)) {
				info.add(new FreeTotalSizeSpacename(bean.getSpacename(), bean.getFreepage(), bean.getTotalpage()));
			}
		}
		return info;
	}
}
