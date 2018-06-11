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
package com.cubrid.common.ui.spi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;

/**
 * 
 * This class implment the ICubridNode interface defaultly.it can construct a
 * simple tree structrue.
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class DefaultCubridNode implements
		ICubridNode,Cloneable {
	protected List<ICubridNode> childList = null;
	private String id = "";
	private String editorId = null;
	private String viewId = null;
	private String label = "";
	private ICubridNode parent = null;
	private boolean isRoot = false;
	private String iconPath = "";
	private ICubridNodeLoader loader = null;
	private String type = null;
	private CubridServer server = null;
	private boolean isContainer = false;
	protected Object modelObj = null;
	private Map<String, Object> objMap = null;

	/**
	 * The constructor
	 * 
	 * @param id
	 * @param label
	 * @param iconPath
	 */
	public DefaultCubridNode(String id, String label, String iconPath) {
		this.id = id;
		this.label = label;
		this.iconPath = iconPath;
		isRoot = false;
		childList = new ArrayList<ICubridNode>();
		objMap = new HashMap<String, Object>();
	}

	/**
	 * Get whether it is container node
	 * 
	 * @return boolean
	 */
	public boolean isContainer() {
		return isContainer;
	}

	/**
	 * 
	 * Set this node for container node
	 * 
	 * @param isContainer whether it is container node
	 */
	public void setContainer(boolean isContainer) {
		this.isContainer = isContainer;
	}

	/**
	 * 
	 * Get child CUBRID Node by id
	 * 
	 * @param id the node id
	 * @return ICubridNode object
	 */
	public ICubridNode getChild(String id) {
		if (childList != null) {
			for (ICubridNode node : childList) {
				if (node != null && node.getId().equals(id)) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Search all children nodes and Get the child node by id
	 * 
	 * @param id the node id
	 * @return the ICubridNode object
	 */
	public ICubridNode getChildInAll(String id) {
		ICubridNode childNode = getChild(id);
		if (childNode == null) {
			for (ICubridNode node : childList) {
				childNode = node.getChild(id);
				if (childNode != null) {
					return childNode;
				}
			}
		} else {
			return childNode;
		}
		return null;
	}

	/**
	 * 
	 * Get all children of this node
	 * 
	 * @return ICubridNode object list
	 */
	public List<ICubridNode> getChildren() {
		return childList;
	}

	/**
	 * 
	 * Get all children of this node
	 * 
	 * @param monitor the IProgressMonitor object
	 * @return the ICubridNode Array
	 */
	public ICubridNode[] getChildren(IProgressMonitor monitor) {
		if (loader != null && !loader.isLoaded()) {
			loader.load(this, monitor);
		}
		if (!childList.isEmpty()) {
			ICubridNode[] nodeArr = new ICubridNode[childList.size()];
			return childList.toArray(nodeArr);
		}
		return new ICubridNode[]{};
	}

	/**
	 * Add child object to this node
	 * 
	 * @param obj the ICubridNode object
	 */
	public void addChild(ICubridNode obj) {
		if (obj != null && !isContained(obj)) {
			obj.setParent(this);
			obj.setServer(this.getServer());
			childList.add(obj);
			if (NodeType.TABLE_FOLDER.equals(getType())
					|| NodeType.VIEW_FOLDER.equals(getType())) {
				Collections.sort(childList);
			}
		}
	}

	/**
	 * Add child object to this node
	 * 
	 * @param obj the ICubridNode object
	 * @param index the insert index of node.
	 */
	public void addChild(ICubridNode obj, int index) {
		if (obj != null && !isContained(obj)) {
			obj.setParent(this);
			obj.setServer(this.getServer());
			childList.add(index, obj);
			if (NodeType.TABLE_FOLDER.equals(getType())
					|| NodeType.VIEW_FOLDER.equals(getType())) {
				Collections.sort(childList);
			}
		}
	}

	/**
	 * Remove child object from this node
	 * 
	 * @param obj the ICubridNode object
	 */
	public void removeChild(ICubridNode obj) {
		if (obj != null) {
			childList.remove(obj);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(obj,
							CubridNodeChangedEventType.NODE_REMOVE));
		}
	}

	/**
	 * Remove all child objects from this node
	 */
	public void removeAllChild() {
		childList.clear();
	}

	/**
	 * Get parent object of this node
	 * 
	 * @return the parent node
	 */
	public ICubridNode getParent() {
		return parent;
	}

	/**
	 * Set this node's parent node object
	 * 
	 * @param obj the parent node
	 */
	public void setParent(ICubridNode obj) {
		parent = obj;
	}

	/**
	 * Get whether contain this child node in this node,only traverse the first
	 * level
	 * 
	 * @param obj the ICubridNode object
	 * @return <code>true</code> if it is contained;<code>false</code> otherwise
	 */
	public boolean isContained(ICubridNode obj) {
		if (obj == null) {
			return false;
		}
		for (ICubridNode node : childList) {
			if (node != null && node.getId().equals(obj.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Get this object position
	 * 
	 * @param obj The ICubridNode
	 * @return int
	 */
	public int position(ICubridNode obj) {
		if (obj == null) {
			return -1;
		}
		int i = 0;
		for (ICubridNode node : childList) {
			if (node != null && node.getId().equals(obj.getId())) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Get whether contain this child node in this node,traverse all children
	 * 
	 * @param obj the ICubridNode object
	 * @return <code>true</code> if it is contained;<code>false</code> otherwise
	 */
	public boolean isContainedInAll(ICubridNode obj) {
		if (childList.contains(obj)) {
			return true;
		} else {
			for (ICubridNode node : childList) {
				if (node.isContainedInAll(obj)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retrun whether it is the top level node
	 * 
	 * @return <code>true</code> if it is root node;<code>false</code> otherwise
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * Set this node for root node
	 * 
	 * @param isRoot whether it is root
	 */
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	/**
	 * Get the path of this node object's icon path
	 * 
	 * @return String the icon path
	 */
	public String getIconPath() {
		return iconPath;
	}

	/**
	 * Set the path of this node object's icon path
	 * 
	 * @param iconPath the icon path
	 */
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	/**
	 * Get displayed label of this node
	 * 
	 * @return String the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set displayed label of this node
	 * 
	 * @param label the label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Get the UUID of this node
	 * 
	 * @return the UUID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the UUID of this node
	 * 
	 * @param id the UUID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * Get this node object's loader for loading all children
	 * 
	 * @return the CUBRID node loader
	 */
	public ICubridNodeLoader getLoader() {
		return this.loader;
	}

	/**
	 * Set this node object's loader for loading all children
	 * 
	 * @param loader the ICubridNodeLoader object
	 */
	public void setLoader(ICubridNodeLoader loader) {
		this.loader = loader;
	}

	/**
	 * Get editor id of this node
	 * 
	 * @return the editor id
	 */
	public String getEditorId() {
		return editorId;
	}

	/**
	 * 
	 * Set editor id of this node
	 * 
	 * @param editorId the editor id
	 */
	public void setEditorId(String editorId) {
		this.editorId = editorId;

	}

	/**
	 * 
	 * Get view id of this node
	 * 
	 * @return the view id
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * 
	 * Set view id of this node
	 * 
	 * @param viewId the view id
	 * 
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;

	}

	/**
	 * Get adapter object
	 * 
	 * @param adapter the adapter
	 * @return the adapter object
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (modelObj != null && modelObj.getClass() == adapter) {
			return modelObj;
		}
		if (ServerInfo.class == adapter) {
			return getServer() == null ? null : getServer().getServerInfo();
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * Set this node object's type
	 * 
	 * @param type the String
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get this node object's type
	 * 
	 * @return the CubridNodeType object
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Get the server that this node belong to
	 * 
	 * @return the CubridServer object
	 */
	public CubridServer getServer() {
		return server;
	}

	/**
	 * Set the server that this node belong to
	 * 
	 * @param obj the CubridServer object
	 */
	public void setServer(CubridServer obj) {
		server = obj;
	}

	/**
	 * Returns whether the editor input exists.
	 * 
	 * @return <code>true</code> if the editor input exists; <code>false</code>
	 *         otherwise
	 */
	public boolean exists() {
		return false;
	}

	/**
	 * Returns an object that can be used to save the state of this editor
	 * input.
	 * 
	 * @return the persistable element, or <code>null</code> if this editor
	 *         input cannot be persisted
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Returns the name of this editor input for display purposes.
	 * 
	 * 
	 * @return the name string; never <code>null</code>;
	 */
	public String getName() {
		return getLabel();
	}

	/**
	 * Returns the tool tip text for this editor input. This text is used to
	 * differentiate between two input with the same name. For instance,
	 * MyClass.java in folder X and MyClass.java in folder Y. The format of the
	 * text varies between input types. </p>
	 * 
	 * @return the tool tip text; never <code>null</code>.
	 */
	public String getToolTipText() {
		String tipText = getLabel();
		ICubridNode parent = getParent();
		while (parent != null) {
			tipText = parent.getLabel() + "/" + tipText;
			parent = parent.getParent();
		}
		return tipText;
	}

	/**
	 * Returns the image descriptor for this input.
	 * 
	 * 
	 * @return the image descriptor for this input; may be <code>null</code> if
	 *         there is no image.
	 */
	public ImageDescriptor getImageDescriptor() {
		if (getIconPath() != null && getIconPath().trim().length() > 0) {
			return CommonUIPlugin.getImageDescriptor(getIconPath());
		}
		return null;
	}

	/**
	 * Set the corresponding CUBRID model object of this node
	 * 
	 * @param obj the model object
	 */
	public void setModelObj(Object obj) {
		modelObj = obj;
	}

	/**
	 * 
	 * Set data
	 * 
	 * @param key String
	 * @param obj Object
	 */
	public void setData(String key, Object obj) {
		if (objMap == null) {
			objMap = new HashMap<String, Object>();
		}
		objMap.put(key, obj);
	}

	/**
	 * 
	 * Get data
	 * 
	 * @param key String
	 * @return Object
	 */
	public Object getData(String key) {
		return objMap == null ? null : objMap.get(key);
	}

	/**
	 * Compare the object for sorter in the same tree level
	 * 
	 * @param obj the ICubridNode object
	 * @return <code>1<code> greater;<code>0</code>equal;<code>-1</code> less
	 */
	public int compareTo(ICubridNode obj) {
		if (obj == null) {
			return 1;
		}
		if (NodeType.USER_TABLE.equals(obj.getType())) {
			return 0;
		}

		// sort for server, the "localhost" is the first
		if (NodeType.SERVER.equals(getType()) && "localhost".equals(getLabel())) {
			return -1;
		} else if (NodeType.SERVER.equals(obj.getType())
				&& "localhost".equals(obj.getLabel())) {
			return 1;
		}
		String id = obj.getId();
		return this.id.compareTo(id);
	}

	/**
	 * Return whether the current object is equal the obj
	 * 
	 * @param obj the object
	 * @return <code>true</code> if they are equal;<code>false</code> otherwise
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultCubridNode)) {
			return false;
		}
		DefaultCubridNode node = (DefaultCubridNode) obj;
		return this.getId().compareTo(node.getId()) == 0;
	}

	/**
	 * Return the hash code value
	 * 
	 * @return the hash code value
	 */
	public int hashCode() {
		return this.id.hashCode();
	}
	
	public DefaultCubridNode clone() throws CloneNotSupportedException {
		DefaultCubridNode obj = (DefaultCubridNode) super.clone();;
		return obj;
	}
}
