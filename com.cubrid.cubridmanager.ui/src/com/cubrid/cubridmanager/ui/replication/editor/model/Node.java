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
package com.cubrid.cubridmanager.ui.replication.editor.model;

import com.cubrid.cubridmanager.core.common.model.PropertyChangeProvider;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * The node model object
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class Node extends PropertyChangeProvider {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3906932274669630514L;

    public static final String PROP_LOCATION = "PROP_LOCATION";
    public static final String PROP_SIZE = "PROP_SIZE";
    public static final String PROP_NAME = "PROP_NAME";
    public static final String PROP_INPUTS = "PROP_INPUTS";
    public static final String PROP_OUTPUTS = "PROP_OUTPUTS";
    protected Point location = new Point(0, 0);
    protected Dimension size = new Dimension(100, 150);
    protected String name = "Node";
    protected List<ArrowConnection> outputs = new ArrayList<ArrowConnection>();
    protected List<ArrowConnection> inputs = new ArrayList<ArrowConnection>();
    protected PropertyChangeProvider parentNode = null;

    public PropertyChangeProvider getParent() {
        return parentNode;
    }

    public void setParent(PropertyChangeProvider parent) {
        parentNode = parent;
    }

    /**
     * add connection to inputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void addInput(ArrowConnection connection) {
        this.inputs.add(connection);
        fireStructureChange(PROP_INPUTS, connection);
    }

    /**
     * add connection to outputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void addOutput(ArrowConnection connection) {
        this.outputs.add(connection);
        fireStructureChange(PROP_OUTPUTS, connection);
    }

    public List<ArrowConnection> getIncomingConnections() {
        return this.inputs;
    }

    public List<ArrowConnection> getOutgoingConnections() {
        return this.outputs;
    }

    /**
     * remove connection to inputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void removeInput(ArrowConnection connection) {
        this.inputs.remove(connection);
        fireStructureChange(PROP_INPUTS, connection);
    }

    /**
     * remove connection from outputs & execute method of fireStructureChange()
     *
     * @param connection ArrowConnection
     */
    public void removeOutput(ArrowConnection connection) {
        this.outputs.remove(connection);
        fireStructureChange(PROP_OUTPUTS, connection);
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
        this.name = name;
        firePropertyChange(PROP_NAME, null, name);
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
        this.location = point;
        firePropertyChange(PROP_LOCATION, null, point);
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
        this.size = dimension;
        firePropertyChange(PROP_SIZE, null, dimension);
    }

    public Dimension getSize() {
        return size;
    }

    /**
     * @see com.cubrid.cubridmanager.ui.replication.editor.model.Node#isValid()
     * @return boolean
     */
    public boolean isValid() {
        return true;
    }
}
