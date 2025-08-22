/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.common.navigator;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

/**
 * Decorating label provider
 *
 * @author Kevin.Wang
 */
public class DecoratingLabelProvider extends DecoratingStyledCellLabelProvider
        implements IPropertyChangeListener, ILabelProvider, ITableLabelProvider {

    private static class StyledLabelProviderAdapter
            implements IStyledLabelProvider, ITableLabelProvider, IColorProvider, IFontProvider {

        private final ILabelProvider provider;

        public StyledLabelProviderAdapter(ILabelProvider provider) {
            this.provider = provider;
        }

        public Image getImage(Object element) {
            return provider.getImage(element);
        }

        public StyledString getStyledText(Object element) {
            if (provider instanceof IStyledLabelProvider) {
                return ((IStyledLabelProvider) provider).getStyledText(element);
            }
            String text = provider.getText(element);
            if (text == null) text = ""; // $NON-NLS-1$
            return new StyledString(text);
        }

        public void addListener(ILabelProviderListener listener) {
            provider.addListener(listener);
        }

        public void dispose() {
            provider.dispose();
        }

        public boolean isLabelProperty(Object element, String property) {
            return provider.isLabelProperty(element, property);
        }

        public void removeListener(ILabelProviderListener listener) {
            provider.removeListener(listener);
        }

        public Color getBackground(Object element) {
            if (provider instanceof IColorProvider) {
                return ((IColorProvider) provider).getBackground(element);
            }
            return null;
        }

        public Color getForeground(Object element) {
            if (provider instanceof IColorProvider) {
                return ((IColorProvider) provider).getForeground(element);
            }
            return null;
        }

        public Font getFont(Object element) {
            if (provider instanceof IFontProvider) {
                return ((IFontProvider) provider).getFont(element);
            }
            return null;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            if (provider instanceof ITableLabelProvider) {
                return ((ITableLabelProvider) provider).getColumnImage(element, columnIndex);
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (provider instanceof ITableLabelProvider) {
                return ((ITableLabelProvider) provider).getColumnText(element, columnIndex);
            }
            return null;
        }
    }

    /**
     * Creates a decorating label provider
     *
     * @param commonLabelProvider the label provider to use
     */
    public DecoratingLabelProvider(ILabelProvider commonLabelProvider) {
        super(
                new StyledLabelProviderAdapter(commonLabelProvider),
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(),
                null);
    }

    public void initialize(ColumnViewer viewer, ViewerColumn column) {
        PlatformUI.getPreferenceStore().addPropertyChangeListener(this);
        JFaceResources.getColorRegistry().addListener(this);

        setOwnerDrawEnabled(showColoredLabels());

        super.initialize(viewer, column);
    }

    public void dispose() {
        super.dispose();
        PlatformUI.getPreferenceStore().removePropertyChangeListener(this);
        JFaceResources.getColorRegistry().removeListener(this);
    }

    private void refresh() {
        ColumnViewer viewer = getViewer();
        if (viewer == null) {
            return;
        }
        boolean showColoredLabels = showColoredLabels();
        if (showColoredLabels != isOwnerDrawEnabled()) {
            setOwnerDrawEnabled(showColoredLabels);
            viewer.refresh();
        } else if (showColoredLabels) {
            viewer.refresh();
        }
    }

    private static boolean showColoredLabels() {
        return PlatformUI.getPreferenceStore()
                .getBoolean(IWorkbenchPreferenceConstants.USE_COLORED_LABELS);
    }

    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(JFacePreferences.QUALIFIER_COLOR)
                || property.equals(JFacePreferences.COUNTER_COLOR)
                || property.equals(JFacePreferences.DECORATIONS_COLOR)
                || property.equals(IWorkbenchPreferenceConstants.USE_COLORED_LABELS)) {
            Display.getDefault()
                    .asyncExec(
                            new Runnable() {
                                public void run() {
                                    refresh();
                                }
                            });
        }
    }

    public String getText(Object element) {
        return getStyledText(element).getString();
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return ((StyledLabelProviderAdapter) getStyledStringProvider())
                .getColumnImage(element, columnIndex);
    }

    public String getColumnText(Object element, int columnIndex) {
        return ((StyledLabelProviderAdapter) getStyledStringProvider())
                .getColumnText(element, columnIndex);
    }
}
