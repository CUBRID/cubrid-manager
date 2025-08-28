package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "EntityPropsList",
        propOrder = {})
public class EntityPropsList {

    @XmlElement(name = "Name")
    protected EntityPropsList.Name name; // logical table name label in xml

    @XmlElement(name = "Physical_Name")
    protected EntityPropsList.PhysicalName physicalName;

    @XmlElement(name = "Type")
    protected EntityPropsList.Type type;

    public EntityPropsList.Type getType() {
        return type;
    }

    public void setType(EntityPropsList.Type type) {
        this.type = type;
    }

    /** @return possible object is {@link EntityPropsList.Name } */
    public EntityPropsList.Name getName() {
        return name;
    }

    /** @param value allowed object is {@link EntityPropsList.Name } */
    public void setName(EntityPropsList.Name value) {
        this.name = value;
    }

    /** @return possible object is {@link EntityPropsList.PhysicalName } */
    public EntityPropsList.PhysicalName getPhysicalName() {
        return physicalName;
    }

    /** @param value allowed object is {@link EntityPropsList.PhysicalName } */
    public void setPhysicalName(EntityPropsList.PhysicalName value) {
        this.physicalName = value;
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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class PhysicalName {

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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class Type {

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
