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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to store basic class(table) objects
 *
 * @author moulinwang 2009-3-26
 */
public class ClassList {

    private List<ClassItem> classList = null;

    /**
     * add a class item to a class list
     *
     * @param item ClassItem The instance of ClassItem
     */
    public void addClass(ClassItem item) {
        synchronized (this) {
            if (null == classList) {
                classList = new ArrayList<ClassItem>();
            }
            if (!classList.contains(item)) {
                classList.add(item);
            }
        }
    }

    /**
     * Remove the given the instance of ClassItem
     *
     * @param item ClassItem The given instance of ClassItem
     */
    public void removeClass(ClassItem item) {
        synchronized (this) {
            if (null != classList) {
                classList.remove(item);
            }
        }
    }

    /** Remove all the class from classList */
    public void removeAllClass() {
        synchronized (this) {
            if (null != classList) {
                classList.clear();
            }
        }
    }

    /**
     * Get the classList
     *
     * @return List<ClassItem> The list that includes the instances of ClassItem
     */
    public List<ClassItem> getClassList() {
        synchronized (this) {
            if (classList == null) {
                return new ArrayList<ClassItem>();
            }
            return classList;
        }
    }
}
