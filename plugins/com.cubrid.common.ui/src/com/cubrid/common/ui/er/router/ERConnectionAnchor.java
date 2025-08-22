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
package com.cubrid.common.ui.er.router;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Anchor for ER tables. The anchor is located on the four edges of table, and the point is default
 * at the middle that the <code> offsetPercent </code> is 50%.
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-12-11 created by Yu Guojia
 */
public class ERConnectionAnchor extends AbstractConnectionAnchor {
    public static int UP = 1;
    public static int RIGHT = 2;
    public static int DOWN = 3;
    public static int LEFT = 4;

    private int anchor;
    private double offsetPercent = 0.5;

    public ERConnectionAnchor(IFigure source, int direction) {
        super(source);
        anchor = direction;
    }

    public ERConnectionAnchor(IFigure source, int direction, double offset) {
        super(source);
        anchor = direction;
        offsetPercent = offset;
    }

    /**
     * Get the converted direct by ray.
     *
     * @param ray
     * @return
     */
    public static int getDirect(Ray ray) {
        if (ray.equals(Ray.UP)) {
            return UP;
        } else if (ray.equals(Ray.RIGHT)) {
            return RIGHT;
        } else if (ray.equals(Ray.DOWN)) {
            return DOWN;
        } else if (ray.equals(Ray.LEFT)) {
            return LEFT;
        }

        return UP;
    }

    public Point getLocation(Point reference) {
        Rectangle rect = getOwner().getBounds().getCopy();
        this.getOwner().translateToAbsolute(rect);
        if (anchor == UP) {
            return rect.getTopLeft()
                    .translate(new Double(rect.width * offsetPercent).intValue(), 0);
        } else if (anchor == RIGHT) {
            return rect.getTopRight()
                    .translate(0, new Double(rect.height * offsetPercent).intValue());
        } else if (anchor == DOWN) {
            return rect.getBottomLeft()
                    .translate(new Double(rect.width * offsetPercent).intValue(), 0);
        } else if (anchor == LEFT) {
            return rect.getTopLeft()
                    .translate(0, new Double(rect.height * offsetPercent).intValue());
        }

        return null;
    }

    public int getAnchor() {
        return anchor;
    }

    public void setAnchor(int anchor) {
        this.anchor = anchor;
    }

    public double getOffsetPercent() {
        return offsetPercent;
    }

    public void setOffsetPercent(double offsetPercent) {
        this.offsetPercent = offsetPercent;
    }
}
