package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {})
@XmlRootElement(name = "Key_Group")
public class KeyGroup {

    @XmlElement(name = "Key_GroupProps", required = true)
    protected KeyGroupPropsList keyGroupProps;

    @XmlElement(name = "Key_Group_Member_Groups")
    protected KeyGroup.KeyGroupMemberGroups keyGroupMemberGroups;

    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name;

    /** @return possible object is {@link KeyGroupPropsList } */
    public KeyGroupPropsList getKeyGroupProps() {
        return keyGroupProps;
    }

    /** @param value allowed object is {@link KeyGroupPropsList } */
    public void setKeyGroupProps(KeyGroupPropsList value) {
        this.keyGroupProps = value;
    }

    /** @return possible object is {@link KeyGroup.KeyGroupMemberGroups } */
    public KeyGroup.KeyGroupMemberGroups getKeyGroupMemberGroups() {
        return keyGroupMemberGroups;
    }

    /** @param value allowed object is {@link KeyGroup.KeyGroupMemberGroups } */
    public void setKeyGroupMemberGroups(KeyGroup.KeyGroupMemberGroups value) {
        this.keyGroupMemberGroups = value;
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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"keyGroupMember"})
    public static class KeyGroupMemberGroups {

        @XmlElement(name = "Key_Group_Member")
        protected List<KeyGroupMember> keyGroupMember;

        /**
         * Gets the value of the keyGroupMember property.
         *
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore
         * any modification you make to the returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the keyGroupMember property.
         *
         * <p>For example, to add a new item, do as follows:
         *
         * <pre>
         * getKeyGroupMember().add(newItem);
         * </pre>
         *
         * <p>Objects of the following type(s) are allowed in the list {@link KeyGroupMember }
         */
        public List<KeyGroupMember> getKeyGroupMember() {
            if (keyGroupMember == null) {
                keyGroupMember = new ArrayList<KeyGroupMember>();
            }
            return this.keyGroupMember;
        }
    }
}
