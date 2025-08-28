package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Parent_RelationshipPropsList",
        propOrder = {})
public class ParentRelationshipPropsList {

    @XmlElement(name = "Name")
    protected ParentRelationshipPropsList.Name name;

    /** @return possible object is {@link ParentRelationshipPropsList.Name } */
    public ParentRelationshipPropsList.Name getName() {
        return name;
    }

    /** @param value allowed object is {@link ParentRelationshipPropsList.Name } */
    public void setName(ParentRelationshipPropsList.Name value) {
        this.name = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class Name {

        @XmlValue protected String value;

        /** @return possible object is {@link String } */
        public String getValue() {
            return value;
        }

        /** @param value allowed object is {@link String } */
        public void setValue(String value) {
            this.value = value;
        }
    }
}
