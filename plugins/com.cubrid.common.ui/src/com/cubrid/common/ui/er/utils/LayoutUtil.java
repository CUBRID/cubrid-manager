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
package com.cubrid.common.ui.er.utils;

import com.cubrid.common.ui.er.layout.ERTableNode;
import java.util.Arrays;
import java.util.List;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Util for layout
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-12-23 created by Yu Guojia
 */
public class LayoutUtil {
    public static void unionAndExpand(Rectangle basicRec, Rectangle rect) {
        int startX = Math.min(basicRec.x, rect.x);
        int startY = Math.min(basicRec.y, rect.y);

        int rightDownX = Math.max(basicRec.x + basicRec.width, rect.x + rect.width);
        int rightDownY = Math.max(basicRec.y + basicRec.height, rect.y + rect.height);

        basicRec.x = startX;
        basicRec.y = startY;
        basicRec.width = rightDownX - startX;
        basicRec.height = rightDownY - startY;
    }

    public static double getCenterNumber(double a, double b, double c) {
        double[] nums = {a, b, c};
        Arrays.sort(nums);
        return nums[1];
    }

    public static void splitSimpleTwoHalf(List allNode, List leftNodes, List rightNodes) {

        int count = allNode.size();
        int half = count / 2;
        for (int i = 0; i < half; i++) {
            leftNodes.add(allNode.get(i));
        }
        for (int i = half; i < count; i++) {
            rightNodes.add(allNode.get(i));
        }
    }

    public static void splitSmartTwoHalf(
            List<ERTableNode> allNode, List<ERTableNode> leftNodes, List<ERTableNode> rightNodes) {

        int count = allNode.size();
        int half = count / 2;
        for (int i = 0; i < half; i++) {
            leftNodes.add(allNode.get(i));
        }
        for (int i = half; i < count; i++) {
            rightNodes.add(allNode.get(i));
        }
    }
}
