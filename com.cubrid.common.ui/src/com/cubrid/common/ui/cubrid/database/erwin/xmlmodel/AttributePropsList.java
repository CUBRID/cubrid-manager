package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AttributePropsList",
        propOrder = {})
public class AttributePropsList {

    @XmlElement(name = "Datatype")
    protected AttributePropsList.Datatype dataType;

    @XmlElement(name = "Name")
    protected AttributePropsList.Name name;

    @XmlElement(name = "Attribute_Order")
    protected AttributePropsList.AttributeOrder attributeOrder;

    @XmlElement(name = "Logical_Datatype")
    protected AttributePropsList.LogicalDataType logicalDataType;

    @XmlElement(name = "Null_Option")
    protected AttributePropsList.NullOption nullOption;

    @XmlElement(name = "Order")
    protected AttributePropsList.Order order;

    @XmlElement(name = "Parent_Attribute")
    protected AttributePropsList.ParentAttribute parentAttribute;

    @XmlElement(name = "Parent_Relationship")
    protected AttributePropsList.ParentRelationship parentRelationship;

    @XmlElement(name = "Physical_Name")
    protected AttributePropsList.PhysicalName physicalName;

    @XmlElement(name = "Physical_Order")
    protected AttributePropsList.PhysicalOrder physicalOrder;

    @XmlElement(name = "System_Name")
    protected AttributePropsList.SystemName systemName;

    @XmlElement(name = "Type")
    protected AttributePropsList.Type type;

    @XmlElement(name = "Default")
    protected AttributePropsList.DefaultValue defaultValue;

    public AttributePropsList.Datatype getDataType() {
        return dataType;
    }

    public void setDataType(AttributePropsList.Datatype dataType) {
        this.dataType = dataType;
    }

    /** @return possible object is {@link AttributePropsList.Name } */
    public AttributePropsList.Name getName() {
        return name;
    }

    /** @param value allowed object is {@link AttributePropsList.Name } */
    public void setName(AttributePropsList.Name value) {
        this.name = value;
    }

    /** @return possible object is {@link AttributePropsList.AttributeOrder } */
    public AttributePropsList.AttributeOrder getAttributeOrder() {
        return attributeOrder;
    }

    /** @param value allowed object is {@link AttributePropsList.AttributeOrder } */
    public void setAttributeOrder(AttributePropsList.AttributeOrder value) {
        this.attributeOrder = value;
    }

    /** @return possible object is {@link AttributePropsList.LogicalDataType } */
    public AttributePropsList.LogicalDataType getLogicalDataType() {
        return logicalDataType;
    }

    /** @param value allowed object is {@link AttributePropsList.LogicalDataType } */
    public void setLogicalDataType(AttributePropsList.LogicalDataType value) {
        this.logicalDataType = value;
    }

    /** @return possible object is {@link AttributePropsList.NullOption } */
    public AttributePropsList.NullOption getNullOption() {
        return nullOption;
    }

    /** @param value allowed object is {@link AttributePropsList.NullOption } */
    public void setNullOption(AttributePropsList.NullOption value) {
        this.nullOption = value;
    }

    /** @return possible object is {@link AttributePropsList.OrderNum } */
    public AttributePropsList.Order getOrder() {
        return order;
    }

    /** @param value allowed object is {@link AttributePropsList.OrderNum } */
    public void setOrder(AttributePropsList.Order value) {
        this.order = value;
    }

    /** @return possible object is {@link AttributePropsList.ParentAttribute } */
    public AttributePropsList.ParentAttribute getParentAttribute() {
        return parentAttribute;
    }

    /** @param value allowed object is {@link AttributePropsList.ParentAttribute } */
    public void setParentAttribute(AttributePropsList.ParentAttribute value) {
        this.parentAttribute = value;
    }

    /** @return possible object is {@link AttributePropsList.PhysicalName } */
    public AttributePropsList.PhysicalName getPhysicalName() {
        return physicalName;
    }

    /** @param value allowed object is {@link AttributePropsList.PhysicalName } */
    public void setPhysicalName(AttributePropsList.PhysicalName value) {
        this.physicalName = value;
    }

    /** @return possible object is {@link AttributePropsList.PhysicalOrder } */
    public AttributePropsList.PhysicalOrder getPhysicalOrder() {
        return physicalOrder;
    }

    /** @param value allowed object is {@link AttributePropsList.PhysicalOrder } */
    public void setPhysicalOrder(AttributePropsList.PhysicalOrder value) {
        this.physicalOrder = value;
    }

    /** @return possible object is {@link AttributePropsList.SystemName } */
    public AttributePropsList.SystemName getSystemName() {
        return systemName;
    }

    /** @param value allowed object is {@link AttributePropsList.SystemName } */
    public void setSystemName(AttributePropsList.SystemName value) {
        this.systemName = value;
    }

    /** @return possible object is {@link AttributePropsList.Type } */
    public AttributePropsList.Type getType() {
        return type;
    }

    /** @param value allowed object is {@link AttributePropsList.Type } */
    public void setType(AttributePropsList.Type value) {
        this.type = value;
    }

    public AttributePropsList.ParentRelationship getParentRelationship() {
        return parentRelationship;
    }

    public void setParentRelationship(AttributePropsList.ParentRelationship parentRelationship) {
        this.parentRelationship = parentRelationship;
    }

    /**
     *
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFacetGroup"/>
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFlags"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class AttributeOrder {

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

    /**
     *
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFacetGroup"/>
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFlags"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class LogicalDataType {

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
    public static class NullOption {

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
    public static class Order {

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
    public static class ParentAttribute {

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
    public static class ParentDomainRef {

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
    public static class ParentRelationship {

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
    public static class PhysicalOrder {

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
    public static class SystemName {

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
    public static class Datatype {

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

    public AttributePropsList.DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AttributePropsList.DefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class DefaultValue {

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
