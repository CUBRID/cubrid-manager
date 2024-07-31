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
package com.cubrid.cubridmanager.ui.host.model;

import java.util.ArrayList;

/**
 * @author fulei
 * @version 1.0 - 2013-2-19 created by fulei
 */
public class CubridConfProperty {

    private String CubridConfPropKey;
    private String CubridConfPropValue;
    private String CubridConfPropAnnotation;
    private boolean isCubridConf = false;
    private ArrayList<CubridConfProperty> propertyList = new ArrayList<CubridConfProperty>();

    public String getCubridConfPropKey() {
        return CubridConfPropKey;
    }

    public void setCubridConfPropKey(String cubridConfPropKey) {
        CubridConfPropKey = cubridConfPropKey;
    }

    public String getCubridConfPropValue() {
        return CubridConfPropValue;
    }

    public void setCubridConfPropValue(String cubridConfPropValue) {
        CubridConfPropValue = cubridConfPropValue;
    }

    public String getCubridConfPropAnnotation() {
        return CubridConfPropAnnotation;
    }

    public void setCubridConfPropAnnotation(String cubridConfPropAnnotation) {
        CubridConfPropAnnotation = cubridConfPropAnnotation;
    }

    public boolean isCubridConf() {
        return isCubridConf;
    }

    public void setCubridConf(boolean isCubridConf) {
        this.isCubridConf = isCubridConf;
    }

    public ArrayList<CubridConfProperty> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(ArrayList<CubridConfProperty> propertyList) {
        this.propertyList = propertyList;
    }

    public void addCubridConfProperty(CubridConfProperty property) {
        getPropertyList().add(property);
    }
}
