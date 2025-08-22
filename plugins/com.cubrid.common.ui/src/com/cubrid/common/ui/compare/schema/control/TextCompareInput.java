package com.cubrid.common.ui.compare.schema.control;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * Basic Text Compare Input
 *
 * @author Ray Yin
 * @version 1.0 - 2012.10.10 created by Ray Yin
 */
public class TextCompareInput implements ITypedElement, IStreamContentAccessor, IEditableContent {
    String fContent;
    List<String> fContent2;

    public TextCompareInput(String s) {
        fContent = s;
    }

    public TextCompareInput(List<String> tableList) {
        fContent2 = tableList;
    }

    public String getName() {
        return "name";
    }

    public Image getImage() {
        return null;
    }

    public String getType() {
        return "txt";
    }

    public InputStream getContents() throws CoreException {
        return new ByteArrayInputStream(fContent.getBytes());
    }

    public boolean isEditable() {
        return true;
    }

    public void setContent(byte[] newContent) {}

    public ITypedElement replace(ITypedElement dest, ITypedElement src) {
        return null;
    }
}
