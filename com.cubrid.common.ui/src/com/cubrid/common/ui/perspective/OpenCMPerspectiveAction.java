package com.cubrid.common.ui.perspective;

import org.eclipse.jface.resource.ImageDescriptor;

public class OpenCMPerspectiveAction extends AbsOpenPerspectiveAction {
    public static final String ID = OpenCMPerspectiveAction.class.getName();

    public OpenCMPerspectiveAction(String text, ImageDescriptor image) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
    }

    protected String getTargetId() {
        return IPerspectiveConstance.CM_PERSPECTIVE_ID;
    }
}
