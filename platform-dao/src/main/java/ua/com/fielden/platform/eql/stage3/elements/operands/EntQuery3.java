package ua.com.fielden.platform.eql.stage3.elements.operands;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.Objects;

import ua.com.fielden.platform.eql.stage3.elements.EntQueryBlocks3;
import ua.com.fielden.platform.eql.stage3.elements.Yields3;
import ua.com.fielden.platform.eql.stage3.elements.conditions.Conditions3;
import ua.com.fielden.platform.eql.stage3.elements.sources.Sources3;

public class EntQuery3 implements ISingleOperand3 {

    public final Sources3 sources;
    public final Conditions3 conditions;
    public final Yields3 yields;

    public EntQuery3(final EntQueryBlocks3 queryBlocks) {
        this.sources = queryBlocks.sources;
        this.conditions = queryBlocks.conditions;
        this.yields = queryBlocks.yields;
    }

    @Override
    public String sql() {
        final StringBuffer sb = new StringBuffer();
        final String yieldsSql = yields.sql();
        sb.append("SELECT\n");
        sb.append(isNotEmpty(yieldsSql) ? yieldsSql : " * "); 
        sb.append(sources.sql());
        final String conditionsSql = conditions.sql();
        if (isNotEmpty(conditionsSql)) {
            sb.append("\nWHERE ");
            sb.append(conditionsSql);
        }        
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
        result = prime * result + ((yields == null) ? 0 : yields.hashCode());
        result = prime * result + ((sources == null) ? 0 : sources.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EntQuery3)) {
            return false;
        }
        
        final EntQuery3 other = (EntQuery3) obj;
        
        return  Objects.equals(sources, other.sources) &&
                Objects.equals(yields, other.yields) &&
                Objects.equals(conditions, other.conditions);
    }
}