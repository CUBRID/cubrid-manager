package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RelationshipPropsList",
        propOrder = {})
public class RelationshipPropsList {

    @XmlElement(name = "Name")
    protected RelationshipPropsList.Name name;

    @XmlElement(name = "Relationship_Child_Delete_Rule")
    protected RelationshipPropsList.RelationshipChildDeleteRule relationshipChildDeleteRule;

    @XmlElement(name = "Relationship_Child_Entity")
    protected RelationshipPropsList.RelationshipChildEntity relationshipChildEntity;

    @XmlElement(name = "Relationship_Child_Insert_Rule")
    protected RelationshipPropsList.RelationshipChildInsertRule relationshipChildInsertRule;

    @XmlElement(name = "Relationship_Child_Update_Rule")
    protected RelationshipPropsList.RelationshipChildUpdateRule relationshipChildUpdateRule;

    @XmlElement(name = "Relationship_No_Nulls")
    protected RelationshipPropsList.RelationshipNoNulls relationshipNoNulls;

    @XmlElement(name = "Relationship_Parent_Delete_Rule")
    protected RelationshipPropsList.RelationshipParentDeleteRule relationshipParentDeleteRule;

    @XmlElement(name = "Relationship_Parent_Entity")
    protected RelationshipPropsList.RelationshipParentEntity relationshipParentEntity;

    @XmlElement(name = "Relationship_Parent_Insert_Rule")
    protected RelationshipPropsList.RelationshipParentInsertRule relationshipParentInsertRule;

    @XmlElement(name = "Relationship_Parent_Update_Rule")
    protected RelationshipPropsList.RelationshipParentUpdateRule relationshipParentUpdateRule;

    @XmlElement(name = "Physical_Name")
    protected RelationshipPropsList.PhysicalName physicalName;

    @XmlElement(name = "Relationship_Sequence")
    protected RelationshipPropsList.RelationshipSequence relationshipSequence;

    @XmlElement(name = "Type")
    protected RelationshipPropsList.Type type;

    /** @return possible object is {@link RelationshipPropsList.Name } */
    public RelationshipPropsList.Name getName() {
        return name;
    }

    /** @param value allowed object is {@link RelationshipPropsList.Name } */
    public void setName(RelationshipPropsList.Name value) {
        this.name = value;
    }

    /** @return possible object is {@link RelationshipPropsList.RelationshipNoNulls } */
    public RelationshipPropsList.RelationshipNoNulls getRelationshipNoNulls() {
        return relationshipNoNulls;
    }

    /** @param value allowed object is {@link RelationshipPropsList.RelationshipNoNulls } */
    public void setRelationshipNoNulls(RelationshipPropsList.RelationshipNoNulls value) {
        this.relationshipNoNulls = value;
    }

    /** @return possible object is {@link RelationshipPropsList.PhysicalName } */
    public RelationshipPropsList.PhysicalName getPhysicalName() {
        return physicalName;
    }

    /** @param value allowed object is {@link RelationshipPropsList.PhysicalName } */
    public void setPhysicalName(RelationshipPropsList.PhysicalName value) {
        this.physicalName = value;
    }

    /** @return possible object is {@link RelationshipPropsList.RelationshipSequence } */
    public RelationshipPropsList.RelationshipSequence getRelationshipSequence() {
        return relationshipSequence;
    }

    /** @param value allowed object is {@link RelationshipPropsList.RelationshipSequence } */
    public void setRelationshipSequence(RelationshipPropsList.RelationshipSequence value) {
        this.relationshipSequence = value;
    }

    /** @return possible object is {@link RelationshipPropsList.Type } */
    public RelationshipPropsList.Type getType() {
        return type;
    }

    /** @param value allowed object is {@link RelationshipPropsList.Type } */
    public void setType(RelationshipPropsList.Type value) {
        this.type = value;
    }

    public RelationshipPropsList.RelationshipChildDeleteRule getRelationshipChildDeleteRule() {
        return relationshipChildDeleteRule;
    }

    public void setRelationshipChildDeleteRule(
            RelationshipPropsList.RelationshipChildDeleteRule relationshipChildDeleteRule) {
        this.relationshipChildDeleteRule = relationshipChildDeleteRule;
    }

    public RelationshipPropsList.RelationshipChildInsertRule getRelationshipChildInsertRule() {
        return relationshipChildInsertRule;
    }

    public void setRelationshipChildInsertRule(
            RelationshipPropsList.RelationshipChildInsertRule relationshipChildInsertRule) {
        this.relationshipChildInsertRule = relationshipChildInsertRule;
    }

    public RelationshipPropsList.RelationshipChildUpdateRule getRelationshipChildUpdateRule() {
        return relationshipChildUpdateRule;
    }

    public void setRelationshipChildUpdateRule(
            RelationshipPropsList.RelationshipChildUpdateRule relationshipChildUpdateRule) {
        this.relationshipChildUpdateRule = relationshipChildUpdateRule;
    }

    public RelationshipPropsList.RelationshipParentDeleteRule getRelationshipParentDeleteRule() {
        return relationshipParentDeleteRule;
    }

    public void setRelationshipParentDeleteRule(
            RelationshipPropsList.RelationshipParentDeleteRule relationshipParentDeleteRule) {
        this.relationshipParentDeleteRule = relationshipParentDeleteRule;
    }

    public RelationshipPropsList.RelationshipParentInsertRule getRelationshipParentInsertRule() {
        return relationshipParentInsertRule;
    }

    public void setRelationshipParentInsertRule(
            RelationshipPropsList.RelationshipParentInsertRule relationshipParentInsertRule) {
        this.relationshipParentInsertRule = relationshipParentInsertRule;
    }

    public RelationshipPropsList.RelationshipParentUpdateRule getRelationshipParentUpdateRule() {
        return relationshipParentUpdateRule;
    }

    public void setRelationshipParentUpdateRule(
            RelationshipPropsList.RelationshipParentUpdateRule relationshipParentUpdateRule) {
        this.relationshipParentUpdateRule = relationshipParentUpdateRule;
    }

    public RelationshipPropsList.RelationshipChildEntity getRelationshipChildEntity() {
        return relationshipChildEntity;
    }

    public void setRelationshipChildEntity(
            RelationshipPropsList.RelationshipChildEntity relationshipChildEntity) {
        this.relationshipChildEntity = relationshipChildEntity;
    }

    public RelationshipPropsList.RelationshipParentEntity getRelationshipParentEntity() {
        return relationshipParentEntity;
    }

    public void setRelationshipParentEntity(
            RelationshipPropsList.RelationshipParentEntity relationshipParentEntity) {
        this.relationshipParentEntity = relationshipParentEntity;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"value"})
    public static class RelationshipChildDeleteRule {

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
    public static class RelationshipChildEntity {

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
    public static class RelationshipChildInsertRule {

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
    public static class RelationshipChildUpdateRule {

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
    public static class RelationshipNoNulls {

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
    public static class RelationshipParentDeleteRule {

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
    public static class RelationshipParentEntity {

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
    public static class RelationshipParentInsertRule {

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
    public static class RelationshipParentUpdateRule {

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
    public static class RelationshipSequence {

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
