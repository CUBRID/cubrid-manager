package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ERwin4",
        propOrder = {})
@XmlRootElement(name = "ERwin4")
public class ERwin4 {

    @XmlElement(name = "Model", required = true)
    protected Model model;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
