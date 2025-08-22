/*
 * Copyright (c) 2004 Steve Northover and Mike Wilson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */
package com.cubrid.common.ui.spi.table.celleditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Image viewer including animated images
 *
 * @author mcq, created by
 * @author pangqiren, modified at Mar 11, 2013
 */
public class ImageViewer extends Canvas {

    protected Point originPoint = new Point(0, 0);
    protected Image image;
    protected ImageData[] imageDatas;
    protected Image[] images;
    protected int currentImage;

    private int repeatCount;
    private Runnable animationRunnable;
    private ScrollBar hScrollBar;
    private ScrollBar vScrollBar;
    private Color background;
    private Display display;

    /**
     * The constructor
     *
     * @param parent
     */
    public ImageViewer(Composite parent) {
        super(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);

        hScrollBar = getHorizontalBar();
        vScrollBar = getVerticalBar();
        background = getBackground();
        display = getDisplay();
        addListeners();
    }

    /**
     * Set the image
     *
     * @param imageData ImageData
     */
    public void setImage(ImageData imageData) {
        checkWidget();
        stopAnimation();
        this.image = new Image(display, imageData);
        this.imageDatas = null;
        this.images = null;
        redraw();
    }

    /**
     * Set the images
     *
     * @param imageDatas ImageData[]
     * @param repeatCount 0 forever
     */
    public void setImages(ImageData[] imageDatas, int repeatCount) {
        checkWidget();

        this.image = null;
        this.imageDatas = imageDatas;
        this.repeatCount = repeatCount;
        createImageByImageData();
        startAnimation();
        redraw();
    }

    /** Compute the size */
    public Point computeSize(int wHint, int hHint, boolean changed) {
        checkWidget();
        Image image = getCurrentImage();
        if (image != null) {
            Rectangle rect = image.getBounds();
            Rectangle trim = computeTrim(0, 0, rect.width, rect.height);
            return new Point(trim.width, trim.height);
        }
        return new Point(wHint, hHint);
    }

    /** Dispose the image */
    public void dispose() {
        if (image != null) {
            image.dispose();
        }

        if (images != null) {
            for (int i = 0; i < images.length; i++) {
                images[i].dispose();
            }
        }
        super.dispose();
    }

    /**
     * Paint the image
     *
     * @param event Event
     */
    protected void paint(Event event) {
        Image image = getCurrentImage();
        if (image == null) {
            return;
        }

        GC gc = event.gc;
        gc.drawImage(image, originPoint.x, originPoint.y);

        gc.setBackground(background);
        Rectangle rect = image.getBounds();
        Rectangle client = getClientArea();
        int marginWidth = client.width - rect.width;
        if (marginWidth > 0) {
            gc.fillRectangle(rect.width, 0, marginWidth, client.height);
        }
        int marginHeight = client.height - rect.height;
        if (marginHeight > 0) {
            gc.fillRectangle(0, rect.height, client.width, marginHeight);
        }
    }

    /** Add the listeners */
    private void addListeners() {
        hScrollBar.addListener(
                SWT.Selection,
                new Listener() {
                    public void handleEvent(Event arg0) {
                        hScroll();
                    }
                });

        vScrollBar.addListener(
                SWT.Selection,
                new Listener() {
                    public void handleEvent(Event arg0) {
                        vScroll();
                    }
                });

        addListener(
                SWT.Resize,
                new Listener() {
                    public void handleEvent(Event e) {
                        resize();
                    }
                });

        addListener(
                SWT.Paint,
                new Listener() {
                    public void handleEvent(Event e) {
                        paint(e);
                    }
                });
    }

    /** Scroll horizontally */
    private void hScroll() {
        Image image = getCurrentImage();
        if (image != null) {
            int hSelection = hScrollBar.getSelection();
            int destX = -hSelection - originPoint.x;
            Rectangle rect = image.getBounds();
            scroll(destX, 0, 0, 0, rect.width, rect.height, false);
            originPoint.x = -hSelection;
        }
    }

    /** Scroll vertically */
    private void vScroll() {
        Image image = getCurrentImage();
        if (image != null) {
            int vSelection = vScrollBar.getSelection();
            int destY = -vSelection - originPoint.y;
            Rectangle rect = image.getBounds();
            scroll(0, destY, 0, 0, rect.width, rect.height, false);
            originPoint.y = -vSelection;
        }
    }

    /** Resize */
    private void resize() {
        Image image = getCurrentImage();
        if (image == null) {
            return;
        }

        Rectangle rect = image.getBounds();
        Rectangle client = getClientArea();
        hScrollBar.setMaximum(rect.width);
        vScrollBar.setMaximum(rect.height);
        hScrollBar.setThumb(Math.min(rect.width, client.width));
        vScrollBar.setThumb(Math.min(rect.height, client.height));
        int hPage = rect.width - client.width;
        int vPage = rect.height - client.height;
        int hSelection = hScrollBar.getSelection();
        int vSelection = vScrollBar.getSelection();
        if (hSelection >= hPage) {
            if (hPage <= 0) {
                hSelection = 0;
            }
            originPoint.x = -hSelection;
        }
        if (vSelection >= vPage) {
            if (vPage <= 0) {
                vSelection = 0;
            }
            originPoint.y = -vSelection;
        }
        redraw();
    }

    /** Create the image by image data */
    private void createImageByImageData() {
        images = new Image[imageDatas.length];

        // Get the image size
        int width = imageDatas[0].width;
        int height = imageDatas[0].height;

        // Create every image
        int disposalMethod = SWT.DM_FILL_BACKGROUND;
        for (int i = 0; i < imageDatas.length; i++) {
            ImageData currImageData = imageDatas[i];
            images[i] = new Image(display, width, height);
            GC currImageGC = new GC(images[i]);

            switch (disposalMethod) {
                case SWT.DM_FILL_PREVIOUS:
                    // Draw the second last image.
                    currImageGC.drawImage(images[i - 2], 0, 0);
                    break;
                case SWT.DM_FILL_NONE:
                case SWT.DM_UNSPECIFIED:
                    // Draw the last image.
                    currImageGC.drawImage(images[i - 1], 0, 0);
                    break;
                default:
                    // If DM_FILL_BACKGROUND or anything else, fill with default background
                    currImageGC.setBackground(background);
                    currImageGC.fillRectangle(0, 0, width, height);
                    break;
            }

            // Draw the current image,then clean up
            Image currImage = new Image(display, currImageData);
            currImageGC.drawImage(
                    currImage,
                    0,
                    0,
                    currImageData.width,
                    currImageData.height,
                    currImageData.x,
                    currImageData.y,
                    currImageData.width,
                    currImageData.height);
            currImage.dispose();
            currImageGC.dispose();

            // Compute the next disposal method.
            disposalMethod = currImageData.disposalMethod;
            if (i == 0 && disposalMethod == SWT.DM_FILL_PREVIOUS) {
                disposalMethod = SWT.DM_FILL_NONE;
            }
        }
    }

    /**
     * Get the current image
     *
     * @return Image
     */
    private Image getCurrentImage() {
        if (image != null) {
            return image;
        }
        if (images == null) {
            return null;
        }
        return images[currentImage];
    }

    /** Start the animation */
    private void startAnimation() {
        if (images == null || images.length < 2) {
            return;
        }

        final int delay = imageDatas[currentImage].delayTime * 10;
        display.timerExec(
                delay,
                animationRunnable =
                        new Runnable() {
                            public void run() {
                                if (isDisposed()) {
                                    return;
                                }

                                currentImage = (currentImage + 1) % images.length;
                                redraw();

                                if (currentImage + 1 == images.length
                                        && repeatCount != 0
                                        && --repeatCount <= 0) {
                                    return;
                                }
                                display.timerExec(delay, this);
                            }
                        });
    }

    /** Stoop the animation */
    private void stopAnimation() {
        if (animationRunnable != null) {
            display.timerExec(-1, animationRunnable);
        }
    }
}
