package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Drawing_Object_RelationshipProps")
public class DrawingObjectRelationshipProps {
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DOUserControledPath {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DORelationshipPath {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XmlElement(name = "DO_Reference_Object")
    protected DOReferenceObject doReferenceObject;

    @XmlElement(name = "DO_Relationship_Path")
    protected List<DrawingObjectRelationshipProps.DORelationshipPath> doRelationshipPathList;

    @XmlElement(name = "DO_User_Controled_Path")
    protected DrawingObjectRelationshipProps.DOUserControledPath doUserControledPath;

    public DrawingObjectRelationshipProps.DOUserControledPath getDoUserControledPath() {
        return doUserControledPath;
    }

    public void setDoUserControledPath(
            DrawingObjectRelationshipProps.DOUserControledPath doUserControledPath) {
        this.doUserControledPath = doUserControledPath;
    }

    public List<DrawingObjectRelationshipProps.DORelationshipPath> getDoRelationshipPathList() {
        if (doRelationshipPathList == null) {
            doRelationshipPathList =
                    new ArrayList<DrawingObjectRelationshipProps.DORelationshipPath>();
        }
        return doRelationshipPathList;
    }

    public void setDoRelationshipPathList(
            List<DrawingObjectRelationshipProps.DORelationshipPath> doRelationshipPathList) {
        this.doRelationshipPathList = doRelationshipPathList;
    }

    public DOReferenceObject getDoReferenceObject() {
        return doReferenceObject;
    }

    public void setDoReferenceObject(DOReferenceObject doReferenceObject) {
        this.doReferenceObject = doReferenceObject;
    }
}
