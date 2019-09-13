package ua.com.fielden.platform.eql.stage3.elements.sources;

import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.eql.stage3.elements.Column;

public interface IQrySource3 {
    Column column(final String colName);
    String sqlAlias();
    String sql(final DbVersion dbVersion);
    int contextId();
}