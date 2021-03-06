package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneConditionCompoundCondition;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneConditionOperand;
import ua.com.fielden.platform.entity.query.model.ConditionModel;

public final class StandAloneConditionCompoundCondition<ET extends AbstractEntity<?>> //
		extends LogicalCondition<IStandAloneConditionOperand<ET>> //
		implements IStandAloneConditionCompoundCondition<ET> {

    public StandAloneConditionCompoundCondition(final Tokens tokens) {
        super(tokens);
    }
    
	@Override
	public ConditionModel model() {
		return new ConditionModel(getTokens().getValues());
	}

	@Override
	protected IStandAloneConditionOperand<ET> nextForLogicalCondition(final Tokens tokens) {
		return new StandAloneConditionOperand<ET>(tokens);
	}
}