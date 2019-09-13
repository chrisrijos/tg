package ua.com.fielden.platform.eql.stage3.elements.sources;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Objects;

import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.eql.stage3.elements.Column;
import ua.com.fielden.platform.eql.stage3.elements.Table;

public class QrySource3BasedOnTable implements IQrySource3 {
    public final Table table;
    public final int contextId;
    public final String subcontextId;
    
    public QrySource3BasedOnTable(final Table table, final int contextId) {
        this(table, contextId, "");
    }

    public QrySource3BasedOnTable(final Table table, final int contextId, final String subcontextId) {
        this.table = table;
        this.contextId = contextId;
        this.subcontextId = subcontextId;
    }

    @Override
    public Column column(final String colName) {
         return table.columns.get(colName);
    }

    @Override
    public String sqlAlias() {
        return "T_" + contextId + (!isEmpty(subcontextId) ? "_" + subcontextId : "");
    }

    @Override
    public String sql(final DbVersion dbVersion) {
        return table.name + " AS " + sqlAlias();
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
        result = prime * result + subcontextId.hashCode();
        result = prime * result + table.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof QrySource3BasedOnTable)) {
            return false;
        }
        
        final QrySource3BasedOnTable other = (QrySource3BasedOnTable) obj;
        
        return Objects.equals(table, other.table) && Objects.equals(contextId, other.contextId) && Objects.equals(subcontextId, other.subcontextId);
    }
}