package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.common.model.IModel;

public class DbSpaceInfoListNew implements IModel{
	private static final Logger LOGGER = LogUtil.getLogger(DbSpaceInfoListNew.class);
	
	private String dbname = null;

	private int pagesize = 0;

	private int logpagesize = 0;

	private int freespace = 0;

	/*
	 * the volume space info list of the database
	 */
	private List<DbSpaceInfo> spaceinfo = null;
	private List<DatabaseDescription> dbinfo = null;
	private List<FileSpaceDescription> fileinfo = null;
	private List<VolumeInfo> volumeinfo = null;

	public String getTaskName() {
		return "dbspaceinfo";
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getLogpagesize() {
		return logpagesize;
	}

	public void setLogpagesize(int logpagesize) {
		this.logpagesize = logpagesize;
	}

	/***
	 * Get the list that encapsulates the instances of DbSpaceInfo
	 *
	 * @return List<DbSpaceInfo> the list that encapsulates the instances of
	 *         DbSpaceInfo
	 */
	public List<DbSpaceInfo> getSpaceinfo() {
		synchronized (this) {
			return spaceinfo;
		}
	}
	
	public List<DatabaseDescription> getDbinfo() {
		synchronized (this) {
			return dbinfo;
		}
	}
	
	public List<FileSpaceDescription> getFileinfo(){
		synchronized (this){
			return fileinfo;
		}
	}
	
	public List<VolumeInfo> getVolumeinfo(){
		synchronized (this){
			return volumeinfo;
		}
	}

	/**
	 * Get the map that encapsulates the instances of DbSpaceInfo
	 *
	 * @return Map<String, DbSpaceInfo> the map that encapsulates the instances
	 *         of DbSpaceInfo
	 */
	public Map<String, DbSpaceInfo> getSpaceInfoMap() {
		synchronized (this) {
			Map<String, DbSpaceInfo> map = new TreeMap<String, DbSpaceInfo>();
			if (spaceinfo == null) {
				return map;
			}
			for (DbSpaceInfo bean : spaceinfo) {
				String type = bean.getType().toUpperCase();
				if (map.containsKey(type)) {
					DbSpaceInfo model = map.get(type);
					int free = model.getFreepage();
					int totl = model.getTotalpage();
					model.setFreepage(free + bean.getFreepage());
					model.setTotalpage(totl + bean.getTotalpage());
					model.plusVolumeCount();
				} else {
					DbSpaceInfo model = new DbSpaceInfo();
					try {
						DbSpaceInfoList.copyBean2Bean(bean, model);
					} catch (IllegalAccessException e) {
						LOGGER.error("", e);
					} catch (InvocationTargetException e) {
						LOGGER.error("", e);
					}
					model.setSpacename("");
					model.plusVolumeCount();
					map.put(type, model);
				}
			}
			return map;
		}
	}

	/**
	 * Set a list that encapsulates the instances of DbSpaceInfo as a field of
	 * this instance
	 *
	 * @param spaceinfoList List<DbSpaceInfo> A list that encapsulates the
	 *        instances of DbSpaceInfo
	 */
	public void setSpaceinfo(List<DbSpaceInfo> spaceinfoList) {
		synchronized (this) {
			this.spaceinfo = spaceinfoList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addSpaceinfo(DbSpaceInfo info) {
		synchronized (this) {
			if (spaceinfo == null) {
				spaceinfo = new ArrayList<DbSpaceInfo>();
			}
			if (!spaceinfo.contains(info)) {
				spaceinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeSpaceinfo(DbSpaceInfo info) {
		synchronized (this) {
			if (spaceinfo != null) {
				spaceinfo.remove(info);
			}
		}
	}
	
	public void setDbinfo(List<DatabaseDescription> databaseDescriptionList) {
		synchronized (this) {
			this.dbinfo = databaseDescriptionList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addDbinfo(DatabaseDescription info) {
		synchronized (this) {
			if (dbinfo == null) {
				dbinfo = new ArrayList<DatabaseDescription>();
			}
			if (!dbinfo.contains(info)) {
				dbinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeDbinfo(DatabaseDescription info) {
		synchronized (this) {
			if (dbinfo != null) {
				dbinfo.remove(info);
			}
		}
	}
	
	public void setFileinfo(List<FileSpaceDescription> fileSpaceDescriptionList) {
		synchronized (this) {
			this.fileinfo = fileSpaceDescriptionList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addFileinfo(FileSpaceDescription info) {
		synchronized (this) {
			if (fileinfo == null) {
				fileinfo = new ArrayList<FileSpaceDescription>();
			}
			if (!fileinfo.contains(info)) {
				fileinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeFileinfo(FileSpaceDescription info) {
		synchronized (this) {
			if (fileinfo != null) {
				fileinfo.remove(info);
			}
		}
	}
	
	public void setVolumeinfo(List<VolumeInfo> volumeInfoList) {
		synchronized (this) {
			this.volumeinfo = volumeInfoList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addVolumeinfo(VolumeInfo info) {
		synchronized (this) {
			if (volumeinfo == null) {
				volumeinfo = new ArrayList<VolumeInfo>();
			}
			if (!volumeinfo.contains(info)) {
				volumeinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeVolumeinfo(VolumeInfo info) {
		synchronized (this) {
			if (volumeinfo != null) {
				volumeinfo.remove(info);
			}
		}
	}

	public int getFreespace() {
		return freespace;
	}

	public void setFreespace(int freespace) {
		this.freespace = freespace;
	}

	/**
	 * Reflect the src field value into dest object
	 *
	 * @param src Object source object
	 * @param dest Object destination object
	 * @throws IllegalAccessException illegal access exception
	 * @throws InvocationTargetException invocation target exception
	 */
	public static void copyBean2Bean(Object src, Object dest) throws IllegalAccessException,
			InvocationTargetException {
		try {
			Method[] srcMtds = src.getClass().getMethods();
			Method[] destMtds = src.getClass().getMethods();

			Field[] yy = dest.getClass().getDeclaredFields();
			String[] proname = new String[yy.length];
			for (int j = 0; j < yy.length; j++) {
				proname[j] = yy[j].getName();
			}

			for (int ff = 0; ff < yy.length; ff++) {
				// get the set-method from dest object and get-method from src object
				String fieldName = yy[ff].getName();
				Method srcGetMethod = null;
				Method destSetMethod = null;

				for (Method m : srcMtds) {
					if (m.getName().equalsIgnoreCase("get" + fieldName)) {
						srcGetMethod = m;
						break;
					}
				}

				for (Method m : destMtds) {
					if (m.getName().equalsIgnoreCase("set" + fieldName)) {
						destSetMethod = m;
						break;
					}
				}

				if (srcGetMethod == null || destSetMethod == null) {
					continue;
				}

				//get the value from the dest object
				Class<?>[] descParams = destSetMethod.getParameterTypes();
				Class<?>[] srcParams = srcGetMethod.getParameterTypes();

				if (srcParams.length != 0 || descParams.length != 1) {
					continue;
				}

				boolean flag = true;
				Object value = srcGetMethod.invoke(src);
				if (value == null) {
					flag = false;
				}
				if (flag && value != null && value.getClass() == Integer.class && descParams[0] == int.class) {
					flag = false;
				}
				if (flag && value != null && value.getClass() == Double.class && descParams[0] == double.class) {
					flag = false;
				}
				if (flag && value != null && descParams[0] != value.getClass()) {
					continue;
				}
				destSetMethod.invoke(dest, new Object[] { value });
			}
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		}
	}

}
