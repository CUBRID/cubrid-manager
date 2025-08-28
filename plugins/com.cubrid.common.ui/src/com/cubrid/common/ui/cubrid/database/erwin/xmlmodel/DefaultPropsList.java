package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "DefaultPropsList",
        propOrder = {})
public class DefaultPropsList {

    @XmlElement(name = "Name")
    protected DefaultPropsList.Name name;

    @XmlElement(name = "LogicalDefault_Value")
    protected DefaultPropsList.LogicalDefaultValue logicalDefaultValue;

    @XmlElement(name = "Physical_Name")
    protected DefaultPropsList.PhysicalName physicalName;

    @XmlElement(name = "Server_Value")
    protected DefaultPropsList.ServerValue serverValue;

    /** @return possible object is {@link DefaultPropsList.Name } */
    public DefaultPropsList.Name getName() {
        return name;
    }

    /** @param value allowed object is {@link DefaultPropsList.Name } */
    public void setName(DefaultPropsList.Name value) {
        this.name = value;
    }

    /** @return possible object is {@link DefaultPropsList.LogicalDefaultValue } */
    public DefaultPropsList.LogicalDefaultValue getLogicalDefaultValue() {
        return logicalDefaultValue;
    }

    /** @param value allowed object is {@link DefaultPropsList.LogicalDefaultValue } */
    public void setLogicalDefaultValue(DefaultPropsList.LogicalDefaultValue value) {
        this.logicalDefaultValue = value;
    }

    /** @return possible object is {@link DefaultPropsList.PhysicalName } */
    public DefaultPropsList.PhysicalName getPhysicalName() {
        return physicalName;
    }

    /** @param value allowed object is {@link DefaultPropsList.PhysicalName } */
    public void setPhysicalName(DefaultPropsList.PhysicalName value) {
        this.physicalName = value;
    }

    /** @return possible object is {@link DefaultPropsList.ServerValue } */
    public DefaultPropsList.ServerValue getServerValue() {
        return serverValue;
    }

    /** @param value allowed object is {@link DefaultPropsList.ServerValue } */
    public void setServerValue(DefaultPropsList.ServerValue value) {
        this.serverValue = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class LogicalDefaultValue {

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
    public static class ServerValue {

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
