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

package com.cubrid.cubridmanager.core.common.model;

import static com.cubrid.common.core.util.NoOp.noOp;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.apache.commons.lang.WordUtils.capitalizeFully;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import java.util.List;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages,it
 * provide comments for cubrid.conf and cm.conf and cubrid_broker.conf
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class ConfComments extends NLS {
    private static final Logger LOGGER = LogUtil.getLogger(ConfComments.class);

    private ConfComments() {
        noOp();
    }

    static {
        NLS.initializeMessages(
                CubridManagerCorePlugin.PLUGIN_ID + ".common.model.ConfComments",
                ConfComments.class);
    }

    /**
     * Get comments of parameter
     *
     * @param param String The given field name of the class ConfConstants
     * @return String
     */
    public static String getComments(String param) {
        String prefix = param;
        if (param.equals(ConfConstants.COMMON_SECTION)) {
            prefix = "common_section";
        } else if (param.equals(ConfConstants.SERVICE_SECTION)) {
            prefix = "service_section";
        }
        try {
            String key =
                    uncapitalize(
                            capitalizeFully(prefix + "_comments", new char[] {'_'})
                                    .replaceAll("_", ""));
            return (String) ConfComments.class.getField(key).get(new ConfComments());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * Parse comments and provide comments multi line support
     *
     * @param commentsList List<String> The given list that includes comments
     * @param comments String One or many new comments
     */
    public static void addComments(List<String> commentsList, String comments) {
        String[] commentsArr = comments.split("\r\n");
        for (int i = 0; i < commentsArr.length; i++) {
            if (commentsArr[i].trim().indexOf("#") == 0) {
                commentsList.add(commentsArr[i]);
            } else if (commentsArr[i].trim().length() == 0) {
                commentsList.add("");
            } else {
                commentsList.add("#" + commentsArr[i]);
            }
        }
    }

    // CUBRID copyright comments
    public static String cubridCopyrightComments;
    // database common section parameter
    public static String serviceSectionComments;
    public static String serviceComments;
    public static String serverComments;
    public static String commonSectionComments;
    public static String autoRestartServerComments;
    public static String checkpointIntervalInMinsComments;
    public static String cubridPortIdComments;
    public static String dataBufferPagesComments;
    public static String dataBufferSizeComments;
    public static String deadlockDetectionIntervalInSecsComments;
    public static String isolationLevelComments;
    public static String javaStoredProcedureComments;
    public static String lockEscalationComments;
    public static String lockTimeoutInSecsComments;
    public static String logBufferPagesComments;
    public static String logBufferSizeComments;
    public static String maxClientsComments;
    public static String replicationComments;
    public static String sortBufferPagesComments;
    public static String sortBufferSizeComments;
    public static String pthreadScopeProcessComments;
    public static String accessIpControl;
    public static String accessIpControlFile;
    public static String asyncCommitComments;
    public static String backupVolumeMaxSizeBytesComments;
    public static String blockDdlStatementComments;
    public static String blockNowhereStatementComments;
    public static String callStackDumpActivationListComments;
    public static String callStackDumpDeactivationListComments;
    public static String callStackDumpOnErrorComments;
    public static String compactdbPageReclaimOnlyComments;
    public static String compatNumericDivisionScaleComments;
    public static String compatPrimaryKeyComments;
    public static String csqlHistoryNumComments;
    public static String dbHostsComments;
    public static String dontReuseHeapFileComments;
    public static String errorLogComments;
    public static String fileLockComments;
    public static String garbageCollectionComments;
    public static String groupCommitIntervalInMsecsComments;
    public static String hostvarLateBindingComments;
    public static String indexScanInOidOrderComments;
    public static String indexScanOidBufferPagesComments;
    public static String insertExecutionModeComments;
    public static String intlMbsSupportComments;
    public static String lockTimeoutMessageTypeComments;
    public static String maxPlanCacheEntriesComments;
    public static String maxQueryCacheEntriesComments;
    public static String mediaFailureSupportComments;
    public static String oracleStyleEmptyStringComments;
    public static String oracleStyleOuterjoinComments;
    public static String queryCacheModeComments;
    public static String queryCacheSizeInPagesComments;
    public static String singleByteCompareComments;
    public static String tempFileMaxSizeInPagesComments;
    public static String tempFileMemorySizeInPagesComments;
    public static String tempVolumePathComments;
    public static String threadStackSizeComments;
    public static String unfillFactorComments;
    public static String volumeExtensionPathComments;
}
