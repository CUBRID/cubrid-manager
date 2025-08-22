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
package com.cubrid.common.ui.query.editor;

import org.eclipse.swt.graphics.RGB;

/**
 * SQL partitions interface
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-21 created by pangqiren
 */
public interface ISQLPartitions { // NOPMD

    String SQL_PARTITIONING = "_sql_partitioning";

    /** SQL content type */
    String SQL_STRING = "_sql_string";

    String SQL_SINGLE_LINE_COMMENT = "_sql_singleline_comment";
    String SQL_MULTI_LINE_COMMENT = "_sql_multiline_comment";
    String SQL_KEYWORD = "_sql_keyword";

    /** SQL content type color */
    RGB COLOR_BACKGROUND = new RGB(255, 255, 255);

    RGB COLOR_COMMENT = new RGB(0, 128, 0);
    RGB COLOR_DEFAULT = new RGB(0, 0, 0);
    RGB COLOR_KEYWORD = new RGB(0, 128, 128);
    RGB COLOR_NUMBER = new RGB(255, 0, 255);
    RGB COLOR_STRING = new RGB(255, 0, 0);
}
