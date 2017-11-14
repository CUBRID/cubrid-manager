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
package com.cubrid.cubridmanager.core.common.socket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible to store the response message by tree structure
 * node
 * 
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */
public class TreeNode {

	private TreeNode parent = null;
	private List<TreeNode> childrenList = null;
	private MessageMap map = null;

	/**
	 * The constructor
	 */
	public TreeNode() {
		if (map == null) {
			map = new MessageMap();
		}
	}

	/**
	 * Add a child node
	 * 
	 * @param node TreeNode The object of TreeNode
	 */
	public void addChild(TreeNode node) {
		if (childrenList == null) {
			childrenList = new ArrayList<TreeNode>();
		}
		node.setParent(this);
		childrenList.add(node);
	}

	/**
	 * 
	 * This is a test method,it is only used in the test occasion
	 * 
	 * @return String
	 */
	public String toString() {
		return toString("");
	}

	/**
	 * 
	 * Convert the given string to a certain format
	 * 
	 * @param tabstr String The given string
	 * @return String
	 */
	private String toString(String tabstr) {
		String tab = tabstr;
		StringBuffer bf = new StringBuffer();
		Pattern pattern = Pattern.compile("^(.*)", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(map.toString());
		bf.append(matcher.replaceAll(tabstr + "$1"));
		tab = tab + "\t";
		if (childrenList != null) {
			for (TreeNode node : childrenList) {
				bf.append(node.toString(tab));
			}
		}
		return bf.toString();
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public List<TreeNode> getChildren() {
		return childrenList;
	}

	/**
	 * Set children nodes
	 * 
	 * @param children List<TreeNode> The object of List<TreeNode> stored some
	 *        objects of TreeNode
	 */
	public void setChildren(List<TreeNode> children) {
		this.childrenList = children;
	}

	/**
	 * 
	 * This is a test method,it is only used in the test occasion
	 * 
	 */
	public void toTreeString() {
		Set<String> set = new HashSet<String>();
		List<TreeNode> list = new ArrayList<TreeNode>();
		list.add(this);
		while (!list.isEmpty()) {
			TreeNode node = list.remove(0);
			if (node.childrenList == null) {
				String path = node.getPath();
				set.add(path);
			} else {
				list.addAll(node.childrenList);
			}
		}
	}

	/**
	 * 
	 * This is a test method,it is only used in the test occasion
	 * 
	 * @return String The path string
	 */
	private String getPath() {
		String path = map.getValue("task");
		if (path == null) {
			path = map.getValue("open");
		}
		if (path == null) {
			path = "";
		}
		if (parent == null) {
			return path;
		}
		return parent.getPath() + "--" + path;
	}

	/**
	 * Get a message value by key in this node
	 * 
	 * @param key String The key string
	 * @return String The value string
	 */
	public String getValue(String key) {
		return map.getValue(key);
	}

	/**
	 * Get all message values by key in this node
	 * 
	 * @param key String The key string
	 * @return String[] The values stored in a array
	 */
	public String[] getValues(String key) {
		return map.getValues(key);
	}

	/**
	 * get all value by Map interface
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getValueByMap() {
		return map.getValueByMap();
	}

	/**
	 * Add message in key-value pair format
	 * 
	 * @param key String The given key string
	 * @param value String The given value string
	 */
	public void add(String key, String value) {
		map.add(key, value);
	}

	/**
	 * Add message string(key:value)
	 * 
	 * @param str String The given string
	 */
	public void add(String str) {
		map.add(str);
	}

	/**
	 * Modify message value by key
	 * 
	 * @param key String The given key string
	 * @param value The given value string
	 */
	public void modifyValue(String key, String value) {
		map.addOrModifyValue(key, value);
	}

	/**
	 * Modify message values by key
	 * 
	 * @param key String The given key string
	 * @param values String[] The given values string
	 */
	public void modifyValues(String key, String[] values) {
		map.addOrModifyValues(key, values);
	}

	/**
	 * 
	 * Return the size of children
	 * 
	 * @return int
	 */
	public int childrenSize() {
		if (childrenList == null) {
			return 0;
		}
		return childrenList.size();
	}

	/**
	 * 
	 * Get this node's message by map
	 * 
	 * @return Map<String, String> The values stored in map
	 */
	public Map<String, String> getValuesByMap() {
		if (map != null) {
			return map.getValueByMap();
		}
		return null;
	}

	public List<String> getResponseMessage() {
		return map.getResponseMessage();
	}
}
