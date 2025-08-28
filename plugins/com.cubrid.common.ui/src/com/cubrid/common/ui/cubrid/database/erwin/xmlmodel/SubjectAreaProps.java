package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {})
@XmlRootElement(name = "Subject_AreaProps")
public class SubjectAreaProps {

    @XmlElement(name = "Name")
    protected SubjectAreaProps.Name name;

    @XmlElement(name = "Referenced_Entities")
    protected List<SubjectAreaProps.ReferencedEntity> referencedEntities;

    public SubjectAreaProps.Name getName() {
        return name;
    }

    public void setName(SubjectAreaProps.Name name) {
        this.name = name;
    }

    public List<ReferencedEntity> getReferencedEntities() {
        if (referencedEntities == null) {
            referencedEntities = new ArrayList<SubjectAreaProps.ReferencedEntity>();
        }
        return referencedEntities;
    }

    public void setReferencedEntities(List<ReferencedEntity> referencedEntities) {
        this.referencedEntities = referencedEntities;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Name {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ReferencedEntity {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
