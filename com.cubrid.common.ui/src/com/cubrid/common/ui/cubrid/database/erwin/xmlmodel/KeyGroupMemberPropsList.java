package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Key_Group_MemberPropsList",
        propOrder = {})
public class KeyGroupMemberPropsList {

    @XmlElement(name = "Name")
    protected KeyGroupMemberPropsList.Name name;

    @XmlElement(name = "Expression")
    protected KeyGroupMemberPropsList.Expression expression;

    @XmlElement(name = "Key_Group_Member_Column")
    protected KeyGroupMemberPropsList.KeyGroupMemberColumn keyGroupMemberColumn;

    @XmlElement(name = "Key_Group_Position")
    protected KeyGroupMemberPropsList.KeyGroupPosition keyGroupPosition;

    @XmlElement(name = "Key_Group_Sort_Order")
    protected KeyGroupMemberPropsList.KeyGroupSortOrder keyGroupSortOrder;

    @XmlElement(name = "Physical_Name")
    protected KeyGroupMemberPropsList.PhysicalName physicalName;

    /** @return possible object is {@link KeyGroupMemberPropsList.Name } */
    public KeyGroupMemberPropsList.Name getName() {
        return name;
    }

    /** @param value allowed object is {@link KeyGroupMemberPropsList.Name } */
    public void setName(KeyGroupMemberPropsList.Name value) {
        this.name = value;
    }

    /** @return possible object is {@link KeyGroupMemberPropsList.Expression } */
    public KeyGroupMemberPropsList.Expression getExpression() {
        return expression;
    }

    /** @param value allowed object is {@link KeyGroupMemberPropsList.Expression } */
    public void setExpression(KeyGroupMemberPropsList.Expression value) {
        this.expression = value;
    }

    /** @return possible object is {@link KeyGroupMemberPropsList.KeyGroupPosition } */
    public KeyGroupMemberPropsList.KeyGroupPosition getKeyGroupPosition() {
        return keyGroupPosition;
    }

    /** @param value allowed object is {@link KeyGroupMemberPropsList.KeyGroupPosition } */
    public void setKeyGroupPosition(KeyGroupMemberPropsList.KeyGroupPosition value) {
        this.keyGroupPosition = value;
    }

    /** @return possible object is {@link KeyGroupMemberPropsList.KeyGroupSortOrder } */
    public KeyGroupMemberPropsList.KeyGroupSortOrder getKeyGroupSortOrder() {
        return keyGroupSortOrder;
    }

    /** @param value allowed object is {@link KeyGroupMemberPropsList.KeyGroupSortOrder } */
    public void setKeyGroupSortOrder(KeyGroupMemberPropsList.KeyGroupSortOrder value) {
        this.keyGroupSortOrder = value;
    }

    /** @return possible object is {@link KeyGroupMemberPropsList.PhysicalName } */
    public KeyGroupMemberPropsList.PhysicalName getPhysicalName() {
        return physicalName;
    }

    /** @param value allowed object is {@link KeyGroupMemberPropsList.PhysicalName } */
    public void setPhysicalName(KeyGroupMemberPropsList.PhysicalName value) {
        this.physicalName = value;
    }

    /**
     *
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFlags"/>
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFacetGroup"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class Expression {

        @XmlValue protected String value;

        @XmlAttribute(name = "Tool")
        protected String tool;

        @XmlAttribute(name = "ReadOnly")
        protected String readOnly;

        @XmlAttribute(name = "Derived")
        protected String derived;

        @XmlAttribute(name = "Optional")
        protected String optional;

        @XmlAttribute(name = "NullValue")
        protected String nullValue;

        @XmlAttribute(name = "HandleNonPrintableChar")
        protected String handleNonPrintableChar;

        @XmlAttribute(name = "Hardened")
        protected String hardened;

        @XmlAttribute(name = "AutoCalculated")
        protected String autoCalculated;

        /** @return possible object is {@link String } */
        public String getValue() {
            return value;
        }

        /** @param value allowed object is {@link String } */
        public void setValue(String value) {
            this.value = value;
        }

        /** @return possible object is {@link String } */
        public String getTool() {
            if (tool == null) {
                return "Y";
            } else {
                return tool;
            }
        }

        /** @param value allowed object is {@link String } */
        public void setTool(String value) {
            this.tool = value;
        }

        /** @return possible object is {@link String } */
        public String getReadOnly() {
            if (readOnly == null) {
                return "Y";
            } else {
                return readOnly;
            }
        }

        /** @param value allowed object is {@link String } */
        public void setReadOnly(String value) {
            this.readOnly = value;
        }

        /** @return possible object is {@link String } */
        public String getDerived() {
            if (derived == null) {
                return "Y";
            } else {
                return derived;
            }
        }

        /** @param value allowed object is {@link String } */
        public void setDerived(String value) {
            this.derived = value;
        }

        /** @return possible object is {@link String } */
        public String getOptional() {
            if (optional == null) {
                return "Y";
            } else {
                return optional;
            }
        }

        /** @param value allowed object is {@link String } */
        public void setOptional(String value) {
            this.optional = value;
        }

        /** @return possible object is {@link String } */
        public String getNullValue() {
            if (nullValue == null) {
                return "Y";
            } else {
                return nullValue;
            }
        }

        /** @param value allowed object is {@link String } */
        public void setNullValue(String value) {
            this.nullValue = value;
        }

        /** @return possible object is {@link String } */
        public String getHandleNonPrintableChar() {
            return handleNonPrintableChar;
        }

        /** @param value allowed object is {@link String } */
        public void setHandleNonPrintableChar(String value) {
            this.handleNonPrintableChar = value;
        }

        /** @return possible object is {@link String } */
        public String getHardened() {
            return hardened;
        }

        /** @param value allowed object is {@link String } */
        public void setHardened(String value) {
            this.hardened = value;
        }

        /** @return possible object is {@link String } */
        public String getAutoCalculated() {
            return autoCalculated;
        }

        /** @param value allowed object is {@link String } */
        public void setAutoCalculated(String value) {
            this.autoCalculated = value;
        }
    }

    public KeyGroupMemberPropsList.KeyGroupMemberColumn getKeyGroupMemberColumn() {
        return keyGroupMemberColumn;
    }

    public void setKeyGroupMemberColumn(
            KeyGroupMemberPropsList.KeyGroupMemberColumn keyGroupMemberColumn) {
        this.keyGroupMemberColumn = keyGroupMemberColumn;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class KeyGroupMemberColumn {

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
    public static class KeyGroupPosition {

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
    public static class KeyGroupSortOrder {

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
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFlags"/>
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFacetGroup"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     */
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

    /**
     *
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFlags"/>
     *       &lt;attGroup ref="{http://www.ca.com/erwin/data}PropertyFacetGroup"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     */
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
}
