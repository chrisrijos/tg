package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage2.elements.IIgnorableAtS2;

public interface ITransformableToS2<S2> {
    S2 transform(PropsResolutionContext resolver);
}