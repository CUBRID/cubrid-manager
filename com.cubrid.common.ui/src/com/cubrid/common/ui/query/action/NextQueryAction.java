package com.cubrid.common.ui.query.action;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.QueryExecuter;
import java.sql.SQLException;
import org.eclipse.jface.action.Action;
import org.slf4j.Logger;

public class NextQueryAction extends Action {
    private static final Logger LOGGER = LogUtil.getLogger(NextQueryAction.class);
    private final QueryExecuter executer;

    public NextQueryAction(QueryExecuter result) {
        super(Messages.qedit_next_run);
        setId("NextQueryAction");
        setImageDescriptor(
                CommonUIPlugin.getImageDescriptor("icons/queryeditor/next_query_run.png"));
        setDisabledImageDescriptor(
                CommonUIPlugin.getImageDescriptor("icons/queryeditor/next_query_run_disabled.png"));
        setToolTipText(Messages.qedit_next_run);
        this.executer = result;
    }

    @Override
    public void run() {
        try {
            executer.runNextQuery();
        } catch (SQLException e) {
            LOGGER.error("Running next query is failed.\nerror messasge: " + e.getMessage());
        }
    }
}
