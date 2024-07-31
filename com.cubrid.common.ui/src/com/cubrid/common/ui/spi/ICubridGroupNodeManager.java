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
package com.cubrid.common.ui.spi;

import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import java.util.List;

/**
 * Cubrid GroupNode Manager
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-24 created by Kevin Cao
 */
public interface ICubridGroupNodeManager {

    /**
     * A prototype of default node,don't change any attribute of it,please use Object.clone to get a
     * new default group node.
     */
    public static final CubridGroupNode DEFAULT_GROUP_NODE =
            new CubridGroupNode("Default Group", "Default Group", "icons/navigator/group.png");

    /**
     * Get all group nodes save at local.
     *
     * @return all group nodes.
     */
    public List<CubridGroupNode> getAllGroupNodes();

    /**
     * Add a new group node to list. The default group node only contain the items which has no
     * parent group.
     *
     * @param group new group node.
     */
    public void addGroupNode(CubridGroupNode group);

    /** Save all group node. */
    public void saveAllGroupNode();

    /**
     * Get all group items just like hosts or connections.
     *
     * @return the group items of all.
     */
    public List<ICubridNode> getAllGroupItems();

    /**
     * Get the group's item by item's name
     *
     * @param name item's name
     * @return Group item
     */
    public ICubridNode getGroupItemByItemName(String name);

    /**
     * get the group object by group id
     *
     * @param id group id
     * @return Group node.
     */
    public CubridGroupNode getGroupById(String id);

    /**
     * get the group object by group name
     *
     * @param name group name
     * @return Group node.
     */
    public CubridGroupNode getGroupByName(String name);

    /**
     * get the group object by group name
     *
     * @param nodeList the group node list
     * @param name group name
     * @return Group node.
     */
    public CubridGroupNode getGroupByName(List<CubridGroupNode> nodeList, String name);

    /**
     * Remove group by id
     *
     * @param groupId group id or group name
     */
    public void removeGroup(String groupId);

    /**
     * Reorder the groups by input string array.
     *
     * @param orderedName the ordered group names.
     */
    public void reorderGroup(String[] orderedName);

    /**
     * Change the item position in the items list.
     *
     * @param node node to be change.
     * @param index position
     */
    public void changeItemPosition(ICubridNode node, int index);

    /**
     * Get the default group of the group list.
     *
     * @return default group.
     */
    public CubridGroupNode getDefaultGroup();

    /**
     * Retrieves whether the parameter is default group.
     *
     * @param group that need to be compare.
     * @return true:is default;
     */
    public boolean isDefaultGroup(CubridGroupNode group);
}
