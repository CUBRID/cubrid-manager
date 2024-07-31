package com.cubrid.common.ui.cubrid.database.erwin.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {})
@XmlRootElement(name = "Drawing_Object_EntityProps")
public class DrawingObjectEntityProps {
    @XmlElement(name = "DO_Location")
    protected DrawingObjectEntityProps.DOLocation dolocation;

    @XmlElement(name = "DO_Reference_Object")
    protected DOReferenceObject doReferenceObject;

    @XmlElement(name = "DO_Entity_Width_AutoResizeable")
    protected DrawingObjectEntityProps.DOEntityWidthAutoResizeable doEntityWidthAutoResizeable;

    @XmlElement(name = "DO_Entity_Height_AutoResizeable")
    protected DrawingObjectEntityProps.DOEntityHeightAutoResizeable doEntityHeightAutoResizeable;

    public DOLocation getDolocation() {
        return dolocation;
    }

    public void setDolocation(DOLocation dolocation) {
        this.dolocation = dolocation;
    }

    public DOReferenceObject getDoReferenceObject() {
        return doReferenceObject;
    }

    public void setDoReferenceObject(DOReferenceObject doReferenceObject) {
        this.doReferenceObject = doReferenceObject;
    }

    public DrawingObjectEntityProps.DOEntityWidthAutoResizeable getDoEntityWidthAutoResizeable() {
        return doEntityWidthAutoResizeable;
    }

    public void setDoEntityWidthAutoResizeable(
            DrawingObjectEntityProps.DOEntityWidthAutoResizeable doEntityWidthAutoResizeable) {
        this.doEntityWidthAutoResizeable = doEntityWidthAutoResizeable;
    }

    public DrawingObjectEntityProps.DOEntityHeightAutoResizeable getDoEntityHeightAutoResizeable() {
        return doEntityHeightAutoResizeable;
    }

    public void setDoEntityHeightAutoResizeable(
            DrawingObjectEntityProps.DOEntityHeightAutoResizeable doEntityHeightAutoResizeable) {
        this.doEntityHeightAutoResizeable = doEntityHeightAutoResizeable;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DOLocation {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DOEntityHeightAutoResizeable {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DOEntityWidthAutoResizeable {
        @XmlValue protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
