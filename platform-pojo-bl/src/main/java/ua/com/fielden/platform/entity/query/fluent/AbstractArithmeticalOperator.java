package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IArithmeticalOperator;

abstract class AbstractArithmeticalOperator<T> extends AbstractQueryLink implements IArithmeticalOperator<T> {
    abstract T getParent();

    @Override
    public T add() {
        return copy(getParent(), getTokens().add());
    }

    @Override
    public T sub() {
        return copy(getParent(), getTokens().subtract());
    }

    @Override
    public T mult() {
        return copy(getParent(), getTokens().multiply());
    }

    @Override
    public T div() {
        return copy(getParent(), getTokens().divide());
    }

    @Override
    public T mod() {
        return copy(getParent(), getTokens().modulo());
    }
}