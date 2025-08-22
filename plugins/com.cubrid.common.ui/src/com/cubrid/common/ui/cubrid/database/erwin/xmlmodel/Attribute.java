package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {})
@XmlRootElement(name = "Attribute")
public class Attribute {

    @XmlElement(name = "AttributeProps", required = true)
    protected AttributePropsList attributeProps;

    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name; // logical name

    /** @return possible object is {@link AttributePropsList } */
    public AttributePropsList getAttributeProps() {
        return attributeProps;
    }

    /** @param value allowed object is {@link AttributePropsList } */
    public void setAttributeProps(AttributePropsList value) {
        this.attributeProps = value;
    }

    /** @return possible object is {@link String } */
    public String getId() {
        return id;
    }

    /** @param value allowed object is {@link String } */
    public void setId(String value) {
        this.id = value;
    }

    /** @return possible object is {@link String } */
    public String getName() {
        return name;
    }

    /** @param value allowed object is {@link String } */
    public void setName(String value) {
        this.name = value;
    }
}
