package com.cubrid.common.ui.common.control;

import java.io.File;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * The broker.conf EditorInput class.
 *
 * @author CUBRID Tool developer
 */
public class BrokerConfigEditorInput implements IEditorInput {
    private final String filePath;
    private final String charset;

    public BrokerConfigEditorInput(final String filePath, final String charset) {
        this.filePath = filePath;
        this.charset = charset;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter.equals(File.class)) {
            return new File(filePath);
        } else if (adapter.equals(String.class)) {
            return charset;
        } else {
            return null;
        }
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return filePath;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return filePath;
    }
}
