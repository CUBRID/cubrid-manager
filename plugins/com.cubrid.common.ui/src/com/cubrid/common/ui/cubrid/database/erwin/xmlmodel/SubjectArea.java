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
@XmlRootElement(name = "Subject_Area")
public class SubjectArea {

    @XmlAttribute(name = "id")
    protected String id;

    @XmlAttribute(name = "Name")
    protected String name;

    @XmlElement(name = "Subject_AreaProps")
    protected SubjectAreaProps subjectAreaProps;

    @XmlElement(name = "Stored_Display_Groups")
    protected SubjectArea.StoredDisplayGroups storedDisplayGroups;

    public SubjectArea.StoredDisplayGroups getStoredDisplayGroups() {
        return storedDisplayGroups;
    }

    public void setStoredDisplayGroups(SubjectArea.StoredDisplayGroups storedDisplayGroups) {
        this.storedDisplayGroups = storedDisplayGroups;
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

    public SubjectAreaProps getSubjectAreaProps() {
        return subjectAreaProps;
    }

    public void setSubjectAreaProps(SubjectAreaProps subjectAreaProps) {
        this.subjectAreaProps = subjectAreaProps;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"storedDisplay"})
    public static class StoredDisplayGroups {

        @XmlElement(name = "Stored_Display")
        protected List<StoredDisplay> storedDisplay;

        public List<StoredDisplay> getStoredDisplay() {
            if (storedDisplay == null) {
                storedDisplay = new ArrayList<StoredDisplay>();
            }
            return storedDisplay;
        }

        public void setStoredDisplay(List<StoredDisplay> storedDisplay) {
            this.storedDisplay = storedDisplay;
        }
    }
}
