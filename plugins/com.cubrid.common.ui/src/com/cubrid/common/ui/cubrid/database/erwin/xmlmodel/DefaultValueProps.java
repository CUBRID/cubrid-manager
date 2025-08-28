package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import com.cubrid.common.ui.cubrid.database.erwin.xmlmodel.DefaultPropsList.ServerValue;
import jakarta.xml.bind.annotation.XmlElement;

public class DefaultValueProps {

    @XmlElement(name = "Server_Value")
    protected ServerValue serverValue;

    public ServerValue getServerValue() {
        return serverValue;
    }

    public void setServerValue(ServerValue serverValue) {
        this.serverValue = serverValue;
    }
}
