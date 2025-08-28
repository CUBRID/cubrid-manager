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
@XmlRootElement(name = "Model")
public class Model {

    @XmlElement(name = "Entity_Groups")
    protected Model.EntityGroups entityGroups;

    @XmlElement(name = "Relationship_Groups")
    protected Model.RelationshipGroups relationshipGroups;

    @XmlElement(name = "Default_Value_Groups")
    protected Model.DefaultValueGroups defaultValueGroups;

    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name;

    @XmlAttribute(name = "TargetServer")
    protected String targetServer;

    @XmlAttribute(name = "DBMSVersion")
    protected String dbmsVersion;

    @XmlElement(name = "ModelProps")
    protected Model.ModelProps modelProps;

    @XmlElement(name = "Subject_Area_Groups")
    protected Model.SubjectAreaGroups subjectAreaGroups;

    public Model.SubjectAreaGroups getSubjectAreaGroups() {
        return subjectAreaGroups;
    }

    public void setSubjectAreaGroups(Model.SubjectAreaGroups subjectAreaGroups) {
        this.subjectAreaGroups = subjectAreaGroups;
    }

    public Model.ModelProps getModelProps() {
        return modelProps;
    }

    public void setModelProps(Model.ModelProps modelProps) {
        this.modelProps = modelProps;
    }

    public String getDbmsVersion() {
        return dbmsVersion;
    }

    public void setDbmsVersion(String dbmsVersion) {
        this.dbmsVersion = dbmsVersion;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }

    /** @return possible object is {@link Model.EntityGroups } */
    public Model.EntityGroups getEntityGroups() {
        return entityGroups;
    }

    /** @param value allowed object is {@link Model.EntityGroups } */
    public void setEntityGroups(Model.EntityGroups value) {
        this.entityGroups = value;
    }

    /** @return the relationshipGroups */
    public Model.RelationshipGroups getRelationshipGroups() {
        return relationshipGroups;
    }

    /** @param relationshipGroups the relationshipGroups to set */
    public void setRelationshipGroups(Model.RelationshipGroups relationshipGroups) {
        this.relationshipGroups = relationshipGroups;
    }

    /** @return the defaultValueGroups */
    public Model.DefaultValueGroups getDefaultValueGroups() {
        return defaultValueGroups;
    }

    /** @param defaultValueGroups the defaultValueGroups to set */
    public void setDefaultValueGroups(Model.DefaultValueGroups defaultValueGroups) {
        this.defaultValueGroups = defaultValueGroups;
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /** @param id the id to set */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"entity"})
    public static class EntityGroups {

        @XmlElement(name = "Entity")
        protected List<Entity> entity;

        /**
         * Gets the value of the entity property.
         *
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore
         * any modification you make to the returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entity property.
         *
         * <p>For example, to add a new item, do as follows:
         *
         * <pre>
         * getEntity().add(newItem);
         * </pre>
         *
         * <p>Objects of the following type(s) are allowed in the list {@link Entity }
         */
        public List<Entity> getEntity() {
            if (entity == null) {
                entity = new ArrayList<Entity>();
            }
            return this.entity;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"relationship"})
    public static class RelationshipGroups {

        @XmlElement(name = "Relationship")
        protected List<Relationship> relationship;

        public List<Relationship> getRelationship() {
            if (relationship == null) {
                relationship = new ArrayList<Relationship>();
            }
            return this.relationship;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"defaultValue"})
    public static class DefaultValueGroups {

        @XmlElement(name = "Default_Value")
        protected List<Default> defaultValue;

        public List<Default> getDefault() {
            if (defaultValue == null) {
                defaultValue = new ArrayList<Default>();
            }
            return this.defaultValue;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"targetServer", "dbmsVersion"})
    public static class ModelProps {

        @XmlElement(name = "Target_Server")
        protected TargetServer targetServer;

        @XmlElement(name = "DBMS_Version")
        protected DBMSVersion dbmsVersion;

        public TargetServer getTargetServer() {
            return targetServer;
        }

        public void setTargetServer(TargetServer targetServer) {
            this.targetServer = targetServer;
        }

        public DBMSVersion getDbmsVersion() {
            return dbmsVersion;
        }

        public void setDbmsVersion(DBMSVersion dbmsVersion) {
            this.dbmsVersion = dbmsVersion;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"subjectAreaList"})
    public static class SubjectAreaGroups {
        @XmlElement(name = "Subject_Area")
        protected List<SubjectArea> subjectAreaList;

        public List<SubjectArea> getSubjectAreaList() {
            if (subjectAreaList == null) {
                subjectAreaList = new ArrayList<SubjectArea>();
            }
            return subjectAreaList;
        }

        public void setSubjectAreaList(List<SubjectArea> subjectAreaList) {
            this.subjectAreaList = subjectAreaList;
        }
    }
}
