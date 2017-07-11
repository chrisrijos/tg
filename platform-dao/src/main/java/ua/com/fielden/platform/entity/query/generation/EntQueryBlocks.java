package ua.com.fielden.platform.entity.query.generation;

import ua.com.fielden.platform.entity.query.generation.elements.Conditions;
import ua.com.fielden.platform.entity.query.generation.elements.GroupBys;
import ua.com.fielden.platform.entity.query.generation.elements.OrderBys;
import ua.com.fielden.platform.entity.query.generation.elements.Sources;
import ua.com.fielden.platform.entity.query.generation.elements.Yields;

public class EntQueryBlocks {
    final Sources sources;
    final Conditions conditions;
    final Yields yields;
    final GroupBys groups;
    final OrderBys orderings;
    final boolean yieldAll;

    public EntQueryBlocks(final Sources sources, final Conditions conditions, final Yields yields, final GroupBys groups, final OrderBys orderings, final boolean yieldAll) {
        this.sources = sources;
        this.conditions = conditions;
        this.yields = yields;
        this.groups = groups;
        this.orderings = orderings;
        this.yieldAll = yieldAll;
    }

    public Sources getSources() {
        return sources;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Yields getYields() {
        return yields;
    }

    public GroupBys getGroups() {
        return groups;
    }

    public OrderBys getOrderings() {
        return orderings;
    }

    public boolean isYieldAll() {
        return yieldAll;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("\n  sources: ");
        sb.append(sources);
        sb.append("\n  conditions: ");
        sb.append(conditions);
        sb.append("\n  groups: ");
        sb.append(groups);
        sb.append("\n  yields: ");
        sb.append(yields);
        sb.append("\n  orderings: ");
        sb.append(orderings);
        sb.append("\n  yieldAll: ");
        sb.append(yieldAll);
        return sb.toString();
    }
}