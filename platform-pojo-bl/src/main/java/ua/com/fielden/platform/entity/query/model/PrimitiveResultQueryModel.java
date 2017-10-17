package ua.com.fielden.platform.entity.query.model;

import java.util.List;

import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.utils.Pair;

public class PrimitiveResultQueryModel extends SingleResultQueryModel {

    protected PrimitiveResultQueryModel() {
    }

    public PrimitiveResultQueryModel(final List<Pair<TokenCategory, Object>> tokens) {
        super(tokens, null, false);
    }
}