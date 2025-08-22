/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.model;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.er.ERException;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Provides base class support for model objects to participate in event handling framework
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-6 created by Yu Guojia
 */
public abstract class PropertyChangeProvider implements Serializable, Cloneable {
    private static final long serialVersionUID = 2219183610325236471L;
    protected String name;
    public static final String CHILD_CHANGE = "CHILD_CHANGE";
    public static final String REORDER_CHANGE = "REORDER_CHANGE";
    public static final String BOUNDS_CHANGE = "BOUNDS_CHANGE";
    public static final String INPUT_CHANGE = "INPUT_CHANGE";
    public static final String OUTPUT_CHANGE = "OUTPUT_CHANGE";
    public static final String TEXT_CHANGE = "TEXT_CHANGE";
    public static final String LAYOUT_CHANGE = "LAYOUT_CHANGE";
    // just auto layout temporarily
    public static final String AUTO_LAYOUT_TEMP = "AUTO_LAYOUT_TEMP";
    public static final String VIEW_MODEL_CHANGE = "VIEW_MODEL_CHANGE";
    public static final String LOGIC_MODEL = "LOGIC_MODEL";
    public static final String PHYSICAL_MODEL = "PHYSICAL_MODEL";
    public static final String RELATION_MAP_CHANGE = "RELATION_MAP_CHANGE";
    protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    protected PropertyChangeProvider() {}

    public String getName() {
        return name;
    }

    public PropertyChangeProvider clone() throws CloneNotSupportedException {
        return (PropertyChangeProvider) super.clone();
    }

    public void setName(String name) {
        String oldName = this.name;
        if (!name.equals(oldName)) {
            this.name = name;
        }
    }

    protected void modifyNameAndFire(String name) {
        String oldName = this.name;
        if (!name.equals(oldName)) {
            this.name = name;
            firePropertyChange(TEXT_CHANGE, null, name);
        }
    }

    public void checkName() throws ERException {
        if (StringUtil.isEmpty(name)) {
            throw new ERException(Messages.errNoTableName);
        } else if (!ValidateUtil.isValidIdentifier(name)) {
            throw new ERException(Messages.bind(Messages.renameInvalidTableNameMSG, "name", name));
        }
    }

    public abstract CubridDatabase getCubridDatabase();

    public abstract ERSchema getERSchema();

    public abstract void setERSchema(ERSchema erSchema);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    public void firePropertyChange(String prop, Object oldValue, Object newValue) {
        listeners.firePropertyChange(prop, oldValue, newValue);
    }

    protected void fireStructureChange(String prop, Object child) {
        listeners.firePropertyChange(prop, null, child);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        listeners = new PropertyChangeSupport(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        PropertyChangeProvider other = (PropertyChangeProvider) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return 31 + ((name == null) ? 0 : name.hashCode());
    }
}
