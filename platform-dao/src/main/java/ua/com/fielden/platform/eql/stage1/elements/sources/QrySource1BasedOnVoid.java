package ua.com.fielden.platform.eql.stage1.elements.sources;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.exceptions.EqlException;
import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.sources.QrySource2BasedOnVoid;

public class QrySource1BasedOnVoid  implements IQrySource1<QrySource2BasedOnVoid> {

    @Override
    public Class<? extends AbstractEntity<?>> sourceType() {
        throw new EqlException("This method shouldn't be invoked.");    
    }
    
    @Override
    public String getAlias() {
        throw new EqlException("This method shouldn't be invoked.");
    } 

    @Override
    public TransformationResult<QrySource2BasedOnVoid> transform(final PropsResolutionContext context) {
        return new TransformationResult<QrySource2BasedOnVoid>(new QrySource2BasedOnVoid(), context);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = 1;
        return prime * result + QrySource1BasedOnVoid.class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof QrySource1BasedOnVoid;
    }
}