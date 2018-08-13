package ua.com.fielden.platform.entity.query.generation.elements;

import static java.lang.String.format;
import static ua.com.fielden.platform.entity.query.DbVersion.MSSQL;
import static ua.com.fielden.platform.entity.query.DbVersion.MYSQL;
import static ua.com.fielden.platform.entity.query.DbVersion.POSTGRESQL;
import static ua.com.fielden.platform.utils.CollectionUtil.listOf;
import static ua.com.fielden.platform.utils.EntityUtils.equalsEx;

import java.util.List;

import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.entity.query.exceptions.EqlException;

public class LikeTest extends AbstractCondition {
    private final ISingleOperand leftOperand;
    private final ISingleOperand rightOperand;
    private final boolean negated;
    private final boolean caseInsensitive;
    private final DbVersion dbVersion;

    public LikeTest(final ISingleOperand leftOperand, final ISingleOperand rightOperand, final boolean negated, final boolean caseInsensitive, final DbVersion dbVersion) {
        if (dbVersion == null) {
            throw new EqlException("The dabase version is missing.");
        }
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.negated = negated;
        this.caseInsensitive = caseInsensitive;
        this.dbVersion = dbVersion;
    }

    @Override
    public String sql() {
        return dbVersion.likeSql(negated, leftOperand.sql(), rightOperand.sql(), caseInsensitive);// format("%s %s %s", prepareOperandSql(leftOperand), prepareLikeOperand(), prepareOperandSql(rightOperand));
    }

    @Override
    public boolean ignore() {
        return leftOperand.ignore() || rightOperand.ignore();
    }

    @Override
    protected List<IPropertyCollector> getCollection() {
        return  listOf(leftOperand, rightOperand);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (caseInsensitive ? 1231 : 1237);
        result = prime * result + dbVersion.hashCode();
        result = prime * result + (leftOperand == null ? 0 : leftOperand.hashCode());
        result = prime * result + (negated ? 1231 : 1237);
        result = prime * result + (rightOperand == null ? 0 : rightOperand.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LikeTest)) {
            return false;
        }

        final LikeTest other = (LikeTest) obj;
        return caseInsensitive == other.caseInsensitive &&
               negated == other.negated &&
               dbVersion == other.dbVersion &&
               equalsEx(leftOperand, other.leftOperand) && 
               equalsEx(rightOperand, other.rightOperand);
    }
}