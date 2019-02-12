package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.exceptions.EqlStage1ProcessingException;
import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.meta.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.QrySource2BasedOnPersistentType;

public class QrySource1BasedOnPersistentType extends AbstractQrySource1<QrySource2BasedOnPersistentType> {
    private final Class<? extends AbstractEntity<?>> sourceType;

    public QrySource1BasedOnPersistentType(final Class<? extends AbstractEntity<?>> sourceType, final String alias) {
        super(alias);
        if (sourceType == null) {
            throw new EqlStage1ProcessingException("Source type is required.");
        }

        this.sourceType = sourceType;
    }

    @Override
    public Class<? extends AbstractEntity<?>> sourceType() {
        return sourceType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + sourceType.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof QrySource1BasedOnPersistentType)) {
            return false;
        }

        final QrySource1BasedOnPersistentType other = (QrySource1BasedOnPersistentType) obj;
        if (!sourceType.equals(other.sourceType)) {
            return false;
        }

        return true;
    }

    @Override
    public TransformationResult<QrySource2BasedOnPersistentType> transform(PropsResolutionContext resolutionContext) {
        final QrySource2BasedOnPersistentType transformedSource = new QrySource2BasedOnPersistentType(sourceType(), resolutionContext.getDomainInfo().get(sourceType()), getAlias());
        return new TransformationResult<QrySource2BasedOnPersistentType>(transformedSource, resolutionContext.cloneWithAdded(transformedSource, resolutionContext.getResolvedProps()));
    }
}