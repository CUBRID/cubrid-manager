package com.cubrid.common.ui.perspective;

import org.eclipse.jface.resource.ImageDescriptor;

public class OpenCQBPerspectiveAction extends AbsOpenPerspectiveAction {
    public static final String ID = OpenCQBPerspectiveAction.class.getName();

    public OpenCQBPerspectiveAction(String text, ImageDescriptor image) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
    }

    protected String getTargetId() {
        return IPerspectiveConstance.CQB_PERSPECTIVE_ID;
    }
}
