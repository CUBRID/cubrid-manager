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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * Decorator Manager
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-1-23 created by Kevin.Wang
 */
public class DecoratorManager {
    public static final String seprator = "#";
    private Map<String, DecoratedImage> decoratorMap = new HashMap<String, DecoratedImage>();

    public DecoratedImage decorate(
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

        String key = getKey(baseKey, topLeftKey, topRightKey, bottomLeftKey, bottomRightKey);
        if (!decoratorMap.containsKey(key)) {
            DecoratedImage decoratedImage =
                    new DecoratedImage(
                            baseImage,
                            baseKey,
                            topLeft,
                            topLeftKey,
                            topRight,
                            topRightKey,
                            bottomLeft,
                            bottomLeftKey,
                            bottomRight,
                            bottomRightKey);
            addDecorateImage(decoratedImage);
        }

        return decoratorMap.get(key);
    }

    public void addDecorateImage(DecoratedImage decoratedImage) {
        decoratorMap.put(decoratedImage.getKey(), decoratedImage);
    }

    public void removeDecoratedImage(String key) {
        if (decoratorMap.containsKey(key)) {
            decoratorMap.remove(key);
        }
    }

    public void clear() {
        decoratorMap.clear();
    }

    public static String getKey(
            String baseKey,
            String topLeftKey,
            String topRightKey,
            String bottomLeftKey,
            String bottomRightKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseKey).append(DecoratorManager.seprator);

        sb.append(topLeftKey == null ? "" : topLeftKey).append(DecoratorManager.seprator);
        sb.append(topRightKey == null ? "" : topRightKey).append(DecoratorManager.seprator);
        sb.append(bottomLeftKey == null ? "" : bottomLeftKey).append(DecoratorManager.seprator);
        sb.append(bottomRightKey == null ? "" : bottomRightKey).append(DecoratorManager.seprator);

        return sb.toString();
    }

    public void dispose() {
        for (DecoratedImage decoratedImage : decoratorMap.values()) {
            if (decoratedImage != null) {
                decoratedImage.dispose();
            }
        }
        clear();
    }
}
