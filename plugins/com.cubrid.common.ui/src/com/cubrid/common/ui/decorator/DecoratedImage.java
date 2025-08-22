/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.decorator;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * DecoratedImage Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-23 created by Kevin.Wang
 */
public class DecoratedImage extends CompositeImageDescriptor {

    private ImageDescriptor topLeft;
    private ImageDescriptor topRight;
    private ImageDescriptor bottomLeft;
    private ImageDescriptor bottomRight;

    private String topLeftKey;
    private String topRightKey;
    private String bottomLeftKey;
    private String bottomRightKey;

    private Image baseImage;
    private Point sizeOfImage;
    private String baseKey;

    private Image decoratedImage = null;

    /**
     * The constructor
     *
     * @param baseImage
     * @param baseKey
     * @param topLeft
     * @param topLeftKey
     * @param topRight
     * @param topRightKey
     * @param bottomLeft
     * @param bottomLeftKey
     * @param bottomRight
     * @param bottomRightKey
     */
    public DecoratedImage(
            Image baseImage,
            String baseKey,
            ImageDescriptor topLeft,
            String topLeftKey,
            ImageDescriptor topRight,
            String topRightKey,
            ImageDescriptor bottomLeft,
            String bottomLeftKey,
            ImageDescriptor bottomRight,
            String bottomRightKey) {
        this.baseImage = baseImage;
        this.sizeOfImage = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
        this.baseKey = baseKey;
        this.topLeft = topLeft;
        this.topLeftKey = topLeftKey;
        this.topRight = topRight;
        this.topRightKey = topRightKey;
        this.bottomLeft = bottomLeft;
        this.bottomLeftKey = bottomLeftKey;
        this.bottomRight = bottomRight;
        this.bottomRightKey = bottomRightKey;
    }

    protected void drawCompositeImage(int width, int height) {

        // Draw the base image
        drawImage(baseImage.getImageData(), 0, 0);

        // Draw on the top left corner
        if (topLeft != null && topLeftKey != null) {
            ImageData imageData = topLeft.getImageData();
            drawImage(imageData, 0, 0);
        }
        // Draw on top right corner
        if (topRight != null && topRightKey != null) {
            ImageData imageData = topRight.getImageData();
            drawImage(imageData, sizeOfImage.x - imageData.width, 0);
        }

        // Draw on bottom left
        if (bottomLeft != null && bottomLeftKey != null) {
            ImageData imageData = bottomLeft.getImageData();
            drawImage(imageData, sizeOfImage.x - imageData.width, 0);
        }
        // Draw on bottom right corner
        if (bottomRight != null && bottomRightKey != null) {
            ImageData imageData = bottomRight.getImageData();
            drawImage(imageData, sizeOfImage.x - imageData.width, sizeOfImage.y - imageData.height);
        }
    }

    protected Point getSize() {
        return sizeOfImage;
    }

    public String getKey() {
        return DecoratorManager.getKey(
                baseKey, topLeftKey, topRightKey, bottomLeftKey, bottomRightKey);
    }

    public Image getDecoratedImage() {
        if (decoratedImage == null) {
            decoratedImage = this.createImage();
        }
        return decoratedImage;
    }

    public void dispose() {
        if (baseImage != null && !baseImage.isDisposed()) {
            baseImage.dispose();
        }
        if (decoratedImage != null && !decoratedImage.isDisposed()) {
            decoratedImage.dispose();
        }
    }
}
