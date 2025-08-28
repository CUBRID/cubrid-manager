package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Key_GroupPropsList",
        propOrder = {})
public class KeyGroupPropsList {

    @XmlElement(name = "Key_Group_Type")
    protected KeyGroupPropsList.KeyGroupType keyGroupType;

    @XmlElement(name = "Key_Group_Relationship_Pointer")
    protected KeyGroupPropsList.KeyGroupRelationPointer keyGroupRelationPointer;

    /** @return possible object is {@link KeyGroupPropsList.KeyGroupType } */
    public KeyGroupPropsList.KeyGroupType getKeyGroupType() {
        return keyGroupType;
    }

    /** @param value allowed object is {@link KeyGroupPropsList.KeyGroupType } */
    public void setKeyGroupType(KeyGroupPropsList.KeyGroupType value) {
        this.keyGroupType = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class KeyGroupType {

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

    public KeyGroupPropsList.KeyGroupRelationPointer getKeyGroupRelationPointer() {
        return keyGroupRelationPointer;
    }

    public void setKeyGroupRelationPointer(
            KeyGroupPropsList.KeyGroupRelationPointer keyGroupRelationPointer) {
        this.keyGroupRelationPointer = keyGroupRelationPointer;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class KeyGroupRelationPointer {

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
