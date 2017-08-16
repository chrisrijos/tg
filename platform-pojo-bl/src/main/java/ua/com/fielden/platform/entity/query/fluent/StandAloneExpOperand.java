package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneExprOperand;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneExprOperationAndClose;

public final class StandAloneExpOperand //
		extends YieldedItem<IStandAloneExprOperationAndClose, AbstractEntity<?>> //
		implements IStandAloneExprOperand {

	@Override
	protected IStandAloneExprOperationAndClose nextForSingleOperand() {
		return new StandAloneExpOperationAndClose();
	}
}