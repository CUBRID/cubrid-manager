/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.model;

import java.util.ArrayList;

/**
 * cubrid broker config property model
 *
 * @author fulei
 * @version 1.0 - 2012-10-29 created by fulei
 */
public class CubridBrokerProperty {

    private String CubridBrokerPropKey;
    private String CubridBrokerPropValue;
    private String CubridBrokerPropAnnotation;
    private boolean isCubridBroker = false;
    private ArrayList<CubridBrokerProperty> propertyList = new ArrayList<CubridBrokerProperty>();

    public String getCubridBrokerPropKey() {
        return CubridBrokerPropKey;
    }

    public void setCubridBrokerPropKey(String cubridBrokerPropKey) {
        CubridBrokerPropKey = cubridBrokerPropKey;
    }

    public String getCubridBrokerPropValue() {
        return CubridBrokerPropValue;
    }

    public void setCubridBrokerPropValue(String cubridBrokerPropValue) {
        CubridBrokerPropValue = cubridBrokerPropValue;
    }

    public String getCubridBrokerPropAnnotation() {
        return CubridBrokerPropAnnotation;
    }

    public void setCubridBrokerPropAnnotation(String cubridBrokerPropAnnotation) {
        CubridBrokerPropAnnotation = cubridBrokerPropAnnotation;
    }

    public ArrayList<CubridBrokerProperty> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(ArrayList<CubridBrokerProperty> propertyList) {
        this.propertyList = propertyList;
    }

    public boolean isCubridBroker() {
        return isCubridBroker;
    }

    public void setCubridBroker(boolean isCubridBroker) {
        this.isCubridBroker = isCubridBroker;
    }

    public void addCubridBrokerProperty(CubridBrokerProperty property) {
        getPropertyList().add(property);
    }
}
