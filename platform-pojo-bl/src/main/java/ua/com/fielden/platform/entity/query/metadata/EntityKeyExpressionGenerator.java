package ua.com.fielden.platform.entity.query.metadata;

import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.cond;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.expr;
import static ua.com.fielden.platform.entity.query.metadata.EntityKeyExpressionGenerator.TypeInfo.ENTITY;
import static ua.com.fielden.platform.entity.query.metadata.EntityKeyExpressionGenerator.TypeInfo.NON_STRING;
import static ua.com.fielden.platform.entity.query.metadata.EntityKeyExpressionGenerator.TypeInfo.STRING;
import static ua.com.fielden.platform.reflection.AnnotationReflector.getPropertyAnnotation;
import static ua.com.fielden.platform.reflection.Finder.getKeyMembers;
import static ua.com.fielden.platform.reflection.Reflector.getKeyMemberSeparator;
import static ua.com.fielden.platform.utils.CollectionUtil.listOf;
import static ua.com.fielden.platform.utils.EntityUtils.isEntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.Optional;
import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.entity.query.exceptions.EqlException;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IConcatFunctionWith;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneExprOperationAndClose;
import ua.com.fielden.platform.entity.query.model.ExpressionModel;

public class EntityKeyExpressionGenerator {
    public static final String EMPTY_STRING = "";

    /** Private default constructor to prevent instantiation. */
    private EntityKeyExpressionGenerator() {
    }

    public static ExpressionModel getVirtualKeyPropForEntityWithCompositeKey(final Class<? extends AbstractEntity<DynamicEntityKey>> entityType) {
        final List<KeyMemberInfo> keyMembersInfo = new ArrayList<>();
        boolean hasNotOptional = false;
        for (Field keyMemberField : getKeyMembers(entityType)) {
            final KeyMemberInfo keyMemberInfo = getKeyMemberInfo(entityType, keyMemberField);
            hasNotOptional = hasNotOptional || !keyMemberInfo.optional;
            keyMembersInfo.add(keyMemberInfo);
        }

        return getVirtualKeyPropForEntityWithCompositeKey(getKeyMemberSeparator(entityType), keyMembersInfo);
    }

    protected static ExpressionModel getVirtualKeyPropForEntityWithCompositeKey(final String keyMemberSeparator, List<KeyMemberInfo> keyMembers) {
        boolean hasNotOptional = false;
        for (KeyMemberInfo keyMemberInfo : keyMembers) {
            hasNotOptional = hasNotOptional || !keyMemberInfo.optional;
        }

        if (hasNotOptional) {
            return getVirtualKeyPropForEntityWithCompositeKeyWithNotOptionalMember(keyMemberSeparator, keyMembers);
        } else if (keyMembers.size() > 1) {
            return getVirtualKeyPropForEntityWithCompositeKeyWithOnlyOptionalMembers(keyMemberSeparator, keyMembers);
        } else {
            throw new EqlException("Entity with single-optional composite key member is not allowed.");
        }
    }

    private static ExpressionModel getVirtualKeyPropForEntityWithCompositeKeyWithOnlyOptionalMembers(final String keyMemberSeparator, List<KeyMemberInfo> keyMembers) {
        ExpressionModel currExp = null;

        for (final KeyMemberInfo originalField : keyMembers) {
            currExp = composeTwo(currExp, originalField, keyMemberSeparator);
        }

        return currExp;
    }

    private static ExpressionModel composeTwo(final ExpressionModel firstExpr, final KeyMemberInfo second, final String separator) {
        final ExpressionModel secondExpr = getKeyMemberConcatenationPropName(second.name, second.typeInfo);

        if (firstExpr == null) {
            return secondExpr;
        } else {
            return expr().caseWhen().condition(cond().expr(firstExpr).isNotNull().and().expr(secondExpr).isNotNull().model()).then().expr(concatenateExpressions(listOf(firstExpr, expr().val(separator).model(), secondExpr))). // 
                    when().condition(cond().expr(firstExpr).isNotNull().and().expr(secondExpr).isNull().model()).then().expr(firstExpr). //
                    when().expr(secondExpr).isNotNull().then().expr(secondExpr). //
                    otherwise().val(null).end().model();
        }
    }

    public static KeyMemberInfo getKeyMemberInfo(final Class<? extends AbstractEntity<DynamicEntityKey>> entityType, final Field keyMemberField) {
        final boolean optional = getPropertyAnnotation(Optional.class, entityType, keyMemberField.getName()) != null;
        final TypeInfo typeInfo = Integer.class.equals(keyMemberField.getType()) ? NON_STRING
                : (!PropertyDescriptor.class.equals(keyMemberField.getType()) && isEntityType(keyMemberField.getType()) ? ENTITY : STRING);

        return new KeyMemberInfo(keyMemberField.getName(), typeInfo, optional);
    }

    private static ExpressionModel concatenateExpressions(final List<ExpressionModel> expressions) {
        final Iterator<ExpressionModel> kmIter = expressions.iterator();
        final ExpressionModel firstMemberExpr = kmIter.next();

        IConcatFunctionWith<IStandAloneExprOperationAndClose, AbstractEntity<?>> concatStart = expr().concat().expr(firstMemberExpr);

        while (kmIter.hasNext()) {
            final ExpressionModel nextKeyMember = kmIter.next();
            concatStart = concatStart.with().expr(nextKeyMember);
        }

        return concatStart.end().model();
    }

    private static ExpressionModel getVirtualKeyPropForEntityWithCompositeKeyWithNotOptionalMember(final String keyMemberSeparator, List<KeyMemberInfo> keyMembers) {
        if (keyMembers.size() == 1) {
            return processSingleKeyMember(keyMembers.get(0).name, keyMembers.get(0).typeInfo);
        } else {
            return concatenateExpressions(getVirtualKeyPropForEntityWithCompositeKeyList(keyMemberSeparator, keyMembers));
        }
    }

    private static List<ExpressionModel> getVirtualKeyPropForEntityWithCompositeKeyList(final String keyMemberSeparator, List<KeyMemberInfo> keyMembers) {
        boolean foundFirstNonOptional = false;
        final List<ExpressionModel> result = new ArrayList<>();
        for (KeyMemberInfo keyMemberInfo : keyMembers) {
            if (keyMemberInfo.optional) {
                result.add(foundFirstNonOptional ? processOptionalKeyMemberAfter(keyMemberInfo.name, keyMemberInfo.typeInfo, keyMemberSeparator)
                        : processOptionalKeyMemberBefore(keyMemberInfo.name, keyMemberInfo.typeInfo, keyMemberSeparator));
            } else if (foundFirstNonOptional) {
                result.add(expr().val(keyMemberSeparator).model());
                result.add(getKeyMemberConcatenationPropName(keyMemberInfo.name, keyMemberInfo.typeInfo));
            } else {
                foundFirstNonOptional = true;
                result.add(getKeyMemberConcatenationPropName(keyMemberInfo.name, keyMemberInfo.typeInfo));
            }
        }

        if (!foundFirstNonOptional) {
            throw new EqlException("Composite key should consist of at least one not-optional member.");
        }

        return result;
    }

    private static ExpressionModel getKeyMemberConcatenationPropName(final String keyMemberName, final TypeInfo keyMemberType) {
        return expr().prop(keyMemberType == ENTITY ? keyMemberName + "." + KEY : keyMemberName).model();
    }

    private static ExpressionModel processSingleKeyMember(final String keyMemberName, final TypeInfo keyMemberType) {
        return keyMemberType == NON_STRING ? expr().concat().prop(keyMemberName).with().val(EMPTY_STRING).end().model()
                : getKeyMemberConcatenationPropName(keyMemberName, keyMemberType);
    }

    private static ExpressionModel processOptionalKeyMemberAfter(final String keyMemberName, final TypeInfo keyMemberType, final String separator) {
        return expr().caseWhen().prop(keyMemberName).isNotNull().then().concat().val(separator).with().expr(getKeyMemberConcatenationPropName(keyMemberName, keyMemberType)).end().otherwise().val(EMPTY_STRING).end()/*.endAsStr(256)*/.model();
    }

    private static ExpressionModel processOptionalKeyMemberBefore(final String keyMemberName, final TypeInfo keyMemberType, final String separator) {
        return expr().caseWhen().prop(keyMemberName).isNotNull().then().concat().expr(getKeyMemberConcatenationPropName(keyMemberName, keyMemberType)).with().val(separator).end().otherwise().val(EMPTY_STRING).end()/*.endAsStr(256)*/.model();
    }

    public static class KeyMemberInfo {
        public final String name;
        public final TypeInfo typeInfo;
        public final boolean optional;

        public KeyMemberInfo(final String name, final TypeInfo typeInfo, final boolean optional) {
            this.name = name;
            this.typeInfo = typeInfo;
            this.optional = optional;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + (optional ? 1231 : 1237);
            result = prime * result + ((typeInfo == null) ? 0 : typeInfo.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof KeyMemberInfo)) {
                return false;
            }

            KeyMemberInfo other = (KeyMemberInfo) obj;

            return Objects.equals(other.name, name) && Objects.equals(other.typeInfo, typeInfo) && optional == other.optional;
        }
    }

    public static enum TypeInfo {
        ENTITY,
        STRING,
        NON_STRING
    }
}