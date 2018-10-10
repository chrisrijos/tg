package ua.com.fielden.platform.entity.query.generation.elements;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.entity.query.fluent.enums.JoinType;

public class CompoundSource implements IPropertyCollector {
    private final ISource source;
    private final JoinType joinType;
    private final Conditions joinConditions;

    public CompoundSource(final ISource source, final JoinType joinType, final Conditions joinConditions) {
        this.source = source;
        this.joinType = joinType;
        this.joinConditions = joinConditions;
    }

    public String sql() {
        return joinType + " " + source.sql() + " ON " + joinConditions.sql();
    }

    @Override
    public String toString() {
        return joinType + " " + source + " ON " + joinConditions;
    }

    public ISource getSource() {
        return source;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Conditions getJoinConditions() {
        return joinConditions;
    }

    @Override
    public List<EntProp> getLocalProps() {
        return joinConditions.getLocalProps();
    }

    @Override
    public List<EntValue> getAllValues() {
        final List<EntValue> result = new ArrayList<>();
        result.addAll(source.getValues());
        result.addAll(joinConditions.getAllValues());
        return result;
    }

    @Override
    public List<EntQuery> getLocalSubQueries() {
        return joinConditions.getLocalSubQueries();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((joinConditions == null) ? 0 : joinConditions.hashCode());
        result = prime * result + ((joinType == null) ? 0 : joinType.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CompoundSource)) {
            return false;
        }
        final CompoundSource other = (CompoundSource) obj;
        if (joinConditions == null) {
            if (other.joinConditions != null) {
                return false;
            }
        } else if (!joinConditions.equals(other.joinConditions)) {
            return false;
        }
        if (joinType != other.joinType) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }
}