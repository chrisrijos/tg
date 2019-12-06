package ua.com.fielden.platform.eql.stage3.elements.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.eql.stage3.elements.Column;
import ua.com.fielden.platform.eql.stage3.elements.Yield3;
import ua.com.fielden.platform.eql.stage3.elements.operands.EntQuery3;

public class QrySource3BasedOnSubqueries implements IQrySource3 {
    private final List<EntQuery3> models = new ArrayList<>();
    public final int contextId;
    private final Map<String, Column> columns = new HashMap<>();
    
    public QrySource3BasedOnSubqueries(final List<EntQuery3> models, final int contextId) {
        this.models.addAll(models);
        this.contextId = contextId;
        for (final Yield3 entry : models.get(0).yields.getYields()) {
            columns.put(entry.alias, entry.column);
        }
    }

    @Override
    public Column column(final String colName) {
         return columns.get(colName);
    }

    @Override
    public String sqlAlias() {
        return "Q_" + contextId;
    }

    @Override
    public String sql(final DbVersion dbVersion) {
        return "(" + models.get(0).sql(dbVersion) + ") AS " + sqlAlias();
    }

    @Override
    public int contextId() {
        return contextId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + contextId;
        result = prime * result + models.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof QrySource3BasedOnSubqueries)) {
            return false;
        }
        
        final QrySource3BasedOnSubqueries other = (QrySource3BasedOnSubqueries) obj;
        
        return Objects.equals(models, other.models) && Objects.equals(contextId, other.contextId);
    }
}