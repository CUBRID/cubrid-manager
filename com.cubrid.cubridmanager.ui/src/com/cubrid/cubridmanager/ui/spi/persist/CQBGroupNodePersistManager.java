package com.cubrid.cubridmanager.ui.spi.persist;

import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.ICubridGroupNodeManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridGroupNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.preference.PreferenceStore;
import org.slf4j.Logger;

/**
 * Group node persist manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-2 created by pangqiren
 */
public final class CQBGroupNodePersistManager implements ICubridGroupNodeManager {

    private static final Logger LOGGER = LogUtil.getLogger(CQBGroupNodePersistManager.class);
    public static final String COM_CUBRID_QB_DBGROUP = "com.cubrid.manager.databasegroup";

    private static CQBGroupNodePersistManager instance;
    private List<CubridGroupNode> groupNodeList;

    private CQBGroupNodePersistManager() {
        init();
    }

    /**
     * Return the only GroupNodePersistManager
     *
     * @return GroupNodePersistManager
     */
    public static CQBGroupNodePersistManager getInstance() {
        synchronized (CQBGroupNodePersistManager.class) {
            if (instance == null) {
                instance = new CQBGroupNodePersistManager();
            }
        }
        return instance;
    }

    /** Initial the persist manager */
    protected void init() {
        synchronized (this) {
            groupNodeList = new ArrayList<CubridGroupNode>();
            loadGroupNode();
        }
    }

    /**
     * Add a new group node to list. The default group node only contain the items which has no
     * parent group.
     *
     * @param group new group node.
     */
    public void addGroupNode(CubridGroupNode group) {
        synchronized (this) {
            if (!groupNodeList.contains(group)) {
                groupNodeList.add(group);
            }
            // refresh the default node.
            List<ICubridNode> nodesHasParent = new ArrayList<ICubridNode>();
            CubridGroupNode dftNode = null;
            for (CubridGroupNode grp : groupNodeList) {
                // if is default node, don't add children here.
                if (isDefaultGroup(grp)) {
                    dftNode = grp;
                } else {
                    nodesHasParent.addAll(grp.getChildren());
                }
            }
            dftNode.removeAllChild();
            List<CubridDatabase> databases = CQBDBNodePersistManager.getInstance().getAllDatabase();
            for (CubridDatabase db : databases) {
                if (!nodesHasParent.contains(db)) {
                    dftNode.addChild(db);
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

    /** Load group nodes from local preference. */
    private void loadGroupNode() {
        synchronized (this) {
            IXMLMemento memento =
                    PersistUtils.getXMLMemento(
                            ApplicationUtil.CQB_UI_PLUGIN_ID, COM_CUBRID_QB_DBGROUP);
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
            String filePath =
                    workspacePath
                            + File.separator
                            + ".metadata"
                            + File.separator
                            + ".plugins"
                            + File.separator
                            + "org.eclipse.core.runtime"
                            + File.separator
                            + ".settings"
                            + File.separator
                            + "com.cubrid.cubridquery.ui.prefs";
            PreferenceStore preference = new PreferenceStore(filePath);
            int size = groupNodeList.size();
            try {
                preference.load();
                String xmlString = preference.getString(COM_CUBRID_QB_DBGROUP);
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
     */
    private void loadGroupNode(IXMLMemento memento) {
        if (memento != null) {
            IXMLMemento[] children = memento.getChildren("group");
            for (int i = 0; i < children.length; i++) {
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
                    CubridDatabase cs = CQBDBNodePersistManager.getInstance().getDatabase(itemId);
                    if (cs == null || cs.getParent() != null) {
                        continue;
                    }
                    cgn.addChild(cs);
                }
            }
        }

        // groupNodeList.size >=1
        if (groupNodeList.isEmpty()) {
            try {
                CubridGroupNode defaultGroup = (CubridGroupNode) DEFAULT_GROUP_NODE.clone();
                groupNodeList.add(defaultGroup);
                List<CubridDatabase> servers =
                        CQBDBNodePersistManager.getInstance().getAllDatabase();
                for (CubridDatabase server : servers) {
                    defaultGroup.addChild(server);
                }
            } catch (CloneNotSupportedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /** Save all group node. */
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
            PersistUtils.saveXMLMemento(
                    ApplicationUtil.CQB_UI_PLUGIN_ID, COM_CUBRID_QB_DBGROUP, memento);
        }
    }

    /**
     * Get all group items just like hosts or connections.
     *
     * @return the group items of all.
     */
    public List<ICubridNode> getAllGroupItems() {
        List<ICubridNode> result = new ArrayList<ICubridNode>();
        List<CubridDatabase> servers = CQBDBNodePersistManager.getInstance().getAllDatabase();
        for (CubridDatabase server : servers) {
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
        if (!(node instanceof CubridDatabase)) {
            return;
        }
        List<CubridDatabase> databaseList = CQBDBNodePersistManager.getInstance().getAllDatabase();
        int oldIndex = databaseList.indexOf(node);
        int insertIndex = Math.min(databaseList.size(), index);
        if (oldIndex < insertIndex) {
            insertIndex--;
        }
        databaseList.remove(node);
        databaseList.add(insertIndex, (CubridDatabase) node);
        CQBDBNodePersistManager.getInstance().saveDatabases();
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

    /** To fix when it has crashed group information */
    public void fix() {
        List<CubridDatabase> dbs = CQBDBNodePersistManager.getInstance().getAllDatabase();
        if (dbs == null) return;

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
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < dbs.size(); i++) {
                CubridDatabase db = dbs.get(i);
                String gid = db.getName() + "/" + db.getName();

                boolean exists = false;
                for (int j = 0; j < cnodes.size(); j++) {
                    ICubridNode cnode = cnodes.get(j);
                    if (cnode.getId() != null && cnode.getId().equals(gid)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    getDefaultGroup().addChild(db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveAllGroupNode();
    }
}
