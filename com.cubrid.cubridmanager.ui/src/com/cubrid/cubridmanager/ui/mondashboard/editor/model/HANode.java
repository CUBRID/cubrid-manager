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
package com.cubrid.cubridmanager.ui.mondashboard.editor.model;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.common.model.PropertyChangeProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * The node model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class HANode extends PropertyChangeProvider {

    private static final int PEREFERENCE_MAX_ERROR = 10;

    public static final String PROP_LOCATION = "PROP_LOCATION";
    public static final String PROP_SIZE = "PROP_SIZE";
    public static final String PROP_NAME = "PROP_NAME";
    public static final String PROP_ADD_INPUTS = "PROP_ADD_INPUTS";
    public static final String PROP_ADD_OUTPUTS = "PROP_ADD_OUTPUTS";
    public static final String PROP_REMOVE_INPUTS = "PROP_REMOVE_INPUTS";
    public static final String PROP_REMOVE_OUTPUTS = "PROP_REMOVE_OUTPUTS";

    protected Point location = new Point(0, 0);
    protected Dimension size = new Dimension(122, 118);
    protected String name = "";

    protected final List<HANodeConnection> outputs = new ArrayList<HANodeConnection>();
    protected final List<HANodeConnection> inputs = new ArrayList<HANodeConnection>();
    private boolean isConnecting = false;

    private final List<String> errorMsg = new ArrayList<String>(PEREFERENCE_MAX_ERROR);

    private String lastErrorMsg = "";

    /**
     * add connection to inputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void addInput(HANodeConnection connection) {
        if (!inputs.contains(connection)) {
            this.inputs.add(connection);
            fireStructureChange(PROP_ADD_INPUTS, connection);
        }
    }

    /**
     * add connection to outputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void addOutput(HANodeConnection connection) {
        if (!outputs.contains(connection)) {
            this.outputs.add(connection);
            fireStructureChange(PROP_ADD_OUTPUTS, connection);
        }
    }

    public List<HANodeConnection> getIncomingConnections() {
        return this.inputs;
    }

    public List<HANodeConnection> getOutgoingConnections() {
        return this.outputs;
    }

    /**
     * remove connection to inputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void removeInput(HANodeConnection connection) {
        if (this.inputs.remove(connection)) {
            fireStructureChange(PROP_REMOVE_INPUTS, connection);
        }
    }

    /**
     * remove connection from outputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void removeOutput(HANodeConnection connection) {
        if (this.outputs.remove(connection)) {
            fireStructureChange(PROP_REMOVE_OUTPUTS, connection);
        }
    }

    public String getName() {
        return name;
    }

    /**
     * set the name
     *
     * @param name String
     */
    public void setName(String name) {
        if (this.name.equals(name)) {
            return;
        }
        String old = this.name;
        this.name = name;
        firePropertyChange(PROP_NAME, old, name);
    }

    /**
     * set the location
     *
     * @param point Point
     */
    public void setLocation(Point point) {
        if (this.location.equals(point)) {
            return;
        }
        Point old = this.location.getCopy();
        this.location = point;
        firePropertyChange(PROP_LOCATION, old, point);
    }

    public Point getLocation() {
        return location;
    }

    /**
     * set the size
     *
     * @param dimension Dimension
     */
    public void setSize(Dimension dimension) {
        if (this.size.equals(dimension)) {
            return;
        }
        Dimension old = this.size.getCopy();
        this.size = dimension;
        firePropertyChange(PROP_SIZE, old, dimension);
    }

    public Dimension getSize() {
        return size;
    }

    /** remove all input and output connections. */
    public void removeAllInputsAndOutputs() {
        List<HANodeConnection> tempList = new ArrayList<HANodeConnection>();
        tempList.addAll(outputs);
        tempList.addAll(inputs);
        for (HANodeConnection conn : tempList) {
            conn.setSource(null);
            conn.setTarget(null);
        }
    }

    /**
     * Remove connections that Target Not In the parameter List
     *
     * @param targets List<HANode>
     * @param targetClazz Class<?>
     */
    public void removeConnectionsTargetNotInList(List<?> targets, Class<?> targetClazz) {
        HANodeConnection[] oldConnections =
                getOutgoingConnections().toArray(new HANodeConnection[] {});
        for (HANodeConnection conn : oldConnections) {
            if (targetClazz.equals(conn.getTarget().getClass())
                    && !targets.contains(conn.getTarget())) {
                conn.setSource(null);
                conn.setTarget(null);
            }
        }
    }

    /**
     * Remove connections that Source Not In the parameter List
     *
     * @param sources List<HANode>
     * @param sourceClazz Class<?>
     */
    public void removeConnectionsSourceNotInList(List<?> sources, Class<?> sourceClazz) {
        HANodeConnection[] oldConnections =
                getIncomingConnections().toArray(new HANodeConnection[] {});
        for (HANodeConnection conn : oldConnections) {
            if (sourceClazz.equals(conn.getSource().getClass())
                    && !sources.contains(conn.getSource())) {
                conn.setSource(null);
                conn.setTarget(null);
            }
        }
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnecting(boolean isConnecting) {
        this.isConnecting = isConnecting;
    }

    /**
     * Get error message from task.
     *
     * @return the errorMsg
     */
    public String getErrorMsg() {
        if (errorMsg.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (String emsg : errorMsg) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(emsg);
        }
        return sb.toString();
    }

    /** @param errorMsg the errorMsg to set */
    public void setErrorMsg(String errorMsg) {
        // max size is 10;
        synchronized (this.errorMsg) {
            lastErrorMsg = errorMsg;
            if (StringUtil.isEmpty(errorMsg)) {
                return;
            }
            String time = DateUtil.getDatetimeString(new Date().getTime(), "yyyy:MM:dd HH:mm:ss :");
            StringBuffer sb = new StringBuffer();
            sb.append(time).append("\n").append(errorMsg);
            if (this.errorMsg.size() == PEREFERENCE_MAX_ERROR) {
                this.errorMsg.remove(0);
            }
            this.errorMsg.add(sb.toString());
        }
    }

    /**
     * Has new error messages.
     *
     * @return true:has.
     */
    public boolean hasNewErrorMsg() {
        return StringUtil.isNotEmpty(lastErrorMsg);
    }

    /** Clear the error messages. */
    public void clearErrorMessages() {
        synchronized (errorMsg) {
            lastErrorMsg = "";
            errorMsg.clear();
        }
    }

    public String getLastErrorMsg() {
        return lastErrorMsg;
    }

    /**
     * If the target object in outputs is exists.
     *
     * @param target HANode
     * @return If the target object in outputs is exists.
     */
    public boolean targetExists(HANode target) {
        for (HANodeConnection conn : this.outputs) {
            if (conn.getTarget().equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If the source object in inputs is exists.
     *
     * @param source HANode
     * @return If the target object in outputs is exists.
     */
    public boolean sourceExists(HANode source) {
        for (HANodeConnection conn : this.inputs) {
            if (conn.getSource().equals(source)) {
                return true;
            }
        }
        return false;
    }
}
