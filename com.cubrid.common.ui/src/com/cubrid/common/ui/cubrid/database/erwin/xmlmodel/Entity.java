package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import java.util.ArrayList;
import java.util.List;
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
@XmlRootElement(name = "Entity")
public class Entity {
    @XmlElement(name = "EntityProps", required = true)
    protected EntityPropsList entityProps;

    @XmlElement(name = "Attribute_Groups")
    protected Entity.AttributeGroups attributeGroups;

    @XmlElement(name = "Key_Group_Groups")
    protected Entity.KeyGroupGroups keyGroupGroups;

    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name;

    public EntityPropsList getEntityProps() {
        return entityProps;
    }

    public void setEntityProps(EntityPropsList entityProps) {
        this.entityProps = entityProps;
    }

    public Entity.AttributeGroups getAttributeGroups() {
        return attributeGroups;
    }

    public void setAttributeGroups(Entity.AttributeGroups attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    public Entity.KeyGroupGroups getKeyGroupGroups() {
        return keyGroupGroups;
    }

    public void setKeyGroupGroups(Entity.KeyGroupGroups keyGroupGroups) {
        this.keyGroupGroups = keyGroupGroups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"attribute"})
    public static class AttributeGroups {

        @XmlElement(name = "Attribute")
        protected List<Attribute> attribute;

        /**
         * Gets the value of the attribute property.
         *
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore
         * any modification you make to the returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the attribute property.
         *
         * <p>For example, to add a new item, do as follows:
         *
         * <pre>
         * getAttribute().add(newItem);
         * </pre>
         *
         * <p>Objects of the following type(s) are allowed in the list {@link Attribute }
         */
        public List<Attribute> getAttribute() {
            if (attribute == null) {
                attribute = new ArrayList<Attribute>();
            }
            return this.attribute;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"keyGroup"})
    public static class KeyGroupGroups {
        @XmlElement(name = "Key_Group")
        protected List<KeyGroup> keyGroup;

        /**
         * Gets the value of the keyGroup property.
         *
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore
         * any modification you make to the returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the keyGroup property.
         *
         * <p>For example, to add a new item, do as follows:
         *
         * <pre>
         * getKeyGroup().add(newItem);
         * </pre>
         *
         * <p>Objects of the following type(s) are allowed in the list {@link KeyGroup }
         */
        public List<KeyGroup> getKeyGroup() {
            if (keyGroup == null) {
                keyGroup = new ArrayList<KeyGroup>();
            }
            return this.keyGroup;
        }
    }
}
