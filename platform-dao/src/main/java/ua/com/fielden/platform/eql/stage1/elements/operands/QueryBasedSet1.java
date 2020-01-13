package ua.com.fielden.platform.eql.stage1.elements.operands;

import java.util.Objects;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.operands.EntQuery2;
import ua.com.fielden.platform.eql.stage2.elements.operands.QueryBasedSet2;

public class QueryBasedSet1 implements ISetOperand1<QueryBasedSet2> {
    private final EntQuery1 model;

    public QueryBasedSet1(final EntQuery1 model) {
        this.model = model;
    }

    @Override
    public TransformationResult<QueryBasedSet2> transform(final PropsResolutionContext context, final String sourceId) {
        final TransformationResult<EntQuery2> modelTr = model.transform(context, sourceId);
        return new TransformationResult<QueryBasedSet2>(new QueryBasedSet2(modelTr.item), modelTr.updatedContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + model.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof QueryBasedSet1)) {
            return false;
        }
        
        final QueryBasedSet1 other = (QueryBasedSet1) obj;
        
        if (model == null) {
            if (other.model != null) {
                return false;
            }
        } else if (!model.equals(other.model)) {
            return false;
        }
        
        return Objects.equals(model, other.model);
    }
}