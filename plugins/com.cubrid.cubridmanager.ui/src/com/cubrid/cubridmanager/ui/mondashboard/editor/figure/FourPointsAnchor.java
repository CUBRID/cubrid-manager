/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.mondashboard.editor.figure;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The anchor point can only be on the middle of the four border of the rectangle.
 *
 * <p>FourPointsAnchor Description
 *
 * @author cyl
 * @version 1.0 - 2010-5-11 created by cyl
 */
public class FourPointsAnchor extends ChopboxAnchor {

    protected FourPointsAnchor() {
        super();
    }

    public FourPointsAnchor(IFigure owner) {
        super(owner);
    }

    /**
     * Return the point to connect.
     *
     * @param reference source point location
     * @return target point.
     */
    public Point getLocation(Point reference) {
        Point result = super.getLocation(reference);
        Rectangle rect = this.getOwner().getBounds();
        if (Math.abs(result.x - rect.x) < 2) {
            result.y = rect.y + rect.height / 2;
        } else if (Math.abs(result.y - rect.y) < 2) {
            result.x = rect.x + rect.width / 2;
        } else if (Math.abs(result.x - (rect.x + rect.width)) < 2) {
            result.y = rect.y + rect.height / 2;
        } else if (Math.abs(result.y - (rect.y + rect.height)) < 2) {
            result.x = rect.x + rect.width / 2;
        }
        return result;
    }
}
