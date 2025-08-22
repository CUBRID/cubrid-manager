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
@XmlRootElement(name = "Stored_Display")
@XmlType(
        name = "",
        propOrder = {})
public class StoredDisplay {

    @XmlAttribute(name = "id")
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name;

    @XmlElement(name = "Drawing_Object_Entity_Groups")
    protected StoredDisplay.DrawingObjectEntityGroups drawingObjectEntityGroups;

    @XmlElement(name = "Drawing_Object_Relationship_Groups")
    protected StoredDisplay.DrawingObjectRelationshipGroups drawingObjectRelationshipGroups;

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

    public StoredDisplay.DrawingObjectEntityGroups getDrawingObjectEntityGroups() {
        return drawingObjectEntityGroups;
    }

    public void setDrawingObjectEntityGroups(
            StoredDisplay.DrawingObjectEntityGroups drawingObjectEntityGroups) {
        this.drawingObjectEntityGroups = drawingObjectEntityGroups;
    }

    public StoredDisplay.DrawingObjectRelationshipGroups getDrawingObjectRelationshipGroups() {
        return drawingObjectRelationshipGroups;
    }

    public void setDrawingObjectRelationshipGroups(
            StoredDisplay.DrawingObjectRelationshipGroups drawingObjectRelationshipGroups) {
        this.drawingObjectRelationshipGroups = drawingObjectRelationshipGroups;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DrawingObjectEntityGroups {

        @XmlElement(name = "Drawing_Object_Entity")
        protected List<DrawingObjectEntity> drawingObjectEntity;

        public List<DrawingObjectEntity> getDrawingObjectEntity() {
            if (drawingObjectEntity == null) {
                drawingObjectEntity = new ArrayList<DrawingObjectEntity>();
            }
            return drawingObjectEntity;
        }

        public void setDrawingObjectEntity(List<DrawingObjectEntity> drawingObjectEntity) {
            this.drawingObjectEntity = drawingObjectEntity;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DrawingObjectRelationshipGroups {

        @XmlElement(name = "Drawing_Object_Relationship")
        protected List<DrawingObjectRelation> drawingObjectRelationshipEntity;

        public List<DrawingObjectRelation> getDrawingObjectRelationshipEntity() {
            if (drawingObjectRelationshipEntity == null) {
                drawingObjectRelationshipEntity = new ArrayList<DrawingObjectRelation>();
            }
            return drawingObjectRelationshipEntity;
        }

        public void setDrawingObjectRelationshipEntity(
                List<DrawingObjectRelation> drawingObjectRelationshipEntity) {
            this.drawingObjectRelationshipEntity = drawingObjectRelationshipEntity;
        }
    }
}
