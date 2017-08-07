package ua.com.fielden.platform.serialisation.jackson;

import static ua.com.fielden.platform.entity.annotation.IsProperty.DEFAULT_LENGTH;
import static ua.com.fielden.platform.entity.annotation.IsProperty.DEFAULT_PRECISION;
import static ua.com.fielden.platform.entity.annotation.IsProperty.DEFAULT_SCALE;
import static ua.com.fielden.platform.entity.annotation.IsProperty.DEFAULT_TRAILING_ZEROS;
import static ua.com.fielden.platform.utils.EntityUtils.equalsEx;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.DateOnly;
import ua.com.fielden.platform.entity.annotation.PersistentType;
import ua.com.fielden.platform.entity.annotation.TimeOnly;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.types.markers.IUtcDateTimeType;
import ua.com.fielden.snappy.DateRangePrefixEnum;
import ua.com.fielden.snappy.MnemonicEnum;

/**
 * A set of utilities to determine if the value of some property or meta-info is default. It is used internally for Jackson entity serialiser to significantly reduce the amount of
 * the information to be serialised.
 *
 * @author TG Team
 *
 */
public class DefaultValueContract {
    private DefaultValueContract() {
    }
    
    ///////////////////////////////////////////////// prevValue /////////////////////////////////////////////////
    /**
     * Returns the value of <code>prevValue</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static Object getPrevValue(final MetaProperty<Object> metaProperty) {
        return metaProperty.getPrevValue();
    }

    /**
     * Returns <code>true</code> if the value of <code>prevValue</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static boolean isPrevValueDefault(final MetaProperty<Object> metaProperty) {
        return equalsEx(getPrevValue(metaProperty), getOriginalValue(metaProperty));
    }

    ///////////////////////////////////////////////// lastInvalidValue /////////////////////////////////////////////////
    /**
     * Returns the value of <code>lastInvalidValue</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static Object getLastInvalidValue(final MetaProperty<Object> metaProperty) {
        return metaProperty.getLastInvalidValue();
    }

    /**
     * Returns <code>true</code> if the value of <code>lastInvalidValue</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static boolean isLastInvalidValueDefault(final MetaProperty<Object> metaProperty) {
        return equalsEx(getLastInvalidValue(metaProperty), null);
    }

    ///////////////////////////////////////////////// valueChangeCount /////////////////////////////////////////////////
    /**
     * Returns the value of <code>valueChangeCount</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static int getValueChangeCount(final MetaProperty<Object> metaProperty) {
        return metaProperty.getValueChangeCount();
    }

    /**
     * Returns <code>true</code> if the value of <code>valueChangeCount</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static boolean isValueChangeCountDefault(final MetaProperty<Object> metaProperty) {
        return equalsEx(getValueChangeCount(metaProperty), 0);
    }

    ///////////////////////////////////////////////// EDITABLE /////////////////////////////////////////////////
    /**
     * Returns the default value of <code>editable</code> property.
     *
     * @return
     */
    public static boolean getEditableDefault() {
        return true;
    }

    /**
     * Returns the value of <code>editable</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean getEditable(final MetaProperty<M> metaProperty) {
        return metaProperty == null ? getEditableDefault() : metaProperty.isEditable();
    }

    /**
     * Returns <code>true</code> if the value of <code>editable</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static boolean isEditableDefault(final MetaProperty<Object> metaProperty) {
        return equalsEx(getEditable(metaProperty), getEditableDefault());
    }

    /**
     * Returns the value of <code>timeZone</code> for date-typed property or <code>null</code> in case of default one.
     * <p>
     * If default timeZone is active for this property (<code>null</code> has been returned from this method),
     * then it was specified on JVM level rather explicitly (through -Duser.timezone=Europe/Sofia) or through OS-dependent timeZone setting.
     *
     * @param entityType
     * @param propertyName
     *
     * @return
     */
    public static String getTimeZone(final Class<?> entityType, final String propertyName) {
        final PersistentType persistentType = AnnotationReflector.getPropertyAnnotation(PersistentType.class, entityType, propertyName);
        return persistentType != null && persistentType.userType().equals(IUtcDateTimeType.class) ? "UTC" : null;
    }

    /**
     * Returns the value that indicates what portion of date property to display.
     *
     * @param entityType
     * @param propertyName
     * @return
     */
    public static String getTimePortionToDisplay(final Class<?> entityType, final String propertyName) {
        if (AnnotationReflector.isPropertyAnnotationPresent(DateOnly.class, entityType, propertyName)) {
            return "DATE";
        } else if (AnnotationReflector.isPropertyAnnotationPresent(TimeOnly.class, entityType, propertyName)) {
            return "TIME";
        }
        return null;
    }

    ///////////////////////////////////////////////// DIRTY /////////////////////////////////////////////////
    /**
     * Returns the default value of <code>changedFromOriginal</code> property.
     *
     * @return
     */
    public static boolean isChangedFromOriginalDefault() {
        return false;
    }

    /**
     * Returns the value of <code>isChangedFromOriginal</code> meta-property attribute, defaulting to {@link #isChangedFromOriginalDefault()} if <code>metaProperty</code> is null.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean isChangedFromOriginal(final MetaProperty<M> metaProperty) {
        return metaProperty == null ? isChangedFromOriginalDefault() : metaProperty.isChangedFromOriginal();
    }

    /**
     * Returns the value of <code>originalValue</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static <M> M getOriginalValue(final MetaProperty<M> metaProperty) {
        if (metaProperty == null) {
            throw new IllegalStateException("If the meta property does not exist -- original value population is unsupported.");
        }
        return metaProperty.getOriginalValue();
    }

    /**
     * Returns <code>true</code> if the value of <code>changedFromOriginal</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean isChangedFromOriginalDefault(final MetaProperty<M> metaProperty) {
        return equalsEx(isChangedFromOriginal(metaProperty), isChangedFromOriginalDefault());
    }

    ///////////////////////////////////////////////// REQUIRED /////////////////////////////////////////////////
    /**
     * Returns the default value of <code>required</code> property.
     *
     * @return
     */
    public static boolean getRequiredDefault() {
        return false;
    }

    /**
     * Returns the value of <code>required</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean getRequired(final MetaProperty<M> metaProperty) {
        return metaProperty == null ? getRequiredDefault() : metaProperty.isRequired();
    }

    /**
     * Returns <code>true</code> if the value of <code>required</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean isRequiredDefault(final MetaProperty<M> metaProperty) {
        return equalsEx(getRequired(metaProperty), getRequiredDefault());
    }

    ///////////////////////////////////////////////// VISIBLE /////////////////////////////////////////////////
    /**
     * Returns the default value of <code>visible</code> property.
     *
     * @return
     */
    public static boolean getVisibleDefault() {
        return true;
    }

    /**
     * Returns the value of <code>visible</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean getVisible(final MetaProperty<M> metaProperty) {
        return metaProperty == null ? getVisibleDefault() : metaProperty.isVisible();
    }

    /**
     * Returns <code>true</code> if the value of <code>visible</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean isVisibleDefault(final MetaProperty<M> metaProperty) {
        return equalsEx(getVisible(metaProperty), getVisibleDefault());
    }

    ///////////////////////////////////////////////// VALIDATION RESULT /////////////////////////////////////////////////
    /**
     * Returns the default value of <code>ValidationResult</code> property.
     *
     * @return
     */
    public static Result getValidationResultDefault() {
        return null;
    }

    /**
     * Returns the value of <code>ValidationResult</code> property.
     *
     * @param metaProperty
     * @return
     */
    public static <M> Result getValidationResult(final MetaProperty<M> metaProperty) {
        return validationResult(metaProperty);
    }

    private static <M> Result validationResult(final MetaProperty<M> metaProperty) {
        return !metaProperty.isValid() ? metaProperty.getFirstFailure() : metaProperty.getFirstWarning();
    }

    /**
     * Returns <code>true</code> if the value of <code>ValidationResult</code> property is default, <code>false</code> otherwise.
     *
     * @param metaProperty
     * @return
     */
    public static <M> boolean isValidationResultDefault(final MetaProperty<M> metaProperty) {
        return equalsEx(getValidationResult(metaProperty), getValidationResultDefault());
    }

    ///////////////////////////////////////////////// COMPOSITE KEY MEMBER SEPARATOR /////////////////////////////////////////////////
    /**
     * Returns <code>true</code> if the value of <code>composite key member separator</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isCompositeKeySeparatorDefault(final String compositeKeySeparator) {
        return equalsEx(compositeKeySeparator, " ");
    }

    ///////////////////////////////////////////////// ENTITY TITLE AND DESC /////////////////////////////////////////////////
    /**
     * Returns <code>true</code> if the value of <code>entity title</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isEntityTitleDefault(final Class<? extends AbstractEntity<?>> entityType, final String entityTitle) {
        return equalsEx(entityTitle, TitlesDescsGetter.getDefaultEntityTitleAndDesc(entityType).getKey());
    }

    /**
     * Returns <code>true</code> if the value of <code>entity description</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isEntityDescDefault(final Class<? extends AbstractEntity<?>> entityType, final String entityDesc) {
        return equalsEx(entityDesc, TitlesDescsGetter.getDefaultEntityTitleAndDesc(entityType).getValue());
    }

    /**
     * Returns <code>true</code> if the value of <code>displayDesc</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isDisplayDescDefault(final boolean shouldDisplayDesc) {
        return equalsEx(shouldDisplayDesc, false);
    }

    ///////////////////////////////////////////////// ENTITY TYPE PROP props /////////////////////////////////////////////////
    /**
     * Returns <code>true</code> if the value of <code>secrete</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isSecreteDefault(final Boolean secrete) {
        return equalsEx(secrete, false);
    }

    /**
     * Returns <code>true</code> if the value of <code>upperCase</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isUpperCaseDefault(final Boolean upperCase) {
        return equalsEx(upperCase, false);
    }

    /**
     * Returns <code>true</code> if the value of <code>critOnly</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isCritOnlyDefault(final Boolean critOnly) {
        return equalsEx(critOnly, false);
    }

    /**
     * Returns <code>true</code> if the value of <code>resultOnly</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isResultOnlyDefault(final Boolean resultOnly) {
        return equalsEx(resultOnly, false);
    }

    /**
     * Returns the value that indicates whether trailingZeros has default value or not.
     *
     * @param trailingZeros
     * @return
     */
    public static boolean isTrailingZerosDefault(final boolean trailingZeros) {
        return trailingZeros == DEFAULT_TRAILING_ZEROS;
    }

    /**
     * Returns <code>true</code> if the value of <code>ignore</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isIgnoreDefault(final Boolean ignore) {
        return equalsEx(ignore, false);
    }

    /**
     * Returns <code>true</code> if the value of <code>length</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isLengthDefault(final Long length) {
        return equalsEx(length, DEFAULT_LENGTH);
    }

    /**
     * Returns <code>true</code> if the value of <code>precision</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isPrecisionDefault(final Long precision) {
        return equalsEx(precision, DEFAULT_PRECISION);
    }

    /**
     * Returns <code>true</code> if the value of <code>scale</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isScaleDefault(final Long scale) {
        return equalsEx(scale, DEFAULT_SCALE);
    }

    /**
     * Returns <code>true</code> if the value of <code>min</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isMinDefault(final Integer min) {
        return equalsEx(min, null);
    }

    /**
     * Returns <code>true</code> if the value of <code>max</code> property is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isMaxDefault(final Integer max) {
        return equalsEx(max, null);
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>exclusive</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isExclusiveDefault(final Boolean exclusive) {
        return exclusive == null || Boolean.FALSE.equals(exclusive);
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>exclusive2</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isExclusive2Default(final Boolean exclusive2) {
        return exclusive2 == null || Boolean.FALSE.equals(exclusive2);
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>orNull</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isOrNullDefault(final Boolean orNull) {
        return orNull == null || Boolean.FALSE.equals(orNull);
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>not</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isNotDefault(final Boolean not) {
        return not == null || Boolean.FALSE.equals(not);
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>datePrefix</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isDatePrefixDefault(final DateRangePrefixEnum datePrefix) {
        return datePrefix == null;
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>dateMnemonic</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isDateMnemonicDefault(final MnemonicEnum dateMnemonic) {
        return dateMnemonic == null;
    }

    /**
     * Returns <code>true</code> if the criterion value of <code>andBefore</code> is default, <code>false</code> otherwise.
     *
     * @return
     */
    public static boolean isAndBeforeDefault(final Boolean andBefore) {
        return andBefore == null; // three meaningful values: 'true', 'false' and null!
    }
}
