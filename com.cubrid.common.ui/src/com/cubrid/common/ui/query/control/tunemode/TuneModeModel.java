package com.cubrid.common.ui.query.control.tunemode;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import java.util.Map;

public class TuneModeModel {
    private StructQueryPlan structQueryPlan;
    private Map<String, String> statistics;

    public TuneModeModel(StructQueryPlan structQueryPlan, Map<String, String> statistics) {
        this.structQueryPlan = structQueryPlan;
        this.statistics = statistics;
    }

    public StructQueryPlan getQueryPlan() {
        return structQueryPlan;
    }

    public void setQueryPlan(StructQueryPlan structQueryPlan) {
        this.structQueryPlan = structQueryPlan;
    }

    public Map<String, String> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, String> statistics) {
        this.statistics = statistics;
    }

    public String getQuery() {
        if (structQueryPlan == null) {
            return null;
        }
        return structQueryPlan.getSql();
    }
}
