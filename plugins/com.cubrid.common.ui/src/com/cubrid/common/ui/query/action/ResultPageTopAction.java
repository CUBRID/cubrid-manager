package com.cubrid.common.ui.query.action;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.control.QueryExecuter;
import org.eclipse.jface.action.Action;

public class ResultPageTopAction extends Action {
    private final QueryExecuter executer;

    public ResultPageTopAction(QueryExecuter result) {
        super(Messages.qedit_top);
        setId("ResultPageTopAction");
        setImageDescriptor(
                CommonUIPlugin.getImageDescriptor("icons/queryeditor/query_page_top.png"));
        setDisabledImageDescriptor(
                CommonUIPlugin.getImageDescriptor("icons/queryeditor/query_page_top_disabled.png"));
        setToolTipText(Messages.qedit_top);
        this.executer = result;
    }

    @Override
    public void run() {
        executer.tblResult.setSelection(0);
    }
}
