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
package com.cubrid.cubridmanager.ui.replication.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.replication.model.DistributorInfo;
import com.cubrid.cubridmanager.core.replication.model.MasterInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamInfo;
import com.cubrid.cubridmanager.core.replication.model.SlaveInfo;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.action.EditAction;
import com.cubrid.cubridmanager.ui.replication.editor.dnd.NodeDropTargetListener;
import com.cubrid.cubridmanager.ui.replication.editor.model.ArrowConnection;
import com.cubrid.cubridmanager.ui.replication.editor.model.ContainerNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Diagram;
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.LeafNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.Node;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import com.cubrid.cubridmanager.ui.replication.editor.parts.PartFactory;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.slf4j.Logger;

/**
 * The replication editor is responsible to design replication
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class ReplicationEditor extends GraphicalEditorWithFlyoutPalette {

    public static final String ID =
            "com.cubrid.cubridmanager.ui.replication.editor.ReplicationEditor";
    private static final Logger LOGGER = LogUtil.getLogger(ReplicationEditor.class);
    private PaletteRoot paletteRoot;
    private Diagram diagram = null;
    protected static int paletteSize = 180;
    private boolean isEditable = true;
    private boolean isDirty = false;
    private boolean isDisposed = false;

    public ReplicationEditor() {
        setEditDomain(new DefaultEditDomain(this));
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#init(org.eclipse.ui.IEditorSite,
     *     org.eclipse.ui.IEditorInput)
     * @param site the editor site
     * @param input the editor input
     * @exception PartInitException if this editor was not initialized successfully
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        createDiagram(input);
        if (input instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) input;
            String serverName = node.getServer().getLabel();
            String port = node.getServer().getMonPort();
            String title = "-- " + input.getName() + "@" + serverName + ":" + port;
            setPartName(Messages.bind(Messages.msgViewRepl, title));
        } else {
            setPartName(Messages.msgCreateRepl);
        }
    }

    /** @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer() */
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();
        ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
        viewer.setRootEditPart(rootEditPart);
        viewer.setEditPartFactory(new PartFactory(this));

        ZoomManager manager = rootEditPart.getZoomManager();
        getActionRegistry().registerAction(new ZoomInAction(manager));
        getActionRegistry().registerAction(new ZoomOutAction(manager));

        double[] zoomLevels =
                new double[] {0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0};
        manager.setZoomLevels(zoomLevels);

        List<String> zoomContributions = new ArrayList<String>();
        zoomContributions.add(ZoomManager.FIT_ALL);
        zoomContributions.add(ZoomManager.FIT_HEIGHT);
        zoomContributions.add(ZoomManager.FIT_WIDTH);
        manager.setZoomLevelContributions(zoomContributions);

        KeyHandler keyHandler = new KeyHandler();
        if (isEditable) {
            keyHandler.put(
                    KeyStroke.getPressed(SWT.DEL, 127, 0),
                    getActionRegistry().getAction(ActionFactory.DELETE.getId()));
        }
        keyHandler.put(
                KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
                getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

        keyHandler.put(
                KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
                getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

        keyHandler.put(
                KeyStroke.getPressed('a', 97, 0),
                getActionRegistry().getAction(ActionFactory.SELECT_ALL.getId()));

        viewer.setProperty(
                MouseWheelHandler.KeyGenerator.getKey(SWT.NONE), MouseWheelZoomHandler.SINGLETON);
        viewer.setKeyHandler(keyHandler);

        ContextMenuProvider provider =
                new ReplEditorContextMenuProvider(viewer, getActionRegistry());
        viewer.setContextMenu(provider);
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
     */
    protected void initializeGraphicalViewer() {
        getGraphicalViewer().setContents(diagram);
        getGraphicalViewer()
                .addDropTargetListener(new NodeDropTargetListener(getGraphicalViewer()));
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        List<ContainerNode> hostNodeList = diagram.getChildNodeList();
        int hostSize = 0;
        if (hostNodeList != null) {
            hostSize = hostNodeList.size();
        }
        // check the host number,2 at less
        if (hostSize <= 1) {
            CommonUITool.openErrorBox(this.getSite().getShell(), Messages.errInvalidReplDesign);
            monitor.setCanceled(true);
            return;
        }
        // check the host and replication component information integrity
        for (int i = 0; i < hostSize; i++) {
            HostNode host = (HostNode) hostNodeList.get(i);
            if (!host.isValid()) {
                CommonUITool.openErrorBox(
                        this.getSite().getShell(),
                        Messages.bind(Messages.errInvalidHost, host.getName()));
                monitor.setCanceled(true);
                return;
            }
            List<LeafNode> leafNodeList = host.getChildNodeList();
            for (int j = 0; j < leafNodeList.size(); j++) {
                LeafNode node = leafNodeList.get(j);
                if (!node.isValid()) {
                    CommonUITool.openErrorBox(
                            this.getSite().getShell(),
                            Messages.bind(Messages.errInvalidReplComponent, node.getName()));
                    monitor.setCanceled(true);
                    return;
                }
            }
        }
        // check the replication component connection validity
        List<List<Node>> replicationsList = new ArrayList<List<Node>>();
        for (int i = 0; i < hostSize; i++) {
            HostNode host = (HostNode) hostNodeList.get(i);
            List<LeafNode> leafNodeList = host.getChildNodeList();
            for (int j = 0; j < leafNodeList.size(); j++) {
                LeafNode node = leafNodeList.get(j);
                if (node instanceof MasterNode) {
                    List<ArrowConnection> connList = node.getOutgoingConnections();
                    if (connList == null || connList.isEmpty()) {
                        CommonUITool.openErrorBox(
                                this.getSite().getShell(),
                                Messages.bind(Messages.errInvalidMasterConn1, node.getName()));
                        monitor.setCanceled(true);
                        return;
                    } else {
                        for (int k = 0; k < connList.size(); k++) {
                            LOGGER.debug("Replication chain" + k);
                            LOGGER.debug(node.getName());
                            List<Node> replicationList = new ArrayList<Node>();
                            replicationList.add(node);
                            HostNode masterHost = (HostNode) node.getParent();
                            ArrowConnection conn = connList.get(k);
                            HostNode distHost = (HostNode) conn.getTarget().getParent();
                            if (masterHost.getName().equals(distHost.getName())) {
                                CommonUITool.openErrorBox(
                                        this.getSite().getShell(),
                                        Messages.bind(
                                                Messages.errInvalidMasterConn2,
                                                new String[] {
                                                    node.getName(), conn.getTarget().getName()
                                                }));
                                monitor.setCanceled(true);
                                return;
                            }
                            createReplChain(replicationList, conn);
                            replicationsList.add(replicationList);
                        }
                    }
                } else if (node instanceof DistributorNode) {
                    if (node.getIncomingConnections() == null
                            || node.getIncomingConnections().size() == 0) {
                        CommonUITool.openErrorBox(
                                this.getSite().getShell(),
                                Messages.bind(Messages.errInvalidDistConn1, node.getName()));
                        monitor.setCanceled(true);
                        return;
                    }
                    if (node.getOutgoingConnections() == null
                            || node.getOutgoingConnections().size() == 0) {

                        CommonUITool.openErrorBox(
                                this.getSite().getShell(),
                                Messages.bind(Messages.errInvalidDistConn2, node.getName()));
                        monitor.setCanceled(true);
                        return;
                    }
                } else if (node instanceof SlaveNode
                        && (node.getIncomingConnections() == null
                                || node.getIncomingConnections().isEmpty())) {
                    CommonUITool.openErrorBox(
                            this.getSite().getShell(),
                            Messages.bind(Messages.errInvalidSlaveConn, node.getName()));
                    monitor.setCanceled(true);
                    return;
                }
            }
        }
        int count = replicationsList.size();
        if (count <= 0) {
            CommonUITool.openErrorBox(this.getSite().getShell(), Messages.errInvalidReplDesign);
            monitor.setCanceled(true);
            return;
        }
        for (int i = 0; i < count; i++) {
            List<Node> replcationlist = replicationsList.get(i);
            int size = replcationlist.size();
            if (size < 3) {
                CommonUITool.openErrorBox(this.getSite().getShell(), Messages.errInvalidReplDesign);
                monitor.setCanceled(true);
                return;
            }
            if (!(replcationlist.get(size - 1) instanceof SlaveNode)) {
                CommonUITool.openErrorBox(this.getSite().getShell(), Messages.errInvalidReplDesign);
                monitor.setCanceled(true);
                return;
            }
            // check the connection validity,
            for (int j = 1; j + 1 < size; j++) {
                Node node1 = (Node) replcationlist.get(j).getParent();
                Node node2 = (Node) replcationlist.get(j + 1).getParent();
                // from the slave to the distributor can not in the same host
                if (j % 2 == 0) {
                    if (node1.getName().equals(node2.getName())) {
                        CommonUITool.openErrorBox(
                                this.getSite().getShell(),
                                Messages.bind(
                                        Messages.errInvalidConn1,
                                        new String[] {node1.getName(), node2.getName()}));
                        monitor.setCanceled(true);
                        return;
                    }
                } else { // from the distributor to the slave must be in the same host
                    if (!node1.getName().equals(node2.getName())) {
                        CommonUITool.openErrorBox(
                                this.getSite().getShell(),
                                Messages.bind(
                                        Messages.errInvalidConn2,
                                        new String[] {node1.getName(), node2.getName()}));
                        monitor.setCanceled(true);
                        return;
                    }
                }
            }
        }
        if (!CommonUITool.openConfirmBox(Messages.msgConfirmCreateRepl)) {
            return;
        }
        isEditable = false;
        getCommandStack().flush();
        setDirty(false);
        createReplication(replicationsList);
    }

    /**
     * create the replication
     *
     * @param replicationsList List<List<Node>>
     */
    private void createReplication(List<List<Node>> replicationsList) {
        List<String> tempList = new ArrayList<String>();
        CreateReplicationJobExecutor executor = new CreateReplicationJobExecutor(this);
        for (int i = 0; i < replicationsList.size(); i++) {
            List<Node> replicationList = replicationsList.get(i);
            MasterNode masterDb = null;
            DistributorNode distDb = null;
            SlaveNode slaveDb = null;
            for (int j = 0; j < replicationList.size(); j++) {
                Node node = replicationList.get(j);
                if (node instanceof MasterNode) {
                    masterDb = (MasterNode) node;
                } else if (node instanceof DistributorNode) {
                    distDb = (DistributorNode) node;
                } else if (node instanceof SlaveNode) {
                    slaveDb = (SlaveNode) node;
                }

                HostNode host = (HostNode) node.getParent();
                String str = host.getIp() + ":" + host.getPort() + ":" + node.getName();
                if (isExist(str, tempList)) {
                    continue;
                }
                tempList.add(str);
                String errorMsg = null;
                if (node instanceof MasterNode) {
                    errorMsg =
                            CreateReplicationUtil.createMasterTaskGroup(
                                    masterDb,
                                    CreateReplicationUtil.getAllSlaveOfMaster(
                                            (MasterNode) node, replicationsList),
                                    executor);
                    if (errorMsg == null) {
                        errorMsg =
                                CreateReplicationUtil.createReplServerTaskGroup(masterDb, executor);
                    }
                } else if (node instanceof DistributorNode) {
                    if (j > 2) {
                        errorMsg =
                                CreateReplicationUtil.createDistributorTaskGroup(
                                        masterDb, slaveDb, distDb, executor);
                    } else {
                        errorMsg =
                                CreateReplicationUtil.createDistributorTaskGroup(
                                        masterDb, distDb, executor);
                    }
                    if (errorMsg == null) {
                        errorMsg = CreateReplicationUtil.createAgentTaskGroup(distDb, executor);
                    }
                } else if (node instanceof SlaveNode) {
                    errorMsg =
                            CreateReplicationUtil.createSlaveTaskGroup(
                                    masterDb, distDb, slaveDb, executor);
                    if (j > 2 && errorMsg == null) {
                        errorMsg =
                                CreateReplicationUtil.createSlaveMasterTaskGroup(
                                        masterDb, slaveDb, executor);
                        if (errorMsg == null) {
                            errorMsg =
                                    CreateReplicationUtil.createReplServerTaskGroup(
                                            masterDb, executor);
                        }
                    }
                }
                if (errorMsg != null) {
                    CommonUITool.openErrorBox(errorMsg);
                    isEditable = true;
                    setDirty(true);
                    return;
                }
            }
        }
        JobFamily jobFamily = new JobFamily();
        jobFamily.setServerName(JobFamily.ALL_SERVER);
        jobFamily.setDbName(JobFamily.ALL_DB);
        executor.schedule(Messages.createReplicationJobName, jobFamily, true, Job.SHORT);
    }

    /**
     * whether the list contains the string
     *
     * @param str String
     * @param list List<String>
     * @return boolean
     */
    private boolean isExist(String str, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (str.equals(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * create the replication chain
     *
     * @param replicationList List<Node>
     * @param conn ArrowConnection
     */
    private void createReplChain(List<Node> replicationList, ArrowConnection conn) {
        Node targetNode = conn.getTarget();
        LOGGER.debug(targetNode.getName());
        replicationList.add(targetNode);
        List<ArrowConnection> connList = targetNode.getOutgoingConnections();
        for (int i = 0; connList != null && i < connList.size(); i++) {
            createReplChain(replicationList, connList.get(i));
        }
    }

    /**
     * create the diagram
     *
     * @param input IEditorInput
     */
    private void createDiagram(IEditorInput input) {
        diagram = new Diagram();
        if (input instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) input;
            if (NodeType.DATABASE.equals(node.getType())) {
                isEditable = false;
                CubridServer server = node.getServer();
                int x = 100;
                int y = 20;
                int hostVertSpan = 100;
                if (server != null) {
                    ReplicationInfo replicationInfo =
                            (ReplicationInfo) node.getAdapter(ReplicationInfo.class);
                    if (replicationInfo != null) {
                        // distributor component
                        DistributorInfo distInfo = replicationInfo.getDistInfo();
                        DistributorNode dist = null;
                        if (distInfo != null) {
                            dist = new DistributorNode();
                            dist.setDbName(distInfo.getDistDbName());
                            dist.setDbPath(distInfo.getDistDbPath());
                            dist.setCopyLogPath(distInfo.getCopyLogPath());
                            dist.setErrorLogPath(distInfo.getErrorLogPath());
                            dist.setTrailLogPath(distInfo.getTrailLogPath());
                            dist.setReplAgentPort(distInfo.getAgentPort());
                            dist.setDelayTimeLogSize(distInfo.getDelayTimeLogSize());
                            dist.setRestartWhenError(distInfo.isRestartReplWhenError());
                            dist.setName(distInfo.getDistDbName());
                            dist.setLocation(new Point(30, 30));
                            dist.setSize(new Dimension(120, 40));
                        }

                        // master component
                        List<MasterInfo> masterList = replicationInfo.getMasterList();
                        MasterNode master = null;
                        for (int i = 0; masterList != null && i < masterList.size(); i++) {
                            MasterInfo masterInfo = masterList.get(i);
                            if (masterInfo != null) {
                                master = new MasterNode();
                                String ip = masterInfo.getMasterIp();
                                String masterDbName = masterInfo.getMasterDbName();
                                String replServerPort = masterInfo.getReplServerPort();
                                boolean isReplAll = masterInfo.isReplAllTable();
                                List<String> tableList = masterInfo.getReplTableList();
                                HostNode mdbHost = new HostNode();
                                mdbHost.setIp(ip);
                                mdbHost.setUserName("admin");
                                mdbHost.setName(ip);
                                mdbHost.setLocation(new Point(x, y));
                                y += mdbHost.getSize().height + hostVertSpan;
                                mdbHost.setParent(diagram);
                                diagram.addNode(mdbHost);

                                master.setDbName(masterDbName);
                                master.setReplServerPort(replServerPort);
                                master.setReplicateAll(isReplAll);
                                master.setReplicatedClassList(tableList);
                                master.setName(masterDbName);
                                master.setLocation(new Point(30, 80));
                                master.setSize(new Dimension(120, 40));
                                master.setParent(mdbHost);
                                mdbHost.addChildNode(master);
                            }
                        }
                        // distributor host component
                        HostNode distdbhost = new HostNode();
                        distdbhost.setIp(server.getHostAddress());
                        distdbhost.setPort(server.getMonPort());
                        distdbhost.setUserName(server.getUserName());
                        distdbhost.setPassword(server.getPassword());
                        distdbhost.setName(server.getHostAddress() + ":" + server.getMonPort());
                        distdbhost.setLocation(new Point(x, y));
                        distdbhost.setParent(diagram);
                        diagram.addNode(distdbhost);
                        // distributor component
                        if (dist != null) {
                            dist.setParent(distdbhost);
                            distdbhost.addChildNode(dist);
                        }
                        // slave component
                        List<SlaveInfo> slaveInfoList = replicationInfo.getSlaveList();
                        SlaveNode slave = null;
                        for (int i = 0; slaveInfoList != null && i < slaveInfoList.size(); i++) {
                            SlaveInfo slaveInfo = slaveInfoList.get(i);
                            if (slaveInfo != null) {
                                slave = new SlaveNode();
                                slave.setDbName(slaveInfo.getSlaveDbName());
                                slave.setDbPath(slaveInfo.getSlaveDbPath());
                                slave.setDbUser(slaveInfo.getDbUser());
                                slave.setDbPassword(slaveInfo.getPassword());
                                ReplicationParamInfo replParaInfo = slaveInfo.getParamInfo();
                                if (replParaInfo != null) {
                                    slave.setParamMap(replParaInfo.getParamMap());
                                }
                                slave.setName(slaveInfo.getSlaveDbName());
                                slave.setLocation(new Point(30, 150));
                                slave.setSize(new Dimension(120, 40));
                                slave.setParent(distdbhost);
                                distdbhost.addChildNode(slave);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @see
     *     org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPartControl(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite
     */
    public void createPartControl(Composite parent) {
        if (isEditable) {
            super.createPartControl(parent);
        } else {
            createGraphicalViewer(parent);
            initConnection();
        }
    }

    /** initialize the connection */
    private void initConnection() {
        if (diagram != null) {
            MasterNode master = null;
            DistributorNode dist = null;
            SlaveNode slave = null;
            List<ContainerNode> containerNodeList = diagram.getChildNodeList();
            for (int i = 0; containerNodeList != null && i < containerNodeList.size(); i++) {
                ContainerNode containerNode = containerNodeList.get(i);
                if (containerNode != null) {
                    List<LeafNode> leafNodeList = containerNode.getChildNodeList();
                    for (int j = 0; j < leafNodeList.size(); j++) {
                        LeafNode leafNode = leafNodeList.get(j);
                        if (leafNode instanceof MasterNode) {
                            master = (MasterNode) leafNode;
                        } else if (leafNode instanceof DistributorNode) {
                            dist = (DistributorNode) leafNode;
                        }
                    }
                }
            }
            // connection from master to dist
            if (master != null && dist != null) {
                new ArrowConnection(master, dist);
            }
            for (int i = 0; containerNodeList != null && i < containerNodeList.size(); i++) {
                ContainerNode containerNode = containerNodeList.get(i);
                if (containerNode != null) {
                    List<LeafNode> leafNodeList = containerNode.getChildNodeList();
                    for (int j = 0; j < leafNodeList.size(); j++) {
                        LeafNode leafNode = leafNodeList.get(j);
                        if (leafNode instanceof SlaveNode) {
                            slave = (SlaveNode) leafNode;
                            // connection from dist to slave
                            if (dist != null && slave != null) {
                                new ArrowConnection(dist, slave);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * set status to dirty.
     *
     * @param isDirty boolean
     */
    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#isDirty()
     * @return <code>true</code> if the contents have been modified and need saving, and <code>false
     *     </code> if they have not changed since the last save
     */
    public boolean isDirty() {
        if (!isEditable) {
            return false;
        }
        return this.isDirty;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
     * @return the palette root
     */
    protected PaletteRoot getPaletteRoot() {
        if (this.paletteRoot == null) {
            this.paletteRoot = PaletteFactory.createPalette();
        }
        return this.paletteRoot;
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
     * @return the palette provider
     */
    protected PaletteViewerProvider createPaletteViewerProvider() {
        return new PaletteViewerProvider(getEditDomain()) {
            protected void configurePaletteViewer(PaletteViewer viewer) {
                super.configurePaletteViewer(viewer);
                viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
            }
        };
    }

    protected FlyoutPreferences getPalettePreferences() {
        return new FlyoutPreferences() {
            public int getDockLocation() {
                return CubridManagerUIPlugin.getDefault()
                        .getPreferenceStore()
                        .getInt(IConstants.PREF_PALETTE_DOCK_LOCATION);
            }

            public int getPaletteState() {
                return CubridManagerUIPlugin.getDefault()
                        .getPreferenceStore()
                        .getInt(IConstants.PREF_PALETTE_STATE);
            }

            public int getPaletteWidth() {
                return CubridManagerUIPlugin.getDefault()
                        .getPreferenceStore()
                        .getInt(IConstants.PREF_PALETTE_SIZE);
            }

            public void setDockLocation(int location) {
                CubridManagerUIPlugin.getDefault()
                        .getPreferenceStore()
                        .setValue(IConstants.PREF_PALETTE_DOCK_LOCATION, location);
            }

            public void setPaletteState(int state) {
                CubridManagerUIPlugin.getDefault()
                        .getPreferenceStore()
                        .setValue(IConstants.PREF_PALETTE_STATE, state);
            }

            public void setPaletteWidth(int width) {
                CubridManagerUIPlugin.getDefault()
                        .getPreferenceStore()
                        .setValue(IConstants.PREF_PALETTE_SIZE, width);
            }
        };
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(java.lang.Class)
     * @param type the adapter class to look up
     * @return a object castable to the given class, or <code>null</code> if this object does not
     *     have an adapter for the given class
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class type) {
        if (type == ZoomManager.class) {
            return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
        }
        return super.getAdapter(type);
    }

    /** @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions() */
    @SuppressWarnings("unchecked")
    protected void createActions() {
        super.createActions();
        ActionRegistry registry = getActionRegistry();

        IAction action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.LEFT);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());

        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.RIGHT);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());

        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.CENTER);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());

        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.MIDDLE);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());

        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.BOTTOM);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());

        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.TOP);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());

        action = new EditAction((IWorkbenchPart) this);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    /** @see org.eclipse.gef.ui.parts.GraphicalEditor#dispose() */
    public void dispose() {
        isDisposed = true;
        super.dispose();
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    /** @see org.eclipse.gef.ui.parts.GraphicalEditor#setFocus() */
    public void setFocus() {
        super.setFocus();
        ICubridNode node = null;
        if (null != this.getEditorInput() && getEditorInput() instanceof ICubridNode) {
            node = (ICubridNode) getEditorInput();
        }
        LayoutManager.getInstance()
                .getTitleLineContrItem()
                .changeTitleForViewOrEditPart(node, this);
        LayoutManager.getInstance()
                .getStatusLineContrItem()
                .changeStuatusLineForViewOrEditPart(node, this);
    }
}
