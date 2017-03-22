/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.spi.persist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferenceStore;
import org.slf4j.Logger;

import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;

/**
 *
 *
 * Group node persist manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-1 created by pangqiren
 */
public final class CMGroupNodePersistManager implements
		ICubridGroupNodeManager {
	private static final Logger LOGGER = LogUtil.getLogger(CMGroupNodePersistManager.class);

	public static final String COM_CUBRID_MANAGER_HOSTGROUP = "com.cubrid.manager.hostgroup";
	private List<CubridGroupNode> groupNodeList;
	private static CMGroupNodePersistManager instance;

	private CMGroupNodePersistManager() {
		init();
	}

	/**
	 * Return the only GroupNodePersistManager
	 *
	 * @return GroupNodePersistManager
	 */
	public static CMGroupNodePersistManager getInstance() {
		synchronized (CMGroupNodePersistManager.class) {
			if (instance == null) {
				instance = new CMGroupNodePersistManager();
			}
		}
		return instance;
	}

	/**
	 *
	 * Initial the persist manager
	 *
	 */
	protected void init() {
		synchronized (this) {
			groupNodeList = new ArrayList<CubridGroupNode>();
			loadGroupNode();
		}
	}

	/**
	 * Load group nodes from local preference.
	 *
	 */
	private void loadGroupNode() {
		synchronized (this) {
			IXMLMemento memento = PersistUtils.getXMLMemento(ApplicationUtil.CM_UI_PLUGIN_ID,
					COM_CUBRID_MANAGER_HOSTGROUP);
			loadGroupNode(memento);
		}
	}

	/**
	 * Load group nodes from file preference.
	 *
	 * @param workspacePath String
	 * @return boolean whether import
	 */
	public boolean loadGroupNode(String workspacePath) {
		synchronized (this) {
			String filePath = workspacePath + File.separator + ".metadata" + File.separator
					+ ".plugins" + File.separator + "org.eclipse.core.runtime" + File.separator
					+ ".settings" + File.separator + "com.cubrid.cubridmanager.ui.prefs";

			PreferenceStore preference = new PreferenceStore(filePath);
			int size = groupNodeList.size();
			try {
				preference.load();
				String xmlString = preference.getString(COM_CUBRID_MANAGER_HOSTGROUP);
				if (xmlString == null || xmlString.trim().length() == 0) {
					return false;
				}
				ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));

				IXMLMemento memento = XMLMemento.loadMemento(in);
				loadGroupNode(memento);
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			boolean isImported = size != groupNodeList.size();
			if (isImported) {
				saveAllGroupNode();
			}
			return isImported;
		}
	}

	/**
	 * Load group nodes from xml memento.
	 *
	 * @param memento IXMLMemento
	 *
	 */
	private void loadGroupNode(IXMLMemento memento) {
		IXMLMemento[] children = memento == null ? null : memento.getChildren("group");
		groupNodeList.clear();
		for (int i = 0; children != null && i < children.length; i++) {
			String id = children[i].getString("id");
			String name = children[i].getString("name");
			CubridGroupNode cgn = getGroupById(id);
			if (cgn == null) {
				cgn = new CubridGroupNode(id, name, DEFAULT_GROUP_NODE.getIconPath());
				groupNodeList.add(cgn);
			}
			cgn.setLoader(DEFAULT_GROUP_NODE.getLoader());
			IXMLMemento[] items = children[i].getChildren("item");
			for (IXMLMemento item : items) {
				String itemId = item.getString("id");
				CubridServer cs = CMHostNodePersistManager.getInstance().getServer(itemId);
				if (cs == null) {
					continue;
				}
				cgn.addChild(cs);
			}
		}

		//groupNodeList.size >=1
		if (groupNodeList.isEmpty()) {
			try {
				groupNodeList.add((CubridGroupNode) DEFAULT_GROUP_NODE.clone());
				List<CubridServer> servers = CMHostNodePersistManager.getInstance().getAllServers();
				for (CubridServer server : servers) {
					groupNodeList.get(0).addChild(server);
				}
			} catch (CloneNotSupportedException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	/**
	 * Add a new group node to list. The default group node only contain the
	 * items which has no parent group.
	 *
	 * @param group new group node.
	 */
	public void addGroupNode(CubridGroupNode group) {
		synchronized (this) {
			if (!groupNodeList.contains(group)) {
				groupNodeList.add(group);
			}
			//refresh the default node.
			List<ICubridNode> nodesHasParent = new ArrayList<ICubridNode>();
			CubridGroupNode dftNode = null;
			for (CubridGroupNode grp : groupNodeList) {
				//if is default node, don't add children here.
				if (isDefaultGroup(grp)) {
					dftNode = grp;
				} else {
					nodesHasParent.addAll(grp.getChildren());
				}
			}
			dftNode.removeAllChild();
			List<CubridServer> servers = CMHostNodePersistManager.getInstance().getAllServers();
			for (CubridServer ser : servers) {
				if (!nodesHasParent.contains(ser)) {
					dftNode.addChild(ser);
				}
			}
		}
	}

	/**
	 * Get all group nodes save at local.
	 *
	 * @return all group nodes.
	 */
	public List<CubridGroupNode> getAllGroupNodes() {
		return groupNodeList;
	}

	/**
	 * Save all group node.
	 *
	 */
	public void saveAllGroupNode() {
		synchronized (this) {
			XMLMemento memento = XMLMemento.createWriteRoot("groups");
			for (CubridGroupNode group : groupNodeList) {
				IXMLMemento child = memento.createChild("group");
				child.putString("id", group.getId());
				child.putString("name", group.getName());
				for (ICubridNode cn : group.getChildren()) {
					IXMLMemento childHost = child.createChild("item");
					childHost.putString("id", cn.getId());
				}
			}
			PersistUtils.saveXMLMemento(ApplicationUtil.CM_UI_PLUGIN_ID,
					COM_CUBRID_MANAGER_HOSTGROUP, memento);
		}

	}

	/**
	 * Get all group items just like hosts or connections.
	 *
	 * @return the group items of all.
	 */
	public List<ICubridNode> getAllGroupItems() {
		List<ICubridNode> result = new ArrayList<ICubridNode>();
		List<CubridServer> servers = CMHostNodePersistManager.getInstance().getAllServers();
		for (CubridServer server : servers) {
			result.add(server);
		}
		return result;
	}

	/**
	 * Get the group's item by item's name
	 *
	 * @param name item's name
	 * @return Group item
	 */
	public ICubridNode getGroupItemByItemName(String name) {
		List<ICubridNode> result = getAllGroupItems();
		for (ICubridNode node : result) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * get the group object by group id or group name
	 *
	 * @param id group id or group name
	 * @return Group node.
	 */
	public CubridGroupNode getGroupById(String id) {
		List<CubridGroupNode> groups = getAllGroupNodes();
		for (CubridGroupNode group : groups) {
			if (group.getId().equals(id)) {
				return group;
			}
		}
		return null;
	}

	/**
	 * get the group object by group name
	 *
	 * @param name group name
	 * @return Group node.
	 */
	public CubridGroupNode getGroupByName(String name) {
		return getGroupByName(getAllGroupNodes(), name);
	}

	/**
	 * get the group object by group name
	 *
	 * @param nodeList group list
	 * @param name group name
	 * @return Group node.
	 */
	public CubridGroupNode getGroupByName(List<CubridGroupNode> nodeList, String name) {
		for (CubridGroupNode group : nodeList) {
			if (group.getName().equals(name)) {
				return group;
			}
		}
		return null;
	}

	/**
	 * Remove group by id
	 *
	 * @param groupId group id or group name
	 */
	public void removeGroup(String groupId) {
		List<CubridGroupNode> groups = getAllGroupNodes();
		CubridGroupNode tobeRemoved = null;
		for (CubridGroupNode group : groups) {
			if (group.getId().equals(groupId)) {
				tobeRemoved = group;
			}
		}
		this.groupNodeList.remove(tobeRemoved);
		CubridGroupNode defaultGroup = getDefaultGroup();
		List<ICubridNode> children = tobeRemoved.getChildren();
		for (ICubridNode chi : children) {
			defaultGroup.addChild(chi);
		}
		saveAllGroupNode();
	}

	/**
	 * Reorder the groups by input string array.
	 *
	 * @param orderedName the ordered group names.
	 */
	public void reorderGroup(String[] orderedName) {
		List<CubridGroupNode> tempNode = new ArrayList<CubridGroupNode>();
		for (String name : orderedName) {
			CubridGroupNode cgn = getGroupByName(name);
			if (cgn == null) {
				continue;
			}
			tempNode.add(cgn);
		}
		groupNodeList.clear();
		groupNodeList.addAll(tempNode);
		saveAllGroupNode();
	}

	/**
	 * Change the item position in the items list.
	 *
	 * @param node node to be change.
	 * @param index position
	 */
	public void changeItemPosition(ICubridNode node, int index) {
		if (!(node instanceof CubridServer)) {
			return;
		}
		List<CubridServer> serverList = CMHostNodePersistManager.getInstance().getAllServers();
		int oldIndex = serverList.indexOf(node);
		int insertIndex = Math.min(serverList.size(), index);
		if (oldIndex < insertIndex) {
			insertIndex--;
		}
		serverList.remove(node);
		serverList.add(insertIndex, (CubridServer) node);
		CMHostNodePersistManager.getInstance().saveServers();
	}

	/**
	 * Retrieve the default group
	 *
	 * @return the default group.
	 */
	public CubridGroupNode getDefaultGroup() {
		return getGroupById(DEFAULT_GROUP_NODE.getId());
	}

	/**
	 * Retrieves whether the parameter is default group.
	 *
	 * @param group that need to be compare.
	 * @return true:is default;
	 */
	public boolean isDefaultGroup(CubridGroupNode group) {
		if (group == null) {
			return false;
		}
		return group.getId().equals(DEFAULT_GROUP_NODE.getId());
	}

	/**
	 * Reload all groups
	 *
	 * @return CubridGroupNode List
	 */
	public List<CubridGroupNode> reloadGroups() {
		loadGroupNode();
		return groupNodeList;
	}

	/**
	 * To fix when it has crashed group information
	 *
	 * @deprecated
	 */
	public void fix() {
		List<CubridServer> svrs = CMHostNodePersistManager.getInstance().getAllServers();
		if (svrs == null)
			return;

		List<ICubridNode> cnodes = new ArrayList<ICubridNode>();

		try {
			List<CubridGroupNode> gnodes = getAllGroupNodes();
			for (int i = 0; i < gnodes.size(); i++) {
				CubridGroupNode node = gnodes.get(i);
				List<ICubridNode> snode = node.getChildren();
				for (int j = 0; j < snode.size(); j++) {
					cnodes.add(snode.get(j));
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		try {
			for (int i = 0; i < svrs.size(); i++) {
				CubridServer svr = svrs.get(i);
				String gid = svr.getName();

				boolean exists = false;
				for (int j = 0; j < cnodes.size(); j++) {
					ICubridNode cnode = cnodes.get(j);
					if (cnode.getId() != null && cnode.getId().equals(gid)) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					getDefaultGroup().addChild(svr);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		saveAllGroupNode();
	}
}
