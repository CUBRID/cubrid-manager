package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Drawing_Object_Entity")
public class DrawingObjectEntity {

    @XmlAttribute(name = "id")
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name;

    @XmlElement(name = "Drawing_Object_EntityProps")
    protected DrawingObjectEntityProps drawingObjectEntityProps;

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

    public DrawingObjectEntityProps getDrawingObjectEntityProps() {
        return drawingObjectEntityProps;
    }

    public void setDrawingObjectEntityProps(DrawingObjectEntityProps drawingObjectEntityProps) {
        this.drawingObjectEntityProps = drawingObjectEntityProps;
    }
}
