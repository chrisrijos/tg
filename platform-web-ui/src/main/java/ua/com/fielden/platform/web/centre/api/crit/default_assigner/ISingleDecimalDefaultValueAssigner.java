package ua.com.fielden.platform.web.centre.api.crit.default_assigner;

import java.math.BigDecimal;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.types.Money;
import ua.com.fielden.platform.web.centre.api.crit.IAlsoCrit;
import ua.com.fielden.platform.web.centre.api.default_value.ISingleValueAssigner;

/**
 * A contract to provide a default value assigner or specific default values for a single-valued kind selection criteria of any decimal type,
 * which at the moment includes {@link BigDecimal} and {@link Money} types (money should be treated as big decimal).
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface ISingleDecimalDefaultValueAssigner<T extends AbstractEntity<?>> extends IAlsoCrit<T> {

    IAlsoCrit<T> withDefaultValueAssigner(final Class<? extends ISingleValueAssigner<BigDecimal, T>> assigner);

    /**
     * <code>null</code> value is not acceptable.
     *
     * @param value
     * @return
     */
    IAlsoCrit<T> setDefaultValue(final BigDecimal value);
}